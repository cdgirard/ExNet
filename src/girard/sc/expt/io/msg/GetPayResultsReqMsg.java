package girard.sc.expt.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ShowPayResultsWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.sql.GetPayResultsReq;

import java.sql.ResultSet;
import java.util.Hashtable;

public class GetPayResultsReqMsg extends ExptMessage
    { 
    public GetPayResultsReqMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Integer))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1 - GetPayResultsReqMsg");
            err_args[1] = new String("GetPayResultsReqMsg");
            return new ExptErrorMsg(err_args);
            }

        ExptComptroller ec = esc.getExptIndex();

// System.err.println(" ESR: Exnet Get Pay Results Req Msg ");
        if (ec == null)
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("No Experiment Registered");
            err_args[1] = new String("GetPayResultsReqMsg");
            return new ExptErrorMsg(err_args);
            }

        int exptOutID = ((Integer)args[0]).intValue();

        GetPayResultsReq tmp = new GetPayResultsReq(exptOutID,esc,this);

        ResultSet rs = tmp.runQuery();

        if (rs == null)
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Failed to get type information from databse - GetPayResultsReq.");
            err_args[1] = new String("GetPayResultsReqMsg");
            return new ExptErrorMsg(err_args);
            }

        try 
            {
            Hashtable payInfo = new Hashtable();

            while(rs.next()) 
                {
                double pay = rs.getFloat("Pay_FLT");
                int action = rs.getInt("Action_Index_INT");
                int user = rs.getInt("User_Num_INT");

                if (payInfo.containsKey(new Integer(action)))
                    {
                    double[] payment = (double[])payInfo.get(new Integer(action));
                    payment[user] = pay;
                    }
                else
                    {
                    double[] payment = new double[ec.getNumUsers()];
                    payment[user] = pay;
                    payInfo.put(new Integer(action),payment);
                    }
                }

            Object[] out_args = new Object[1];
            out_args[0] = payInfo;
	    System.err.println("****returning new GetPayResRqMsg*****");
            return new GetPayResultsReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            System.out.println(e.getMessage());
            e.printStackTrace();
            Object[] err_args = new Object[2];
            err_args[0] = new String("Error with action database entries");
            err_args[1] = new String("GetPayResultsReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Hashtable))
            {
            // Return an error msg.
            new ErrorDialog("Incorrect Object Types. - GetPayResultsReqMsg") ;
            }
	System.err.println("showing pay results ehre...");
        new ShowPayResultsWindow((Hashtable)args[0],ew);
        }
    }
