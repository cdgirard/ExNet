package girard.sc.expt.io.msg;

/* Used to get the list of simulant actors for a particular experiment
   type
   
   Author: Dudley Girard
   Created: 8-26-2000
*/

import girard.sc.expt.obj.SimActor;
import girard.sc.expt.sql.SimActorTypeListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Vector;

public class SimActorTypeListReqMsg extends ExptMessage 
    { 
    public SimActorTypeListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
// System.err.println("Exnet Simulant Actor Type List Req Msg");
// System.err.flush();

        SimActorTypeListReq tmp = new SimActorTypeListReq(wlgsc,this);
 
        ResultSet rs = tmp.runQuery();

        if (rs == null)
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Failed to get type information from databse.");
            err_args[1] = new String("SimActorTypeListReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Vector sims = new Vector();
        try
            {
            while(rs.next()) 
                {
                int id = rs.getInt("Sim_Type_ID_INT");
                ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Actor_OBJ"));
                SimActor sa = (SimActor)ois.readObject();
                sa.setActorTypeID(id);
                sims.addElement(sa);
                }

            Object[] out_args = new Object[1];
            out_args[0] = sims;

            return new SimActorTypeListReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Failed to get type information from databse.");
            err_args[1] = new String("SimActorTypeListReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
