package girard.sc.ce.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeDisplay;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENetworkAction;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEPeriod;
import girard.sc.exnet.obj.Network;
import girard.sc.expt.awt.BaseActionFormatWindow;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.awt.LoadBaseActionWindow;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Allows user to add an ExNet III CENetworkAction to the action list for an
 * experiment.
 * <p>
 * <br> Started: 01-21-2003
 * <p>
 *
 * @author Dudley Girard
 */

public class CEAddNetworkActionWindow extends BaseActionFormatWindow implements ActionListener
    {
    ExptBuilderWindow m_EBWApp;
    CENetworkAction m_CENApp;
    Network m_net = null;

    // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_Help;

    Button m_chooseNetworkButton;

    TextField m_NetworkNameField;
    TextField m_AccessGroupField;

    Button m_OKButton, m_CancelButton;

    public CEAddNetworkActionWindow(ExptOverlord app1, ExptBuilderWindow app2, CENetworkAction app3)
        {
        super (app1,null,new Network());

        m_EBWApp = app2;
        m_CENApp = app3;

        initializeLabels();

  // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getMedWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ceanaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ceanaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

  //  Setup Button and Label Fields
        setTitle(m_EOApp.getLabels().getObjectLabel("ceanaw_title"));
        setBackground(m_EOApp.getWinBkgColor());
        setFont(m_EOApp.getMedWinFont());
        getContentPane().setLayout(new BorderLayout());

    // Start Setup for South Panel.
        GridBagPanel southPanel = new GridBagPanel();

        m_chooseNetworkButton = new Button(m_EOApp.getLabels().getObjectLabel("ceanaw_sn"));
        m_chooseNetworkButton.addActionListener(this);
        southPanel.constrain(m_chooseNetworkButton,1,1,6,1,GridBagConstraints.CENTER);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceanaw_network")),1,2,2,1);
        m_NetworkNameField = new TextField("None",20);
        southPanel.constrain(m_NetworkNameField,3,2,1,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceanaw_ag")),1,3,2,1);
        m_AccessGroupField = new TextField("None",20);
        m_AccessGroupField.setEditable(false);
        southPanel.constrain(m_AccessGroupField,3,3,4,1);

        m_OKButton = new Button(m_EOApp.getLabels().getObjectLabel("ceanaw_ok"));
        m_OKButton.addActionListener(this);
        southPanel.constrain(m_OKButton,1,4,2,1);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("ceanaw_cancel"));
        m_CancelButton.addActionListener(this);
        southPanel.constrain(m_CancelButton,3,4,2,1);
    // End Setup for South Panel.

        getContentPane().add("South",new BorderPanel(southPanel,BorderPanel.FRAME));
        pack();
  
        setSize(getPreferredSize());

        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (m_EditMode)
            return;
        
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_chooseNetworkButton)
                {
                m_EditMode = true;
                new LoadBaseActionWindow(m_EOApp,this,m_BApp);
                }

            if (theSource == m_OKButton)
                {
                if (m_net != null)
                    {
                    addTheAction();
                    m_EBWApp.setEditMode(false);
                    removeLabels();
                    dispose();
                    return;
                    }
                }
            if (theSource == m_CancelButton)
                {
                m_EBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            }

       if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

        // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ceanaw_help")))
                {
                m_EOApp.helpWindow("ehlp_ceanaw");
                }
            }
        }

    public void addTheAction()
        {
        CENetwork net = new CENetwork(m_net);

        CEPeriod cep = new CEPeriod(1,10,100,net.getNodeList());
        net.addPeriod(cep);

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode n = (CENode)enm.nextElement();

            CENodeResource nr = new CENodeResource(n,net);

            n.addExptData(nr);
            }

        enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge e = (CEEdge)enm.nextElement();

            CEEdgeDisplay ed = new CEEdgeDisplay(e,net);
            e.addExptData(ed);

            CEEdgeInteraction ei = new CEEdgeInteraction(e,net);
            e.addExptData(ei);
            }

        double[] pay = new double[net.getNumNodes()];
        for (int x=0;x<net.getNumNodes();x++)
            {
            pay[x] = 0;
            }
        net.setExtraData("Pay",pay);
        net.setExtraData("ProfitInfo","All");
// Can be Non-Simultaneous or Simultaneous
        net.setExtraData("TimingMethod",new String("Non-Simultaneous"));
  // The base settings for the initial window.
        Hashtable iw = new Hashtable();
        iw.put("Message","A New Experiment is About to Start.\nPress the READY button to continue.");
        iw.put("FontName","Monospaced");
        iw.put("FontType",new Integer(Font.PLAIN));
        iw.put("FontSize",new Integer(18));
        iw.put("Loc",new Point(0,0));
        iw.put("Continue","Client");
        net.setExtraData("InitialWindow",iw);

        m_CENApp.setAction(net);
        m_EBWApp.addAction(m_CENApp.getName(),m_CENApp);
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/ceanaw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/ceanaw.txt");
        }

    public void setActiveBaseAction(BaseAction ba)
        {
        int numUsers = ((Network)ba).getNodeList().size();
        if (m_EBWApp.getExpApp().sameNumUsers(numUsers))
            {
            m_net = (Network)ba;
            m_NetworkNameField.setText(m_net.getFileName());
            if (m_net.getAppName() != null)
                m_AccessGroupField.setText(m_net.getAppName());
            else
                m_AccessGroupField.setText("None");
            }
        else
            {
            new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ceanaw_nondnmnou"));
            }
        }

    
    }