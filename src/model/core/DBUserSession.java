package model.core;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * DBUserSession
 * Соединение с базой данных.
 *
 * @author dmk.dp.ua 2014-03-24
 */
//C:\Users\ianagez\sinta_13.12\src\model\core\DBUserSession.java
public class DBUserSession {

    public static final String DBENGINE = "db.engine";
    public static final String DBENGINE_DERBY = "derby";
    public static final String DBENGINE_DERBY_EMBEDDED = "derbyembedded";
    public static final String CREATE_DB_IF_NOT_EXISTS = "db.create_if_not_exits";
    public static final String DBENGINE_HSQLDB = "hsqldb";
    public static final String DBENGINE_MYSQL = "mysql";
    public static final String DBENGINE_MSSQL = "mssql";
    public static final String DBENGINE_POSTGRESQL = "postgresql";
    public static final String DRIVERNAME = "db.driver";
    public static final String DRIVERLIB = "db.driverlib";
    public static final String DBHOST = "db.Host";
    public static final String DBNAME = "db.Name";
    public static final String DBURL = "db.DBURL";
    public static final String USER = "db.user";
    public static final String PASSWORD = "db.password";
    Properties conprops;
    private static boolean isDBDriverRegistered = false;
    private Connection userCon;
    private boolean InTransaction;

    /**
     * Creates a new instance of DBUserSession
     */
    public DBUserSession(Properties conprops)
            throws SQLException, ClassNotFoundException, MalformedURLException, InstantiationException, IllegalAccessException {
        this.conprops = conprops;
        if (!isDBDriverRegistered) regDBDriver(conprops);
        connect();
        InTransaction = false;
    }

