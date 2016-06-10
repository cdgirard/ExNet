package girard.sc.expt.obj;

import girard.sc.expt.io.obj.ExptComptroller;

import java.io.Serializable;

/**
 * ObserverExptInfo: Stores all the information that an observer needs
 * to watch the Experiment.
 * <p>
 * <br>Started: 05-14-2000
 * <br>Modified: 4-30-2001
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1   
 */

public class ObserverExptInfo implements Serializable
    {
/**
 * The id for the observer, it is assigned by the ExptComptroller when the
 * observer connects to the experiment.
 *
 * @see girard.sc.expt.io.obj.ExptComptroller
 */
    protected int       m_observerID = -1;
/**
 * The unique identifier for the experiment, is the same as the value of m_exptUID 
 * from the ExptComptroller.
 *
 * @see girard.sc.expt.io.obj.ExptComptroller#m_exptUID
 */ 
    protected Long      m_exptUID = new Long(-1);
/**
 * The information needed to observe the active ExperimentAction, usually a scaled
 * down version of the actual ExperimentAction.
 */ 
    protected Object    m_activeAction;
/**
 * The name of the experiment being run.
 */
    protected String    m_exptName = "NONE";
/**
 * User name of the person running the Experiment.
 */
    protected String    m_supervisor;
/**
 * Total number of subjects in the experiment.
 */
    protected int       m_NumUsers = -1;
/**
 * true -> Human    false -> Computer
 */
    protected boolean[] m_HumanUser;
/**
 * If that user position has been registered 
 */
    protected boolean[] m_registered;
/**
 * Have we been completely joined to the experiment, need this cause we let
 * observers join in the middle of experiments. 
 */
    protected boolean   m_joined = false; 
/**
 * Are we ready to start.
 */
    protected boolean   m_readyToStart = false;
/**
 * Is the Experiment running, useful flag for ExptMessages to check.
 */
    protected boolean   m_exptRunning = false;
/**
 * Used to tell when an experiment is being stopped.
 */
    protected boolean   m_exptStopping = false;
     
/**
 * Creates a new ObserverExptInfo object. Is this constructor still used?
 *
 * @param uid The m_exptUID that this ObserverExptInfo object is tied to.
 */
    public ObserverExptInfo(long uid)
        {
        m_exptUID = new Long(uid);
        }
/**
 * Creates a new ObserverExptInfo object.  This constructor is called by JoinExptReqMsg.
 *
 * @param ec The ExptComptroller that this ObserverExptInfo object is attached to.
 * @see girard.sc.expt.io.JoinExptReqMsg
 */
    public ObserverExptInfo(ExptComptroller ec)
        {
        m_exptUID = new Long(ec.getExptUID().longValue());
        m_exptName = ec.getExptName();
        m_NumUsers = ec.getNumUsers();
        m_supervisor = ec.getSupervisor();

        setHumanUser(ec.getHumanUser());
        setRegistered(ec.getRegistered());
        }
/**
 * Creates a new ObserverExptInfo object. Is this constructor still used?
 *
 * @param uid The m_exptUID that this ObserverExptInfo object is tied to.
 * @param name m_exptName.
 * @param users m_NumUsers.
 * @param sup m_supervisor.
 */
    public ObserverExptInfo(long uid, String name, int users, String sup)
        {
        m_exptUID = new Long(uid);
        m_exptName = name;
        m_NumUsers = users;
        m_supervisor = sup;
        }
/**
 * Creates a new ObserverExptInfo object. Is this constructor still used?
 *
 * @param uid The m_exptUID that this ObserverExptInfo object is tied to.
 * @param name m_exptName.
 * @param users m_NumUsers.
 * @param sup m_supervisor.
 */ 
    public ObserverExptInfo(Long uid, String name, int users, String sup)
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
 * @return Returns the value of m_joined.
 */
    public boolean getJoined()
        {
        return m_joined;
        }
/**
 * Returns the total number of human controlled subject positions.
 *
 * @return Returns the number of human subjects.
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
 * Returns the total number of computer controlled subject positions.
 *
 * @return Returns the number of computer subjects.
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
 * @return Returns the value for m_observerID.
 */
    public int getObserverID()
        {
        return m_observerID;
        }
/**
 * @return Returns the value of m_readyToStart.
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
 * Left over from Experiment perhaps?
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
 * Changes the value of m_joined.
 * 
 * @param value The new value for m_joined.
 */
    public void setJoined(boolean value)
        {
        m_joined = value;
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
 * Changes the value of m_observerID.
 *
 * @param value The new value for m_observerID.
 */
    public void setObserverID(int value)
        {
        m_observerID = value;
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
    }
