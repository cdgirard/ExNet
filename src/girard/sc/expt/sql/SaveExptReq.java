package girard.sc.expt.sql;

import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Used to save an Experiment to the database.  Firsts removes any old Experiment by the
 * same name and accessibility.
 * <p>
 * <br> Started: 2001
 * <br> Modified: 10-23-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class SaveExptReq extends WLQuery
    {
/**
 * The Experiment to be saved.
 */
    Experiment m_experiment;

    public SaveExptReq (Experiment experiment, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_experiment = experiment;
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

                ResultSet rs;
                System.err.println("murali:I'm in saveexptreq:1");
                
                if (m_experiment.getAppID() == null)
                    {
                    rs = stmt.executeQuery("SELECT Experiment_ID_INT FROM Experiments_T WHERE ID_INT = "+m_userID+" AND App_ID IS NULL AND Experiment_Name_VC = '"+m_experiment.getExptName()+"'");

                    if (rs.next())
                        {
                        int exptID = rs.getInt("Experiment_ID_INT");
                        stmt.executeUpdate("DELETE FROM Experiments_T WHERE Experiment_ID_INT = "+exptID);
                        stmt.executeUpdate("DELETE FROM Experiment_Actions_T WHERE Experiment_ID_INT = "+exptID);
                        }
                    }
                else
                    {
                    rs = stmt.executeQuery("SELECT Experiment_ID_INT FROM Experiments_T WHERE App_ID = '"+m_experiment.getAppID()+"' AND Experiment_Name_VC = '"+m_experiment.getExptName()+"'");

                    if (rs.next())
                        {
                        int exptID = rs.getInt("Experiment_ID_INT");
                        stmt.executeUpdate("DELETE FROM Experiments_T WHERE Experiment_ID_INT = "+exptID);
                        stmt.executeUpdate("DELETE FROM Experiment_Actions_T WHERE Experiment_ID_INT = "+exptID);
                        }
                    }

                m_experiment.setUserID(m_userID);

                CallableStatement cs = m_DB.prepareCall(m_experiment.getInsertFormat());
                Vector ed = m_experiment.formatInsertStatement(cs);

                cs.execute();
 
                FMSObjCon.cleanUp(ed);
                System.err.println("murali:I'm in saveexptreq:2");
                m_experiment.updateObject(cs);

                Enumeration enm = m_experiment.getActions().elements();
                int counter = 0;
                while (enm.hasMoreElements())
                    {
                    ExperimentAction obj = (ExperimentAction)enm.nextElement();
                    PreparedStatement ps = m_DB.prepareStatement("INSERT INTO Experiment_Actions_T (ID_INT, Experiment_ID_INT, Action_Index_INT, Action_OBJ) VALUES (?, ?, ?, ?)");
                    ps.setInt(1,m_userID);
                    ps.setInt(2,m_experiment.getExptID());
                    ps.setInt(3,counter);
                    Vector v = FMSObjCon.addObjectToStatement(4,obj.getSettings(),ps);
                    ps.executeUpdate();
                    FMSObjCon.cleanUp(v);
                    counter++;
                    }

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
