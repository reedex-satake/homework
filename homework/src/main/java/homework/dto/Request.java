package homework.dto;

import java.util.List;

import homework.entity.Answer;
import homework.entity.User;

/**
 * リクエスト情報クラス。
 * <p>
 * アプリケーション全体で要求の種類が少ないのですべてを満たす情報を一つにまとめてある。
 * </p>
 * @author satake
 */
public class Request {

	/** ユーザ情報 */
	private User user;
	/** 問題数 */
	private int questionVolume;
	/** 回答情報一覧 */
	private List<Answer> answers;
	/** 経過時間 */
	private int elapsed;

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
	 * 問題数を取得する。
	 * @return	問題数
	 */
	public int getQuestionVolume() {
		return questionVolume;
	}

	/**
	 * 問題数を設定する。
	 * @param questionVolume	問題数
	 */
	public void setQuestionVolume(int questionVolume) {
		this.questionVolume = questionVolume;
	}

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
	 * 経過時間を取得する。
	 * @return	経過時間
	 */
	public int getElapsed() {
		return elapsed;
	}

	/**
	 * 経過時間を設定する。
	 * @param elapsed	経過時間
	 */
	public void setElapsed(int elapsed) {
		this.elapsed = elapsed;
	}

}
