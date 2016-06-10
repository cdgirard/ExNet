package girard.sc.expt.obj;

import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * ExperimentAction: Base class for all ExperimentActions.  This class is extended
 * when you want to create a class object that is to be used to create an experiment
 * application.  Objects of this class type are used by the Experiment object during
 * the running of an experiment.
 * <p>
 * <br>Started:  05-14-2000
 * <br>Modified: 04-30-2001
 * <br>Modified: 05-02-2001
 * <br>Modified: 05-18-2001
 * <br>Modified: 02-27-2003
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 * @see girard.sc.be.obj.BENetworkAction
 * @see girard.sc.expt.obj.Experiment
 */

public abstract class ExperimentAction implements Serializable,Cloneable
    {
/**
 * The Identifier in the DB for the action.
 *
 */
    protected int          m_ActionType = 0;
/**
 * The name of the class object, is Unique for each class object.
 *
 */
    protected String       m_name = new String("Exnet Action");
/**
 * A simple description of the class object as to what sort of application it is for;
 * Also used when building an experiment to provide additional details about what the 
 * ExperimentAction will be doing.
 *
 * @see girard.sc.expt.obj.Experiment
 */
    protected String       m_desc = new String("This is an experiment action.");
/**
 * Any data or objects such as BaseActions are stored here.
 *
 */
    protected Object       m_action;
/**
 * Any sim actors being used are stored here using their user number as the key.
 *
 */
    protected Hashtable    m_Actors = new Hashtable(); 

/**
 * The database where data for the ExperimentAction is saved.
 *
 */
    protected String m_dataDB = "none";

/**
 * The constructor function.
 *
 * @param obj The information stored by m_action to be used by the ExperimentAction.
 */    
    public ExperimentAction(Object obj)
        {
        m_action = obj;
        }
/**
 * The constructor function.
 *
 * @param obj The information stored by m_action to be used by the ExperimentAction.
 * @param name The unique name for the ExperimentAction class object.
 */
    public ExperimentAction(Object obj, String name)
        {
        m_name = name;
        m_action = obj;
        }

/**
 * Used when constructing an Experiment.  Let's the constructor for the Experiment
 * know if this ExperimentAction requires the number of users to remain fixed or not.
 *
 * @see girard.sc.expt.obj.Experiment
 * @see girard.sc.expt.awt.ExptBuilderWindow
 */
    public abstract boolean allowChangeNumUsers();

/**
 * Used to restore an ExperimentAction to its proper state.  When ExperimentActions
 * are saved in the database, a Hashtable of data values representing the object are
 * stored.  This allow changes to be made to the ExperimentAction object itself without
 * needing to update all saved instances of the object in the database.
 * <p>
 * Required by many of the built in management functions.  If not implemented correctly,
 * may cause your ExperimentAction to not function properly. The following variables
 * should be restored from the Hashtable: m_ActionType, m_name, m_desc, m_dataDB.
 *
 * @param h The Hashtable of data values that represents the ExperimentAction.
 * @see girard.sc.expt.obj.ExperimentAction#getSettings()
 * @see girard.sc.expt.sql.SaveExptReq
 */
    public abstract void applySettings(Hashtable h);

/**
 * Used to make a copy of the ExperimentAction.  Required by many of the built in
 * management functions.  If not implemented correctly, may cause your ExperimentAction
 * to not function properly.  The following variables should be copied: m_ActionType,
 * m_name, m_desc, m_dataDB.
 *
 * @return Returns the newly created copy of the ExperimentAction.
 */
    public abstract Object clone();

/**
 * Used to display the data from any experiments with this ExperimentAction.
 *
 * @param app The ExptOverlord object.
 * @param bdi The data to be displayed.
 * @see girard.sc.expt.web.DataPage
 */
    public abstract void displayData(ExptOverlord app, BaseDataInfo bdi);

/**
 * @return Returns the m_action Object.
 */
    public Object getAction()
        {
        return m_action;
        }
/**
 * @return Returns the value of m_ActionType.
 */
    public int getActionType()
        {
        return m_ActionType;
        }
/**
 * @return Returns the Hashtable m_Actors.
 */
    public Hashtable getActors()
        {
        return m_Actors;
        }
/**
 * Returns the requested SimActor class object from the m_Actors.
 *
 * @param index The User number for the SimActor desired.
 * @return The SimActor if one is there for that user number.
 */
    public SimActor getActor(int index)
        {
        return (SimActor)m_Actors.get(new Integer(index));
        }
/**
 * @return Returns the value of m_dataDB.
 */
    public String getDataBD()
        {
        return m_dataDB;
        }
/**
 * @return Returns the value of m_desc.
 */
    public String getDesc()
        {
        return m_desc;
        }
/**
 * Returns a more informative name for the ExperimentAction.  Sort of a
 * shorthand for the description used for the ExperimentAction in the 
 * Experiment object.
 *
 * @return A more informative name for the ExperimentAction.
 */
    public abstract String getDetailName();
    public String getName()
        {
        return m_name;
        }
/**
 * Used to store  the present state of an ExperimentAction.  When ExperimentActions
 * are saved in the database, a Hashtable of data values representing the object are
 * stored.  This allow changes to be made to the ExperimentAction object itself without
 * needing to update all saved instances of the object in the database.
 * <p>
 * Required by many of the built in management functions.  If not implemented correctly,
 * may cause your ExperimentAction to not function properly.  The following variables
 * should be stored in the Hashtable: m_ActionType, m_name, m_desc, m_dataDB.
 *
 * @return The Hashtable of data values that represents the ExperimentAction.
 * @see girard.sc.expt.obj.ExperimentAction#applySettings(Hashtable h)
 * @see girard.sc.expt.sql.SaveExptReq
 */
    public abstract Hashtable getSettings();

/**
 * Used to provide a means to adjust the settings to the ExperimentAction.  Normally
 * calls up a window in which a user can make changes to the settings of the
 * ExperimentAction.  If it is not possible to change the settings for an ExperimentAction
 * then you should simply set the m_editMode for the ExptBuilderWindow to false.
 *
 * @param app The ExptOverlord object.
 * @param ebw The ExptBuilderWindow.
 * @see girard.sc.expt.awt.ExptBuilderWindow#setEditMode(boolean value)
 */
    public abstract void formatAction(ExptOverlord app, ExptBuilderWindow ebw);

/**
 * Just before it is time to start the ExperimentAction during an experiment. Will
 * normally initialize any variables before a copy of the ExperimentAction is saved
 * to the database.
 *
 * @param app1 ExptOverlord.
 * @param app2 The Experiment that the ExperimentAction is attached to.
 * @param app3 The ExptMessageListener, needed for sending and receiving messages during the experiment.
 */
    public void initializeAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        }

