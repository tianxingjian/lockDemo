package cn.com.zwz.lock.support;

/**
 * <p>
 * 	��ǰ����Դ��ȡ�ӿ�
 *  ����ʱ����Դ�����е�һ�����ȣ��ṩ��ȡ��ǰ����Դ�Ľӿڡ���Ӧ������п���ʵ������ӿ����ȡ�����û���
 *  ����ʵ�ִ˽ӿ�ʱ���ͨ��ThreadLock���洢�����߳�����Դ
 *  ����֧��SpringӦ�ó���������ͨ����ȡSession��ȡ��ǰ����Դ������ͨ��Service����ע�����û�ȡ����Դ
 * <p>
 * @author tianxingjian
 *
 */
public interface IDataSourceFactory {
	String getCurrentDataSource(String ds);
	String getCurrentDataSource();
}
