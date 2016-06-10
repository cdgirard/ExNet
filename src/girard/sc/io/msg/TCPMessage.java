package girard.sc.io.msg;

import java.io.Serializable;

/**
 * The the base message type for all messages sent using the girard.sc.io class package.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 * @see girard.sc.io
 */

public abstract class TCPMessage implements Serializable
    {
/**
 * Is an array of Objects that allows a wide range of data to be sent.
 * NOTE: ALL OBJECTS STORED IN THE ARRAY MUST BE SERIALIZABLE!
 *
 */
    protected Object m_args[];

/**
 * The base constructor function.  Pass in the array of Objects to be sent by the
 * message.
 *
 * @param args[] The array of Objects being sent.
 */
    public TCPMessage (Object args[])
        {
        m_args = args;
        }

/**
 * Is required by TCPMessage to have this function.  Is provided as a basic function for
 * handling message processing.
 *
 * @param obj Usually the ServerConnection or message handler that is processing the message.
 * @return Any return message that should be sent back.
 */
    public abstract TCPMessage getResponse(Object obj);

    public Object[] getArgs()
        {
        return m_args;
        }
    }