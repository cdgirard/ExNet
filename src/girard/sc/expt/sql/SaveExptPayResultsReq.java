package girard.sc.expt.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;

public class SaveExptPayResultsReq extends WLQuery
    {
    int m_exptOutID;
    int m_actionIndex;
    double[] m_pay;

    public SaveExptPayResultsReq (int eoID, int ai, double[] pay, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_exptOutID = eoID;
        m_actionIndex = ai;
        m_pay = pay;
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
                
                for(int x=0;x<m_pay.length;x++)
                    {
                   // ExptOutID, actionIndex, subject, pay
                    CallableStatement cs = m_DB.prepareCall("{call up_insert_JExptPay (?, ?, ?, ?)}");

                    cs.setInt(1,m_exptOutID);
                    cs.setInt(2,m_actionIndex);
                    cs.setInt(3,x);
                    cs.setFloat(4,(float)m_pay[x]);

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
