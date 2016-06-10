package girard.sc.cc.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCEdgeDisplay;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNetworkAction;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.cc.obj.CCPeriod;
import girard.sc.exnet.obj.Network;
import girard.sc.expt.awt.BaseActionFormatWindow;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.awt.LoadBaseActionWindow;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Allows user to add an ExNet III CCNetworkAction to the action list for an
 * experiment.
 * <p>
 * <br> Started: 05-29-2001
 * <br> Modified: 10-29-2002
 * <p>
 *
 * @author Dudley Girard
 */

public class CCAddNetworkActionWindow extends BaseActionFormatWindow implements ActionListener
    {
    ExptBuilderWindow m_EBWApp;
    CCNetworkAction m_CCNApp;
    Network m_net = null;

    // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_Help;

    Button m_chooseNetworkButton;

    TextField m_NetworkNameField;
    TextField m_AccessGroupField;

    Button m_OKButton, m_CancelButton;

    public CCAddNetworkActionWindow(ExptOverlord app1, ExptBuilderWindow app2, CCNetworkAction app3)
        {
        super (app1,null,new Network());

        m_EBWApp = app2;
        m_CCNApp = app3;

        initializeLabels();

  // Start Setup for Menubar
        m_mbar.setFont(m_EOApp.getMedWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

     // Help Menu
        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ccanaw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ccanaw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
   // End Setup for Menubar

  //  Setup Button and Label Fields
        setTitle(m_EOApp.getLabels().getObjectLabel("ccanaw_title"));
        setBackground(m_EOApp.getWinBkgColor());
        setFont(m_EOApp.getMedWinFont());
        getContentPane().setLayout(new BorderLayout());

    // Start Setup for South Panel.
        GridBagPanel southPanel = new GridBagPanel();

        m_chooseNetworkButton = new Button(m_EOApp.getLabels().getObjectLabel("ccanaw_sn"));
        m_chooseNetworkButton.addActionListener(this);
        southPanel.constrain(m_chooseNetworkButton,1,1,6,1,GridBagConstraints.CENTER);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccanaw_network")),1,2,2,1);
        m_NetworkNameField = new TextField("None",20);
        southPanel.constrain(m_NetworkNameField,3,2,1,1);

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccanaw_ag")),1,3,2,1);
        m_AccessGroupField = new TextField("None",20);
        m_AccessGroupField.setEditable(false);
        southPanel.constrain(m_AccessGroupField,3,3,4,1);

        m_OKButton = new Button(m_EOApp.getLabels().getObjectLabel("ccanaw_ok"));
        m_OKButton.addActionListener(this);
        southPanel.constrain(m_OKButton,1,4,2,1);

        m_CancelButton = new Button(m_EOApp.getLabels().getObjectLabel("ccanaw_cancel"));
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
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ccanaw_help")))
                {
                m_EOApp.helpWindow("ehlp_ccanw");
                }
            }
        }

    public void addTheAction()
        {
        CCNetwork net = new CCNetwork(m_net);

        CCPeriod ccp = new CCPeriod(1,10,100,net.getNodeList());
        net.setPeriod(ccp);

        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode n = (CCNode)enm.nextElement();

            CCNodeResource nr = new CCNodeResource(n,net);

            n.addExptData(nr);
            }

        enm = net.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge e = (CCEdge)enm.nextElement();

            CCEdgeDisplay ed = new CCEdgeDisplay(e,net);

            e.addExptData(ed);
            }

        double[] pay = new double[net.getNumNodes()];
        for (int x=0;x<net.getNumNodes();x++)
            {
            pay[x] = 0;
            }
        net.setExtraData("Pay",pay);

        CCNetworkAction ccn = (CCNetworkAction)m_CCNApp.clone();
        ccn.setAction(net);
        m_EBWApp.addAction(ccn.getName(),ccn);
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccanaw.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccanaw.txt");
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
            new ErrorDialog(m_EOApp.getLabels().getObjectLabel("ccanaw_nondnmnou"));
            }
        }

    
    }