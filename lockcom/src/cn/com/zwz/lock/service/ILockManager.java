package cn.com.zwz.lock.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.zwz.lock.model.LockableVO;

/**
 * 
 * @author tianxingjian
 *
 */
public interface ILockManager {
	
	public final String GROUP_CORP_KEY = "0000";
	/**
	 * 获取单个锁
	 * @return 成功返回 true
	 * @param lockable 锁对象 ID，128 字节 VARCHAR
	 * @param userID 锁拥有者用户 ID，20 字节 String
	 * @param dsName 数据库名称
	 */
	public abstract boolean acquireLock_RequiresNew(String lockable, String userID,
			String dsName);
	/**
	 * 看该锁是否已加上
	 * @return 成功返回 true
	 * @param lockable 锁对象 ID，128 字节 VARCHAR
	 * @param userID 锁拥有者用户 ID，20 字节 String
	 * @param dsName 数据库名称
	 */
	public abstract boolean isLocked_RequiresNew(String lockable, String userID,
			String dsName);

	/**
	 * 批量获取锁
	 * @return 成功返回 true
	 * @param lockables 锁对象 ID 数组，其元素为 20 字节 String
	 * @param userID 锁拥有者用户 ID，20 字节 String
	 * @param dsName 数据库名称
	 */
	public abstract boolean acquireBatchLock_RequiresNew(String[] lockables, String userID,
			String dsName);

	/**
	 * 释放单个锁
	 * @return void
	 * @param lockable 锁对象 ID，128 字节 VARCHAR
	 * @param userID 锁拥有者用户 ID，20 字节 String
	 * @param dsName 数据库名称
	 */
	public abstract void releaseLock_RequiresNew(String lockable, String userID, String dsName);

	/**
	 * 批量释放锁
	 * @return void
	 * @param lockables 锁对象 ID 数组，其元素为 20 字节 String
	 * @param userID 锁拥有者用户 ID，20 字节 String
	 * @param dsName 数据库名称
	 */
	public abstract void releaseBatchLock_RequiresNew(String[] lockables, String userID,
			String dsName);

	/**
	 * 按用户释放锁
	 * @return void
	 * @param userID 锁拥有者用户 ID，20 字节 String
	 * @param dsName 数据库名称
	 */
	public abstract void releaseUserLocks_RequiresNew(String userID, String dsName);

	/**
	 * 虚拟机启动解锁
	 * @return void
	 */
	public abstract void onJvmStart_RequiresNew();
	
	/**
	 * 获取该数据库中所有的锁VO
	 * @param dsName 数据库名称
	 * @return
	 */
	public LockableVO[] getAllLockVOs_RequiresNew(String dsName);

	/**
	 * 获取一特定用户的锁VO
	 * @param userID 用户ID
	 * @param dsName 数据库名称
	 * @return
	 */
	public LockableVO[] getUserLockVOs_RequiresNew(String userID, String dsName);
	/**
	 * 
	 * 判断是否可加锁 
	 * <strong>最后修改人</strong>
	 * dengjt
	 * @param pk 待加锁资源ID，以常量IPKLockBS.STR_SHARED_LOCK结尾为共享锁
	 * @param dsName 数据库名称
	 * @return true:可加，false，不可加
	 */
	public boolean canLockPK_RequiresNew(String lockable, String dsName, Set shareLockList);

	/**
	 * 根据用户强制释放锁
	 */
	void releasePKByUserForce(String userid);
	
	/**
	 * 获得加锁失败信息
	 */
	List<Map> getFailInfo();
	
	void releasePKByMachine(String machine);
}
