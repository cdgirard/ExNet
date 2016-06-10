package girard.sc.expt.obj;

import girard.sc.expt.awt.ActionBuilderWindow;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Is the base class for actions.  What you extend when you want to build
 * an object that will be used by an ExperimentAction.
 * <p>
 * <br>Started: 05-14-2000
 * <br>Modified: 02-06-2002
 * <br>Modified: 10-08-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.2
 * @since JDK1.1
 * @see girard.sc.expt.obj.ExperimentAction
 * @see girard.sc.expt.awt.LoadBaseActionWindow
 * @see girard.sc.expt.awt.SaveBaseActionWindow
 * @see girard.sc.expt.awt.DeleteBaseActionWindow
 */

public abstract class BaseAction extends Object implements Serializable,Cloneable
    {
/**
 * Is the name identifier given to the BaseAction;
 * It is usually this name that you use to identify the BaseAction as you
 * don't normally know what its m_ActionType value will be.
 *
 * @see girard.sc.expt.obj.BaseAction#m_ActionType
 */
    protected String       m_name = new String("Base Action");
/**
 * Is the id value for the action in the database;
 * This value is only used by the database, rarely if ever should you
 * need to use it;  Instead you should use m_name.
 *
 * @see girard.sc.expt.obj.BaseAction#m_name
 */
    protected int          m_ActionType = 0;
/**
 * Is the user id value of the owner of the object;
 * Used by the database to keep track of who the object belongs to;
 * This value is automatically set when the object is saved.
 *
 */
    protected int          m_userID = 0;
/**
 * Allows files to be accessible based on application accessibility in
 * Web-Lab;  Is the name of the application group tied to the BaseAction.
 *
 */
    protected String       m_appName = null;
/**
 * Allows files to be accessible based on application accessibility in
 * Web-Lab;  Is the unique id of the application group tied to the BaseAction.
 *
 */
    protected String       m_appID = null;
/**
 *The database that you can find saved base actions of this type in.
 *
 */
    protected String       m_db = "exptDB";
/**
 * The database table that base actions of this type are saved in.
 *
 */
    protected String       m_dbTable = "Base_Actions_Type_T";
/**
 * The name a newly created object is saved under;  Set by the creator of the
 * object just before they save it to the database.
 *
 * @see girard.sc.expt.awt.SaveBaseActionWindow
 */
    protected String       m_fileName = "NONE";
/**
 * A description of the saved BaseAction;  Can be set automatically or
 * by the creator of the object.
 *
 */
    protected String       m_desc = new String("none");

/**
 * The base constructor for the BaseAction.
 */
    public BaseAction()
        {
        }
/**
 * A constructor for the BaseAction.
 *
 * @param name The value to set m_name to for the BaseAction.
 */
    public BaseAction(String name)
        {
        m_name = name;
        }
/**
 * A constructor for the BaseAction.
 *
 * @param name The value to set m_name to for the BaseAction.
 * @param db The value to set m_db to for the BaseAction.
 * @param dbTable The value to se m_dbTable to for the BaseAction.
 */
    public BaseAction(String name, String db, String dbTable)
        {
        m_name = name;
        m_db = db;
        m_dbTable = dbTable;
        }

/**
 * Used to initialize a BaseAction from its database entry.
 *
 * @param rs The ResultSet passed in from the database.
 * @see java.sql.ResultSet
 */
    public void applyResultSet(ResultSet rs)
        {
        try 
            {
            m_userID = rs.getInt("ID_INT");
            m_fileName = rs.getString("Name_VC");
            m_desc = rs.getString("Desc_VC");
            m_appID = rs.getString("App_ID");
            m_appName = rs.getString("App_Name_VC");
            
            ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Settings_OBJ"));
            Hashtable settings = (Hashtable)ois.readObject();
            applySettings(settings);
            }
        catch(SQLException sqle) { System.err.println(sqle); }
        catch(IOException ioe) { System.err.println(ioe); }
        catch(ClassNotFoundException cnfe) { System.err.println(cnfe); }
        }
/**
 * Used to initialize a BaseAction from its stored settings.  In order to make
 * code updates to objects easier, instead of storing the acutal object, a Hashtable
 * of settings for that object is stored instead.  Thus, instead of updating hunderds
 * or thousands of objects when a minor change is made, only one object is changed.  No
 * other updates are needed so long as no changes to what is actually saved in the
 * Hashtable of settings.
 * <p>
 * When creating a new BaseAction object, this function will normally be overridden 
 * so that your own settings can be saved.  You will either need to add 
 * super.applySettings(h) to your function or make sure that the base variables are
 * set as well.
 * <p>
 * <br>m_name = (String)h.get("Name");
 * <br>m_ActionType = ((Integer)h.get("ActionType")).intValue();
 * <br>m_userID = ((Integer)h.get("UserID")).intValue();
 * <br>m_db = (String)h.get("DB");
 * <br>m_dbTable = (String)h.get("DBTable");
 * <br>m_fileName = (String)h.get("FileName");
 * <br>m_desc = (String)h.get("Desc");
 * <br>if (h.containsKey("AppID"))
 * <br>m_appID = (String)h.get("AppID");
 * <br>if (h.containsKey("AppName"))
 * <br>m_appName = (String)h.get("AppName");
 *
 * @param h The Hashtable of settings passed in.
 * @see java.util.Hashtable
 * @see girard.sc.expt.obj.BaseAction#getSettings()
 */
    public void applySettings(Hashtable h)
        {
        m_name = (String)h.get("Name");
        m_ActionType = ((Integer)h.get("ActionType")).intValue();
        m_userID = ((Integer)h.get("UserID")).intValue();
        m_db = (String)h.get("DB");
        m_dbTable = (String)h.get("DBTable");  
        m_fileName = (String)h.get("FileName");
        m_desc = (String)h.get("Desc");

        if (h.containsKey("AppID"))
            m_appID = (String)h.get("AppID");
        if (h.containsKey("AppName"))
            m_appName = (String)h.get("AppName");
        }

/**
 * Used to make a clone of the BaseAction object.  A version of this function
 * is required for you BaseAction object to function properly.
 *
 * @return The newly created copy of the BaseAction object.
 */
    public abstract Object clone();

/**
 * Used to format the BaseAction object. Normally a window will be launched that
 * allows a user to create, save, load, and delete objects of a certain type. 
 * The window launched is usually an extension of the BaseActionFormatWindow.
 * By extending this class you can use the SaveBaseActionWindow, LoadBaseActionWindow,
 * and DeleteBaseActionWindow to do your saves, loads, and deletes instead of writing
 * your own.
 * <p>
 * The ActionBuilderWindow is passed in so that you can change the setting of its
 * m_EditMode variable using setEditMode().  The ExptOverlord is passed in because it
 * allows access to all of its functions as well as those of the WLOverlord.
 *
 * @see girard.sc.expt.web.ExptOverlord
 * @see girard.sc.wl.web.WLOverlord
 * @see girard.sc.expt.awt.ActionBuilderWindow#setEditMode(boolean value)
 * @see girard.sc.expt.awt.BaseActionFormatWindow
 * @see girard.sc.expt.awt.SaveBaseActionWindow
 * @see girard.sc.expt.awt.LoadBaseActionWindow
 * @see girard.sc.expt.awt.DeleteBaseActionWindow
 */
    public abstract void formatAction(ExptOverlord app, ActionBuilderWindow abw);

/**
 * Used to set the data values for the CallableStatement that will be used to store 
 * the object in the database.  The function should not be overrode unless you REALLY
 * know what you are doing.
 *
 * @param cs The CallableStatement to be sent to the database.
 * @param v Needed to keep track of InputStreams for writing JAVA objects to the database.
 * @exception SQLException If something is not set properly with the CallableStatement.
 * @see girard.sc.io.FMSObjCon
 * @see java.sql.CallableStatement
 * @see java.util.Vector
 */
    public void formatInsertStatement(CallableStatement cs, Vector v) throws java.sql.SQLException
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

        if (m_fileName == null)
            {
            cs = null;
            return;
            }
        else
            cs.setString(4,m_fileName);

        if (m_desc == null)
            {
            cs = null;
            return;
            }
        else
            cs.setString(5,m_desc);

        v.addElement(FMSObjCon.addObjectToStatement(6,getSettings(),cs));
        }
