package homework.utils;

/**
 * システム例外クラス。
 * @author satake
 */
public class SystemException extends RuntimeException {

	/**
	 * コンストラクタ。
	 * @param message	例外メッセージ
	 */
	public SystemException(String message) {
		super(message);
	}

	/**
	 * コンストラクタ。
	 * @param t			例外オブジェクト
	 */
	public SystemException(Throwable t) {
		super(t);
	}

	/**
	 * コンストラクタ。
	 * @param message	例外メッセージ
	 * @param t			例外オブジェクト
	 */
	public SystemException(String message, Throwable t) {
		super(message, t);
	}

}
