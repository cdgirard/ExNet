package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;

public class DeleteExptReq extends WLQuery
    { 
    int m_exptID;

    public DeleteExptReq (int expt, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_exptID = expt;
        }

    public ResultSet runQuery() 
        {
        return null;
        }

    public boolean runUpdate()
        {
        synchronized (m_DBA)
            {
            try 
                {
                createConnection();

                // get a Statement object from the Connection
                //
                Statement stmt = m_DB.createStatement();
 
                stmt.executeUpdate("DELETE FROM Experiments_T WHERE Experiment_ID_INT = "+m_exptID);
                stmt.executeUpdate("DELETE FROM Experiment_Actions_T WHERE Experiment_ID_INT = "+m_exptID);
   
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