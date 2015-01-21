package cn.com.zwz.lock.support;

import cn.com.zwz.lock.service.ILockManager;

public interface ILockTableInfo {
	
	/**
	 * 全局锁标识
	 */
	public static final String GROUP_CORP_KEY = ILockManager.GROUP_CORP_KEY;
	/** 主表表名 */
	public static final String GROUP_TABLE_NAME = "pub_lock";
	/** 分表数量 */
	public static final int TABLE_NUM = 5;

	/**
	 * @param label 区分标志，默认实现为公司 ID
	 * @return 业务锁表名 
	 */
	public String getTableName(String label);
}
