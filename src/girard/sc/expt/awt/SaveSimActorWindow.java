package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.obj.SimActor;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/** 
 * Allows user to save SimActors of a specific type that they have created to the
 * appropriate ExNet 3.0 database.
 * <p>
 * <br> Started: 2-27-2001
 * <br> Modified: 11-1-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class SaveSimActorWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
/**
 * The FormatSimActorWindow that the SimActor was created in.
 */
    FormatSimActorWindow m_FSAWApp;
/**
 * The SimActor that we wish to save.
 */
    SimActor m_activeActor;

/**
 * The MenuBar for the window.
 */
    MenuBar m_mbar = new MenuBar();
/**
 * The Help Menu.
 */
    Menu m_Help;

/**
 * Where the filename for the SimActor to be saved is displayed and entered.
 */
    TextField m_SaveNameField;
/**
 * Displays a list of already saved SimActors of the same type.
 */
    SortedFixedList m_SaveFileList;
/**
 * A list of all accessible SimActors of this type for the user.
 */
    Vector m_allActors = new Vector();
/**
 * A list of the SimActors displayed in m_SaveFileList of this type for the user.
 */
    Vector m_listedActors = new Vector();

/**
 * Where the access group name for the SimActor to be saved is displayed.
 */
    TextField m_AccessGroupField;
/**
 * Lists the possible access groups that the SimActor file may be attached to.
 */
    SortedFixedList m_AccessGroupList;
/**
 * The list of access groups available to the user.
 */
    Vector m_accessGroups = new Vector();

/**
 * Used to either try and save the SimActor or cancel out, respectively.
 */
    Button m_SaveButton, m_CancelButton;

/**
 * Overwrite Frame Variables.
 */
    Frame m_CFFOWFrame;
    GridBagPanel m_CFFOWPanel;
    Button m_OverwriteYes, m_OverwriteNo;

/**
 * The constructor.
 *
 * @param app1 The ExptOverlord to set m_EOApp to.
 * @param app2 The FormatSimActorWindow to set m_FSAWApp to.
 * @param app3 The SimActor to set m_activeActor to.
 */
    public SaveSimActorWindow(ExptOverlord app1, FormatSimActorWindow app2, SimActor app3)
        {
        super ();

        m_EOApp = app1;
        m_FSAWApp = app2;
        m_activeActor = app3;

        initializeLabels();

        setTitle(m_EOApp.getLabels().getObjectLabel("ssaw_title"));
        setBackground(m_EOApp.getWinBkgColor());
        setFont(m_EOApp.getMedWinFont());
        setLayout(new GridLayout(1,1));

  // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
     
     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ssaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ssaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

   // Setup the saveFramePanel.
        GridBagPanel saveFramePanel = new GridBagPanel();

        saveFramePanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ssaw_saf")),1,1,4,1);
        m_SaveFileList = new SortedFixedList(8,false,1,20);
        m_SaveFileList.addItemListener(this);
        saveFramePanel.constrain(m_SaveFileList,1,2,4,8);

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

        saveFramePanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ssaw_ag")),5,1,4,1,GridBagConstraints.CENTER);
        saveFramePanel.constrain(m_AccessGroupList,5,2,4,8,GridBagConstraints.CENTER);

        m_allActors = m_EOApp.loadSimActorFileList(m_activeActor,m_accessGroups);

        CreateSavedFileList(0);

        saveFramePanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ssaw_filename")),1,10,2,1);

        m_SaveNameField = new TextField(20);
        saveFramePanel.constrain(m_SaveNameField,3,10,6,1);
        
        saveFramePanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ssaw_ga")),1,11,2,1);

        m_AccessGroupField = new TextField(20);
        m_AccessGroupField.setEditable(false);
        saveFramePanel.constrain(m_AccessGroupField,3,11,6,1);

        m_SaveButton = new Button(m_EOApp.getLabels().getObjectLabel("ssaw_save"));
        m_SaveButton.addActionListener(this);
        saveFramePanel.constrain(m_SaveButton,1,12,4,1,GridBagConstraints.CENTER);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("ssaw_cancel"));
        m_CancelButton.addActionListener(this);
        saveFramePanel.constrain(m_CancelButton,5,12,4,1,GridBagConstraints.CENTER);

        add(saveFramePanel);
        pack();
        show();
        }

