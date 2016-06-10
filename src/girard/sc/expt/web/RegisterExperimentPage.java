package girard.sc.expt.web;

import girard.sc.awt.ColorTextField;
import girard.sc.awt.FixedList;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.awt.SetUsersWindow;
import girard.sc.expt.obj.Experiment;
import girard.sc.web.WebPanel;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


public class RegisterExperimentPage extends WebPanel implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;

    int m_buttonWidth = 150;
    int m_buttonHeight = 40;
    GridBagPanel m_spacePanel = new GridBagPanel();
    Panel m_MainPanel = new Panel(new BorderLayout());
    
    Vector m_Experiments = new Vector();
    Vector m_listedExperiments = new Vector();

 //  Expt List Panel Stuff
    SortedFixedList m_ExptList;
    Experiment m_exptIndex = null;
    
    SortedFixedList m_AccessGroupList;
    Vector m_accessGroups = new Vector();

    CheckboxGroup m_allowObservers = new CheckboxGroup();
    ColorTextField m_observerPass = new ColorTextField("",10);
 
    TextArea m_exptDesc = new TextArea("",6,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

    GraphicButton m_StartButton, m_BackButton, m_HelpButton; 
    
    public RegisterExperimentPage(ExptOverlord app)
        {
        m_EOApp = app;
         
        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("rep_title"));
        setBackground(m_EOApp.getDispBkgColor());
        setFont(m_EOApp.getSmLabelFont());

    // Build North Panel

        GridBagPanel northPanel = new GridBagPanel();

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

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("rep_ag")),1,1,4,1,GridBagConstraints.CENTER);
        northPanel.constrain(m_AccessGroupList,1,2,4,6,GridBagConstraints.CENTER);

        GridBagPanel tmpGBPanel = new GridBagPanel();
 
        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("rep_ao")),1,1,4,1);
        tmpGBPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("rep_yes"),true,m_allowObservers),1,2,4,1);
        tmpGBPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("rep_no"),false,m_allowObservers),1,3,4,1);

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("rep_op")),1,4,2,1);
        tmpGBPanel.constrain(m_observerPass,3,4,2,1);

        northPanel.constrain(tmpGBPanel,5,1,4,8);
  // End Build North Panel

  // Start setup for center panel
        GridBagPanel centerPanel = new GridBagPanel();

        /* ExptName, NumUsers */
        int[] exptSpacing = { 20, 5};
        m_ExptList = new SortedFixedList(8,false,2,exptSpacing,FixedList.CENTER);

        m_AccessGroupList.select(0);

        m_Experiments = m_EOApp.loadExptFileList(m_accessGroups);

        CreateExptFileList(0);
        
        m_ExptList.addItemListener(this);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("rep_ennu")),1,1,4,1,GridBagConstraints.SOUTH);
        centerPanel.constrain(m_ExptList,1,2,4,8,GridBagConstraints.NORTH);

        m_exptDesc.setEditable(false);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("rep_description")),5,1,4,1,GridBagConstraints.SOUTH);
        centerPanel.constrain(m_exptDesc,5,2,4,8);
   // End setup for the center panel.

 
   // Start Setup for South Panel
        Panel southPanel = new Panel(new GridLayout(1,3));

        m_StartButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_StartButton.addActionListener(this);
        southPanel.add(m_StartButton);
       
        m_BackButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_BackButton.addActionListener(this);
        southPanel.add(m_BackButton); 

        m_HelpButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_HelpButton.addActionListener(this);
        southPanel.add(m_HelpButton); 
   // End Setup for South Panel.
        
        m_MainPanel.add("North",northPanel);
        m_MainPanel.add("Center",centerPanel);
        m_MainPanel.add("South",southPanel);

        LoadImages();

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

        m_ExptList.setSize(m_ExptList.getPreferredSize());
        m_AccessGroupList.setSize(m_AccessGroupList.getPreferredSize());

        this.validate();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (m_EOApp.getEditMode())
            return;

        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            if (theSource == m_StartButton)
                {
                if (m_exptIndex != null)
                    {
                    m_exptIndex.setObserverPass(m_observerPass.getText());
                    m_exptIndex.setAllowObservers(false);
                    if (m_allowObservers.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("rep_yes")))
                        m_exptIndex.setAllowObservers(true);
                    m_EOApp.setEditMode(true);
                    new SetUsersWindow(m_EOApp,this,m_exptIndex);
                    }
                }
            if (theSource == m_BackButton)
                {
                // Handle Back
	          m_EOApp.removeThenAddPanel(this, new OptionsPage(m_EOApp));
                }
            if (theSource == m_HelpButton)
                {
                // Handle Help
	          m_EOApp.helpWindow("ehlp_rep");
                }
            }
        }

    public String[] BuildExptListEntry(Experiment exp)
        {
        String[] str = new String[2];

        str[0] = exp.getExptName();
        str[1] = new String(""+exp.getNumUsers());

        return str;
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
        m_listedExperiments.removeAllElements();
        m_ExptList.removeAll();

        Hashtable h = (Hashtable)m_accessGroups.elementAt(loc);
        String uid = new String("-");
        if (h.containsKey("App ID"))
            uid = (String)h.get("App ID");

        Enumeration enm = m_Experiments.elements();
        while (enm.hasMoreElements())
            {
            Experiment expt = (Experiment)enm.nextElement();

            String uid2 = new String("-");
            if (expt.getAppID() != null)
                uid2 = expt.getAppID();

            if (uid.equals(uid2))
                {
                m_ExptList.addItem(BuildExptListEntry(expt));

                m_listedExperiments.insertElementAt(expt,m_ExptList.last);
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/web/rep.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {

        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_AccessGroupList) && (m_AccessGroupList.getSelectedIndex() >= 0))
                {
                int index = m_AccessGroupList.getSelectedIndex();
                CreateExptFileList(index);
                m_exptDesc.setText("");
                }

            if (theSource == m_ExptList)
                {
                if (m_ExptList.getSelectedIndex() > -1)
                    {
                    m_exptIndex = (Experiment)m_listedExperiments.elementAt(m_ExptList.getSelectedIndex());
                    m_exptDesc.setText(m_exptIndex.getExptDesc());
                    }
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/web/rep.txt");
        }

    private void LoadImages()
        {
        int x, y;
        Graphics g;
        Image tmp, tmp2;

        // Initialize Button Image
        tmp = m_EOApp.getButtonImage();

    // Create Start Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("rep_setup").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("rep_setup"),x,y);
     
        m_StartButton.setImage(tmp2);

    // Create Back Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("rep_back").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("rep_back"),x,y);
     
        m_BackButton.setImage(tmp2);

    // Create Help Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("rep_help").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("rep_help"),x,y);
     
        m_HelpButton.setImage(tmp2);
        }
    }
