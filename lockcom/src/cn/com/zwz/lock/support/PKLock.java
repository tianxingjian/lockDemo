package cn.com.zwz.lock.support;

import org.apache.log4j.Logger;

import cn.com.zwz.lock.model.LockableVO;
import cn.com.zwz.lock.service.ILockBS;

/*
 * 
 * 对ILockBS进行了封装，使其对用户更加友好，此外提供了一个方法
 * 
 * 共享锁和排它锁。共享锁以常量值 ILockBS.STR_SHARED_LOCK 结尾.
 * 
 * 同时他提供了动态锁的能力，提供添加动态锁释放动态锁的方法
 */
public class PKLock {
	/** 异步阻塞锁试锁时间参数 */
	protected static long MIN_WAIT = 100; // Should be greater than 2 *

	public static final String PKLOCK_ATTR = "pklock";

	/** 记录日志 */
	protected Logger logger = Logger.getLogger(PKLock.class);

	/** IBasicBS 实例从容器获得 */
	protected ILockBS pkLock;

	/** 单例 */
	private static PKLock instance = null;

	static {
		instance = new PKLock();
	}

	/** 静态工厂方法 */
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
	 * 阻塞，直到获取锁
	 * 
	 * @deprecated 使用此方法可能造成永久阻塞
	 * @return void
	 * @param lockable
	 *            可锁对象 ID，128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者，通常是用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
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
	 * 阻塞一定时间，直到获得锁或超时
	 * 
	 * @return 成功返回 true
	 * @param lockable
	 *            锁对象 ID，128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者，通常是用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 * @param millis
	 *            等待时间，应该大于 1 秒（10 * MIN_WAIT）
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
	 * 获取单个锁
	 * 
	 * @return 成功返回 true
	 * @param lockable
	 *            锁对象 ID，128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public boolean acquireLock(String lockable, String userID, String dsName) {
		manageNullBService();

		return pkLock.acquireLock_RequiresNew(lockable, dsName);
	}

	/**
	 * 看该锁是否加上。
	 * 
	 * @return 成功返回 true
	 * @param lockable
	 *            锁对象 ID，128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public boolean isLocked(String lockable, String userID, String dsName) {
		manageNullBService();

		return pkLock.isLocked_RequiresNew(lockable, userID, dsName);
	}

	/**
	 * 批量获取锁
	 * 
	 * @return 成功返回 true
	 * @param lockables
	 *            锁对象 ID 数组，其元素为 128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public boolean acquireBatchLock(String[] lockables, String userID,
			String dsName) {
		manageNullBService();

		return pkLock.acquireBatchLock_RequiresNew(lockables, dsName);
	}

	/**
	 * 释放单个锁
	 * 
	 * @return void
	 * @param lockable
	 *            锁对象 ID，128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public void releaseLock(String lockable, String userID, String dsName) {
		manageNullBService();
		
		pkLock.releaseLock_RequiresNew(lockable, userID, dsName);
	}

	/**
	 * 批量释放用户的一些锁
	 * 
	 * @return void
	 * @param lockables
	 *            锁对象 ID 数组，其元素为 128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public void releaseBatchLock(String[] lockables, String userID,
			String dsName) {
		manageNullBService();

		pkLock.releaseBatchLock_RequiresNew(lockables, userID, dsName);
	}

	/**
	 * 按一个特定用户释放锁
	 * 
	 * @return void
	 * @param owner
	 *            锁拥有者用户 ID，20 字节 String
	 * @param label
	 *            锁拥有者用户所在公司 ID，4 字节 String
	 */
	public void releaseLocks(String userID, String dsName) {
		manageNullBService();
		pkLock.releaseUserLocks_RequiresNew(userID, dsName);
	}

	/**
	 * release一堆用户的锁
	 * 
	 * @param userID
	 *            用户ID
	 * @param dsName
	 *            数据库名称
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
	 * 该用户的所有锁信息
	 * 
	 * @param userID
	 *            用户ID
	 * @param dsName
	 *            数据库名称
	 */
	public LockableVO[] getUserLockVOs(String userID, String dsName) {
		manageNullBService();
		LockableVO[] lockableVOs = pkLock.getUserLockVOs_RequiresNew(userID,
				dsName);
		return lockableVOs;
	}

	/**
	 * 所有的锁信息
	 * 
	 * @param dsName
	 *            数据库名称
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
