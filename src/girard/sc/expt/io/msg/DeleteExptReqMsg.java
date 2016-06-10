package girard.sc.expt.io.msg;

import girard.sc.expt.obj.Experiment;
import girard.sc.expt.sql.DeleteExptReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

public class DeleteExptReqMsg extends ExptMessage 
    { 
    public DeleteExptReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Experiment))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect data type for delete experiment message.");
            err_args[1] = new String("DeleteExptReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Experiment ee = (Experiment)args[0];

        DeleteExptReq tmp = new DeleteExptReq(ee.getExptID(),wlgsc,this);

        try
            {
            if (tmp.runUpdate())
                {
                Object[] out_args = new Object[1];
                out_args[0] = ee;
                return new DeleteExptReqMsg(out_args);
                }
            else
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Experiment does not exist");
                err_args[1] = new String("DeleteExptReqMsg");
                return new ExptErrorMsg(err_args);
                }
            }
        catch (Exception e)
            {
            wlgsc.addToLog(e.getMessage());
            Object[] err_args = new Object[2];
            err_args[0] = new String("Error retreving experiment: "+e);
            err_args[1] = new String("DeleteExptReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
