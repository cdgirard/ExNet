package girard.sc.expt.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
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
 * Used to delete Base Actions files from the database. Prompts user before each
 * file is actually deleted.
 * <p>
 * <br>Started: 02-13-2002
 * <br>Modified: 10-09-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class DeleteBaseActionWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
/**
 * The BaseAction object being saved.
 *
 */
    BaseActionFormatWindow m_BAFWApp;
/**
 * The link to the BaseActionFormatWindow;  This allows you to reset its m_EditMode
 * variable to false when done formating;  Otherwise the ActionBuilderWindow doesn't
 * know when to start processing action events again.
 * 
 */
    BaseAction m_BApp;

/**
 * The displayed list of saved BaseAction files that can be deleted.
 */
    SortedFixedList m_SavedFileList;
/**
 * Displays the list of access groups the user has access to.
 */
    SortedFixedList m_AccessGroupList;
/**
 * Displays the selected BaseAction file that is to be deleted.
 */
    TextField m_FileNameField;
/**
 * Used to respectively: Delete the selected file, exit out, and call up the
 * HelpWindow.
 * 
 * @see girard.sc.expt.help.HelpWindow
 */
    Button m_DeleteButton, m_DoneButton, m_HelpButton;

/**
 * The file info for the presently selected file to be deleted.
 */
    Hashtable m_fileInfo = new Hashtable();
/**
 * The list index for the presently selected file to be deleted.
 */
    int m_deleteIndex = -1;

/**
 * Displays the description of the selected BaseAction file.
 */
    TextArea m_baDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);
/**
 * Displays the description of the selected access group.
 */
    TextArea m_agDesc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

/**
 * List of files already stored in the database.
 */
    Vector m_oldFiles = new Vector();

/**
 * The list of files presently displayed in m_SavedFileList.
 */
    Vector m_listedFiles = new Vector();
/**
 * The list of access groups displayed in m_AccessGroupList.
 */
    Vector m_accessGroups = new Vector();

/**
 * The Confirm deletion frame.
 */
    Frame m_CFFOWFrame;
    GridBagPanel m_CFFOWPanel;
/**
 * Respectively for confirming the deletion or canceling the deletion.
 */
    Button m_OverwriteYes, m_OverwriteNo;

/**
 * The constructor.
 *
 * @param app1 The ExptOverlord.
 * @param app2 The BaseActionFormatWindow
 * @param app3 The BaseAction class object type to delete files from.
 */
    public DeleteBaseActionWindow(ExptOverlord app1, BaseActionFormatWindow app2, BaseAction app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_BAFWApp = app2; /* Need so can unset edit mode */
        m_BApp = app3;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getWinBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("dbaw_title"));
        setFont(m_EOApp.getMedWinFont());

    // Start Setup For Center Panel
        GridBagPanel CenterPanel = new GridBagPanel();

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dbaw_files")),1,1,4,1,GridBagConstraints.CENTER);

        m_SavedFileList = new SortedFixedList(8,false,1,25);
        m_SavedFileList.addItemListener(this);

        CenterPanel.constrain(m_SavedFileList,1,2,4,8,GridBagConstraints.CENTER);

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dbaw_description")),1,10,4,1,GridBagConstraints.CENTER);
        m_baDesc.setEditable(false);
        CenterPanel.constrain(m_baDesc,1,11,4,4,GridBagConstraints.CENTER);

        m_AccessGroupList = new SortedFixedList(8,false,1,25);
        m_AccessGroupList.addItemListener(this);

        String[] str = new String[1];
        str[0] = "<NONE>";
        m_AccessGroupList.addItem(str);
        Hashtable h = new Hashtable();
        h.put("Name","<NONE>");
        h.put("Desc","Only this user may access this file.");
        m_accessGroups.addElement(h);

        CreateAccessGroupList();

        m_AccessGroupList.select(0);

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dbaw_ag")),5,1,4,1,GridBagConstraints.CENTER);
        CenterPanel.constrain(m_AccessGroupList,5,2,4,8,GridBagConstraints.CENTER);

        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dbaw_description")),5,10,4,1,GridBagConstraints.CENTER);
        m_agDesc.setEditable(false);
        CenterPanel.constrain(m_agDesc,5,11,4,4,GridBagConstraints.CENTER);

        LoadFileList();

        CreateSavedFileList(0);
    // End Setup For Center Panel 

    // Start Setup For South Panel
        GridBagPanel SouthPanel = new GridBagPanel();

        SouthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dbaw_sf")),1,1,2,1,GridBagConstraints.CENTER);

        m_FileNameField = new TextField(15);
        m_FileNameField.setEditable(false);
        SouthPanel.constrain(m_FileNameField,3,1,4,1,GridBagConstraints.WEST);

        m_DeleteButton = new Button(m_EOApp.getLabels().getObjectLabel("dbaw_delete"));
        m_DeleteButton.addActionListener(this);
        SouthPanel.constrain(m_DeleteButton,1,2,2,1,GridBagConstraints.CENTER);

        m_DoneButton = new Button(m_EOApp.getLabels().getObjectLabel("dbaw_done"));
        m_DoneButton.addActionListener(this);
        SouthPanel.constrain(m_DoneButton,3,2,2,1,GridBagConstraints.CENTER);

        m_HelpButton = new Button(m_EOApp.getLabels().getObjectLabel("dbaw_help"));
        m_HelpButton.addActionListener(this);
        SouthPanel.constrain(m_HelpButton,5,2,2,1,GridBagConstraints.CENTER);

        add("Center",new BorderPanel(CenterPanel,BorderPanel.FRAME));
        add("South",new BorderPanel(SouthPanel,BorderPanel.FRAME));

        pack();
        show();
        }

