package girard.sc.expt.web;

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.help.HelpWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptNetObjectStream;
import girard.sc.expt.io.msg.DeleteBaseActionReqMsg;
import girard.sc.expt.io.msg.DeleteExptReqMsg;
import girard.sc.expt.io.msg.ExptFileListReqMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.LoadBaseActionReqMsg;
import girard.sc.expt.io.msg.LoadExptReqMsg;
import girard.sc.expt.io.msg.LoadSimActorReqMsg;
import girard.sc.expt.io.msg.SaveBaseActionReqMsg;
import girard.sc.expt.io.msg.SaveExptReqMsg;
import girard.sc.expt.io.msg.SaveSimActorReqMsg;
import girard.sc.expt.io.msg.SavedBaseActionFileListReqMsg;
import girard.sc.expt.io.msg.SimActorFileListReqMsg;
import girard.sc.expt.io.msg.SimActorTypeListReqMsg;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.SimActor;
import girard.sc.web.WebPanel;
import girard.sc.wl.io.msg.WLAccessGroupListReqMsg;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.web.WLOverlord;

import java.applet.Applet;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * An extension of WLOverlord.  Provides a few additional functions designed
 * specifically for running online multi-user experiments.
 * <p>
 * <br> Started: 2001
 * <br> Modified: 10-30-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 * @see girard.sc.wl.web.WLOverlord
 */

public class  ExptOverlord extends WLOverlord
    {
/**
 * The location of the help files.  This is where the built in help system
 * goes looking for files.
 *
 * @see girard.sc.expt.help.HelpWindow
 */
    protected String HELP_DIR = new String("/Java/ExNet/help/");

/**
 * Port number for the expt server
 */
    private int    EXPT_PORT = 8180;
/**
 * The HelpWindow.
 */
    private HelpWindow m_help = null;

/**
 * A constructor for ExptOverlord.
 *
 * @param app The Applet running in the web browser that created ExptOverlord.
 */
    public ExptOverlord(Applet app)
        {
        super(app);
        }
/**
 * A constructor for ExptOverlord.
 *
 * @param app The Applet running in the web browser that created ExptOverlord.
 * @param width The value for m_width.
 * @param height The value for m_height.
 */
    public ExptOverlord(Applet app, int width, int height)
        {
        super(app,width,height);
        }    

/**
 * Add a WebPanel to be displayed within the m_WebpageBasePanel.  Overridden so that
 * the m_TitleCanvas says ExNet 3.0 rather than Web-Lab.
 *
 * @param A The WebPanel to be displayed.
 */
    public void addPanel(WebPanel A)
        {
        A.initializeLabels();
        m_activePanel = A;
        m_WebpageBasePanel.add(A);
        m_TitleCanvas.setLabel("ExNet 3.0 - "+A.getTitle());
        m_TitleCanvas.centerLabel();
        m_WebpageBasePanel.validate();
        }

/**
 * Used to create an ExptMessageListener connected to the ExptServer.
 *
 * @return Returns an ExptMessageListener connected to the ExptServer.
 * @see girard.sc.expt.io.ExptMessageListener
 * @see girard.sc.expt.io.ExptServer
 */
    public ExptMessageListener createExptML()
        {
        return new ExptMessageListener(HOST_NAME,EXPT_PORT,this);
        }

/**
 * Deletes the BaseAction object under the choosen filename by using the 
 * DeleteBaseActionRegMsg class.
 * <p>
 * fileInfo should have the following format:
 * <br> Key: "FileName" Data: A String that is the Name_VC for the BaseAction file.
 * <br> (Optional) Key: "App ID" Data: A String that is the App_ID value for the file. 
 *
 * @param db The database that the BaseAction files are located in.
 * @param dbTable The table where the BaseAction files are located.
 * @param fileInfo Key information about the file so it can be located withing the table
 * of the database.
 * @return Returns true if sucessfully deleted the file, false otherwise.
 * @see girard.sc.expt.io.msg.DeleteBaseActionReqMsg
 */
    public boolean deleteBaseAction(String db, String dbTable, Hashtable fileInfo)
        {
        Object[] out_args = new Object[3];
        out_args[0] = fileInfo;
        out_args[1] = db;
        out_args[2] = dbTable;
        DeleteBaseActionReqMsg tmp = new DeleteBaseActionReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em instanceof DeleteBaseActionReqMsg)
            {
            return true;
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            return false;
            }
        }
