package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

/**
 * The class object used to query the database to load a specific BaseAction.
 * <p>
 * <br> Modified: 10-08-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class LoadBaseActionReq extends WLQuery
    {
/**
 * The name of the BaseAction file to load.
 *
 */
    Hashtable m_fileInfo;
/**
 * The Database table that the file is located in.
 *
 */
    String m_dbTable;

/**
 * The main constructor function.
 *
 * @param fileName The name of the BaseAction to load.
 * @param db The database to load the BaseAction from, this information is taken from
             the BaseAction type class.
 * @param dbTable The table in the database the BaseAction file is stored, this information
                  is taken from the BaseAction type class.
 * @param wlsc The WLServerConnection processing the message.
 * @param wlm The WLMessage that is being processed.
 * @see girard.sc.expt.BaseAction
 */
    public LoadBaseActionReq (Hashtable fileInfo, String db, String dbTable, WLServerConnection wlsc, WLMessage wlm)
        {
        super(db, wlsc, wlm);
        m_fileInfo = fileInfo;
        m_dbTable = dbTable;
        }

/**
 * Makes the request to the database to return the requested BaseAction.  Any
 * error messages are written to a log file for the server.
 *
 * @return The ResultSet containing the data for the BaseAction, or null if failed.
 */
    public ResultSet runQuery() 
        {
        synchronized (m_DBA)
            {
            try 
                {
                if (!createConnection())
                    return null;

                // get a Statement object from the Connection
                //
                Statement stmt = m_DB.createStatement();
 
                String fileName = (String)m_fileInfo.get("FileName");
                String uid = null;
                if (m_fileInfo.containsKey("App ID"))
                    uid = (String)m_fileInfo.get("App ID");


                ResultSet rs;
                if (uid != null)
                    rs = stmt.executeQuery("SELECT * FROM "+m_dbTable+" WHERE Name_VC = '"+fileName+"' AND App_ID = '"+uid+"'");
                else
                    {
                    int altUser = ((Integer)m_fileInfo.get("UserID")).intValue();
                    if (altUser == 0)
                        rs = stmt.executeQuery("SELECT * FROM "+m_dbTable+" WHERE ID_INT = "+m_userID+" AND Name_VC = '"+fileName+"' AND App_ID IS NULL");
                    else
                        rs = stmt.executeQuery("SELECT * FROM "+m_dbTable+" WHERE ID_INT = "+altUser+" AND Name_VC = '"+fileName+"' AND App_ID IS NULL");
                    }
                // closeConnection();

                return rs;
                }
            catch( Exception e ) 
                {
                m_wlsc.addToLog(e.getMessage());
                closeConnection();
                return null;
                }
            }
        }

/**
 * Doesn't do anything but return true.
 *
 * @return Always returns true.
 */
    public boolean runUpdate()
        {
        return true;
        }
    }