/**
 * Used to process action events.
 * 
 * @param e The ActionEvent to process.
 */
    public void actionPerformed (ActionEvent e)
        {
        Button theSource = (Button)e.getSource();
       
        if (theSource == m_DoneButton)
            {
            // Handle Done
            m_BAFWApp.setEditMode(false);
            dispose();
            }
        if (theSource == m_DeleteButton)
            {
            if (m_SavedFileList.getSelectedIndex() > -1)
                {
                m_fileInfo = (Hashtable)m_listedFiles.elementAt(m_SavedFileList.getSelectedIndex());
                m_deleteIndex = m_SavedFileList.getSelectedIndex();
                CheckForFileOverwrite();
                }
            }
        if (theSource == m_HelpButton)
            {
            // Handle Help
            m_EOApp.helpWindow("ehlp_dbaw");
            }
        if (theSource == m_OverwriteYes)
            {
            m_CFFOWFrame.dispose();
            m_DeleteButton.addActionListener(this);
            m_DoneButton.addActionListener(this);
            if (DeleteBaseAction(m_fileInfo))
                {
                }
            else
                {
                new ErrorDialog(m_EOApp.getLabels().getObjectLabel("dbaw_ftdtf"));
                }
            }
        if (theSource == m_OverwriteNo)
            {
            m_CFFOWFrame.dispose();
            m_DeleteButton.addActionListener(this);
            m_DoneButton.addActionListener(this);
            }
        }

/**
 * Requests a list of Access Groups from the ExptOverlord that can be accessed by the 
 * user for granting access rights to the file being deleted. Then displays these groups
 * in the m_AccessGroupList.
 * 
 * @see girard.sc.expt.web.ExptOverlord
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
 * Displays a list of BaseAction files in m_SavedFileList that can be deleted by the 
 * user of the type the m_BApp variable is.  The list created is based on the presently
 * selected access group.
 * 
 * @param loc The index of the selected access group.
 */
    public void CreateSavedFileList(int loc) 
        {   
        m_listedFiles.removeAllElements();
        m_SavedFileList.removeAll();

        Hashtable h = (Hashtable)m_accessGroups.elementAt(loc);
        String uid = new String("-");
        if (h.containsKey("App ID"))
            uid = (String)h.get("App ID");

        Enumeration enm = m_oldFiles.elements();
        while (enm.hasMoreElements())
            {
            Hashtable h2 = (Hashtable)enm.nextElement();

            String uid2 = new String("-");
            if (h2.containsKey("App ID"))
                uid2 = (String)h2.get("App ID");

            if (uid.equals(uid2))
                {
                String[] str = new String[1];
                str[0] = (String)h2.get("FileName");
                m_SavedFileList.addItem(str);

                m_listedFiles.insertElementAt(h2,m_SavedFileList.last);
                }
            }
        }

