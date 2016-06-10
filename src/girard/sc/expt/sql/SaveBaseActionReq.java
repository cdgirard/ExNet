package girard.sc.expt.sql;

import girard.sc.expt.obj.BaseAction;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The class object used to update the database to save a specific BaseAction.
 *
 * @author Dudley Girard
 * @version ExNet III 3.3
 * @since JDK1.1
 */

public class SaveBaseActionReq extends WLQuery
    {
/**
 * The BaseAction object that is to be saved to the database.
 *
 */
    BaseAction m_ba;

    Hashtable m_ag = null;

/**
 * The main constructor function.  The database of where to store the BaseAction
 * is gotten from the BaseAction using getDB().
 *
 * @param ba The BaseAction to save.
 * @param wlsc The WLServerConnection processing the message.
 * @param wlm The WLMessage that is being processed.
 */
    public SaveBaseActionReq (BaseAction ba, Hashtable ag, WLServerConnection wlsc, WLMessage wlm)
        {
        super(ba.getDB(),wlsc, wlm);

        m_ba = ba;
        m_ag = ag;
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
 * Makes the request to the database to save the requested BaseAction.  Any
 * error messages are written to a log file for the server.  Instead of saving
 * the actual object, a set of datavalues describing the object are saved.  This
 * are gotten from the BaseAction object by calling getSettings().
 *
 * @return Returns true if update was successful, false otherwise.
 * @see girard.sc.expt.obj.BaseAction
 */
    public boolean runUpdate() 
        {
        
        try 
            {
            CallableStatement cs = null;
            Vector v = new Vector();
            String agID = null;
            String agName = null;

            synchronized (m_DBA)
                {
                if (!createConnection())
                    return false;

                // get a Statement object from the Connection
                //
                Statement stmt = m_DB.createStatement();
 
                if (m_ag != null)
                    {
                    agID = (String)m_ag.get("App ID");
                    agName = (String)m_ag.get("App Name");
                    }

                ResultSet rs;

                if (agID != null)
                    {
                    rs = stmt.executeQuery("SELECT Name_VC FROM "+m_ba.getDBTable()+" WHERE Name_VC = '"+m_ba.getFileName()+"' AND App_ID = '"+agID+"'");

                    if (rs.next())
                        {
                        stmt.executeUpdate("DELETE FROM "+m_ba.getDBTable()+" WHERE Name_VC =  '"+m_ba.getFileName()+"' AND App_ID = '"+agID+"'");
                        }
                    }
                else
                    {
                    rs = stmt.executeQuery("SELECT Name_VC FROM "+m_ba.getDBTable()+" WHERE ID_INT = "+m_userID+" AND Name_VC = '"+m_ba.getFileName()+"' AND App_ID IS NULL");

                    if (rs.next())
                        {
                        stmt.executeUpdate("DELETE FROM "+m_ba.getDBTable()+" WHERE ID_INT = "+m_userID+" AND Name_VC =  '"+m_ba.getFileName()+"' AND App_ID IS NULL");
                        }
                    }

                m_ba.setUserID(m_userID);
                m_ba.setAppID(agID);
                m_ba.setAppName(agName);

                cs = m_DB.prepareCall(m_ba.getInsertFormat());
                m_ba.formatInsertStatement(cs,v); 

                cs.execute();

                // closeConnection();
                }

            Enumeration enm = v.elements();
            while(enm.hasMoreElements())
                {
                Vector v1 = (Vector)enm.nextElement();
                FMSObjCon.cleanUp(v1);
                }        

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
