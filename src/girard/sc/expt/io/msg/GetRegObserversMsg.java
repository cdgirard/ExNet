package girard.sc.expt.io.msg;


import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.io.obj.ObserverComptroller;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This message retreives the observer list that is stored in the ExptComptroller
 * attached to this ExptServerConnection.
 * <p>
 * Started: 4-27-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class GetRegObserversMsg extends ExptMessage
    {
/**
 * The constructor for the message.  Doesn't require any arguments, so can pass in null.
 *
 * @param args[] The list of the arguments attached to the ExptMessage.
 */
    public GetRegObserversMsg (Object args[])
        {
        super(args);
        }

/**
 * Processes the message if received by an ExptServerConnection.  This would be called
 * from the ExptServerConnection.  Gets the list of Observers that have connected to
 * the experiment from the ExptComptroller, stores them in a Vector and sends them back
 * in a GetRegObserversMsg.  Information on the Observers is stored in the Vector is as a 
 * girard.sc.expt.obj.ExptUserData class object.
 *
 * @param esc The ExptServerConnection that received the message.
 * @return Returns a response message, normally will return a GetRegObserversMsg with the
 * list of Observers stored in the m_args of the message; Otherwise if there is an error
 * it returns an ExptErrorMsg.
 * @see girard.sc.expt.obj.ExptUserData
 */
    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        ExptComptroller ec = esc.getExptIndex();

// System.err.println(" ESR: Exnet Get Reg Observers Msg ");
        if (ec == null)
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("No Experiment Registered");
            err_args[1] = new String("GetRegObserversMsg");
            return new ExptErrorMsg(err_args);
            }

        Vector tmp = new Vector();
        Enumeration enm = ec.getObservers().elements();
        while (enm.hasMoreElements())
            {
            ObserverComptroller oc = (ObserverComptroller)enm.nextElement();
            tmp.addElement(oc.getObserver());
            }

        Object[] out_args = new Object[1];
        out_args[0] = tmp;

        return new GetRegObserversMsg(out_args);
        }

/**
 * Processes the message if it is received by an ExperimenterWindow.  Checks to make sure
 * it has the proper m_args, updates the m_ExpApp variable, m_Observers, with the new
 * Observer list, and lastly calls the updateDisplay() function for the 
 * ExperimenterWindow.
 *
 * @param ew The ExperimenterWindow that received this message.
 * @see girard.sc.expt.awt.ExperimenterWindow#m_ExpApp
 */
    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Vector))
            {
            // Return an error msg.
            new ErrorDialog("Incorrect Object Types. - GetRegObserversMsg");
            }

        ew.getExpApp().setObservers((Vector)args[0]);
        ew.updateDisplay();
        }

/**
 * We don't update the ObserverWindow because the ObserverExptInfo class object has 
 * no built in abilities for dealing with Observers at this time.
 *
 * @param ow The ObserverWindow that called this function.
 */
    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }
