package girard.sc.expt.io.msg;

/* Saves the output results from an ExperimentAction.

   Author: Dudley Girard
   Started: 7-24-2001
   Modified: 02-04-2002
*/

import girard.sc.expt.sql.SaveOutputResultsReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.util.Vector;

public class SaveOutputResultsReqMsg extends ExptMessage 
    { 
    public SaveOutputResultsReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof String) && !(args[1] instanceof Vector))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SaveOutputResultsReqMsg");
            return new ExptErrorMsg(err_args);
            }

        String db = (String)args[0];
        Vector v = (Vector)args[1];

        SaveOutputResultsReq tmp = new SaveOutputResultsReq(db,v,wlgsc,this);

        if (tmp.runUpdate())
            {
            return new SaveOutputResultsReqMsg(null);
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Exception Error - SORRM");
            err_args[1] = new String("SaveOutputResultsReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
