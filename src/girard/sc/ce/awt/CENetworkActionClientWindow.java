package girard.sc.ce.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.GridBagPanel;
import girard.sc.ce.io.msg.CEHelpMsg;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CEExchange;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEResource;
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
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JScrollPane;

/**
 * Used to display information to the subjects interacting in a CE Network Action.
 * <p>
 * <br> Started: 01-31-2003
 * <br> Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 */

public class CENetworkActionClientWindow extends ClientWindow
    {
    CENetwork m_network;

    FixedLabel m_timeLabel;

    TextArea m_messageArea = new TextArea("",5,20,TextArea.SCROLLBARS_VERTICAL_ONLY);

    // TextArea m_profitArea = new TextArea("",6,15,TextArea.SCROLLBARS_VERTICAL_ONLY);
    ProfitTextPane m_profitArea = new ProfitTextPane();

 // Labels
    FixedLabel m_partnerLabel;
    FixedLabel[] m_partnerCmdLabel = new FixedLabel[4];
    FixedLabel[] m_myCmdLabel = new FixedLabel[4];

    CEClientDisplayCanvas m_displayArea;
    CEClientMiniDisplayCanvas m_miniDisplayArea;
    Button m_helpButton;

/**
 * Min of 0 Max of 4, higher value more area shown
 */
    int m_zoomLevel = 0;  

/**
 * So that we always display the commodities in the same order.
 */
    Vector m_commodities = new Vector();
    Vector m_subWindows = new Vector();
    Hashtable m_images = new Hashtable();

    CardLayout m_OfferOptionsCard = new CardLayout();
    Panel m_OfferOptionsPanel = new Panel(m_OfferOptionsCard);
    CEClientDisplayArrow m_arrow;

    int m_regListenIndex;
    
    public CENetworkActionClientWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        m_network = (CENetwork)m_ExpApp.getActiveAction();
        
        initializeLabels();
        loadImages();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("cenacw_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());
        setSize(750,650);
    
        m_displayArea = new CEClientDisplayCanvas(this,m_network);
        m_miniDisplayArea = new CEClientMiniDisplayCanvas(m_network,m_displayArea);

    // North Panel

        GridBagPanel northPanel = new GridBagPanel();

        GridBagPanel messagePanel = new GridBagPanel();
        messagePanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenacw_messages")),1,1,4,1);
        m_messageArea.setEditable(false);
        messagePanel.constrain(m_messageArea,1,2,4,4);

        // Initialize the m_commodities Vector.
        CENode node = (CENode)m_network.getNodeList().elements().nextElement();
        CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
        Enumeration enm = nr.getInitialResources().elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)enm.nextElement();
            m_commodities.addElement(cer.clone());
            }

        northPanel.constrain(new BorderPanel(messagePanel,BorderPanel.FRAME),1,1,4,5);
    // My Cmd Panel
        GridBagPanel myCmdPanel = new GridBagPanel();
        myCmdPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenacw_cav")),1,1,16,1);
        myCmdPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenacw_y")),1,2,2,1);
        m_myCmdLabel[0] = new FixedLabel(14);
        myCmdPanel.constrain(m_myCmdLabel[0],3,2,14,1);
        myCmdPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenacw_o")),1,3,2,1);
        m_myCmdLabel[1] = new FixedLabel(14);
        myCmdPanel.constrain(m_myCmdLabel[1],3,3,14,1);
        myCmdPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenacw_u")),1,4,2,1);
        m_myCmdLabel[2] = new FixedLabel(14);
        myCmdPanel.constrain(m_myCmdLabel[2],3,4,14,1);
        myCmdPanel.constrain(new Label(" |"),1,5,2,1);
        m_myCmdLabel[3] = new FixedLabel(14);
        myCmdPanel.constrain(m_myCmdLabel[3],3,5,14,1);

        myCmdPanel.setFont(new Font("Monospaced",Font.PLAIN,14));
        
        northPanel.constrain(new BorderPanel(myCmdPanel,BorderPanel.FRAME),5,1,4,5);
    // Parnter Cmd Panel
        GridBagPanel partnerCmdPanel = new GridBagPanel();
        partnerCmdPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenacw_cav")),1,1,16,1);
        partnerCmdPanel.constrain(new FixedLabel(2," |"),1,2,2,1);
        m_partnerCmdLabel[0] = new FixedLabel(14);
        partnerCmdPanel.constrain(m_partnerCmdLabel[0],3,2,14,1);
        m_partnerLabel = new FixedLabel(2,"-|");
        partnerCmdPanel.constrain(m_partnerLabel,1,3,2,1);
        m_partnerCmdLabel[1] = new FixedLabel(14);
        partnerCmdPanel.constrain(m_partnerCmdLabel[1],3,3,14,1);
        partnerCmdPanel.constrain(new FixedLabel(2," |"),1,4,2,1);
        m_partnerCmdLabel[2] = new FixedLabel(14);
        partnerCmdPanel.constrain(m_partnerCmdLabel[2],3,4,14,1);
        partnerCmdPanel.constrain(new FixedLabel(2," |"),1,5,2,1);
        m_partnerCmdLabel[3] = new FixedLabel(14);
        partnerCmdPanel.constrain(m_partnerCmdLabel[3],3,5,14,1);

        partnerCmdPanel.setFont(new Font("Monospaced",Font.PLAIN,14));

        northPanel.constrain(new BorderPanel(partnerCmdPanel,BorderPanel.FRAME),9,1,4,5);

        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(m_miniDisplayArea);
        m_miniDisplayArea.setSize(160,135);
        northPanel.constrain(new CEMiniDisplayPanel(tmpPanel,BorderPanel.FRAME),13,1,4,5,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
    // End Setup for North Panel
        
   // Start Setup for the West Panel
        
        GridBagPanel westPanel = new GridBagPanel();

        GridBagPanel thPanel = new GridBagPanel();

        thPanel.constrain(new Label(" "),1,1,4,1);

        m_timeLabel = new FixedLabel(4,"0");
        thPanel.constrain(new Label("Time Left:"),1,2,2,1);
        thPanel.constrain(m_timeLabel,3,2,2,1);

        thPanel.constrain(new Label(" "),1,3,4,1);

        m_helpButton = new Button(m_EOApp.getLabels().getObjectLabel("cenacw_help"));
        m_helpButton.addActionListener(this);
        thPanel.constrain(m_helpButton,1,4,4,1,GridBagConstraints.CENTER);

        thPanel.constrain(new Label(" "),1,5,4,1);

        westPanel.constrain(new BorderPanel(thPanel,BorderPanel.FRAME),1,1,4,5);

        GridBagPanel profitPanel = new GridBagPanel();
        profitPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cenacw_profit")),1,1,4,1,GridBagConstraints.CENTER);
        m_profitArea.setEditable(false);
        m_profitArea.initializeStyles();
        m_profitArea.setText("\n \n \n \n \n \n");
        profitPanel.constrain(new JScrollPane(m_profitArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER),1,2,4,7,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
        westPanel.constrain(new BorderPanel(profitPanel,BorderPanel.FRAME),1,6,4,8,GridBagConstraints.CENTER,GridBagConstraints.BOTH);
   // End Setup for the West Panel


    // Start setup for center panel
        Panel centerPanel = new Panel(new GridLayout(1,1));
        centerPanel.add(m_displayArea);
    // End setup for center panel

    // Start setup for south panel
        GridBagPanel southPanel = new GridBagPanel();
              
        m_arrow = new CEClientDisplayArrow(this);

        initializeNetwork();

        southPanel.constrain(new Panel(new GridLayout(1,1)),1,1,1,1); // Place holder
        southPanel.constrain(m_arrow,2,1,8,1,GridBagConstraints.CENTER);
        southPanel.constrain(new Panel(new GridLayout(1,1)),10,1,1,1); // Place holder.
    // End setup for the south panel

        getContentPane().add("North",northPanel);
        getContentPane().add("West",westPanel);
        getContentPane().add("Center",new BorderPanel(centerPanel,BorderPanel.FRAME));
        getContentPane().add("South",southPanel);

        m_miniDisplayArea.zoomAdjust(m_zoomLevel);

        show();

        GetRegUsersMsg tmpMsg = new GetRegUsersMsg(null);
        m_regListenIndex = m_SML.addListenRequest(tmpMsg,5000);
        }

	public void actionPerformed(ActionEvent e){

	    if (e.getSource() instanceof Button){
		Button theSource = (Button)e.getSource();
		if (theSource == m_helpButton){
		    // Send help message.
		    CEHelpMsg tmp = new CEHelpMsg(null);
		    m_SML.sendMessage(tmp);
		}
            }
	    if (e.getSource() instanceof ExptMessage){
		synchronized(m_SML){
		    ExptMessage em = (ExptMessage)e.getSource();
		    if (em instanceof ExptErrorMsg){
			String str = (String)em.getArgs()[0];
			new ErrorDialog(str);
                    }
		    else{
			em.getClientResponse(this);
                    }
                }
            }
        }

	public void addSubWindow(Window w){
	    m_subWindows.addElement(w);
        }

	private String buildColumnEntry(String str, int width){
	    int x, k, m;
	    StringBuffer entry = new StringBuffer("");
	    if (width > str.length()){
		m = (width - str.length())/2;
		for (k=0;k<m;k++){
		    entry.append(" ");    
                }
		entry.append(str);
		m = width - (m + str.length());
		for (k=0;k<m;k++){
		    entry.append(" ");    
                }
            }
	    else
		entry.append(str.substring(0,width));
	    return entry.toString();
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


    public CEClientDisplayArrow getArrow()
        {
        return m_arrow;
        }
    public Image getImage(String str)
        {
        return (Image)m_images.get(str);
        }
    public CENetwork getNetwork()
        {
        return m_network;
        }
    public Vector getSubWindows()
        {
        return m_subWindows;
        }
    
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/cenacw.txt");
        }

 // Called at the very beginning and at the start of each period.
    public void initializeNetwork()
        {
        CENode me = null;
        Vector n = new Vector();

    // Label nodes based on are they me, my neighbor, or other.
        Enumeration enm = m_network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode node = (CENode)enm.nextElement();
            if (node.isMe(m_ExpApp.getUserIndex(),m_network))
                {
                me = node;
                node.setExtraData("Type","Me");
                updateMyCmdPanel(me);
                m_arrow.setMeODPLabel(node.getLabel().substring(0,1));
                m_arrow.setMeOPLabel(node.getLabel().substring(0,1));
                m_network.setExtraData("Me",node);
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
  // Used by the display area to figure out where the node is.
            node.setExtraData("XLoc",new Integer(-1)); 
            node.setExtraData("YLoc",new Integer(-1));
            }
        
        // m_arrow.setToNode(null);
        // m_arrow.setEdge(null);

        int index = (int)(Math.random()*n.size());
        CENode newToNode = (CENode)n.elementAt(index);
        m_arrow.setToNode(newToNode);
        updatePartnerCmdPanel(newToNode);

        Enumeration enum2 = m_network.getEdgeList().elements();
        while (enum2.hasMoreElements())
            {
            CEEdge edge = (CEEdge)enum2.nextElement();
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

        m_arrow.displayCard(CEClientDisplayArrow.ACTIVE);
        }

    private void loadImages()
        {
        Image tmp;

    // Initialize Red Bubble Image
        tmp = m_EOApp.getImage(m_EOApp.getImgURL("girard/sc/ce/awt/red_bubble.gif"));
        m_images.put("Red Bubble",tmp);

    // Initialize Yellow Bubble Image
        tmp = m_EOApp.getImage(m_EOApp.getImgURL("girard/sc/ce/awt/yellow_bubble.gif"));
        m_images.put("Yellow Bubble",tmp);

    // Initialize Green Bubble Image
        tmp = m_EOApp.getImage(m_EOApp.getImgURL("girard/sc/ce/awt/green_bubble.gif"));
        m_images.put("Green Bubble",tmp);

    // Initialize Grey Bubble Image
        tmp = m_EOApp.getImage(m_EOApp.getImgURL("girard/sc/ce/awt/grey_bubble.gif"));
        m_images.put("Grey Bubble",tmp);

    // Initialize Black Arrow Image
        tmp = m_EOApp.getImage(m_EOApp.getImgURL("girard/sc/ce/awt/black_arrow.gif"));
        m_images.put("Black Arrow",tmp);

    // Initialize Blue Arrow Image
        tmp = m_EOApp.getImage(m_EOApp.getImgURL("girard/sc/ce/awt/blue_arrow.gif"));
        m_images.put("Blue Arrow",tmp);

    // Initialize Final Arrow Image
        tmp = m_EOApp.getImage(m_EOApp.getImgURL("girard/sc/ce/awt/final_arrow.gif"));
        m_images.put("Final Arrow",tmp);
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cenacw.txt");
        }
    public void removeSubWindow(Frame f)
        {
        m_subWindows.removeElement(f);
        f.dispose();
        }

    public void repaint()
        {
        m_displayArea.repaint();
        if (m_arrow != null)
            m_arrow.repaint();
        }

    public void setMessageLabel(String value)
        {
        m_messageArea.setText(value);
        }
    public void setTimeLabel(int value)
        {
        m_timeLabel.setText(""+value);
        }


    public void updateMyCmdPanel(CENode node)
        {
        if (node != null)
            {
            CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
            int counter = 0;
            Enumeration enm = m_commodities.elements();
            while (enm.hasMoreElements())
                {
                CEResource cer = (CEResource)enm.nextElement();
                CEResource cerN = nr.getAvailableResources(cer.getLabel());
                if (cerN != null)
                    {
                    String amt = buildColumnEntry(""+cerN.getIntResource(),4);
                    String cmd = buildColumnEntry(cerN.getLabel().substring(0,1),5);
                    String val = buildColumnEntry(""+cerN.getIntValue(),5);
                    m_myCmdLabel[counter].setText(cmd+amt+val);
                    }
                else
                    {
                    String amt = buildColumnEntry("-",4);
                    String cmd = buildColumnEntry(cer.getLabel().substring(0,1),5);
                    String val = buildColumnEntry("-",5);
                    m_myCmdLabel[counter].setText(cmd+amt+val);
                    }
                counter++;
                }
            }
        else
            {
            int counter = 0;
            Enumeration enm = m_commodities.elements();
            while (enm.hasMoreElements())
                {
                CEResource cer = (CEResource)enm.nextElement();

                String amt = buildColumnEntry("-",4);
                String cmd = buildColumnEntry(cer.getLabel().substring(0,1),5);
                String val = buildColumnEntry("-",5);
                m_myCmdLabel[counter].setText(cmd+amt+val);
                counter++;
                }
            }
        validate();
        }
    public void updatePartnerCmdPanel(CENode node)
        {
        String pi = (String)m_network.getExtraData("ProfitInfo");

        if (node != null)
            {
            m_partnerLabel.setText(""+node.getLabel().substring(0,1)+"|");
            CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
            int counter = 0;
            Enumeration enm = m_commodities.elements();
            while (enm.hasMoreElements())
                {
                CEResource cer = (CEResource)enm.nextElement();
                CEResource cerN = nr.getAvailableResources(cer.getLabel());
                if (cerN != null)
                    {
                    String amt = buildColumnEntry(""+cerN.getIntResource(),4);
                    String cmd = buildColumnEntry(cerN.getLabel().substring(0,1),5);
                    String val = new String("     ");
                    if (pi.equals("All"))
                        {
                        val = buildColumnEntry(""+cerN.getIntValue(),5);
                        }
                    else
                        {
                        val = buildColumnEntry("-",5);
                        }
                    
                    m_partnerCmdLabel[counter].setText(cmd+amt+val);
                    }
                else
                    {
                    String amt = buildColumnEntry("-",4);
                    String cmd = buildColumnEntry(cer.getLabel().substring(0,1),5);
                    String val = buildColumnEntry("-",5);
                    m_partnerCmdLabel[counter].setText(cmd+amt+val);
                    }
                counter++;
                }
            }
        else
            {
            m_partnerLabel.setText("-|");
            int counter = 0;
            Enumeration enm = m_commodities.elements();
            while (enm.hasMoreElements())
                {
                CEResource cer = (CEResource)enm.nextElement();
                String amt = buildColumnEntry("-",4);
                String cmd = buildColumnEntry(cer.getLabel().substring(0,1),5);
                String val = buildColumnEntry("-",5);
                m_partnerCmdLabel[counter].setText(cmd+amt+val);
                counter++;
                }
            }
        validate();
        }
    public void updateProfitDisplay(CEEdge edge)
        {
        String pi = (String)m_network.getExtraData("ProfitInfo");
        CENode n1 = (CENode)m_network.getNode(edge.getNode1());
        CENodeResource nr1 = (CENodeResource)n1.getExptData("CENodeResource");
        CENode n2 = (CENode)m_network.getNode(edge.getNode2());
        CENodeResource nr2 = (CENodeResource)n2.getExptData("CENodeResource");
        m_profitArea.clearText();
        m_profitArea.addString(n1.getLabel()+" => "+n2.getLabel()+"\n");
        int profN1 = 0;
        int profN2 = 0;
        if (edge.getActive())
            {
            CEEdgeInteraction ei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");
            
            profN1 = ei.getOfferProfit(n1.getID(),n1.getID(),n2.getID());
            profN2 = ei.getOfferProfit(n2.getID(),n1.getID(),n2.getID());
            }
        else if (edge.getCompleted())
            {
            CEEdgeInteraction ei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            Hashtable h = ei.getCompletedExchange(ei.getExchanges());
            if (h != null)
                {
                CEExchange cee1 = (CEExchange)h.get(""+n1.getID()+"-"+n2.getID());
		if(cee1==null)
		    return;
                profN1 = (int)(cee1.getNode1().getProfit() - cee1.getNode2().getProfit());
                CEExchange cee2 = ei.getOffer(n2.getLabel()+"-"+n1.getLabel());
		if(cee2==null) return;
                profN2 = (int)(cee2.getNode2().getProfit() - cee2.getNode1().getProfit());
                }
            }
	if(n1==null)
	    return;
	if(n2==null)
	    return;
        if (pi.equals("All"))
            {
            m_profitArea.addProfit(n1.getLabel(),profN1);
            m_profitArea.addProfit(n2.getLabel(),profN2);
            }
        else
            {
            if (((String)n1.getExtraData("Type")).equals("Me"))
                {
                m_profitArea.addProfit(n1.getLabel(),profN1);
                m_profitArea.addString(n2.getLabel()+": -\n");
                }
            else if (((String)n2.getExtraData("Type")).equals("Me"))
                {
                m_profitArea.addString(n1.getLabel()+": -\n");
                m_profitArea.addProfit(n2.getLabel(),profN2);
                }
            else
                {
                m_profitArea.addString(n1.getLabel()+": -\n");
                m_profitArea.addString(n2.getLabel()+": -\n");
                }
            }

        m_profitArea.addString(n2.getLabel()+" -> "+n1.getLabel()+"\n");
        profN1 = 0;
        profN2 = 0;
        if (edge.getActive())
            {
            CEEdgeInteraction ei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");
            profN1 = ei.getOfferProfit(n1.getID(),n2.getID(),n1.getID());
            profN2 = ei.getOfferProfit(n2.getID(),n2.getID(),n1.getID());
            }
        else if (edge.getCompleted())
            {
            CEEdgeInteraction ei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");
            Hashtable h = ei.getCompletedExchange(ei.getExchanges());
            if (h != null)
                {
                CEExchange cee1 = (CEExchange)h.get(""+n1.getID()+"-"+n2.getID());
                profN1 = (int)(cee1.getNode1().getProfit() - cee1.getNode2().getProfit());
                CEExchange cee2 = ei.getOffer(n2.getID()+"-"+n1.getID());
                profN2 = (int)(cee2.getNode2().getProfit() - cee2.getNode1().getProfit());
                }
            }

        if (pi.equals("All"))
            {
            m_profitArea.addProfit(n1.getLabel(),profN1);
            m_profitArea.addProfit(n2.getLabel(),profN2);
            }
        else
            {
            if (((String)n1.getExtraData("Type")).equals("Me"))
                {
                m_profitArea.addProfit(n1.getLabel(),profN1);
                m_profitArea.addString(n2.getLabel()+": -\n");
                }
            else if (((String)n2.getExtraData("Type")).equals("Me"))
                {
                m_profitArea.addString(n1.getLabel()+": -\n");
                m_profitArea.addProfit(n2.getLabel(),profN2);
                }
            else
                {
                m_profitArea.addString(n1.getLabel()+": -\n");
                m_profitArea.addString(n2.getLabel()+": -\n");
                }
            }
        m_profitArea.validate();
        }
    }
