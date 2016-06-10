package girard.sc.expt.io;

import girard.sc.be.io.msg.BEVoteJoinResultMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.ServerDownMsg;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.ListenRequest;
import girard.sc.io.MessageListener;
import girard.sc.io.msg.TCPMessage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OptionalDataException;
import java.util.Enumeration;
import java.util.Hashtable;
//-kar-
/**
 * Is an extension of the MessageListener class that is modified to allow for a
 * simple way to send and receive messages from an ExptServerConnection.
 *
 * @author Dudley Girard
 * @version ExNet III 3.2
 * @since JDK1.1
 */

public class ExptMessageListener extends MessageListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * for initializing messages before they are sent.
 */
    protected ExptOverlord m_EOApp;

/**
 * The constructor for the ExptMessageListener.
 *
 * @param host The host name or IP address of the computer where the ExptServer is running.
 * @param port The port address that the ExptServer is listening at.
 * @param eo The active ExptOverlord class object.
 */
    public ExptMessageListener(String host, int port, ExptOverlord eo)
        { 
        super(host,port);

        m_EOApp = eo;
        }
/**
 * An extension of the addListenRequest function from MessageListener.  Runs the 
 * initializeWLMessage function if the message is of type ExptMessage.  Then 
 * creates a ListenRequest thread for the message.
 * Lastly it adds the ListenRequest thread to the m_listenRequests table.
 * 
 *
 * @param req The message to be added to the m_listenRequests table.
 * @param wait How long to wait inbetween sendings of the message.
 * @return The id for where the ListenRequest was placed in the m_listenRequests table.
 * @see girard.sc.io.ListenRequest
 */
    public int addListenRequest(TCPMessage req, int wait)
        {
        int reqNum;
        ListenRequest lr;

        if (req instanceof ExptMessage)
            {
            m_EOApp.initializeWLMessage((ExptMessage)req);
            }

        synchronized (m_listenRequests)
            {
            lr = new ListenRequest(this,req,wait);
            m_listenRequests.put(new Integer(m_listenReqCounter),lr);
            reqNum = m_listenReqCounter;
            m_listenReqCounter++;
            lr.start();
            }
 
        return reqNum;
        }

/**
 * What the ExptMessageListener does while it is running.  It checks to see if there
 * is an incomming message.  If there is it retreives it, then if there are any ActionListeners
 * attached it notifies them of the new message.  If there are no attached actionListeners
 * it adds the message to the m_incoming Vector.  After checking for incomming messages
 * it checks to see if there are any messages in the m_outgoing queue and if so it sends
 * them on their way, clearing the queue as it does so.
 */
    public void run() 
        {
        int i;
        int resetCounter = 0;

        m_cleanUpFlag = true;

        try 
            {
            while (m_flag)
                {
                synchronized (m_outgoing)
                    {
                    while ((m_outgoing.size() > 0) && (m_flag))
                        {
                        if (resetCounter > 10)
                            {
                            m_out.reset();
                            resetCounter = 0;
                            }
                        m_out.write(1);
                        m_out.writeObject(m_outgoing.elementAt(0));
                        m_out.flush();
                        m_outgoing.removeElementAt(0);
                        resetCounter++; 
                        }
                    }

                synchronized (m_in)
                    {
                    boolean in_flag = true;

                    while ((in_flag) && (m_flag))
                        {
                        try
                            {
                            // m_s.setSoTimeout(50);
                            if (m_in.available() > 0)
                                {
                                m_in.readByte();
                                Object obj = m_in.readObject();
								// -kar-
								if(obj instanceof BEVoteJoinResultMsg){
									
								Hashtable h = (Hashtable)(((ExptMessage)obj).getArgs()[0]);
                    			Enumeration enm = h.keys();
                    			Integer ii = new Integer(-1);
                    			while (enm.hasMoreElements()){
                    			ii = (Integer)enm.nextElement();
                    			System.out.println("Received BEVoteJoinResultMsg: Node "+ii+" voted "+(Boolean)h.get(ii))	;
                    			}
  								}
                        		//System.out.println("Client "+m_EOApp.getUserID()+": MsgClass: "+obj.getClass());
                        		// -kar-
                                if (m_listeners > 0)
                                    m_actionListener.actionPerformed(new ActionEvent(obj,ActionEvent.ACTION_PERFORMED,"Message"));
                                else
                                    m_incoming.addElement(obj);
                                }
                            else
                                {
                                in_flag = false;
                                }
                            }
                        catch (InterruptedIOException e) { in_flag = false; }
                        catch (OptionalDataException ode) {System.err.println("ODE: Is this bad?"); m_in.skip(ode.length); }
                        }
                    }

                try { this.sleep(100); }
                catch (InterruptedException e) { System.err.println(e); }
                }
            }
        catch (IOException ioe) 
            {
            if (m_listeners > 0)
                m_actionListener.actionPerformed(new ActionEvent(new ServerDownMsg(null),ActionEvent.ACTION_PERFORMED,"Message"));
            System.err.println(""+m_EOApp.getUserID());System.err.println(ioe);
            ioe.printStackTrace();
            }
        catch (ClassNotFoundException cnfe)
            {
            if (m_listeners > 0)
                m_actionListener.actionPerformed(new ActionEvent(new ServerDownMsg(null),ActionEvent.ACTION_PERFORMED,"Message"));
            System.err.println(""+m_EOApp.getUserID());System.err.println(cnfe);
            cnfe.printStackTrace();
            }

        // Always be sure to close the socket
        try { if (m_s != null) m_s.close(); } catch (IOException e2) { ; }

        m_cleanUpFlag = false;
        }

/**
 * An extension of the sendMessage function for the MessageListener class.  If the
 * message is of type ExptMessage it runs the initializeWLMessage on it.  Then it
 * adds the message to m_outgoing.
 */
    public void sendMessage(TCPMessage msg)
        {
        if (msg instanceof ExptMessage)
            {
            m_EOApp.initializeWLMessage((ExptMessage)msg);
            }

        synchronized(m_outgoing)
            {
            m_outgoing.addElement(msg);
            }
        }
    }