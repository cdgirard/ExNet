package girard.sc.expt.web;

import girard.sc.awt.DescriptionDialog;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.io.msg.ExptActionDataListReqMsg;
import girard.sc.expt.io.msg.ExptActionTypesListReqMsg;
import girard.sc.expt.io.msg.ExptDataListReqMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.LoadDataFileReqMsg;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.web.WebPanel;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * DataPage: Is the page that allows experimenters to
 * retrieve any experimental data from any ExSoc actions that they
 * have access to.  Files are listed by Experiment and Access Group initially,
 * for each Experiment-Group the individual ExperimentAction files are listed.
 * <p>
 * <br> Started: 08-19-2001
 * <br> Modified: 10-24-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class DataPage extends WebPanel implements ActionListener, ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
 
    GridBagPanel m_spacePanel;

/**
 * Where the data for an ExperimentAction is stored for viewing.
 */
    BaseDataInfo m_dataObjectIndex = null;

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
 * Contains a list of all valid ExperimentActions.
 */
    Vector m_exptActions = new Vector();
/**
 * Contains a list of descriptions for the ExperimentActions.
 */
    Vector m_actionDescriptions = new Vector();
/**
 * Conatins the list of ExperimentActions for a selected Experiment file.
 */
    Vector m_dataIndexes = new Vector();
    
/**
 * Where the list of possible Experiment data files to view are displayed.
 */
    SortedFixedList m_ExptList;

/**
 * The list of all the access groups the user has access to.
 */
    SortedFixedList m_AccessGroupList;

/**
 * Where the list of ExperimentActions run from an Experiment file is displayed.
 */
    FixedList m_DataList;

/**
 * Used to display the name of the selected ExperimentAction data file.
 */
    TextField m_dataNameLabel;
/**
 * Used to display the exact date the selected ExperimentAction data file
 * was run.
 */
    FixedLabel m_dataDateLabel;

/**
 * Button used to retrieve a specific Experiment data file.
 */
    Button m_ExptRetrieveButton;

/**
 * Button used to call up a window for viewing the description of a selected Experiment
 * file.
 */
    Button m_ViewExptDesc;

/**
 * Button used to call up a window for viewing the description of a selected
 * Access Group.
 */
    Button m_ViewAGDesc;

/**
 * Button used to retrieve a specific ExperimentAction data file.
 */    
    Button m_DataRetrieveButton;
   
/**
 * Buttons used to either go back to the Options page or call up the help file
 * on the Data page.
 */  
    GraphicButton m_BackButton, m_HelpButton;
/**
 * How wide to make the GraphicButtons.
 */
    int m_buttonWidth = 150;
/**
 * How tall to make the GraphicButtons.
 */
    int m_buttonHeight = 40;

