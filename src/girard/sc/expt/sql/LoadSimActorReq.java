package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

public class LoadSimActorReq extends WLQuery
    { 
    String m_simActorName;
    int m_simType = -1;
    Hashtable m_ag = new Hashtable();

    public LoadSimActorReq (String db, String sa, int simType, Hashtable ag, WLServerConnection wlsc, WLMessage wlm)
        {
        super(db, wlsc, wlm);
        m_simActorName = sa;
        m_simType = simType;
        m_ag = ag;
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
 
                String appID = null;
                if (m_ag.containsKey("App ID"))
                    appID = (String)m_ag.get("App ID");

                ResultSet rs = null;

                if (appID == null)
                    rs = stmt.executeQuery("SELECT * FROM Simulants_T WHERE ID_INT = "+m_userID+" AND App_ID IS NULL AND Sim_Name_VC = '"+m_simActorName+"' AND Sim_Type_ID_INT = "+m_simType);
                else
                    rs = stmt.executeQuery("SELECT * FROM Simulants_T WHERE App_ID = '"+appID+"' AND Sim_Name_VC = '"+m_simActorName+"' AND Sim_Type_ID_INT = "+m_simType);

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
