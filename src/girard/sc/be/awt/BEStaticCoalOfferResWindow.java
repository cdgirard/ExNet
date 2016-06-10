package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BERRThread;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;

public class BEStaticCoalOfferResWindow extends Frame
    {
    BENetworkActionClientWindow m_CWApp;
/**
 * Round Running thread, that will clean up the window at the end of the round.
 */
    BERRThread m_watcher;  
    ExptOverlord m_EOApp;

    public BEStaticCoalOfferResWindow(BENetworkActionClientWindow app)
        {
        super();
        m_CWApp = app;
        m_EOApp = m_CWApp.getEOApp();

        initializeLabels();

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("bescorw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        BENode myNode = (BENode)m_CWApp.getNetwork().getExtraData("Me");
        BENodeOrSubNet myNOS = (BENodeOrSubNet)myNode.getExptData("BENodeExchange");
        BENetwork net = m_CWApp.getNetwork();

        int members = myNOS.getCoalition().getNumJoinedCoalMembers(net);
        int offerAmt = myNOS.getCoalition().getCoalOfferAmt(net);
        int avgOffer = (int)Math.round((1.0*offerAmt)/members);

   // Create Center Panel.
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label("The consensus offer is: "+avgOffer),1,1,4,1);

   // Create Filler panels.
        Panel southPanel = new Panel(new GridLayout(1,1));
        southPanel.add(new Label("   "));
        Panel northPanel = new Panel(new GridLayout(1,1));
        northPanel.add(new Label("   "));
        Panel eastPanel = new Panel(new GridLayout(1,1));
        eastPanel.add(new Label("   "));
        Panel westPanel = new Panel(new GridLayout(1,1));
        westPanel.add(new Label("   "));

        add("North",northPanel);
        add("South",southPanel);
        add("West",westPanel);
        add("East",eastPanel);
        add("Center",centerPanel);
        pack();
        show();

        m_watcher = new BERRThread(m_CWApp,this);
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/bescorw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bescorw.txt");
        }
    }
