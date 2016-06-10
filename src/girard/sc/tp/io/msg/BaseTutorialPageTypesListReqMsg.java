package girard.sc.tp.io.msg;

/* Used to get the list of the base tutorial page types that can
   be used in a TutorialAction.
   
   Author: Dudley Girard
   Started: 11-19-2001
*/

import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.tp.obj.TutorialPage;
import girard.sc.tp.sql.BaseTutorialPageTypesListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Vector;


public class BaseTutorialPageTypesListReqMsg extends ExptMessage 
    { 
    public BaseTutorialPageTypesListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
// System.err.println("Exnet Base Tutorial Page Types List Req Msg");
// System.err.flush();

        BaseTutorialPageTypesListReq tmp = new BaseTutorialPageTypesListReq(wlgsc,this);
 
        ResultSet rs = tmp.runQuery();

        if (rs == null)
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Failed to get type information from databse.");
            err_args[1] = new String("BaseTutorialPageTypesListReqMsg");
            return new ExptErrorMsg(err_args);
            }

        try
            {
            Vector tpInfo = new Vector();
            Vector tpDesc = new Vector();

            while(rs.next()) 
                {
                String desc = rs.getString("Desc_VC");

                ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Tut_Page_OBJ"));
                TutorialPage tp = (TutorialPage)ois.readObject();

                tpInfo.addElement(tp);
                tpDesc.addElement(desc);
                }


            Object[] out_args = new Object[2];
            out_args[0] = tpInfo;
            out_args[1] = tpDesc;

            return new BaseTutorialPageTypesListReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Failed to get type information from databse.");
            err_args[1] = new String("BaseTutorialPageTypesListReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
