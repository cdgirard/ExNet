package girard.sc.expt.io.msg;

import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;

public class SimJoinExptReqMsg extends ExptMessage 
    { 
    public SimJoinExptReqMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();
        
// System.err.println("ESR: Exnet Sim Join Experiment Request Message");
// System.err.flush();

        if (!(args[0] instanceof Long)  && !(args[1] instanceof Integer))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SimJoinExptReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Long cei = (Long)args[0];
        int index = ((Integer)args[1]).intValue();

        ExptComptroller ec = esc.getActiveExpt(cei);
       
        synchronized(ec)
            {
            esc.setExptIndex(ec);
            esc.setUserNum(index);
            esc.setData(new Integer(index));

// System.err.println("Sent out successful sim join");
// System.err.flush();

            return new SimJoinExptReqMsg(null);
            }
        }
    }