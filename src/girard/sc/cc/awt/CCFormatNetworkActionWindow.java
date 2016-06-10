package girard.sc.cc.awt;

/* Formats the CCNetworkAction by allowing one to assign resources, tokens,
   fuzzies, and sanctions to various nodes.

   Author: Dudley Girard
   Started: 5-30-2001
   Modified: 7-31-2001
*/

import girard.sc.awt.GraphicButton;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNetworkAction;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CCFormatNetworkActionWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    CCNetworkAction m_CNApp;
    ExptBuilderWindow m_EBWApp;

    Panel m_MainPanel = new Panel(new BorderLayout());

   // North Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Edit, m_Format, m_Help;

   // Center Area
    Panel m_ExnetDrawingAreaPanel = new Panel();
    ScrollPane m_DrawingAreaSP = new ScrollPane();
    Panel m_DrawingAreaP = new Panel(new GridLayout(1,1));
    CCNetworkActionCanvas m_DrawingAreaC;

    boolean m_EditMode = false;

    public CCFormatNetworkActionWindow(ExptOverlord app1, ExptBuilderWindow app2,CCNetworkAction app3)
        {
        m_EOApp = app1;
        m_EBWApp = app2;
        m_CNApp = app3;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ccfnaw_title"));
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());
   
        m_DrawingAreaC = new CCNetworkActionCanvas(m_EOApp,(CCNetwork)m_CNApp.getAction());
        m_DrawingAreaC.setBackground(m_EOApp.getDispBkgColor());
        m_DrawingAreaP.setSize(300,300);

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
    
    // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("ccfnaw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccfnaw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

    // Edit Menu Options

        m_Edit = new Menu(m_EOApp.getLabels().getObjectLabel("ccfnaw_edit"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccfnaw_description"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        m_mbar.add(m_Edit);

    // Format Menu Options

        m_Format = new Menu(m_EOApp.getLabels().getObjectLabel("ccfnaw_format"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccfnaw_exchanges"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccfnaw_period"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccfnaw_pay"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccfnaw_nr"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        m_mbar.add(m_Format);
 
    // Format Menu Options

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ccfnaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccfnaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);


    // Setup Center Area
        m_ExnetDrawingAreaPanel.setLayout(new GridLayout(1,1));
        m_DrawingAreaP.add(m_DrawingAreaC);
        m_DrawingAreaSP.add(m_DrawingAreaP);
        m_ExnetDrawingAreaPanel.add(m_DrawingAreaSP);

        add("Center",m_ExnetDrawingAreaPanel);
        
        m_DrawingAreaC.repaint();
        show();
        }
 
    public void actionPerformed(ActionEvent e) 
        {
        if (m_EditMode)
            return;
        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            }
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    
        // File Menu

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccfnaw_done")))
                {
                m_EBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
       // Edit Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccfnaw_description")))
                {
                setEditMode(true);
                new CCEditDescriptionWindow(m_EOApp,this,m_CNApp);
                }

       // Format Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccfnaw_pay")))
                {
                setEditMode(true);
                new CCSetPayWindow(m_EOApp,this,(CCNetwork)m_CNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccfnaw_exchanges")))
                {
                setEditMode(true);
                new CCNumExchWindow(m_EOApp,this,(CCNetwork)m_CNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccfnaw_period")))
                {
                setEditMode(true);
                new CCEditPeriodWindow(m_EOApp,this,(CCNetwork)m_CNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccfnaw_nr")))
                {
                setEditMode(true);
                new CCEditNodeResourceWindow(m_EOApp,this,(CCNetwork)m_CNApp.getAction());
                }
         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccfnaw_help")))
                {
                m_EOApp.helpWindow("ehlp_ccfnaw");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccfnaw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccfnaw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    }
