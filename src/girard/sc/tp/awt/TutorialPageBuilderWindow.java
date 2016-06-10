package girard.sc.tp.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.awt.ActionBuilderWindow;
import girard.sc.expt.awt.BaseActionFormatWindow;
import girard.sc.expt.awt.DeleteBaseActionWindow;
import girard.sc.expt.awt.LoadBaseActionWindow;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.tp.io.msg.BaseTutorialPageTypesListReqMsg;
import girard.sc.tp.obj.TutorialPage;

import java.awt.BorderLayout;
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
import java.util.Vector;

public class TutorialPageBuilderWindow extends BaseActionFormatWindow implements ActionListener,ItemListener
    {
    TutorialPage m_basePage;

    GridBagPanel m_MainPanel = new GridBagPanel();

   // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    Vector m_availableTypes = new Vector();
    Vector m_typeDescriptions = new Vector();

    FixedList m_typeList;
    TutorialPage m_activeType = null;
    int m_typeIndex = -1;

    TextArea m_typeDesc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

    public TutorialPageBuilderWindow(ExptOverlord app1, ActionBuilderWindow app2, TutorialPage app3)
        {
        super(app1,app2,app3);

        m_basePage = app3;

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("tpbw_title"));
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("tpbw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("tpbw_new"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("tpbw_open"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("tpbw_delete"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("tpbw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);
 
    // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("tpbw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("tpbw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);

    // Setup List of Types
        GridBagPanel tmpGBPanel = new GridBagPanel();

        loadAvailableTypes();

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("tpbw_tpt")),1,1,4,1);
        m_typeList = new FixedList(8,false,1,25);
        m_typeList.addItemListener(this);

        fillTypeList();

        tmpGBPanel.constrain(m_typeList,1,2,4,8);

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("tpbw_description")),1,10,4,1);

        tmpGBPanel.constrain(m_typeDesc,1,11,4,4);

        m_MainPanel.constrain(tmpGBPanel,1,1,1,1);
    // End Setup List of Types

     // End Setup User Fields

        getContentPane().add("Center",m_MainPanel);

        pack();
        show();
        }
    
    public void actionPerformed(ActionEvent e) 
        {
        if (getEditMode())
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    
        // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("tpbw_exit")))
                {
                m_ABWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if ((m_typeIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("tpbw_new"))))
                {
                setEditMode(true);

                ((TutorialPage)m_activeType.clone()).formatPage(m_EOApp,this);
                }
            if ((m_typeIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("tpbw_open"))))
                {
                setEditMode(true);
               
                new LoadBaseActionWindow(m_EOApp,this,m_activeType);
                }
            if ((m_typeIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("tpbw_delete"))))
                {
                setEditMode(true);

                new DeleteBaseActionWindow(m_EOApp,this,m_activeType);
                }
        // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("tpbw_help")))
                {
                m_EOApp.helpWindow("ehlp_tpbw");
                }
            }
        }

    public void fillTypeList()
        {
        
        Enumeration enm = m_availableTypes.elements();
        while (enm.hasMoreElements())
            {
            String[] str = new String[1];
            TutorialPage tp = (TutorialPage)enm.nextElement();

            str[0] = tp.getName();
            m_typeList.addItem(str);
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/tp/awt/tpbw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_typeList)
                {
                m_typeIndex = theSource.getSelectedIndex();
                if (m_typeIndex != -1)
                    {
                    m_activeType = (TutorialPage)m_availableTypes.elementAt(m_typeIndex);
                    String str = (String)m_typeDescriptions.elementAt(m_typeIndex);
                    m_typeDesc.setText(str);
                    }
                }
            }
         }

    public void loadAvailableTypes()
        {
        BaseTutorialPageTypesListReqMsg tmp = new BaseTutorialPageTypesListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof BaseTutorialPageTypesListReqMsg)
            {
            Object[] in_args = em.getArgs();
            m_availableTypes = (Vector)in_args[0];
            m_typeDescriptions = (Vector)in_args[1];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/tp/awt/tpbw.txt");
        }

/**
 * We have to override the setActiveBaseAction function so that we use
 * the format page function of the TutorialPages.
 */
    public void setActiveBaseAction(BaseAction ba)
        {
        setEditMode(true);
        TutorialPage tp = (TutorialPage)ba;
        tp.formatPage(m_EOApp,this);
        }
    }
