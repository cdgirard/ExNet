package girard.sc.expt.sql;

import girard.sc.expt.obj.SimActor;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

public class SaveSimActorReq extends WLQuery
    {
    SimActor m_simActor;

    public SaveSimActorReq (SimActor simActor, WLServerConnection wlsc, WLMessage wlm)
        {
        super(simActor.getDB(), wlsc, wlm);
        m_simActor = simActor;
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

                if (m_simActor.getAppID() != null)
                    {
                    ResultSet rs = stmt.executeQuery("SELECT Sim_Actor_ID_INT FROM Simulants_T WHERE App_ID = '"+m_simActor.getAppID()+"' AND Sim_Type_ID_INT = "+m_simActor.getActorTypeID()+" AND Sim_Name_VC = '"+m_simActor.getActorName()+"'");

                    if (rs.next())
                        {
                        stmt.executeUpdate("DELETE FROM Simulants_T WHERE Sim_Actor_ID_INT = "+rs.getInt("Sim_Actor_ID_INT"));
                        }
                    }
                else
                    {
                    ResultSet rs = stmt.executeQuery("SELECT Sim_Actor_ID_INT FROM Simulants_T WHERE ID_INT = "+m_userID+" AND Sim_Type_ID_INT = "+m_simActor.getActorTypeID()+" AND Sim_Name_VC = '"+m_simActor.getActorName()+"' AND App_ID IS NULL");

                    if (rs.next())
                        {
                        stmt.executeUpdate("DELETE FROM Simulants_T WHERE Sim_Actor_ID_INT = "+rs.getInt("Sim_Actor_ID_INT"));
                        }
                    }

                m_simActor.setUserID(m_userID);

                CallableStatement cs = m_DB.prepareCall(m_simActor.getInsertFormat());
                Vector v = new Vector();
                m_simActor.formatInsertStatement(cs,v); 

                cs.execute();

                m_simActor.updateObject(cs);
  
                Enumeration enm = v.elements();
                while(enm.hasMoreElements())
                    {
                    Vector v1 = (Vector)enm.nextElement();
                    FMSObjCon.cleanUp(v1);
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
