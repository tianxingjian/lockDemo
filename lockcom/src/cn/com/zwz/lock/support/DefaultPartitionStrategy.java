package cn.com.zwz.lock.support;

/**
 * <p>
 * 	Ĭ�ϵķֱ����
 * <p>
 * @author tianxingjian
 *
 */
public class DefaultPartitionStrategy extends AbstractPartitionStrategy {

	public String getTableName(String label) {
		// �ֱ���ݹ�˾ ID ����ģ���䣬���� ID ������
		if (label.compareTo(GROUP_CORP_KEY) == 0)
			return ILockTableInfo.GROUP_TABLE_NAME;
		long p = Long.parseLong(label);
		return (ILockTableInfo.GROUP_TABLE_NAME + "_" + (p % TABLE_NUM));
	}

}