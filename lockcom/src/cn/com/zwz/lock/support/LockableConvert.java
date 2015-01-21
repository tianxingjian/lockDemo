package cn.com.zwz.lock.support;

import java.sql.ResultSet;
import java.util.List;

import cn.com.zwz.lock.model.LockableVO;

public class LockableConvert implements ConvertObject<LockableVO> {

	@Override
	public LockableVO covertRsToObject(ResultSet rs) {
		return null;
	}

	@Override
	public List<LockableVO> covertRsToObjects(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LockableVO[] covertRsToObjectArray(ResultSet rs) {
		// TODO Auto-generated method stub
		return null;
	}

}
