package cn.com.zwz.lock.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.com.zwz.lock.model.LockableVO;
import cn.com.zwz.lock.service.ILockBS;

/**
 * 内存锁数据类
 */
public class ShareData {

	private static ShareData instance = new ShareData();

	private Map<String, Map<String, ShareLockData>> shareLockMap;

	private Map<String, Map<String, String>> exclusiveLockMap;

	private static final int MAX_LOCKS = 1024 * 1000 * 3;

	private static final int INIT_SIZE = 1024 * 8;

	private List<Map> alFail = new ArrayList<Map>(300);

	private Map<String, Map<String, String>> machineMap = new HashMap<String, Map<String, String>>();

	private static Logger logger = Logger.getLogger(ShareData.class);
	/**
	 * ShareData 构造子注解。
	 */
	private ShareData() {
		super();
		shareLockMap = new HashMap<String, Map<String, ShareLockData>>();
		exclusiveLockMap = new HashMap<String, Map<String, String>>();
	}

	public static ShareData getInstance() {
		return instance;
	}

	public synchronized void releasePKByUserForce(String userid) {
		Iterator it = shareLockMap.values().iterator();
		while (it.hasNext()) {
			Map sharedMap = (Map) it.next();
			Iterator kit = sharedMap.keySet().iterator();
			while (kit.hasNext()) {
				Object key = kit.next();
				ShareLockData sld = (ShareLockData) sharedMap.get(key);
				if (sld.decreaseLockByUser(userid))
					kit.remove();
			}
		}
		it = exclusiveLockMap.values().iterator();
		while (it.hasNext()) {
			Map excludeMap = (Map) it.next();
			Iterator kit = excludeMap.keySet().iterator();
			while (kit.hasNext()) {
				Object key = kit.next();
				if (excludeMap.get(key).equals(userid)) {
					kit.remove();
				}
			}
		}
	}

	public List<Map> getFailInfo() {
		return alFail;
	}

	private void logFail(String userid, String ds, String lock) {
		if (ds == null)
			return;
		try {
			boolean isShare = isShareLock(lock);
			String elock = extractLock(lock, isShare);
			Map<String, String> dsLockMap = exclusiveLockMap.get(ds);
			String holdUser = (dsLockMap == null ? null : dsLockMap.get(elock));

			Map<String, String> mLockMap = machineMap.get(ds);

			String machine = null;
			if (mLockMap != null) {
				machine = mLockMap.get(lock);
			}

			Map<String, String> failInfo = new HashMap<String, String>();
			failInfo.put("ru", userid);
			failInfo.put("hu", holdUser);
			failInfo.put("lock", lock);
			failInfo.put("ts", String.valueOf(System.currentTimeMillis()));
			failInfo.put("machine", machine);
			alFail.add(failInfo);
			if (alFail.size() > 300) {
				alFail.remove(0);
			}
		} catch (Exception e) {

		}
	}

	public void releasePKByMachine(String machine) {
		logger.debug("releasePKByMachine server=" + machine);
		if (machine == null) {
			return;
		}
		Iterator it = shareLockMap.values().iterator();
		while (it.hasNext()) {
			Map sharedMap = (Map) it.next();
			Iterator kit = sharedMap.keySet().iterator();
			while (kit.hasNext()) {
				Object key = kit.next();
				ShareLockData sld = (ShareLockData) sharedMap.get(key);
				if (sld.decreaseLockByMachine(machine))
					kit.remove();
			}
		}

		it = exclusiveLockMap.keySet().iterator();

		while (it.hasNext()) {
			String ds = (String) it.next();
			Map<String, String> excludeMap = exclusiveLockMap.get(ds);
			Map<String, String> mMap = machineMap.get(ds);
			if (mMap != null) {
				Iterator<String> itr = mMap.keySet().iterator();
				while (itr.hasNext()) {
					String lock = itr.next();
					if (machine.equals(mMap.get(lock))) {
						itr.remove();
						excludeMap.remove(lock);
					}
				}
			}
		}

	}

