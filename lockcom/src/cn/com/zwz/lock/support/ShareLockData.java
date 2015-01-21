package cn.com.zwz.lock.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ShareLockData {

	public List<String> userSet = new ArrayList<String>();
	
	List<ShareLockStruct> sls = new ArrayList<ShareLockStruct>();

	public ShareLockData() {
		super();
	}

	public boolean decreaseLock(String user, String machine) {
		if (userSet.contains(user)) {			
			Iterator<ShareLockStruct> itr = sls.iterator();
			while (itr.hasNext()) {
				ShareLockStruct sl = itr.next();
				if (sl.user.equals(user)) {
					userSet.remove(user);
					itr.remove();
					break;
				}
			}
		}
			
		return userSet.size() == 0;
	}

	public void increaseLock(String user, String machine) {
		userSet.add(user);
		sls.add(new ShareLockStruct(user, machine));
	}

	public boolean decreaseLockByUser(String user) {
		Iterator<ShareLockStruct> itr = sls.iterator();
		while (itr.hasNext()) {
			if (user.equals(itr.next().user)) {
				itr.remove();
				userSet.remove(user);
			}
		}
		
		return userSet.size() == 0;
	}
	
	public boolean decreaseLockByMachine(String machine) {
		Iterator<ShareLockStruct> itr = sls.iterator();
		while (itr.hasNext()) {
			ShareLockStruct sl = itr.next();
			if (null != sl.machine && machine.equals(sl.machine)) {
				itr.remove();
				userSet.remove(sl.user);
			}
		}
		
		return userSet.size() == 0;
	}
	
	class ShareLockStruct {
		String user; 
		String machine;
		public ShareLockStruct(String user, String machine) {
			this.user = user;
			this.machine = machine;
		}
	}

}
