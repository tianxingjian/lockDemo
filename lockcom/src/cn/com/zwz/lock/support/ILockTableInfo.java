package cn.com.zwz.lock.support;

import cn.com.zwz.lock.service.ILockManager;

public interface ILockTableInfo {
	
	/**
	 * ȫ������ʶ
	 */
	public static final String GROUP_CORP_KEY = ILockManager.GROUP_CORP_KEY;
	/** ������� */
	public static final String GROUP_TABLE_NAME = "pub_lock";
	/** �ֱ����� */
	public static final int TABLE_NUM = 5;

	/**
	 * @param label ���ֱ�־��Ĭ��ʵ��Ϊ��˾ ID
	 * @return ҵ�������� 
	 */
	public String getTableName(String label);
}