	public synchronized boolean lockPK(String lock, String userid, String ds) {

		String callServer = null;
		if (lock.indexOf("B$_") > -1) {
			callServer = lock.substring(3, lock.lastIndexOf("B$_"));
			lock = lock.substring(lock.lastIndexOf("B$_") + 3);
		}

		if (canLock(ds, lock)) {
			lock(ds, userid, lock, callServer);
			return true;
		}

		logFail(userid, ds, lock);

		return false;
	}

	public synchronized boolean lockPK(String[] lockable, String userid,
			String dsName) {
		String callServer = null;
		for (int i = 0; i < lockable.length; i++) {
			if (lockable[i].indexOf("B$_") > -1) {
				callServer = lockable[i].substring(3, lockable[i]
						.lastIndexOf("B$_"));
				lockable[i] = lockable[i].substring(lockable[i]
						.lastIndexOf("B$_") + 3);
			}
		}

		for (int i = 0; i < lockable.length; i++) {
			if (!canLock(dsName, lockable[i])) {
				logFail(userid, dsName, lockable[i]);
				return false;
			}
		}
		for (int i = 0; i < lockable.length; i++) {
			lock(dsName, userid, lockable[i], callServer);
		}
		return true;
	}

	public synchronized void releasePK(String[] locks, String userid,
			String dsName) {
		for (int i = 0; i < locks.length; i++) {
			releasePK(locks[i], userid, dsName);
		}
	}

	public synchronized void releasePK(String lock, String userid, String dsName) {
		unlock(dsName, userid, lock);
	}

	public synchronized void releasePKByUser(String userid, String ds) {
		Iterator it = shareLockMap.values().iterator();
		while (it.hasNext()) {
			Map sharedMap = (Map) it.next();
			Iterator kit = sharedMap.keySet().iterator();
			while (kit.hasNext()) {
				Object key = kit.next();
				if (!key.toString().startsWith("F$_")) {
					continue;
				}
				ShareLockData sld = (ShareLockData) sharedMap.get(key);

				if (sld.decreaseLockByUser(userid))
					kit.remove();
			}
		}
		it = exclusiveLockMap.values().iterator();
		while (it.hasNext()) {
			Map excludeMap = (Map) it.next();
			Iterator kit = excludeMap.keySet().iterator();
			while (kit.hasNext()) {
				Object key = kit.next();
				// zhaogb 2009-10-19
				if (!key.toString().startsWith("F$_")) {
					continue;
				}
				if (excludeMap.get(key).equals(userid)) {
					kit.remove();
				}
			}
		}
	}

	/**
	 * 判断相应类型的锁是否已加上
	 * 
	 * TODO:????
	 * 
	 * @param lock
	 * @param userid
	 * @param ds
	 * @return
	 */
	public boolean isLocked(String lock, String userid, String ds) {
		if (lock.indexOf("B$_") > -1) {
			if (lock.indexOf("B$_") > -1) {
				lock = lock.substring(lock.lastIndexOf("B$_") + 3);
			}
		}
		boolean isShare = isShareLock(lock);
		lock = extractLock(lock, isShare);
		Map dsLockMap = null;
		if (isShare) {
			dsLockMap = shareLockMap.get(ds);
		} else {
			dsLockMap = exclusiveLockMap.get(ds);
		}
		return dsLockMap != null && dsLockMap.containsKey(lock);
	}

	public LockableVO[] getUserLockVOs(String userid, String ds) {
		ArrayList<LockableVO> list = new ArrayList<LockableVO>();

		Map dsLockMap = shareLockMap.get(ds);
		if (dsLockMap != null) {
			new ShareExtractor(userid, ds).extract(list, dsLockMap);
		}

		dsLockMap = exclusiveLockMap.get(ds);
		if (dsLockMap != null) {
			new ExclusiveExtractor(userid, ds).extract(list, dsLockMap);
		}

		return (LockableVO[]) list.toArray(new LockableVO[list.size()]);
	}