/**
 * Deletes an Experiment file from the database.
 *
 * @param expt The Experiment to be deleted.
 * @return Returns true if successful, false otherwise.
 */
    public boolean deleteExpt(Experiment expt)
        {
        Object[] out_args = new Object[1];
        out_args[0] = expt;
        DeleteExptReqMsg tmp = new DeleteExptReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em instanceof DeleteExptReqMsg)
            {
            return true;
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }

        return false;
        }

/**
 * Creates a URL to a help file from a String listing where it is.  The
 * format of the String is: package/package/helpfile.txt
 * <p>
 * Example: girard/sc/expt/awt/ActionBuilderWindow.txt
 * <p>
 * For more information on the Help File System see the JavaDoc's on the
 * HelpWindow.
 *
 * @param str The location of the help file.
 * @return Returns a URL to the help file, or null if no help file found.
 * @see girard.sc.expt.help.HelpWindow
 * @see java.lang.String
 * @see java.net.URL
 */
    public URL getHelpURL(String str)
        {
        URL helpURL = null;

        try
            {
            helpURL = new URL("http://"+HOST_NAME+HELP_DIR+str);
            }
        catch(MalformedURLException e) { e.printStackTrace(); }
        
        return helpURL;
        }

/**
 * Calls up a HelpWindow if one hasn't already been created.
 *
 * @see girard.sc.expt.help.HelpWindow
 */
    public void helpWindow()
        {
        if (m_help == null)
            m_help = new HelpWindow(this);
        }
/**
 * Calls up a HelpWindow if one hasn't already been created.  Inaddition it also
 * loads the help file specified by the String passed in.
 *
 * @param key The specific help file to display when the HelpWindow is created.
 * @see girard.sc.expt.help.HelpWindow
 */
    public void helpWindow(String key)
        {
        if (m_help == null)
            {
            m_help = new HelpWindow(this);
            m_help.displayHelpPage(key);
            }
        else
            {
            m_help.displayHelpPage(key);
            }
        }

/**
 * Requests a list of Access Groups that can be accessed by the user for granting
 * or determining access rights to a file. Does this by sending a WLAccessGroupListReqMsg.
 * The Vector returned contains a list of Hashtables, each Hashtable has the following
 * format:
 * <br> App_ID stored as a String under the key "App ID"
 * <br> App_Name_VC stored as a String under the key "App Name"
 * <br> App_Desc_VC stored as a String under the key "App Desc"
 * 
 * @return Returns the list of access groups in the form of a Vector of Hashtables.
 * @see girard.sc.wl.io.msg.WLAccessGroupListReqMsg
 */
    public Vector loadAccessGroupList() 
        {
        WLAccessGroupListReqMsg tmp = new WLAccessGroupListReqMsg(null);
        WLMessage wlm = sendWLMessage(tmp);

        if (wlm == null)
            {
            new ErrorDialog("No message, error connecting.");
            return new Vector();
            }

        if (wlm instanceof WLAccessGroupListReqMsg)
            {
            Object[] in_args = wlm.getArgs();
            return (Vector)in_args[0];
            }
        else
            {
            new ErrorDialog((String)wlm.getArgs()[0]);
            return new Vector();
            }
        }
