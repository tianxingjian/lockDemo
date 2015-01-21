package cn.com.zwz.lock.model;

import java.io.Serializable;

/**
 * <p>
 * 	ȫ����VO
 * <p>
 * @author tianxingjian
 *
 */
public class LockableVO implements Serializable {

	private static final long serialVersionUID = -533703132202466443L;

	//��������ʶ
	public static final int SHARE = 1;

	//��������ʶ
	public static final int EXCLUSIVE = 0;

	//�û�ID����ʶ˭��ȡ����
	private String userID = "";

	//����Դ��һ���ǿ�Ψһ��ʶҪ������Դ�ı�ʶ
	private String lockable = "";

	// ����Դ��,�ֱ���Ա�ǩ
	private String ds = "";

	//�����ͣ�Ĭ����������
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
	 *            1:������ 0��������
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
