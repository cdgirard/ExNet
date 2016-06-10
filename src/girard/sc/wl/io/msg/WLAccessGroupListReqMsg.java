package girard.sc.wl.io.msg;

import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.sql.WLAccessGroupListReq;

import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Retrieves a list of access groups that are accessible by a particular user.
 * Returns the list of groups in a Vector of Hashtables.  Each Hashtable contains
 * the following information:
 * <br> App_ID stored as a String under the key "App ID"
 * <br> App_Name_VC stored as a String under the key "App Name"
 * <br> App_Desc_VC stored as a String under the key "App Desc"
 * <p>
 * <br> Started: 10-24-2002
 * <p>
 * 
 * @author Dudley Girard
 */

public class WLAccessGroupListReqMsg extends WLMessage 
    { 
    public WLAccessGroupListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        WLAccessGroupListReq tmp = new WLAccessGroupListReq(wlgsc,this);

        try
            {
            ResultSet rs = tmp.runQuery();
            if (rs != null)
                {
                Vector accessGroups = new Vector();
                while (rs.next())
                    {
                    Hashtable accessGroup = new Hashtable();
                    accessGroup.put("App Name",rs.getString("App_Name_VC"));
                    accessGroup.put("App Desc",rs.getString("App_Desc_VC"));
                    accessGroup.put("App ID",rs.getString("App_ID"));
                    accessGroups.addElement(accessGroup);
                    }
                Object[] out_args = new Object[1];
                out_args[0] = accessGroups;
                return new WLAccessGroupListReqMsg(out_args);
                }
            else
                {
                Object[] err_args = new Object[1];
                err_args[0] = new String("Unable to retrieve list of access groups.");
                return new WLErrorMsg(err_args);
                }
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());
            Object[] err_args = new Object[1];
            err_args[0] = e.getMessage();
            return new WLErrorMsg(err_args);
            }
        }
    }
