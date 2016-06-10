package girard.sc.expt.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * This message retreives the user list and whether they are presently
 *  registered yet or not for the experiment that the ExptServerConnection
 *  is joined to.
 * <p>
 * <br>Started: 6-12-2000
 * <br>Modified: 4-24-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */


public class GetRegUsersMsg extends ExptMessage
    {
/**
 * The constructor for the message.  Doesn't require any arguments, so can pass in null.
 *
 * @param args[] The list of the arguments attached to the ExptMessage.
 */
    public GetRegUsersMsg (Object args[])
        {
        super(args);
        }

/**
 * Processes the message if received by an ExptServerConnection.  This would be called
 * from the ExptServerConnection.  Gets the list of Subject positions and whether they
 * have been filled as well as are they to be a human or a computer subject. Stores 
 * them in two arrays, and sends them back in a GetRegUsersMsg. The first array is
 * a boolean array that states true if a position has been filled or false if it has
 * not. The second array is m_HumanUser boolean array from the ExptComptroller.
 *
 * @param esc The ExptServerConnection that received the message.
 * @return Returns a response message, normally will return a GetRegUsersMsg with the
 * two arrays stored in the m_args of the message (1st array in position 0, 2nd array
 * in position 1); Otherwise if there is an error it returns an ExptErrorMsg.
 * @see girard.sc.expt.io.obj.ExptComptroller
 */
    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        ExptComptroller ec = esc.getExptIndex();

// System.err.println(" ESR: Exnet Get Reg Users Msg ");
        if (ec == null)
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("No Experiment Registered");
            err_args[1] = new String("GetRegUsersMsg");
            return new ExptErrorMsg(err_args);
            }

        boolean[] tmp1 = new boolean[ec.getNumUsers()];
        for (int i=0;i<ec.getNumUsers();i++)
            {
            tmp1[i] = ec.getRegistered(i);
            }

        Object[] out_args = new Object[2];
        out_args[0] = tmp1;
        out_args[1] = ec.getHumanUser();

        return new GetRegUsersMsg(out_args);
        }

/**
 * Processes the message if it is received by an ExperimenterWindow.  Checks to make sure
 * it has the proper m_args, updates the m_ExpApp variable with the new m_registered
 * settings, and lastly calls the updateDisplay() function for the ExperimenterWindow.
 * It does not update who is a human and who is a computer because that remains constant
 * throughout the experiment.  I'm sure there is a good reason why I include that info
 * in the return message though, just don't know what it is right now.
 *
 * @param ew The ExperimenterWindow that received this message.
 * @see girard.sc.expt.awt.ExperimenterWindow#m_ExpApp
 */
    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof boolean[]) && !(args[1] instanceof boolean[]))
            {
            // Return an error msg.
            new ErrorDialog("Incorrect Object Types. - GetRegUsersMsg");
            }

        ew.getExpApp().setRegistered((boolean[])args[0]);
        ew.updateDisplay();
        }

/**
 * Processes the message if it is received by an ObserverWindow.  Checks to make sure
 * it has the proper m_args, updates the m_ExpApp variable with the new m_registered
 * settings, and finally calls the updateDisplay() function for the ObserverWindow.
 * It does not update who is a human and who is a computer because that remains constant
 * throughout the experiment.  I'm sure there is a good reason why I include that info
 * in the return message though, just don't know what it is right now.
 *
 * @param ew The ObserverWindow that received this message.
 * @see girard.sc.expt.awt.ObserverWindow#m_ExpApp
 */
    public void getObserverResponse(ObserverWindow ow)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof boolean[]) && !(args[1] instanceof boolean[]))
            {
            // Return an error msg.
            new ErrorDialog("Incorrect Object Types. - GetRegUsersMsg");
            }

        ow.getExpApp().setRegistered((boolean[])args[0]);
        ow.updateDisplay();
        }
    }