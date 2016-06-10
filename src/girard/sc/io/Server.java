package girard.sc.io;

// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.ServerSocket;
import java.util.Vector;

public abstract class Server extends Thread 
    {
    protected int m_port;
    protected ServerSocket m_socket;
    protected ThreadGroup m_threadgroup;
    protected Vector m_connections;
    protected ServerVulture m_vulture;

    protected ActivityLogger m_log;

    // Exit with an error message, when an exception occurs.
    public static void fail(Exception e, String msg) 
        {
        System.err.println(msg + ": " +  e);
        }
    
    // Create a ServerSocket to listen for connections on;  start the thread.
    public Server(String Sname, String TGname, int port) 
        {
     // Create our server thread with a name.
        super(Sname);
        setPriority(5);
// System.err.println(""+Sname+": "+this.getPriority()+" : "+Thread.MAX_PRIORITY+" : "+Thread.MIN_PRIORITY);
        m_port = port;
        try { m_socket = new ServerSocket(m_port); }
        catch (IOException e) { fail(e, "Exception creating server socket"); }

     // Create a threadgroup for our connections
        m_threadgroup = new ThreadGroup(TGname);

        m_log = new ActivityLogger(Sname);

    // Initialize a vector to store our connections in
        m_connections = new Vector();

    // Create a Vulture thread to wait for other threads to die.
    // It starts itself automatically.
        m_vulture = new ServerVulture(this);
        }

    public synchronized void addToLog(String str)
        {
        m_log.addMessage(str);
        }
    public synchronized void addToLog(Exception e)
        {
        m_log.addMessage(e);
        }

    public Vector getConnections()
        {
        return m_connections;
        }
    public ServerSocket getSocket()
        {
        return m_socket;
        }
    public ThreadGroup getThreadgroup()
        {
        return m_threadgroup;
        }
    public ServerVulture getVulture()
        {
        return m_vulture;
        }  

    public abstract void run();

    /* public void setTitle(String str)
        {
        m_title.setText(str);
        } */

    public void shutdown()
        {
        m_log.shutdown();
        try { m_socket.close(); }
        catch (IOException ioe) { }
        }
    } 

