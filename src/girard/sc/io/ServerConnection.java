package girard.sc.io;

// This example is from the book _Java in a Nutshell_ by David Flanagan.
// Written by David Flanagan.  Copyright (c) 1996 O'Reilly & Associates.
// You may study, use, modify, and distribute this example for any purpose.
// This example is provided WITHOUT WARRANTY either expressed or implied.

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


// This class is the thread that handles all communication with a client
// It also notifies the Vulture when the connection is dropped.
public abstract class ServerConnection extends Thread 
    {
    protected Socket m_client;
    protected ServerVulture m_vulture;
    protected ObjectInputStream m_in;
    protected ObjectOutputStream m_out;
    protected Server m_SApp;

    // Initialize the streams and start the thread
    public ServerConnection(Socket client_socket, ThreadGroup threadgroup, String threadname, int priority, ServerVulture vulture, Server app) 
        {
        // Give the thread a group, a name, and a priority.
        super(threadgroup, threadname);
        this.setPriority(priority);
// System.err.println(""+threadname+": "+this.getPriority()+" : "+Thread.MAX_PRIORITY+" : "+Thread.MIN_PRIORITY);
        // Save our other arguments away
        m_client = client_socket;
        m_vulture = vulture;
        m_SApp = app;
        // Create the streams
        try 
            { 
            m_in = new ObjectInputStream(m_client.getInputStream());
            m_out = new ObjectOutputStream(m_client.getOutputStream());
            m_out.flush();
            }
        catch (IOException e) 
            {
            try { m_client.close(); } catch (IOException e2) { ; }
            System.err.println("Exception while getting socket streams: " + e);
            return;
            }
        }
    
    public void addToLog(String str)
        {
        m_SApp.addToLog(str);
        }
    public void addToLog(Exception e)
        {
        m_SApp.addToLog(e);
        }

    public void closeClient() throws IOException
        {
        m_client.close();
        }

    public Socket getClient()
        {
        return m_client;
        }
    protected Server getSApp()
        {
        return m_SApp;
        }

    public void notifyVulture()
       {
       synchronized (m_vulture) { m_vulture.notify(); }
       }

    // Provide the service.
    public abstract void run();

    // This method returns the string representation of the Connection.
    // This is the string that will appear in the GUI List.
    public String toString() 
        {
        return this.getName() + " connected to: " 
            + m_client.getInetAddress().getHostName()
            + ":" + m_client.getPort();
        }
    }

