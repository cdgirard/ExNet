package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

/**
 * The class object used to update the database to delete a specific BaseAction.
 *
 * <p>
 * <br> Modified: 10-09-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class DeleteBaseActionReq extends WLQuery
    {
/**
 * The filename of the BaseAction object that is to be deleted from the database.
 *
 */
    Hashtable m_fileInfo = new Hashtable();
/**
 * The database table in which the BaseAction file is stored.
 *
 */
    String m_dbTable;

/**
 * The main constructor function.
 *
 * @param fileName The name of the BaseAction to load.
 * @param db The database to delete the BaseAction from, this information is taken from
             the BaseAction type class.
 * @param dbTable The table in the database the BaseAction file is stored, this information
                  is taken from the BaseAction type class.
 * @param wlsc The WLServerConnection processing the message.
 * @param wlm The WLMessage that is being processed.
 * @see girard.sc.expt.BaseAction
 */
    public DeleteBaseActionReq (Hashtable fileInfo, String db, String dbTable, WLServerConnection wlsc, WLMessage wlm)
        {
        super(db, wlsc, wlm);
        m_fileInfo = fileInfo;
        m_dbTable = dbTable;
        }

/**
 * Not intended to be used, returns null only.
 *
 * @return Returns null.
 */
    public ResultSet runQuery() 
        {
        return null;
        }

/**
 * Makes the request to the database to delete the requested BaseAction.  Any
 * error messages are written to a log file for the server.
 *
 * @return Returns true if update was successful, false otherwise.
 * @see girard.sc.expt.obj.BaseAction
 */
    public boolean runUpdate()
        {
        synchronized (m_DBA)
            {
            try 
                {
                if (!createConnection())
                    return false;

                // get a Statement object from the Connection
                //
                Statement stmt = m_DB.createStatement();
 
                String fileName = (String)m_fileInfo.get("FileName");
                String uid = null;
                if (m_fileInfo.containsKey("App ID"))
                    uid = (String)m_fileInfo.get("App ID");

                if (uid != null)
                    stmt.executeUpdate("DELETE FROM "+m_dbTable+" WHERE Name_VC = '"+fileName+"' AND App_ID = '"+uid+"'");
                else
                    stmt.executeUpdate("DELETE FROM "+m_dbTable+" WHERE ID_INT = "+m_userID+" AND Name_VC = '"+fileName+"' AND App_ID IS NULL");

                // closeConnection();

                return true;
                }
            catch( Exception e ) 
                {
                m_wlsc.addToLog(e.getMessage());
                closeConnection();
                return false;
                }
            }
        }
    }