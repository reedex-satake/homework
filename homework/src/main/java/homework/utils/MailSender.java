package homework.utils;

import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;

import homework.entity.Answer;
import homework.entity.History;
import homework.entity.User;
import homework.utils.Constant.CorrectWrong;

/**
 * メール送信クラス。
 * @author satake
 */
public class MailSender implements Runnable {

	/** メール送信設定ファイル名 */
	private static final String CONFIG_NAME = "mail.properties";
	/** 送信用アカウントを設定ファイルから取得するキーの定義 */
	private static final String KEY_USERID = "account";
	/** 送信用パスワードを設定ファイルから取得するキーの定義 */
	private static final String KEY_PASSWD = "password";
	/** 送信先アドレスのセパレータの定義 */
	private static final String MAIL_SEPARATOR = "\\|@\\|";
	/** メール本文の改行文字の定義 */
	private static final String NEXT_LINE = "\r\n";
	/** メールの文字コードの定義 */
	private static final String CHAR_SET = "ISO-2022-JP";

	/** ユーザ情報 */
	private final User user;
	/** 履歴情報 */
	private final History history;
	/** 回答情報 */
	private final List<Answer> answers;

	/**
	 * コンストラクタ。
	 * @param user		ユーザ情報
	 * @param history	履歴情報
	 * @param answers	回答情報
	 */
	public MailSender(User user, History history, List<Answer> answers) {
		this.user = user;
		this.history = history;
		this.answers = answers;
	}

	@Override
	public void run() {
		send();
	}

	/**
	 * 実施結果をメール送信する。
	 */
	public void send() {
		String message = StringUtils.EMPTY;
		try {
			message = "設定情報読み込み";
			Properties property = new Properties();
			property.load(getClass().getClassLoader().getResourceAsStream(CONFIG_NAME));

			message = "セッションの作成";
			Session session = Session.getInstance(property, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(property.getProperty(KEY_USERID), property.getProperty(KEY_PASSWD));
				}
			});

			message = "メールの作成";
			MimeMessage mimeMessage = createMail(session);

			message = "宛先の設定";
			String[] noticeMails = user.getNoticeMail().split(MAIL_SEPARATOR);
			InternetAddress[] toAddress = new InternetAddress[noticeMails.length];
			for (int i = 0; i < noticeMails.length; i++) {
				toAddress[i] = new InternetAddress(noticeMails[i]);
			}
			mimeMessage.setRecipients(MimeMessage.RecipientType.TO, toAddress);

			InternetAddress fromAddress = new InternetAddress("noreply@system.com", "ホームワーク");
			mimeMessage.setFrom(fromAddress);

			message = "保存";
			mimeMessage.saveChanges();

			// 送信
			message = "送信";
			Transport.send(mimeMessage);
		}
		catch (Exception e) {
			throw new SystemException("メール送信の" + message + "処理で失敗しました。", e);
		}
	}

	/**
	 * メッセージを生成する。
	 * @param session	メールセッション
	 * @return	メッセージ
	 * @throws MessagingException	メッセージの設定に失敗した場合。
	 */
	private MimeMessage createMail(Session session) throws MessagingException {
		MimeMessage mimeMessage = new MimeMessage(session);

		// タイトル
		String title = "【ドリルの結果】（" + DateUtil.toString(DateUtil.getDate(), "yyyy/MM/dd") + "）" + user.getLastName() + user.getFirstName();
		mimeMessage.setSubject(title, CHAR_SET);

		// 本文
		StringBuilder text = new StringBuilder();
		text.append(user.getLastName()).append(user.getFirstName()).append("さんの実施結果").append(NEXT_LINE);
		text.append(NEXT_LINE);
		int elapsed = history.getElapsed() / 1000;
		text.append("所要時間： 約").append(elapsed / 60).append("分").append(elapsed % 60).append("秒").append(NEXT_LINE);
		text.append("正解率　： ").append(history.getCorrectPercent()).append("%").append(NEXT_LINE);
		text.append(NEXT_LINE);
		text.append("問題と解答は以下の通りです。").append(NEXT_LINE);
		text.append("-----------------------------------------------------").append(NEXT_LINE);
		int index = 1;
		for (Answer answer : answers) {
			text.append("問題").append(index).append(NEXT_LINE);
			text.append("　　").append(answer.getQuestion().getQuestion()).append(NEXT_LINE);
			text.append("回答　⇒　").append(answer.getCorrectWrong() == CorrectWrong.COLLECT ? "正解" : "不正解").append(NEXT_LINE);
			text.append("　　").append(StringUtils.isNotBlank(answer.getAnswer()) ? answer.getAnswer() : "未回答").append(NEXT_LINE);
			text.append("解答").append(NEXT_LINE);
			text.append("　　").append(answer.getQuestion().getCorrect()).append(NEXT_LINE);
			text.append("-----------------------------------------------------").append(NEXT_LINE);
			index++;
		}
		text.append(NEXT_LINE);
		text.append(NEXT_LINE);
		text.append("-----------------------------------------------------").append(NEXT_LINE);
		text.append("※ このメールはシステムより自動的に送信されています。").append(NEXT_LINE);
		text.append(NEXT_LINE);
		mimeMessage.setText(text.toString(), CHAR_SET);

		return mimeMessage;
	}
}
