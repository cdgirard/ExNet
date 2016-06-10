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
 * Allows user to load ExNet 3.0 Base Actions they have created.
 * <p>
 * <br>Started: 2-13-2002
 * <br>Modified: 10-08-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
*/

public class LoadBaseActionWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
/**
 * The link to the BaseActionFormatWindow;  This allows you to reset its m_EditMode
 * variable to false when done formating;  Otherwise the ActionBuilderWindow doesn't
 * know when to start processing action events again.
 * 
 */
    BaseActionFormatWindow m_BAFWApp;
/**
 * The BaseAction object type being loaded.
 *
 */
    BaseAction m_BApp;

/**
 * Displays the selected BaseAction file that is to be loaded.
 */
    TextField m_FileNameField;
/**
 * The displayed list of saved BaseAction files that can be loaded.
 *
 */
    SortedFixedList m_SaveFileList;
/**
 * Displays the list of access groups the user has access to.
 */
    SortedFixedList m_AccessGroupList;
/**
 * Used to respectively: Load the selected file, exit out, and call up the
 * HelpWindow.
 * 
 * @see girard.sc.expt.help.HelpWindow
 */
    Button m_OKButton, m_CancelButton, m_HelpButton;

/**
 * Displays the description of the selected BaseAction file.
 */
    TextArea m_baDesc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);
/**
 * Displays the description of the selected access group.
 */
    TextArea m_agDesc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

/**
 * The list of filenames for the possible BaseAction files to load.
 *
 */
    Vector m_fileNames = new Vector();
/**
 * The list of files presently displayed in m_SavedFileList.
 */
    Vector m_listedFiles = new Vector();
/**
 * The list of access groups displayed in m_AccessGroupList.
 */
    Vector m_accessGroups = new Vector();

/**
 * The constructor.
 *
 * @param app1 The ExptOverlord.
 * @param app2 The BaseActionFormatWindow
 * @param app3 The BaseAction class type to load a file from.
 */
    public LoadBaseActionWindow(ExptOverlord app1, BaseActionFormatWindow app2, BaseAction app3)
        {
        super ();

        m_EOApp = app1;
        m_BAFWApp = app2;
        m_BApp = app3;

        initializeLabels();

  //  Setup Button and Label Fields
        setTitle(m_EOApp.getLabels().getObjectLabel("lbaw_title"));
        setBackground(m_EOApp.getWinBkgColor());
        setFont(m_EOApp.getMedWinFont());
        setLayout(new BorderLayout());

   // Start Construction of Center Panel
        GridBagPanel centerPanel = new GridBagPanel();

        m_SaveFileList = new SortedFixedList(8,false,1,25);
        m_SaveFileList.addItemListener(this);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lbaw_sf")),1,1,4,1);
        centerPanel.constrain(m_SaveFileList,1,2,4,8);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lbaw_description")),1,10,4,1,GridBagConstraints.CENTER);
        m_baDesc.setEditable(false);
        m_baDesc.setFont(m_EOApp.getSmWinFont());
        centerPanel.constrain(m_baDesc,1,11,4,4,GridBagConstraints.CENTER);

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

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lbaw_ag")),5,1,4,1,GridBagConstraints.CENTER);
        centerPanel.constrain(m_AccessGroupList,5,2,4,8,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lbaw_description")),5,10,4,1,GridBagConstraints.CENTER);
        m_agDesc.setEditable(false);
        m_agDesc.setFont(m_EOApp.getSmWinFont());
        centerPanel.constrain(m_agDesc,5,11,4,4,GridBagConstraints.CENTER);

        LoadFileList();

        CreateSavedFileList(0);
    // End Construction of Center Panel

    // Start Construction of South Panel
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("lbaw_fn")),1,1,2,1);
        m_FileNameField = new TextField(25);
        southPanel.constrain(m_FileNameField,3,1,4,1);

        m_OKButton = new Button(m_EOApp.getLabels().getObjectLabel("lbaw_ok"));
        m_OKButton.addActionListener(this);
        southPanel.constrain(m_OKButton,1,2,2,1);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("lbaw_cancel"));
        m_CancelButton.addActionListener(this);
        southPanel.constrain(m_CancelButton,3,2,2,1);

        m_HelpButton = new Button(m_EOApp.getLabels().getObjectLabel("lbaw_help"));
        m_HelpButton.addActionListener(this);
        southPanel.constrain(m_HelpButton,5,2,2,1);
   // End Construction of South Panel.

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
      
        if (theSource == m_OKButton)
            {
            if (m_SaveFileList.getSelectedIndex() >= 0)
                {
                Hashtable fileInfo = (Hashtable)m_listedFiles.elementAt(m_SaveFileList.getSelectedIndex());
                if (LoadBaseAction(fileInfo))
                    {
                    removeLabels();
                    dispose();
                    return;
                    }
                else
                    {
                    new ErrorDialog(m_EOApp.getLabels().getObjectLabel("lbaw_ftpltf"));
                    }
                }
            else
                {
                // Maybe popup an error dialog.
                }
            }
        if (theSource == m_CancelButton)
            {
            m_BAFWApp.setEditMode(false);
            removeLabels();
            dispose(); 
            }
        if (theSource == m_HelpButton)
            {
            m_EOApp.helpWindow("ehlp_lbaw");
            }
        }

/**
 * Requests a list of Access Groups from the ExptOverlord that can be accessed by the 
 * user for granting access rights to the file being loaded. Then displays these groups
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
 * Displays a list of BaseAction files in m_SavedFileList that can be loaded by the 
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

        Enumeration enm = m_fileNames.elements();
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
        m_EOApp.initializeLabels("girard/sc/expt/awt/lbaw.txt");
        }  

/**
 * Used to process ItemEvents.
 * 
 * @param e The ItemEvent to process.
 */  
    public void itemStateChanged(ItemEvent ie)
        {
        if (ie.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)ie.getSource();

            if ((theSource == m_SaveFileList) && (theSource.getSelectedIndex() >= 0))
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
 * Loads the choosen filename by using the loadBaseAction function in the ExptOverlord
 * class.
 *
 * @param fileInfo Hashtable of information needed to load the specific BaseAction file.
 * @see girard.sc.expt.web.ExptOverlord
 */
    public boolean LoadBaseAction(Hashtable fileInfo)
        {
        BaseAction ba = m_EOApp.loadBaseAction(fileInfo,m_BApp);

        if (ba != null)
            {
            m_BAFWApp.setEditMode(false);
            m_BAFWApp.setActiveBaseAction(ba);
            return true;
            }

        return false;
        }
/**
 * Loads the list of saved BaseAction files of a specific type by calling the
 * loadBaseActionFileList function in ExptOverlord.
 *
 * @see girard.sc.expt.web.ExptOverlord
 */
    public void LoadFileList() 
        {
        m_fileNames = m_EOApp.loadBaseActionFileList(m_BApp.getDB(),m_BApp.getDBTable(),m_accessGroups); 
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
        m_EOApp.removeLabels("girard/sc/expt/awt/lbaw.txt");
        }

    }