/**
 * Processes any ActionEvents.  If the saving of a SimActor is successful or the cancel
 * button is pressed the the m_EditMode of m_FSAWApp is set to false.
 *
 * @param e The ActionEvent that trigger the function call.
 */
    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
            int i;
            boolean flag;
      
            if (theSource == m_SaveButton)
                {
                if (m_SaveNameField.getText().trim().length() == 0)
                    return;

                int index = m_AccessGroupList.getSelectedIndex();
                Hashtable h = (Hashtable)m_accessGroups.elementAt(index);

                m_activeActor.setActorName(m_SaveNameField.getText());
                if (h.containsKey("App ID"))
                    {
                    m_activeActor.setAppID((String)h.get("App ID"));
                    m_activeActor.setAppName((String)h.get("App Name"));
                    }
                else
                    {
                    m_activeActor.setAppID(null);
                    m_activeActor.setAppName(null);
                    }

                flag = true;
                for (i=0;i<m_SaveFileList.getItemCount();i++)
                    {
                    if (m_SaveFileList.getSubItem(i,0).equals(m_SaveNameField.getText()))
                        {
                        CheckForFileOverwrite();
                        flag = false;
                        break;
                        }
                    }
                if (flag)
                    {
                    if (m_EOApp.saveSimActor(m_activeActor))
                        {
                        m_FSAWApp.setEditMode(false);
                        removeLabels();
                        dispose();
                        return;
                        }
                    else
                        {
                        new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ssaw_ftpstf"));
                        }
                    }
                }
            if (theSource == m_CancelButton)
                {
                m_FSAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return; 
                }   
            if (theSource == m_OverwriteYes)
                {
                m_CFFOWFrame.dispose();
                if (m_EOApp.saveSimActor(m_activeActor))
                    {
                    m_FSAWApp.setEditMode(false);
                    removeLabels();
                    dispose();
                    return;
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ssaw_ftpstf"));
                    m_SaveButton.addActionListener(this);
                    m_CancelButton.addActionListener(this);
                    }
                }
            if (theSource == m_OverwriteNo)
                {
                m_CFFOWFrame.dispose();
                m_SaveButton.addActionListener(this);
                m_CancelButton.addActionListener(this);
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ssaw_help")))
                {
                m_EOApp.helpWindow("ehlp_ssaw");
                }
            }

        }

/**
 * Requests a list of Access Groups that can be accessed by the user for granting
 * access rights to the file being saved.  This list is then stored in m_accessGroups
 * and displayed in the SortedFixedList m_AccessGroupList.
 * 
 * @see girard.sc.expt.web.ExptOverlord#loadAccessGroupList()
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
 * Creates a list of saved SimActor files that can be loaded by the user  based
 * on the selected access group in m_AccessGroupList.
 *
 * @param loc The selected index of m_AccessGroupList.
 */
    public void CreateSavedFileList(int loc) 
        {
        m_listedActors.removeAllElements();
        m_SaveFileList.removeAll();

        Hashtable h = (Hashtable)m_accessGroups.elementAt(loc);
        String uid = new String("-");
        if (h.containsKey("App ID"))
            uid = (String)h.get("App ID");

        Enumeration enm = m_allActors.elements();
        while (enm.hasMoreElements())
            {
            Hashtable h2 = (Hashtable)enm.nextElement();

            String uid2 = new String("-");
            if (h2.containsKey("App ID"))
                uid2 = (String)h2.get("App ID");

            if (uid.equals(uid2))
                {
                String[] str = new String[1];
                str[0] = (String)h2.get("Sim Name");
                m_SaveFileList.addItem(str);

                m_listedActors.insertElementAt(h2,m_SaveFileList.last);
                }
            }
        }

/**
 * Used to update the WebResourceBundle with any new entries for this window.
 *
 * @see girard.sc.web.WebResourceBundle
 */
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/ssaw.txt");
        }  

/**
 * Used to process any ItemEvents.
 *
 * @param ie The ItemEvent that triggered this function.
 */
    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_SaveFileList) && (m_SaveFileList.getSelectedIndex() >= 0))
                {
                int index = m_SaveFileList.getSelectedIndex();
                Hashtable h = (Hashtable)m_listedActors.elementAt(index);
                m_SaveNameField.setText((String)h.get("Sim Name"));
                }

            if ((theSource == m_AccessGroupList) && (m_AccessGroupList.getSelectedIndex() >= 0))
                {
                int index = m_AccessGroupList.getSelectedIndex();
                Hashtable h = (Hashtable)m_accessGroups.elementAt(index);
                m_AccessGroupField.setText((String)h.get("App Name"));
                CreateSavedFileList(index);
                }
            }
        }

/**
 * Used to update the WebResourceBundle by removing any entries for this window.
 *
 * @see girard.sc.web.WebResourceBundle
 */
    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/ssaw.txt");
        }

/**
 * Pops up a window to make sure the user wants to overwrite a SimActor file.
 */
    private void CheckForFileOverwrite()
        {
        m_CFFOWFrame = new Frame("File Already Exists");
        m_CFFOWFrame.setLayout(new GridLayout(1,1));
        m_CFFOWFrame.setBackground(m_EOApp.getWinBkgColor());
        m_CFFOWFrame.setFont(m_EOApp.getMedWinFont());

        m_CFFOWPanel = new GridBagPanel();

        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ssaw_fae")),1,1,4,1);
        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ssaw_dywtoi")),1,2,4,1);

        m_OverwriteYes = new Button(m_EOApp.getLabels().getObjectLabel("ssaw_yes"));
        m_OverwriteYes.addActionListener(this);

        m_OverwriteNo = new Button(m_EOApp.getLabels().getObjectLabel("ssaw_no"));
        m_OverwriteNo.addActionListener(this);

        m_CFFOWPanel.constrain(m_OverwriteYes,1,3,2,1);
        m_CFFOWPanel.constrain(m_OverwriteNo,3,3,2,1);

        m_SaveButton.removeActionListener(this);
        m_CancelButton.removeActionListener(this);
 
        m_CFFOWFrame.add(m_CFFOWPanel);
        m_CFFOWFrame.pack();
        m_CFFOWFrame.show();
        }
    }