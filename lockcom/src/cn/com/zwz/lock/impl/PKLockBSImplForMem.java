package cn.com.zwz.lock.impl;

import java.util.List;
import java.util.Map;

import cn.com.zwz.lock.model.LockableVO;
import cn.com.zwz.lock.service.ILockManager;

/**
 * Memory PKLock实现
 * 
 * @author dengjt 2005-12-28
 */
public class PKLockBSImplForMem extends AbstractPKLockBSImpl {

	// 通过注入来解决依赖
	private ILockManager lockManager;

	public boolean isLocked_RequiresNew(String lockable, String userID,
			String dsName) {
		checkLockableNotNull(lockable);
		userID = getCurrUser();
		dsName = judgeDsName(dsName);
		return getLockManager().isLocked_RequiresNew(lockable, userID, dsName);
	}

	public boolean acquireLock_RequiresNew(String lockable, String dsName) {
		checkLockableNotNull(lockable);
		String userID = getCurrUser();
		dsName = judgeDsName(dsName);
		return getLockManager().acquireLock_RequiresNew(lockable, userID,
				dsName);
	}

	public boolean acquireBatchLock_RequiresNew(String[] lockables,
			String dsName) {
		checkLockableNotNull(lockables);
		String userID = getCurrUser();
		dsName = judgeDsName(dsName);
		return getLockManager().acquireBatchLock_RequiresNew(lockables, userID,
				dsName);
	}

	public void releaseLock_RequiresNew(String lockable, String userID,
			String dsName) {
		checkLockableNotNull(lockable);
		userID = getCurrUser();
		dsName = judgeDsName(dsName);
		getLockManager().releaseLock_RequiresNew(lockable, userID, dsName);
	}

	public void releaseBatchLock_RequiresNew(String[] lockables, String userID,
			String dsName) {
		checkLockableNotNull(lockables);
		userID = getCurrUser();
		dsName = judgeDsName(dsName);
		getLockManager()
				.releaseBatchLock_RequiresNew(lockables, userID, dsName);
	}

	/**
	 * only for system management
	 */
	public void releaseUserLocks_RequiresNew(String userID, String dsName) {
		if (userID == null) {
			userID = getCurrUser();
		}
		if (dsName == null) {
			dsName = judgeDsName(dsName);
		}
		getLockManager().releaseUserLocks_RequiresNew(userID, dsName);
	}

	/**
	 * only form system management
	 */
	public LockableVO[] getUserLockVOs_RequiresNew(String userID, String dsName) {
		if (userID == null) {
			userID = getCurrUser();
		}
		if (dsName == null) {
			dsName = judgeDsName(dsName);
		}
		return getLockManager().getUserLockVOs_RequiresNew(userID, dsName);
	}

	public LockableVO[] getAllLockVOs_RequiresNew(String dsName) {
		dsName = judgeDsName(dsName);
		return getLockManager().getAllLockVOs_RequiresNew(dsName);
	}
	
	public void releasePKByUserForce(String userid) {
		getLockManager().releasePKByUserForce(userid);
	}
	
	public List<Map> getFailInfo() {
		return getLockManager().getFailInfo();
	}
	
	public void releasePKByMachine(String machine) {
		getLockManager().releasePKByMachine(machine);
	}

	/* 依赖解决区域 */

	/**
	 * @return Returns the lockManager.
	 */
	public ILockManager getLockManager() {
		return lockManager;
	}

	/**
	 * @param lockManager
	 *            The lockManager to set.
	 */
	public void setLockManager(ILockManager lockManager) {
		this.lockManager = lockManager;
	}
}
