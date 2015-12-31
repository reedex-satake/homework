package homework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.jboss.logging.Logger;

/**
 * 日付ユーティリティクラス。
 * <p>
 * 日付がテスト時に固定されないと検証ができない場合が出てくるかもしれないので、
 * システム日付はこのユーティリティ経由で取得する。
 * </p>
 * @author satake
 */
public final class DateUtil {

	/** 年月日（YYYYMMDD）のフォーマット定義 */
	public static final String FORMAT_YMD = "yyyyMMdd";
	/** 年月日時分秒（YYYYMMDDHHMMSS）のフォーマット定義 */
	public static final String FORMAT_YMDHMS = "yyyyMMddhhmmss";

	/** 環境変数から日付文字列を取得するためのキーの定義 */
	private static final String KEY_DATE = "DateUtil.sysDate";
	/** 環境変数から定義された日付が経過するかどうかの定義を取得するためのキーの定義 */
	private static final String KEY_ELAPSE = "DateUtil.isElapse";
	/** 環境変数から日付取得クラスの指定を取得するためのキーの定義 */
	private static final String KEY_CLASS = "DateUtil.className";

	/** 自インスタンス */
	protected static DateUtil dateUtil = null;
	/** ロガー */
	private static Logger logger = Logger.getLogger(DateUtil.class);

	/** 日付取得クラス */
	private SysDate sysDate = null;
	/** 開始時間 */
	private Long initTime = null;
	/** 差分時間 */
	private Long diffTime = null;
	/** 時間経過指定 */
	private Boolean isElapse = null;

	/**
	 * コンストラクタ。
	 * <p>
	 * 環境変数から日付の取得方法を定義する。
	 * </p>
	 */
	private DateUtil() {
		String envDate = System.getProperty(KEY_DATE);
		if (envDate != null && !envDate.isEmpty()) {
			// 環境変数に日付が指定されていたらインスタンス変数に設定する
			try {
				initTime = toDate(envDate, FORMAT_YMDHMS, FORMAT_YMD).getTime();
				diffTime = System.currentTimeMillis() - initTime;
				String envElapse = System.getProperty(KEY_ELAPSE);
				isElapse = (envElapse != null) ? Boolean.parseBoolean(envElapse) : false;
			}
			catch (SystemException e) {
				logger.warn("環境変数に設定されたシステム日付［" + envDate + "］はDate型に変換できませんでした。", e);
			}
		}
		String envClass = System.getProperty(KEY_CLASS);
		if (envClass != null && !envClass.isEmpty()) {
			// 環境変数に日付取得クラスが指定されていたらインスタンス変数に設定する
			try {
				sysDate = (SysDate) Class.forName(envClass).newInstance();
			}
			catch (Exception e) {
				logger.warn("環境変数に設定されたシステム日付クラス［" + envClass + "］はインスタンス化出来ませんでした。", e);
			}
		}
		if (sysDate == null) {
			// 日付取得クラスが未設定の場合はデフォルトクラスを設定する
			sysDate = new DafaultDate();
		}
	}

	/**
	 * 現在時間を取得する。
	 * @return	現在時間
	 */
	private long getTime() {
		if (isElapse == null) {
			// 経過時間指定が未設定の場合は日付を指定されていないため、日付取得クラスから取得する
			return sysDate.getTime();
		}
		else if (isElapse) {
			// 経過時間設定が true で指定されているため、指定された時間からの時間を返す
			return System.currentTimeMillis() + diffTime;
		}
		else {
			// 指定された日付の時間を返す
			return initTime;
		}
	}

	/**
	 * 日付文字列を日付オブジェクトに変換する。
	 * @param target	対象の日付文字列
	 * @param formats	フォーマットの列挙
	 * @return	日付オブジェクト
	 */
	public static Date toDate(String target, String... formats) {
		try {
			return DateUtils.parseDate(target, formats);
		}
		catch (ParseException e) {
			throw new SystemException("文字列（" + target + "）を日付に変換できませんでした。", e);
		}
	}

	/**
	 * 日付オブジェクトを文字列に変換する。
	 * @param target	対象の日付オブジェクト
	 * @param format	フォーマット
	 * @return	時間文字列
	 */
	public static String toString(Date target, String format) {
		return new SimpleDateFormat(format).format(target);
	}

	/**
	 * 現在を表すDateオブジェクトを取得する。
	 * @return	現在日付
	 */
	public static Date getDate() {
		return new Date(getInstance().getTime());
	}

	/**
	 * 現在を表すCalendarオブジェクトを取得する。
	 * @return	カレンダー
	 */
	public static Calendar getCalendar() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(getInstance().getTime());
		return calendar;
	}

	/**
	 * 日付をカレンダーに変換する。
	 * @param date	対象日付
	 * @return		カレンダー
	 */
	public static Calendar toCalendar(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(date.getTime());
		return calendar;
	}

	/**
	 * 日付を学年年齢に変換する。
	 * @param date	対象日付
	 * @return	学年年齢
	 */
	public static short toSchoolAge(Date date) {
		Calendar birthDay = toCalendar(date);
		Calendar system = getCalendar();
		short years = (short) (system.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR));
		if (system.get(Calendar.MONTH) + 1 < 4) {
			years--;
		}
		if (birthDay.get(Calendar.MONTH) + 1 < 4) {
			years++;
		}
		return years;
	}

	/**
	 * 自インスタンスを取得する。
	 * @return	自インスタンス
	 */
	private static synchronized DateUtil getInstance() {
		if (dateUtil == null) {
			dateUtil = new DateUtil();
		}
		return dateUtil;
	}


	/**
	 * 日付取得インターフェース。
	 * @author satake
	 */
	public interface SysDate {
		long getTime();
	}

	/**
	 * 日付取得インターフェースのデフォルト実装クラス。
	 * @author satake
	 */
	public class DafaultDate implements SysDate {
		@Override
		public long getTime() {
			return System.currentTimeMillis();
		}
	}
}
