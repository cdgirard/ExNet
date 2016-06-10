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
 * Allows user to save Base Actions they have created to the
 * Web-Lab database system.
 * <p>
 * <br>Started: 02-06-2002
 * <br>Modified: 10-09-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class SaveBaseActionWindow extends Frame implements ActionListener,ItemListener
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
    BaseAction m_BApp;
/**
 * The link to the BaseActionFormatWindow;  This allows you to reset its m_EditMode
 * variable to false when done formating;  Otherwise the ActionBuilderWindow doesn't
 * know when to start processing action events again.
 * 
 */
    BaseActionFormatWindow m_BAFWApp;

/**
 * Displays the name to save the BaseAction file under.
 */
    TextField m_SaveNameField;
/**
 * The displayed list of saved BaseAction files for a specific access group.
 */
    SortedFixedList m_SaveFileList;
/**
 * Displays the name of the selected access group to save the file under.
 */
    TextField m_AccessGroupField;
/**
 * Displays the list of access groups the user has access to.
 */
    SortedFixedList m_AccessGroupList;

/**
 * Used to respectively: Save the file, exit out, and call up the
 * HelpWindow.
 * 
 * @see girard.sc.expt.help.HelpWindow
 */
    Button m_SaveButton, m_CancelButton, m_HelpButton;

/**
 * Displays the description of the selected BaseAction file.
 */
    TextArea m_baDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);
/**
 * Displays the description of the selected access group.
 */
    TextArea m_agDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);

/**
 * Complete list of files already stored in the database that the user has access to.
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
 * The name to save the BaseAction file as.
 */
    String m_saveName = "None";
/**
 * The access group to assign to the BaseAction file when it is saved.
 */
    Hashtable m_accessGroup = null;

/** 
 * Overwrite Frame. This window comes up if the user wants to save using
 * an already existing filename.
 *
 */
    Frame m_CFFOWFrame;
    GridBagPanel m_CFFOWPanel;
/**
 * Respectively for confirming the overwrite or canceling the overwrite.
 */
    Button m_OverwriteYes, m_OverwriteNo;

/**
 * The constructor.
 *
 * @param app1 The ExptOverlord.
 * @param app2 The BaseActionFormatWindow
 * @param app3 The BaseAction class object that is to be saved.
 */
    public SaveBaseActionWindow(ExptOverlord app1, BaseActionFormatWindow app2, BaseAction app3)
        {
        super ();

        m_EOApp = app1;
        m_BAFWApp = app2;
        m_BApp = app3;
        
        initializeLabels();

  //  Setup Button and Label Fields
        setTitle(m_EOApp.getLabels().getObjectLabel("sbaw_title"));
        setBackground(m_EOApp.getWinBkgColor());
        setFont(m_EOApp.getMedWinFont());
        setLayout(new BorderLayout());

    // Construct Center Panel.
        GridBagPanel centerPanel = new GridBagPanel();

        m_SaveFileList = new SortedFixedList(6,false,1,25);
        m_SaveFileList.addItemListener(this);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbaw_sf")),1,1,4,1,GridBagConstraints.CENTER);
        centerPanel.constrain(m_SaveFileList,1,2,4,6,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbaw_description")),1,8,4,1,GridBagConstraints.CENTER);
        m_baDesc.setEditable(false);
        centerPanel.constrain(m_baDesc,1,9,4,4,GridBagConstraints.CENTER);

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

        m_AccessGroupList.select(0);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbaw_ag")),5,1,4,1,GridBagConstraints.CENTER);
        centerPanel.constrain(m_AccessGroupList,5,2,4,6,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbaw_description")),5,8,4,1,GridBagConstraints.CENTER);
        m_agDesc.setEditable(false);
        centerPanel.constrain(m_agDesc,5,9,4,4,GridBagConstraints.CENTER);

        LoadSavedFileList();

        CreateSavedFileList(0);
  // End Construction on Central Panel.

  // Start Construction on South Panel.
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbaw_filename")),1,1,2,1);

        m_SaveNameField = new TextField(20);
        southPanel.constrain(m_SaveNameField,3,1,4,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbaw_ga")),1,2,2,1);

        m_AccessGroupField = new TextField(20);
        m_AccessGroupField.setEditable(false);
        southPanel.constrain(m_AccessGroupField,3,2,4,1);

        m_SaveButton = new Button(m_EOApp.getLabels().getObjectLabel("sbaw_save"));
        m_SaveButton.addActionListener(this);
        southPanel.constrain(m_SaveButton,1,3,2,1);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("sbaw_cancel"));
        m_CancelButton.addActionListener(this);
        southPanel.constrain(m_CancelButton,3,3,2,1);
     
        m_HelpButton = new Button(m_EOApp.getLabels().getObjectLabel("sbaw_help"));
        m_HelpButton.addActionListener(this);
        southPanel.constrain(m_HelpButton,5,3,2,1);
  // End Construction on South Panel.

        m_AccessGroupField.setText(m_AccessGroupList.getSelectedSubItem(0));

        add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));
        add("South",new BorderPanel(southPanel,BorderPanel.FRAME));
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
        int i;
        boolean flag;
      
        if (theSource == m_SaveButton)
            {
            if (m_SaveNameField.getText().trim().length() == 0)
                return;

            m_saveName = m_SaveNameField.getText();
            Hashtable h = (Hashtable)m_accessGroups.elementAt(m_AccessGroupList.getSelectedIndex());
            if (h.containsKey("App ID"))
                m_accessGroup = h;
            else
                m_accessGroup = null;

            flag = true;
            for (i=0;i<m_SaveFileList.getItemCount();i++)
                {
                Hashtable of = (Hashtable)m_listedFiles.elementAt(i);
                String str = (String)of.get("FileName");
                if (str.equals(m_saveName))
                    {
                    CheckForFileOverwrite();
                    flag = false;
                    break;
                    }
                }
            if (flag)
                {
                if (SaveBaseAction())
                    {
                    m_BAFWApp.setEditMode(false);
                    removeLabels();
                    dispose();
                    return;
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("sbaw_ftpstf"));
                    }
                }
            }
        if (theSource == m_CancelButton)
            {
            m_BAFWApp.setEditMode(false);
            removeLabels();
            dispose();
            return; 
            }
        if (theSource == m_HelpButton)
            {
            m_EOApp.helpWindow("ehlp_sbaw");
            return; 
            }
        if (theSource == m_OverwriteYes)
            {
            m_CFFOWFrame.dispose();
            if (SaveBaseAction())
                {
                m_BAFWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            else
                {
                new ErrorDialog(m_EOApp.getLabels().getObjectLabel("sbaw_ftpstf"));
                }
            }
        if (theSource == m_OverwriteNo)
            {
            m_CFFOWFrame.dispose();
            m_SaveButton.addActionListener(this);
            m_CancelButton.addActionListener(this);
            m_SaveNameField.setEditable(true);
            }
        }