/**
 * @return Returns the value of m_ActionType.
 */
    public int getActionType()
        {
        return m_ActionType;
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
 * @return Returns the value of m_db.
 */
    public String getDB()
        {
        return m_db;
        }
/**
 * @return Returns the value of m_dbTable.
 */
    public String getDBTable()
        {
        return m_dbTable;
        }
/**
 * @return Returns the value of m_desc.
 */
    public String getDesc()
        {
        return m_desc;
        }
/**
 * @return Returns the value of m_fileName.
 */
    public String getFileName()
        {
        return m_fileName;
        }
/**
 * Used to format the CallableStatement that will be used to store the object in 
 * the database.  This function is normally overrode to create your own callableStatement.
 * The callable Statement should expect 6 variables so that the following can be saved in
 * the database table: ID_INT, App_ID, App_Name_VC, Name_VC, Desc_INT, Settings_OBJ.
 *
 * @return The String that is to be used to create the CallableStatement.
 * @see girard.sc.expt.obj.BaseAction#formatInsertStatement(CallableStatement cs, Vector v)
 * @see java.sql.CallableStatement
 */
    public String getInsertFormat()
        {
    /* ID_INT, App_ID, App_Name_VC, Name_VC, Desc_INT, Settings_OBJ */
        return new String("{call up_insert_JBaseAction (?, ?, ?, ?, ?, ?)}");
        }
/**
 * @return Returns the value of m_name.
 */
    public String getName()
        {
        return m_name;
        }
/**
 * Used to create the Hashtable of values that define the BaseAction object.
 * <p>
 * When creating a new BaseAction object, this function will normally be overridden 
 * so that your own settings can be added.  You will either need to add 
 * super.getSettings(h) to your function or make sure that the base variables are
 * set as well.
 * <p>
 *
 * <br>settings.put("Name",m_name);
 * <br>settings.put("UserID",new Integer(m_userID));
 * <br>settings.put("ActionType",new Integer(m_ActionType));
 * <br>settings.put("DB",m_db);
 * <br>settings.put("DBTable",m_dbTable);
 * <br>settings.put("FileName",m_fileName);
 * <br>settings.put("Desc",m_desc);
 * <br>if (m_appID != null)
 * <br>settings.put("AppID",m_appID);
 * <br>if (m_appName != null)
 * <br>settings.put("AppName",m_appName);
 * <p>
 * @return The Hashtable of values that represent the BaseAction object.
 * @see girard.sc.expt.obj.BaseAction#applySettings(Hashtable h)
 * @see java.util.Hashtable
 */
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
        settings.put("Name",m_name);
        settings.put("UserID",new Integer(m_userID));
        if (m_appID != null)
            settings.put("AppID",m_appID);
        if (m_appName != null)
            settings.put("AppName",m_appName);
        settings.put("ActionType",new Integer(m_ActionType));
        settings.put("DB",m_db);
        settings.put("DBTable",m_dbTable);
        settings.put("FileName",m_fileName);
        settings.put("Desc",m_desc);
       
        return settings;
        }
/**
 * @return Returns the value of m_userID.
 */
    public int getUserID()
        {
        return m_userID;
        }

/**
 * @param id The new value of m_ActionType.
 */
    public void setActionType(int id)
        {
        m_ActionType = id;
        }
/**
 * @param value The new value of m_appID.
 */
    public void setAppID(String value)
        {
        m_appID = value;
        }
/**
 * @param value The new value of m_appName.
 */
    public void setAppName(String value)
        {
        m_appName = value;
        }
/**
 * @param db The new value of m_db.
 */
    public void setDB(String db)
        {
        m_db = db;
        }
/**
 * @param dbTable The new value of m_dbTable.
 */
    public void setDBTable(String dbTable)
        {
        m_dbTable = dbTable;
        }
/**
 * @param name The new value of m_desc.
 */
    public void setDesc(String name)
        {
        m_desc = name;
        }
/**
 * @param name The new value of m_fileName.
 */
    public void setFileName(String name)
        {
        m_fileName = name;
        }
/**
 * @param name The new value of m_name.
 */
    public void setName(String name)
        {
        m_name = name;
        }
/**
 * @param value The new value of m_userID.
 */
    public void setUserID(int value)
        {
        m_userID = value;
        }
    }
