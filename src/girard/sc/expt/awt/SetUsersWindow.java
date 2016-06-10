package girard.sc.expt.awt;

import girard.sc.awt.ColorTextField;
import girard.sc.awt.DescriptionDialog;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.LoadExptReqMsg;
import girard.sc.expt.io.msg.RegisterExptReqMsg;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.obj.SimActor;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.expt.web.OptionsPage;
import girard.sc.expt.web.RegisterExperimentPage;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/** 
 * Used to set whether a user is simulant or human.  If it is a simulant
 * actor then a specific simulant must be assigned.
 *
 * Exnet III file system.
 * <p>
 * <br> Started: 8-23-2000
 * <br> Modified: 11-4-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class SetUsersWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
    RegisterExperimentPage m_BEApp;
/**
 * The Experiment that the user is preparing to run.
 */
    Experiment m_ExpApp;
    ExptMessageListener m_SML;

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    

    FixedLabel m_UserLabel;
/**
 * Lists all the user poisitions available in this Experiment.
 */
    FixedList m_UserList;
    CheckboxGroup m_userType = new CheckboxGroup();
    Checkbox m_humanBox, m_computerBox;
    ColorTextField m_PasswordField;
/**
 * Updates the selected user's password to the one in the m_PasswordField.
 */
    Button m_updatePasswordButton;

/**
 * List of Simulant Actors that can be used.
 */
    SortedFixedList m_SimulantsList; 
/**
 * A Hashtable of Vectors, with each Vector being a complete list of all the
 * SimActors of a specific type a user has access to.
 */
    Hashtable m_allActors = new Hashtable();
    Vector m_listedActors = new Vector();
    Vector m_simTypes = new Vector();
/**
 * Gets the info on the Simulated Actor and displays it.
 */
    Button m_SimInfoButton;
    Button m_ChangeGroupButton;
    