/**
 * The Constructor.
 *
 * @param app The ExptOverlord to set m_EOApp to.
 */    
    public DataPage(ExptOverlord app)
        {
        m_EOApp = app;
         
        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("dp_title"));
        setBackground(m_EOApp.getDispBkgColor());
        setFont(m_EOApp.getSmLabelFont());

    // Build North Panel
        GridBagPanel NorthPanel = new GridBagPanel();

        loadExptActions();

        int[] exptListSpacing = { 30, 10};
        m_ExptList = new SortedFixedList(10,false,2,exptListSpacing,FixedList.CENTER);
        m_ExptList.setFont(m_EOApp.getSmLabelFont());
        m_ExptList.addItemListener(this);

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dp_ed")),1,1,4,1);
        NorthPanel.constrain(m_ExptList,1,2,4,10,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

        m_ExptRetrieveButton = new Button(m_EOApp.getLabels().getObjectLabel("dp_retrieve"));
        m_ExptRetrieveButton.addActionListener(this);
        NorthPanel.constrain(m_ExptRetrieveButton,1,12,2,1,GridBagConstraints.CENTER);

        m_ViewExptDesc = new Button(m_EOApp.getLabels().getObjectLabel("dp_vd"));
        m_ViewExptDesc.addActionListener(this);
        NorthPanel.constrain(m_ViewExptDesc,3,12,2,1,GridBagConstraints.CENTER);

        m_AccessGroupList = new SortedFixedList(10,false,1,25);
        m_AccessGroupList.addItemListener(this);

        String[] str = new String[1];
        str[0] = "<NONE>";
        m_AccessGroupList.addItem(str);
        Hashtable h = new Hashtable();
        h.put("Name","<NONE>");
        h.put("Desc","Only this user may access this file.");
        m_accessGroups.addElement(h);

        CreateAccessGroupList();

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dp_ag")),5,1,4,1,GridBagConstraints.CENTER);
        NorthPanel.constrain(m_AccessGroupList,5,2,4,10,GridBagConstraints.CENTER);

        m_ViewAGDesc = new Button(m_EOApp.getLabels().getObjectLabel("dp_vd"));
        m_ViewAGDesc.addActionListener(this);
        NorthPanel.constrain(m_ViewAGDesc,3,12,2,1,GridBagConstraints.CENTER);

        LoadExptFileList();

        m_AccessGroupList.select(0);

        CreateExptFileList(0);
   // End build north panel

   // Build Center Panel
        GridBagPanel CenterPanel = new GridBagPanel();

        // Expt Action Data Name, Date
        int[] dataSpacing = { 30, 10}; 
        m_DataList = new FixedList(4,false,2,dataSpacing,FixedList.CENTER);
        m_DataList.setFont(m_EOApp.getSmLabelFont());
        m_DataList.addItemListener(this);
        
        CenterPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dp_fdat")),1,1,8,1,GridBagConstraints.SOUTH);
        CenterPanel.constrain(m_DataList,1,2,8,4,GridBagConstraints.NORTH,GridBagConstraints.HORIZONTAL);

        m_DataRetrieveButton = new Button(m_EOApp.getLabels().getObjectLabel("dp_retrieve"));
        m_DataRetrieveButton.addActionListener(this);
        CenterPanel.constrain(m_DataRetrieveButton,1,6,8,1,GridBagConstraints.CENTER);
    // End build center Panel

    // Build East Panel
        GridBagPanel EastPanel = new GridBagPanel();
       
        EastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dp_name")),1,1,2,1,GridBagConstraints.SOUTH);
        m_dataNameLabel = new TextField(30);
        m_dataNameLabel.setEditable(false);
        EastPanel.constrain(m_dataNameLabel,3,1,6,1,GridBagConstraints.SOUTH);

        EastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("dp_date")),1,2,2,1);
        m_dataDateLabel = new FixedLabel(30);
        EastPanel.constrain(m_dataDateLabel,3,2,6,1);       
    // End build East panel.
        
   // Build the South Panel
        
        GridBagPanel SouthPanel = new GridBagPanel();

        m_BackButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_BackButton.addActionListener(this);

        m_HelpButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_HelpButton.addActionListener(this);

        loadImages();

        SouthPanel.constrain(m_BackButton,1,1,1,1,GridBagConstraints.CENTER);
        SouthPanel.constrain(m_HelpButton,2,1,1,1,GridBagConstraints.CENTER);
  // End build South Panel.

   // Attach panels to Main Panel

        Panel MainPanel = new Panel(new BorderLayout());
        
        MainPanel.add("North",NorthPanel);
        MainPanel.add("Center",CenterPanel);
        MainPanel.add("East",EastPanel);
        MainPanel.add("South",SouthPanel);

        m_spacePanel = new GridBagPanel();

        m_spacePanel.constrain(MainPanel,1,1,60,40,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);

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
        }

