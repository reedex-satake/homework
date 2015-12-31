package homework.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import homework.utils.EMProducer;

/**
 * サービス基底クラス。
 * <p>
 * 通常はコンテナからアノテーションの指定でトランザクションマネジャーを取得するが、
 * 単体テスト用に特定のトランザクションマネジャーを設定するコンストラクタを用意している。
 * </p>
 * @author satake
 */
@Stateless
public class BaseService {

	/** ロガー */
	protected Logger logger = Logger.getLogger(getClass());

	/** エンティティマネジャー */
	@PersistenceContext(unitName = EMProducer.DEFAULT_UNIT_NAME)
	private EntityManager entityManager;

	/**
	 * デフォルトコンストラクタ。
	 */
	public BaseService() {
		super();
	}

	/**
	 * 単体テスト用コンストラクタ。
	 * <p>
	 * パラメータで指定された {@linkplain EntityManager} をインスタンスに設定する。
	 * </p>
	 * @param entityManager	エンティティマネジャー
	 */
	public BaseService(EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

	/**
	 * エンティティマネジャーを取得する。
	 * @return	エンティティマネジャーを返す。
	 */
	protected EntityManager getManager() {
		return entityManager;
	}

	/**
	 * トランザクションが有効か確認する。
	 * @return	トランザクションが有効な場合はtrueを、そうでない場合はfalseを返す。
	 */
	protected boolean isActive() {
		return getManager().getTransaction().isActive();
	}

	/**
	 * トランザクションを開始する。
	 */
	protected void begin() {
		if (!isActive()) {
			logger.debug("トランザクションを開始します。");
			getManager().getTransaction().begin();
		}
	}

	/**
	 * トランザクションをコミットする。
	 */
	protected void commit() {
		if (isActive()) {
			logger.debug("トランザクションをコミットします。");
			getManager().getTransaction().commit();
		}
	}

	/**
	 * トランザクションをロールバックする。
	 */
	protected void rollback() {
		if (isActive()) {
			logger.debug("トランザクションをロールバックします。");
			getManager().getTransaction().rollback();
		}
	}
}
