package girard.sc.test;

import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

public class SaveTestReq extends WLQuery
    {
    Vector m_simActor;
    String m_simActorName;

    public SaveTestReq (String sn, Vector simActor, WLServerConnection wlsc, WLMessage wlm)
        {
        super("testDB", wlsc, wlm);
        m_simActor = simActor;
        m_simActorName = sn;
        }

    public ResultSet runQuery()
        {
        return null;
        }

    public boolean runUpdate() 
        {
        
            try 
                {
                synchronized (m_DBA)
            {
                createConnection();

                // get a Statement object from the Connection
                //
                Statement stmt = m_DB.createStatement();

             // Save copy into Test_T

                ResultSet rs = stmt.executeQuery("SELECT Name_VC FROM Test_T WHERE ID_INT = "+m_userID+" AND Name_VC = '"+m_simActorName+"'");

                if (rs.next())
                    {
                    stmt.executeUpdate("DELETE FROM Test_T WHERE ID_INT = "+m_userID+" AND Name_VC =  '"+m_simActorName+"'");
                    }

                CallableStatement cs = m_DB.prepareCall("{call up_insert_JTest (?, ?, ?)}");

                cs.setInt(1,m_userID);
                cs.setString(2,m_simActorName);
                Vector v = FMSObjCon.addObjectToStatement(3,m_simActor,cs);

                cs.execute();
                FMSObjCon.cleanUp(v); 
                }
         synchronized (m_DBA)
            {
             // Save copy into Test2_T
                Statement stmt = m_DB.createStatement();

                ResultSet rs = stmt.executeQuery("SELECT Name_VC FROM Test2_T WHERE ID_INT = "+m_userID+" AND Name_VC = '"+m_simActorName+"'");

                if (rs.next())
                    {
                    stmt.executeUpdate("DELETE FROM Test2_T WHERE ID_INT = "+m_userID+" AND Name_VC =  '"+m_simActorName+"'");
                    }

                CallableStatement cs = m_DB.prepareCall("{call up_insert_JTest2 (?, ?, ?)}");

                cs.setInt(1,m_userID);
                cs.setString(2,m_simActorName);
                Vector v = FMSObjCon.addObjectToStatement(3,m_simActor,cs);

                cs.execute();
                FMSObjCon.cleanUp(v); 
                }
            //    closeConnection();

                return true;
                }
            catch( Exception e ) 
                {
                m_wlsc.addToLog(e.getMessage());
                closeConnection();
                return false;
                }
       //     }
        }
    }