/**
 * Handles all the ActionEvnets.
 *
 * @param e The ActionEvent that trigger the function.
 */
    public void actionPerformed (ActionEvent e)
        {
        if (m_EOApp.getEditMode())
            return;

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if ((theSource == m_ExptRetrieveButton) && (m_ExptList.getSelectedIndex() > -1))
                {
                Hashtable expt = (Hashtable)m_listedExpts.elementAt(m_ExptList.getSelectedIndex()); 

                int exptOutID = ((Integer)expt.get("Expt_Out_ID_INT")).intValue();

                loadExptData(exptOutID);

                m_DataList.removeAll();
                m_dataNameLabel.setText("");
                m_dataDateLabel.setText("");

                Enumeration enm = m_dataIndexes.elements();
                while (enm.hasMoreElements())
                    {
                    Hashtable h = (Hashtable)enm.nextElement();
                    m_DataList.addItem(BuildDataListEntry(h));
                    }
                }
            if ((theSource == m_DataRetrieveButton) && (m_DataList.getSelectedIndex() > -1))
                {
                Hashtable h = (Hashtable)m_dataIndexes.elementAt(m_DataList.getSelectedIndex());
                ExperimentAction ea = null;
                int eaID = ((Integer)h.get("Action_Object_Index_INT")).intValue();
                Enumeration enm = m_exptActions.elements();
                while (enm.hasMoreElements())
                    {
                    ExperimentAction tmp = (ExperimentAction)enm.nextElement();
                    if (tmp.getActionType() == eaID)
                        {
                        ea = tmp;
                        break;
                        }
                    }

                if (loadDataFile(h,ea))
                    {
                    m_EOApp.setEditMode(true);
                    m_dataObjectIndex.getAction().displayData(m_EOApp,m_dataObjectIndex);
                    }
                }

            if ((theSource == m_ViewAGDesc) && (m_AccessGroupList.getSelectedIndex() > -1))
                {
                Hashtable ag = (Hashtable)m_accessGroups.elementAt(m_AccessGroupList.getSelectedIndex());
                if (ag.containsKey("App Desc"))
                    new DescriptionDialog((String)ag.get("App Desc"));
                }
            if ((theSource == m_ViewExptDesc) && (m_ExptList.getSelectedIndex() > -1))
                {
                Hashtable expt = (Hashtable)m_listedExpts.elementAt(m_ExptList.getSelectedIndex()); 
                new DescriptionDialog((String)expt.get("Expt_Desc_VC"));
                }
            }

        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            if (theSource == m_BackButton)
                {
                // Handle Back
                m_EOApp.removeThenAddPanel(this,new OptionsPage(m_EOApp));
                }
            if (theSource == m_HelpButton)
                {
                // Handle Help
                m_EOApp.helpWindow("ehlp_dp");
                }
            }
        }

/**
 * Builds a list entry for the m_DataList.  The Hashtable passed in should
 * have the following two keys: Action_Name_VC and Date_Run_DATE.
 *
 * @param h Hashtable of information needed to build the list entry.
 */
    private String[] BuildDataListEntry(Hashtable h)
        {
        String[] str = new String[2];
 
        str[0] = (String)h.get("Action_Name_VC");
        str[1] = ((Timestamp)h.get("Date_Run_DATE")).toString();

        return str;
        }

/**
 * Builds a list entry for the m_ExptList.  The Hashtable passed in should
 * have the following two keys: Expt_Name_VC and Date_Run_DATE.
 *
 * @param h Hashtable of information needed to build the list entry.
 */
    private String[] BuildExptListEntry(Hashtable expt)
        {
        String[] str = new String[2];
 
        str[0] = (String)expt.get("Expt_Name_VC");
        str[1] = ((Timestamp)expt.get("Date_Run_DATE")).toString();

        return str;
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
 * Fills in the m_SavedExptList and m_listedExpts based on the selected access group.
 *
 * @param loc The index of the access group in m_accessGroups.
 */
    public void CreateExptFileList(int loc) 
        {
        m_listedExpts.removeAllElements();
        m_ExptList.removeAll();

        Hashtable h = (Hashtable)m_accessGroups.elementAt(loc);
        String uid = new String("-");
        if (h.containsKey("App ID"))
            uid = (String)h.get("App ID");

        Enumeration enm = m_allExperiments.elements();
        while (enm.hasMoreElements())
            {
            Hashtable expt = (Hashtable)enm.nextElement();

            String uid2 = new String("-");
            if (expt.containsKey("App ID"))
                uid2 = (String)expt.get("App ID");

            if (uid.equals(uid2))
                {
                m_ExptList.addItem(BuildExptListEntry(expt));

                m_listedExpts.insertElementAt(expt,m_ExptList.last);
                }
            }
        }

/**
 * Processes ItemEvents.
 *
 * @param e The ItemEvent that triggered this function.
 */
    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();
 
            if (theSource == m_DataList)
                {
                if (m_DataList.getSelectedIndex() > -1)
                    {
                    int dataListIndex = m_DataList.getSelectedIndex();
                    Hashtable h = (Hashtable)m_dataIndexes.elementAt(dataListIndex);
                    m_dataNameLabel.setText((String)h.get("Action_Name_VC"));
                    m_dataDateLabel.setText(((Timestamp)h.get("Date_Run_DATE")).toString());
                    }
                }

            }
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_AccessGroupList) && (m_AccessGroupList.getSelectedIndex() >= 0))
                {
                int index = m_AccessGroupList.getSelectedIndex();
                Hashtable h = (Hashtable)m_accessGroups.elementAt(index);
                CreateExptFileList(index);
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
        m_EOApp.initializeLabels("girard/sc/expt/web/dp.txt");
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
        m_EOApp.removeLabels("girard/sc/expt/web/dp.txt");
        }

