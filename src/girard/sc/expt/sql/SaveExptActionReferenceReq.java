package girard.sc.expt.sql;

/* Saves to the database table the base information on the experiment action being 
   run who's data is to be collected.

   Author: Dudley Girard
   Started: 1-23-2001
*/

import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Vector;

public class SaveExptActionReferenceReq extends WLQuery
    {
    Object[] m_args;

    public SaveExptActionReferenceReq (Object[] args, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_args = args;
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
                     
               // Expt_Out_ID_INT, ID_INT, Action_Index_INT, Action_Object_Index_INT, Action_OBJ, Action_DB_VC, Action_Name_VC, Action_Desc_VC
                CallableStatement cs = m_DB.prepareCall("{call up_insert_JExptActionReference (?, ?, ?, ?, ?, ?, ?, ?)}");

                cs.setInt(1,((Integer)m_args[0]).intValue());
                cs.setInt(2,m_userID);
                cs.setInt(3,((Integer)m_args[1]).intValue());
                cs.setInt(4,((Integer)m_args[2]).intValue());
                Vector v = FMSObjCon.addObjectToStatement(5,m_args[3],cs);
                cs.setString(6,(String)m_args[4]);
                cs.setString(7,(String)m_args[5]);
                cs.setString(8,(String)m_args[6]);
                cs.execute();
                FMSObjCon.cleanUp(v);

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
