package girard.sc.be.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BEEdgeDisplay;
import girard.sc.be.obj.BEEdgeResource;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeExchange;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BENodeSubnetwork;
import girard.sc.be.obj.BEPeriod;
import girard.sc.exnet.obj.Network;
import girard.sc.expt.awt.BaseActionFormatWindow;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.awt.LoadBaseActionWindow;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
import java.util.Vector;

/** 
 * Allows user to add an Exnet 3.0 BENetworkAction to the action list for an
 * experiment.  User must choose what exchange rule will be under effect.
 * <p>
 * <br> Started:  04-26-2000
 * <br> Modified: 09-16-2002
 * <br> Modified: 10-29-2002
 * <p>
 * @author: Dudley Girard
 */

public class BEAddNetworkActionWindow extends BaseActionFormatWindow implements ActionListener
    {
    ExptBuilderWindow m_EBWApp;
    BENetworkAction m_BENApp;
    Network m_net = null;

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_Help;

    CheckboxGroup m_type = new CheckboxGroup();
    Checkbox m_simpleBox, m_subNetBox, m_ordSubNetBox;

    Button m_chooseNetworkButton;

    TextField m_NetworkNameField;
    TextField m_AccessGroupField;

    Button m_OKButton, m_CancelButton;

    public BEAddNetworkActionWindow(ExptOverlord app1, ExptBuilderWindow app2, BENetworkAction app3)
        {
        super (app1, null, new Network());

        m_EBWApp = app2;
        m_BENApp = app3;

        initializeLabels();

  //  Setup Button and Label Fields
        setTitle(m_EOApp.getLabels().getObjectLabel("beanaw_title"));
        setBackground(m_EOApp.getWinBkgColor());
        setFont(m_EOApp.getMedWinFont());
        getContentPane().setLayout(new BorderLayout());

  // Setup Menubar
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

        m_mbar.setFont(m_EOApp.getSmWinFont());

  // Help Menu Options
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("beanaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("beanaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
  // End Setup for Menubar

    // Start setup for Center Panel
        GridBagPanel centerPanel = new GridBagPanel();
 
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beanaw_er")),1,1,4,1);

        m_simpleBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beanaw_basic"),m_type,true);
        centerPanel.constrain(m_simpleBox,1,2,4,1);

        m_subNetBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beanaw_subnetworks"),m_type,false);
        centerPanel.constrain(m_subNetBox,1,3,4,1);

        m_ordSubNetBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("beanaw_os"),m_type,false);
        centerPanel.constrain(m_ordSubNetBox,1,4,4,1);

   // End Setup for the Center Panel.

   // Start Setup for South Panel
        GridBagPanel southPanel = new GridBagPanel();

        m_chooseNetworkButton = new Button(m_EOApp.getLabels().getObjectLabel("beanaw_sn"));
        m_chooseNetworkButton.addActionListener(this);
        southPanel.constrain(m_chooseNetworkButton,1,1,6,1,GridBagConstraints.CENTER);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beanaw_network")),1,2,2,1);
        m_NetworkNameField = new TextField("None",20);
        m_NetworkNameField.setEditable(false);
        southPanel.constrain(m_NetworkNameField,3,2,4,1);
   
        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beanaw_ag")),1,3,2,1);
        m_AccessGroupField = new TextField("None",20);
        m_AccessGroupField.setEditable(false);
        southPanel.constrain(m_AccessGroupField,3,3,4,1);

        m_OKButton = new Button(m_EOApp.getLabels().getObjectLabel("beanaw_ok"));
        m_OKButton.addActionListener(this);
        southPanel.constrain(m_OKButton,1,4,3,1,GridBagConstraints.CENTER);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("beanaw_cancel"));
        m_CancelButton.addActionListener(this);
        southPanel.constrain(m_CancelButton,4,4,3,1,GridBagConstraints.CENTER);
   // End Setup for South Panel

        getContentPane().add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));
        getContentPane().add("South",new BorderPanel(southPanel,BorderPanel.FRAME));
        pack();
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
                    int exchangeType = 0;
                    if (m_subNetBox.getState())
                        {
                        exchangeType = 1;
                        }
                    if (m_ordSubNetBox.getState())
                        {
                        exchangeType = 2;
                        }
                    addTheAction(exchangeType);
                    m_EBWApp.setEditMode(false);
                    removeLabels();
                    dispose();
                    return;
                    }
                else
                    {
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

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("beanaw_help")))
                {
                m_EOApp.helpWindow("ehlp_beanaw"); 
                }
            }
        }

    public void addTheAction(int exchType)
        {
        BENetwork net = new BENetwork(m_net);

        BEPeriod epo = new BEPeriod(1,10,100,net.getNodeList());
        net.addPeriod(epo);

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode n = (BENode)enm.nextElement();

            if (exchType == 0)
                {
                BENodeExchange ne = new BENodeExchange(n);
                n.addExptData(ne);
                }
            if (exchType == 1)
                {
                Vector nodes = new Vector();
                Enumeration enum2 = net.getEdgeList().elements();
                while (enum2.hasMoreElements())
                    {
                    BEEdge edge = (BEEdge)enum2.nextElement();
                    if (edge.getNode1() == n.getID())
                        {
                        nodes.addElement(new Integer(edge.getNode2()));
                        }
                    if (edge.getNode2() == n.getID())
                        {
                        nodes.addElement(new Integer(edge.getNode1()));
                        }
                    }
                BENodeSubnetwork bens = new BENodeSubnetwork(n,nodes);
                n.addExptData(bens);
                }
            if (exchType == 2)
                {
                Vector nodes = new Vector();
                Enumeration enum2 = net.getEdgeList().elements();
                while (enum2.hasMoreElements())
                    {
                    BEEdge edge = (BEEdge)enum2.nextElement();
                    if (edge.getNode1() == n.getID())
                        {
                        nodes.addElement(new Integer(edge.getNode2()));
                        }
                    if (edge.getNode2() == n.getID())
                        {
                        nodes.addElement(new Integer(edge.getNode1()));
                        }
                    }
                BENodeOrSubNet beons = new BENodeOrSubNet(n,nodes);
                n.addExptData(beons);
                }
            }

        enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge e = (BEEdge)enm.nextElement();

            BEEdgeResource er = new BEEdgeResource(e);

            e.addExptData(er);

            BEEdgeDisplay ed = new BEEdgeDisplay(e,net);

            e.addExptData(ed);
            }

        double[] pay = new double[net.getNumNodes()];
        for (int x=0;x<net.getNumNodes();x++)
            {
            pay[x] = 0;
            }
        net.setExtraData("Pay",pay);

        net.setExtraData("ProfitInfo","All");
// Can be Consecutive or Simultaneous
        net.setExtraData("ExchangeMethod",new String("Consecutive"));
// The base settings for the initial window.
        Hashtable iw = new Hashtable();
        iw.put("Message","A New Experiment is About to Start.\nPress the READY button to continue.");
        iw.put("FontName","Monospaced");
        iw.put("FontType",new Integer(Font.PLAIN));
        iw.put("FontSize",new Integer(18));
        iw.put("Loc",new Point(0,0));
        iw.put("Continue","Client");
        net.setExtraData("InitialWindow",iw);

        BENetworkAction ben = (BENetworkAction)m_BENApp.clone();
        ben.setAction(net);
        m_EBWApp.addAction(ben.getName(),ben);
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/beanaw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/beanaw.txt");
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
            new ErrorDialog(m_EOApp.getLabels().getObjectLabel("beanaw_nondnmnou"));
            }
        }
    }