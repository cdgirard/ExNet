package girard.sc.expt.obj;

import girard.sc.expt.awt.SimulantBuilderWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * The base class that all other computer controlled actors are based off of.
 * Has a set of functions that allow the SimActor interact with
 * an ExperimentAction that can be overridden or that are declared 
 * abstract.
 * <p>
 * <br> Started: 01-05-2001
 * <br> Modified: 05-24-2001
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1  
 */


public abstract class SimActor extends Thread implements Cloneable,Serializable,ActionListener
    {
/**
 * The id of the SimActor within it's type.
 * @see girard.sc.expt.obj.SimActor#m_actorTypeID
 */
    protected int              m_actorID = 0;
/**
 * The user who created the sim actor.
 */
    protected int              m_userID = 0;
/**
 * The ID for the access group that can utilize this SimActor.
 */
    protected String           m_appID = null;
/**
 * The name of the access group that can utilize this SimActor.
 */
    protected String           m_appName = null;
/**
 * The index location of the base type for an actor.
 */
    protected int              m_actorTypeID = 0;
/**
 * What ExperimentAction type the SimActor is designed for.
 * @see girard.sc.expt.obj.ExperimentAction#m_name
 */
    protected String           m_actionType = "";
/**
 * Database the SimActor is stored in.
 */
    protected String           m_db;
/**
 * The filename of the SimActor, how a user would identify the actor.
 */  
    protected String	       m_actorName = "SimActor";
/**
 * The description of the SimActor.
 */
    protected String           m_actorDesc = "This is a simulant actor.";
/**
 * User that the Sim Actor is tied to.
 */
    protected int              m_user = -1;
/**
 * Data about the ExperimentAction when an Experiment is running;  Used so
 * that the SimActor can keep track of what is going on.
 */
    protected Object           m_activeAction = null;
/**
 * The ExptMessageListener assigned to the SimActor when it is being used in an
 * experiment.
 */
    protected transient ExptMessageListener  m_SML;
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 */
    protected transient ExptOverlord     m_EOApp;

/**
 * The base constructor.  
 *
 * @param db The database where all the SimActor files of its type are stored.
 */
    public SimActor (String db)
        {
        super("SimActor");
        m_db = db;
        }
/**
 * A constructor that allows you to set the name, database, and description of
 * the SimActor.
 *
 * @param db The value for the m_db variable.
 * @param n The value for the m_actorName variable and the name of the thread.
 * @param d The value for the m_actorDesc variable.
 */
    public SimActor (String db, String n, String d)
        {
        super(n);

        m_db = db;
        m_actorName = n;
        m_actorDesc = d;
        }
   
/**
 * Used to handle incoming messages.
 */ 
    public abstract void actionPerformed(ActionEvent e);

/**
 * Used to restore a SimActor from its settings in the database.
 */
    public abstract void applySettings(Hashtable h);

/**
 * Used to create a clone of the SimActor.
 */
    public abstract Object clone();

/**
 * Used to kill off the SimActor by calling stopSimActor().
 */
    public void finalize()
        {
        stopSimActor();
        }

/**
 * Used to define how the SimActor is formated.
 *
 * @param app The ExptOverlord.
 * @param sbw The SimulantBuilderWindow.
 * @see girard.sc.expt.awt.FormatSimActorWindow
 */
    public abstract void formatActor(ExptOverlord app, SimulantBuilderWindow sbw);

/**
 * Stores the vital informatin of the SimActor into a CallableStatement for saving the
 * SimActor to the database.
 *
 * @param cs The CallableStatement.
 * @param v The Vector needed for the FMSObjCon class object.
 * @see girard.sc.io.FMSObjCon
 */
    public final void formatInsertStatement(CallableStatement cs, Vector v) throws java.sql.SQLException
        {
        if (m_userID == 0)
            {
            cs = null;
            return;
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

        if (m_actorTypeID == 0)
            {
            cs = null;
            return;
            }
        else
            cs.setInt(4,m_actorTypeID);

        if (m_actorName == null)
            {
            cs = null;
            return;
            }
        else
            cs.setString(5,m_actorName);

        if (m_actorDesc == null)
            {
            cs = null;
            return;
            }
        else
            cs.setString(6,m_actorDesc);

        v.addElement(FMSObjCon.addObjectToStatement(7,getSettings(),cs));

        cs.registerOutParameter(8, java.sql.Types.INTEGER);
        }

/**
 * @return Returns the String, m_actorDesc.
 */
    public String getActorDesc()
        {
        return m_actorDesc;
        }
/**
 * @return Returns the value for m_actorID.
 */
    public int getActorID()
        {
        return m_actorID;
        }
/**
 * @return Returns the String, m_actorName.
 */
    public String getActorName()
        {
        return m_actorName;
        }
/**
 * @return Returns the value for m_actorTypeID.
 */
    public int getActorTypeID()
        {
        return m_actorTypeID;
        }
/**
 * @return Returns the String, m_actionType.
 */
    public String getActionType()
        {
        return m_actionType;
        }
/**
 * @return Returns the String, m_appID.
 */
    public String getAppID()
        {
        return m_appID;
        }
/**
 * @return Returns the String, m_appName.
 */
    public String getAppName()
        {
        return m_appName;
        }
/**
 * @return Returns the String, m_db.
 */
    public String getDB()
        {
        return m_db;
        }
/**
 * @return Returns the ExptOverlord, m_EOApp.
 */
    public ExptOverlord getEO()
        {
        return m_EOApp;
        }
/**
 * Returns the insert format for the CallableStatement.
 *
 * @returns The insert format.
 */
    public final String getInsertFormat()
        {
    /* ID_INT, App_ID, App_Name_VC, Sim_Type_ID_INT, Sim_Name_VC, Sim_Desc_VC, Settings_OBJ, return Sim_Actor_ID_INT */
        return new String("{call up_insert_JSimulant (?, ?, ?, ?, ?, ?, ?, ?)}");
        }
/**
 * Used to get the values of the variables that make up ths SimActor.  Needed for saving
 * a the SimActor to the database.  We don't save the actual object since complications
 * can arise when we need to make minor changes to the object's code.
 *
 * @return Returns a Hashtable containing all the values of the key variables.
 */
    public abstract Hashtable getSettings();
/**
 * @return Returns the value for m_user.
 */
    public int getUser()
        {
        return m_user;
        }
/**
 * @return Returns the ExptMessageListener, m_SML.
 */
    public ExptMessageListener getSML()
        {
        return m_SML;
        }

/**
 * This is called when a SimActor is retrieved from the database.
 *
 * @param rs The ResultSet containing the information about the SimActor.
 */
    public final void initializeActor(ResultSet rs)
        {
        try 
            {
            m_actorID = rs.getInt("Sim_Actor_ID_INT");
            m_userID = rs.getInt("ID_INT");
            m_appID = rs.getString("App_ID");
            m_appName = rs.getString("App_Name_VC");
            m_actorTypeID = rs.getInt("Sim_Type_ID_INT");
            m_actorName = rs.getString("Sim_Name_VC");
            m_actorDesc = rs.getString("Sim_Desc_VC");
            
            ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Settings_OBJ"));
            Hashtable h = (Hashtable)ois.readObject();
     
            applySettings(h);
            }
        catch(SQLException sqle) { System.err.println(sqle); }
        catch(IOException ioe) { System.err.println(ioe); }
        catch(ClassNotFoundException cnfe) { System.err.println(cnfe); }
        }

/**
 * The run() function for the SimActor.  As the SimActor is a thread this is should
 * run until the ExperimentAction the SimActor is attached to is finished.
 */
    public abstract void run();

/**
 * Changes the value of the Strin, m_actorDesc.
 *
 * @param desc The new String to set m_actorDesc to.
 */
    public void setActorDesc(String desc)
        {
        m_actorDesc = desc;
        }
/**
 * Changes the value of m_actorID.
 * 
 * @param value The new value to set m_actorID to.
 */
    public void setActorID(int value)
        {
        m_actorID = value;
        }
/**
 * Changes the value of m_actorName.
 *
 * @param name The new String to set m_actorName to.
 */
    public void setActorName(String name)
        {
        m_actorName = name;
        }
/**
 * Changes the value of m_actionType.
 *
 * @param value The new String to set m_actionType to.
 */
    public void setActionType(String value)
        {
        m_actionType = value;
        }
/**
 * Changes the value of m_actorTypeID.
 *
 * @param value The new value to set m_actorTypeID to.
 */
    public void setActorTypeID(int value)
        {
        m_actorTypeID = value;
        }
/**
 * Changes the value of m_appID.
 * 
 * @param value The new String to set m_appID to.
 */
    public void setAppID(String value)
        {
        m_appID = value;
        }
/**
 * Changes the value of m_appName.
 *
 * @param value The new String to set m_appName to.
 */
    public void setAppName(String value)
        {
        m_appName = value;
        }
/**
 * Changes the ExptOverlord that m_EOApp is set to.
 *
 * @param eo The new ExptOverlord to set m_EOApp to.
 */
    public void setEO(ExptOverlord eo)
        {
        m_EOApp = eo;
        }
/**
 * Changes the ExptMessageListener that m_SML is set to.
 *
 * @param ml The new ExptMessageListener to set m_SML to.
 */
    public void setSML(ExptMessageListener ml)
        {
        m_SML = ml;
        }
/**
 * Changes the value of m_user.
 * 
 * @param value The new value to set m_user to.
 */
    public void setUser(int value)
        {
        m_user = value;
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
 * Used to define how to go about stopping the SimActor, such as making sure to clean
 * up any objects before stopping.
 */
    public abstract boolean stopSimActor();

/**
 * Updates the m_actorID value based on the 8th paramter from the CallableStatement.
 *
 * @param cs The CallableStatement.
 */
    public void updateObject(CallableStatement cs) throws SQLException
        {
        this.setActorID(cs.getInt(8));
        }
    }
