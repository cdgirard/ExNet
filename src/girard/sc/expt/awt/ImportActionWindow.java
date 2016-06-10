package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExperimentAction;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to import an Experiment Action from an Experiment saved to the database.
 * Lists all previously saved experiments by user and group.  Group access is
 * determined by which software applications the user is granted read and write
 * access to through the Web-Lab.
 * <p>
 * <br> Started: Jan 2002
 * <br> Modified: 12-16-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class ImportActionWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
/**
 * Allows access to add the Experiment Action loaded and so we can reset the
 * m_editMode of the ExptBuilderWindow.
 */
    ExptBuilderWindow m_EBWApp;

    Experiment m_ExpApp = new Experiment();

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_Help;

/**
 * The list of all the access groups the user has access to.
 */
    SortedFixedList m_AccessGroupList;
/**
 * Where a partial listing of experiments saved by the user is displayed.
 */
    SortedFixedList m_SavedExptList;
    int m_activeExptIndex = -1;

    FixedList m_ActionList;
    FixedLabel m_ActionNameLabel;
    FixedLabel m_FixedUNLabel;
    FixedLabel m_NumUsersLabel;

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
 * Displays the description of the presently selected access group in the 
 * m_AccessGroupList.
 */
    TextArea m_agDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);
    TextArea m_exptDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);
    TextArea m_actionDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);

    Button m_ImportButton, m_CancelButton;

    public ImportActionWindow(ExptOverlord app1, ExptBuilderWindow app2)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_EBWApp = app2; /* Need so can unset edit mode */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getWinBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("iew_title"));
        setFont(m_EOApp.getMedWinFont());
       
    // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("iew_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("iew_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

     // Start Setup the center panel.
        GridBagPanel centerPanel = new GridBagPanel();
        
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

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_ag")),1,1,4,1,GridBagConstraints.CENTER);
        centerPanel.constrain(m_AccessGroupList,1,2,4,6,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_description")),1,8,4,1,GridBagConstraints.CENTER);
        m_agDesc.setEditable(false);
        m_agDesc.setFont(m_EOApp.getSmWinFont());
        centerPanel.constrain(m_agDesc,1,9,4,4,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_experiments")),5,1,4,1,GridBagConstraints.CENTER);

        m_SavedExptList = new SortedFixedList(6,false,1,20,SortedFixedList.CENTER);
        m_SavedExptList.addItemListener(this);
        m_SavedExptList.addActionListener(this);

        m_allExperiments = m_EOApp.loadExptFileList(m_accessGroups);

        m_AccessGroupList.select(0);

        CreateExptFileList(0);

        centerPanel.constrain(m_SavedExptList,5,2,4,6,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_description")),5,8,4,1,GridBagConstraints.CENTER);
        m_exptDesc.setEditable(false);
        m_exptDesc.setFont(m_EOApp.getSmWinFont());
        centerPanel.constrain(m_exptDesc,5,9,4,4,GridBagConstraints.CENTER);
        
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_actions")),9,1,4,1,GridBagConstraints.CENTER);

        m_ActionList = new FixedList(6,false,1,30,SortedFixedList.CENTER);
        m_ActionList.addItemListener(this);

        centerPanel.constrain(m_ActionList,9,2,4,6,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_description")),9,8,4,1,GridBagConstraints.CENTER);
        m_actionDesc.setEditable(false);
        m_actionDesc.setFont(m_EOApp.getSmWinFont());
        centerPanel.constrain(m_actionDesc,9,9,4,4,GridBagConstraints.CENTER);
     // Finished setup for east Panel.

     // Start setup for south panel
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_nu")),1,1,1,1,GridBagConstraints.CENTER);

        m_NumUsersLabel = new FixedLabel(30,"");
        southPanel.constrain(m_NumUsersLabel,2,1,5,1,GridBagConstraints.WEST);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_fixed")),1,2,1,1,GridBagConstraints.CENTER);

        m_FixedUNLabel = new FixedLabel(30,"");
        southPanel.constrain(m_FixedUNLabel,2,2,5,1,GridBagConstraints.WEST);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("iew_action")),1,3,1,1,GridBagConstraints.CENTER);

        m_ActionNameLabel = new FixedLabel(30,"");
        southPanel.constrain(m_ActionNameLabel,2,3,5,1,GridBagConstraints.WEST);

        m_ImportButton = new Button(m_EOApp.getLabels().getObjectLabel("iew_import"));
        m_ImportButton.addActionListener(this);
        southPanel.constrain(m_ImportButton,1,4,3,1,GridBagConstraints.CENTER);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("iew_cancel"));
        m_CancelButton.addActionListener(this);
        southPanel.constrain(m_CancelButton,3,4,3,1,GridBagConstraints.CENTER);

        add("Center",centerPanel);
        add("South",southPanel);

        pack();

        m_AccessGroupList.setSize(m_AccessGroupList.getPreferredSize());
        m_SavedExptList.setSize(m_SavedExptList.getPreferredSize());
        m_ActionList.setSize(m_ActionList.getPreferredSize());
        setSize(getPreferredSize());

        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
       
            if (theSource == m_CancelButton)
                {
                // Handle Save
                m_EBWApp.setEditMode(false);
                dispose();
                return;
                }
            if ((theSource == m_ImportButton) && (m_ActionList.getSelectedIndex() >= 0))
                {
                ExperimentAction ea = (ExperimentAction)((ExperimentAction)m_ExpApp.getAction(m_ActionList.getSelectedIndex())).clone();
                int numUsers = (Integer.valueOf(m_NumUsersLabel.getText().trim())).intValue();

                if ((m_EBWApp.sameNumUsers(numUsers)) || (ea.allowChangeNumUsers()))
                    { 
                    m_EBWApp.addAction(ea.getName(),ea);
                    m_EBWApp.setEditMode(false);
                    removeLabels();
                    dispose();
                    return;
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("iew_utia"));
                    }
                }
            }

        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_SavedExptList) && (m_SavedExptList.getSelectedIndex() > -1))
                {
                int index = m_SavedExptList.getSelectedIndex();

                if (LoadExpt((Experiment)m_listedExpts.elementAt(m_SavedExptList.getSelectedIndex())))
                    {
                    FillActionList();
                    m_activeExptIndex = index;
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("iew_ftpltf"));
                    }
                }
            return;
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("iew_help")))
                {
                m_EOApp.helpWindow("ehlp_iew");
                }
            }
        }

    public String[] buildActionListEntry(ExperimentAction obj)
        {
        String[] s = { obj.getDetailName() };

        return s;
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

    public void FillActionList()
        {
        m_ActionList.removeAll();
        Enumeration enm = m_ExpApp.getActions().elements();
        while(enm.hasMoreElements())
            {
            ExperimentAction ea = (ExperimentAction)enm.nextElement();

            String[] str = buildActionListEntry(ea);
            m_ActionList.addItem(str);
            }
        m_ActionNameLabel.setText("");
        m_actionDesc.setText("");
        }

    
    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_AccessGroupList) && (m_AccessGroupList.getSelectedIndex() >= 0))
                {
                int index = m_AccessGroupList.getSelectedIndex();
                Hashtable h = (Hashtable)m_accessGroups.elementAt(index);
                m_agDesc.setText((String)h.get("App Desc"));
                CreateExptFileList(index);
                m_exptDesc.setText("");
                }

            if ((theSource == m_SavedExptList) && (m_SavedExptList.getSelectedIndex() >= 0))
                {
                Experiment exp = (Experiment)m_listedExpts.elementAt(m_SavedExptList.getSelectedIndex());
                m_exptDesc.setText(exp.getExptDesc());
                m_NumUsersLabel.setText(""+exp.getNumUsers());
                }
            }
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();
  
            if ((theSource == m_ActionList) && (m_ActionList.getSelectedIndex() >= 0))
                {
                ExperimentAction ea = (ExperimentAction)m_ExpApp.getAction(m_ActionList.getSelectedIndex());
                m_ActionNameLabel.setText(ea.getDetailName());
                m_actionDesc.setText(ea.getDesc());
                if (ea.allowChangeNumUsers())
                    m_FixedUNLabel.setText("Yes");
                else
                    m_FixedUNLabel.setText("No");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/iew.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/iew.txt");
        }

    public boolean LoadExpt(Experiment exp)
        {
        exp = m_EOApp.loadExpt(exp);

        if (exp != null)
            {
            m_ExpApp = exp;
            return true;
            }

        return false;
        }
    }