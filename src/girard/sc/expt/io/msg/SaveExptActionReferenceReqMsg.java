package girard.sc.expt.io.msg;

import girard.sc.expt.sql.SaveExptActionReferenceReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.util.Hashtable;

/**
 * Saves a reference to an ExperimentAction being run in an experiment to the database.
 * <p>
 * Started: 1-24-2000
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1  
 */

public class SaveExptActionReferenceReqMsg extends ExptMessage 
    {
/**
 * The constructor for the class object. The following arguments are needed for it
 * to function properly:
 * <br>1. The OutputID assigned to the Experiment.
 * <br>2. The order in which the ExperimentAction was arranged in the experiment.
 * <br>3. The type identified for the ExperimentAction, this is for restoring the
 * ExperimentAction later.
 * <br>4. The settings for the ExperimentAction, gotten via its getSettings() function.
 * <br>5. The database in which the save files for this ExperimentAction are placed.
 * <br>6. The detailed name for the ExperimentAction.
 * <br>7. The description of the ExperimentAction.
 *
 * <p>
 * <br>NOTE: When the return message is sent back there are no argurments attached to it. 
 */
    public SaveExptActionReferenceReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Attempts to save the ExperimentAction to the database.  If successful then returns
 * a SaveExptActionReferenceReqMsg with no arguments attached, otherwise returns an 
 * ExptErrorMsg.
 *
 * @param wlgsc The WLGeneralServerConnection that is processing the message.
 * @return Returns a SaveExptActionReferenceReqMsg if succssful, otherwise returns an ExptErrorMsg.
 */
    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if ((!(args[0] instanceof Integer)) || (!(args[1] instanceof Integer)) || (!(args[2] instanceof Integer)))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SaveExptActionReferenceReqMsg");
            return new ExptErrorMsg(err_args);
            }

        if (!(args[3] instanceof Hashtable))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SaveExptActionReferenceReqMsg");
            return new ExptErrorMsg(err_args);
            }

        if ((!(args[4] instanceof String)) || (!(args[5] instanceof String)) || (!(args[6] instanceof String)))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SaveExptActionReferenceReqMsg");
            return new ExptErrorMsg(err_args);
            }

        SaveExptActionReferenceReq tmp = new SaveExptActionReferenceReq(args,wlgsc,this);

        if (tmp.runUpdate())
            {
            return new SaveExptActionReferenceReqMsg(null);
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Exception Error - SaveExptReferenceReqMsg");
            err_args[1] = new String("SaveExptActionReferenceReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }