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
 * 	�����ݿ�ʵ�ֲַ�ʽ��Ⱥ�Ĺ�������������
 *  ��ǰ�汾û�и�������ܼ��ɣ����������Spring�ȿ����ȡ�������Ӻ���������ɸ��ݾ��������д���ݲ��������Ӵ����������
 * <p>
 * @author tianxingjian
 *
 */
public class LockManagerForDB extends AbstractLockManagerForDB {

	/**
	 * <p>
	 *  ��Զ�����Դ����ĳЩ������Ӧ�ý��д�������ܸ��ݲ�ͬ����ԴȡJDBCConnection
	 *  ���������������Ż�
	 * <p>
	 */
	JDBCUtilService jdbcConnection;
	/**
	 * ��ȡ������.ͬһ�û�����ͬһ�������᷵��true���������ݱ��ﲢû��ʵ���������ݣ�����3.1��ǰ������)
	 * 
	 * @return �ɹ����� true
	 * @param lockable
	 *            ������ ID��128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
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
	 * �ж���Ӧ���͵����Ƿ��Ѽ���
	 * 
	 * @return �ɹ����� true
	 * @param lockable
	 *            ������ ID��128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
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
	 * ������ȡ��
	 * 
	 * @return �ɹ����� true
	 * @param lockables
	 *            ������ ID ���飬��Ԫ��Ϊ 128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
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
	 * �ͷŵ�����
	 * 
	 * @return void
	 * @param lockable
	 *            ������ ID��128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
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
	 * �����ͷ���
	 * 
	 * @return void
	 * @param lockables
	 *            ������ ID ���飬��Ԫ��Ϊ128 �ֽ� VARCHAR
	 * @param userID
	 *            ��ӵ�����û� ID��20 �ֽ� String
	 * @param dsName
	 *            ���ݿ�����
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
	 * �������������
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
	 * �Ƿ����������
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
	 * ɾ����������׺
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
	 * ���ݺ�׺�ж��Ƿ�����
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
