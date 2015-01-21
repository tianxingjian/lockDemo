package cn.com.zwz.lock.impl;

import org.apache.log4j.Logger;

import cn.com.zwz.lock.service.ILockManager;

public abstract class AbstractLockManagerForMem implements ILockManager{
	protected Logger  logger = Logger.getLogger(AbstractLockManagerForMem.class);
}
