package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Vector;

public class NewExptPayReq extends WLQuery
    {
    Vector m_payments;

    public NewExptPayReq (Vector payments, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_payments = payments;
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
                     
               // ExptOutputID, ActionIndex, Subject, Pay.
                Enumeration enm = m_payments.elements();
                while(enm.hasMoreElements())
                    {
                    Object[] args = (Object[])enm.nextElement();

                 // Expt_Out_ID, Expt_Action_Index, Subject_ID, Pay
                    CallableStatement cs = m_DB.prepareCall("{call up_insert_JExptPay (?, ?, ?, ?)}");

                    cs.setInt(1,((Integer)args[0]).intValue());
                    cs.setInt(2,((Integer)args[1]).intValue());
                    cs.setInt(3,((Integer)args[2]).intValue());
                    cs.setInt(4,((Integer)args[3]).intValue());

                    cs.execute();
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