/**
 * Deletes the BaseAction object under the choosen filename by calling deleteBaseAction
 * in ExptOverlord.
 *
 * @see girard.sc.expt.web.ExptOverlord
 */
    public boolean DeleteBaseAction(Hashtable fileInfo)
        {

        if (m_EOApp.deleteBaseAction(m_BApp.getDB(),m_BApp.getDBTable(),fileInfo))
            {
            m_SavedFileList.deselect(m_deleteIndex);
            m_SavedFileList.remove(m_deleteIndex);
            m_FileNameField.setText("");
            m_listedFiles.removeElementAt(m_deleteIndex);
            m_baDesc.setText("");
            m_oldFiles.removeElement(fileInfo);
            return true;
            }
        else
            {
            return false;
            }
        }

/**
 * Used to process ItemEvents.
 * 
 * @param e The ItemEvent to process.
 */  
    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

        // if clicked on an item in NodeList determine which item then
        // update the other lists.

            if ((theSource == m_SavedFileList) && (theSource.getSelectedIndex() > -1))
                {
                int index = theSource.getSelectedIndex();
                Hashtable h = (Hashtable)m_listedFiles.elementAt(index);
                m_FileNameField.setText((String)h.get("FileName"));
                m_baDesc.setText((String)h.get("Desc"));
                }
            if ((theSource == m_AccessGroupList) && (m_AccessGroupList.getSelectedIndex() >= 0))
                {
                int index = m_AccessGroupList.getSelectedIndex();
                Hashtable h = (Hashtable)m_accessGroups.elementAt(index);
                m_agDesc.setText((String)h.get("App Desc"));
                CreateSavedFileList(index);
                m_FileNameField.setText("");
                m_baDesc.setText("");
                }
            }
        }

/**
 * Used to update the WebResourceBundle with any new entries for this window.
 * <p>
 * Example Code: m_EOApp.initializeLabels("girard/sc/expt/awt/dbaw.txt");
 * <p>
 * @see girard.sc.web.WebResourceBundle
 */
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/dbaw.txt");
        }  

/**
 * Loads the list of saved BaseAction files of a specific type by calling the
 * loadBaseActionFileList function in ExptOverlord.
 *
 * @see girard.sc.expt.web.ExptOverlord
 */
    public void LoadFileList() 
        {
        m_oldFiles = m_EOApp.loadBaseActionFileList(m_BApp.getDB(),m_BApp.getDBTable(),m_accessGroups);
        }

/**
 * Used to update the WebResourceBundle by removing any entries for this window.
 * <p>
 * Example Code: m_EOApp.removeLabels("girard/sc/expt/awt/dbaw.txt");
 * <p>
 * @see girard.sc.web.WebResourceBundle
 */
    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/dbaw.txt");
        }

/**
 * Confirms the request to delete the selected file.
 */
    private void CheckForFileOverwrite()
        {
        m_CFFOWFrame = new Frame("Are You Sure");
        m_CFFOWFrame.setLayout(new GridLayout(1,1));
        m_CFFOWFrame.setBackground(m_EOApp.getWinBkgColor());
        m_CFFOWFrame.setFont(m_EOApp.getMedWinFont());

        m_CFFOWPanel = new GridBagPanel();

        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dbaw_dywtdtf")),1,1,4,1);

        m_OverwriteYes = new Button(m_EOApp.getLabels().getObjectLabel("dbaw_yes"));
        m_OverwriteYes.addActionListener(this);

        m_OverwriteNo = new Button(m_EOApp.getLabels().getObjectLabel("dbaw_no"));
        m_OverwriteNo.addActionListener(this);

        m_CFFOWPanel.constrain(m_OverwriteYes,1,2,2,1);
        m_CFFOWPanel.constrain(m_OverwriteNo,3,2,2,1);

        m_DeleteButton.removeActionListener(this);
        m_DoneButton.removeActionListener(this);
 
        m_CFFOWFrame.add(m_CFFOWPanel);
        m_CFFOWFrame.pack();
        m_CFFOWFrame.show();
        }
    }