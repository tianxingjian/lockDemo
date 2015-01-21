package cn.com.zwz.lock.support;

/**
 * <p>
 * 	当前数据源获取接口
 *  加锁时数据源是其中的一个粒度，提供获取当前数据源的接口。在应用设计中可以实现这个接口完成取当期用户。
 *  子类实现此接口时最好通过ThreadLock来存储当期线程数据源
 *  比如支持Spring应用程序的事务可通过获取Session获取当前数据源，可以通过Service依赖注入配置获取数据源
 * <p>
 * @author tianxingjian
 *
 */
public interface IDataSourceFactory {
	String getCurrentDataSource(String ds);
	String getCurrentDataSource();
}
