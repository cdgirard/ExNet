package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;

public class SimActorTypeListReq extends WLQuery
    {

    public SimActorTypeListReq (WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
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

                ResultSet rs = stmt.executeQuery("SELECT * FROM Simulants_Type_T");

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