    private void regDBDriver(Properties prop)
            throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        String sDBDriverLib = prop.getProperty(DRIVERLIB);
        String sDBDriverName = prop.getProperty(DRIVERNAME);
        ClassLoader cloader = new URLClassLoader(new URL[]{new File(sDBDriverLib).toURI().toURL()});
        DriverManager.registerDriver((Driver) Class.forName(sDBDriverName, true, cloader).newInstance());
        isDBDriverRegistered = true;
    }

    public static void setDefPropsForDBConnection(String sDBEngine, Properties prop) {
        prop.setProperty(DBENGINE, sDBEngine);
        if (DBENGINE_DERBY.equals(sDBEngine)) {
            //---------- properties for connect to derby database ----------
            prop.setProperty(DRIVERNAME, "org.apache.derby.jdbc.ClientDriver");
            prop.setProperty(DRIVERLIB, new File(new File("./"), "lib/derbyclient.jar").getAbsolutePath());
            prop.setProperty(DBHOST, "localhost:1527");
            prop.setProperty(DBNAME, "MYDB");
            prop.setProperty(USER, "APP");
            prop.setProperty(PASSWORD, "APP");
        } else if (DBENGINE_DERBY_EMBEDDED.equals(sDBEngine)) {
            //---------- properties for connect to derby database embedded ----------
            prop.setProperty(DRIVERNAME, "org.apache.derby.jdbc.EmbeddedDriver");
            prop.setProperty(DRIVERLIB, new File(new File("./"), "lib/db/derby.jar").getAbsolutePath());
            prop.setProperty(USER, "APP");
            prop.setProperty(PASSWORD, "APP");
        } else if (DBENGINE_HSQLDB.equals(sDBEngine)) {
            //---------- properties for connect to hsqldb database ----------
            prop.setProperty(DRIVERNAME, "org.hsqldb.jdbcDriver");
            prop.setProperty(DRIVERLIB, new File(new File("./"), "lib/db/hsqldb.jar").getAbsolutePath());
            prop.setProperty(USER, "sa");
            prop.setProperty(PASSWORD, "");
        } else if (DBENGINE_MYSQL.equals(sDBEngine)) {
            //---------- properties for connect to mysql database ----------
            prop.setProperty(DRIVERNAME, "com.mysql.jdbc.Driver");
            prop.setProperty(DRIVERLIB, new File(new File("./"), "lib/db/mysql-connector-java-5.1.18-bin.jar").getAbsolutePath());
            prop.setProperty(DBHOST, "localhost:3306");
            prop.setProperty(DBNAME, "MYDB");
            prop.setProperty(USER, "user");
            prop.setProperty(PASSWORD, "password");
        } else if (DBENGINE_MSSQL.equals(sDBEngine)) {
            //---------- properties for connect to MS SQL database ----------
            prop.setProperty(DRIVERNAME, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
            prop.setProperty(DRIVERLIB, new File(new File("./"), "lib/db/sqljdbc4-3.0.jar").getAbsolutePath());
            prop.setProperty(DBHOST, "localhost\\SQLEXPRESS");
            prop.setProperty(DBNAME, "MYDB");
            prop.setProperty(USER, "sa");
            prop.setProperty(PASSWORD, "");
        } else if (DBENGINE_POSTGRESQL.equals(sDBEngine)) {
            //---------- properties for connect to postgresql database ----------
            prop.setProperty(DRIVERNAME, "org.postgresql.Driver");
            prop.setProperty(USER, "user");
            prop.setProperty(PASSWORD, "password");
        }
    }

    public static String getDBConnURL(Properties prop, String sDBEngine) {
        if (sDBEngine == null) sDBEngine = prop.getProperty(DBENGINE);
        String sDBHost = prop.getProperty(DBHOST);
        String sDBName = prop.getProperty(DBNAME);
        String sDBURL = "";
        if (DBENGINE_DERBY.equals(sDBEngine)) {
            sDBURL = "jdbc:derby://" + sDBHost + "/" + sDBName;
        } else if (DBENGINE_DERBY_EMBEDDED.equals(sDBEngine)) {
            //---------- properties for connect to derby database embedded ----------
            //String sDBName = new File(new File("./"), "MYDB").getAbsolutePath(); prop.setProperty(DBNAME, sDBName);
            String sDoCreate = "";
            if ("true".equals(prop.get(CREATE_DB_IF_NOT_EXISTS))) sDoCreate = ";create=true";
            File dbfile = new File(sDBName);
            if (!dbfile.exists()) dbfile = new File(new File("."), sDBName);
            sDBURL = "jdbc:derby:" + dbfile.getAbsolutePath() + sDoCreate;
        } else if (DBENGINE_HSQLDB.equals(sDBEngine)) {
            //---------- properties for connect to hsqldb database ----------
            //String sDBName = new File(new File("./"), "MYDB").getAbsolutePath(); prop.setProperty(DBNAME, sDBName);
            File dbfile = new File(sDBName);
            if (!dbfile.exists()) dbfile = new File(new File("./"), sDBName);
            sDBURL = "jdbc:hsqldb:file:" + dbfile.getAbsolutePath() + ";shutdown=true";
        } else if (DBENGINE_MYSQL.equals(sDBEngine)) {
            //---------- properties for connect to mysql database ----------
            sDBURL = "jdbc:mysql://" + sDBHost + "/" + sDBName;
        } else if (DBENGINE_MSSQL.equals(sDBEngine)) {
            //---------- properties for connect to MS SQL database ----------
            sDBURL = "jdbc:sqlserver://" + sDBHost + ";databaseName=" + sDBName;
        } else if (DBENGINE_POSTGRESQL.equals(sDBEngine)) {
            //---------- properties for connect to postgresql database ----------
            sDBURL = "jdbc:postgresql://localhost:5432/database";
        }
        prop.setProperty(DBURL, sDBURL);
        return sDBURL;
    }

    public String getDBEngine() {
        if (conprops==null) return null;
        return conprops.getProperty(DBENGINE);
    }
//    public String getConURL() { return conURL; }
//    public String getConUserName() { return conUserName; }
//    public void setConnectParams(String surl, String suser, String spassword) {
//        conURL = surl;
//        conUserName = suser;
//        conUserPassword = spassword;
//    }
    public String getURL() throws SQLException {
        return getConnection().getMetaData().getURL();
    }

    public void connect() throws SQLException {
        // rollback and close all
        close();
        // new connection
        String sDBURL = getDBConnURL(conprops, null);
        Properties dbNewConProperties = new Properties();
        dbNewConProperties.put("user", conprops.get(USER));
        dbNewConProperties.put("password", conprops.get(PASSWORD));
        String sDBEngine = (String) conprops.get(DBENGINE);
        if (DBENGINE_MYSQL.equals(sDBEngine)) {
            dbNewConProperties.put("useUnicode", "true");
            dbNewConProperties.put("characterEncoding", "UTF8");
        }
        userCon = DriverManager.getConnection(sDBURL, dbNewConProperties);
        userCon.setAutoCommit(true);
        InTransaction = false;
    }

    public Connection getConnection() throws SQLException {
        if (!InTransaction) {
            ensureConnection();
        }
        return userCon;
    }

    public boolean connected() {
        return userCon != null;
    }

    public boolean isTransaction() {
        return InTransaction;
    }

    private void ensureConnection() throws SQLException {
        // только при isTransaction == false
        boolean bclosed;
        try {
            bclosed = userCon == null || userCon.isClosed();
        } catch (SQLException e) {
            bclosed = true;
        }
        // reconnect if closed
        if (bclosed) {
            connect();
        }
    }

    public void close() throws SQLException {
        if (userCon == null) return;
        if (InTransaction) {
            InTransaction = false;
            userCon.rollback();
            userCon.setAutoCommit(true);
        }
        userCon.close();
        userCon = null;
    }

    public void closeForced() {
        try {
            close();
        } catch (SQLException e) { //
        } finally {
            userCon = null;
        }
    }

    public void begin() throws SQLException {
        if (InTransaction) {
            throw new SQLException("Already in transaction!");
        } else {
            ensureConnection();
            userCon.setAutoCommit(false);
            InTransaction = true;
        }
    }

    public void commit() throws SQLException {
        if (InTransaction) {
            InTransaction = false; //
            userCon.commit();
            userCon.setAutoCommit(true);
        } else {
            throw new SQLException("Transaction not started!");
        }
    }

    public void rollback() throws SQLException {
        if (InTransaction) {
            InTransaction = false; //
            userCon.rollback();
            userCon.setAutoCommit(true);
        } else {
            throw new SQLException("Transaction not started!");
        }
    }


    public void beginTransaction() throws DataModelException {
        if(isTransaction()) try {
            rollback();
        } catch (SQLException e) {
            throw new DataModelException("Cannot rollback prev transaction! Reason:"+e.getLocalizedMessage());
        }
        try {
            begin();
        } catch (SQLException e) {
            throw new DataModelException("Cannot begin transaction! Reason:"+e.getLocalizedMessage());
        }
    }
    public void endTransaction(boolean commit) throws DataModelException {
        if(commit){
            try {
                commit();
                return;
            } catch (SQLException e) {
                throw new DataModelException("Cannot commit transaction! Reason:"+e.getLocalizedMessage());
            }
        }
        try {
            rollback();
            return;
        } catch (SQLException e) {
            throw new DataModelException("Cannot rollback transaction! Reason:"+e.getLocalizedMessage());
        }
    }

    public final int execSQLQuery(String sSQLQuery) throws SQLException {
        Statement st = userCon.createStatement();
        int res=st.executeUpdate(sSQLQuery);
        st.close();
        return res;
    }
    public final int execSQLQuery(String sqlQuery, Object[] sqlQueryParameters) throws SQLException {
        if (sqlQueryParameters==null || sqlQueryParameters.length==0) {
            return execSQLQuery(sqlQuery);
        }
        PreparedStatement preparedStatement = userCon.prepareStatement(sqlQuery);
        for(int i=0;i<sqlQueryParameters.length;i++){
            Object sParamVal = sqlQueryParameters[i];
            if (sParamVal==null){
                preparedStatement.setNull(i+1, Types.VARCHAR);
            } else if(sParamVal.getClass().getName().equals("java.lang.String") ) {
                preparedStatement.setString(i+1, sParamVal.toString());
            } else {
                preparedStatement.setObject(i+1, sParamVal);
            }
        }
        int res=preparedStatement.executeUpdate();
        preparedStatement.close();
        return res;
    }
    public final int execSQLQuery(Object[] sqlQueryWithParameters) throws SQLException {
        if (sqlQueryWithParameters.length==1)
            return execSQLQuery(sqlQueryWithParameters[0].toString());
        PreparedStatement preparedStatement = userCon.prepareStatement(sqlQueryWithParameters[0].toString());
        for(int i=1;i<sqlQueryWithParameters.length;i++){
            Object sParamVal = sqlQueryWithParameters[i];
            if (sParamVal==null){
                preparedStatement.setNull(i, Types.VARCHAR);
            } else if(sParamVal.getClass().getName().equals("java.lang.String") ) {
                preparedStatement.setString(i, sParamVal.toString());
            } else {
                preparedStatement.setObject(i, sParamVal);
            }
        }
        int res=preparedStatement.executeUpdate();
        preparedStatement.close();
        return res;
    }
    public final int execSQLQueryFromResource(String sSQLResourceName) throws SQLException, IOException {
        String sSQLQuery = Utils.toStringUTF8(sSQLResourceName);
        return execSQLQuery(sSQLQuery);
    }

    public final ArrayList<HashMap<String,Object>> getDataListFromSQLQuery(String sqlSelectQuery, Object[] sqlSelectQueryParameters,
                                                                              HashMap<String,String> replacingDataItemLabels) throws SQLException {
        ResultSet rs = null; ArrayList<HashMap<String,Object>> result = null;
        if(sqlSelectQueryParameters==null || sqlSelectQueryParameters.length==0){
            Statement st = userCon.createStatement();
            rs = st.executeQuery(sqlSelectQuery);
            result= getDataListFromRS(rs,replacingDataItemLabels);
            st.close();
            return result;
        } else {
            PreparedStatement preparedStatement = userCon.prepareStatement(sqlSelectQuery);
            for(int j=0;j<sqlSelectQueryParameters.length;j++){
                Object paramValue=sqlSelectQueryParameters[j];
                if (paramValue==null){preparedStatement.setNull(j+1, Types.VARCHAR);}else{preparedStatement.setObject(j+1, paramValue);}
            }
            rs = preparedStatement.executeQuery();
            result= getDataListFromRS(rs,replacingDataItemLabels);
            preparedStatement.close();
        }
        return result;
    }
    public final ArrayList<HashMap<String,Object>> getDataListFromSQLQuery(String sqlSelectQuery, Object[] sqlSelectQueryParameters) throws SQLException {
        return getDataListFromSQLQuery(sqlSelectQuery, sqlSelectQueryParameters, null);
    }
    public final HashMap<String,HashMap> getDataMapFromSQLQuery(String sqlSelectQuery, Object[] sqlSelectQueryParameters,
                                                                          String sKeyFieldNameForResultMap) throws SQLException {
        Statement st = userCon.createStatement();
        ResultSet rs = null; HashMap<String,HashMap> result = null;
        if(sqlSelectQueryParameters==null || sqlSelectQueryParameters.length==0){
            rs = st.executeQuery(sqlSelectQuery);
            result= getDataMapFromRS(rs, sKeyFieldNameForResultMap);
            st.close();
        } else {
            PreparedStatement preparedStatement = userCon.prepareStatement(sqlSelectQuery);
            for(int j=0;j<sqlSelectQueryParameters.length;j++){
                Object paramValue=sqlSelectQueryParameters[j];
                if (paramValue==null){preparedStatement.setNull(j+1, Types.VARCHAR);}else{preparedStatement.setObject(j+1, paramValue);}
            }
            rs = preparedStatement.executeQuery();
            result= getDataMapFromRS(rs, sKeyFieldNameForResultMap);
            preparedStatement.close();
        }
        return result;
    }

    private static final ArrayList<HashMap<String,Object>> getDataListFromRS(ResultSet rs, HashMap<String,String> replacingDataItemLabels) throws SQLException {
        ArrayList<HashMap<String,Object>> result = new ArrayList<>(); HashMap item;
        while (rs.next()) {
            result.add(item = new HashMap());
            ResultSetMetaData rsMetaData = rs.getMetaData();
            for(int ic=1;ic<=rsMetaData.getColumnCount();ic++) {
                String colname = rsMetaData.getColumnName(ic);
                if(replacingDataItemLabels!=null&&replacingDataItemLabels.containsKey(colname)) colname= replacingDataItemLabels.get(colname);
                try {//!!!throw if column with number ic exists in rsMetaData and NOT EXISTS in rs!!!
                    if(rsMetaData.getColumnType(ic)== Types.BIGINT) item.put(colname, rs.getString(ic));
                    else item.put(colname, rs.getObject(ic));
                } catch (Throwable t){}
            }
        }
        return result;
    }
    private static final HashMap<String,HashMap> getDataMapFromRS(ResultSet rs, String sIdentifierName) throws SQLException {
        HashMap<String,HashMap> result = new HashMap<>(); HashMap item;
        while (rs.next()) {
            String sIdentifierValue= rs.getString(sIdentifierName);
            result.put(sIdentifierValue, item = new HashMap());
            ResultSetMetaData rsMetaData = rs.getMetaData();
            for(int ic=1;ic<=rsMetaData.getColumnCount();ic++) {
                String colname= rsMetaData.getColumnName(ic);
                try {//!!!throw if column with number ic exists in rsMetaData and NOT EXISTS in rs!!!
                    if(rsMetaData.getColumnType(ic)== Types.BIGINT) item.put(colname, rs.getString(ic));
                    else item.put(colname, rs.getObject(ic));
                } catch (Throwable t){}
            }
        }
        return result;
    }
}
