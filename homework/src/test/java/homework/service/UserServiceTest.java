package homework.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import homework.dto.Request;
import homework.dto.Responce;
import homework.entity.Answer;
import homework.entity.Question;
import homework.entity.User;
import homework.utils.Crypter;
import homework.utils.DateUtil;

/**
 * {@link UserService} の単体テストクラス。
 * <p>
 * テストが実行できることの確認がメインだったので、詳細なテストは行っていない。
 * </p>
 * @author satake
 */
public class UserServiceTest extends BaseTest {

	/**
	 * 履歴を持っていないユーザのログイン。
	 * <p>
	 * ユーザ情報と空の回答一覧が返却される。
	 * </p>
	 */
	@Test
	public void testLogin1() {
		try {
			loadTestData("TestUserServiceData.xlsx");
			User req = new User();
			req.setAccountId("account1");
			req.setPassword("passwd1");
			UserService service = createService(UserService.class);
			Responce res = service.login(req);
			assertThat("account1", is(res.getUser().getAccountId()));
			assertThat(Crypter.encrypt("passwd1"), is(res.getUser().getPassword()));
			assertThat("試験", is(res.getUser().getLastName()));
			assertThat("一郎", is(res.getUser().getFirstName()));
			assertThat("テスト", is(res.getUser().getLastNameKana()));
			assertThat("イチロウ", is(res.getUser().getFirstNameKana()));
			assertThat("test1@test.com|@|test2@test.com", is(res.getUser().getNoticeMail()));
			assertThat(0, is(res.getAnswers().size()));
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
			fail();
		}
	}

	/**
	 * 直前の履歴では全問正解のユーザのログイン。
	 * <p>
	 * ユーザ情報と空の回答一覧が返却される。
	 * </p>
	 */
	@Test
	public void testLogin2() {
		try {
			loadTestData("TestUserServiceData.xlsx");
			User req = new User();
			req.setAccountId("account2");
			req.setPassword("passwd2");
			UserService service = createService(UserService.class);
			Responce res = service.login(req);
			assertThat(100002, is(res.getUser().getUserId()));
			assertThat(0, is(res.getAnswers().size()));
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
			fail();
		}
	}

	/**
	 * 直前の履歴で誤答のあるユーザのログイン。
	 * <p>
	 * ユーザ情報と誤答した回答一覧が返却される。
	 * </p>
	 */
	@Test
	public void testLogin3() {
		try {
			loadTestData("TestUserServiceData.xlsx");
			User req = new User();
			req.setAccountId("account3");
			req.setPassword("passwd3");
			UserService service = createService(UserService.class);
			Responce res = service.login(req);
			assertThat(100003, is(res.getUser().getUserId()));
			assertThat(3, is(res.getAnswers().size()));
			assertThat("質問14", is(res.getAnswers().get(0).getQuestion().getQuestion()));
			assertThat("解答14", is(res.getAnswers().get(0).getQuestion().getCorrect()));
			assertThat("誤答14", is(res.getAnswers().get(0).getAnswer()));
			assertThat("質問15", is(res.getAnswers().get(1).getQuestion().getQuestion()));
			assertThat("解答15", is(res.getAnswers().get(1).getQuestion().getCorrect()));
			assertThat("誤答15", is(res.getAnswers().get(1).getAnswer()));
			assertThat("質問16", is(res.getAnswers().get(2).getQuestion().getQuestion()));
			assertThat("解答16", is(res.getAnswers().get(2).getQuestion().getCorrect()));
			assertThat("誤答16", is(res.getAnswers().get(2).getAnswer()));
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
			fail();
		}
	}

	/**
	 * 存在しないアカウントIDのログイン。
	 * <p>
	 * ユーザ情報がnullで返却される。
	 * </p>
	 */
	@Test
	public void testLogin4() {
		try {
			loadTestData("TestUserServiceData.xlsx");
			User req = new User();
			req.setAccountId("account_not_exist");
			req.setPassword("passwd");
			UserService service = createService(UserService.class);
			Responce res = service.login(req);
			assertNull(res.getUser());
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
			fail();
		}
	}

	/**
	 * アカウントは存在するがパスワードの異なるログイン。
	 * <p>
	 * ユーザ情報がnullで返却される。
	 * </p>
	 */
	@Test
	public void testLogin5() {
		try {
			loadTestData("TestUserServiceData.xlsx");
			User req = new User();
			req.setAccountId("account1");
			req.setPassword("wrong_passwd");
			UserService service = createService(UserService.class);
			Responce res = service.login(req);
			assertNull(res.getUser());
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
			fail();
		}
	}

	/**
	 *
	 */
	@Test
	public void testGetQuestions() {
		try {
			loadTestData("TestUserServiceData.xlsx");
			Request req = new Request();
			req.setUser(new User());
			req.getUser().setUserId(100001);
			req.getUser().setBirthDate(DateUtil.toDate("2002/01/01", "yyyy/MM/dd"));
			req.setQuestionVolume(5);
			UserService service = createService(UserService.class);
			Responce res = service.getQuestions(req);
			assertThat(5, is(res.getQuestions().size()));
			Responce res2 = service.getQuestions(req);
			assertThat(5, is(res2.getQuestions().size()));
			assertNotEquals(res.getQuestions(), res2.getQuestions());
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
			fail();
		}
	}

	@Test
	public void testRegistAnswers() {
		try {
			loadTestData("TestUserServiceData.xlsx");
			Request req = new Request();
			req.setUser(new User());
			req.getUser().setUserId(100001);
			req.getUser().setBirthDate(DateUtil.toDate("2002/01/01", "yyyy/MM/dd"));
			req.setAnswers(new ArrayList<>());
			req.getAnswers().add(new Answer());
			req.getAnswers().get(0).setQuestion(new Question());
			req.getAnswers().get(0).getQuestion().setQuestionId(100001);
			req.getAnswers().get(0).getQuestion().setCorrect(Crypter.encrypt("解答1"));
			req.getAnswers().get(0).setAnswer("解答1");
			req.getAnswers().add(new Answer());
			req.getAnswers().get(1).setQuestion(new Question());
			req.getAnswers().get(1).getQuestion().setQuestionId(100002);
			req.getAnswers().get(1).getQuestion().setCorrect(Crypter.encrypt("解答2"));
			req.getAnswers().get(1).setAnswer("誤答2");
			req.getAnswers().add(new Answer());
			req.getAnswers().get(2).setQuestion(new Question());
			req.getAnswers().get(2).getQuestion().setQuestionId(100003);
			req.getAnswers().get(2).getQuestion().setCorrect(Crypter.encrypt("解答3"));
			req.getAnswers().get(2).setAnswer("解答3");
			UserService service = createService(UserService.class);
			Responce res = service.registAnswer(req);
			assertThat(3, is(res.getAnswers().size()));
			assertThat("1", is(res.getAnswers().get(0).getCorrectWrong()));
			assertThat("0", is(res.getAnswers().get(1).getCorrectWrong()));
			assertThat("1", is(res.getAnswers().get(2).getCorrectWrong()));
			assertThat(66.67, is(res.getHistory().getCorrectPercent().doubleValue()));
		}
		catch (Exception e) {
			logger.error("想定外の例外が発生しました。", e);
			fail();
		}
	}
}
