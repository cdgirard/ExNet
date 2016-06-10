package girard.sc.expt.web;

/* JoinExperimentPage: Is the page that allows subjects and observers to
   connect to any experiments that might be running on the ExSoc server.

   Author: Dudley Girard
   Started: 2000
   Modified: 5-24-2001
*/

import girard.sc.awt.ColorTextField;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.awt.ClientStartWindow;
import girard.sc.expt.awt.ObserverStartWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.DisconnectReqMsg;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetRegExptListReqMsg;
import girard.sc.expt.io.msg.JoinExptReqMsg;
import girard.sc.expt.io.msg.ServerDownMsg;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.web.WebPanel;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class JoinExperimentPage extends WebPanel implements ActionListener, ItemListener
    {
    ExptOverlord m_EOApp;
 
    GridBagPanel m_spacePanel;
    GridBagPanel m_MainPanel;
    
    ExptMessageListener m_SML;
    Hashtable m_RegisteredExperiments = new Hashtable();
    Vector m_exptVector = new Vector();
    
 //  Expt List Panel Stuff
    FixedList m_ExptList;
    ClientExptInfo m_exptIndex = null;

 //  User List Panel
    FixedList m_UserList;
    int m_UserListIndex = -1;

 // User and Password field Panel Stuff
    FixedLabel m_userLabel;
    ColorTextField m_PasswordField;
     
    GraphicButton m_JoinButton, m_BackButton;
    int m_buttonWidth = 150;
    int m_buttonHeight = 40;

    int m_regListenIndex = -1; 

    boolean m_registering = false;
    
    public JoinExperimentPage(ExptOverlord app)
        {
        m_EOApp = app;
         
        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("bejep_title"));
        setBackground(m_EOApp.getDispBkgColor());
        setFont(m_EOApp.getSmLabelFont());

    // Setup Message Listener
        m_SML = m_EOApp.createExptML();
        m_SML.addActionListener(this);
        m_SML.start();

    // Build Expt List Panel

        GridBagPanel ExptListPanel = new GridBagPanel();

        int[] exptSpacing = { 20, 20 };
        m_ExptList = new FixedList(8,false,2,exptSpacing,FixedList.CENTER);
        m_ExptList.setFont(m_EOApp.getSmLabelFont());

        Enumeration enm = m_RegisteredExperiments.elements();
        while (enm.hasMoreElements())
            {
            ClientExptInfo exp = (ClientExptInfo)enm.nextElement();
            m_ExptList.addItem(BuildExptListEntry(exp));
            }
        m_ExptList.addItemListener(this);

        ExptListPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bejep_ens")),1,1,4,1);
        ExptListPanel.constrain(m_ExptList,1,2,4,8,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

   // Build Human User List Panel
        GridBagPanel UserListPanel = new GridBagPanel();

        int[] userSpacing = { 10, 8 }; 
        m_UserList = new FixedList(8,false,2,userSpacing,FixedList.CENTER);
        m_UserList.setFont(m_EOApp.getSmLabelFont());
        m_UserList.addItemListener(this);
        
        UserListPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bejep_ur")),1,1,4,1);
        UserListPanel.constrain(m_UserList,1,2,4,8,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL);

   // User-Password Panel
        GridBagPanel UserLabelPanel = new GridBagPanel();
        
        UserLabelPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bejep_user")),1,1,4,1);

        m_userLabel = new FixedLabel(8,"NONE");
        UserLabelPanel.constrain(m_userLabel,5,1,4,1); 
       
        UserLabelPanel.constrain(new Label("     "),1,2,8,1);

        UserLabelPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bejep_password")),1,3,4,1);
        m_PasswordField = new ColorTextField(10);
        m_PasswordField.setBackground(m_EOApp.getObjectBkgColor());
        UserLabelPanel.constrain(m_PasswordField,5,3,4,1);
        UserLabelPanel.constrain(new Label(""),1,4,8,1);

   // Build the OK and Cancel buttons

        m_JoinButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_JoinButton.addActionListener(this);
       
        m_BackButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_BackButton.addActionListener(this); 

        

        m_MainPanel = new GridBagPanel();
        m_spacePanel = new GridBagPanel();

        m_MainPanel.constrain(ExptListPanel,1,1,2,2);
        m_MainPanel.constrain(UserListPanel,1,3,2,2);
        m_MainPanel.constrain(UserLabelPanel,3,3,2,2);

        m_MainPanel.constrain(m_JoinButton,1,5,1,1,GridBagConstraints.CENTER);
        m_MainPanel.constrain(m_BackButton,2,5,1,1,GridBagConstraints.CENTER);

        loadImages();

        m_spacePanel.constrain(m_MainPanel,1,1,60,40,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);

        if (m_EOApp.getWidth() > 640) 
            {
            m_spacePanel.constrain(new Panel(new GridLayout(1,1)),61,1,m_EOApp.getWidth()/10,40,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);
            }
        if (m_EOApp.getHeight() > 480)
            {
            m_spacePanel.constrain(new Panel(new GridLayout(1,1)),1,41,m_EOApp.getWidth()/10,m_EOApp.getHeight()/10,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);
            }

        add(m_spacePanel);

        this.validate();

        GetRegExptListReqMsg tmpMsg = new GetRegExptListReqMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000); 
        }

    public void actionPerformed (ActionEvent e)
        {
        if (m_EOApp.getEditMode())
            return;

        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            if (m_registering)
                return;

            if (theSource == m_JoinButton)
                {
                if (m_UserListIndex > -1)
                    {
                    if (m_UserListIndex == m_exptIndex.getNumUsers())  // Connect as an observer
                        {
                        JoinExperiment(m_exptIndex,ClientExptInfo.OBSERVER,m_PasswordField.getText());
                        }
                    else   // Connect as a user
                        {
                        JoinExperiment(m_exptIndex,m_UserListIndex,m_PasswordField.getText());
                        }
                    }
                }
            if (theSource == m_BackButton)
                {
                // Handle Cancel
                try
                    {
                    m_SML.removeActionListener(this);
                    m_SML.removeListenRequest(m_regListenIndex);
                    DisconnectReqMsg tmp = new DisconnectReqMsg(null);
                    m_SML.sendMessage(tmp);
                    m_SML.finalize(0);
                    }
                catch (NullPointerException npe) { ; }
                m_EOApp.setUserID(-1);
	          m_EOApp.removeThenAddPanel(this, new SubjectLoginPage(m_EOApp));
                }
            }

        if (e.getSource() instanceof ExptMessage)
            {
            ExptMessage em = (ExptMessage)e.getSource();

            if (em instanceof GetRegExptListReqMsg)
                {
                Object[] in_args = em.getArgs();
// System.err.println("MSG: "+em);
                m_RegisteredExperiments.clear();
                m_RegisteredExperiments = (Hashtable)in_args[0];

                updateLists();
                }
            if (em instanceof JoinExptReqMsg)
                {
                if (em.getArgs()[0] instanceof ClientExptInfo)
                    {
                    ClientExptInfo exp = (ClientExptInfo)em.getArgs()[0];

                    if (m_RegisteredExperiments.containsKey(exp.getExptUID()))
                        {
                        m_SML.removeActionListener(this);
                        m_EOApp.setEditMode(true);
                        new ClientStartWindow(m_EOApp,exp,m_SML);
                        }
                    else
                        {
                        new ErrorDialog(m_EOApp.getLabels().getObjectLabel("bejep_jawu"));
                        m_registering = false;
                        return;
                        }
                    }
                if (em.getArgs()[0] instanceof ObserverExptInfo)
                    {
                    ObserverExptInfo exp = (ObserverExptInfo)em.getArgs()[0];
                    if (m_RegisteredExperiments.containsKey(exp.getExptUID()))
                        {
                        m_SML.removeActionListener(this);
                        m_EOApp.setEditMode(true);
                        new ObserverStartWindow(m_EOApp,exp,m_SML);
                        }
                    else
                        {
                        new ErrorDialog(m_EOApp.getLabels().getObjectLabel("bejep_jawu"));
                        m_registering = false;
                        return;
                        }
                    }
                }
            if (em instanceof ExptErrorMsg)
                {
                String str = (String)em.getArgs()[1];
                if (str.equals("JoinExptReqMsg"))
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("bejep_jawu"));
                    m_registering = false;
                    return;
                    }
                }
            if (em instanceof ServerDownMsg)
                {
                m_EOApp.setUserID(-1);
	          m_EOApp.removeThenAddPanel(this, new SubjectLoginPage(m_EOApp));
                }
            }
        }

    public String[] BuildExptListEntry(ClientExptInfo exp)
        {
        String[] str = new String[2];

        str[0] = exp.getExptName();
        str[1] = new String(""+exp.getSupervisor());

        return str;
        }

    public String[] BuildUserListEntry(ClientExptInfo exp, int i)
        {
        String[] str = new String[2];
 
        str[0] = new String("User"+i);
        if (exp.getRegistered(i))
            {
            str[1] = new String(m_EOApp.getLabels().getObjectLabel("bejep_true")); // was true
            }
        else
            {
            str[1] = new String(m_EOApp.getLabels().getObjectLabel("bejep_false")); // was false
            }

        return str;
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_ExptList)
                {
                if (m_ExptList.getSelectedIndex() > -1)
                    {
                    m_exptIndex = (ClientExptInfo)m_exptVector.elementAt(m_ExptList.getSelectedIndex());
                    UpdateDisplayArea();
                    }
                }

            if (theSource == m_UserList)
                {
                m_UserListIndex = m_UserList.getSelectedIndex();
                if (m_UserListIndex == -1)
                    return;
                m_userLabel.setText(m_UserList.getSelectedSubItem(0));
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/web/jep.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/web/jep.txt");
        }

    public void restartPage()
        {
        m_registering = false;

        m_SML = m_EOApp.createExptML();
        m_SML.addActionListener(this);
        m_SML.start();

        GetRegExptListReqMsg tmpMsg = new GetRegExptListReqMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000);
        }

    public void UpdateDisplayArea()
        {
        m_UserList.removeAll();

        for (int i=0;i<m_exptIndex.getNumUsers();i++)
            {
            m_UserList.addItem(BuildUserListEntry(m_exptIndex,i));
            }
        if (m_exptIndex.getAllowObservers())
            {
            String[] str = new String[2];
            str[0] = new String(m_EOApp.getLabels().getObjectLabel("bejep_observer"));
            str[1] = "-";
            m_UserList.addItem(str);
            }
        m_UserListIndex = -1;

        m_userLabel.setText("NONE");
        m_PasswordField.setText(""); 
        }

    private void JoinExperiment(ClientExptInfo exp,int user, String pass)
        {    
        int i;
 System.err.println("Trying to join: "+exp.getExptUID()+" Pass: "+pass);
        Object[] out_args = new Object[3]; 
        out_args[0] = exp.getExptUID();
        out_args[1] = new Integer(user);
        out_args[2] = pass;

        JoinExptReqMsg tmp = new JoinExptReqMsg(out_args);

        m_registering = true;

        m_SML.sendMessage(tmp);
        }

    private void loadImages()
        {
        int x, y;
        Graphics g;
        Image tmp, tmp2;

        // Initialize Button Image
        tmp = m_EOApp.getButtonImage();


    // Create Join Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("bejep_join").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("bejep_join"),x,y);
     
        m_JoinButton.setImage(tmp2);

    // Create Back Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("bejep_back").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("bejep_back"),x,y);
     
        m_BackButton.setImage(tmp2);
        }

    private void updateLists()
        {
        m_exptVector.removeAllElements();

        Enumeration enm = m_RegisteredExperiments.elements();
        while (enm.hasMoreElements())
            {
            m_exptVector.addElement(enm.nextElement());
            }

        m_ExptList.removeAll();
 
        int eIndex = -1;

        enm = m_RegisteredExperiments.elements();
        while (enm.hasMoreElements())
            {
            ClientExptInfo exp = (ClientExptInfo)enm.nextElement();

            m_ExptList.addItem(BuildExptListEntry(exp));
            if (m_exptIndex != null)
                {
                if (m_exptIndex.getExptUID().longValue() == exp.getExptUID().longValue())
                    {
                    m_exptIndex = exp;
                    eIndex = m_ExptList.getItemCount() - 1;
                    }
                }
            }
        if (eIndex > -1)
            m_ExptList.select(eIndex);

        if (m_exptIndex != null)
            {
            if (m_RegisteredExperiments.containsKey(m_exptIndex.getExptUID()))
                {
                m_UserList.removeAll();

                for (int i=0;i<m_exptIndex.getNumUsers();i++)
                    {
                    m_UserList.addItem(BuildUserListEntry(m_exptIndex,i));
                    }
                if (m_exptIndex.getAllowObservers())
                    {
                    String[] str = new String[2];
                    str[0] = new String(m_EOApp.getLabels().getObjectLabel("bejep_observer"));
                    str[1] = "-";
                    m_UserList.addItem(str);
                    }
                if (m_UserListIndex > -1)
                    {
                    m_UserList.select(m_UserListIndex);
                    }
                else
                    {
                    m_userLabel.setText("NONE");
                    m_PasswordField.setText("");
                    }
                }
            else
                {
                m_exptIndex = null;

                m_UserList.removeAll();

                m_UserListIndex = -1;

                m_userLabel.setText("NONE");
                m_PasswordField.setText("");
                }
            }
        this.validate();
        }
    }
