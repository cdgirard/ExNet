package girard.sc.test;

import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;

import java.util.Vector;

public class SaveTestReqMsg extends ExptMessage 
    { 
    public SaveTestReqMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();
        
// System.err.println("STRM");

        if (!(args[0] instanceof String) || !(args[1] instanceof Vector))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SaveTestReqMsg");
            return new ExptErrorMsg(err_args);
            }

        String n = (String)args[0];
        Vector v = (Vector)args[1];
        Vector times = (Vector)args[2];

        SaveTestReq tmp = new SaveTestReq(n,v,esc,this);

        if (tmp.runUpdate())
            {
            Object[] out_args = new Object[1];
            out_args[0] = times;

// System.err.println("GOOD");
            return new SaveTestReqMsg(out_args);
            }
        else
            {
// System.err.println("BAD");
            Object[] err_args = new Object[2];
            err_args[0] = new String("Error Saving the Test");
            err_args[1] = new String("SaveTestReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }