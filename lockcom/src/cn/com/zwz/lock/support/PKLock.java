package cn.com.zwz.lock.support;

import org.apache.log4j.Logger;

import cn.com.zwz.lock.model.LockableVO;
import cn.com.zwz.lock.service.ILockBS;

/*
 * 
 * ��ILockBS�����˷�װ��ʹ����û������Ѻã������ṩ��һ������
 * 
 * �����������������������Գ���ֵ ILockBS.STR_SHARED_LOCK ��β.
 * 
 * ͬʱ���ṩ�˶�̬�����������ṩ��Ӷ�̬���ͷŶ�̬���ķ���
 */
public class PKLock {
	/** �첽����������ʱ����� */
	protected static long MIN_WAIT = 100; // Should be greater than 2 *

	public static final String PKLOCK_ATTR = "pklock";

	/** ��¼��־ */
	protected Logger logger = Logger.getLogger(PKLock.class);

	/** IBasicBS ʵ����������� */
	protected ILockBS pkLock;

	/** ���� */
	private static PKLock instance = null;

	static {
		instance = new PKLock();
	}

	/** ��̬�������� */
	public static PKLock getInstance() {
		return instance;
	}

	public ILockBS getPkLock() {
		return pkLock;
	}

	public void setPkLock(ILockBS pkLock) {
		this.pkLock = pkLock;
	}

	/**
	 * ������ֱ����ȡ��
	 * 
	 * @deprecated ʹ�ô˷������������������
	 * @return void
	 * @param lockable
	 *            �������� ID��128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ���ߣ�ͨ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
	 */
	public void acquireAsynLock(String lockable, String userID, String dsName) {
		manageNullBService();

		logger.info("Deprecated method acquireAsynLock called, with params "
				+ lockable + " " + userID + " " + dsName + ".");

		// Try each second...
		while (!pkLock.acquireLock_RequiresNew(lockable, dsName)) {
			try {
				Thread.sleep(MIN_WAIT);
			} catch (InterruptedException e) {
				logger.error("Thread Interrupted exception error "
						+ "acquiring asyn lock on" + lockable);
			}
		}
	}

	/**
	 * ����һ��ʱ�䣬ֱ���������ʱ
	 * 
	 * @return �ɹ����� true
	 * @param lockable
	 *            ������ ID��128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ���ߣ�ͨ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
	 * @param millis
	 *            �ȴ�ʱ�䣬Ӧ�ô��� 1 �루10 * MIN_WAIT��
	 */
	public boolean acquireAsynLock(String lockable, String userID,
			String dsName, long millis) {
		manageNullBService();
		logger.info("Method acquireAsynLock called, with params " + lockable
				+ " " + userID + " " + dsName + ".");
		if (millis < MIN_WAIT * 10)
			logger.info("LockManager.acquireAsynLock should wait longer.");

		// Try each second within millis time...
		boolean rv = pkLock.acquireLock_RequiresNew(lockable, dsName);
		while (!rv) {
			try {
				millis -= MIN_WAIT;
				if (millis <= 0)
					break;
				Thread.sleep(MIN_WAIT);
			} catch (InterruptedException e) {
				logger.error("Thread Interrupted exception error "
						+ "acquiring asyn lock on" + lockable);
			}
			rv = pkLock.acquireLock_RequiresNew(lockable, dsName);
		}

		return rv ? rv : pkLock.acquireLock_RequiresNew(lockable, dsName); // try
		// last
		// time.
	}

	/**
	 * ��ȡ������
	 * 
	 * @return �ɹ����� true
	 * @param lockable
	 *            ������ ID��128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
	 */
	public boolean acquireLock(String lockable, String userID, String dsName) {
		manageNullBService();

		return pkLock.acquireLock_RequiresNew(lockable, dsName);
	}

