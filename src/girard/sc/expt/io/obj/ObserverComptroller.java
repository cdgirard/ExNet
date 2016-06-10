package girard.sc.expt.io.obj;

import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.ExptUserData;

import java.io.Serializable;
import java.util.Vector;

/**
 * The object that contains the Observer data for an observer connected
 * to an experiment. Created in JoinExptReqMsg when the joining request
 * is made as an Observer request.
 * <p>
 * Started: 4-21-2001
 * Last Modified: 4-30-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 * @see girard.sc.expt.io.msg.JoinExptReqMsg
 */

public class ObserverComptroller implements Serializable
    {
/**
 * The identifier for the observer, used as the key value for keeping track of
 * the Observer in the ExptComptroller.
 *
 * @see girard.sc.expt.io.obj.ExptComptroller
 */
    int        m_id = -1;
/**
 * The user that is this observer.
 */
    ExptUserData m_observer = null;
/**
 * Messages to be sent to this observer.
 */
    Vector     m_messages = new Vector();

/**
 * The constructor for the ObserverComptroller.
 *
 * @param user The class object that contains information on the user.
 * @param id The m_id for the ObserverComptroller.
 */
    public ObserverComptroller (ExptUserData user, int id)
        {
        m_id = id;
        m_observer = user;
        }

/**
 * Adds a message to m_messages.
 *
 * @param msg The ExptMessage to be added.
 */
    public void addMessage(ExptMessage msg)
        {
        m_messages.addElement(msg);
        }

    public int getID()
        {
        return m_id;
        }
    public Object getMessage(int index)
        {
        return m_messages.elementAt(index);
        }
    public Vector getMessages()
        {
        return m_messages;
        }
    public ExptUserData getObserver()
        {
        return m_observer;
        }

/**
 * Removes a message from the m_messages at the giving index.
 *
 * @param index The index of the message to remove.
 */
    public void removeMessage(int index)
        {
        m_messages.removeElementAt(index);
        }
    }
