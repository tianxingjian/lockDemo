package cn.com.zwz.lock.support;

import java.sql.SQLException;

public abstract class DbException extends Exception {

    protected int sqlErrorCode = 0;


    protected String SQLState = null;


    public abstract boolean isDataIntegrityViolation();


    public abstract boolean isBadSQLGrammar();

    protected SQLException  realException;

    public DbException(String msg, SQLException e) {
        super(msg,e);
       realException=e;
        sqlErrorCode = e.getErrorCode();
         SQLState=e.getSQLState();
    }


    public DbException(String msg) {
        super(msg);
        sqlErrorCode = -1;
        SQLState = null;
    }


    public int getSQLErrorCode() {
        return (sqlErrorCode);
    }


    public String getSQLState() {
        return (SQLState);
    }

    public SQLException getRealException()
    {
        return realException;
    }
}