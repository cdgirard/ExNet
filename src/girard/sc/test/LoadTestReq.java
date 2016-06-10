package girard.sc.test;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Vector;

public class LoadTestReq extends WLQuery
    { 
    String m_simActorName;
    Vector m_time;

    public LoadTestReq (Vector time, String sa, WLServerConnection wlsc, WLMessage wlm)
        {
        super("testDB", wlsc, wlm);
        m_simActorName = sa;
        m_time = time;
        }

    public ResultSet runQuery() 
        {
        ResultSet rs = null;
        Statement stmt = null;
             try 
                {
      synchronized (m_DBA)
            {

                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));
                if (!createConnection())
                    return null;
                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));

                // get a Statement object from the Connection
                //
                stmt = m_DB.createStatement();
 
                stmt.executeQuery("SELECT * FROM Test_T WHERE Name_VC = '"+m_simActorName+"'");

             //   

             //   stmt.executeQuery("SELECT * FROM Test_T WHERE Name_VC = '"+m_simActorName+"'");

             //   stmt.executeQuery("SELECT * FROM Test_T WHERE ID_INT = "+m_userID);

             //   stmt.executeQuery("SELECT * FROM Test_T WHERE ID_INT = "+m_userID+" AND Name_VC = '"+m_simActorName+"'");

              //  closeConnection();
                }

        synchronized (m_DBA)
            {
                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));

                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));
                if (!createConnection())
                    return null;
                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));

                // get a Statement object from the Connection
                //
                stmt = m_DB.createStatement();
 
                stmt.executeQuery("SELECT * FROM Test_T WHERE ID_INT = "+m_userID);

             //   closeConnection();
                }

         synchronized (m_DBA)
            {
                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));

                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));
                if (!createConnection())
                    return null;
                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));

                // get a Statement object from the Connection
                //
                stmt = m_DB.createStatement();
 
                rs = stmt.executeQuery("SELECT * FROM Test_T WHERE ID_INT = "+m_userID+" AND Name_VC = '"+m_simActorName+"'");

             //   closeConnection();
                }
                m_time.addElement(new Long(Calendar.getInstance().getTime().getTime()));

// System.err.println(""+m_time.elementAt(0));
// System.err.println(""+m_time.elementAt(1));

                return rs;
                }
            catch( Exception e ) 
                {
                m_wlsc.addToLog(e.getMessage());
                closeConnection();
                return null;
                }
        //    }
        }

    public boolean runUpdate()
        {
        return true;
        }
    }
