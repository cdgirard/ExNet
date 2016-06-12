package girard.sc.exnet.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.exnet.obj.Network;
import girard.sc.exnet.obj.NetworkBuilder;
import girard.sc.expt.awt.ActionBuilderWindow;
import girard.sc.expt.awt.BaseActionFormatWindow;
import girard.sc.expt.awt.DeleteBaseActionWindow;
import girard.sc.expt.awt.LoadBaseActionWindow;
import girard.sc.expt.awt.SaveBaseActionWindow;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NetworkBuilderWindow extends BaseActionFormatWindow implements ActionListener
    {
    NetworkBuilder m_NBApp;
    
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    Panel m_ToolPanel = new Panel(new BorderLayout());
    GraphicButton m_AddNodeButton, m_DeleteNodeButton, m_AddEdgeButton, m_DeleteEdgeButton;
    int m_tbw = 66;
    int m_tbh = 66;

    Panel m_ExnetDrawingAreaPanel = new Panel();
    ScrollPane m_DrawingAreaSP = new ScrollPane();
    Panel m_DrawingAreaP = new Panel(new GridLayout(1,1));
    NetworkCanvas m_DrawingAreaC;

    public NetworkBuilderWindow(ExptOverlord app1, ActionBuilderWindow app2, Network app3)
        {
        super(app1,app2,app3);

        m_NBApp = new NetworkBuilder(m_EOApp,this);
        m_NBApp.setActiveNetwork(app3);

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("nbw_title")+" - NONE");
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
    
        m_DrawingAreaC = new NetworkCanvas(NetworkBuilder.WIDTH,NetworkBuilder.HEIGHT,m_NBApp);
        m_DrawingAreaP.setSize(300,300);

    // Setup Menubar
        MenuItem tmpMI;

        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("nbw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("nbw_new"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("nbw_open"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("nbw_save"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("nbw_delete"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("nbw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("nbw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("nbw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End setup for Menubar

    // Tool Panel
      
        GridBagPanel tmpP2 = new GridBagPanel();

        Label tmp = new Label(" Tools");
        tmp.setFont(m_EOApp.getLgButtonFont());
        tmpP2.constrain(tmp,1,1,2,1,GridBagConstraints.CENTER);

        m_AddNodeButton = new GraphicButton(m_tbw,m_tbh,null);
        m_AddNodeButton.addActionListener(this);
        tmpP2.constrain(m_AddNodeButton,1,2,2,2,GridBagConstraints.CENTER);

        m_AddEdgeButton = new GraphicButton(m_tbw,m_tbh,null);
        m_AddEdgeButton.addActionListener(this);
        tmpP2.constrain(m_AddEdgeButton,1,4,2,2,GridBagConstraints.CENTER);

        m_DeleteNodeButton = new GraphicButton(m_tbw,m_tbh,null);
        m_DeleteNodeButton.addActionListener(this);
        tmpP2.constrain(m_DeleteNodeButton,1,6,2,2,GridBagConstraints.CENTER);

        m_DeleteEdgeButton = new GraphicButton(m_tbw,m_tbh,null);
        m_DeleteEdgeButton.addActionListener(this);
        tmpP2.constrain(m_DeleteEdgeButton,1,8,2,2,GridBagConstraints.CENTER);

        m_ToolPanel.add("North",tmpP2);
        m_ToolPanel.add("Center",new Panel(new GridLayout(1,1)));

    // Exnet Drawing Area
        m_ExnetDrawingAreaPanel.setLayout(new GridLayout(1,1));
        m_DrawingAreaP.add(m_DrawingAreaC);
        m_DrawingAreaSP.add(m_DrawingAreaP);
        m_ExnetDrawingAreaPanel.add(m_DrawingAreaSP);

        loadImages();

        getContentPane().add("West",m_ToolPanel);
        getContentPane().add("Center",m_ExnetDrawingAreaPanel);
        
        m_DrawingAreaC.repaint();

        show();
        }
 
    public void actionPerformed(ActionEvent e) 
        {
        if (m_EditMode)
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    
        // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("nbw_new")))
                {
                // Handle New
                m_NBApp.reset();
                m_BApp = m_NBApp.getActiveNetwork();
                m_DrawingAreaC.repaint();
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("nbw_open")))
                {
                // Handle Open
                setEditMode(true);
                new LoadBaseActionWindow(m_EOApp,this,new Network());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("nbw_save")))
                {
                // Handle Save
                if (m_NBApp.validNetwork())
                    {
                    setEditMode(true);
                    m_BApp.setDesc(""+m_NBApp.getNumNodes());
                    new SaveBaseActionWindow(m_EOApp,this,m_BApp);
                    }
                 else
                    {
                    String[] str = new String[2];
                    str[0] = "Not all nodes have a connection.";
                    str[1] = "Need at least two nodes.";
                    new ErrorDialog(str);
                    }
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("nbw_delete")))
                {
                setEditMode(true);
                new DeleteBaseActionWindow(m_EOApp,this,m_BApp);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("nbw_exit")))
                {
                // Handle Exit
                m_ABWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
        // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("nbw_help")))
                {
                // Handle Help Request
                m_EOApp.helpWindow("ehlp_nbw");
                }
            }

        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            if (theSource == m_AddNodeButton)
                {
                // Handle Add Node
                m_DrawingAreaC.setActiveTool(1);
                }
            if (theSource == m_AddEdgeButton)
                {
                // Handle Add Node
                m_DrawingAreaC.setActiveTool(2);
                }
            if (theSource == m_DeleteNodeButton)
                {
                // Handle Add Node
                m_DrawingAreaC.setActiveTool(3);
                }
            if (theSource == m_DeleteEdgeButton)
                {
                // Handle Add Node
                m_DrawingAreaC.setActiveTool(4);
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/exnet/awt/nbw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/exnet/awt/nbw.txt");
        }

    public void resetDrawingArea()
        {
        m_DrawingAreaC.repaint();
        }

    public void setActiveBaseAction(BaseAction ba)
        {
        m_NBApp.setActiveNetwork((Network)ba);
        m_BApp = ba;
        resetDrawingArea();
        setTitle(m_EOApp.getLabels().getObjectLabel("nbw_title")+" - "+ba.getFileName());
        }
    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        setTitle(m_EOApp.getLabels().getObjectLabel("nbw_title")+" : "+m_BApp.getFileName());
        validate();
        }
    private void loadImages()
        {
        int x, y;
        Graphics g;
        Image tmp, tmp2;

    // Initialize Delete Node Image
        tmp = m_EOApp.getImage("images/girard/sc/exnet/awt/delete_node.gif");

        m_DeleteNodeButton.setImage(tmp);

    // Initialize Add Node Image
        tmp = m_EOApp.getImage("images/girard/sc/exnet/awt/add_node.gif");

        m_AddNodeButton.setImage(tmp);

    // Initialize Add Edge Image
        tmp = m_EOApp.getImage("images/girard/sc/exnet/awt/add_edge.gif");

        m_AddEdgeButton.setImage(tmp);

    // Initialize Delete Edge Image
        tmp = m_EOApp.getImage("images/girard/sc/exnet/awt/delete_edge.gif");

        m_DeleteEdgeButton.setImage(tmp);
        }
    }
