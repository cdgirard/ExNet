package girard.sc.wl.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Hashtable;

/**
 * Base class for all classes that need to acces the database.
 * <p>
 * Last Modified: 4-30-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
*/

public abstract class WLQuery
    {
/**
 * The name of the database the WLQuery is accessing.
 *
 */
    protected String m_queryDB;
/**
 * The actual connection to the database.
 *
 */
    protected Connection m_DB;
/**
 * The user that the WLQuery is attached to.
 *
 */
    protected int m_userID;
/**
 * The information needed to form a connection with the database.
 *
 */
    protected Hashtable m_DBA;
/**
 * Used for writing out errors or information to the log file.  Also used
 * to get the location information for the database.
 *
 */
    protected WLServerConnection m_wlsc; // For writing out stuff to the log file.

/**
 * The constructor for WLQuery.
 * 
 * @param name The name of the database to use, this should match the name being used
 *             by the WLGeneralServer in its database lookup Hashtable.
 * @param wlsc The WLServerConnection that received the message.
 * @param wlm The message that created the WLQuery class object.
 * @see girard.sc.wl.io.WLServer#m_dbConnections
 */
    public WLQuery (String name, WLServerConnection wlsc, WLMessage wlm)
        {
        m_queryDB = name;
        wlsc.initializeDBQuery(this);
        wlm.initializeDBQuery(this);
        }

/**
 * Closes a connection to the database.  This is not normally called, as keeping
 * the connection open is more efficient.
 *
 */
    public void closeConnection()
        {
        try  { m_DB.close(); }
        catch( Exception e ) 
            {
            m_wlsc.addToLog(e.getMessage());
            }
        }

/**
 * Attempts to form a connection to the database.  The database is determined by 
 * m_queryDB.  Normally the connection is already established, however if it isn't
 * it tries to re-establish a connection to the database in question.
 *
 * @return Returns true if successful, false otherwise.
 * @see girard.sc.wl.sql.WLQuery#m_queryDB
 */
    public boolean createConnection()
        {
        int counter = 0;

        while(counter < 4)
            {
            try 
                { 
                // Make a connection to the SQL Driver.

                if (m_DBA.containsKey("connected"))
                    {
                    m_DB = (Connection)m_DBA.get("connected");

                    if (m_DB.isClosed())
                        {
                        if (m_DBA.containsKey("login_name"))
                            m_DB = DriverManager.getConnection((String)m_DBA.get("database"),(String)m_DBA.get("login_name"),(String)m_DBA.get("password"));
                         else
                            m_DB = DriverManager.getConnection((String)m_DBA.get("database"));

                        m_DBA.put("connected",m_DB);
                        }
                    }
                else
                    {
                    if (m_DBA.containsKey("login_name"))
                        m_DB = DriverManager.getConnection((String)m_DBA.get("database"),(String)m_DBA.get("login_name"),(String)m_DBA.get("password"));
                    else
                        m_DB = DriverManager.getConnection((String)m_DBA.get("database"));

                    m_DBA.put("connected",m_DB);
                    }

                return true;
                }
            catch( Exception e ) 
                {
                m_wlsc.addToLog(e.getMessage());
                counter++;
                }
            }
        return false;
        }
    public String getQueryDB()
        {
        return m_queryDB;
        }

/**
 * Used to run a query on the database.  Any results are returned through
 * a ResultSet. Normally used when you want to load data from the database.
 *
 * @return Returns the ResultSet generated from the database query.
 */
    public abstract ResultSet runQuery();

/**
 * Used to update the database.  If successful returns true, otherwise returns false.
 * Normally used to save data to the database.
 *
 * @return If successful returns true, otherwise returns false.
 */
    public abstract boolean runUpdate();

    public void setDB(Hashtable db)
        {
        m_DBA = db;
        }
    public void setUserID(int value)
        {
        m_userID = value;
        }
    public void setWlsc(WLServerConnection wlsc)
        {
        m_wlsc = wlsc;
        }
    }
