package girard.sc.expt.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to delete an Experiment from the database.  Lists all previously saved
 * experiments by user and group.  Group access is determined by which software
 * applications the user is granted read and write access to through the Web-Lab.
 * <p>
 * <br> Started: 2000
 * <br> Modified: 10-24-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class DeleteExptWindow extends Frame implements ActionListener,ItemListener
{
    /**
     * Allows access to ExptOverlord's functions, key among them being the ones
     * dealing with the WebResourceBundle and the sending of ExptMessages.
     *
     */
    ExptOverlord m_EOApp;
    /**
     * Allows access to the ExptBuilderWindow so we can reset the
     * m_editMode to false.
     */
    ExptBuilderWindow m_EBWApp;

    /**
     * The MenuBar for the Frame.
     */
    MenuBar m_mbar = new MenuBar();
    /**
     * For displaying the help Menu in the MenuBar.
     */
    Menu m_Help;

    /**
     * Where the name of the experiment to be saved is typed.
     */
    TextField m_ExptNameField;
    /**
     * Where a partial listing of experiments saved by the user is displayed.
     */
    SortedFixedList m_SavedExptList;

    /**
     * Which access group is presently selected, determines which experiments are listed
     * in the m_SavedExptList.
     */ 
    TextField m_AccessGroupField;
    /**
     * The list of all the access groups the user has access to.
     */
    SortedFixedList m_AccessGroupList;

    /**
     * The buttons that either delete the Experiment from the database, or exit back to the
     * ExptBuilderWindow.
     */
    Button m_DeleteButton, m_DoneButton;

    /**
     * Contains a list of all Experiments saved that this user has access to.
     */
    Vector m_allExperiments = new Vector();

    /**
     * Contains a list of the Experiments presently displayed in the m_SavedExptList.
     */
    Vector m_listedExpts = new Vector();

    /**
     * Contains a list of all access groups the user can get to.
     */
    Vector m_accessGroups = new Vector();

    /**
     * Displays the description of the presently selected Experiment in the m_SaveExptList.
     */
    TextArea m_exptDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);
    /**
     * Displays the description of the presently selected access group in the 
     * m_AccessGroupList.
     */
    TextArea m_agDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);

    /**
     * The Experiment to be removed, we need this for when checking for file overwrite.
     */
    Experiment m_selectedExpt = null;
    int m_deleteIndex = -1;

    /**
     * Popup window for making sure we want to delete a file.
     */
    Frame m_CFFOWFrame;
    GridBagPanel m_CFFOWPanel;
    Button m_OverwriteNo, m_OverwriteYes;


    public DeleteExptWindow(ExptOverlord app1, ExptBuilderWindow app2){
        super();
	
        m_EOApp = app1; /* Need to make pretty buttons. */
        m_EBWApp = app2; /* Need so can unset edit mode */
	
        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getWinBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("dew_title"));
        setFont(m_EOApp.getMedWinFont());

	// Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

	// Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("dew_help"));
        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("dew_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);
        m_mbar.add(m_Help);
	// End Setup for Menubar

	// Start Setup For Center Panel
        GridBagPanel CenterPanel = new GridBagPanel();
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dew_experiments")),1,1,4,1,GridBagConstraints.CENTER);
        m_SavedExptList = new SortedFixedList(6,false,1,25);
        m_SavedExptList.addItemListener(this);
        CenterPanel.constrain(m_SavedExptList,1,2,4,6,GridBagConstraints.CENTER);
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dew_description")),1,8,4,1,GridBagConstraints.CENTER);
        m_exptDesc.setEditable(false);
        CenterPanel.constrain(m_exptDesc,1,9,4,4,GridBagConstraints.CENTER);
        m_AccessGroupList = new SortedFixedList(6,false,1,25);
        m_AccessGroupList.addItemListener(this);
        String[] str = new String[1];
        str[0] = "<NONE>";
        m_AccessGroupList.addItem(str);
        Hashtable h = new Hashtable();
        h.put("Name","<NONE>");
        h.put("Desc","Only this user may access this file.");
        m_accessGroups.addElement(h);
        CreateAccessGroupList();
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dew_ag")),5,1,4,1,GridBagConstraints.CENTER);
        CenterPanel.constrain(m_AccessGroupList,5,2,4,6,GridBagConstraints.CENTER);
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dew_description")),5,8,4,1,GridBagConstraints.CENTER);
        m_agDesc.setEditable(false);
        CenterPanel.constrain(m_agDesc,5,9,4,4,GridBagConstraints.CENTER);
        LoadExptFileList();
        m_AccessGroupList.select(0);
        

        CreateExptFileList(0);
	// End Setup For Center Panel 

	// Start Setup For South Panel
        GridBagPanel SouthPanel = new GridBagPanel();

        SouthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dew_sf")),1,1,2,1,GridBagConstraints.CENTER);
        m_ExptNameField = new TextField(20);
        m_ExptNameField.setEditable(false);
        SouthPanel.constrain(m_ExptNameField,3,1,2,1);
        SouthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dew_ga")),1,2,2,1);
        m_AccessGroupField = new TextField(20);
        m_AccessGroupField.setEditable(false);
        SouthPanel.constrain(m_AccessGroupField,3,2,4,1);
        m_AccessGroupField.setText(m_AccessGroupList.getSelectedSubItem(0));
        m_DeleteButton = new Button(m_EOApp.getLabels().getObjectLabel("dew_delete"));
        m_DeleteButton.addActionListener(this);
        SouthPanel.constrain(m_DeleteButton,1,3,2,1,GridBagConstraints.CENTER);
        m_DoneButton = new Button(m_EOApp.getLabels().getObjectLabel("dew_done"));
        m_DoneButton.addActionListener(this);
        SouthPanel.constrain(m_DoneButton,3,3,2,1,GridBagConstraints.CENTER);
        add("Center",new BorderPanel(CenterPanel,BorderPanel.FRAME));
        add("South",new BorderPanel(SouthPanel,BorderPanel.FRAME));

        pack();
        show();
    }

    public void actionPerformed (ActionEvent e){
	if (e.getSource() instanceof Button){
	    Button theSource = (Button)e.getSource();
	    if (theSource == m_DoneButton){
                // Handle Save
                m_EBWApp.setEditMode(false);
                dispose();
                return;
	    }
            if (theSource == m_DeleteButton){
                if (m_SavedExptList.getSelectedIndex() > -1){
                    m_deleteIndex = m_SavedExptList.getSelectedIndex();
                    m_selectedExpt = (Experiment)m_listedExpts.elementAt(m_SavedExptList.getSelectedIndex());
                    CheckForOverwrite();
		}
	    }
            if (theSource == m_OverwriteYes){
                if (DeleteExpt(m_selectedExpt)){
                m_CFFOWFrame.dispose();
                m_DoneButton.addActionListener(this);
                m_DeleteButton.addActionListener(this);
		}
                else{
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("dew_ftdtf"));
		}
	    }
            if (theSource == m_OverwriteNo){
                m_CFFOWFrame.dispose();
                m_DoneButton.addActionListener(this);
                m_DeleteButton.addActionListener(this);
	    }
	}

        if (e.getSource() instanceof MenuItem)
            {
		MenuItem theSource = (MenuItem)e.getSource();

		// Help Menu
		if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("dew_help")))
		    {
			m_EOApp.helpWindow("ehlp_dew");
		    }
            }
    }

    /**
     * Creates a window to check to make sure that the user does indeed want to delete 
     * the selected Experiemnt from the database.
     */
    public void CheckForOverwrite()
    {
        m_CFFOWFrame = new Frame("Are You Sure about this?");
        m_CFFOWFrame.setLayout(new GridLayout(1,1));
        m_CFFOWFrame.setBackground(m_EOApp.getWinBkgColor());
        m_CFFOWFrame.setFont(m_EOApp.getMedWinFont());

        m_CFFOWPanel = new GridBagPanel();

        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dew_ays")),1,1,4,1);
        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dew_dywtoi")),1,2,4,1);

        m_OverwriteYes = new Button(m_EOApp.getLabels().getObjectLabel("dew_yes"));
        m_OverwriteYes.addActionListener(this);

        m_OverwriteNo = new Button(m_EOApp.getLabels().getObjectLabel("dew_no"));
        m_OverwriteNo.addActionListener(this);

        m_CFFOWPanel.constrain(m_OverwriteYes,1,3,2,1);
        m_CFFOWPanel.constrain(m_OverwriteNo,3,3,2,1);

        m_DoneButton.removeActionListener(this);
        m_DeleteButton.removeActionListener(this);
 
        m_CFFOWFrame.add(m_CFFOWPanel);
        m_CFFOWFrame.pack();
        m_CFFOWFrame.show();
    }


    /**
     * Requests a list of Access Groups that can be accessed by the user for granting
     * access rights to the file being saved. Does this by sending a WLAccessGroupListReqMsg.
     * 
     * @see girard.sc.wl.io.msg.WLAccessGroupListReqMsg
     */
    public void CreateAccessGroupList() 
    {
        Vector accessGroups = m_EOApp.loadAccessGroupList();
        Enumeration enm = accessGroups.elements();
        while (enm.hasMoreElements())
            {
		Hashtable h = (Hashtable)enm.nextElement();
		String[] str = new String[1];
		str[0] = (String)h.get("App Name");
		m_AccessGroupList.addItem(str);
		m_accessGroups.insertElementAt(h,m_AccessGroupList.last);
            }
    }
    /**
     * Fills in the m_SavedExptList and m_listedExpts based on the selected access group.
     *
     * @param loc The index of the access group in m_accessGroups.
     */
    public void CreateExptFileList(int loc) 
    {
        m_listedExpts.removeAllElements();
        m_SavedExptList.removeAll();

        Hashtable h = (Hashtable)m_accessGroups.elementAt(loc);
        String uid = new String("-");
        if (h.containsKey("App ID"))
            uid = (String)h.get("App ID");

        Enumeration enm = m_allExperiments.elements();
        while (enm.hasMoreElements())
            {
		Experiment expt = (Experiment)enm.nextElement();

		String uid2 = new String("-");
		if (expt.getAppID() != null)
		    uid2 = expt.getAppID();

		if (uid.equals(uid2))
		    {
			String[] str = new String[1];
			str[0] = expt.getExptName();
			m_SavedExptList.addItem(str);

			m_listedExpts.insertElementAt(expt,m_SavedExptList.last);
		    }
            }
    }

    public boolean DeleteExpt(Experiment expt)
    {
        if (m_EOApp.deleteExpt(expt))
            { 
		m_listedExpts.removeElementAt(m_deleteIndex);
		m_SavedExptList.deselect(m_deleteIndex);
		m_SavedExptList.remove(m_deleteIndex);
		m_ExptNameField.setText("");
		m_exptDesc.setText("");
		m_allExperiments.removeElement(expt);
		return true;
            }

        return false;
    }

    public void initializeLabels()
    {
        m_EOApp.initializeLabels("girard/sc/expt/awt/dew.txt");
    }    

    public void itemStateChanged(ItemEvent e)
    {
        if (e.getSource() instanceof SortedFixedList)
            {
		SortedFixedList theSource = (SortedFixedList)e.getSource();

		// if clicked on an item in NodeList determine which item then
		// update the other lists.

		if ((theSource == m_SavedExptList) && (m_SavedExptList.getSelectedIndex() >= 0))
		    {
			int index = m_SavedExptList.getSelectedIndex();
			Experiment expt = (Experiment)m_listedExpts.elementAt(index);
			m_ExptNameField.setText(expt.getExptName());
			m_exptDesc.setText(expt.getExptDesc());
		    }

		if ((theSource == m_AccessGroupList) && (m_AccessGroupList.getSelectedIndex() >= 0))
		    {
			int index = m_AccessGroupList.getSelectedIndex();
			Hashtable h = (Hashtable)m_accessGroups.elementAt(index);
			m_AccessGroupField.setText((String)h.get("App Name"));
			m_agDesc.setText((String)h.get("App Desc"));
			CreateExptFileList(index);
			m_exptDesc.setText("");
		    }

		return;
            }
    }

    /**
     * Requests a list of saved Experiment files that can be loaded by the user, based
     * on user's ID and user's group access. Does this by sending a 
     * ExptFileListReqMsg.
     * 
     * @see girard.sc.expt.io.msg.ExptFileListReqMsg
     */
    public void LoadExptFileList()
    {
        m_allExperiments = m_EOApp.loadExptFileList(m_accessGroups);
    }

    public void removeLabels()
    {
        m_EOApp.removeLabels("girard/sc/expt/awt/dew.txt");
    }
}
