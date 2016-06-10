package girard.sc.expt.io.msg;

import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.sql.ExptActionTypesListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Vector;

public class ExptActionTypesListReqMsg extends ExptMessage 
    { 
    public ExptActionTypesListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        ExptActionTypesListReq tmp = new ExptActionTypesListReq(wlgsc,this);

        ResultSet rs = tmp.runQuery();

        if (rs == null)
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Failed to get type information from databse - ExptActionTypesListReq.");
            err_args[1] = new String("ExptActionTypesListReqMsg");
            return new ExptErrorMsg(err_args);
            }

        try 
            {
            Vector actInfo = new Vector();
            Vector actDesc = new Vector();

            while(rs.next()) 
                {
                int id = rs.getInt("Action_Type_ID_INT");
                String desc = rs.getString("Action_Desc_VC");

                ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Action_OBJ"));
                try
                    {
                    ExperimentAction ea = (ExperimentAction)ois.readObject();

                    ea.setActionType(id);
                    actInfo.addElement(ea);
                    actDesc.addElement(desc);
                    }
                catch (Exception e)
                    {
                    wlgsc.addToLog(e.getMessage());
                    }
                }


            Object[] out_args = new Object[2];
            out_args[0] = actInfo;
            out_args[1] = actDesc;

            return new ExptActionTypesListReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Error with action database entries");
            err_args[1] = new String("ExptActionTypesListReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
