package girard.sc.expt.obj;

import girard.sc.expt.io.obj.ExptComptroller;

import java.io.Serializable;

/**
 * ClientExptInfo: Keeps track of key information about the Experiment
 * and active ExperimentAction for the ClientWindow.
 * <p>
 * Started: 05-14-2000
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class ClientExptInfo implements Serializable
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
 * Unique identifier for the experiment that the client is attached to.
 */
    protected Long             m_exptUID = new Long(-1);
/**
 * The information needed to interact with the active ExperimentAction, 
 * usually a scaled down version of the actual ExperimentAction.
 */ 
    protected Object           m_activeAction;
/**
 * The name of the experiment being run.
 */
    protected String           m_exptName = "NONE";
/**
 * User name of the person running the Experiment.
 */
    protected String           m_supervisor;
/**
 * Total number of subjects in the experiment.
 */
    protected int              m_NumUsers = -1;
/**
 * User number for this user.
 */
    protected int              m_userIndex = -1;
/**
 * true -> Human    false -> Computer
 */
    protected boolean[]        m_HumanUser;
/**
 * Which users have registered.
 */
    protected boolean[]        m_registered;
/**
 * Don't know why this is here, but is to let us know if observers are allowed for this
 * Experiment.
 */
    protected boolean          m_allowObservers;
/**
 * Are we ready to start.
 */
    protected boolean          m_readyToStart = false;
/**
 * Is the Experiment running, useful flag for ExptMessages.
 */
    protected boolean          m_exptRunning = false;
/**
 * Used to tell when an Experiment is being stopped.
 */
    protected boolean          m_exptStopping = false;

/**
 * Creates a new ClientExptInfo object.
 *
 * @param uid The m_exptUID value.
 */
    public ClientExptInfo(long uid)
        {
        m_exptUID = new Long(uid);
        }
/**
 * Creates a new ClientExptInfo object.
 *
 * @param ec The ExptComptroller that this ClientExptInfo object is attached to.
 */
    public ClientExptInfo(ExptComptroller ec)
        {
        m_exptUID = new Long(ec.getExptUID().longValue());
        m_exptName = ec.getExptName();
        m_allowObservers = ec.getAllowObservers();
        m_NumUsers = ec.getNumUsers();
        m_supervisor = ec.getSupervisor();

        setHumanUser(ec.getHumanUser());
        m_registered = new boolean[m_NumUsers];

        for (int i=0;i<m_NumUsers;i++)
            {
            m_registered[i] = ec.getRegistered(i);
            }
        }
/**
 * Creates a new ClientExptInfo object.
 *
 * @param uid The m_exptUID value.
 * @param name The m_exptName.
 * @param users The m_NumUsers.
 * @param sup The m_supervisor.
 */
    public ClientExptInfo(long uid, String name, int users, String sup)
        {
        m_exptUID = new Long(uid);
        m_exptName = name;
        m_NumUsers = users;
        m_supervisor = sup;
        }
/**
 * Creates a new ClientExptInfo object.
 *
 * @param uid The m_exptUID value.
 * @param name The m_exptName.
 * @param users The m_NumUsers.
 * @param sup The m_supervisor.
 */ 
    public ClientExptInfo(Long uid, String name, int users, String sup)
        {
        m_exptUID = uid;
        m_exptName = name;
        m_NumUsers = users;
        m_supervisor = sup;
        }

/**
 * @return Returns the Object, m_activeAction.
 */
    public Object getActiveAction()
        {
        return m_activeAction;
        }
/**
 * @return Returns the value for m_allowObservers.
 */
    public boolean getAllowObservers()
        {
        return m_allowObservers;
        }
/**
 * @return Returns the String, m_exptName.
 */
    public String getExptName()
        {
        return m_exptName;
        }
/**
 * @return Returns the value for m_exptRunning.
 */
    public boolean getExptRunning()
        {
        return m_exptRunning;
        }
/**
 * @return Returns the value for m_exptStopping.
 */
    public boolean getExptStopping()
        {
        return m_exptStopping;
        }
/**
 * @return Returns the Long, m_exptUID.
 */
    public Long getExptUID()
        {
        return m_exptUID;
        }
/**
 * @param index The index in m_HumanUser to get the value from.
 * @return Returns the value of a specified index from m_HumanUser.
 */
    public boolean getHumanUser(int index)
        {
        return m_HumanUser[index];
        }
/**
 * @return Returns the boolean array, m_HumanUser.
 */
    public boolean[] getHumanUser()
        {
        return m_HumanUser;
        }
