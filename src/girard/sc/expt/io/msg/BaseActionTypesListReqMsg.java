package girard.sc.expt.io.msg;

import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.sql.BaseActionTypesListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Vector;

/**
 * Retrieves list of BaseActions from the database.
 * 
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1 
*/

public class BaseActionTypesListReqMsg extends ExptMessage 
    { 
    public BaseActionTypesListReqMsg (Object[] args)
        {
        super(args);
        }

/**
 * Retrieves list of BaseActions from the database. The actual BaseActions and their
 * descriptions are placed into Vectors.  The Vectors are stored in the returned
 * BaseActionTypesListReqMsg.
 * <p>
 * The actual calls to the database is done through a BaseActionTypesListReq object.
 * <p>
 * If there are any problems with in the retrieval an ExptErrorMsg is sent back instead
 * with details.  Additional information may be written to one of the server log files as well.
 * <p.
 * @param wlgsc The WLGeneralServerConnection that is processing the message.
 * @return Returns a BaseActionTypesListReqMsg with the BaseActions and their descriptions.
 * @see girard.sc.expt.obj.BaseAction
 * @see girard.sc.expt.sql.BaseActionTypesListReq
 */
    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        BaseActionTypesListReq tmp = new BaseActionTypesListReq(wlgsc,this);

        ResultSet rs = tmp.runQuery();

        if (rs == null)
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Failed to get type information from databse - BaseActionTypesListReq.");
            err_args[1] = new String("BaseActionTypesListReqMsg");
            return new ExptErrorMsg(err_args);
            }

        try 
            {
            Vector actInfo = new Vector();
            Vector actDesc = new Vector();

            while(rs.next()) 
                {
                int id = rs.getInt("Base_Action_Type_ID_INT");
                String desc = rs.getString("Base_Action_Desc_VC");

                ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Base_Action_OBJ"));
                BaseAction ba = (BaseAction)ois.readObject();

                ba.setActionType(id);
                actInfo.addElement(ba);
                actDesc.addElement(desc);
                }


            Object[] out_args = new Object[2];
            out_args[0] = actInfo;
            out_args[1] = actDesc;

            return new BaseActionTypesListReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());
            
            Object[] err_args = new Object[1];
            err_args[0] = new String("Error with action database entries");
            err_args[1] = new String("BaseActionTypesListReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
