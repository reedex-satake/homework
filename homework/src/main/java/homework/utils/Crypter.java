package homework.utils;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * 暗号ユーティリティ。
 * <p>
 * 暗号化・復号化の static メソッドを提供するユーティリティクラス。
 * </p>
 * @author satake
 */
public final class Crypter {

	/** 秘密鍵の定義 */
	private static final String SECRET_KEY = "homework";
	/** 暗号化・復号化のアルゴリズムの定義 */
	private static final String ALGORITHM = "Blowfish";

	/**
	 * 非インスタンス化のためのprivateコンストラクター。
	 */
	private Crypter() {
	}

	/**
	 * 文字列を暗号化する。
	 * @param target	暗号化する文字列
	 * @return	暗号化した文字列
	 */
	public static String encrypt(String target) {
		try {
			Cipher cipher = createCipher(Cipher.ENCRYPT_MODE);
			byte[] encrypted = cipher.doFinal(target.getBytes());
			return (new Base64()).encodeToString(encrypted);
		}
		catch (Exception e) {
			throw new SystemException("暗号化に失敗しました。", e);
		}
	}

	/**
	 * 文字列を復号化する。
	 * @param target	復号化する文字列
	 * @return	復号化した文字列
	 */
	public static String decrypt(String target) {
		try {
			byte[] encrypted = (new Base64()).decode(target);
			Cipher cipher = createCipher(Cipher.DECRYPT_MODE);
			byte[] decrypted = cipher.doFinal(encrypted);
			return new String(decrypted, "UTF-8");
		}
		catch (Exception e) {
			throw new SystemException("復号化に失敗しました。", e);
		}
	}

	/**
	 * 暗号機能オブジェクトを生成する。
	 * @param	mode	暗号化する場合は {@link Cipher#ENCRYPT_MODE} を、
	 * 					復号化する場合は {@link Cipher#DECRYPT_MODE} を指定する。
	 * @return	暗号機能オブジェクトのインスタンス。
	 * @throws NoSuchAlgorithmException	アルゴリズムが使用できない場合。
	 * @throws NoSuchPaddingException	パディングが使用できない場合。
	 * @throws InvalidKeyException		キーの指定が無効な場合。
	 */
	private static Cipher createCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(mode, secretKeySpec);
		return cipher;
	}
}