/** 
 * Lists the actions and the simulant actor for that action for the presently 
 * selected user.
 */
    FixedList m_SimAssignList;  
    
    Button m_StartButton, m_CancelButton;
    
    int m_activeUserIndex = -1;
    int m_SimAssignIndex = -1;
    

    Frame m_AGFrame;
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
    int m_accessGroupIndex = -1;

    boolean m_EditMode = false;

    public SetUsersWindow(ExptOverlord app1, RegisterExperimentPage app2, Experiment app3)
        {
        super ();

        m_EOApp = app1;
        m_BEApp = app2;
        m_ExpApp = app3;

        initializeLabels();

        LoadExperiment();

  //  Setup Button and Label Fields
        setTitle(m_EOApp.getLabels().getObjectLabel("suw_title"));
        setBackground(m_EOApp.getWinBkgColor());
        setFont(m_EOApp.getMedWinFont());
        setLayout(new BorderLayout());

  // Start Setup for the Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

     // File Menu

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("suw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("suw_start"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("suw_cancel"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

     // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("suw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("suw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for the Menubar.


  // Start setup for North Panel
        Panel northPanel = new Panel(new GridLayout(1,1));

        northPanel.add(new Label(m_EOApp.getLabels().getObjectLabel("suw_experiment")+m_ExpApp.getExptName()));
  // End Setup for the North Panel

  // Start setup Center Panel
        GridBagPanel centerPanel = new GridBagPanel();    

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("suw_utp")),1,1,6,1);

      // User, Type, Password
        int[] p = {7, 10, 15};

        m_UserList = new FixedList(8,false,3,p);
        m_UserList.addItemListener(this);

        FillUserList();
   
        centerPanel.constrain(m_UserList,1,2,6,6,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);

      // Start setup for user and password
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("suw_user")),7,1,2,1);
        m_UserLabel = new FixedLabel(6,"");
        centerPanel.constrain(m_UserLabel,9,1,2,1);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("suw_password")),7,2,2,1);
        m_PasswordField = new ColorTextField(10);
        centerPanel.constrain(m_PasswordField,9,2,2,1);

      // End setup for user and password

        m_updatePasswordButton = new Button(m_EOApp.getLabels().getObjectLabel("suw_uppass"));
        m_updatePasswordButton.addActionListener(this);
        centerPanel.constrain(m_updatePasswordButton,7,3,4,1,GridBagConstraints.CENTER);

      // Start setup for type
        GridBagPanel tmpGBPanel = new GridBagPanel();

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("suw_type")),1,1,2,2);
        m_humanBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("suw_human"),m_userType,true);
        m_humanBox.addItemListener(this);
        tmpGBPanel.constrain(m_humanBox,3,1,2,1);
        m_computerBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("suw_computer"),m_userType,false);
        m_computerBox.addItemListener(this);
        tmpGBPanel.constrain(m_computerBox,3,2,2,1);

        centerPanel.constrain(tmpGBPanel,7,4,4,2);
      // End setup for type

        
   // End setup for Center Panel

   // Start Setup for South Panel
        GridBagPanel southPanel = new GridBagPanel();

        CreateAccessGroupWindow();

        m_simTypes = m_EOApp.loadSimActorTypeList();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("suw_avSims")),1,1,4,1,GridBagConstraints.CENTER);

        m_SimulantsList = new SortedFixedList(6,false,1,20,FixedList.CENTER);
        m_SimulantsList.addItemListener(this);

        southPanel.constrain(m_SimulantsList,1,2,4,6,GridBagConstraints.CENTER);

        m_SimInfoButton = new Button(m_EOApp.getLabels().getObjectLabel("suw_info"));
        m_SimInfoButton.addActionListener(this);
        southPanel.constrain(m_SimInfoButton,1,8,1,1);

        m_ChangeGroupButton = new Button(m_EOApp.getLabels().getObjectLabel("suw_cg"));
        m_ChangeGroupButton.addActionListener(this);
        southPanel.constrain(m_ChangeGroupButton,2,8,3,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("suw_ag:")),1,9,4,1,GridBagConstraints.EAST);
        m_AccessGroupField = new TextField(20);
        m_AccessGroupField.setEditable(false);
        southPanel.constrain(m_AccessGroupField,5,9,4,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("suw_acSim")),5,1,6,1);

        m_SimAssignList = new FixedList(6,false,2,20,FixedList.CENTER);
        m_SimAssignList.addItemListener(this);

        FillSimAssignList();

        southPanel.constrain(m_SimAssignList,5,2,6,6,GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL);
    // End Setup for South Panel.

        add("North",northPanel);
        add("Center",centerPanel);
        add("South",southPanel);
        pack();
        
        m_UserList.setSize(m_UserList.getPreferredSize());
        m_SimAssignList.setSize(m_SimAssignList.getPreferredSize());
        setSize(getPreferredSize());

        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof ExptMessage)
            {
            ExptMessage em = (ExptMessage)e.getSource();

            if (em instanceof RegisterExptReqMsg)
                {
                Experiment exp = (Experiment)em.getArgs()[0];
                Long exptUID = (Long)em.getArgs()[1];

                m_AGFrame.dispose();

                m_SML.removeActionListener(this);
                m_EOApp.setEditMode(true);
                m_EOApp.removeThenAddPanel(m_BEApp, new OptionsPage(m_EOApp));
                exp.setActionIndex(0);
                exp.initializeSimSMLs(m_EOApp,exptUID);
                exp.initializeSimActors(m_EOApp);
                new ExperimenterStartWindow(m_EOApp,exp,m_SML);
                this.dispose();
                }
            else
                {
                new ErrorDialog((String)em.getArgs()[0]);
                m_SML.removeActionListener(this);
                m_SML.setFlag(false);
                m_EditMode = false;
                }
            }

        if (m_EditMode)
            return;

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_ChangeGroupButton)
                {
                m_AGFrame.show();
                }

            if ((theSource == m_SimInfoButton) && (m_SimulantsList.getSelectedIndex() != -1))
                {
                Hashtable h = (Hashtable)m_listedActors.elementAt(m_SimulantsList.getSelectedIndex());
                new DescriptionDialog((String)h.get("Sim Desc"));
                }

            if (theSource == m_updatePasswordButton)
                {
                if (m_activeUserIndex > -1)
                    {
                    m_ExpApp.setPassword(m_PasswordField.getText(),m_activeUserIndex);
                    m_UserList.replaceItem(BuildUserListEntry(m_activeUserIndex),m_activeUserIndex);
                    m_UserList.select(m_activeUserIndex);
                    }
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("suw_start")))
                {
                if (validUsers())
                    {
                    RegisterExperiment(m_ExpApp);
                    }
                else
                    {
                    String[] errStr = new String[2];
                    errStr[0] = m_EOApp.getLabels().getObjectLabel("suw_nacsh");
                    errStr[1] = m_EOApp.getLabels().getObjectLabel("suw_aafeea");
                    new ErrorDialog(errStr);
                    }
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("suw_cancel")))
                {
                m_AGFrame.dispose();
                removeLabels();
                m_EOApp.setEditMode(false);
                this.dispose();
                return;
                }
         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("suw_help")))
                {
                m_EOApp.helpWindow("ehlp_suw");
                }
            }

        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if (theSource == m_AccessGroupList)
                {
                m_accessGroupIndex = m_AccessGroupList.getSelectedIndex();
                Hashtable ag = (Hashtable)m_accessGroups.elementAt(m_accessGroupIndex);
                m_AccessGroupField.setText((String)ag.get("App Name"));
                if (m_SimAssignIndex > -1)
                    FillSimulantsList(m_SimAssignIndex);
                m_AGFrame.hide();
                }
            }
        }