	/**
	 * 
	 * @param ds
	 * @return
	 */
	public LockableVO[] getAllLockVOs(String ds) {
		ArrayList<LockableVO> list = new ArrayList<LockableVO>();

		Map dsLockMap = shareLockMap.get(ds);
		if (dsLockMap != null) {
			new AllShareExtractor(ds).extract(list, dsLockMap);
		}

		dsLockMap = exclusiveLockMap.get(ds);
		if (dsLockMap != null) {
			new AllExclusiveExtractor(ds).extract(list, dsLockMap);
		}

		return (LockableVO[]) list.toArray(new LockableVO[list.size()]);
	}

	private String extractLock(String lock, boolean isShare) {
		if (isShare) {
			lock = lock.substring(0, lock.length() - ILockBS.STR_SHARED_LOCK.length());
		}
		return lock;
	}

	private boolean isShareLock(String lock) {
		return lock.endsWith(ILockBS.STR_SHARED_LOCK);
	}

	private boolean canLock(String ds, String lock) {
		boolean result = canLockPK(ds, lock);
		if (result && lock.startsWith("F$_")) {
			String lockPK = lock.substring(3);
			return canLockPK(ds, lockPK);
		}
		return result;
	}

	// zhaogb 2009-10-19
	private boolean canLockPK(String ds, String lock) {
		boolean isShare = isShareLock(lock);
		lock = extractLock(lock, isShare);
		Map dsLockMap = exclusiveLockMap.get(ds);
		boolean noe = dsLockMap == null
				|| (!dsLockMap.containsKey(lock) && !dsLockMap
						.containsKey("F$_" + lock));
		if (noe && !isShare) {
			dsLockMap = shareLockMap.get(ds);
			return dsLockMap == null || !dsLockMap.containsKey(lock);

		}
		return noe;
	}

	private void lock(String ds, String userid, String lock, String callServer) {
		boolean isShare = isShareLock(lock);
		lock = extractLock(lock, isShare);

		if (isShare) {
			Map<String, ShareLockData> dsLockMap = shareLockMap.get(ds);
			if (dsLockMap == null) {
				dsLockMap = new HashMap<String, ShareLockData>(INIT_SIZE);
				shareLockMap.put(ds, dsLockMap);
			}
			if (dsLockMap.size() > MAX_LOCKS) {
				throw new LockRuntimeException(
						"HH(share): PKLock overflow ds: " + ds + " max:"
								+ +MAX_LOCKS);
			}
			ShareLockData sld = (ShareLockData) dsLockMap.get(lock);
			if (sld == null) {
				sld = new ShareLockData();
				dsLockMap.put(lock, sld);
			}
			sld.increaseLock(userid, callServer);
		} else {
			Map<String, String> dsLockMap = exclusiveLockMap.get(ds);
			if (dsLockMap == null) {
				dsLockMap = new HashMap<String, String>(INIT_SIZE);
				exclusiveLockMap.put(ds, dsLockMap);
			}
			if (dsLockMap.size() > MAX_LOCKS) {
				throw new LockRuntimeException("HH: PKLock overflow ds: "
						+ ds + " max:" + +MAX_LOCKS);
			}
			dsLockMap.put(lock, userid);

			if (callServer != null) {
				Map<String, String> mLockMap = machineMap.get(ds);
				if (mLockMap == null) {
					mLockMap = new HashMap<String, String>();
					machineMap.put(ds, mLockMap);
				}
				mLockMap.put(lock, callServer);
			}
		}
	}

	private void unlock(String ds, String userid, String lock) {
		String callServer = null;
		if (lock.indexOf("B$_") > -1) {
			if (lock.indexOf("B$_") > -1) {
				callServer = lock.substring(3, lock.lastIndexOf("B$_"));
				lock = lock.substring(lock.lastIndexOf("B$_") + 3);
			}
		}
		boolean isShare = isShareLock(lock);
		lock = extractLock(lock, isShare);
		if (isShare) {
			Map<String, ShareLockData> dsLockMap = shareLockMap.get(ds);
			if (dsLockMap != null) {
				ShareLockData sld = dsLockMap.get(lock);
				if (sld != null) {
					if (sld.decreaseLock(userid, callServer)) {
						dsLockMap.remove(lock);
					}
				}
			}
		} else {
			Map<String, String> dsLockMap = exclusiveLockMap.get(ds);
			if (dsLockMap != null) {
				String useridInMap = dsLockMap.get(lock);
				if (userid.equals(useridInMap)) {
					dsLockMap.remove(lock);
					// zhaogb for server down
					Map<String, String> mLockMap = machineMap.get(ds);
					if (mLockMap != null && mLockMap.containsKey(lock)) {
						mLockMap.remove(lock);
					}
				}
			}
		}
	}

