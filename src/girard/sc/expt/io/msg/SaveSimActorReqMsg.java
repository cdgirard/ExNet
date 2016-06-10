package girard.sc.expt.io.msg;

import girard.sc.expt.obj.SimActor;
import girard.sc.expt.sql.SaveSimActorReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

public class SaveSimActorReqMsg extends ExptMessage 
    { 
    public SaveSimActorReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();
        
        if (!(args[0] instanceof SimActor))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("BESaveSimActorReqMsg");
            return new ExptErrorMsg(err_args);
            }

        SimActor sa = (SimActor)args[0];

        SaveSimActorReq tmp = new SaveSimActorReq(sa,wlgsc,this);

        if (tmp.runUpdate())
            {
            return new SaveSimActorReqMsg(null);
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Error Saving the Simulant Actor");
            err_args[1] = new String("SaveSimActorReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }