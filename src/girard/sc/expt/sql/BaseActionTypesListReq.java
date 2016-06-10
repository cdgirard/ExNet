package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * The class object used to query the database on what BaseAction classes are
 * available for use.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class BaseActionTypesListReq extends WLQuery
    {

/**
 * The constructor for the class object. It always uses the database exptDB,
 * which is why you don't need to pass in the name of the database.
 *
 * @param wlsc The WLServerConnection processing the message.
 * @param wlm The WLMessage that is being processed.
 */
    public BaseActionTypesListReq (WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        }

/**
 * Makes the request to the database to return all the available BaseActions.  Any
 * error messages are written to a log file for the server.
 *
 * @return The ResultSet containing all of the BaseActions, or null if failed.
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
 
                ResultSet rs = stmt.executeQuery("SELECT * FROM Base_Actions_Type_T");
   
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
