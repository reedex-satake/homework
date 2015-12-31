package homework.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;

import homework.dto.Request;
import homework.dto.Responce;
import homework.entity.Answer;
import homework.entity.History;
import homework.entity.Question;
import homework.entity.User;
import homework.extend.ServiceLogging;
import homework.utils.Constant.CorrectWrong;
import homework.utils.Crypter;
import homework.utils.DateUtil;
import homework.utils.MailSender;

/**
 * ユーザサービスクラス
 * <p>
 * ユーザに紐づくサービスを提供する。
 * </p>
 * @author satake
 */
@Path("/api/user")
public class UserService extends BaseService {

	/**
	 * コンストラクタ。
	 */
	public UserService() {
		super();
	}

	/**
	 * エンティティマネジャーを引き継ぐコンストラクタ。
	 * @param entityManager	エンティティマネジャー
	 */
	public UserService(EntityManager entityManager) {
		super(entityManager);
	}

	/**
	 * ログインサービス
	 * <p>
	 * ログイン処理を行う。<br />
	 * ユーザIDとパスワードで認証して、前回誤答した問題を取得する。
	 * </p>
	 * @param auth	アカウントIDとパスワードを設定したユーザオブジェクト。
	 * @return		ユーザ情報と前回誤答した問題の一覧を返する。
	 */
	@Path("/login")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ServiceLogging
	public Responce login(User auth) {
		Responce responce = new Responce();
		User user = authenticate(auth.getAccountId(), auth.getPassword());
		responce.setUser(user);
		if (user != null) {
			logger.info("認証に成功しました。");
			responce.setAnswers(getLatestWrongAnswers(user));
		}
		return responce;
	}

	/**
	 * 問題を取得する。
	 * <p>
	 * 指定された数の問題を取得する。<br />
	 * 前回対象とした問題を除いて、ランダムに問題を取得する。<br />
	 * 正答は暗号化した状態で返却する。
	 * </p>
	 * @param request	ユーザ情報と問題数
	 * @return			問題の一覧
	 */
	@Path("/question")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ServiceLogging
	public Responce getQuestions(Request request) {
		// 問題を取得する
		List<Question> questions = getQuestion(request.getUser(), request.getQuestionVolume());
		// 正答は暗号化する
		questions.stream()
				.forEach(q -> q.setCorrect(Crypter.encrypt(q.getCorrect())));
		// 返却値の設定
		Responce responce= new Responce();
		responce.setUser(request.getUser());
		responce.setQuestions(questions);
		return responce;
	}

	/**
	 * 回答を登録する。
	 * <p>
	 * 回答に正誤を設定した後で、履歴情報に登録する。
	 * </p>
	 * @param request	ユーザ情報と回答
	 * @return			解答一覧
	 */
	@Path("/answer")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ServiceLogging
	@Transactional
	public Responce registAnswer(Request request) {
		// 正答を復号化する
		request.getAnswers().stream()
				.forEach(answer -> setCorrect(answer));

		// 履歴情報に登録する
		History history = new History();
		history.setUser(request.getUser());
		history.setRegistDate(DateUtil.getDate());
		history.setElapsed(request.getElapsed());
		long correctAnswer = request.getAnswers().stream()
				.filter(answer -> StringUtils.equals(answer.getCorrectWrong(), CorrectWrong.COLLECT)).count();
		BigDecimal correctCount = BigDecimal.valueOf(correctAnswer * 100);
		BigDecimal totalCount = BigDecimal.valueOf(request.getAnswers().size());
		history.setCorrectPercent(correctCount.divide(totalCount, 2, BigDecimal.ROUND_HALF_EVEN));
		getManager().persist(history);
		// 回答情報に登録する
		for (Answer answer : request.getAnswers()) {
			answer.setHistory(history);
			getManager().persist(answer);
		}

		// メール送信
		if (StringUtils.isNotBlank(request.getUser().getNoticeMail())) {
			MailSender mailSender = new MailSender(request.getUser(), history, request.getAnswers());
			Executors.newSingleThreadExecutor().execute(mailSender);
		}

		// 返却値の設定
		Responce responce = new Responce();
		responce.setUser(request.getUser());
		responce.setHistory(history);
		responce.setAnswers(request.getAnswers());
		return responce;
	}

	/**
	 * 認証処理。
	 * <p>
	 * ユーザIDとパスワードをもとにテーブルからユーザ情報を取得する。
	 * </p>
	 * @param accountId	アカウントID
	 * @param password	パスワード
	 * @return	認証に成功した場合はユーザ情報を、失敗した場合はnullを返す。
	 */
	private User authenticate(String accountId, String password) {
		try {
			return getManager()
					.createNamedQuery("User.findByAccountPasswd", User.class)
					.setParameter("accountId", accountId)
					.setParameter("password", Crypter.encrypt(password))
					.getSingleResult();
		}
		catch (NoResultException e) {
			logger.warn(e);
			return null;
		}
	}

