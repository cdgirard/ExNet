package girard.sc.wl.io.msg;

import girard.sc.io.msg.TCPMessage;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.sql.WLQuery;

/**
 * The the base message type for all messages sent in ExNet III.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class WLMessage extends TCPMessage 
    { 
    private long m_securityKey = 0;
    private int  m_userID = 0;
    private int  m_numSecChecks = 0;  // Why is this here?

/**
 * The constructor for WLMessage.
 *
 * @param args[] The array of objects being sent via the message.
 */
    public WLMessage (Object args[])
        {
        super(args);
        }

/**
 * Used to make sure that the message is from a legitimate source.
 *
 * @param value Value used in determining if the source of the message is legitimate.
 * @return Returns true if a legitimate, false otherwise.
 */
    public boolean containsValidMsg(long value)
        {
        if ((m_numSecChecks < 1) && (value == m_securityKey))
            return true;
        else
            return false;
        }

/**
 * Is required by TCPMessage to have this function.  Because a bit more control is needed
 * and to make debugging easier this function is not used.  The function 
 * getGeneralServerResponse is used in its place.
 *
 * @param obj Usually the ServerConnection or message handler that is processing the message.
 * @return Any return message that should be sent back.
 */
    public TCPMessage getResponse(Object obj)
        {
        return new WLConfirmMsg(null);
        }

/**
 * Used to set the action to preform for a WLGeneralServerConnection.  When a
 * WLGeneralServerConnection receives an WLMessage it calls this function.  This is
 * normally where the message is utilizes additional classes to access the database.
 * The function allows you to return a message directly back to the sender if needed.
 * <p>
 * Classes that extend the WLMessage should override this function, otherwise all
 * that will happen is a WLConfirmMsg(null) will be sent back.
 * <p>
 * @param wlgsc The WLGeneralServerConnection that received the message.
 * @return Returns a WLMessage if there is a response, otherwise should return null.
 * @see girard.sc.wl.sql.WLQuery
 */
    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        return new WLConfirmMsg(null);
        }

/**
 * Used to initialize a WLQuery with needed information attached to a WLMessage.
 * You don't need to call this directly as it is called from the WLQuery class
 * constructor.  For more information see the WLQuery class doc.
 *
 * @param eq The WLQuery class object.
 */
    public void initializeDBQuery(WLQuery eq)
        {
        eq.setUserID(m_userID);
        }

    public void setUserID(int value)
        {
        m_userID = value;
        }
    public void setSecurityKey(long value)
        {
        m_securityKey = value;
        }
    }
