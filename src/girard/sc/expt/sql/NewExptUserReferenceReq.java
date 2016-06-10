package girard.sc.expt.sql;

import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class NewExptUserReferenceReq extends WLQuery
    {
    int m_exptOutID;
    int m_numUsers;
    Hashtable m_actionSimData;

    public NewExptUserReferenceReq (int eoID, int nu, Hashtable s, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);
        m_exptOutID = eoID;
        m_numUsers = nu;
        m_actionSimData = s;
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
                
                Enumeration enm = m_actionSimData.keys();
                while (enm.hasMoreElements())
                    {
                    Integer actionNum = (Integer)enm.nextElement();
                    Hashtable sims = (Hashtable)m_actionSimData.get(actionNum);

                    for (int i=0;i<m_numUsers;i++)
                        {
                        if (sims.containsKey(new Integer(i)))
                            {
                            Object[] data = (Object[])sims.get(new Integer(i));

                            if (actionNum.intValue() == 0)
                                {
                                CallableStatement cs = m_DB.prepareCall("{call up_insert_JExptUserReference (?, ?, ?)}");

                                cs.setInt(1,m_exptOutID);
                                cs.setInt(2,i);
                                cs.setBoolean(3,false);
                                
                                cs.execute();
                                }

                            CallableStatement cs = m_DB.prepareCall("{call up_insert_JExptSimReference (?, ?, ?, ?, ?, ?, ?)}");

                            cs.setInt(1,m_exptOutID);
                            cs.setInt(2,actionNum.intValue());
                            cs.setInt(3,i);
                            cs.setInt(4,((Integer)data[0]).intValue());
                            cs.setString(5,(String)data[1]);
                            cs.setString(6,(String)data[2]);
                            Vector v = FMSObjCon.addObjectToStatement(7,data[3],cs);

                            cs.execute();
                            FMSObjCon.cleanUp(v);
                            }
                        else if (actionNum.intValue() == 0)
                            {
                            CallableStatement cs = m_DB.prepareCall("{call up_insert_JExptUserReference (?, ?, ?)}");

                            cs.setInt(1,m_exptOutID);
                            cs.setInt(2,i);
                            cs.setBoolean(3,true);
 
                            cs.execute();
                            }
                        }
                    }

                // closeConnection();

                return true;
                }
            catch( Exception e ) 
                {
                m_wlsc.addToLog(e.getMessage());
                m_wlsc.addToLog("EX ID: "+m_exptOutID+" NU: "+m_numUsers);
                closeConnection();
                return false;
                }
            }
        }
    }