	/**
	 * �������Ƿ���ϡ�
	 * 
	 * @return �ɹ����� true
	 * @param lockable
	 *            ������ ID��128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
	 */
	public boolean isLocked(String lockable, String userID, String dsName) {
		manageNullBService();

		return pkLock.isLocked_RequiresNew(lockable, userID, dsName);
	}

	/**
	 * ������ȡ��
	 * 
	 * @return �ɹ����� true
	 * @param lockables
	 *            ������ ID ���飬��Ԫ��Ϊ 128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
	 */
	public boolean acquireBatchLock(String[] lockables, String userID,
			String dsName) {
		manageNullBService();

		return pkLock.acquireBatchLock_RequiresNew(lockables, dsName);
	}

	/**
	 * �ͷŵ�����
	 * 
	 * @return void
	 * @param lockable
	 *            ������ ID��128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
	 */
	public void releaseLock(String lockable, String userID, String dsName) {
		manageNullBService();
		
		pkLock.releaseLock_RequiresNew(lockable, userID, dsName);
	}

	/**
	 * �����ͷ��û���һЩ��
	 * 
	 * @return void
	 * @param lockables
	 *            ������ ID ���飬��Ԫ��Ϊ 128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
	 */
	public void releaseBatchLock(String[] lockables, String userID,
			String dsName) {
		manageNullBService();

		pkLock.releaseBatchLock_RequiresNew(lockables, userID, dsName);
	}

	/**
	 * ��һ���ض��û��ͷ���
	 * 
	 * @return void
	 * @param owner
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param label
	 *            ��ӵ�����û����ڹ�˾ ID��4 �ֽ� String
	 */
	public void releaseLocks(String userID, String dsName) {
		manageNullBService();
		pkLock.releaseUserLocks_RequiresNew(userID, dsName);
	}

	/**
	 * releaseһ���û�����
	 * 
	 * @param userID
	 *            �û�ID
	 * @param dsName
	 *            ���ݿ�����
	 */
	public void releaseLocks(String[] userID, String dsName) {
		manageNullBService();
		if (null != userID) {
			for (int i = 0; i < userID.length; i++) {
				String id = userID[i];
				releaseLocks(id, dsName);
			}
		}
	}

	/**
	 * ���û�����������Ϣ
	 * 
	 * @param userID
	 *            �û�ID
	 * @param dsName
	 *            ���ݿ�����
	 */
	public LockableVO[] getUserLockVOs(String userID, String dsName) {
		manageNullBService();
		LockableVO[] lockableVOs = pkLock.getUserLockVOs_RequiresNew(userID,
				dsName);
		return lockableVOs;
	}

	/**
	 * ���е�����Ϣ
	 * 
	 * @param dsName
	 *            ���ݿ�����
	 */
	public LockableVO[] getAllLockVOs(String dsName) {
		manageNullBService();
		LockableVO[] lockableVOs = pkLock.getAllLockVOs_RequiresNew(dsName);
		return lockableVOs;
	}

	/**
	 * 
	 */
	private void manageNullBService() {
		if (null == pkLock) {
			logger.error("No ILockManager instance.");
			throw new NullPointerException("Null pointer on ILockManager.");
		}
	}

	public LockableVO getLock(String dsName, String lock) {
		LockableVO[] vos = getAllLockVOs(dsName);
		for (int i = 0; i < vos.length; i++) {
			if (vos[i].getLockable().equals(lock))
				return vos[i];
		}
		return null;
	}

	private static class LockDsPair {

		public String ds;

		public String lock;

		public LockDsPair(String userDataSource, String lock2) {
			ds = userDataSource;
			lock = lock2;
		}

		public boolean equals(Object o) {
			if (o instanceof LockDsPair) {
				LockDsPair other = (LockDsPair) o;
				return ds.equals(other.ds) && lock.equals(other.lock);
			}
			return false;
		}

		public int hashCode() {
			return ds.hashCode() * 37 + lock.hashCode();
		}
	}
}