/**
 * Used to initialize the ExperimentAction when it is first added to an Experiment.
 * This may or may not require input from the user.  Does not need to acutally add
 * an action to the Experiment, but does need to set the m_editMode for the
 * ExptBuilderWindow to false upon finishing.
 *
 * @param app The ExptOverlord object.
 * @param ebw The ExptBuilderWindow.
 * @see girard.sc.expt.awt.ExptBuilderWindow#setEditMode(boolean value)
 * @see girard.sc.expt.awt.ExptBuilderWindow#addAction(String str, ExperimentAction obj)
 */
    public abstract void initializeAction(ExptOverlord app, ExptBuilderWindow ebw);

/**
 * Used to remove a SimActor from the m_Actors list.
 *
 * @param index The user position of the SimActor to be removed.
 */
    public void removeActor(int index)
        {
        if (m_Actors.containsKey(new Integer(index)))
            {
            m_Actors.remove(new Integer(index));
            }
        }

/**
 * Used to load the data from an experiment that was run.  When a request for data from
 * an ExperimentAction is made, a LoadDataFileReqMsg is sent. At the WLGeneralServerConnection
 * an exact copy of the ExperimentAction used is created using information from the
 * BaseDataInfo object.  
 * Then this function is called to retrieve the data.  Normally retrieving data is done
 * utilizing LoadDataResultsReq and a class object that is an extension of DataOutputObject.
 * All of the DataOutputObjects of a certain type are placed together in a vector then
 * that Vector is added to the Hashtable that is returned.  
 * The Hashtable is then stored in the BaseDataInfo object in the variable m_actionData.
 *
 * @param wlgsc The WLGeneralServerConnection that is processing the message.
 * @param em The actual message that is being processed, in this case normally a LoadDataFileReqMsg.
 * @param bdi The BaseDataInfo class object that contains all information of the ExperimentAction at the time the Experiment was run.
 * @return The Hashtable of data, normally a group of Vectors with each Vector containing a list of DataOutputObjects.
 * @see girard.sc.expt.sql.LoadDataResultsReq
 * @see girard.sc.expt.obj.DataOutputObject
 * @see girard.sc.expt.io.msg.LoadDataFileReqMsg
 */
    public abstract Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi);

/**
 * Sends the present state of the ExperimentAction during an Experiment to an Observer.
 * When an Observer starts watching an Experiment after it is started, this is used
 * to tell what information to send to the new Observer.
 *
 * @param obv The identifier for the Observer.
 * @param eo The ExptOverlord.
 * @param ml The ExptMessageListener. 
 */
    public abstract void sendPresentState(Integer obv, ExperimenterWindow ew);  

/**
 * Sets the Object for m_action.
 *
 * @param obj The Object to set m_action to.
 */
    public void setAction(Object obj)
        {
        m_action = obj;
        }
/**
 * Sets the value for m_ActionType.
 *
 * @param id The value to set m_ActionType to.
 */
    public void setActionType(int id)
        {
        m_ActionType = id;
        }
/**
 * Addes a SimActor to the Hashtable m_Actors.
 *
 * @param index The user index of the SimActor.
 * @param sa The SimActor to add.
 */
    public void setActor(int index, SimActor sa)
        {
        m_Actors.put(new Integer(index),sa);
        }
/**
 * Sets the value for m_dataDB.
 *
 * @param db The value to set m_dataDB to.
 */
    public void setDataDB(String db)
        {
        m_dataDB = db;
        }
/**
 * Sets the value of m_desc.
 *
 * @param desc The value to set m_desc to.
 */
    public void setDesc(String desc)
        {
        m_desc = desc;
        }
/**
 * Sets the value of m_name.
 *
 * @param name The value to set m_name to.
 */
    public void setName(String name)
        {
        m_name = name;
        }

/**
 * Called when it is time to start the ExperimentAction during an experiment. Will
 * normally create a screen for the Experimenter to follow the experiment from and
 * send a message off to the subjects and observers.
 *
 * @param app1 ExptOverlord.
 * @param app2 The Experiment that the ExperimentAction is attached to.
 * @param app3 The ExptMessageListener, needed for sending and receiving messages during the experiment.
 */
    public abstract void startAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3);

/**
 * Called when it is time to end the ExperimentAction during an experiment.
 *
 * @param app1 ExptOverlord.
 * @param app2 The Experiment that the ExperimentAction is attached to.
 * @param app3 The ExptMessageListener, needed for sending and receiving messages during the experiment.
 */
    public abstract void stopAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3);
    }
