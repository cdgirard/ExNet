//
// General class for 
//

package girard.sc.io;

import girard.sc.io.msg.TCPMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetObjectStream
    {
    protected Socket m_s;
    protected ObjectInputStream m_in;
    protected ObjectOutputStream m_out;
/**
 * Did the NetObjectStream get setup successfully?
 */
    protected boolean m_flag = true;

    public NetObjectStream(String host, int port)
        {
        try 
            {
            // Create a socket to communicate to the specified host and port
            m_s = new Socket(host, port);

            m_out = new ObjectOutputStream(m_s.getOutputStream());
            m_out.flush();

            m_in = new ObjectInputStream(m_s.getInputStream());
            }
        catch (IOException e) 
            {
            m_flag = false;
            System.err.println(e);
            }
        }

    public boolean getFlag()
        {
        return m_flag;
        }
    public Object getNextMessage() throws IOException, ClassNotFoundException
        {
        Object obj;

        if (m_in == null)
            {
        	System.err.println("returning null in the getNextMessage");
            return null;
            }
        obj = m_in.readObject();
        return obj;
        }

    public void retry(String host, int port)
        {
        try 
            {
            // Create a socket to communicate to the specified host and port
            m_s = new Socket(host, port);

            m_out = new ObjectOutputStream(m_s.getOutputStream());
            m_out.flush();

            m_in = new ObjectInputStream(m_s.getInputStream());
            }
        catch (IOException e) 
            {
            m_flag = false;
            System.err.println(e);
            }
        }

    public void sendMessage(TCPMessage msg) throws IOException
        {
        if (m_out != null)
            {
            m_out.writeObject(msg);
            m_out.flush();
            }
        }

    public void showError(TCPMessage err)
        {
        System.err.println((String)(err.getArgs()[0]));
        }

    // Go and close down the port connections.
    public void close()
        {
        try
            {
            if (m_in != null)
                m_in.close();
            }
        catch(Exception ioe)
            {
            ioe.printStackTrace();
            }
        try
            {
            if (m_out != null)
                m_out.close();
            }
        catch(Exception ioe)
            {
            ioe.printStackTrace();
            }
        try 
            {
            if (m_s != null) 
                m_s.close();
            }
        catch (Exception e2) 
            {
            e2.printStackTrace();
            }
        }
    }
