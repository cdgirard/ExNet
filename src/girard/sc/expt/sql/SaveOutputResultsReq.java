package girard.sc.expt.sql;

/** Saves the output results from an ExperimentAction to the database in the table
 *  X.
 *
 * @author Dudley Girard
 * Started: 7-24-2001
 * Last Modified: 02-04-2002
 */

import girard.sc.ce.obj.CEExternalityOutputObject;
import girard.sc.expt.obj.DataOutputObject;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Vector;

public class SaveOutputResultsReq extends WLQuery
    {
    Vector m_data;

    public SaveOutputResultsReq(String db, Vector data, WLServerConnection wlsc, WLMessage wlm)
        {
        super(db, wlsc, wlm);
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
                    {
                    System.err.println("unable to create connection");
                    return false;
                    }
                // get a Statement object from the Connection
                //
                Enumeration enm = m_data.elements();
                while (enm.hasMoreElements())
                    {
                    DataOutputObject tmp = (DataOutputObject) enm.nextElement();
                    CallableStatement cs = m_DB.prepareCall(tmp.getInsertFormat());
                    tmp.formatInsertStatement(cs);
                    if (tmp instanceof CEExternalityOutputObject)
                        {
                        System.err.println("dupmping here");
                        }
                    cs.execute();
                    }

                closeConnection();

                return true;
                }
            catch (Exception e)
                {
                m_wlsc.addToLog(e);
                closeConnection();
                return false;
                }
            }
        }
    }
