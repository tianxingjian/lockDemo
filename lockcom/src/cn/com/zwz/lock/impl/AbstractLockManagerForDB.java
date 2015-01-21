package cn.com.zwz.lock.impl;

import org.apache.log4j.Logger;

import cn.com.zwz.lock.service.ILockManager;
import cn.com.zwz.lock.support.ILockTableInfo;

public abstract class AbstractLockManagerForDB implements ILockManager{
    /** ������ڵ�Ψһ ID ��ʶ��Ӧ�ô� NCFramework �����л�� */
	protected static final String JVM = "jvm_name_uap_chencen";

	/** ����������������������棩 */
	protected static int MAX_BATCH = 10000;

	/** ������� */
	protected static final String GROUP_TABLE_NAME = ILockTableInfo.GROUP_TABLE_NAME;

    protected static final String INSERT_SQL = "insert into "
            + GROUP_TABLE_NAME + " (Lockable, UserID, JVM, Locktype) "
            + "values (?, ?, '" + JVM + "',?)";
  
    protected static final String DELETE_SINGLE_SQL = "delete from "
            + GROUP_TABLE_NAME
            + " where rtrim(Lockable) = ? and (rtrim(UserID) = ? )";

    protected static final String DELETE_BY_USER_SQL = "delete from "
            + GROUP_TABLE_NAME
            + " where rtrim(UserID) = ? ";

    protected static final String DELETE_FOR_SPECIFIC_DB = "delete from "
        + GROUP_TABLE_NAME
        + " where rtrim(UserID) = ?";

    protected static final String CHECK_SQL = "select Lockable from "
            + GROUP_TABLE_NAME + " where Lockable = ? and UserID = ? and LockType = ?";
 
    /** �ж��Ƿ���������*/
    protected static final String CHECK_SQL_EXCLUDE = "select UserId,LockType from "
    	 + GROUP_TABLE_NAME + " where Lockable= ? ";
    /**�ж��Ƿ�����*/
    protected static final String CHECK_SQL_ALL = "select Lockable from "
    	+ GROUP_TABLE_NAME + " where Lockable= ?";
    	 

    protected static final String DELETE_BY_JVM_SQL = "delete from "
            + GROUP_TABLE_NAME + " where rtrim(JVM) = '" + JVM + "'";
	
	/** ��¼��־ */
	protected Logger logger = Logger.getLogger(this.getClass());
	
	
}