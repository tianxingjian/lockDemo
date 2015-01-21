package cn.com.zwz.lock.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.zwz.lock.model.LockableVO;
import cn.com.zwz.lock.service.ILockBS;
import cn.com.zwz.lock.service.JDBCUtilService;
import cn.com.zwz.lock.support.LockableConvert;

/**
 * <p>
 * 	用数据库实现分布式或集群的共享锁和排他锁
 *  当前版本没有跟其他框架集成，如果集成了Spring等框架在取数据连接和事务处理方面可根据具体情况重写数据操作和连接处理，事务管理
 * <p>
 * @author tianxingjian
 *
 */
public class LockManagerForDB extends AbstractLockManagerForDB {

	/**
	 * <p>
	 *  针对多数据源的在某些场景下应该进行处理，最好能根据不同数据源取JDBCConnection
	 *  后期碰到场景再优化
	 * <p>
	 */
	JDBCUtilService jdbcConnection;
	/**
	 * 获取单个锁.同一用户申请同一个锁，会返回true，但是数据表里并没有实际增添数据（参照3.1以前的做法)
	 * 
	 * @return 成功返回 true
	 * @param lockable
	 *            锁对象 ID，128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public boolean acquireLock_RequiresNew(final String lockable,
			final String userID, final String dsName) {
		boolean rv = true;
		String sql = INSERT_SQL;
		Connection con = jdbcConnection.getJDBCConnection(dsName);
		PreparedStatement prestate = null;
	
		try {
			prestate = con.prepareStatement(sql);
			prestate.setString(1, cutShareSign(lockable));
			prestate.setString(2, userID);
			prestate.setInt(3, isShareLock(lockable) ? 1 : 0);
			prestate.execute();
			
		} catch (Exception ex) {
			rv = false;
			logger.info("error occurred  while acquiring the same lock.");

		} finally {
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}
		return rv;
	}

	/**
	 * 判断相应类型的锁是否已加上
	 * 
	 * @return 成功返回 true
	 * @param lockable
	 *            锁对象 ID，128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public boolean isLocked_RequiresNew(String lockable,
			final String userID, final String dsName) {
		boolean rv = true;
		String sql = CHECK_SQL;
		int type = 0;
		if(isShareLock(lockable))
		{
			type = 1;
			lockable = cutShareSign(lockable);
		}
		Connection con = jdbcConnection.getJDBCConnection(dsName);
		PreparedStatement prestate = null;
		ResultSet rs = null;

		try {
			prestate = con.prepareStatement(sql);
			prestate.setString(1, cutShareSign(lockable));
			prestate.setString(2, userID);
			prestate.setInt(3, isShareLock(lockable) ? 1 : 0);
			rs = prestate.executeQuery();
			
			boolean result = rs.next();
			rv = result ? false : true;
		} catch (Exception ex) {
			rv = false;
			logger.error("Cannot find if it is locked: " + lockable
					+ ", for the sql operation exception", ex);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("rs close fail");
				}
			}
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
			
		}
		return rv;
	}

	/**
	 * 批量获取锁
	 * 
	 * @return 成功返回 true
	 * @param lockables
	 *            锁对象 ID 数组，其元素为 128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public boolean acquireBatchLock_RequiresNew(final String[] lockables,
			final String userID, final String dsName) {
		if (lockables.length > MAX_BATCH) {
			logger.error("LockManager.acquireBatchLock TOO MANY batch!!.");
		}
		String sql = INSERT_SQL;
		
		boolean rv = true;
		Connection con = jdbcConnection.getJDBCConnection(dsName);
		PreparedStatement prestate = null;

		try {
			prestate = con.prepareStatement(sql);
			
			for(String lockable : lockables){
				prestate.setString(1, cutShareSign(lockable));
				prestate.setString(2, userID);
				prestate.setInt(3, isShareLock(lockable) ? 1 : 0);
				prestate.addBatch();
			}
			
			prestate.executeBatch();
			
		} catch (Exception ex) {
			rv = false;
			logger.error("Error acquiring batch locks, user '" + userID + "'.", ex);
		} finally {
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
			
		}
	
		return rv;
	}

	/**
	 * 释放单个锁
	 * 
	 * @return void
	 * @param lockable
	 *            锁对象 ID，128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public void releaseLock_RequiresNew(final String lockable,
			final String userID, final String dsName) {
		
		String sql = DELETE_SINGLE_SQL;
		
		Connection con = jdbcConnection.getJDBCConnection(dsName);
		PreparedStatement prestate = null;
		try {
			prestate = con.prepareStatement(sql);
			prestate.setString(1, cutShareSign(lockable));
			prestate.setString(2, userID);
			prestate.executeUpdate();
		} catch (Exception ex) {
			logger.error(
					"Cannot release id '" + lockable + "', unknown error.", ex);
		} finally {
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}
	}

	/**
	 * 批量释放锁
	 * 
	 * @return void
	 * @param lockables
	 *            锁对象 ID 数组，其元素为128 字节 VARCHAR
	 * @param userID
	 *            锁拥有者用户 ID，20 字节 String
	 * @param dsName
	 *            数据库名称
	 */
	public void releaseBatchLock_RequiresNew(final String[] lockables,
			final String userID, final String dsName) {
		// Check params
		if (lockables.length > MAX_BATCH)
			logger.warn("NCLockManager.releaseBatchLock TOO MANY batch!!.");

		String sql = DELETE_SINGLE_SQL;
		Connection con = jdbcConnection.getJDBCConnection(dsName);
		PreparedStatement prestate = null;
		try {
			prestate = con.prepareStatement(sql);
			for (int i = 0; i < lockables.length; ++i) {
				prestate.setString(1, cutShareSign(lockables[i]));
				prestate.setString(2, userID);
				prestate.addBatch();
			}
			prestate.executeBatch();
		} catch (Exception batchEx) {
			logger.error("Unexpected error releasing batch locks.");
		} finally {
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}
	}