/**
 * Loads the choosen filename by using the LoadBaseActionRegMsg class.  The fileInfo
 * Hashtable should contain the same info as the Hashtables retrieved by
 * loadBaseActionFileList.
 *
 * @param fileInfo The Hashtable of information on the BaseAction file to be loaded.
 * @param ba The actual BaseAction class object type that is being loaded.
 * @return Returns the loaded BaseAction.
 * @see girard.sc.expt.io.msg.LoadBaseActionReqMsg
 * @see girard.sc.expt.web.ExptOverlord#loadBaseActionFileList(String db, String dbTable, Vector ag)
 */
    public BaseAction loadBaseAction(Hashtable fileInfo, BaseAction ba)
        {
        Object[] out_args = new Object[2];
        out_args[0] = fileInfo;
        out_args[1] = ba.clone();
 
        LoadBaseActionReqMsg tmp = new LoadBaseActionReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("No message, error connecting.");
            return null;
            }

        if (em instanceof LoadBaseActionReqMsg)
            {
            Object[] args = em.getArgs();
            return (BaseAction)args[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            return null;
            }
        }
/**
 * Loads a list of all the saved files of a specific BaseAction type for the
 * user.  The Hashtable contains the following information:
 * <br> The filename of the saved file. (Key: "FileName", Object: String)
 * <br> The description of the saved file. (Key: "Desc", Object: String)
 * <br> (The following two objects may be null and thus not in the Hashtable.)
 * <br> The access group id associated with the file. (Key: "App ID", Object: String)
 * <br> The access group name associated with the file. (Key: "App Name", Object: String)
 *
 * @param db The database where the BaseAction files are stored.
 * @param dbTable The database table where the files are stored.
 * @param ag The Vector of access group Hashtable information.
 * @return Returns a Vector of Hashtables for all the files.
 */
    public Vector loadBaseActionFileList(String db, String dbTable, Vector ag) 
        {   
        Object[] out_args = new Object[3];
        out_args[0] = db;
        out_args[1] = dbTable;
        out_args[2] = ag;
        SavedBaseActionFileListReqMsg tmp = new SavedBaseActionFileListReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("No message, error connecting.");
            return new Vector();
            }

        if (em instanceof SavedBaseActionFileListReqMsg)
            {
            Object[] in_args = em.getArgs();
            Vector fileNames = new Vector();
            Vector fileList = (Vector)in_args[0];
            Enumeration enm = fileList.elements();
            while (enm.hasMoreElements())
                {
                Object[] obj = (Object[])enm.nextElement();

                Hashtable h = new Hashtable();
                h.put("FileName",obj[0]);
                h.put("Desc",obj[1]);
                if (obj[2] != null)
                    {
                    h.put("App Name",obj[2]);
                    h.put("App ID",obj[3]);
                    }
                h.put("UserID",obj[4]);

                fileNames.addElement(h);
                }
            return fileNames;
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            return new Vector();
            }
        }
/**
 * Loads an Experiment from the database.
 *
 * @param exp The Experiment to be loaded.
 * @return Returns the entire Experiment from the database.
 */
    public Experiment loadExpt(Experiment exp)
        {
        Object[] out_args = new Object[1];
        out_args[0] = exp;
        LoadExptReqMsg tmp = new LoadExptReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("No message returned, error loading or connecting.");
            return null;
            }

        if (em instanceof LoadExptReqMsg)
            {
            return (Experiment)em.getArgs()[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            return null;
            }
        }
/**
 * Requests a list of saved Experiment files that can be loaded by the user, based
 * on user's ID and user's group access. Does this by sending a 
 * ExptFileListReqMsg.  The Vector of Experiments returns only contains the base
 * information about each Experiment.  All the information for a specific Experiment
 * can be retrieved using loadExpt(Experiment expt).
 * 
 * @param ag The Vector of access groups.
 * @return Returns a Vector of Experiments.
 * @see girard.sc.expt.io.msg.ExptFileListReqMsg
 */
    public Vector loadExptFileList(Vector ag)
        {
        Object[] out_args = new Object[1];
        out_args[0] = ag;
        ExptFileListReqMsg tmp = new ExptFileListReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("No message returned, error loading or connecting.");
            return new Vector();
            }

        if (em instanceof ExptFileListReqMsg)
            {
            Object[] in_args = em.getArgs();
            return (Vector)in_args[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            return new Vector();
            }
        }
