package girard.sc.expt.obj;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Hashtable;

/**
 * BaseDataInfo: Used to retrieve and manage data saved from an Experiment.
 * 
 * <p>
 * Started: 08-28-2001
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class BaseDataInfo implements Serializable
    {
/**
 * The unique identifier of the person who ran the experiment (ID_INT).
 */
    protected int              m_userID;
/**
 * Name of the person who ran the experiment, First and Last.
 */
    protected String           m_userName;
/**
 * Unique identifier for the expt output index (Expt_Out_ID_INT).
 */ 
    protected int              m_exptOutID;
/**
 * The order in which this action was executed within the Experiment (Action_Index_INT).
 */
    protected int              m_actionIndex;
/**
 * This is the index location of the class object for the ExperimentAction
 * type that this data was created by;  It would probably be better if
 * this were the name of the ExperimentAction instead, but that would
 * require some serious recoding of the database tables.
 */
    protected int              m_actionObjectIndex;
/**
 * The the ExperimentAction itself.
 */
    protected ExperimentAction        m_action;
/**
 * The date when this data was gathered.
 */
    protected Timestamp        m_dateRun;
/**
 * The codename for the database where the data is stored (ie beDB).
 */
    protected String           m_actionDB = new String("");
/**
 * The descriptive name for the ExperimentAction that generated the
 * data.
 */
    protected String           m_actionDetailName = new String("");
/**
 * The description for the ExperimentAction.
 */
    protected String           m_actionDesc;
/**
 * Is everyone allowed to look at this data or not;  Probably going to be phased
 * out.
 */
    protected boolean          m_generalAccess = false;
/**
 * Is this real experimental data, or practice data of some sort;  Probably going
 * to be phased out.
 */
    protected boolean          m_practiceData = false;
/**
 * Where the data once retrieved from its database table(s) is kept.
 */
    protected Hashtable        m_actionData = new Hashtable(); 

/**
 * The base constructor.
 */
    public BaseDataInfo()
        {
        }

/**
 * Adds additional data to the m_actionData variable.  Should probably enforce
 * the data to be in a Vector, but not something that has to be done.
 *
 * @param key The lookup key for the data to be added.
 * @param data The data to add to the Hashtable, how the data is setup
 * is determined by how the retrieveData function is defined in the
 * ExperimentAction.
 * @see girard.sc.expt.obj.ExperimentAction
 */
    public void addActionData(String key,Object data)
        {
        m_actionData.put(key,data);
        }

/**
 * @return Returns the ExperimentAction, m_action.
 */    
    public ExperimentAction getAction()
        {
        return m_action;
        }
/**
 * @return Returns the Hashtable, m_actionData.
 */
    public Hashtable getActionData()
        {
        return m_actionData;
        }
/**
 * @return Returns the String, m_actionDB.
 */
    public String getActionDB()
        {
        return m_actionDB;
        }
/**
 * @return Returns the String, m_actionDesc.
 */
    public String getActionDesc()
        {
        return m_actionDesc;
        }
/**
 * @return Returns the String, m_actionDetailName.
 */
    public String getActionDetailName()
        {
        return m_actionDetailName;
        }
/**
 * @return Returns the value of m_actionIndex.
 */
    public int getActionIndex()
        {
        return m_actionIndex;
        }
/**
 * @return Returns the value of m_actionObjectIndex.
 */
    public int getActionObjectIndex()
        {
        return m_actionObjectIndex;
        }
/**
 * @return Returns the Timestamp, m_dateRun.
 */
    public Timestamp getDateRun()
        {
        return m_dateRun;
        }
/**
 * @return Returns the value of m_exptOutID.
 */
    public int getExptOutID()
        {
        return m_exptOutID;
        }
/**
 * @return Returns the value of m_generalAccess.
 */
    public boolean getGeneralAccess()
        {
        return m_generalAccess;
        }
/**
 * @return Returns the value of m_practiceData.
 */
    public boolean getPracticeData()
        {
        return m_practiceData;
        }
/**
 * @return Returns the value of m_userID.
 */
    public int getUserID()
        {
        return m_userID;
        }
/**
 * @return Returns the value of m_userName.
 */
    public String getUserName()
        {
        return m_userName;
        }
 
/**
 * Changes the ExperimentAction m_action is set to.
 *
 * @param ea The ExperimentAction to set m_action to.
 */
    public void setAction(ExperimentAction ea)
        {
        m_action = ea;
        }
/**
 * Changes the Hashtable m_actionData is set to.
 *
 * @param data The Hashtable to set m_actionData to.
 */
    public void setActionData(Hashtable data)
        {
        m_actionData = data;
        }
/**
 * Changes the String m_actionDB is set to.
 *
 * @param str The String to set m_actionDB to.
 */
    public void setActionDB(String str)
        {
        m_actionDB = str;
        }
/**
 * Changes the String m_actionDesc is set to.
 *
 * @param str The String to set m_actionDesc to.
 */
    public void setActionDesc(String str)
        {
        m_actionDesc = str;
        }
/**
 * Changes the String m_actionDetailName is set to.
 *
 * @param str The String to set m_actionDetialName to.
 */
    public void setActionDetailName(String str)
        {
        m_actionDetailName = str;
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
 * Changes the value of m_actionObjectIndex.
 *
 * @param value The value to set m_actionObjectIndex to.
 */
    public void setActionObjectIndex(int value)
        {
        m_actionObjectIndex = value;
        }
/**
 * Changes the Timestamp that m_dateRun is set to.
 *
 * @param day The new Timestamp to set m_dateRun to.
 */
    public void setDateRun(Timestamp day)
        {
        m_dateRun = day;
        }
/**
 * Changes the value of m_exptOutID.
 *
 * @param value The new value to set m_exptOutID to.
 */
    public void setExptOutID(int value)
        {
        m_exptOutID = value;
        }
/**
 * Changes the value of m_generalAccess.
 *
 * @param value The new value to m_generalAccess to.
 */
    public void setGeneralAccess(boolean value)
        {
        m_generalAccess = value;
        }
/**
 * Changes the value of m_practiceData.
 *
 * @param value The new value to set m_practiceData to.
 */
    public void setPracticeData(boolean value)
        {
        m_practiceData = value;
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
 * Changes the String that m_userName is set to.
 *
 * @param str The String to set m_userName to.
 */
    public void setUserName(String str)
        {
        m_userName = str;
        }
    }