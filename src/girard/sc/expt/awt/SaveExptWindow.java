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
 * Used to save an Experiment to the database.  Lists all previously saved
 * experiments by user and group.  Group access is determined by which software
 * applications the user is granted read and write access to through the Web-Lab.
 * <p>
 * <br> Started: 2000
 * <br> Modified: 10-23-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class SaveExptWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
/**
 * Allows access to the Experiment this is to be saved and so we can reset the
 * m_editMode of the ExptBuilderWindow.
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
 * The buttons that either save the Experiment to the database, or cancel out.
 */
    Button m_SaveOK, m_SaveCancel;

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
 * The name to call the Experiment, we need this for when checking for file overwrite.
 */
    String m_saveName = "None";
/**
 * 
 */
    Hashtable m_accessGroup = null;

/**
 * Overwrite popup window for making sure we want to overwrite a file.
 */
    Frame m_CFFOWFrame;
    GridBagPanel m_CFFOWPanel;
    Button m_OverwriteNo, m_OverwriteYes;

    public SaveExptWindow(ExptOverlord app1, ExptBuilderWindow app2)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_EBWApp = app2; /* Need so can unset edit mode */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getWinBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("sew_title"));
        setFont(m_EOApp.getMedWinFont());
         
    // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("sew_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("sew_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar
      
   // Start Setup for Center Panel.
        GridBagPanel centerPanel = new GridBagPanel();

        m_SavedExptList = new SortedFixedList(6,false,1,25);
        m_SavedExptList.addItemListener(this);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sew_experiments")),1,1,4,1,GridBagConstraints.CENTER);
        centerPanel.constrain(m_SavedExptList,1,2,4,6,GridBagConstraints.CENTER); 

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sew_description")),1,8,4,1,GridBagConstraints.CENTER);
        m_exptDesc.setEditable(false);
        centerPanel.constrain(m_exptDesc,1,9,4,4,GridBagConstraints.CENTER);

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

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sew_ag")),5,1,4,1,GridBagConstraints.CENTER);
        centerPanel.constrain(m_AccessGroupList,5,2,4,6,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sew_description")),5,8,4,1,GridBagConstraints.CENTER);
        m_agDesc.setEditable(false);
        centerPanel.constrain(m_agDesc,5,9,4,4,GridBagConstraints.CENTER);

        LoadExptFileList();

        m_AccessGroupList.select(0);
        

        CreateExptFileList(0);
   // End Setup for Center Panel

   // Start Setup for South Panel
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sew_sa")),1,1,2,1);

        m_ExptNameField = new TextField(20);
        southPanel.constrain(m_ExptNameField,3,1,2,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sew_ga")),1,2,2,1);

        m_AccessGroupField = new TextField(20);
        m_AccessGroupField.setEditable(false);
        southPanel.constrain(m_AccessGroupField,3,2,4,1);

        m_AccessGroupField.setText(m_AccessGroupList.getSelectedSubItem(0));

        m_SaveOK = new Button(m_EOApp.getLabels().getObjectLabel("sew_ok"));
        m_SaveOK.addActionListener(this);
        southPanel.constrain(m_SaveOK,1,3,2,1,GridBagConstraints.CENTER);

        m_SaveCancel = new Button(m_EOApp.getLabels().getObjectLabel("sew_cancel"));
        m_SaveCancel.addActionListener(this);
        southPanel.constrain(m_SaveCancel,3,3,2,1,GridBagConstraints.CENTER);
  // End Setup for South Panel.

        add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));
        add("South",new BorderPanel(southPanel,BorderPanel.FRAME));

        pack();
  
        m_SavedExptList.setSize(m_SavedExptList.getPreferredSize());
        m_AccessGroupList.setSize(m_AccessGroupList.getPreferredSize());
        setSize(getPreferredSize());

        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
       
            if (theSource == m_SaveCancel)
                {
                // Handle Save
                m_EBWApp.setEditMode(false);
                dispose();
                return;
                }
            if (theSource == m_SaveOK)
                {
                if (m_ExptNameField.getText().trim().length() == 0)
                    return;

                m_saveName = m_ExptNameField.getText().trim();
                Hashtable h = (Hashtable)m_accessGroups.elementAt(m_AccessGroupList.getSelectedIndex());
                if (h.containsKey("App ID"))
                    m_accessGroup = h;
                else
                    m_accessGroup = null;

                if (!CheckForOverwrite())
                    {
                    if (SaveExpt(m_saveName,m_accessGroup))
                        {
                        m_EBWApp.setEditMode(false);
                        removeLabels();
                        dispose();
                        return;
                        }
                    else
                        {
                        new ErrorDialog(m_EOApp.getLabels().getObjectLabel("sew_ftpstf"));
                        }
                    }
                }
            if (theSource == m_OverwriteYes)
                {
                if (SaveExpt(m_saveName,m_accessGroup))
                    {
                    m_EBWApp.setEditMode(false);
                    m_CFFOWFrame.dispose();
                    removeLabels();
                    dispose();
                    return;
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("sew_ftpstf"));
                    }
                }
            if (theSource == m_OverwriteNo)
                {
                m_CFFOWFrame.dispose();
                m_SaveOK.addActionListener(this);
                m_SaveCancel.addActionListener(this);
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("sew_help")))
                {
                m_EOApp.helpWindow("ehlp_sew");
                }
            }
        }

    public boolean CheckForOverwrite()
        {
        boolean flag = false;

        for (int i=0;i<m_SavedExptList.getItemCount();i++)
            {
            if (m_SavedExptList.getSubItem(i,0).equals(m_ExptNameField.getText()))
                {
                flag = true;
                break;
                }
            }

        if (!flag)
            return flag;

        m_CFFOWFrame = new Frame("File Already Exists");
        m_CFFOWFrame.setLayout(new GridLayout(1,1));
        m_CFFOWFrame.setBackground(m_EOApp.getWinBkgColor());
        m_CFFOWFrame.setFont(m_EOApp.getMedWinFont());

        m_CFFOWPanel = new GridBagPanel();

        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sew_fae")),1,1,4,1);
        m_CFFOWPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sew_dywtoi")),1,2,4,1);

        m_OverwriteYes = new Button(m_EOApp.getLabels().getObjectLabel("sew_yes"));
        m_OverwriteYes.addActionListener(this);

        m_OverwriteNo = new Button(m_EOApp.getLabels().getObjectLabel("sew_no"));
        m_OverwriteNo.addActionListener(this);

        m_CFFOWPanel.constrain(m_OverwriteYes,1,3,2,1);
        m_CFFOWPanel.constrain(m_OverwriteNo,3,3,2,1);

        m_SaveOK.removeActionListener(this);
        m_SaveCancel.removeActionListener(this);
 
        m_CFFOWFrame.add(m_CFFOWPanel);
        m_CFFOWFrame.pack();
        m_CFFOWFrame.show();

        return flag;
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

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/sew.txt");
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
        m_EOApp.removeLabels("girard/sc/expt/awt/sew.txt");
        }

    public boolean SaveExpt(String name, Hashtable ag)
        {
        m_EBWApp.getExpApp().setExptName(name);

        if (ag != null)
            {
            if (ag.containsKey("App ID"))
                {
                m_EBWApp.getExpApp().setAppID((String)ag.get("App ID"));
                m_EBWApp.getExpApp().setAppName((String)ag.get("App Name"));
                }
            System.err.println("err is null");
            }
        else
            {
            m_EBWApp.getExpApp().setAppID(null);
            m_EBWApp.getExpApp().setAppName(null);
            }
        System.err.println("before returning");
        return m_EOApp.saveExpt(m_EBWApp.getExpApp());
        }
    }