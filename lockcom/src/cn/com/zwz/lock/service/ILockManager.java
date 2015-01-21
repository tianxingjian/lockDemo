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
	 * ��ȡ������
	 * @return �ɹ����� true
	 * @param lockable ������ ID��128 �ֽ� VARCHAR
	 * @param userID ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName ���ݿ�����
	 */
	public abstract boolean acquireLock_RequiresNew(String lockable, String userID,
			String dsName);
	/**
	 * �������Ƿ��Ѽ���
	 * @return �ɹ����� true
	 * @param lockable ������ ID��128 �ֽ� VARCHAR
	 * @param userID ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName ���ݿ�����
	 */
	public abstract boolean isLocked_RequiresNew(String lockable, String userID,
			String dsName);

	/**
	 * ������ȡ��
	 * @return �ɹ����� true
	 * @param lockables ������ ID ���飬��Ԫ��Ϊ 20 �ֽ� String
	 * @param userID ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName ���ݿ�����
	 */
	public abstract boolean acquireBatchLock_RequiresNew(String[] lockables, String userID,
			String dsName);

	/**
	 * �ͷŵ�����
	 * @return void
	 * @param lockable ������ ID��128 �ֽ� VARCHAR
	 * @param userID ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName ���ݿ�����
	 */
	public abstract void releaseLock_RequiresNew(String lockable, String userID, String dsName);

	/**
	 * �����ͷ���
	 * @return void
	 * @param lockables ������ ID ���飬��Ԫ��Ϊ 20 �ֽ� String
	 * @param userID ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName ���ݿ�����
	 */
	public abstract void releaseBatchLock_RequiresNew(String[] lockables, String userID,
			String dsName);

	/**
	 * ���û��ͷ���
	 * @return void
	 * @param userID ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName ���ݿ�����
	 */
	public abstract void releaseUserLocks_RequiresNew(String userID, String dsName);

	/**
	 * �������������
	 * @return void
	 */
	public abstract void onJvmStart_RequiresNew();
	
	/**
	 * ��ȡ�����ݿ������е���VO
	 * @param dsName ���ݿ�����
	 * @return
	 */
	public LockableVO[] getAllLockVOs_RequiresNew(String dsName);

	/**
	 * ��ȡһ�ض��û�����VO
	 * @param userID �û�ID
	 * @param dsName ���ݿ�����
	 * @return
	 */
	public LockableVO[] getUserLockVOs_RequiresNew(String userID, String dsName);
	/**
	 * 
	 * �ж��Ƿ�ɼ��� 
	 * <strong>����޸���</strong>
	 * dengjt
	 * @param pk ��������ԴID���Գ���IPKLockBS.STR_SHARED_LOCK��βΪ������
	 * @param dsName ���ݿ�����
	 * @return true:�ɼӣ�false�����ɼ�
	 */
	public boolean canLockPK_RequiresNew(String lockable, String dsName, Set shareLockList);

	/**
	 * �����û�ǿ���ͷ���
	 */
	void releasePKByUserForce(String userid);
	
	/**
	 * ��ü���ʧ����Ϣ
	 */
	List<Map> getFailInfo();
	
	void releasePKByMachine(String machine);
}