	/**
	 * 誤答問題を取得する。
	 * <p>
	 * 前回誤答した問題を取得する。<br />
	 * 初回の場合や、前回全問正解の場合は空のリストを返す。
	 * </p>
	 * @param user	ユーザ情報
	 * @return		誤答した問題のリストを返す。誤答した問題がない場合は空のリストを返す。
	 */
	private List<Answer> getLatestWrongAnswers(User user) {
		List<Answer> answers = getLatestAnswers(user);
		return answers.stream()
				.filter(answer -> StringUtils.equals(answer.getCorrectWrong(), CorrectWrong.WRONG))
				.collect(Collectors.toList());
	}

	/**
	 * 前回回答を取得する。
	 * <p>
	 * 直前の回答履歴を取得する。
	 * </p>
	 * @param user	ユーザ情報
	 * @return		前回回答履歴。取得できない場合はnullを返す。
	 */
	private List<Answer> getLatestAnswers(User user) {
		return getManager()
				.createNamedQuery("Answer.findLatestHistory", Answer.class)
				.setParameter("userId", user.getUserId())
				.getResultList();
	}

	// TODO	正直、ここは本当はもっとちゃんと考えるべきメソッド
	// 		科目ごとに均等に取得するとか、過去に実施した問題の回数で少ないものを優先させるとか。
	//		全部のID取得した後で、シャッフルして問題取り直しているけど、本当に大丈夫なの？　とか。
	/**
	 * 問題を取得する。
	 * <p>
	 * <ul>
	 * <li>最後に実行した問題は対象外とし、「現在の学年～現在の学年-3年」の問題を出題範囲として、データを取得する。</li>
	 * <li>取得したデータをシャッフルし、IDを問題数の分だけに絞り込む。</li>
	 * <li>絞り込んだIDの問題を改めて取得する。</li>
	 * </p>
	 * @param user				ユーザ情報
	 * @param questionVolume	問題数
	 * @return	問題の一覧
	 */
	private List<Question> getQuestion(User user, int questionVolume) {
		List<Integer> latestQuestionId = getLatestQuestionId(user);
		short schoolAge = DateUtil.toSchoolAge(user.getBirthDate());
		// 問題IDを取得する
		List<Integer> questionIdList = getManager()
				.createNamedQuery("Question.selectQuestionId", Integer.class)
				.setParameter("schoolAgeFrom", (short) (schoolAge - 3))
				.setParameter("schoolAgeTo", schoolAge)
				.setParameter("latestQuestions", latestQuestionId)
				.getResultList();
		// ランダムにIDをシャッフルして指定された問題数分を取り出す
		Collections.shuffle(questionIdList, new Random(System.currentTimeMillis()));
		List<Integer> targetIdList = questionIdList.stream()
				.limit(questionVolume)
				.collect(Collectors.toList());
		// 問題を取得する
		List<Question> questions = getManager()
				.createNamedQuery("Question.selectQuestions", Question.class)
				.setParameter("questionId", targetIdList)
				.getResultList();
		return questions;
	}

	/**
	 * 最終履歴の問題IDを取得する。
	 * @param user	ユーザ情報
	 * @return	問題IDの一覧
	 */
	private List<Integer> getLatestQuestionId(User user) {
		List<Integer> latestQuestionId;
		List<Answer> answers = getLatestAnswers(user);
		if (!answers.isEmpty()) {
			latestQuestionId = new ArrayList<>();
			answers.stream()
					.forEach(answer -> latestQuestionId.add(answer.getQuestion().getQuestionId()));
		}
		else {
			latestQuestionId = Arrays.asList(new Integer[]{0});
		}
		return latestQuestionId;
	}

	/**
	 * 回答に正誤を設定する。
	 * @param answer	回答
	 */
	private void setCorrect(Answer answer) {
		Question question = answer.getQuestion();
		question.setCorrect(Crypter.decrypt(question.getCorrect()));
		String collectWrong = CorrectWrong.WRONG;
		if (StringUtils.isNotBlank(question.getSepChar())) {
			// 複数回答問題の場合
			List<String> corrects = Arrays.asList(question.getCorrect().split(question.getSepChar()));
			if (StringUtils.isNotBlank(answer.getAnswer())) {
				List<String> answers = Arrays.asList(answer.getAnswer().split(question.getSepChar()));
				if (corrects.size() == answers.size()) {
					collectWrong = CorrectWrong.COLLECT;
					for (String correct : corrects) {
						if (!answers.contains(correct)) {
							collectWrong = CorrectWrong.WRONG;
							break;
						}
					}
				}
			}
		}
		else {
			List<String> corrects = Arrays.asList(question.getCorrect().split("::"));
			if (corrects.contains(answer.getAnswer())) {
				collectWrong = CorrectWrong.COLLECT;
			}
		}
		answer.setCorrectWrong(collectWrong);
	}
}
