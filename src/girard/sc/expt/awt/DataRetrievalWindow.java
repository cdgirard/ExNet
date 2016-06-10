package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.msg.BaseActionTypesListReqMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

public class DataRetrievalWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    
    GridBagPanel m_MainPanel = new GridBagPanel();

   // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_View, m_Help;

    Vector m_Experiments = new Vector(); // A list of main experiment types to load a file from.
    Vector m_Actions = new Vector();  // A list of base actions to load a data file from.

    FixedList m_DataList;
    int m_dataIndex = -1;

    TextArea m_Desc = new TextArea(4,25); // A description of the experiment or action.

    boolean m_EditMode = false;

    public DataRetrievalWindow(ExptOverlord app)
        {
        m_EOApp = app;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("drw_title"));
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

   // File Menu
        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("drw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("drw_retrieve"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("drw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

   // View Menu
        m_View = new Menu(m_EOApp.getLabels().getObjectLabel("drw_view"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("drw_experiment"));
        tmpMI.addActionListener(this);
        m_View.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("drw_at"));
        tmpMI.addActionListener(this);
        m_View.add(tmpMI);

        m_mbar.add(m_View);

    // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("drw_help"));
        m_mbar.add(m_Help);

    // Setup List of Actions
        GridBagPanel tmpGBPanel = new GridBagPanel();

        loadAvailableActions();

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("drw_bal")),1,1,4,1);
        m_DataList = new FixedList(8,false,1,25);
        m_DataList.addItemListener(this);

        fillDataList();

        tmpGBPanel.constrain(m_DataList,1,2,4,8);

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("drw_details")),1,10,4,1);

        tmpGBPanel.constrain(m_Desc,1,11,4,4);

        m_MainPanel.constrain(tmpGBPanel,1,1,1,1);
    // End Setup List of Actions

     // End Setup User Fields

        add("Center",m_MainPanel);

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
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("drw_exit")))
                {
                m_EOApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }

            if ((m_dataIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("drw_details"))))
                {
                // setEditMode(true);
                }

            }
        }

    public void fillDataList()
        {
        }

    public boolean getEditMode()
        {
        return m_EditMode;
        }
    public ExptOverlord getEOApp()
        {
        return m_EOApp;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/drw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_DataList)
                {
                m_dataIndex = theSource.getSelectedIndex();
                }
            }
         }

    public void loadAvailableActions()
        {
        BaseActionTypesListReqMsg tmp = new BaseActionTypesListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof BaseActionTypesListReqMsg)
            {
            Object[] in_args = em.getArgs();
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/drw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    public void setEOApp(ExptOverlord obj)
        {
        m_EOApp = obj;
        }
    }
