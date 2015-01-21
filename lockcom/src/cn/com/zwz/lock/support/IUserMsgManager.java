package cn.com.zwz.lock.support;

/**
 * <p>
 * 	当前操作用户获取接口
 *  加锁时用户是其中的一个粒度，提供获取当前用户的接口。在应用设计中可以实现这个接口完成取当期用户。
 *  子类实现此接口时应该通过ThreadLock来存储当期线程用户
 *  比如WEB中可以根据上下文或者Session获取到当前用户
 * <p>
 * @author tianxingjian
 *
 */
public interface IUserMsgManager {
	public String getCurrentUserId();
	public String getCurrentUserId(String userId);
}