/**
 *
 */
    public void AddToSimulantsList(Vector actors) 
        {
        Hashtable h = (Hashtable)m_accessGroups.elementAt(m_accessGroupIndex);
        String uid = new String("-");
        if (h.containsKey("App ID"))
            uid = (String)h.get("App ID");

        Enumeration enm = actors.elements();
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
                m_SimulantsList.addItem(str);

                m_listedActors.insertElementAt(h2,m_SimulantsList.last);
                }
            }
        }


    public String[] BuildSimAssignListEntry(int index)
        {
        ExperimentAction ea = m_ExpApp.getAction(index);
        String[] str = new String[2];
        SimActor sa = null;

        str[0] = new String(ea.getName());
        if (m_activeUserIndex > -1)
            sa = ea.getActor(m_activeUserIndex);

        if (sa != null)
            str[1] = sa.getActorName();
        else
            str[1] = new String("-");
        return str; 
        }
    public String[] BuildUserListEntry(int index)
        {
        String[] str = new String[3];

        str[0] = new String("User"+index);
        if (m_ExpApp.getHumanUser(index))
            str[1] = new String(m_EOApp.getLabels().getObjectLabel("suw_human"));
        else
            str[1] = new String(m_EOApp.getLabels().getObjectLabel("suw_computer"));
        str[2] = m_ExpApp.getPassword(index);

        return str; 
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
 *
 */
    public void CreateAccessGroupWindow()
        {
        m_AGFrame = new Frame("Access Groups");
        m_AGFrame.setLayout(new GridLayout(1,1));
        m_AGFrame.setBackground(m_EOApp.getWinBkgColor());
        m_AGFrame.setFont(m_EOApp.getMedWinFont());

        GridBagPanel tmpPanel = new GridBagPanel();

        m_AccessGroupList = new SortedFixedList(8,false,1,25);
        m_AccessGroupList.addItemListener(this);
        m_AccessGroupList.addActionListener(this);

        String[] str = new String[1];
        str[0] = "<NONE>";
        m_AccessGroupList.addItem(str);
        Hashtable h = new Hashtable();
        h.put("Name","<NONE>");
        h.put("Desc","Only this user may access this file.");
        m_accessGroups.addElement(h);

        CreateAccessGroupList();

        m_AccessGroupList.select(0);
        m_accessGroupIndex = 0;

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("suw_ag")),1,1,4,1,GridBagConstraints.CENTER);
        tmpPanel.constrain(m_AccessGroupList,1,2,4,8,GridBagConstraints.CENTER);

        m_AGFrame.add(tmpPanel);

        m_AGFrame.pack();
        }

    public void FillSimAssignList() 
        {
        for (int i=0;i<m_ExpApp.getNumActions();i++)
            {
            m_SimAssignList.addItem(BuildSimAssignListEntry(i));
            }  
        }
    public void FillSimulantsList(int index) 
        {
        m_listedActors.removeAllElements();
        m_SimulantsList.removeAll();

        ExperimentAction ea = m_ExpApp.getAction(index);
        for (int i=0;i<m_simTypes.size();i++)
            {
            SimActor sa = (SimActor)m_simTypes.elementAt(i);
            if (sa.getActionType().equals(ea.getName()))
                {
                if (!m_allActors.containsKey(sa.getName()))
                    {
                    Vector actors = m_EOApp.loadSimActorFileList(sa,m_accessGroups);
                    Enumeration enm = actors.elements();
                    while (enm.hasMoreElements())
                        {
                        Hashtable h = (Hashtable)enm.nextElement();
                        h.put("Sim Index",new Integer(i));
                        }
                    m_allActors.put(sa.getName(),actors);
                    }
                AddToSimulantsList((Vector)m_allActors.get(sa.getName()));
                }
            }
        }
    public void FillUserList() 
        {
        for (int i=0;i<m_ExpApp.getNumUsers();i++)
            {
            m_UserList.addItem(BuildUserListEntry(i));
            }  
        }    

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/suw.txt");
        }  

    public void itemStateChanged(ItemEvent ie)
        {
        if (m_EditMode)
            return;

        if (ie.getSource() instanceof Checkbox)
            {
            Checkbox theSource = (Checkbox)ie.getSource();

            if (m_activeUserIndex > -1)
                {
                if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("suw_human")))
                    {
                    if (!m_ExpApp.getHumanUser(m_activeUserIndex))
                        {
                        m_ExpApp.setHumanUser(true,m_activeUserIndex);
                        m_UserList.replaceItem(BuildUserListEntry(m_activeUserIndex),m_activeUserIndex);
                        UpdateDisplayArea();
                        }
                    }
                if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("suw_computer")))
                    {
                    if (m_ExpApp.getHumanUser(m_activeUserIndex))
                        {
                        m_ExpApp.setHumanUser(false,m_activeUserIndex);
                        m_UserList.replaceItem(BuildUserListEntry(m_activeUserIndex),m_activeUserIndex);
                        m_UserList.select(m_activeUserIndex);
                        }
                    }
                }
            }
        if (ie.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)ie.getSource();

            if ((theSource == m_UserList) && (m_UserList.getItemCount() > 0))
                {
                if (ie.getStateChange() == ItemEvent.SELECTED)
                    {
                    m_activeUserIndex = theSource.getSelectedIndex();
                    UpdateDisplayArea();
                    }
                }
            if ((theSource == m_SimAssignList) && (m_activeUserIndex > -1))
                {
                if (m_SimAssignList.getSelectedIndex() > -1)
                    {
                    m_SimAssignIndex = theSource.getSelectedIndex();
                    m_SimulantsList.removeAll();
                    FillSimulantsList(m_SimAssignIndex);
                    }
                }
            }
        if (ie.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)ie.getSource();

            if ((theSource == m_SimulantsList) && (m_SimAssignIndex > -1) && (!m_ExpApp.getHumanUser(m_activeUserIndex)))
                {
                if (m_SimulantsList.getSelectedIndex() > -1)
                    {
                    int index = m_SimulantsList.getSelectedIndex();
                    ExperimentAction ea = m_ExpApp.getAction(m_SimAssignIndex);
                    Hashtable h = (Hashtable)m_listedActors.elementAt(index);
                    int simIndex = ((Integer)h.get("Sim Index")).intValue();

                    SimActor sa = (SimActor)((SimActor)m_simTypes.elementAt(simIndex)).clone();
                    Hashtable ag = (Hashtable)m_accessGroups.elementAt(m_accessGroupIndex);
                    String fileName = (String)h.get("Sim Name");
                    SimActor sa2 = m_EOApp.loadSimActor(fileName,sa,ag);

                    sa2.setUser(m_activeUserIndex);
                    ea.setActor(m_activeUserIndex,sa2);

                    m_SimAssignList.replaceItem(BuildSimAssignListEntry(m_SimAssignIndex),m_SimAssignIndex);
                    }
                }

            if ((theSource == m_AccessGroupList) && (m_SimAssignIndex > -1) && (m_AccessGroupList.getSelectedIndex() > -1))
                {
                m_accessGroupIndex = m_AccessGroupList.getSelectedIndex();
                FillSimulantsList(m_SimAssignIndex);
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/suw.txt");
        }

    public boolean validUsers()
        {
        for (int j=0;j<m_ExpApp.getNumUsers();j++)
            {
            if (!m_ExpApp.getHumanUser(j))
                {
                for (int i=0;i<m_ExpApp.getNumActions();i++)
                    {
                    ExperimentAction ea = m_ExpApp.getAction(i);
                    SimActor sa = ea.getActor(j);
                    if (sa == null)
                        return false;
                    }
                }
            }
        return true;
        }

    private void LoadExperiment()
        {
        Object[] out_args = new Object[1];
        out_args[0] = m_ExpApp;
        LoadExptReqMsg tmp = new LoadExptReqMsg(out_args);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof LoadExptReqMsg)
            {
            m_ExpApp = (Experiment)em.getArgs()[0];
            }
        }

    private void RegisterExperiment(Experiment exp)
        {    
        int i;
  
        m_SML = m_EOApp.createExptML();
        m_SML.addActionListener(this);
        m_SML.start();
 
        Object[] out_args = new Object[1]; 
        out_args[0] = exp;

        RegisterExptReqMsg tmp = new RegisterExptReqMsg(out_args);

        m_SML.sendMessage(tmp);

        m_EditMode = true;
        }

    public void UpdateDisplayArea()
        { 
        m_SimAssignList.removeAll();
        m_SimAssignIndex = -1;
        m_SimulantsList.removeAll();

        FillSimAssignList();

        if ((m_ExpApp.getHumanUser(m_activeUserIndex)) && (m_userType.getSelectedCheckbox() == m_computerBox))
            {
            m_humanBox.setState(true);
            }
        else if ((!m_ExpApp.getHumanUser(m_activeUserIndex)) && (m_userType.getSelectedCheckbox() == m_humanBox))
            {
            m_computerBox.setState(true);
            }

        if (m_activeUserIndex > -1)
            {
            m_UserLabel.setText("User"+m_activeUserIndex);
            m_PasswordField.setText(m_ExpApp.getPassword(m_activeUserIndex));
            m_UserList.select(m_activeUserIndex);
            } 
        }
    }
