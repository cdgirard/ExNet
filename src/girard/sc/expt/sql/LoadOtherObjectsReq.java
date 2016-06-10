package girard.sc.expt.sql;

/*  Used to load objects that are needed when rebuilding ExSoc
    objects from their settings.

Author: Dudley Girard
Started: 4-30-2001
*/

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Vector;

public class LoadOtherObjectsReq extends WLQuery
    {
    Vector m_types = new Vector();

    public LoadOtherObjectsReq (Vector types, WLServerConnection wlsc, WLMessage wlm)
        {
        super("exptDB", wlsc, wlm);

        m_types = types;
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
 
                StringBuffer objList = new StringBuffer("(");
                Enumeration enm = m_types.elements();
                while (enm.hasMoreElements())
                    {
                    String str = (String)enm.nextElement();
                    objList.append("'"+str+"'");
                    if (enm.hasMoreElements())
                        {
                        objList.append(", ");
                        }
                    }
                objList.append(")");

                ResultSet rs = stmt.executeQuery("SELECT * FROM Other_Objects_T WHERE Object_Name_VC IN "+objList);   

                // closeConnection();

                return rs;
                }
            catch( Exception e ) 
                {
                m_wlsc.addToLog(e.getMessage());
                closeConnection();
                return null;
                }
            }
        }

    public boolean runUpdate()
        {
        return true;
        }
    }
