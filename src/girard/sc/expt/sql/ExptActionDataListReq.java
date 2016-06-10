package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Used to run the actual database query for getting a list of saved ExperimentAction
 * data files from the database.  Retrieves files based on the Experiment they are
 * attached to.
 * <p>
 * <br> Started: 10-25-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class ExptActionDataListReq extends WLQuery
    { 
    int m_exptOutID = -1;

    public ExptActionDataListReq (int exptOutID, WLServerConnection wlsc, WLMessage wlm)
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
 
                ResultSet rs;

                rs = stmt.executeQuery("SELECT Action_Index_INT, Action_Object_Index_INT, Action_Name_VC, Date_Run_DATE FROM Expt_Action_Reference_T WHERE Expt_Out_ID_INT = "+m_exptOutID);

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
