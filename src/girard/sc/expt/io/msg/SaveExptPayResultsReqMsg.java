package girard.sc.expt.io.msg;

import girard.sc.expt.sql.SaveExptPayResultsReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

/**
 * Saves the pay results reference to an ExperimentAction being run in an experiment to
 * the database.
 * <p>
 * Started: 1-24-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1  
 */

public class SaveExptPayResultsReqMsg extends ExptMessage 
    {
/**
 * The constructor for the class object. The following arguments are needed for it
 * to function properly:
 * <br>1. The OutputID assigned to the Experiment.
 * <br>2. The order (from getActionIndex() in Experiment) in which the ExperimentAction 
 * was arranged in the experiment.
 * <br>3. The pay amount.  Is an array of doubles that is the same length as the number
 * of users (from getNumUsers in Experiment).
 *
 * <p>
 * <br>NOTE: When the return message is sent back there are no argurments attached to it. 
 */
    public SaveExptPayResultsReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Attempts to save the pay information to the database.  If successful then returns
 * a SaveExptPayResultsReqMsg with no arguments attached, otherwise returns an 
 * ExptErrorMsg.
 *
 * @param wlgsc The WLGeneralServerConnection that is processing the message.
 * @return Returns a SaveExptPayResutsReqMsg if succssful, otherwise returns an ExptErrorMsg.
 */

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if ((!(args[0] instanceof Integer)) || (!(args[1] instanceof Integer)) || (!(args[2] instanceof double[])))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1 - SaveExptPayResultsReqMsg");
            err_args[1] = new String("SaveExptPayResultsReqMsg");
            return new ExptErrorMsg(err_args);
            }

        int exptOutID = ((Integer)args[0]).intValue();
        int actionIndex = ((Integer)args[1]).intValue();
        double[] pay = (double[])args[2];

        SaveExptPayResultsReq tmp = new SaveExptPayResultsReq(exptOutID,actionIndex,pay,wlgsc,this);

        if (tmp.runUpdate())
            {
            return new SaveExptPayResultsReqMsg(null);
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Exception Error - SaveExptPayResultsReqMsg");
            err_args[1] = new String("SaveExptPayResultsReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }