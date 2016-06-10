package girard.sc.expt.obj;

import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.EndExptReqMsg;
import girard.sc.expt.io.msg.SimJoinExptReqMsg;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Experiment: Stores all the actions for an experiment.
 * <p>
 * <br> Started: 05-14-2000
 * <br> Modified: 10-23-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class Experiment implements Serializable
    {
/**
 * The name of the experiment, unique for each user.
 *
 */
    protected String      m_exptName = "NONE";
/**
 * A description of the experiment, entered in by the user with the ExptBuilderWindow.
 *
 * @see girard.sc.expt.awt.ExptBuilderWindow
 */
    protected String      m_exptDesc = "An Experiment.";
/**
 * The user that created or is using the experiment.
 *
 */
    protected int         m_userID = 0;
/**
 * The access group Name that is cleared to use this Experiment, if no access group is
 * cleared to use this Experiment then access is based off of m_userID.
 */
    protected String      m_appName = null;
/**
 * The access group ID that is cleared to use this Experiment, if no ID then base access
 * off of m_userID.
 */
    protected String      m_appID = null;

/**
 * The unique index identifier for the Experiment created by the database when it
 * is first saved.
 *
 */
    protected int         m_exptID = 0;
/**
 * When an Experiment is run, it is given an index where its output
 * files go.
 *
 */
    protected int         m_exptOutputID = 0;
/**
 * The list of ExperimentActions stored in the order in which they will occur.
 *
 * @see girard.sc.expt.obj.ExperimentAction
 */ 
    protected Vector      m_Actions = new Vector();
/**
 * The number of users required for this experiment.
 *
 */
    protected int         m_NumUsers = 1;
/**
 * true -> Human    false -> Computer
 *
 */
    protected boolean[]   m_HumanUser;
/**
 * Password needed for that user position to connect, only important for human users.
 *
 */
    protected String[]    m_Password;
/**
 * The password required by any observers that wish to view the experiment.
 *
 */
    protected String      m_ObserverPass;

/**
 * Stores the Message Listeners for the Sim Actors, uses the index as the key;
 * <br>REMEMBER: transient makes the Hashtable when sent over a network set to null.
 *
 */
    protected transient Hashtable   m_simSMLs = new Hashtable();
/**
 * A list of who is watching the experiment.
 *
 */
    protected Vector      m_Observers = new Vector();
/**
 * A list of who is particpating in the experiment.
 *
 */
    protected Vector      m_Subjects = new Vector();
/**
 * Whether to allow observers or not, set when the experiment is started.
 *
 * @see girard.sc.expt.web.RegisterExperimentPage
 */
    protected boolean     m_allowObservers = true;
/**
 * Which user positions have been filled for the experiment.
 *
 */ 
    protected boolean[]   m_registered;
/**
 * Has that user sent back some key ready reply.
 *
 */
    protected boolean[]   m_ready;
/**
 * Is the experiment ready to start.
 *
 */
    protected boolean     m_readyToStart = false;
/**
 * Is the experiment running.
 *
 */
    protected boolean     m_exptRunning = false;
/**
 * Used to tell when an experiment is being stopped.
 *
 */
    protected boolean     m_exptStopping = false;

/**
 * Which ExperimentAction are we currently running.
 *
 */
    protected int         m_actionIndex = -1;

/**
 * Any extra settings attached to the Experiment, such as how to
 * start the experiment up (an initial window) or how to shut it down
 * (a final window).
 */
    protected Hashtable m_extraData = new Hashtable();

/**
 * A Constructor.  Called when creating a brand new Experiment in the ExptBuilderWindow.
 *
 */     
    public Experiment()
        {
        m_extraData.put("EndWindow","No");
        m_extraData.put("AutoStart","false");
        }
/**
 * A Constructor. Called when loading an Experiment from the database.
 *
 * @param rs The ResultSet containing saved information on the settings of the Experiment.
 */
    public Experiment(ResultSet rs)
        {
        try
            {
            m_userID = rs.getInt("ID_INT");
            this.setExptID(rs.getInt("Experiment_ID_INT"));
            m_appID = rs.getString("App_ID");
            m_appName = rs.getString("App_Name_VC");
            this.setExptName(rs.getString("Experiment_Name_VC"));
            this.setExptDesc(rs.getString("Experiment_Desc_VC"));
            this.initializeUsers(rs.getInt("Num_Users_INT"));
            InputStream bs = rs.getBinaryStream("Extra_Data_OBJ");
            if (bs != null)
                {
                ObjectInputStream ois = new ObjectInputStream(bs);
                this.initializeExtraData(ois.readObject());
                }
            else
                {
                this.initializeExtraData(null);
                }

            initializeUsers(m_NumUsers);
            }
         catch( Exception e ) { }
        }
/**
 * Adds an ExperimentAction to the m_Actions list.
 *
 * @param obj The ExperimentAction to add to the Experiment.
 */
    public void addAction(ExperimentAction obj)
        {
        m_Actions.addElement(obj);
        }
/**
 * Adds an ExperimentAction to the Experiment at a specific location.
 *
 * @param obj The ExperimentAction to add to the Experiment.
 * @param index The location to add the ExperimentAction to in the m_Actions list.
 */
    public void addAction(ExperimentAction obj, int index)
        {
        if (index > m_Actions.size())
            {
            m_Actions.addElement(obj);
            }
        else
            {
            m_Actions.insertElementAt(obj,index);
            }
        }

/**
 * Goes through all the ExptMessageListeners that are for use by the SimActors
 * closes them down and removes them from m_simSMLs.
 *
 */
    public void cleanUpSimSMLs()
        {
        for (int x=0;x<m_NumUsers;x++)
            {
            if (!m_HumanUser[x])
                {
                ExptMessageListener ml = (ExptMessageListener)m_simSMLs.get(new Integer(x));
                ml.finalize(-1);
                }
            }
        m_simSMLs.clear();
        }

/**
 * Formats the CallableStatement that will be used to store the Experiment to the
 * database.
 *
 * @param cs The CallableStatement to format.
 */
    public Vector formatInsertStatement(CallableStatement cs) throws java.sql.SQLException
        {
        if (m_userID == 0)
            {
            cs = null;
            return new Vector();
            }
        else 
            cs.setInt(1, m_userID);

        if (m_appID == null)
            {
            cs.setNull(2,java.sql.Types.VARCHAR);
            }
        else
            cs.setString(2,m_appID);

        if (m_appName == null)
            {
            cs.setNull(3,java.sql.Types.VARCHAR);
            }
        else
            cs.setString(3,m_appName);

        if (m_exptName == null)
            {
            cs = null;
            return new Vector();
            }
        else
            cs.setString(4,m_exptName);

        if (m_exptDesc == null)
            {
            cs = null;
            return new Vector();
            }
        else
            cs.setString(5,m_exptDesc);

        cs.setInt(6,this.getNumUsers());

        Vector v = FMSObjCon.addObjectToStatement(7,m_extraData,cs);

        cs.registerOutParameter(8, java.sql.Types.INTEGER);

        return v;
        }

/**
 * @param value The index in m_Actions to get the ExperimentAction from.
 * @return Returns the ExperimentAction at a given index in m_Actions.
 */
    public ExperimentAction getAction(int value)
        {
        return (ExperimentAction)m_Actions.elementAt(value);
        }
/**
 * @return Returns the present value of m_actionIndex.
 */
    public int getActionIndex()
        {
        return m_actionIndex;
        }
/**
 * @return Returns the ExperimentAction located at m_actionIndex.
 */
    public ExperimentAction getActiveAction()
        {
        return (ExperimentAction)m_Actions.elementAt(m_actionIndex);
        }
/**
 * @return Returns the Vecot m_Actions.
 */
    public Vector getActions()
        {
        return m_Actions;
        }
/**
 * @return Returns the value for m_allowObservers.
 */
    public boolean getAllowObservers()
        {
        return m_allowObservers;
        }
/**
 * @return Returns the value of m_appID.
 */
    public String getAppID()
        {
        return m_appID;
        }
/**
 * @return Returns the value of m_appName.
 */
    public String getAppName()
        {
        return m_appName;
        }
/**
 * @return Returns the Hashtable m_extraData.
 */
    public Hashtable getExtraData()
        {
        return m_extraData;
        }
/**
 * Returns a stored Object in m_extraData based on the key.
 *
 * @param key The value of the key to the Object to retrieve from m_extraData.
 * @return Returns a stored Object in m_extraData based on the key.
 */
    public Object getExtraData(String key)
        {
        if (m_extraData.containsKey(key))
            return m_extraData.get(key);
        return null;
        }
/**
 * @return Returns the value of m_exptID.
 */
    public int getExptID()
        {
        return m_exptID;
        }
/**
 * @return Returns the value of m_exptDesc.
 */
    public String getExptDesc()
        {
        return m_exptDesc;
        }
/**
 * @return Returns the value of m_exptName.
 */
    public String getExptName()
        {
        return m_exptName;
        }
/**
 * @return Returns the value of m_exptOutputID.
 */
    public int getExptOutputID()
        {
        return m_exptOutputID;
        }
/**
 * @return Returns the value of m_exptRunning. 
 */
    public boolean getExptRunning()
        {
        return m_exptRunning;
        }
/**
 * @return Returns the value of m_exptStopping.
 */
    public boolean getExptStopping()
        {
        return m_exptStopping;
        }
/**
 * @param The index to get the value from m_HumanUser.
 * @return Returns a value from m_HumanUser based on the index requested.
 */
    public boolean getHumanUser(int index)
        {
        return m_HumanUser[index];
        }
/**
 * @return Returns the boolean array m_HumanUser.
 */
    public boolean[] getHumanUser()
        {
        return m_HumanUser;
        }
/**
 * The callable statement that is used to save the Experiment information to Experiments_T
 * in the GirardExptDB.
 *
 * @return Returns the format of the callable statement.
 */
    public String getInsertFormat()
        {
    /* ID_INT, App_ID, App_Name_VC, Expt_Name_VC, Expt_Desc_VC, Num_Users_INT, Extra_Data_OBJ, return Expt_ID_INT */
        return new String("{call up_insert_JExperiments (?, ?, ?, ?, ?, ?, ?, ?)}");
        }
/**
 * @return Returns the number of ExperimentActions stored in m_Actions.
 */
    public int getNumActions()
        {
        return m_Actions.size();
        }
/**
 * @return Returns how many entries in m_HumanUser are set to true.
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
 * @return Returns the number of observers in m_Observers.
 */
    public int getNumObservers()
        {
        return m_Observers.size();
        }
/**
 * @return Returns how many entries in m_HumanUser are set to false.
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
 * @param index The index in m_Observers to return the ExptUserData from.
 * @return Returns the ExptUserData object for the observer store in m_Observers at the
 * requested index.
 */
    public ExptUserData getObserver(int index)
        {
        return (ExptUserData)m_Observers.elementAt(index);
        }
/**
 * @return Returns the value of m_ObserverPass.
 */
    public String getObserverPass()
        {
        return m_ObserverPass;
        }
/**
 * @return Returns the Vector m_Observers.
 */
    public Vector getObservers()
        {
        return m_Observers;
        }
/**
 * Returns all the passwords.
 *
 * @return Returns the String array containing all the passwords.
 */
    public String[] getPassword()
        {
        return m_Password;
        }
/**
 * @param index The index in m_Password to return the value from.
 * @return Returns the value stored in the requested index of m_Password.
 */
    public String getPassword(int index)
        {
        return m_Password[index];
        }
/**
 * @param index The index in m_ready to return the value from.
 * @return Returns the value stored in the requested index of m_ready.
 */
    public boolean getReady(int index)
        {
        return m_ready[index];
        }
/**
 * @return Returns the value of m_readToStart.
 */
    public boolean getReadyToStart()
        {
        return m_readyToStart;
        }
/**
 * @return Returns the array of booleans, m_registered.
 */
    public boolean[] getRegistered()
        {
        return m_registered;
        }
/**
 * @param index The index in m_registered to return the value from.
 * @return Returns the value stored in the requested index of m_registered.
 */
    public boolean getRegistered(int index)
        {
        return m_registered[index];
        }

/**
 * Used to initialize m_extraData.  If the passed in Object is null or is not
 * a Hashtable then m_extraData is given default information.  Otherwise
 * m_extraData is set equal to the Object.  This was needed as m_extraData was
 * added later and so many Experiment objects don't have it saved in their
 * settings in the database.
 *
 * @param obj The Object to set m_extraData to.
 */
    public void initializeExtraData(Object obj)
        {
        if (obj == null)
            {
            m_extraData.put("EndWindow","No");
            return;
            }
        if (obj instanceof Hashtable)
            {
            m_extraData = (Hashtable)obj;
            }
        else
            {
            m_extraData.put("EndWindow","No");
            return;
            }
        }
/**
 * Called when you want to initialize the m_ready value for human subjects to
 * false, but need the SimActor subjects set to true.
 */
    public void initializeReadyToStart()
        {
        for (int x=0;x<getNumUsers();x++)
            {
            if (m_HumanUser[x])
                m_ready[x] = false;
            else
                m_ready[x] = true;
            }
        }
/**
 * Called when you want to initialize the m_ready value to false for all
 * the subjects.
 */
    public void initializeReady()
        {
        for (int x=0;x<getNumUsers();x++)
            {
            m_ready[x] = false;
            }
        }
/**
 * Initializes the settings for m_registered.  Done just before an Experiment is added
 * to the server.  Sets m_registered for positions that will be occupied by human subjects
 * to false and sets m_registered to true for positions to be occupied by SimActors.
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
 * Gets the SimActors running for a specific ExperimentAction. Normally called
 * from startNextAction function in the Experiment class object. 
 *
 * @param eo The ExptOverlord.
 * @see girard.sc.expt.obj.Experiment#startNextAction(ExperimentWindow ew)
 */
    public void initializeSimActors(ExptOverlord eo)
        {
        for (int x=0;x<m_NumUsers;x++)
            {
            if (!m_HumanUser[x])
                {
                ExperimentAction ea = getActiveAction();
                SimActor sa = ea.getActor(x);
                ExptMessageListener ml = (ExptMessageListener)m_simSMLs.get(new Integer(x));
                sa.setSML(ml);
                ml.addActionListener(sa);
                sa.setEO(eo);
                sa.start();
                }
            }
        }
/**
 * Initializes the ExptMessageListener class objects for the SimActors that will be used
 * in the experiment.  Called in the SetUsersWindow when the Experiment is actually started,
 * just before the initial SimActors are initialized.  After each connection is created, it
 * sends out a SimJoinExptReqMsg to the Server to connect itself to the proper Experiment.
 *
 * @param eo The ExptOverlord.
 * @param exptID The Experiment that the SimActors belong to.
 * @see girard.sc.expt.awt.SetUsersWindow
 * @see girard.sc.expt.io.msg.SimJoinExptReqMsg
 */
    public void initializeSimSMLs(ExptOverlord eo, Long exptID)
        {
        m_simSMLs = new Hashtable();

        for (int x=0;x<m_NumUsers;x++)
            {
            if (!m_HumanUser[x])
                {
                ExptMessageListener ml = eo.createExptML();
                ml.start();
                m_simSMLs.put(new Integer(x),ml);

                Object[] out_args = new Object[3];
                out_args[0] = exptID;
                out_args[1] = new Integer(x);
                SimJoinExptReqMsg tmp = new SimJoinExptReqMsg(out_args);
                ml.sendMessage(tmp);
                }
            }
        }
/**
 * Initialize the user settings for the Experiment.  Run when an Experiment is
 * recreated from its ResultSet in the constructor.  Sets all users to Human and
 * sets all passwords to "User"+<user number>.
 *
 * @param numUsers How many users are in the Experiment.
 */
    public void initializeUsers(int numUsers)
        {
        m_NumUsers = numUsers;
        m_HumanUser = new boolean[numUsers];
        m_Password = new String[numUsers];
        m_registered = new boolean[numUsers];
        m_ready = new boolean[numUsers];

        for (int x=0;x<numUsers;x++)
            {
            m_HumanUser[x] = true;
            m_Password[x] = new String("User"+x);
            m_registered[x] = false;
            }
        }

/**
 * Removes an ExperimentAction from m_Actions.
 *
 * @param index The index location from which to remove the ExperimentAction.
 */
    public void removeAction(int index)
        {
        m_Actions.removeElementAt(index);
        }
/**
 * Removes an Object from m_extraData.
 *
 * @param key The Key under which the Object to be removed is referenced in m_extraData.
 */
    public void removeExtraData(String key)
        {
        if (m_extraData.containsKey(key))
            m_extraData.remove(key);
        }

/**
 * Used to compare a number to the number of Users for the Experiment.  Not sure
 * why I return true if m_NumUsers equals -1, or for that matter when m_NumUsers
 * would equal -1.
 *
 * @param value The number to compare to m_NumUsers
 * @return Returns true if the value equals m_NumUsers or if m_NumUsers equals -1.
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
 * Changes the value of m_actionIndex.
 *
 * @param value The new value to set m_actionIndex to.
 */
    public void setActionIndex(int value)
        {
        m_actionIndex = value;
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
 * Changes the value of m_appID.
 *
 * @param value The new value to set m_appID to.
 */ 
    public void setAppID(String value)
        {
        m_appID = value;
        }
/**
 * Changes the value of m_appName.
 *
 * @param value The new value to set m_appName to.
 */
    public void setAppName(String value)
        {
        m_appName = value;
        }
/**
 * Changes the Hashtable of m_extraData.
 *
 * @param h The new Hashtable to set m_extraData to.
 */
    public void setExtraData(Hashtable h)
        {
        m_extraData = h;
        }
/**
 * Adds an Object to m_extraData.
 *
 * @param key The value of the Key for the Object to be stored in m_extraData.
 * @param obj The Object to be stored in m_extraData.
 */
    public void setExtraData(String key, Object obj)
        {
        m_extraData.put(key,obj);
        }
/**
 * Changes the value of m_exptDesc.
 *
 * @param desc The new value to set m_exptDesc to.
 */
    public void setExptDesc(String desc)
        {
        m_exptDesc = desc;
        }
/**
 * Changes the value of m_exptID.
 *
 * @param value The new value to set m_exptID to.
 */
    public void setExptID(int value)
        {
        m_exptID = value;
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
 * Changes the value of m_exptOutputID.
 *
 * @param value The new value to set m_exptOutputID to.
 */
    public void setExptOutputID(int value) 
        {
        m_exptOutputID = value;
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
 * Sets the m_HumanUser to the passed in array of boolean values.
 *
 * @param obj The array of boolean value.
 */
    public void setHumanUser(boolean[] obj)
        {
        m_HumanUser = obj;
        }
/**
 * Makes a specific user either a human or a simulant.  Goes through and removes any 
 * SimActors for that position that were set previously if changed from a simulant back
 * to a human user.
 *
 * @param value Whether the User is now Human (true) or Simulant (false).
 * @param index Which User position to change.
 */
    public void setHumanUser(boolean value, int index)
        {
        if ((!m_HumanUser[index]) && (value))
            {
            Enumeration enm = m_Actions.elements();
            while (enm.hasMoreElements())
                {
                ExperimentAction ea = (ExperimentAction)enm.nextElement();

                ea.removeActor(index);
                }
            }
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
 * Changes the value of m_ObserverPass.
 *
 * @param str The new value to set m_ObserverPass to, if null sets to "".
 */
    public void setObserverPass(String str)
        {
        if (str != null)
            m_ObserverPass = str;
        else
            m_ObserverPass = new String("");
        }
/**
 * Changes the Vector of m_Observers.
 *
 * @param v The new Vector to set m_Observers to.
 */
    public void setObservers(Vector v)
        {
        m_Observers.removeAllElements();
        m_Observers = v;
        }
/**
 * Changes the value of a given index in the String array, m_Password.
 *
 * @param value The new value to set the given index in m_Password to.
 * @param index The index to be changed in m_Password.
 */
    public void setPassword(String value, int index)
        {
        if (value != null)
            m_Password[index] = value;
        else
            m_Password[index] = new String("");
        }
/**
 * Changes the value of a given index in the boolean array, m_ready.
 *
 * @param value The new value to set the given index in m_ready to.
 * @param index The index to be changed in m_ready.
 */
    public void setReady(boolean value,int index)
        {
        m_ready[index] = value;
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
 * Attaches a new boolean arry  to m_registered.
 *
 * @param values The new boolean arry to set m_registered to.
 */
    public void setRegistered(boolean[] values)
        {
        m_registered = values;
        }
/**
 * Changes the value of m_userID.
 *
 * @param value The new value to set m_userID to.
 */
    public void setUserID(int value)
        {
        m_userID = value;
        }

/**
 * Starts the next ExperimentAction or ends the Experiment if none left. When an 
 * ExperimentAction finishes it should call this function so the next ExperimentAction
 * can be started.  If finished it sends out an EndExptReqMsg.
 *
 * @param ew The ExperimenterWindow attached to the Experiment.
 * @see girard.sc.expt.io.msg.EndExptReqMsg
 */
    public void startNextAction(ExperimenterWindow ew)
        {
        stopActiveSimActors();

        m_actionIndex++;

        if (m_actionIndex < m_Actions.size())   // Start the next experiment action.
            {
            initializeSimActors(ew.getEOApp());  // Make Sure We Start up the next group of Sim actors.
            ew.setWatcher(false); // Should start the next action.
            return;
            }
        else  // The experiment is over.
            {
            String a = (String)m_extraData.get("EndWindow");

            if (a.equals("Yes"))
                {
                Object[] out_args = new Object[1];
                out_args[0] = m_extraData.get("EndWindowDetails");

                EndExptReqMsg tmp = new EndExptReqMsg(out_args);
                ew.getSML().sendMessage(tmp);
                }
            else
                {
                EndExptReqMsg tmp = new EndExptReqMsg(null);
                ew.getSML().sendMessage(tmp);
                }
            }
        }

/**
 * Stops the presently running SimActors.  Done after an ExperimentAction has finished.
 * Called by startNextAction.
 */
    public void stopActiveSimActors()
        {
        for (int x=0;x<m_NumUsers;x++)
            {
            if (!m_HumanUser[x])
                {
                ExperimentAction ea = getActiveAction();
                SimActor sa = ea.getActor(x);
                sa.stopSimActor();
                }
            }
        }

/**
 * Used to update the m_exptID variable from a CallableStatement.  Assumes that
 * the value is located at the 8th index.
 *
 * @param cs The CallableStatement.
 */
    public void updateObject(CallableStatement cs) throws SQLException
        {
        this.setExptID(cs.getInt(8));
        }
    }