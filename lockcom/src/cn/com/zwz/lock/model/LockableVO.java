package cn.com.zwz.lock.model;

import java.io.Serializable;

/**
 * <p>
 * 	全局锁VO
 * <p>
 * @author tianxingjian
 *
 */
public class LockableVO implements Serializable {

	private static final long serialVersionUID = -533703132202466443L;

	//共享锁标识
	public static final int SHARE = 1;

	//排他锁标识
	public static final int EXCLUSIVE = 0;

	//用户ID，标识谁获取到锁
	private String userID = "";

	//锁资源，一般是可唯一标识要加锁资源的标识
	private String lockable = "";

	// 数据源名,分表策略标签
	private String ds = "";

	//锁类型，默认是排他锁
	private int lockType = EXCLUSIVE;
	
	private int count;

	/**
	 * @return Returns the LockType
	 */
	public int getLockType() {
		return lockType;
	}

	/**
	 * 
	 * @param lockType
	 *            1:共享锁 0：排它锁
	 */
	public void setLockType(int lockType) {
		this.lockType = lockType;
	}

	/**
	 * @return Returns the jvmID.
	 */
	public String getDs() {
		return ds;
	}

	/**
	 * @param jvmID
	 *            The jvmID to set.
	 */
	public void setDs(String ds) {
		this.ds = ds;
	}

	/**
	 * @return Returns the lockable.
	 */
	public String getLockable() {
		return lockable;
	}

	/**
	 * @param lockable
	 *            The lockable to set.
	 */
	public void setLockable(String lockable) {
		this.lockable = lockable;
	}

	/**
	 * @return Returns the userID.
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID
	 *            The userID to set.
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getRealUserID() {
		return userID;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