/**
 * @return Returns the total number of users that are human.
 */
    public int getNumHumans()
        {
        int total = 0;
        
        for (int i=0;i<m_HumanUser.length;i++)
            {
            if (m_HumanUser[i])
                total++;
            }
        return total;
        }
/**
 * @return Returns the total number of users that are computer controlled.
 */
    public int getNumSims()
        {
        int total = 0;
        
        for (int i=0;i<m_HumanUser.length;i++)
            {
            if (!(m_HumanUser[i]))
                total++;
            }
        return total;

        }
/**
 * @return Returns the value of m_NumUsers.
 */
    public int getNumUsers()
        {
        return m_NumUsers;
        }
/**
 * @return Returns the value m_readyToStart.
 */
    public boolean getReadyToStart()
        {
        return m_readyToStart;
        }
/**
 * @return Returns the boolean array m_registered.
 */
    public boolean[] getRegistered()
        {
        return m_registered;
        }
/**
 * @param index The index to get the value from m_registered.
 * @return Returns a value from m_registered based on the supplied index.
 */
    public boolean getRegistered(int index)
        {
        return m_registered[index];
        }
/**
 * @return Returns the String, m_supervisor.
 */
    public String getSupervisor()
        {
        return m_supervisor;
        }
/**
 * @return Returns the value for m_userIndex.
 */
    public int getUserIndex()
        {
        return m_userIndex;
        }

/**
 * Initializes the m_registered variable.
 * Sets any computer positions to true automatically.
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
 * Initializes the m_NumUsers, m_HumanUser, and m_registered variables.
 * All users are set as human and not having registered.
 *
 * @param numUsers The number of clients.
 */
    public void initializeUsers(int numUsers)
        {
        m_NumUsers = numUsers;
        m_HumanUser = new boolean[numUsers];
        m_registered = new boolean[numUsers];

        for (int x=0;x<numUsers;x++)
            {
            m_HumanUser[x] = true;
            m_registered[x] = false;
            }
        }
/**
 * Not sure why this is still here.
 * <br>Left over from Experiment perhaps?
 *
 */
    public boolean sameNumUsers(int value)
        {
        if (m_NumUsers == -1)
            return true;

        if (value == m_NumUsers)
            return true;
        else
            return false;
        }

/**
 * Changes the Object that m_activeAction is set to.
 *
 * @param ea The new Object to set m_activeAction to.
 */
    public void setActiveAction(Object ea)
        {
        m_activeAction = ea;
        }
/**
 * Changes the value of m_allowObservers.
 *
 * @param value The new value to set m_allowObservers to.
 */
    public void setAllowObservers(boolean value)
        {
        m_allowObservers = value;
        }
/**
 * Changes the value of m_exptName.
 *
 * @param name The new value to set m_exptName to.
 */
    public void setExptName(String name)
        {
        m_exptName = name;
        }
/**
 * Changes the value of m_exptRunning.
 *
 * @param value The new value to set m_exptRunning to.
 */
    public void setExptRunning(boolean value)
        {
        m_exptRunning = value;
        }
/**
 * Changes the value of m_exptStopping.
 *
 * @param value The new value to set m_exptStopping to.
 */
    public void setExptStopping(boolean value)
        {
        m_exptStopping = value;
        }
/**
 * Changes the values of the boolean array, m_HumanUser.
 *
 * @param obj The boolean array who's values to set the boolean array, m_HumanUser,
 * indexes to.
 */
    public void setHumanUser(boolean[] obj)
        {
        m_HumanUser = new boolean[obj.length];

        for (int x=0;x<obj.length;x++)
            {
            m_HumanUser[x] = obj[x];
            }
        }
/**
 * Changes the value of a specific index of m_HumanUser.
 *
 * @param value The new value to set the specified index to.
 * @param index The index location in m_HumanUser that is to be changed.
 */
    public void setHumanUser(boolean value, int index)
        {
        m_HumanUser[index] = value;
        }
/**
 * Changes the value of m_NumUsers.
 *
 * @param value The new value to set m_NumUsers to.
 */
    public void setNumUsers(int value)
        {
        m_NumUsers = value;
        }
/**
 * Changes the value of m_readyToStart.
 *
 * @param value The new value to set m_readyToStart to.
 */
    public void setReadyToStart(boolean value)
        {
        m_readyToStart = value;
        }
/**
 * Changes the values of the boolean array, m_registered.
 *
 * @param obj The boolean array who's values to set the boolean array, m_registered,
 * indexes to.
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
 * Changes the value of m_userIndex.
 *
 * @param value The new value to set m_userIndex to.
 */
    public void setUserIndex(int value)
        {
        m_userIndex = value;
        }
    }
