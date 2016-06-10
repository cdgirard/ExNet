package girard.sc.wl.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.sql.Statement;

public class WLUserNameReq extends WLQuery
    { 
    int m_user2 = -1;

    public WLUserNameReq (int user, WLServerConnection wlsc, WLMessage wlm)
        {
        super("weblabDB", wlsc, wlm);

        m_user2 = user;
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
 
                ResultSet rs = stmt.executeQuery("SELECT Last_Name_VC,First_Name_VC FROM Users_T WHERE ID_INT = "+m_user2);
   
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
