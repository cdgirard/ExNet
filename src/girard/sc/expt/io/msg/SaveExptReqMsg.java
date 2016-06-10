package girard.sc.expt.io.msg;

import girard.sc.expt.obj.Experiment;
import girard.sc.expt.sql.SaveExptReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

public class SaveExptReqMsg extends ExptMessage 
    { 
    public SaveExptReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();
        System.err.println("murali:I'm in io.msg.saveexptreq:1");
        
        if (!(args[0] instanceof Experiment))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SaveExptReqMsg");
            System.err.println("murali:I'm in io.msg.saveexptreq: before returning error");
                return new ExptErrorMsg(err_args);
            }

        Experiment ee = (Experiment)args[0];

        SaveExptReq tmp = new SaveExptReq(ee,wlgsc,this);
        System.err.println("murali:I'm in io.msg.saveexptreq: after calling the sql.SaveExptReq");
        
        if (tmp.runUpdate())
            {
            return new SaveExptReqMsg(null);
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Exception Error");
            err_args[1] = new String("SaveExptReqMsg");
            System.err.println("murali:I'm in io.msg.saveexptreq: second error");
            
            return new ExptErrorMsg(err_args);
            }
        }
    }