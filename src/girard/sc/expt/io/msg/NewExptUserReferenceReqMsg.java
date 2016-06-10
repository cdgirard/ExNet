package girard.sc.expt.io.msg;

import girard.sc.expt.sql.NewExptUserReferenceReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.util.Hashtable;

/**
 * Saves the user information for an experiment that is being run to the database.
 * <p>
 * <br>Started: 1-24-2000
 * <br>Modified: 6-20-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1  
 */

public class NewExptUserReferenceReqMsg extends ExptMessage 
    {
/**
 * The constructor for the class object. The following arguments are needed for it
 * to function properly:
 * <br>1. The OutputID assigned to the Experiment.
 * <br>2. The number of users in the experiment.
 * <br>3. A Hashtable containing all the user information.
 *
 * <p>
 * <br>NOTE: When the return message is sent back there are no argurments attached to it. 
 */
    public NewExptUserReferenceReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Attempts to save the user information to the database.  If successful then returns
 * a NewExptUserReferenceReqMsg with no arguments attached, otherwise returns an 
 * ExptErrorMsg.
 *
 * @param wlgsc The WLGeneralServerConnection that is processing the message.
 * @return Returns a NewExptUserReferenceReqMsg if succssful, otherwise returns an ExptErrorMsg.
 */
    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

// System.err.println("New Expt UserReference Req Msg");
// System.err.flush();
        if ((!(args[0] instanceof Integer)) || (!(args[1] instanceof Integer)) || (!(args[2] instanceof Hashtable)))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("NewExptUserReferenceReqMsg");
            return new ExptErrorMsg(err_args);
            }

        int exptOutID = ((Integer)args[0]).intValue();
        int numUsers = ((Integer)args[1]).intValue();
        Hashtable sims = (Hashtable)args[2];

        NewExptUserReferenceReq tmp = new NewExptUserReferenceReq(exptOutID,numUsers,sims,wlgsc,this);

        if (tmp.runUpdate())
            {
            return new NewExptUserReferenceReqMsg(null);
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Exception Error - NewExptUserReferenceReqMsg");
            err_args[1] = new String("NewExptUserReferenceReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }