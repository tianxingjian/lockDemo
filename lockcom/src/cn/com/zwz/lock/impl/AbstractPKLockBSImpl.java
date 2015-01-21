package cn.com.zwz.lock.impl;

import cn.com.zwz.lock.service.ILockBS;
import cn.com.zwz.lock.support.IDataSourceFactory;
import cn.com.zwz.lock.support.IUserMsgManager;


/**
 * <p>
 * 	 LockBS����ࡣ�ṩ������鹦��
 * <p>

 */
public abstract class AbstractPKLockBSImpl implements ILockBS {
	
	IUserMsgManager userManager;
	IDataSourceFactory dataSourceFactory;

    public IUserMsgManager getUserManager() {
		return userManager;
	}

	public void setUserManager(IUserMsgManager userManager) {
		this.userManager = userManager;
	}

	public IDataSourceFactory getDataSourceFactory() {
		return dataSourceFactory;
	}

	public void setDataSourceFactory(IDataSourceFactory dataSourceFactory) {
		this.dataSourceFactory = dataSourceFactory;
	}
	/**
     * ��ThreadLocal�л��userId����������ڣ�ʹ��ILockBS.STR_USER_CONS������
     * @return
     */
    protected String getCurrUser() {
        String user = getUserManager().getCurrentUserId();
        if (user == null)
            user = ILockBS.STR_USER_CONS;
        return user;
    }

    /**
     * ȷ��lock id����Ϊ��
     * @param lockable
     */
    protected void checkLockableNotNull(String lockable) {
        if (lockable == null)
            throw new IllegalArgumentException("lock resource id can't be null");
    }

    /**
     * ȷ��lock id����Ϊ��
     * @param lockables
     */
    protected void checkLockableNotNull(String[] lockables) {
        if (lockables == null || lockables.length == 0)
            throw new IllegalArgumentException("lock resource id can't be null");
        for (int i = 0; i < lockables.length; i++) {
            if (lockables[i] == null)
                throw new IllegalArgumentException("lock resource id array can't contains null value");
        }
    }
    
    protected String judgeDsName(String oriName) {
        return getDataSourceFactory().getCurrentDataSource(oriName);
    }
}
