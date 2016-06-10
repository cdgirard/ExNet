package girard.sc.ce.awt;

import girard.sc.awt.GraphicButton;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENetworkAction;
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

/**
 * Used to format a CE Network Action that has been added to an Experiemnt.
 * <p>
 * <br> Started: 01-28-2003
 * <p>
 * @author Dudley Girard
 */

public class CEFormatNetworkActionWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    CENetworkAction m_CNApp;
    ExptBuilderWindow m_EBWApp;

    Panel m_MainPanel = new Panel(new BorderLayout());

   // North Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Edit, m_Format, m_Help;

   // Center Area
    Panel m_ExnetDrawingAreaPanel = new Panel();
    ScrollPane m_DrawingAreaSP = new ScrollPane();
    Panel m_DrawingAreaP = new Panel(new GridLayout(1,1));
    CENetworkActionCanvas m_DrawingAreaC;

    boolean m_EditMode = false;

    public CEFormatNetworkActionWindow(ExptOverlord app1, ExptBuilderWindow app2,CENetworkAction app3)
        {
        m_EOApp = app1;
        m_EBWApp = app2;
        m_CNApp = app3;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("cefnaw_title"));
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());
   
        m_DrawingAreaC = new CENetworkActionCanvas(m_EOApp,(CENetwork)m_CNApp.getAction());
        m_DrawingAreaC.setBackground(m_EOApp.getDispBkgColor());
        m_DrawingAreaP.setSize(300,300);

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
    
    // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("cefnaw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

    // Edit Menu Options

        m_Edit = new Menu(m_EOApp.getLabels().getObjectLabel("cefnaw_edit"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_description"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_iw"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_it"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_externality"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);
        
        m_mbar.add(m_Edit);

    // Format Menu Options

        m_Format = new Menu(m_EOApp.getLabels().getObjectLabel("cefnaw_format"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_periods"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_pay"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_resources"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_exchanges"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);
	//-kar- 
	// Adding contract formation menu item here
		tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_contracts"));
		tmpMI.addActionListener(this);
		m_Format.add(tmpMI);
	//-kar-

        m_mbar.add(m_Format);
 
    // Format Menu Options

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("cefnaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cefnaw_help"));
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

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_done")))
                {
                m_EBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
        // Edit Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_description")))
                {
                setEditMode(true);
                new CEEditDescriptionWindow(m_EOApp,this,m_CNApp);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_iw")))
                {
                setEditMode(true);
                new CEEditInitialWindow(m_EOApp,this,(CENetwork)m_CNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_it")))
                {
                setEditMode(true);
                new CESetInfoAndTimingWindow(m_EOApp,this,(CENetwork)m_CNApp.getAction());
                }
            
            if(theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_externality"))){
            	setEditMode(true);
            	new CEExternalityInformation(m_EOApp,this,(CENetwork)m_CNApp.getAction());
            }
            
       // Format Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_exchanges")))
                {
                setEditMode(true);
                new CESetExchLimitsWindow(m_EOApp,this,(CENetwork)m_CNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_pay")))
                {
                setEditMode(true);
                new 	CESetPayWindow(m_EOApp,this,(CENetwork)m_CNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_periods")))
                {
                setEditMode(true);
                new CEAddPeriodWindow(m_EOApp,this,(CENetwork)m_CNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_resources")))
                {
                setEditMode(true);
                new CEEditResWindow(m_EOApp,this,(CENetwork)m_CNApp.getAction());
                }
        //-kar-
        	if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_contracts")))
                {
                setEditMode(true);
                // new CEEditContractsWindow(m_EOApp,this,(CENetwork)m_CNApp.getAction());
                }
        //-kar-        
       // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cefnaw_help")))
                {
                m_EOApp.helpWindow("ehlp_cefnaw");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/cefnaw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cefnaw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    }
