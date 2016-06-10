package girard.sc.expt.io;

import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.io.ServerVulture;
import girard.sc.wl.io.WLServerConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This class is the thread that handles all communication between the experimenter,
 * clients, and observers.  Is assisted by the ExptComptroller class object, which links
 * together all ExptServerConnection for the experimenter with the clients' and 
 * observers' ExptServerConnections. It also notifies the Vulture when the connection
 * is dropped.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 * @see girard.sc.io.obj.ExptComptroller
 * @see girard.sc.io.ServerVulture
 */
public class ExptServerConnection extends WLServerConnection 
    {
/**
 * The ExptComptroller attached to this ExptServerConnection.
 */
    protected ExptComptroller m_ExptIndex = null;
/**
 * -1 For experimenter, 0-X for users, -2 for Observer, -3 for Unknown
 */
    protected int m_userNum = -3;
/**
 * Is the lookup key for a subject or observer.
 */
    protected Object m_data = null;
/**
 * Basically is a pointer to the m_activeExpts var of its ExptServer class.
 *
 * @see girard.sc.expt.io.ExptServer
 */
    protected Hashtable m_activeExpts;
/**
 * Used to stop the thread.
 */
    protected boolean m_flag = true;

/**
 * The constructor.  Initialize the streams and start the thread.
 *
 * @param client_socket The Socket that the input and output streams are attached to.
 * @param threadgoup The threadgroup that this thread is apart of.
 * @param priority What priority to give the thread.
 * @param vulture The ServerVulture for the ExptServerConnection.
 * @param app The ExptServer this ExptServerConnection is attached to.
 */
    public ExptServerConnection(Socket client_socket, ThreadGroup threadgroup, int priority, ServerVulture vulture, ExptServer app) 
        {
        // Give the thread a group, a name, and a priority.
        super(client_socket,threadgroup,priority,vulture,app);

        m_activeExpts = ((ExptServer)this.getSApp()).getActiveExpts();

        // And start the thread up
        this.start();
        }
/**
 * Adds an ExptComptroller to the list of available experiments (m_activeExpts)
 *
 * @param eec The ExptComptroller to add to the list.
 */
    public void addActiveExpt(ExptComptroller eec)
        {
        m_activeExpts.put(eec.getExptUID(),eec);
        }

/**
 * @return Returns the Hashtable, m_activeExpts.
 */
    public Hashtable getActiveExpts()
        {
        return m_activeExpts;
        }
/**
 * Shouldn't be used now, but maybe later when start using
 * application limitations within Exnet III.
 */
    public Hashtable getActiveExpts(int type)
        {
        Hashtable tmp = new Hashtable();

        Enumeration enm = m_activeExpts.elements();
        while (enm.hasMoreElements())
            {
            ExptComptroller ec = (ExptComptroller)enm.nextElement();
            if (-99 == type)
                tmp.put(ec.getExptUID(),ec);
            }
        return tmp;
        }
/**
 * @param loc The Key value for the ExptComptroller we want from m_activeExpts.
 * @return Returns the requested ExptComptroller based on the submitted Key.
 */
 
    public ExptComptroller getActiveExpt(Long loc)
        {
        return (ExptComptroller)m_activeExpts.get(loc);
        }
/**
 * @return Returns the Object, m_data.
 */
    public Object getData()
        {
        return m_data;
        }
/**
 * @return Returns the ExptComptroller, m_ExptIndex.
 */
    public ExptComptroller getExptIndex()
        {
        return m_ExptIndex;
        }
/**
 * @return Returns the value of m_ExptUID for ExptServer that spawned the 
 * ExptServerConnection.
 */
    public synchronized long getExptUID()
        {
        return ((ExptServer)this.getSApp()).getExptUID();
        }
/**
 * @return Returns the value of m_ObserverID contained in the ExptComptroller
 * m_ExptIndex.
 */
    public synchronized int getObserverID()
        {
        return m_ExptIndex.getObserverID();
        }
/**
 * @return Returns the value of m_userNum.
 */
    public int getUserNum()
        {
        return m_userNum;
        }

/**
 * Removes the ExptComptroller, m_ExptIndex, from m_activeExpts.
 */
    public void removeActiveExpt()
        {
        m_activeExpts.remove(m_ExptIndex.getExptUID());
        m_ExptIndex = null;
        }
/**
 * Removes an ExptComptroller from the Hashtable, m_activeExpts.
 * 
 * @param loc The Key associated with the ExptComptroller to be removed.
 */
    public void removeActiveExpt(Long loc)
        {
        m_activeExpts.remove(loc);
        }
    
/**
 * Provides the service. Checks to see if there are any incoming messages.  If there are
 * it checks to make sure the message is valid.  If valid it processes the message by
 * running getExptServerConnectionResoponse.  If there is any response it is sent back.
 * It then goes through and checks its respective outgoing message queue and sends those.
 * Thus, if this is the ExptServerConnection for the Experimenter it checks the outgoing
 * message queue for the Experimenter.
 */
    public void run() 
        {
        int resetCounter = 0;
        int isAliveCounter = 0;

        try
            {
            while (m_flag)
                {
                if (m_in.available() > 0)
                    {
                    m_in.readByte();

                    ExptMessage msg = (ExptMessage)m_in.readObject();
                    ExptMessage out_msg = null;
                    if (msg.containsValidMsg(m_securityKey))
                        {
                        	// -kar-
                        //	if(msg instanceof BEVoteJoinMsg )
                        //	System.out.println("Node "+msg.getArgs().args[0]+" joined: "+msg.getArgs().args[1]);
                        	// -kar-
                        out_msg = msg.getExptServerConnectionResponse(this);
                        }
                    else
                        {
                        Object[] out_args = new Object[1];
                        out_args[0] = new String("Illegal User "+msg);
                        out_msg = new ExptErrorMsg(out_args);
                        }
                    if (out_msg != null)
                        {
                        if (resetCounter > 10)
                            {
                            m_out.reset();
                            resetCounter = 0;
                            }
                        m_out.write(1);  // To let the message listener know an object is coming.
                        m_out.writeObject(out_msg);
                        m_out.flush();
                        resetCounter++;
                        }
                    }

                if ((m_ExptIndex != null) && (m_userNum > -1))
                    {
                    if (!m_activeExpts.containsKey(m_ExptIndex.getExptUID()))
                        {
                        m_flag = false;
                        }
                    else
                        {
                        while (m_ExptIndex.getNumUserMessages(m_userNum) > 0)
                            {
                            if (resetCounter > 10)
                                {
                                m_out.reset();
                                resetCounter = 0;
                                }
                            m_out.write(1);
                            m_out.writeObject(m_ExptIndex.removeUserMessage(m_userNum));
                            m_out.flush();
                            resetCounter++;
                            }
                        }
                    }
                if ((m_ExptIndex != null) && (m_userNum == ExptComptroller.EXPERIMENTER))
                    {
                    while (m_ExptIndex.getNumServerMessages() > 0)
                        {
                        if (resetCounter > 10)
                            {
                            m_out.reset();
                            resetCounter = 0;
                            }
                        m_out.write(1);
                        m_out.writeObject(m_ExptIndex.removeServerMessage());
                        m_out.flush();
                        resetCounter++;
                        }
                    }
                if ((m_ExptIndex != null) && (m_userNum == ExptComptroller.OBSERVER))
                    {
                    if (!m_activeExpts.containsKey(m_ExptIndex.getExptUID()))
                        {
                        m_flag = false;
                        }
                    else
                        {
                        while (m_ExptIndex.getNumObserverMessages(m_data) > 0)
                            {
                            if (resetCounter > 10)
                                {
                                m_out.reset();
                                resetCounter = 0;
                                }
                            m_out.write(1);
                            m_out.writeObject(m_ExptIndex.removeObserverMessage(m_data));
                            m_out.flush();
                            resetCounter++;
                            }
                        }
                    }
                isAliveCounter++;
                if (isAliveCounter > 50)  // Check every 5 seconds.
                    {
                    m_out.write(1);
                    m_out.writeObject(new ExptMessage(null)); // Simple basic message to test the line.
                    m_out.flush();
                    isAliveCounter = 0;
                    }
                try { this.sleep(100); }
                catch (InterruptedException e) { System.err.println(e); }
                }
            }
        catch (IOException ioe) { addToLog("Error:"+ioe+", going down: "+this); }
        catch (NullPointerException npe) { addToLog("Error:"+npe+", going down: "+this);  }
        catch (ClassNotFoundException cnfe) { addToLog("Error:"+cnfe+", going down: "+this); }

        // When we're done, for whatever reason, be sure to close
        // the socket, and to notify the Vulture object.  Note that
        // we have to use synchronized first to lock the vulture
        // object before we can call notify() for it.

        HandelLostLink();
        m_activeExpts = null;
        try { this.closeClient(); } catch (IOException e2) { ; }
        this.notifyVulture();
        }

/**
 * Sets the Object m_data to a new Object.
 *
 * @param obj The new Object to set m_data to.
 */
    public void setData(Object obj)
        {
        m_data = obj;
        }
/**
 * Sets m_ExptIndex to a new ExptComptroller.
 *
 * @param eec The new ExptComptroller to set m_ExptIndex to.
 */
    public void setExptIndex(ExptComptroller eec)
        {
        m_ExptIndex = eec;
        }
/**
 * Sets m_flag to a new value.
 *
 * @param value The new value to set m_flag to.
 */
    public void setFlag(boolean value)
        {
        m_flag = value;
        }
/**
 * Sets m_userNum to a new value.
 *
 * @param value The new value to set m_userNum to.
 */
    public void setUserNum(int value)
        {
        m_userNum = value;
        }

/**
 * Network connection broken remember to remove Experiment from list or unregister
 * registered users.
 */
    private void HandelLostLink()
        {
        
   // System.out.println("HLL");
        // If it was a user that registered an experiment, then unregister it.
        if ((m_ExptIndex != null) && (m_userNum == ExptComptroller.EXPERIMENTER))
            {
            synchronized (m_ExptIndex) 
                {
// System.out.println("Loosing Experimenter");
                removeActiveExpt(m_ExptIndex.getExptUID());
                }
            }
   // If it was a user that had joined an experiment then unjoin them.
        if ((m_ExptIndex != null) && (m_userNum > -1))
            {
// System.out.println("Unjoin Client");
            m_ExptIndex.removeSubject(m_userNum,m_data);
            }
   // If it was an observer that had joined an experiment then unjoin them.
        if ((m_ExptIndex != null) && (m_userNum == ExptComptroller.OBSERVER))
            {
// System.out.println("Unjoin Observer");
            // Need to remove the observer from the observer list.
            m_ExptIndex.removeObserver(m_data);
            }

        m_ExptIndex = null;
        }
    }