package girard.sc.expt.io.obj;

import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.ExptUserData;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class is used to manage who has connected to an experiment
 * and where messages should be sent.  Created by a RegisterExptReqMsg.
 * Will normally be attached to multiple ExptServerConnection class objects
 * depending on how many subjects and observers there are.
 * <p>
 * Started: 8-30-2000
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 * @see girard.sc.expt.io.msg.RegisterExptReqMsg
 */

public class ExptComptroller
    {
/**
 * The designation for an Experimenter, any subjects are designated by their user number.
 */
    public static final int EXPERIMENTER = -1;
/**
 * The designation for an Observer.
 */
    public static final int OBSERVER     = -2;
/**
 * If someone is not a Subject, Experimenter or Observer.
 */
    public static final int UNKNOWN      = -3;
/**
 * Unique identifier for the experiment.
 */
    protected Long      m_exptUID = new Long(-1);
/**
 * User name of the person running the Experiment.
 */
    protected String    m_supervisor;
/**
 * Name of the Experiment, usually given by the person that made it.
 */ 
    protected String    m_exptName;
/**
 * Total number of subjects(sims and humans).
 */
    protected int	      m_numUsers;
/**
 * true -> Human    false -> Computer
 */
    protected boolean[] m_HumanUser;
/**
 * Password for each user Position.
 */ 
    protected String[]  m_password;
/**
 * Password for the Observer position.
 */
    protected String    m_observerPass;
/** 
 * To allow (true) observers or not (false).
 */
    protected boolean   m_allowObservers = true;
/**
 * If that user position has been registered.
 */  
    protected boolean[] m_registered;
/**
 * Is the list of subjects (humans only) connected to the ExptComptroller,
 * uses their user index as the key.
 */
    protected Hashtable m_Subjects = new Hashtable();
/** 
 * Is the list of ObserverComptrollers connected to the ExptComptroller, ObserverCounter 
 * gives each Observer a new unique key.
 */
    protected Hashtable m_Observers = new Hashtable();
/**
 * Used to give each Observer a unique identifier.
 */
    protected int       m_observerCount = 0;
/**
 * Is a Vector of Vectors that keeps a list of what messages still need to be sent to Subjects
 * and in what order to send them.
 */
    protected Vector[]  m_clientMessages;
/** 
 * Messages to be sent to the experimenter station.
 */
    protected Vector    m_serverMessages = new Vector();

    
/**
 * Creates an ExptComptroller.
 *
 * @param value The m_exptUID for the ExptComptroller.
 */
    public ExptComptroller(long value)
        {
        m_exptUID = new Long(value);
        m_numUsers = 0;
        }
/**
 * Creates an ExptComptroller.
 *
 * @param value The m_exptUID for the ExptComptroller.
 * @param name The m_exptName for the ExptComptroller.
 * @param supervisor The m_supervisor for the ExptComptroller.
 * @param n The number of users (m_numUsers) for th ExptComptroller.
 */
    public ExptComptroller(long value, String name, String supervisor, int n)
        {
        m_exptUID = new Long(value);
        m_exptName = name;
        m_supervisor = supervisor;
        setNumUsers(n);
        }
/**
 * Adds an ObserverComptroller to the list of m_Observers.  The id for the
 * ObserverComptroller is set before it is passed in.
 *
 * @param oc The ObserverComptroller to add.
 */
    public void addObserver(ObserverComptroller oc)
        {
        m_Observers.put(new Integer(oc.getID()),oc);
        }
/**
 * Adds a message to be sent to a specifice Observer based on which key value is passed it.
 * This message is not immediately sent, merely added to the queue of messages to be 
 * sent when the ExptServerConnection gets to it.
 *
 * @param msg The ExptMessage to be sent to the Observer.
 * @param key The id of the Observer to send the message to.
 */
    public void addObserverMessage(ExptMessage msg, Integer key)
        {
        if (m_Observers.size() == 0) // There are no observers so why bother sending them a message.
            return;
// System.err.println("Adding an Observer Message - "+msg);
// System.err.flush();
        synchronized (m_Observers)
            {
            if (m_Observers.containsKey(key))
                {
                ObserverComptroller oc = (ObserverComptroller)m_Observers.get(key);
                oc.addMessage(msg);
                }
            }
        }
/**
 * Adds a human subject to the list of subjects.
 *
 * @param index The user index of the subject being added.
 * @param wlud Information about the human subject.
 */
    public void addSubject(int index, ExptUserData wlud)
        {
        m_Subjects.put(new Integer(index),wlud);
        }
/**
 * Adds an ExptMessage to be sent to a specific user.  This message is not immediately
 * sent, merely added to the queue of messages to be sent when the ExptServerConnection
 * gets to it.
 *
 * @param msg The ExptMessage to be sent to the Subject.
 * @param loc The user number of the Subject to send the message to.
 */
    public void addUserMessage(ExptMessage msg, int loc)
        {
// System.err.println("Adding a User Message - "+msg);
// System.err.flush();
        synchronized (m_clientMessages[loc])
            {
            m_clientMessages[loc].addElement(msg);
            }
        }
/**
 * Adds an ExptMessage to be sent to the Experimenter.  This message is not immediately
 * sent, merely added to the queue of messages to be sent when the ExptServerConnection
 * gets to it.
 *
 * @param msg The ExptMessage to be sent to the Experimenter.
 */
    public void addServerMessage(ExptMessage msg)
        {
// System.err.println("Adding a Server Message - "+msg);
// System.err.flush();
        synchronized (m_serverMessages)
            {
            m_serverMessages.addElement(msg);
            }
        }

/**
 * Checks to see if everyone is registered.
 *
 * @return Returns tue if every position has been filled, false otherwise.
 */
    public boolean allRegistered()
        {
        for (int x=0;x<m_numUsers;x++)
            {
            if (!m_registered[x])
                {
                return false;
                }
            }
        return true;
        }

/**
 * @return Returns the value of m_allowObservers.
 */
    public boolean getAllowObservers()
        {
        return m_allowObservers;
        }
/**
 * @return Returns the value of m_exptUID.
 */
    public Long getExptUID()
        {
        return m_exptUID;
        }
/**
 * @return Returns the value of m_exptName.
 */
    public String getExptName()
        {
        return m_exptName;
        }
/**
 * @return Returns the boolean arry, m_HumanUser.
 */
    public boolean[] getHumanUser()
        {
        return m_HumanUser;
        }
/**
 * Returns the number of messages that are queued up to be sent to a specific Observer.
 *
 * @param key The Obeserver we are asking about.
 * @return Returns the number of messages in his or her queue.
 */
    public int getNumObserverMessages(Object key)
        {
        ObserverComptroller oc = (ObserverComptroller)m_Observers.get(key);

        return oc.getMessages().size();
        }
/**
 * @return Returns the value of m_numUsers.
 */
    public int getNumUsers()
        {
        return m_numUsers;
        }
/**
 * Returns the number of messages that are queued up to be sent to a specific Subject.
 *
 * @param loc The Subject we are asking about.
 * @return Returns the number of messages in his or her queue.
 */
    public int getNumUserMessages(int loc)
        {
        return m_clientMessages[loc].size();
        }
/**
 * @return Returns the number of messages stored in the Vector m_serverMessages.
 */
    public int getNumServerMessages()
        {
        return m_serverMessages.size();
        }
/**
 * Increments m_observerCount, then returns its new value.
 *
 * @return Returns the value of m_observerCount after incrementing it by 1.
 */
    public synchronized int getObserverID()
        {
        m_observerCount++;
 
        return m_observerCount;
        }
/**
 * @return Returns the value of m_observerPass.
 */
    public String getObserverPass()
        {
        return m_observerPass;
        }
/**
 * @return Returns the Hashtable, m_Observers.
 */
    public Hashtable getObservers()
        {
        return m_Observers;
        }
/**
 * @param loc The index in m_password to get the String from.
 * @return Returns the password at index loc from the String array m_password.
 */
    public String getPassword(int loc)
        {
        return m_password[loc];
        }
/**
 * @return Returns the boolean array, m_registered.
 */
    public boolean[] getRegistered()
        {
        return m_registered;
        }
/**
 * @param loc The index in m_registered to get the boolean value from.
 * @return Returns the value at index loc from the boolean array m_registered.
 */
    public boolean getRegistered(int loc)
        {
        return m_registered[loc];
        }
/**
 * @return Returns the Vector, m_serverMessages.
 */
    public Vector getServerMessages()
        {
        return m_serverMessages;
        }
/**
 * @return Returns the Hashtable, m_Subjects.
 */
    public Hashtable getSubjects()
        {
        return m_Subjects;
        }
/**
 * @return Returns the value of m_supervisor.
 */
    public String getSupervisor()
        {
        return m_supervisor;
        }
/**
 * @param loc The index in m_clientMessages to get the Vector of ExptMessages.
 * @return Returns a Vector of messages for a specific client given by the variable
 * loc.
 */
    public Vector getUserMessages(int loc)
        {
        return m_clientMessages[loc];
        }

/**
 * Initializes the m_registered variable, done when the ExptComptroller is first created.
 * Sets any computer positions to true automatically to make sure no human positions can
 * accidentally take their spot.
 *
 */
    public void initializeRegistered()
        {
        for (int x=0;x<getNumUsers();x++)
            {
            if (m_HumanUser[x])
                m_registered[x] = false;
            else
                m_registered[x] = true;
            }
        }

/**
 * Removes an ObserverComptroller from m_Observers based on the supplied key.
 * I'm sure there's a perfectly good reason as to why I have it pass in an
 * Object instead of an Integer which the Key is suppose to be.
 *
 * @param obj The Key of the ObserverComptroller we want removed.
 */
    public void removeObserver(Object obj)
        {
        m_Observers.remove(obj);
        }
/**
 * Takes a message off the queue of a specific Observer.  The removed message is
 * returned.
 *
 * @param obj The Observer to get the message from.
 * @return Returns the message to be sent.
 */
    public Object removeObserverMessage(Object obj)
        {
        Object msg = null;

        synchronized (m_Observers)
            {
            if (m_Observers.containsKey(obj))
                {
                ObserverComptroller oc = (ObserverComptroller)m_Observers.get(obj);
                if (oc.getMessages().size() > 0)
                    {
                    msg = oc.getMessage(0);
                    oc.removeMessage(0);
                    }
                }
            }
        return msg;
        }
/**
 * Takes a message off the queue for the Experimenter messages.  The removed message is
 * returned.
 *
 * @return Returns the message to be sent.
 */
    public Object removeServerMessage()
        {
        Object msg = null;

        synchronized (m_serverMessages)
            {
            if (m_serverMessages.size() > 0)
                {
                msg = m_serverMessages.elementAt(0);
                m_serverMessages.removeElementAt(0);
                }
            }

        return msg;
        }
/**
 * Removes a subject from the ExptComptroller.
 *
 * @param index The user num of the subject to be removed.
 * @param key The key index of the subject entry in m_Subjects.
 */
    public void removeSubject(int index, Object key)
        {
        setRegistered(false,index);
        m_Subjects.remove(key);
        }
/**
 * Takes a message off the queue of a specific Subject.  The removed message is
 * returned.
 *
 * @param loc The Subject to get the message from.
 * @return Returns the message to be sent.
 */
    public Object removeUserMessage(int loc)
        {
        Object msg = null;

        synchronized (m_clientMessages[loc])
            {
            if (m_clientMessages[loc].size() > 0)
                {
                msg = m_clientMessages[loc].elementAt(0);
                m_clientMessages[loc].removeElementAt(0);
                }
            }

        return msg;
        }

/**
 * Adds a specific message to all Observer's message queues.
 *
 * @param msg The ExptMessage to be sent to the Observers.
 */
    public void sendToAllObservers(ExptMessage msg)
        {
        if (m_Observers.size() == 0) // There are no observers so why bother sending them a message.
            return;

        synchronized (m_Observers)
            {
            Enumeration enm = m_Observers.keys();
            while (enm.hasMoreElements())
                {
                Integer key = (Integer)enm.nextElement();
                addObserverMessage(msg,key);
                }
            }
        }
/**
 * Adds a specific message to all Subject's message queues.  Sends messages only to
 * Subject positions that have been filled (m_registered = true).
 *
 * @param msg The ExptMessage to be sent to the Observers.
 */ 
    public void sendToAllUsers(ExptMessage msg)
        {
        for (int i=0;i<m_numUsers;i++)
            {
/* If that user is registered send em a message, otherwise shouldn't
   matter as no one to receive the message. 
*/ 
            if (m_registered[i])  
                addUserMessage(msg,i);
            }
        }

/**
 * Changes the value of m_allowObservers.
 *
 * @param value The value to set m_allowObservers to.
 */
    public void setAllowObservers(boolean value)
        {
        m_allowObservers = value;
        }
/**
 * Changes the value of m_exptName.
 *
 * @param str The value to set m_exptName to.
 */
    public void setExptName(String str)
        {
        m_exptName = str;
        }
/**
 * Changes the value of m_supervisor.
 *
 * @param str The value to set m_supervisor to.
 */
    public void setExptSupervisor(String str)
        {
        m_supervisor = str;
        }
/**
 * Updates the boolean array, m_HumanUser.  Creates a new boolean array for m_HumanUser
 * and then sets its indexes equal to the same values at the indexes of the passed in
 * boolean array.
 *
 * @param values The new boolean array to update m_HumanUser with.
 */
    public void setHumanUser(boolean[] values)
        {
        m_HumanUser = new boolean[values.length];

        for (int x=0;x<values.length;x++)
            {
            m_HumanUser[x] = values[x];
            }
        }
/**
 * Sets the value for m_numUsers.  This in turn causes m_password, m_registered, and
 * m_clentMessages to be reinstaniated as arrays of their given type of the size equal to
 * the new value for m_numUsers.
 *
 * @param value The new value for m_numUsers.
 */
    public void setNumUsers(int value)
        {
        int i;

        m_numUsers = value;
        m_password = new String[value];
        m_registered = new boolean[value];
        m_clientMessages = new Vector[value];
        for (i=0;i<value;i++)
            {
            m_clientMessages[i] = new Vector();
            }
        }
/**
 * Sets the value of m_observerPass.  If the passed in value is null then m_observerPass
 * is set to "".
 *
 * @param str The new value to set m_observerPass to.
 */
    public void setObserverPass(String str)
        {
        if (str != null)
            m_observerPass = str;
        else
            m_observerPass = new String("");
        }
/**
 * Updates the String array, m_password.  Creates a new String array for m_password
 * and then sets its indexes equal to the same values at the indexes of the passed in
 * String array.
 *
 * @param pass The new String array to update m_password with.
 */
    public void setPassword(String[] pass)
        {
        m_password = new String[pass.length];
 
        for (int x=0;x<pass.length;x++)
            {
            m_password[x] = pass[x];
            }
        }
/**
 * Changes the value of a specific index in m_password.
 *
 * @param pass The new value to set the specified index of m_password to.
 * @param loc The index of m_password to assign a new value to.
 */
    public void setPassword(String pass, int loc)
        {
        if (loc < m_numUsers)
            {
            m_password[loc] = pass;
            }
        }
/**
 * Updates the boolean array, m_registered.  Creates a new boolean array for m_registered
 * and then sets its indexes equal to the same values at the indexes of the passed in
 * boolean array.
 *
 * @param values The new boolean array to update m_registered with.
 */
    public void setRegistered(boolean[] values)
        {
        m_registered = new boolean[values.length];

        for (int x=0;x<values.length;x++)
            {
            m_registered[x] = values[x];
            }
        }
/**
 * Changes the value of a specific index in m_registered.
 *
 * @param value The new value to set the specified index of m_registered to.
 * @param loc The index of m_registered to assign a new value to.
 */
    public void setRegistered(boolean value, int loc)
        {
        if (loc < m_numUsers)
            {
            m_registered[loc] = value;
            }
        }
/**
 * Sets m_supervisor to a new value.
 *
 * @param value The new value to set m_supervisor to.
 */
    public void setSupervisor(String value)
        {
        m_supervisor = value;
        }

/**
 * Checks to see if its the proper password to a user position.
 *
 * @param user The user position to check the password with.
 * @param pass The password to be compared to the actual password.
 * @return Returns true if the passwords match, false otherwise.
 */
    public boolean validLogin(int user, String pass)
        {
        if ((user != -1) && (user < m_numUsers))
            {
            if (m_registered[user])
                return false;
            if (m_password[user].equals(pass))
                return true;
            }
        return false;
        }
    }