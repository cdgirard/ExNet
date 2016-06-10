package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;

public class ExptUserInfoReq extends WLQuery
    { 

    public ExptUserInfoReq (WLServerConnection wlsc, WLMessage wlm)
        {
        super("weblabDB", wlsc, wlm);
        }

    public ResultSet runQuery() 
        {
        synchronized (m_DBA)
            {
            try 
                {
                createConnection();

                // get a Statement object from the Connection
                //
                Statement stmt = m_DB.createStatement();
 
                ResultSet rs = stmt.executeQuery("SELECT Last_Name_VC,First_Name_VC,Mi_VC FROM Users_T WHERE ID_INT = "+m_userID);
   
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

    public boolean runUpdate()
        {
        return true;
        }
    }
