package girard.sc.expt.sql;

import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Vector;

/**
 * 
 */

public class NewExptOutputReq extends WLQuery
    {
    Object[] m_data;

    public NewExptOutputReq (Object[] data, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_data = data;
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
                if (!createConnection())
                    return false;

		System.err.println(" in the getGeneralResponse Method of NewExptOutputReq"); 
               // Expt Name, Expt_Desc_VC, User ID, App_ID, App_Name_VC, Extra_Data_OBJ, Expt ID (output)
                CallableStatement cs = m_DB.prepareCall("{call up_insert_JExperimentOutput (?, ?, ?, ?, ?, ?, ?)}");

                cs.setString(1,(String)m_data[0]);
                cs.setString(2,(String)m_data[1]);
                cs.setInt(3,m_userID);
                if (m_data[2] == null)
                    cs.setNull(4,java.sql.Types.VARCHAR);
                else
                    cs.setString(4,(String)m_data[2]);
                if (m_data[3] == null)
                    cs.setNull(5,java.sql.Types.VARCHAR);
                else
                    cs.setString(5,(String)m_data[3]);
                Vector v = FMSObjCon.addObjectToStatement(6,m_data[4],cs);
                cs.registerOutParameter(7,java.sql.Types.INTEGER);

                cs.execute();

                FMSObjCon.cleanUp(v);

                m_data[5] = new Integer(cs.getInt(7));

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
