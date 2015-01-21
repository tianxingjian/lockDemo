package cn.com.zwz.lock.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.zwz.lock.model.LockableVO;
import cn.com.zwz.lock.support.ShareData;

public class LockManagerForMem extends AbstractLockManagerForMem {

	@Override
	public boolean acquireLock_RequiresNew(String lockable, String userID,
			String dsName) {
		return ShareData.getInstance().lockPK(lockable, userID, dsName);
	}

	@Override
	public boolean isLocked_RequiresNew(String lockable, String userID,
			String dsName) {
		return ShareData.getInstance().isLocked(lockable, userID, dsName);
	}

	@Override
	public boolean acquireBatchLock_RequiresNew(String[] lockables,
			String userID, String dsName) {
		return ShareData.getInstance().lockPK(lockables, userID, dsName);
	}

	@Override
	public void releaseLock_RequiresNew(String lockable, String userID,
			String dsName) {
		ShareData.getInstance().releasePK(lockable, userID, dsName);
	}

	@Override
	public void releaseBatchLock_RequiresNew(String[] lockables, String userID,
			String dsName) {
		ShareData.getInstance().releasePK(lockables, userID, dsName);
	}

	@Override
	public void releaseUserLocks_RequiresNew(String userID, String dsName) {
		ShareData.getInstance().releasePKByUser(userID, dsName);
	}

	@Override
	public void onJvmStart_RequiresNew() {

	}

	@Override
	public LockableVO[] getAllLockVOs_RequiresNew(String dsName) {
		return ShareData.getInstance().getAllLockVOs(dsName);
	}

	public LockableVO[] getUserLockVOs_RequiresNew(String userID, String dsName) {
		return ShareData.getInstance().getUserLockVOs(userID, dsName);
	}

	@Override
	public boolean canLockPK_RequiresNew(String lockable, String dsName,
			Set shareLockList) {
		return false;
	}
	
	public void releasePKByUserForce(String userid) {
		ShareData.getInstance().releasePKByUserForce(userid);
	}
	
	public List<Map> getFailInfo() {
		return ShareData.getInstance().getFailInfo();
	}
	
	public void releasePKByMachine(String machine) {
		ShareData.getInstance().releasePKByMachine(machine);
	}
	
}
