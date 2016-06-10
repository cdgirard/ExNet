package girard.sc.wl.io;

import girard.sc.io.ServerVulture;
import girard.sc.wl.io.msg.WLErrorMsg;
import girard.sc.wl.io.msg.WLMessage;

import java.io.IOException;
import java.net.Socket;

/**
 * When the WLGeneralServer gets a connection request it spawns one of these.
 * This class is the thread that handles all communication with a client.
 * It also notifies the ServerVulture when the connection is dropped.  It only
 * processes one request (message), then closes down.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 * @see girard.sc.wl.io.WLGeneralServer
 * @see girard.sc.io.ServerVulture
 */

public class WLGeneralServerConnection extends WLServerConnection
    {

/**
 * Initialize the streams and start the thread.
 *
 * @param clientSocket The Socket that the messages are sent to and from.
 * @param threadgroup The ThreadGroup the class object is tied to.
 * @param priority The priority given to this Thread in the ThreadGroup.
 * @param vulture The ServerVulture for this class object.
 * @param app Which WLGeneralServer spawned this class object.
 */
    public WLGeneralServerConnection(Socket clientSocket, ThreadGroup threadgroup, int priority, ServerVulture vulture, WLGeneralServer app) 
        {
        // Give the thread a group, a name, and a priority.
        super(clientSocket,threadgroup,priority,vulture,app);

        // And start the thread up
        this.start();
        }
    
/**
 * Listens on the incoming stream for any messages.  If a message is received it
 * checks to see if it is valid then runs getGeneralServerResponse.  If there is
 * a message to send back it sends it back via the out stream. The streams are then
 * closed and the thread stops.
 * <p>
 * @see girard.sc.wl.io.msg.WLMessage
 */
    public void run() 
        {
        try 
            {
            WLMessage msg = (WLMessage)m_in.readObject();
            if (msg.containsValidMsg(m_securityKey))
                {
                WLMessage out_msg = msg.getGeneralServerResponse(this);
                m_out.writeObject(out_msg);
                m_out.flush();
                }
            else
                {
                Object[] err_args = new Object[1]; 
                err_args[0] = new String("Illegal Message");
                WLErrorMsg err_msg = new WLErrorMsg(err_args);
                m_out.writeObject(err_msg);
                m_out.flush();
                }
            }
        catch (IOException ioe) { addToLog("IOE: "+ioe+" Loc: "+this); }
        catch (ClassNotFoundException cnfe) { addToLog("CNFE: "+cnfe+" Loc: "+this); }
        catch (Exception e) { addToLog("E: "+e+" Loc: "+this); }

        // When we're done, for whatever reason, be sure to close
        // the socket, and to notify the Vulture object.  Note that
        // we have to use synchronized first to lock the vulture
        // object before we can call notify() for it.
        finally 
            {
            try { this.closeClient(); } catch (IOException e2) { ; }
            this.notifyVulture();
            }
        }
    }

