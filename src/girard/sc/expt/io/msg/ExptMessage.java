package girard.sc.expt.io.msg;

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.wl.io.msg.WLMessage;

/**
 * Base class for all experiment messages.  What you normally extend when you want 
 * to create a message.  Is an extension of the WLMessage class object.
 * <p>
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 * @see girard.sc.wl.io.msg.WLMessage
 */

public class ExptMessage extends WLMessage 
    { 

/**
 * The constructor.  Allows the programmer to pass in an array of Objects to
 * allow for pretty much anything to be sent using an ExptMessage so long as it
 * is serializable.
 * 
 * @param args The array of Objects that is tied to the ExptMessage.
 */
    public ExptMessage (Object args[])
        {
        super(args);
        }

/**
 * Used to set the action to preform for when the message is received by an 
 * ExptServerConnection.  When an ExptServerConnection receives an ExptMessage it
 * calls this function.  This is normally where the message is fowarded, such as
 * from the experimenter station to one of the client stations. The function allows
 * you to return a message directly back to the sender if needed.
 *
 * @param esc The ExptServerConnection that received the message.
 * @return Returns an ExptMessage if there is a response, otherwise should return null.
 * @see girard.sc.expt.io.ExptServerConnection
 * @see girard.sc.expt.awt.ExperimenterWindow
 * @see girard.sc.expt.awt.ClientWindow
 * @see girard.sc.expt.awt.ObserverWindow
 */
    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        return new ExptMessage(null);
        }

/**
 * Used to set the action to preform for an ExperimenterWindow.  When an
 * ExperimenterWindow receives an ExptMessage it should call this function.  This is
 * normally where the message is processed by the experimenter station.  
 * <p>
 *
 * @param ew The ExperimenterWindow that received the message.
 * @see girard.sc.expt.awt.ExperimenterWindow
 */
    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        }

/**
 * Used to set the action to preform for an ClientWindow.  When a
 * ClientWindow receives an ExptMessage it should call this function.  This is
 * normally where the message is processed by the client station.  
 * <p>
 *
 * @param cw The ClientWindow that received the message.
 * @see girard.sc.expt.awt.ClientWindow
 */
    public void getClientResponse(ClientWindow cw)
        {
        }

/**
 * Used to set the action to preform for an ObserverWindow.  When an
 * ObserverWindow receives an ExptMessage it should call this function.  This is
 * normally where the message is processed by the observer station.  
 * <p>
 *
 * @param ow The ObserverWindow that received the message.
 * @see girard.sc.expt.awt.ObserverWindow
 */
    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }