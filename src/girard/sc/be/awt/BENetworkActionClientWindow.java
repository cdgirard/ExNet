package girard.sc.be.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FlashCanvas;
import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BEHelpMsg;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
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

public class BENetworkActionClientWindow extends ClientWindow
    {
    BENetwork m_network;

    FixedLabel m_timeLabel;
    FixedLabel m_messageLabel;
    
    FlashCanvas m_flashCanvas;

    BEClientDisplayCanvas m_displayArea;
    BEClientMiniDisplayCanvas m_miniDisplayArea;
    Button m_helpButton;
    Button m_zoomInButton;
    Button m_zoomOutButton;
    int m_zoomLevel = 0;  /* Min of 0 Max of 4, higher value more area shown */

    Vector m_subWindows = new Vector();
    Hashtable m_images = new Hashtable();

    CardLayout m_OfferOptionsCard = new CardLayout();
    Panel m_OfferOptionsPanel = new Panel(m_OfferOptionsCard);
    BEClientDisplayArrow m_arrow;

    int m_regListenIndex;
    

    public BENetworkActionClientWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        m_network = (BENetwork)m_ExpApp.getActiveAction();
        
        initializeLabels();
        loadImages();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("benacw_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());
        setSize(m_EOApp.getWidth(),m_EOApp.getHeight());
    
        m_displayArea = new BEClientDisplayCanvas(this,m_network);
        m_miniDisplayArea = new BEClientMiniDisplayCanvas(m_network,m_displayArea);

    // North Panel

        GridBagPanel northPanel = new GridBagPanel();

        m_timeLabel = new FixedLabel(5,"0");
        northPanel.constrain(new Label("Time Left:"),1,1,2,1);
        northPanel.constrain(m_timeLabel,3,1,2,1);

        m_helpButton = new Button(m_EOApp.getLabels().getObjectLabel("benacw_help"));
        m_helpButton.addActionListener(this);
        northPanel.constrain(m_helpButton,1,2,4,1);

        m_zoomInButton = new Button(m_EOApp.getLabels().getObjectLabel("benacw_zi"));
        m_zoomInButton.addActionListener(this);
        northPanel.constrain(m_zoomInButton,5,1,4,1);

        m_zoomOutButton = new Button(m_EOApp.getLabels().getObjectLabel("benacw_zo"));
        m_zoomOutButton.addActionListener(this);
        northPanel.constrain(m_zoomOutButton,5,2,4,1);

        m_messageLabel = new FixedLabel(45);
        northPanel.constrain(m_messageLabel,1,3,8,1);

        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(m_miniDisplayArea);
        m_miniDisplayArea.setSize(100,100);
        northPanel.constrain(new BEMiniDisplayPanel(tmpPanel,BorderPanel.FRAME),9,1,3,3,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
    // End Setup for North Panel
        

    // Start setup for center panel
        Panel centerPanel = new Panel(new GridLayout(1,1));
        centerPanel.add(m_displayArea);
    // End setup for center panel

    // Start setup for south panel
        GridBagPanel southPanel = new GridBagPanel();
              
        m_arrow = new BEClientDisplayArrow(this);

        m_OfferOptionsPanel.add(BEClientDisplayArrow.COMPLETED,m_arrow.getOfferDonePanel());
        m_OfferOptionsPanel.add(BEClientDisplayArrow.ACTIVE,m_arrow.getOfferPanel());

        m_OfferOptionsCard.show(m_OfferOptionsPanel,BEClientDisplayArrow.ACTIVE);

        initializeNetwork();

        Panel flashPanel = new Panel(new GridLayout(1, 1));
        flashPanel.add(this.m_flashCanvas = new FlashCanvas());
        this.m_flashCanvas.setSize(100, 100);
        southPanel.constrain(flashPanel, 1, 1, 1, 1);
        
        southPanel.constrain(m_OfferOptionsPanel,2,1,4,1,GridBagConstraints.CENTER);
        
        Panel flashPanel2 = new Panel(new GridLayout(1, 1));
        final FlashCanvas tmpCanvas = new FlashCanvas();
        flashPanel2.add(tmpCanvas);
        tmpCanvas.setSize(100, 100);
        southPanel.constrain(flashPanel2, 6, 1, 1, 1);
    // End setup for the south panel

        getContentPane().add("North",northPanel);
        getContentPane().add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));
        getContentPane().add("South",southPanel);

        m_miniDisplayArea.zoomAdjust(m_zoomLevel);

        setVisible(true);

        GetRegUsersMsg tmpMsg = new GetRegUsersMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000);
        }
 
    public void actionPerformed(ActionEvent e) 
        {

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_helpButton)
                {
                // Send help message.
                BEHelpMsg tmp = new BEHelpMsg(null);
                m_SML.sendMessage(tmp);
                }
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
    
    public void offerSentFlash()
    {
        this.m_flashCanvas.startFlashOne(1);
    }

    public void offerAcceptedFlash()
    {
        this.m_flashCanvas.startFlashOne(3);
    }

    public void offerRoundWindowGoodFlash()
    {
        this.m_flashCanvas.startFlashOne(5);
    }

    public void offerRoundWindowBadFlash()
    {
        this.m_flashCanvas.startFlashOne(6);
    }

    public void offerCompletedFlash()
    {
        this.m_flashCanvas.startFlashOne(4);
    }

    public void offerReceivedFlash()
    {
        this.m_flashCanvas.startFlashOne(2);
    }

    public void addSubWindow(Window w)
        {
        m_subWindows.addElement(w);
        }

    public void cleanUpWindow()
        {
  // Clear out any subwindows that may be hanging around.
        Enumeration enm = m_subWindows.elements();
        while (enm.hasMoreElements())
            {
            Frame f = (Frame)enm.nextElement();
            f.dispose();
            }

        m_subWindows.removeAllElements();  // Remove any references to the subwindows.

        removeLabels();
        }


    public BEClientDisplayArrow getArrow()
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
    public BENetwork getNetwork()
        {
        return m_network;
        }
    public Vector getSubWindows()
        {
        return m_subWindows;
        }

    
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/benacw.txt");
        }

 // Called at the very beginning and at the start of each period.
    public void initializeNetwork()
        {
        BENode me = null;
        Vector n = new Vector();

    // Label nodes based on are they me, my neighbor, or other.
        Enumeration enm = m_network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode node = (BENode)enm.nextElement();
            if (node.isMe(m_ExpApp.getUserIndex(),m_network))
                {
                me = node;
                node.setExtraData("Type","Me");
                m_arrow.setMeODPLabel(node.getLabel().substring(0,1));
                m_arrow.setMeOPLabel(node.getLabel().substring(0,1));
                m_network.setExtraData("Me",node);   // May not need this, don't seem to be using it.
                m_network.setExtraData("InfoLevel",new Integer(me.getInfoLevel()));
                }
            else if (node.isNeighbor(m_ExpApp.getUserIndex(),m_network))
                {
                node.setExtraData("Type","Neighbor");
                n.addElement(node);
                }
            else
                {
                node.setExtraData("Type","Other");
                }
            node.setExtraData("XLoc",new Integer(-1)); // Used by the display area to figure out where the node is.
            node.setExtraData("YLoc",new Integer(-1)); // Used by the display area to figure out where the node is.
            }
        
        // m_arrow.setToNode(null);
        // m_arrow.setEdge(null);

        int index = (int)(Math.random()*n.size());
        BENode newToNode = (BENode)n.elementAt(index);
        m_arrow.setToNode(newToNode);

        Enumeration enum2 = m_network.getEdgeList().elements();
        while (enum2.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enum2.nextElement();
            if ((edge.getNode1() == newToNode.getID()) && (edge.getNode2() == me.getID()))
                {
                m_arrow.setEdge(edge);
                break;
                }
            if ((edge.getNode2() == newToNode.getID()) && (edge.getNode1() == me.getID()))
                {
                m_arrow.setEdge(edge);
                break;
                }
            }

        m_OfferOptionsCard.show(m_OfferOptionsPanel,BEClientDisplayArrow.ACTIVE);
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/benacw.txt");
        }
    public void removeSubWindow(Frame f)
        {
        m_subWindows.removeElement(f);
        f.dispose();
        }

    public void repaint()
        {
        m_displayArea.repaint();
        m_miniDisplayArea.repaint();
        if (m_arrow != null)
            m_arrow.repaint();
        }

    public void setMessageLabel(String value)
        {
        m_messageLabel.setText(value);
        }
    public void setTimeLabel(int value)
        {
        m_timeLabel.setText(""+value);
        }

    private void loadImages()
        {
        Image tmp;

    // Initialize Black Arrow Image
        tmp = m_EOApp.getImage("images/girard/sc/be/awt/black_arrow.gif");
        m_images.put("Black Arrow",tmp);

    // Initialize Blue Arrow Image
        tmp = m_EOApp.getImage("images/girard/sc/be/awt/blue_arrow.gif");
        m_images.put("Blue Arrow",tmp);

    // Initialize Red Bubble Image
        tmp = m_EOApp.getImage("images/girard/sc/be/awt/red_bubble.gif");
        m_images.put("Red Bubble",tmp);

    // Initialize Yellow Bubble Image
        tmp = m_EOApp.getImage("images/girard/sc/be/awt/yellow_bubble.gif");
        m_images.put("Yellow Bubble",tmp);

    // Initialize Green Bubble Image
        tmp = m_EOApp.getImage("images/girard/sc/be/awt/green_bubble.gif");
        m_images.put("Green Bubble",tmp);

    // Initialize Money Bag Image
        tmp = m_EOApp.getImage("images/girard/sc/be/awt/money_bag.gif");
        m_images.put("Money Bag",tmp);

    // Initialize Final Arrow Image
        tmp = m_EOApp.getImage("images/girard/sc/be/awt/final_arrow.gif");
        m_images.put("Final Arrow",tmp);

    // Initialize Grey Bubble Image
        tmp = m_EOApp.getImage("images/girard/sc/be/awt/grey_bubble.gif");
        m_images.put("Grey Bubble",tmp);
        }
    }