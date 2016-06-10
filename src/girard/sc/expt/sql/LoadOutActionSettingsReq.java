package girard.sc.expt.sql;

import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Used to run the actual database query for getting the settings of an ExperimentAction
 * data file from the database.
 * <p>
 * <br> Started: 10-25-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class LoadOutActionSettingsReq extends WLQuery
    { 
    BaseDataInfo m_bdi = null;

    public LoadOutActionSettingsReq(BaseDataInfo bdi, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_bdi = bdi;
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

                m_bdi.setUserID(m_userID);
 
                ResultSet rs = stmt.executeQuery("SELECT * FROM Expt_Action_Reference_T WHERE Expt_Out_ID_INT = "+m_bdi.getExptOutID()+" AND Action_Index_INT = "+m_bdi.getActionIndex());

                // closeConnection();

                return rs;
                }
            catch(SQLException sqle) 
                { 
                m_wlsc.addToLog(""+sqle); 
                closeConnection();
                }
            }
        return null;
        }

    public boolean runUpdate()
        {
        return true;
        }
    }
