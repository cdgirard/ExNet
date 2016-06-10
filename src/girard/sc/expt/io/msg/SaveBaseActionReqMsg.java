package girard.sc.expt.io.msg;

import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.sql.SaveBaseActionReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.util.Hashtable;

/**
 * Used to save a specific BaseAction.
 *
 * @author Dudley Girard
 * @version ExNet III 3.3
 * @since JDK1.1
 */

public class SaveBaseActionReqMsg extends ExptMessage 
    {
/**
 * The constructor function.  The Object array passed in should have one object.
 * The first object is the BaseAction object being saved.
 *
 * @param args[] The Object array of information needed to save the BaseAction.
 */
    public SaveBaseActionReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Used to set the action to preform for a WLGeneralServerConnection.  Gets the
 * the BaseAction from the m_args[] variable.  Uses this information to
 * initialize a SaveBaseActionReq.  Upon a successful runUpdate, it
 * sends back a SaveBaseActionReqMsg.  If anything wrong happens an ExptErrorMsg is
 * sent back instead.
 * <p>
 * @param wlgsc The WLGeneralServerConnection that received the message.
 * @return Returns a SaveBaseActionReqMsg with the retrieved BaseAction, otherwise returns an ExptErrorMsg.
 * @see girard.sc.expt.obj.BaseAction
 * @see girard.sc.expt.sql.SaveBaseActionReq
 * @see girard.sc.io.msg.TCPMessage#m_args
 */
    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();
        
        if (!(args[0] instanceof BaseAction))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SaveBaseActionReqMsg");
            return new ExptErrorMsg(err_args);
            }

        BaseAction ba = (BaseAction)args[0];
        Hashtable ag = (Hashtable)args[1];

        SaveBaseActionReq tmp = new SaveBaseActionReq(ba,ag,wlgsc,this);

        if (tmp.runUpdate())
            {
            return new SaveBaseActionReqMsg(null);
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Error Saving the : "+ba.getName());
            err_args[1] = new String("SaveBaseActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }