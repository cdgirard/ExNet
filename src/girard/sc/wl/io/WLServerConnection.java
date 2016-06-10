package girard.sc.wl.io;

import girard.sc.io.ServerConnection;
import girard.sc.io.ServerVulture;
import girard.sc.wl.sql.WLQuery;

import java.net.Socket;
import java.util.Hashtable;

// This class is the thread that handles all communication with a client
// It also notifies the Vulture when the connection is dropped.
public abstract class WLServerConnection extends ServerConnection 
    {
    static protected long m_connectionNumber = 0;
    protected long m_securityKey = (Long.valueOf("3938457194759273")).longValue();

    // Initialize the streams and start the thread
    public WLServerConnection(Socket client_socket, ThreadGroup threadgroup, int priority, ServerVulture vulture, WLServer app) 
        {
        // Give the thread a group, a name, and a priority.
        super(client_socket,threadgroup,"Connection-" + m_connectionNumber++,priority,vulture,app);
        }

    public void initializeDBQuery(WLQuery eq)
        {
        Hashtable con  = ((WLServer)this.getSApp()).getDBAddress(eq.getQueryDB());

        eq.setDB(con);
        eq.setWlsc(this);
        }

    // Provide the service.
    public abstract void run();    
    }