/**
 * Loads the data list for the ExperimentActions from the selected Experiment data file.
 * Does this using the ExptActionDataListReqMsg class object.
 */
    private void loadExptData(int exptOutID)
        {
        Object[] out_args = new Object[1];
        out_args[0] = new Integer(exptOutID);
        ExptActionDataListReqMsg tmp = new ExptActionDataListReqMsg(out_args);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof ExptActionDataListReqMsg)
            {
            Object[] in_args = em.getArgs();
            m_dataIndexes = (Vector)in_args[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }
/**
 * Loads the data for the selected ExperimentAction data file using LoadDataFileReqMsg.
 *
 * @param h The information on the ExperimantAction file to be loaded so it can be found.
 * @param ea The base object that the ExperimentAction is based on so we can reform it.
 * @return Returns true if successful, false otherwise.
 */
    private boolean loadDataFile(Hashtable h, ExperimentAction ea)
        {
        Object[] out_args = new Object[3];
        out_args[0] = h.get("Expt_Out_ID_INT");
        out_args[1] = h.get("Action_Index_INT");
        out_args[2] = ea;
        LoadDataFileReqMsg tmp = new LoadDataFileReqMsg(out_args);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);
	System.err.println(" in the load file method of the DataPage");

        if (em instanceof LoadDataFileReqMsg)
            {
            Object[] in_args = em.getArgs();
            m_dataObjectIndex = (BaseDataInfo)in_args[0];
            return true;
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            return false;
            }
        }
/**
 * Requests a list of saved Experiment data files that can be loaded by the user, based
 * on user's ID and user's group access. Does this by sending a 
 * ExptDataListReqMsg.
 * 
 * @see girard.sc.expt.io.msg.ExptFileListReqMsg
 */
    public void LoadExptFileList()
        {
        Object[] out_args = new Object[1];
        out_args[0] = m_accessGroups;
        ExptDataListReqMsg tmp = new ExptDataListReqMsg(out_args);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof ExptDataListReqMsg)
            {
            Object[] in_args = em.getArgs();
            m_allExperiments = (Vector)in_args[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }
/**
 * Loads the available ExperimentActions.
 */
    private void loadExptActions()
        {
        ExptActionTypesListReqMsg tmp = new ExptActionTypesListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof ExptActionTypesListReqMsg)
            {
            Object[] in_args = em.getArgs();
            m_exptActions = (Vector)in_args[0];
            m_actionDescriptions = (Vector)in_args[1];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }
/**
 * Loads the images for the GraphicButtons.
 */
    private void loadImages()
        {
        Graphics g;
        int x, y;
        Image tmp, tmp2;

        // Initialize Button Image
        tmp = m_EOApp.getButtonImage();

    // Create Back Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("dp_back").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("dp_back"),x,y);
     
        m_BackButton.setImage(tmp2);

    // Create Help Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("dp_help").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("dp_help"),x,y);
     
        m_HelpButton.setImage(tmp2);
        }
    }