/**
 * Calls the loadParameters of the WLOverlord, then loads any new parameters specific
 * to an ExptOverlord object.
 *
 * @param validate Whether to validate the m_appToken or not.
 * @return Returns true if successful, false otherwise.
 */
    public boolean loadParameters(boolean validate)
        {
        boolean value = super.loadParameters(validate);
        HELP_DIR = m_WB.getParameter("HELP_DIR");
        try { EXPT_PORT = Integer.valueOf(m_WB.getParameter("EP")).intValue(); }
        catch (Exception e) { }

        return value;
        }
/**
 * Loads a SimActor file from its approaite database.
 *
 * @param fileName The value of the Sim_Name_VC for the SimActor.
 * @param sa The SimActor class type that the file is.
 * @param ag The Hashtable of the access group related to this SimActor.
 * @return Returns the actual SimActor if successful, null otherwise.
 */
    public SimActor loadSimActor(String fileName, SimActor sa, Hashtable ag)
        {
        Object[] out_args = new Object[3];
        out_args[0] = fileName;
        out_args[1] = sa.clone();
        out_args[2] = ag;
        LoadSimActorReqMsg tmp = new LoadSimActorReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("No message, error connecting.");
            return null;
            }

        if (em instanceof LoadSimActorReqMsg)
            {
            Object[] args = em.getArgs();
            return (SimActor)args[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }

        return null;
        }
/**
 * Retrieves a list of SimActors of a given type that the user has access to.
 * The Vector returns a list of Hashtables each with the following format:
 * <br>Key: "Sim Name" Data: A String containing the value for Sim_Name_VC
 * <br>Key: "Sim Desc" Data: A String containing the value for Sim_Desc_VC
 * <br>(Optional)Key: "App ID" Data: A String containing the value for App_ID
 * 
 * @param sa The SimActor type.
 * @param ag The Vector of access groups that the user has access to.
 * @return Returns a Vector of Hashtables.
 */
    public Vector loadSimActorFileList(SimActor sa,Vector ag) 
        {   
        Object[] out_args = new Object[3];
        out_args[0] = new Integer(sa.getActorTypeID());
        out_args[1] = sa.getDB();
        out_args[2] = ag;
        SimActorFileListReqMsg tmp = new SimActorFileListReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("No message, error connecting.");
            return new Vector();
            }

        if (em instanceof SimActorFileListReqMsg)
            {
            Object[] in_args = em.getArgs();
            return (Vector)in_args[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        return new Vector();
        }
/**
 * Retrieves the list of types of SimActors available for use.
 *
 * @return A Vector of SimActors.
 */
    public Vector loadSimActorTypeList()
        {
        SimActorTypeListReqMsg tmp = new SimActorTypeListReqMsg(null);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("No message, error connecting.");
            return new Vector();
            }

        if (em instanceof SimActorTypeListReqMsg)
            {
            Object[] in_args = em.getArgs();
            return (Vector)in_args[0];
            }
        return new Vector();
        }

/**
 * Let's the ExptOverlord know the HelpWindow is closed.
 *
 * @see girard.sc.expt.help.HelpWindow
 */
    public void removeHelpWindow()
        {
        m_help = null;
        }
/**
 * Removes a WebPanel from being dipslayed in m_WebpageBasePanel and sets m_activePanel
 * to null. Overridden so that the label of the m_TitleCanvas is set to ExNet 3.0 instead
 * of Web-Lab.
 *
 * @param R The WebPanel to be removed.
 */
    public void removePanel(WebPanel R)
        {
        R.removeLabels();
        m_activePanel = null;
        m_WebpageBasePanel.remove(R);
        m_TitleCanvas.setLabel("ExNet 3.0");
        m_TitleCanvas.centerLabel();
        m_WebpageBasePanel.validate();
        }
