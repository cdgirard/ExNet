package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;

public class SavedBaseActionFileListReq extends WLQuery
    { 
    String m_dbTable = "none";
    String m_ag = null;

    public SavedBaseActionFileListReq (String db, String dbTable, String ag, WLServerConnection wlsc, WLMessage wlm)
        {
        super(db, wlsc, wlm);

        m_dbTable = dbTable;
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
 
                ResultSet rs;

                if (m_ag == null)
                    rs = stmt.executeQuery("SELECT ID_INT,Name_VC,Desc_VC,App_ID,App_Name_VC FROM "+m_dbTable+" WHERE ID_INT = "+m_userID+" AND App_ID IS NULL");
                else
                    rs = stmt.executeQuery("SELECT ID_INT,Name_VC,Desc_VC,App_ID,App_Name_VC FROM "+m_dbTable+" WHERE App_ID = '"+m_ag+"'");

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
