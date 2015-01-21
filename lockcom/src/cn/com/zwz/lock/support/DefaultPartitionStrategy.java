package cn.com.zwz.lock.support;

/**
 * <p>
 * 	默认的分表策略
 * <p>
 * @author tianxingjian
 *
 */
public class DefaultPartitionStrategy extends AbstractPartitionStrategy {

	public String getTableName(String label) {
		// 分表根据公司 ID 进行模分配，集团 ID 进主表
		if (label.compareTo(GROUP_CORP_KEY) == 0)
			return ILockTableInfo.GROUP_TABLE_NAME;
		long p = Long.parseLong(label);
		return (ILockTableInfo.GROUP_TABLE_NAME + "_" + (p % TABLE_NUM));
	}

}