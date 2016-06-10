package girard.sc.expt.io.msg;

import girard.sc.expt.sql.SavedBaseActionFileListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class SavedBaseActionFileListReqMsg extends ExptMessage 
    {
    public SavedBaseActionFileListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
// System.err.println("Saved Base Action File List Req Msg");
// System.err.flush();
        Object[] args = this.getArgs();

        if (!(args[0] instanceof String) || !(args[1] instanceof String))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SavedBaseActionFileListReqMsg");
            return new ExptErrorMsg(err_args);
            }

        try
            {
            String db = (String)args[0];
            String dbTable = (String)args[1];
            Vector ags = (Vector)args[2];
            String ag = null;
            Vector netInfo = new Vector();

            SavedBaseActionFileListReq tmp = new SavedBaseActionFileListReq(db,dbTable,ag,wlgsc,this);

            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from databse - SavedBaseActionFileListReq.");
                err_args[1] = new String("SavedBaseActionFileListReqMsg");
                return new ExptErrorMsg(err_args);
                }
         // Retrieve the personal files first.   
            while(rs.next()) 
                {
                Object[] obj = new Object[5];
                obj[0] = rs.getString("Name_VC");
                obj[1] = rs.getString("Desc_VC");
                obj[2] = rs.getString("App_Name_VC");
                obj[3] = rs.getString("App_ID");
                obj[4] = new Integer(rs.getInt("ID_INT"));

                netInfo.addElement(obj);
                }
         // Retrieve shared files next.
            Enumeration enm = ags.elements();
            while (enm.hasMoreElements())
                {
                Hashtable h = (Hashtable)enm.nextElement();
                ag = (String)h.get("App ID");

                if (ag != null)
                    {
                    SavedBaseActionFileListReq tmp2 = new SavedBaseActionFileListReq(db,dbTable,ag,wlgsc,this);

                    ResultSet rs2 = tmp2.runQuery();

                    if (rs2 == null)
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Failed to get type information from databse - SavedBaseActionFileListReq.");
                        err_args[1] = new String("SavedBaseActionFileListReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
            
                    while(rs2.next()) 
                        {
                        Object[] obj = new Object[5];
                        obj[0] = rs2.getString("Name_VC");
                        obj[1] = rs2.getString("Desc_VC");
                        obj[2] = rs2.getString("App_Name_VC");
                        obj[3] = rs2.getString("App_ID");
                        obj[4] = new Integer(rs2.getInt("ID_INT"));

                        netInfo.addElement(obj);
                        }
                    }
                }

            Object[] out_args = new Object[1];
            out_args[0] = netInfo;

            return new SavedBaseActionFileListReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in SavedBaseActionFileListReqMsg");
            err_args[1] = new String("ExnetNetworkFileListReqMsg");
            return new ExptErrorMsg(err_args);

            }
        }
    }
