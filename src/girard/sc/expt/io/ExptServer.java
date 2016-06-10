package girard.sc.expt.io;

import girard.sc.wl.io.WLServer;

import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The main server class object for experiments in the ExNet III system.  Keeps
 * a list of all active experiments and spawns new ExptServerConnections as new
 * requests come in.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class ExptServer extends WLServer 
    {
/**
 * The list of active expriments (ExptComptroller class objects) for the ExNet III 
 * at this server.
 *
 * @see girard.sc.expt.io.obj.ExptComptroller
 */
    protected Hashtable m_activeExpts = new Hashtable();
/**
 * Used to generate ids for each experiment that is added to the m_activeExpts list.
 */
    protected long m_exptCounter = 0;
    
/**
 * Exit with an error message, when an exception occurs.
 *
 * @param e The Exception that occured.
 * @param msg The error message associated with the reason for calling fail.
 */
    public static void fail(Exception e, String msg) 
        {
        System.err.println(msg + ": " +  e);
        System.exit(1);
        }

/**
 * Create a ServerSocket to listen for connections on;  start the thread.
 *
 * @param port The port to listen at for connections.
 * @param databases The list of databases that one can access using a WLQuery class object.
 * @see girard.sc.wl.sql.WLQuery
 */
    public ExptServer(int port, Hashtable databases) 
        {
        // Create our server thread with a name.
        super("RegExptServer","Login Thread Group",port,databases);
	
        // Create a window to display our connections in
        // this.setTitle("Experiment Server Status");

        // Start the server listening for connections
        this.start();
        }
    
    public Hashtable getActiveExpts()
        {
        return m_activeExpts;
        }
    public synchronized long getExptUID()
        {
        m_exptCounter++;

        return m_exptCounter;
        }

/**
 * The body of the server thread.  Loop forever, listening for and
 * accepting connections from clients.  For each connection, 
 * create an ExptServerConnection object to handle communication through the
 * new Socket.  When we create a new connection, add it to the
 * Vector of connections.  Note that we use synchronized to lock the Vector of
 * connections.  The Vulture class does the same, so the vulture won't be removing dead 
 * connections while we're adding fresh ones.
 *
 * @see girard.sc.io.ServerVulture
 */
    public void run() 
        {
        try 
            {
            while(m_flag) 
                {
                Socket client_socket = this.getSocket().accept();
                ExptServerConnection c = new ExptServerConnection(client_socket, this.getThreadgroup(), 5, this.getVulture(),this);
                // prevent simultaneous access.
                synchronized (this.getConnections()) 
                    {
                    this.getConnections().addElement(c);
                    this.addToLog(new String("OPENING: "+c.toString()));
                    }
                }
            }
        catch (IOException e) 
            {
            fail(e, "Exception while listening for connections");
            }
        }

/**
 * Shuts down the ExptServer by closing any ExptServerConnections first, then
 * closing the actual ExptServer.
 */
    public void shutdown()
        {
        Enumeration enm = this.getConnections().elements();
        while (enm.hasMoreElements())
            {
            ExptServerConnection eesc = (ExptServerConnection)enm.nextElement();
            eesc.setFlag(false);
            }

        m_flag = false;

        m_log.shutdown();

        try { m_socket.close(); }
        catch (IOException ioe) { }
        }
    } 

