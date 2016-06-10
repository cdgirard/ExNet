package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;

public class GetPayResultsReq extends WLQuery
    {
    int m_exptOutID = -1;

    public GetPayResultsReq (int exptOutID, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_exptOutID = exptOutID;
        }

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
 
                ResultSet rs = stmt.executeQuery("SELECT * FROM Expt_Pay_T WHERE Expt_Out_ID_INT = "+m_exptOutID);
   
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
