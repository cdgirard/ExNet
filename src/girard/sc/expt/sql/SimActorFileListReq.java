package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;

public class SimActorFileListReq extends WLQuery
    { 
    int m_simType = -1;
    String m_appID = null;

    public SimActorFileListReq (String db, int simType, String appID, WLServerConnection wlsc, WLMessage wlm)
        {
        super(db, wlsc, wlm);

        m_simType = simType;
        m_appID = appID;
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
 
                ResultSet rs = null;

                if (m_appID == null)
                    rs = stmt.executeQuery("SELECT Sim_Name_VC,Sim_Desc_VC FROM Simulants_T WHERE ID_INT = "+m_userID+" AND Sim_Type_ID_INT = "+m_simType+" AND App_ID IS NULL");
                else
                    rs = stmt.executeQuery("SELECT Sim_Name_VC,Sim_Desc_VC FROM Simulants_T WHERE App_ID = '"+m_appID+"' AND Sim_Type_ID_INT = "+m_simType);

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
