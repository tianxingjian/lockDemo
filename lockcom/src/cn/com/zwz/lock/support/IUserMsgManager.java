package cn.com.zwz.lock.support;

/**
 * <p>
 * 	��ǰ�����û���ȡ�ӿ�
 *  ����ʱ�û������е�һ�����ȣ��ṩ��ȡ��ǰ�û��Ľӿڡ���Ӧ������п���ʵ������ӿ����ȡ�����û���
 *  ����ʵ�ִ˽ӿ�ʱӦ��ͨ��ThreadLock���洢�����߳��û�
 *  ����WEB�п��Ը��������Ļ���Session��ȡ����ǰ�û�
 * <p>
 * @author tianxingjian
 *
 */
public interface IUserMsgManager {
	public String getCurrentUserId();
	public String getCurrentUserId(String userId);
}