/**
 * Requests a list of Access Groups from the ExptOverlord that can be accessed by the 
 * user for granting access rights to the file being saved. Then displays these groups
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
 * Displays a list of saved BaseAction files in m_SavedFileList  accessible by the 
 * user of the type the m_BApp variable is.  The list created is based on the presently
 * selected access group.
 * 
 * @param loc The index of the selected access group.
 */
    public void CreateSavedFileList(int loc) 
        {
        m_listedFiles.removeAllElements();
        m_SaveFileList.removeAll();

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
                m_SaveFileList.addItem(str);

                m_listedFiles.insertElementAt(h2,m_SaveFileList.last);
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
        m_EOApp.initializeLabels("girard/sc/expt/awt/sbaw.txt");
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

            if ((theSource == m_SaveFileList) && (m_SaveFileList.getSelectedIndex() >= 0))
                {
                int index = m_SaveFileList.getSelectedIndex();
                Hashtable h = (Hashtable)m_listedFiles.elementAt(index);
                m_SaveNameField.setText((String)h.get("FileName"));
                m_baDesc.setText((String)h.get("Desc"));
                }

            if ((theSource == m_AccessGroupList) && (m_AccessGroupList.getSelectedIndex() >= 0))
                {
                int index = m_AccessGroupList.getSelectedIndex();
                Hashtable h = (Hashtable)m_accessGroups.elementAt(index);
                m_AccessGroupField.setText((String)h.get("App Name"));
                m_agDesc.setText((String)h.get("App Desc"));
                CreateSavedFileList(index);
                m_baDesc.setText("");
                }
            }
        }

/**
 * Loads the list of saved BaseAction files of a specific type by calling the
 * loadBaseActionFileList function in ExptOverlord.
 *
 * @see girard.sc.expt.web.ExptOverlord
 */
    public void LoadSavedFileList() 
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
        m_EOApp.removeLabels("girard/sc/expt/awt/sbaw.txt");
        }

/**
 * Saves the BaseAction object under the choosen filename by calling saveBaseAction 
 * in ExptOverlord.
 *
 * @see girard.sc.expt.web.ExptOverlord
 */
    public boolean SaveBaseAction()
        {
        m_BApp.setFileName(m_saveName);

        return m_EOApp.saveBaseAction(m_BApp,m_accessGroup);
        }

/**
 * Checks to make sure the user wants to save over a specific file.
 */
    private void CheckForFileOverwrite()
        {
        m_CFFOWFrame = new Frame("File Already Exists");
        m_CFFOWFrame.setLayout(new GridLayout(1,1));
        m_CFFOWFrame.setBackground(m_EOApp.getWinBkgColor());
        m_CFFOWFrame.setFont(m_EOApp.getMedWinFont());

        m_CFFOWPanel = new GridBagPanel();

        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbaw_fae")),1,1,4,1);
        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbaw_dywtoi")),1,2,4,1);

        m_OverwriteYes = new Button(m_EOApp.getLabels().getObjectLabel("sbaw_yes"));
        m_OverwriteYes.addActionListener(this);

        m_OverwriteNo = new Button(m_EOApp.getLabels().getObjectLabel("sbaw_no"));
        m_OverwriteNo.addActionListener(this);

        m_CFFOWPanel.constrain(m_OverwriteYes,1,3,2,1);
        m_CFFOWPanel.constrain(m_OverwriteNo,3,3,2,1);

        m_SaveButton.removeActionListener(this);
        m_CancelButton.removeActionListener(this);
        m_SaveNameField.setEditable(false);
 
        m_CFFOWFrame.add(m_CFFOWPanel);
        m_CFFOWFrame.pack();
        m_CFFOWFrame.show();
        }
    }