/**
 * Removes a WebPanel from being dipslayed in m_WebpageBasePanel and then adds 
 * a new WebPanel to be displayed within the m_WebpageBasePanel. Overridden so that 
 * the label of the m_TitleCanvas is set to ExNet 3.0 instead of Web-Lab.
 *
 * @param R The WebPanel to be removed.
 * @param A The WebPanel to be displayed.
 */
    public void removeThenAddPanel(WebPanel R, WebPanel A)
        {
        R.removeLabels();
        m_WebpageBasePanel.remove(R);
        A.initializeLabels();
        m_activePanel = A;
        m_WebpageBasePanel.add(A);
        m_TitleCanvas.setLabel("ExNet 3.0 - "+A.getTitle());
        m_TitleCanvas.centerLabel();
        m_WebpageBasePanel.validate();
        }

/**
 * Saves the BaseAction object under the choosen filename by using the 
 * SaveBaseActionRegMsg class.  The Hashtable for ag is setup with the same
 * format as the Hashtables in loadAccessGroupList.
 *
 * @param ba The BaseAction to be saved.
 * @param ag The access group to attach it to.
 * @see girard.sc.expt.io.msg.SaveBaseActionReqMsg
 */
    public boolean saveBaseAction(BaseAction ba, Hashtable ag)
        {
        Object[] out_args = new Object[2];
        out_args[0] = ba;
        out_args[1] = ag;

        SaveBaseActionReqMsg tmp = new SaveBaseActionReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("Error connecting. File not saved.");
            return false;
            }

        if (em instanceof SaveBaseActionReqMsg)
            {
            return true;
            } 
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }

        return false;
        }
/**
 * Saves a created Experiment to the GirardExptDB database.
 *
 * @param expt The Experiment to be saved.
 */
    public boolean saveExpt(Experiment expt)
        {
        Object[] out_args = new Object[1];
        out_args[0] = expt;
        System.err.println("before creating the nre obj.");
        SaveExptReqMsg tmp = new SaveExptReqMsg(out_args);
        System.err.println("after creating the mew saveexptReqMsg.");
        ExptMessage em = sendExptMessage(tmp);
        System.err.println("after calling the send expt message method.");
        
        if (em == null)
            {
        	System.err.println("got a null ...");
	        new ErrorDialog("Error connecting. File not saved.");
            return false;
            }

        if (em instanceof SaveExptReqMsg)
            {
            return true;
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }

        return false;
        }
/**
 * Saves a built SimActor to its proper database.
 *
 * @param sa The SimActor to be saved.
 */
    public boolean saveSimActor(SimActor sa)
        {
        Object[] out_args = new Object[1];
        out_args[0] = sa;

        SaveSimActorReqMsg tmp = new SaveSimActorReqMsg(out_args);
        ExptMessage em = sendExptMessage(tmp);

        if (em == null)
            {
            new ErrorDialog("Error connecting. File not saved.");
            return false;
            }

        if (em instanceof SaveSimActorReqMsg)
            {
            return true;
            } 
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }

        return false;
        }

/**
 * Sends an ExptMessage to the WLGeneralServer.  Any ExptMessage response
 * is returned.
 *
 * @param em The ExptMessage to be sent.
 * @return Returns the ExptMessage response or null if there was no response.
 * @see girard.sc.expt.io.msg.ExptMessage
 */
    public ExptMessage sendExptMessage(ExptMessage em)
        {
        ExptNetObjectStream nos = new ExptNetObjectStream(HOST_NAME,GENERAL_PORT,this);

        int counter = 0;
        while ((!nos.getFlag()) && (counter < 5))
            {
            nos.retry(HOST_NAME,GENERAL_PORT);
	    // Wait a little time between connection attempts.
            for (int x=0;x<500;x++)
                {
                int i = 1;
                i = i + 5;
                }
            counter++;
            }
        System.err.println("counter value:"+counter);
        if (nos.getFlag())
            {
            try 
                {
            	System.err.println("test1");
                nos.sendMessage(em);
                System.err.println("test");

                ExptMessage emNew = (ExptMessage)nos.getNextMessage();
                System.err.println("test3");

                nos.close();

                return emNew;
                }
            catch (IOException ioe) {System.err.println("ok...here it is"); System.err.println(ioe); }
            catch (ClassNotFoundException cnfe) { System.err.println(cnfe); }
            }

        return null;
        }
    }