	static abstract class Extractor {
		protected String ds;

		public Extractor(String ds) {
			this.ds = ds;
		}

		@SuppressWarnings("unchecked")
		public void extract(List<LockableVO> list, Map dsLockMap) {
			Set<String> dsLockSet = (Set<String>) dsLockMap.keySet();
			for (String lockable : dsLockSet) {
				extractLockVO(list, dsLockMap, lockable);
			}
		}

		abstract protected void extractLockVO(List<LockableVO> list,
				Map dsLockMap, String lockable);
	}

	static class ShareExtractor extends Extractor {
		protected String userid;

		private Map<String, LockableVO> voMap;

		public ShareExtractor(String userid, String ds) {
			super(ds);
			this.userid = userid;
			voMap = new HashMap<String, LockableVO>();
		}

		@Override
		protected void extractLockVO(List<LockableVO> list, Map dsLockMap,
				String lockable) {
			ShareLockData sld = (ShareLockData) dsLockMap.get(lockable);
			String key = userid + "$$" + lockable;
			if (sld.userSet.contains(userid)) {
				LockableVO vo = voMap.get(key);
				if (vo == null) {
					vo = new LockableVO();
					vo.setDs(ds);
					vo.setLockable(lockable);
					vo.setLockType(LockableVO.SHARE);
					vo.setUserID(userid);
					list.add(vo);
					voMap.put(key, vo);
				}
				vo.setCount(vo.getCount() + 1);
			}
		}
	}

	static class ExclusiveExtractor extends Extractor {
		protected String userid;

		public ExclusiveExtractor(String userid, String ds) {
			super(ds);
			this.userid = userid;
		}

		@Override
		protected void extractLockVO(List<LockableVO> list, Map dsLockMap,
				String lockable) {
			if (userid.equals(dsLockMap.get(lockable))) {
				LockableVO vo = new LockableVO();
				vo.setDs(ds);
				vo.setLockable(lockable);
				vo.setLockType(LockableVO.EXCLUSIVE);
				vo.setUserID(userid);
				vo.setCount(1);
				list.add(vo);

			}

		}

	}

	static class AllShareExtractor extends Extractor {

		private Map<String, LockableVO> voMap;

		public AllShareExtractor(String ds) {
			super(ds);
			voMap = new HashMap<String, LockableVO>();
		}

		@Override
		protected void extractLockVO(List<LockableVO> list, Map dsLockMap,
				String lockable) {
			ShareLockData sld = (ShareLockData) dsLockMap.get(lockable);

			for (Object user : sld.userSet) {
				String key = user + "$$" + lockable;
				LockableVO vo = voMap.get(key);
				if (vo == null) {
					vo = new LockableVO();
					vo.setDs(ds);
					vo.setLockable(lockable);
					vo.setLockType(LockableVO.SHARE);
					vo.setUserID((String) user);
					list.add(vo);
					voMap.put(key, vo);
				}
				vo.setCount(vo.getCount() + 1);
			}
		}
	}

	static class AllExclusiveExtractor extends Extractor {

		public AllExclusiveExtractor(String ds) {
			super(ds);
		}

		@Override
		protected void extractLockVO(List<LockableVO> list, Map dsLockMap,
				String lockable) {
			LockableVO vo = new LockableVO();
			vo.setDs(ds);
			vo.setLockable(lockable);
			vo.setLockType(LockableVO.EXCLUSIVE);
			vo.setUserID((String) dsLockMap.get(lockable));
			vo.setCount(1);
			list.add(vo);

		}

	}

}
