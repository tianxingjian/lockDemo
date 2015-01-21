package cn.com.zwz.lock.service;

import cn.com.zwz.lock.model.LockableVO;

/**
 * <p>
 * 	全局锁公共接口, 目的就是适配ILockManager，应用调用使用此接口实现类依赖注入ILockManager来完成不同
 *  的锁应用
 * <p>
 * @author tianxingjian
 *
 */
public interface ILockBS {

	public final static String STR_SHARED_LOCK = "$@$SHARED_LOCK$@$";  
    
	public final static String STR_USER_CONS = "#PUB#";       
    /**
     * 获取单个锁
     * 
     * @return 成功返回 true
     * @param lockable
     *            锁对象 ID，20 字节 String
     * @param dsName
     *            数据源名,分表策略标签
     */
    public abstract boolean isLocked_RequiresNew(String lockable, String userID, String dsName);
    /**
     * 获取单个锁
     * 
     * @return 成功返回 true
     * @param lockable
     *            锁对象 ID，20 字节 String
     * @param dsName
     *            数据源名,分表策略标签
     */
    public abstract boolean acquireLock_RequiresNew(String lockable, String dsName);
  
    /**
     * 批量获取锁
     * 
     * @return 成功返回 true
     * @param lockables
     *            锁对象 ID 数组，其元素为 128 字节 String
     * @param dsName
     *            数据源名,分表策略标签
     */
    public abstract boolean acquireBatchLock_RequiresNew(String[] lockables, String dsName);

    /**
     * 释放单个锁
     * 
     * @return void
     * @param lockable
     *            锁对象 ID，20 字节 String
     * @param label
     *            分表策略标签，默认是锁拥有者用户所在公司 ID，4 字节 String
     */
    public abstract void releaseLock_RequiresNew(String lockable, String userID, String dsName);
    
   

    /**
     * 批量释放锁
     * 
     * @return void
     * @param lockables
     *            锁对象 ID 数组，其元素为 20 字节 String
     * @param label
     *            分表策略标签，默认是锁拥有者用户所在公司 ID，4 字节 String
     */
    public abstract void releaseBatchLock_RequiresNew(String[] lockables, String userID, String dsName);

    /**
     * 按特定用户释放锁
     * 
     * @return void
     * @param owner
     *            锁拥有者用户 ID，20 字节 String
     * @param label
     *            分表策略标签，默认是锁拥有者用户所在公司 ID，4 字节 String
     */
    public abstract void releaseUserLocks_RequiresNew(String userID,
            String dsName);

    /**
     * 获取一特定用户的锁VO
     * 
     * @param userID
     *            用户ID
     * @param dsName
     *            数据库名称
     * @return
     */
    public abstract LockableVO[] getUserLockVOs_RequiresNew(String userID,
            String dsName);

    /**
     * 获取该数据库中所有的锁VO
     * 
     * @param dsName
     *            数据库名称
     * @return
     */
    public abstract LockableVO[] getAllLockVOs_RequiresNew(String dsName);
}
