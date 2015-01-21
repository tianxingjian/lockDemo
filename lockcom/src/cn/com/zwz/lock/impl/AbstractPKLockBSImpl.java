package cn.com.zwz.lock.impl;

import cn.com.zwz.lock.service.ILockBS;
import cn.com.zwz.lock.support.IDataSourceFactory;
import cn.com.zwz.lock.support.IUserMsgManager;


/**
 * <p>
 * 	 LockBS虚基类。提供参数检查功能
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
     * 从ThreadLocal中获得userId。如果不存在，使用ILockBS.STR_USER_CONS常量。
     * @return
     */
    protected String getCurrUser() {
        String user = getUserManager().getCurrentUserId();
        if (user == null)
            user = ILockBS.STR_USER_CONS;
        return user;
    }

    /**
     * 确保lock id不能为空
     * @param lockable
     */
    protected void checkLockableNotNull(String lockable) {
        if (lockable == null)
            throw new IllegalArgumentException("lock resource id can't be null");
    }

    /**
     * 确保lock id不能为空
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