	public void releaseUserLocks_RequiresNew(String userID, String dsName) {

		String sql = DELETE_BY_USER_SQL;
		Connection con = jdbcConnection.getJDBCConnection(dsName);
		PreparedStatement prestate = null;
		try {
			prestate = con.prepareStatement(sql);
			prestate.setString(1, userID);
			prestate.executeUpdate();
		} catch (Exception ex) {
			logger.error("Cannot release all lock by user '" + userID
					+ ", unknown error.", ex);
		} finally {
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}

	}

	/**
	 * 虚拟机启动解锁
	 * 
	 * @return void
	 */
	public void onJvmStart_RequiresNew() {
		Connection con = jdbcConnection.getJDBCConnection();
		PreparedStatement prestate = null;
		try {
			
			prestate = con.prepareStatement(DELETE_BY_JVM_SQL);
			prestate.executeUpdate();
		
		} catch (Exception ex) {
			logger.error("Cannot clear jvm '" + JVM + "', unknown error.", ex);
		} finally {
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}
	}

	public LockableVO[] getAllLockVOs_RequiresNew(String dsName) {
		LockableVO[] lockableVOs = new LockableVO[0];
		String sql = "select Lockable,UserID,JVM,LockType from "
				+ GROUP_TABLE_NAME;
		Connection con = jdbcConnection.getJDBCConnection();
		PreparedStatement prestate = null;
		ResultSet rs = null;
		try {
			prestate = con.prepareStatement(sql);
			rs = prestate.executeQuery();
			lockableVOs = new LockableConvert().covertRsToObjectArray(rs);
		} catch (Exception ex) {
			logger.error("Cannot get all lockvos from '" + dsName
					+ ", unknown error.", ex);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("rs close fail");
				}
			}
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}
		return lockableVOs;
	}

	public LockableVO[] getUserLockVOs_RequiresNew(String userID, String dsName) {
		LockableVO[] lockableVOs = new LockableVO[0];
		String sql = "select Lockable,UserID,JVM from " + GROUP_TABLE_NAME
				+ " where rtrim(UserID) = ? ";
		Connection con = jdbcConnection.getJDBCConnection();
		PreparedStatement prestate = null;
		ResultSet rs = null;
		try {
			prestate = con.prepareStatement(sql);
			prestate.setString(1, userID);
			rs = prestate.executeQuery();
			lockableVOs = new LockableConvert().covertRsToObjectArray(rs);
		} catch (Exception ex) {
			logger.error("Cannot get all lockvos from '" + dsName
					+ ", unknown error.", ex);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("rs close fail");
				}
			}
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}
		return lockableVOs;
	}


	/**
	 * 是否可申请锁。
	 */
	public boolean canLockPK_RequiresNew(String lockable, String dsName,
			Set lockSet) {
		if (isShareLock(lockable)) {
			return canLockSharePK_RequiresNew(lockable, dsName, lockSet);
		} 
		else
			return canLockExcludePK_RequiresNew(lockable, dsName);
	}
	
	private boolean canLockSharePK_RequiresNew(String lockable, String dsName,
			Set lockSet) 
	{
		lockable = lockable.substring(0, lockable.length()
				- ILockBS.STR_SHARED_LOCK.length());
		String sql = CHECK_SQL_EXCLUDE;
		boolean rt = true;
		Connection con = jdbcConnection.getJDBCConnection();
		PreparedStatement prestate = null;
		ResultSet rs = null;
		try {
			prestate = con.prepareStatement(sql);
			prestate.setString(1, lockable);
			rs = prestate.executeQuery();
			LockableVO[] lockableVOs = new LockableConvert().covertRsToObjectArray(rs);
			if (lockableVOs.length == 0)
				rt = true;
			else {
				for(int i = 0; i < lockableVOs.length; i++){
					if(LockableVO.EXCLUSIVE == lockableVOs[i].getLockType()){
						rt = false;
						break;
					}
					lockSet.add(lockableVOs[i].getUserID());
				}
			}
		} catch (Exception ex) {
			rt = false;
			logger.error("Cannot find if it is locked: " + lockable
					+ ", for the sql operation exception", ex);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("rs close fail");
				}
			}
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}
		return rt;
	}
	
	private boolean canLockExcludePK_RequiresNew(String lockable, String dsName) 
	{
		String sql = CHECK_SQL_ALL;
		boolean rt = true;
		Connection con = jdbcConnection.getJDBCConnection();
		PreparedStatement prestate = null;
		ResultSet rs = null;
		try {
			prestate = con.prepareStatement(sql);
			prestate.setString(1, lockable);
			rs = prestate.executeQuery();
			
			boolean result = rs.next();
			rt = result ? true:false;
		} catch (Exception ex) {
			rt = false;
			logger.error("Cannot find if it is locked: " + lockable
					+ ", for the sql operation exception", ex);
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("rs close fail");
				}
			}
			if(prestate != null){
				try {
					prestate.close();
				} catch (SQLException e) {
					logger.error("statement close fail");
				}
			}
			if(con != null){
				try {
					con.close();
				} catch (SQLException e) {
					logger.error("conn close fail");
				}
			}
		}
		return rt;
	}
	/**
	 * 删除共享锁后缀
	 * 
	 * @param lockable
	 * @return
	 */
	private String cutShareSign(String lockable) {
		if (lockable.endsWith(ILockBS.STR_SHARED_LOCK))
			return lockable.substring(0, lockable.length()
					- ILockBS.STR_SHARED_LOCK.length());
		return lockable;
	}

	/**
	 * 
	 * 根据后缀判断是否共享锁
	 * 
	 * @param lockable
	 * @return
	 */
	private boolean isShareLock(String lockable) {
		if (lockable.endsWith(ILockBS.STR_SHARED_LOCK))
			return true;
		return false;
	}
	
	public List<Map> getFailInfo() {
		return null;
	}

	public void releasePKByUserForce(String userid) {
	}
	
	public void releasePKByMachine(String machine) {
		
	}
}
