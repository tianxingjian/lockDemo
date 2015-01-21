package cn.com.zwz.lock.service;

import cn.com.zwz.lock.model.LockableVO;

/**
 * <p>
 * 	ȫ���������ӿ�, Ŀ�ľ�������ILockManager��Ӧ�õ���ʹ�ô˽ӿ�ʵ��������ע��ILockManager����ɲ�ͬ
 *  ����Ӧ��
 * <p>
 * @author tianxingjian
 *
 */
public interface ILockBS {

	public final static String STR_SHARED_LOCK = "$@$SHARED_LOCK$@$";  
    
	public final static String STR_USER_CONS = "#PUB#";       
    /**
     * ��ȡ������
     * 
     * @return �ɹ����� true
     * @param lockable
     *            ������ ID��20 �ֽ� String
     * @param dsName
     *            ����Դ��,�ֱ���Ա�ǩ
     */
    public abstract boolean isLocked_RequiresNew(String lockable, String userID, String dsName);
    /**
     * ��ȡ������
     * 
     * @return �ɹ����� true
     * @param lockable
     *            ������ ID��20 �ֽ� String
     * @param dsName
     *            ����Դ��,�ֱ���Ա�ǩ
     */
    public abstract boolean acquireLock_RequiresNew(String lockable, String dsName);
  
    /**
     * ������ȡ��
     * 
     * @return �ɹ����� true
     * @param lockables
     *            ������ ID ���飬��Ԫ��Ϊ 128 �ֽ� String
     * @param dsName
     *            ����Դ��,�ֱ���Ա�ǩ
     */
    public abstract boolean acquireBatchLock_RequiresNew(String[] lockables, String dsName);

    /**
     * �ͷŵ�����
     * 
     * @return void
     * @param lockable
     *            ������ ID��20 �ֽ� String
     * @param label
     *            �ֱ���Ա�ǩ��Ĭ������ӵ�����û����ڹ�˾ ID��4 �ֽ� String
     */
    public abstract void releaseLock_RequiresNew(String lockable, String userID, String dsName);
    
   

    /**
     * �����ͷ���
     * 
     * @return void
     * @param lockables
     *            ������ ID ���飬��Ԫ��Ϊ 20 �ֽ� String
     * @param label
     *            �ֱ���Ա�ǩ��Ĭ������ӵ�����û����ڹ�˾ ID��4 �ֽ� String
     */
    public abstract void releaseBatchLock_RequiresNew(String[] lockables, String userID, String dsName);

    /**
     * ���ض��û��ͷ���
     * 
     * @return void
     * @param owner
     *            ��ӵ�����û� ID��20 �ֽ� String
     * @param label
     *            �ֱ���Ա�ǩ��Ĭ������ӵ�����û����ڹ�˾ ID��4 �ֽ� String
     */
    public abstract void releaseUserLocks_RequiresNew(String userID,
            String dsName);

    /**
     * ��ȡһ�ض��û�����VO
     * 
     * @param userID
     *            �û�ID
     * @param dsName
     *            ���ݿ�����
     * @return
     */
    public abstract LockableVO[] getUserLockVOs_RequiresNew(String userID,
            String dsName);

    /**
     * ��ȡ�����ݿ������е���VO
     * 
     * @param dsName
     *            ���ݿ�����
     * @return
     */
    public abstract LockableVO[] getAllLockVOs_RequiresNew(String dsName);
}
