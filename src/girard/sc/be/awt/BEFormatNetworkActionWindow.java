package girard.sc.be.awt;

import girard.sc.awt.GraphicButton;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BENodeSubnetwork;
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
import java.util.Enumeration;
import java.util.Hashtable;

public class BEFormatNetworkActionWindow extends Frame implements ActionListener
    {
    ExptOverlord m_EOApp;
    BENetworkAction m_BNApp;
    ExptBuilderWindow m_EBWApp;

    Panel m_MainPanel = new Panel(new BorderLayout());

   // North Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Edit, m_Format, m_Help;

   // Center Area
    Panel m_ExnetDrawingAreaPanel = new Panel();
    ScrollPane m_DrawingAreaSP = new ScrollPane();
    Panel m_DrawingAreaP = new Panel(new GridLayout(1,1));
    BENetworkActionCanvas m_DrawingAreaC;

    boolean m_EditMode = false;

    public BEFormatNetworkActionWindow(ExptOverlord app1, ExptBuilderWindow app2,BENetworkAction app3)
        {
        m_EOApp = app1;
        m_EBWApp = app2;
        m_BNApp = app3;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("befnaw_title"));
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());
   
        m_DrawingAreaC = new BENetworkActionCanvas(m_EOApp,(BENetwork)m_BNApp.getAction());
        m_DrawingAreaC.setBackground(m_EOApp.getDispBkgColor());
        m_DrawingAreaP.setSize(300,300);

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
    
    // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("befnaw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

    // Edit Menu Options

        m_Edit = new Menu(m_EOApp.getLabels().getObjectLabel("befnaw_edit"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_description"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_iw"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_settings"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        m_mbar.add(m_Edit);

    // Format Menu Options

        m_Format = new Menu(m_EOApp.getLabels().getObjectLabel("befnaw_format"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_periods"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_pay"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_resources"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        BENetwork net = (BENetwork)m_BNApp.getAction();
        Hashtable h = net.getNodeList();
        Enumeration enm = h.elements();
        BENode node = (BENode)enm.nextElement();

        Object nodeExch = node.getExptData("BENodeExchange");

        if (nodeExch instanceof BENodeSubnetwork)
            {
            tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_subnetworks"));
            tmpMI.addActionListener(this);
            m_Format.add(tmpMI);
            }
        else if (nodeExch instanceof BENodeOrSubNet)
            {
            tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_osn"));
            tmpMI.addActionListener(this);
            m_Format.add(tmpMI);

            tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_coalitions"));
            tmpMI.addActionListener(this);
            m_Format.add(tmpMI);
            }
        else
            {
            tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_exchanges"));
            tmpMI.addActionListener(this);
            m_Format.add(tmpMI);
            }

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_sanctions"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

        m_mbar.add(m_Format);
 
    // Format Menu Options

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("befnaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("befnaw_help"));
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

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_done")))
                {
                m_EBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
        // Edit Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_description")))
                {
                setEditMode(true);
                new BEEditDescriptionWindow(m_EOApp,this,m_BNApp);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_iw")))
                {
                setEditMode(true);
                new BEEditInitialWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_settings")))
                {
                setEditMode(true);
                new BESetMethodInfoWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }

       // Format Menu
            
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_coalitions")))
                {
                setEditMode(true);
                new BEFormatCoalitionWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_exchanges")))
                {
                setEditMode(true);
                new BENumExchWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_osn")))
                {
                setEditMode(true);
                new BESetOrdSubNetsWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_pay")))
                {
                setEditMode(true);
                new BESetPayWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_periods")))
                {
                setEditMode(true);
                new BEAddPeriodWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_resources")))
                {
                setEditMode(true);
                new BEEditRPWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_sanctions")))
                {
                setEditMode(true);
                new BESetSanctionsWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_subnetworks")))
                {
                setEditMode(true);
                new BESetSubnetworksWindow(m_EOApp,this,(BENetwork)m_BNApp.getAction());
                }

       // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("befnaw_help")))
                {
                m_EOApp.helpWindow("ehlp_befnaw");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/befnaw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/befnaw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    }
