package girard.sc.test;

import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;

import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Vector;

public class LoadTestReqMsg extends ExptMessage 
    { 
    public LoadTestReqMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("LTRM");

        if (!(args[0] instanceof String))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadTestReqMsg");
            err_args[1] = new String("LoadTestReqMsg");
            return new ExptErrorMsg(null);
            }

        String fileName = (String)args[0];
        Vector times = (Vector)args[1];

        times.addElement(new Long(Calendar.getInstance().getTime().getTime()));

        try 
            { 
            LoadTestReq tmp = new LoadTestReq(times,fileName,esc,this);
// System.err.println("RUN QUERY");
            ResultSet rs = tmp.runQuery();
// System.err.println("FINISHED QUERY");
            if (rs.next())
                {
                ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Object_OBJ"));
                Object tmpObj = ois.readObject();
   
                

                Object[] out_args = new Object[2];
                out_args[0] = tmpObj;
                out_args[1] = times;

// System.err.println("SENDING BACK MESSAGE1");
                times.addElement(new Long(Calendar.getInstance().getTime().getTime()));
                return new LoadTestReqMsg(out_args);
                }
            else
                {
// System.err.println("SENDING BACK MESSAGE2");
                times.addElement(new Long(0));
                times.addElement(new Long(0));
                

                Object[] err_args = new Object[4];
                err_args[0] = new String("Nothing to Load");
                err_args[1] = times;
                times.addElement(new Long(Calendar.getInstance().getTime().getTime()));

                return new LoadTestReqMsg(err_args);
                }
            }
        catch( Exception e )
            {
// System.err.println("SENDING BACK MESSAGE3A: "+e.getMessage());
            esc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadTestReqMsg");
            err_args[1] = new String("LoadTestReqMsg");
// System.err.println("SENDING BACK MESSAGE3B");
            return new ExptErrorMsg(err_args);
            }
        }
    }
