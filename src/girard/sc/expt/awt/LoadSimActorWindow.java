package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.obj.SimActor;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
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

/** Allows user to load SimActor files of a specific SimActor type from the
 * Exnet 3.0 database.
 * <p>
 * <br> Started: 2-27-2001
 * <br> Modified: 11-4-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class LoadSimActorWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
/**
 * The FormatSimActorWindow that is used to format SimActors of the m_activeActor type.
 */
    FormatSimActorWindow m_FSAWApp;
/**
 * The SimActor type that we want to load a file of.
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
 * Used to display the name of the selected SimActor file.
 */
    TextField m_SimActorNameField;
/**
 * Use to display a list of already saved SimActors of the same type.
 */
    SortedFixedList m_SimActorFileList;
/**
 * A list of all accessible SimActors of this type for the user.
 */
    Vector m_allActors = new Vector();
/**
 * A list of the SimActors displayed in m_SimActorFileList of this type for the user.
 */
    Vector m_listedActors = new Vector();
/**
 * Shows the description of the selected SimActor in m_SimActorFileList.
 */
    TextArea m_actorDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);

/**
 * Where the access group name for the SimActors being listed is displayed.
 */
    TextField m_AccessGroupField;
/**
 * Show the list of possible access groups that the SimActor file may be attached to.
 */
    SortedFixedList m_AccessGroupList;
/**
 * The list of access groups available to the user.
 */
    Vector m_accessGroups = new Vector();

/**
 * Buttons used to ok the loading of the selected SimActor or cancel out without loading
 * any new SimActor.
 */
    Button m_OKButton, m_CancelButton;

/**
 * The constructor.
 *
 * @param app1 The ExptOverlord to set m_EOApp to.
 * @param app2 The FormatSimActorWindow to set m_FSAWApp to.
 * @param app3 The SimActor to set m_activeActor to.
 */
    public LoadSimActorWindow(ExptOverlord app1, FormatSimActorWindow app2, SimActor app3)
        {
        super ();

        m_EOApp = app1;
        m_FSAWApp = app2;
        m_activeActor = app3;

        initializeLabels();

        setTitle(m_EOApp.getLabels().getObjectLabel("lsaw_title"));
        setBackground(m_EOApp.getWinBkgColor());
        setFont(m_EOApp.getMedWinFont());
        setLayout(new BorderLayout());

  // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("lsaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("lsaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

  //  Setup West Panel
        GridBagPanel westPanel = new GridBagPanel();

        m_SimActorFileList = new SortedFixedList(8,false,1,25);
        m_SimActorFileList.addItemListener(this);

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lsaw_saf")),1,1,4,1);
        westPanel.constrain(m_SimActorFileList,1,3,4,8);
       
  // End West Panel Setup.

  // Start Setup of East Panel
        GridBagPanel eastPanel = new GridBagPanel();

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

        eastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lsaw_ag")),1,1,4,1,GridBagConstraints.CENTER);
        eastPanel.constrain(m_AccessGroupList,1,2,4,8,GridBagConstraints.CENTER);

        m_allActors = m_EOApp.loadSimActorFileList(m_activeActor,m_accessGroups);

        CreateSavedFileList(0);      
   // End Setup of East Panel.

   // Start Setup of South Panel
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lsaw_simulant")),1,1,2,1);
        m_SimActorNameField = new TextField("None",20);
        m_SimActorNameField.setEditable(false);
        southPanel.constrain(m_SimActorNameField,3,1,6,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lsaw_ga")),1,2,2,1);

        m_AccessGroupField = new TextField(20);
        m_AccessGroupField.setEditable(false);
        southPanel.constrain(m_AccessGroupField,3,2,6,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lsaw_description")),1,3,4,1,GridBagConstraints.WEST);
        m_actorDesc.setEditable(false);
        southPanel.constrain(m_actorDesc,1,4,4,4,GridBagConstraints.WEST);

        m_OKButton = new Button(m_EOApp.getLabels().getObjectLabel("lsaw_ok"));
        m_OKButton.addActionListener(this);
        southPanel.constrain(m_OKButton,1,8,4,1,GridBagConstraints.CENTER);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("lsaw_cancel"));
        m_CancelButton.addActionListener(this);
        southPanel.constrain(m_CancelButton,5,8,4,1,GridBagConstraints.CENTER);
   // End Setup of South Panel.

        add("West",westPanel);
        add("East",eastPanel);
        add("South",southPanel);
        pack();
        show();
        }

/**
 * Processes any ActionEvents.  If the loading of a SimActor is successful or the cancel
 * button is pressed the the m_EditMode of m_FSAWApp is set to false.
 *
 * @param e The ActionEvent that trigger the function call.
 */
    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
      
            if ((theSource == m_OKButton) && (m_SimActorFileList.getSelectedIndex() > -1))
                {
                int index = m_AccessGroupList.getSelectedIndex();
                Hashtable h = (Hashtable)m_accessGroups.elementAt(index);

                if (LoadSimActor(m_SimActorNameField.getText(),h))
                    {
                    m_FSAWApp.setEditMode(false);
                    removeLabels();
                    dispose();
                    return;
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("lsaw_ftpltf"));
                    }
                }
            if (theSource == m_CancelButton)
                {
                m_FSAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("lsaw_help")))
                {
                m_EOApp.helpWindow("ehlp_lsaw");
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
        m_SimActorFileList.removeAll();

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
                m_SimActorFileList.addItem(str);

                m_listedActors.insertElementAt(h2,m_SimActorFileList.last);
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
        m_EOApp.initializeLabels("girard/sc/expt/awt/lsaw.txt");
        }  

/**
 * Used to process any ItemEvents.
 *
 * @param ie The ItemEvent that triggered this function.
 */
    public void itemStateChanged(ItemEvent ie)
        {
        if (ie.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)ie.getSource();

            if ((theSource == m_SimActorFileList) && (m_SimActorFileList.getSelectedIndex() >= 0))
                {
                int index = m_SimActorFileList.getSelectedIndex();
                Hashtable h = (Hashtable)m_listedActors.elementAt(index);
                m_SimActorNameField.setText((String)h.get("Sim Name"));
                m_actorDesc.setText((String)h.get("Sim Desc"));
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
        m_EOApp.removeLabels("girard/sc/expt/awt/lsaw.txt");
        }

/**
 * Loads the choosen filename by using the loadSimActor function in the ExptOverlord
 * class.
 *
 * @param fileName The name of the SimActor file to load.
 * @param ag The Hashtable containing information on the access group related to the
 * requested SimActor file.
 * @see girard.sc.expt.web.ExptOverlord
 */
    public boolean LoadSimActor(String fileName, Hashtable ag)
        {
        SimActor sa = m_EOApp.loadSimActor(fileName, m_activeActor, ag);

        if (sa != null)
            {
            m_FSAWApp.setActiveActor(sa);
            return true;
            }

        return false;
        }
    }