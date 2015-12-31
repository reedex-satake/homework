package homework.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;

import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.Before;

import homework.tools.ExcelDataImporter;
import homework.utils.EMProducer;
import homework.utils.SystemException;

public class BaseTest {

	static {
		// テスト用データベースを指定
		System.setProperty("jpaUnitName", "homework_ut");
		// log4jdbcの設定
		System.setProperty("log4jdbc.spylogdelegator.name", "net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator");
		// log4jdbcのログはINFOレベルでいろいろ出力していて情報が多いので、
		// いったん全体を「ERROR」のみにして、実行SQLとダンプを出力するようにロガーを設定
		System.setProperty("org.slf4j.simpleLogger.log.jdbc", "ERROR");
		System.setProperty("org.slf4j.simpleLogger.log.jdbc.sqltiming", "INFO");
		System.setProperty("org.slf4j.simpleLogger.log.jdbc.resultsettable", "INFO");
	}
	/** ロガー */
	protected Logger logger = Logger.getLogger(getClass());
	/** エンティティマネジャー */
	protected EntityManager manager = EMProducer.createManager();
	/** エクセルデータ登録ユーティリティ */
	private ExcelDataImporter importer = new ExcelDataImporter();

	@Before
	public void before() {
		// トランザクションを開始する
		manager.getTransaction().begin();
	}

	@After
	public void after() {
		// トランザクションをロールバックする
		manager.getTransaction().rollback();
	}

	/**
	 * サービスクラスのインスタンスを生成する。
	 * @param clazz	対象クラス
	 * @return		対象クラスのインスタンス
	 */
	protected <T> T createService(Class<T> clazz) {
		try {
			Constructor<T> constructor = clazz.getConstructor(EntityManager.class);
			T instance = constructor.newInstance(manager);
			return instance;
		}
		catch (InvocationTargetException e) {
			logger.error(e.getMessage(), e.getTargetException());
			throw new SystemException(e);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new SystemException(e);
		}
	}

	/**
	 * テストデータを登録する。
	 * @param xlsFile	テストデータが記載されたエクセルファイル
	 */
	protected void loadTestData(String xlsFile) {
		importer.importRecords(manager, xlsFile);
	}
}
