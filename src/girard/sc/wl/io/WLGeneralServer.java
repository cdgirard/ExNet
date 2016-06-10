package girard.sc.wl.io;

import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;

public class WLGeneralServer extends WLServer
    {
    
    // Create a ServerSocket to listen for connections on;  start the thread.
    public WLGeneralServer(int port, Hashtable databases) 
        {
        // Create our server thread with a name.
        super("WLGeneralServer","General Thread Group",port,databases);

        // Create a window to display our connections in
        // this.setTitle("Web-Lab General Server Status");

        // Start the server listening for connections
        this.start();
        }
    
    // The body of the server thread.  Loop forever, listening for and
    // accepting connections from clients.  For each connection, 
    // create a Connection object to handle communication through the
    // new Socket.  When we create a new connection, add it to the
    // Vector of connections, and display it in the List.  Note that we
    // use synchronized to lock the Vector of connections.  The Vulture
    // class does the same, so the vulture won't be removing dead 
    // connections while we're adding fresh ones.
    public void run() 
        {
        try 
            {
            while(m_flag)
                {
                Socket clientSocket = this.getSocket().accept();
                WLGeneralServerConnection c = new WLGeneralServerConnection(clientSocket, this.getThreadgroup(), 5, this.getVulture(), this);
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
    } 

