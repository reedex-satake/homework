package homework.dto;

import java.util.List;

import homework.entity.Answer;
import homework.entity.History;
import homework.entity.Question;
import homework.entity.User;

/**
 * レスポンス情報クラス。
 * <p>
 * アプリケーション全体で応答の種類が少ないのですべてを満たす情報を一つにまとめてある。
 * </p>
 * @author satake
 */
public class Responce {

	/** ユーザ情報 */
	private User user;
	/** 問題情報一覧 */
	private List<Question> questions;
	/** 回答情報一覧 */
	private List<Answer> answers;
	/** 履歴情報 */
	private History history;

	/**
	 * ユーザ情報を取得する。
	 * @return	ユーザ情報
	 */
	public User getUser() {
		return user;
	}

	/**
	 * ユーザ情報を設定する。
	 * @param user	ユーザ情報
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * 問題情報一覧を取得する。
	 * @return	問題情報一覧
	 */
	public List<Question> getQuestions() {
		return questions;
	}

	/**
	 * 問題情報一覧を設定する。
	 * @param questions	問題情報一覧
	 */
	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	/**
	 * 回答情報一覧を取得する。
	 * @return	回答情報一覧
	 */
	public List<Answer> getAnswers() {
		return answers;
	}

	/**
	 * 回答情報一覧を設定する。
	 * @param answers	回答情報一覧
	 */
	public void setAnswers(List<Answer> answers) {
		this.answers = answers;
	}

	/**
	 * 履歴情報を取得する。
	 * @return	履歴情報
	 */
	public History getHistory() {
		return history;
	}

	/**
	 * 履歴情報を設定する。
	 * @param history	履歴情報
	 */
	public void setHistory(History history) {
		this.history = history;
	}
}
