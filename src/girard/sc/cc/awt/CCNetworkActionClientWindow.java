package girard.sc.cc.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.GridBagPanel;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetRegUsersMsg;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CCNetworkActionClientWindow extends ClientWindow
    {
    CCNetwork m_network;

    FixedLabel m_timeLabel;
    FixedLabel m_bankLabel;
    FixedLabel m_pointsLabel;
    FixedLabel m_messageLabel;

    CCClientDisplayCanvas m_displayArea;
    CCClientMiniDisplayCanvas m_miniDisplayArea;
    Button m_zoomInButton;
    Button m_zoomOutButton;
    int m_zoomLevel = 0;  /* Min of 0 Max of 4, higher value more area shown */

    Vector m_subWindows = new Vector();
    Hashtable m_images = new Hashtable();

    CardLayout m_OfferOptionsCard = new CardLayout();
    Panel m_OfferOptionsPanel = new Panel(m_OfferOptionsCard);
    CCClientDisplayArrow m_arrow;

    int m_regListenIndex;
    

    public CCNetworkActionClientWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        m_network = (CCNetwork)m_ExpApp.getActiveAction();
        
        initializeLabels();
        loadImages();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ccnacw_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
    
        m_displayArea = new CCClientDisplayCanvas(this,m_network);
        m_miniDisplayArea = new CCClientMiniDisplayCanvas(m_network,m_displayArea);

    // North Panel

        GridBagPanel northPanel = new GridBagPanel();

        m_timeLabel = new FixedLabel(5,"0");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnacw_tl")),1,1,2,1);
        northPanel.constrain(m_timeLabel,3,1,2,1);

        m_bankLabel = new FixedLabel(5,"0");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnacw_bank")),1,2,2,1);
        northPanel.constrain(m_bankLabel,3,2,2,1);

        m_pointsLabel = new FixedLabel(5,"0");
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnacw_points")),1,3,2,1);
        northPanel.constrain(m_pointsLabel,3,3,2,1);

        m_zoomInButton = new Button(m_EOApp.getLabels().getObjectLabel("ccnacw_zi"));
        m_zoomInButton.addActionListener(this);
        northPanel.constrain(m_zoomInButton,5,1,4,1);

        m_zoomOutButton = new Button(m_EOApp.getLabels().getObjectLabel("ccnacw_zo"));
        m_zoomOutButton.addActionListener(this);
        northPanel.constrain(m_zoomOutButton,5,2,4,1);

        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(m_miniDisplayArea);
        northPanel.constrain(new CCMiniDisplayPanel(tmpPanel,BorderPanel.FRAME),9,1,4,4,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

        m_messageLabel = new FixedLabel(30);
        northPanel.constrain(m_messageLabel,1,4,8,1);

    // Start setup for center panel

        Panel centerPanel = new Panel(new GridLayout(1,1));
        centerPanel.add(m_displayArea);

    // End setup for center panel

    // Start setup for south panel
        GridBagPanel southPanel = new GridBagPanel();
              
        m_arrow = new CCClientDisplayArrow(this);
 
        m_OfferOptionsPanel.add(CCClientDisplayArrow.ACTIVE,m_arrow.getOfferPanel());
        m_OfferOptionsPanel.add(CCClientDisplayArrow.COMPLETED,m_arrow.getOfferDonePanel());
        
        m_OfferOptionsCard.show(m_OfferOptionsPanel,CCClientDisplayArrow.ACTIVE);

        initializeNetwork();

        southPanel.constrain(new Panel(new GridLayout(1,1)),1,1,1,1); // Place holder
        southPanel.constrain(m_OfferOptionsPanel,2,1,4,1,GridBagConstraints.CENTER);
        southPanel.constrain(new Panel(new GridLayout(1,1)),6,1,1,1); // Place holder.
    // End setup for the south panel

        getContentPane().add("North",northPanel);
        getContentPane().add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));
        getContentPane().add("South",southPanel);

        show();

        GetRegUsersMsg tmpMsg = new GetRegUsersMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000);
        }
 
    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_zoomInButton)
                {
                if (m_zoomLevel != 0)
                    m_zoomLevel--;
                m_miniDisplayArea.zoomAdjust(m_zoomLevel);
                }
            if (theSource == m_zoomOutButton)
                {
                if (m_zoomLevel != 4)
                    m_zoomLevel++;
                m_miniDisplayArea.zoomAdjust(m_zoomLevel);
                }
            }

        if (e.getSource() instanceof ExptMessage)
            {
            synchronized(m_SML)
                {
                ExptMessage em = (ExptMessage)e.getSource();

                if (em instanceof ExptErrorMsg)
                    {
                    String str = (String)em.getArgs()[0];
                    new ErrorDialog(str);
                    }
                else
                    {
                    em.getClientResponse(this);
                    }
                }
            }
        }

    public void addSubWindow(Window w)
        {
        m_subWindows.addElement(w);
        }

    public void cleanUpWindow()
        {
        Enumeration enm = m_subWindows.elements();
        while (enm.hasMoreElements())
            {
            Frame f = (Frame)enm.nextElement();
            f.dispose();
            }

        m_subWindows.removeAllElements();
        removeLabels();
        }

    public CCClientDisplayArrow getArrow()
        {
        return m_arrow;
        }
    public Image getImage(String str)
        {
        return (Image)m_images.get(str);
        }
    public CardLayout getOfferOptionsCard()
        {
        return m_OfferOptionsCard;
        }
    public Panel getOfferOptionsPanel()
        {
        return m_OfferOptionsPanel;
        }
    public CCNetwork getNetwork()
        {
        return m_network;
        }

    
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccnacw.txt");
        }

 // Called at the very beginning and at the start of each period.
    public void initializeNetwork()
        {
    // Label nodes based on are they me, my neighbor, or other.
        Enumeration enm = m_network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            if (node.isMe(m_ExpApp.getUserIndex(),m_network))
                {
                node.setExtraData("Type","Me");
                m_arrow.setMyNode(node);
                m_network.setExtraData("Me",node);   // May not need this, don't seem to be using it.
                CCNodeResource nr = (CCNodeResource)node.getExptData("CCNodeResource");
                setBankLabel(nr.getActiveBank());

                Double pep = (Double)m_network.getExtraData("PntEarnedPeriod");
                Double pen = (Double)m_network.getExtraData("PntEarnedNetwork");

                m_network.setExtraData("PntEarnedPeriod",new Double(nr.getActiveBank() + pep.doubleValue()));
                m_network.setExtraData("PntEarnedNetwork",new Double(nr.getActiveBank() + pen.doubleValue()));

                }
            else if (node.isNeighbor(m_ExpApp.getUserIndex(),m_network))
                {
                node.setExtraData("Type","Neighbor");
                }
            else
                {
                node.setExtraData("Type","Other");
                }
            node.setExtraData("XLoc",new Integer(-1)); // Used by the display area to figure out where the node is.
            node.setExtraData("YLoc",new Integer(-1)); // Used by the display area to figure out where the node is.
            }
        m_arrow.setToNode(null);

        enm = m_network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            node.initializeStart();
            }

        enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            edge.initializeStart();
            }

        m_OfferOptionsCard.show(m_OfferOptionsPanel,CCClientDisplayArrow.ACTIVE);
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccnacw.txt");
        }
    public void removeSubWindow(Window w)
        {
        m_subWindows.removeElement(w);
        w.dispose();
        }

    public void repaint()
        {
        m_displayArea.repaint();
        if (m_arrow != null)
            m_arrow.repaint();
        }

    public void setBankLabel(int value)
        {
        m_bankLabel.setText(""+value);
        }
    public void setMessageLabel(String str)
        {
        m_messageLabel.setText(str);
        }
    public void setPointsLabel(int value)
        {
        m_pointsLabel.setText(""+value);
        }
    public void setTimeLabel(int value)
        {
        m_timeLabel.setText(""+value);
        }

    private void loadImages()
        {
        Image tmp;

    // Initialize Black Arrow Image
        tmp = m_EOApp.getImage("images/girard/sc/cc/awt/black_arrow.gif");
        m_images.put("Black Arrow",tmp);

    // Initialize Blue Arrow Image
        tmp = m_EOApp.getImage("images/girard/sc/cc/awt/blue_arrow.gif");
        m_images.put("Blue Arrow",tmp);

    // Initialize Red Bubble Image
        tmp = m_EOApp.getImage("images/girard/sc/cc/awt/red_bubble.gif");
        m_images.put("Red Bubble",tmp);

    // Initialize Yellow Bubble Image
        tmp = m_EOApp.getImage("images/girard/sc/cc/awt/yellow_bubble.gif");
        m_images.put("Yellow Bubble",tmp);

    // Initialize Green Bubble Image
        tmp = m_EOApp.getImage("images/girard/sc/cc/awt/green_bubble.gif");
        m_images.put("Green Bubble",tmp);

    // Initialize Money Bag Image
        tmp = m_EOApp.getImage("images/girard/sc/cc/awt/money_bag.gif");
        m_images.put("Money Bag",tmp);

    // Initialize Final Arrow Image
        tmp = m_EOApp.getImage("images/girard/sc/cc/awt/final_arrow.gif");
        m_images.put("Final Arrow",tmp);

    // Initialize Grey Bubble Image
        tmp = m_EOApp.getImage("images/girard/sc/cc/awt/grey_bubble.gif");
        m_images.put("Grey Bubble",tmp);
        }
    }