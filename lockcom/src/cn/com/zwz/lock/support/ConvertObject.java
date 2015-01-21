package cn.com.zwz.lock.support;

import java.sql.ResultSet;
import java.util.List;

public interface ConvertObject<T> {
	T covertRsToObject(ResultSet rs);
	
	List<T> covertRsToObjects(ResultSet rs);
	
	T[] covertRsToObjectArray(ResultSet rs);
}
