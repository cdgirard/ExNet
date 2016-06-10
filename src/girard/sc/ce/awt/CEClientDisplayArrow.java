package girard.sc.ce.awt;

import girard.sc.awt.BorderPanel;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.ImageCanvas;
import girard.sc.ce.io.msg.CEAcceptOfferMsg;
import girard.sc.ce.io.msg.CECompleteOfferMsg;
import girard.sc.ce.io.msg.CEOfferMsg;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeDisplay;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CEExchange;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEResource;

import java.awt.CardLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * How the subject sends and accepts offers in a CE Network Action.
 * <p>
 * <br> Started: 02-03-2003
 * <p>
 * @author Dudley Girard
 */

public class CEClientDisplayArrow extends GridBagPanel implements ActionListener,ItemListener{
    public static final int BLACK = 1;
    public static final int BLUE = 2;
    public static final String COMPLETED = "Completed Panel";
    public static final String ACTIVE = "Active Panel";
    
    CENetworkActionClientWindow m_CWApp;
    /**
     * Which node the offers are being sent to via the Arrow.
     */
    CENode         m_toNode = null; 
    /**
     * The CEEdge the arrow is emulating.
     */
    CEEdge          m_edge = null;
    /**
     * So we know whether the blue arrow image is being displayed or the
     * black arrow image.
     */ 
    int             m_ActiveArrow = BLACK;
    int             m_tmpOfferAmt = -1;
    String          m_tmpOfferItem = new String("");
    CheckboxGroup   m_offerGrp;
    Checkbox[]        m_offerBox;
    int             m_tmpDemandAmt = -1;
    String          m_tmpDemandItem = new String("");
    CheckboxGroup   m_demandGrp;
    Checkbox[]      m_demandBox; 

    CENumberTextField   m_offerField = null;
    CENumberTextField   m_demandField = null;
    Label           m_offerLabel = new Label("of - to");
    Label           m_demandLabel = new Label("of - for");
    FixedLabel      m_profitLabel = new FixedLabel(16,"a profit of  - .");
    GraphicButton   m_arrowButton = null;
    /**
     * Keeps track of the colored bubbles.
     */
    GraphicButton   m_bubbleButton = null; 
    
    CardLayout      m_OfferOptionsCard = new CardLayout();
    Panel           m_OfferOptionsPanel = new Panel(m_OfferOptionsCard);

    GridBagPanel    m_OfferDonePanel = null;
    Label           m_toNodeODPLabel = null;
    Label           m_meODPLabel = null;
    FixedLabel      m_toAmtLabel = null;
    FixedLabel      m_meAmtLabel = null;
    ImageCanvas     m_finalArrowCanvas = null;

    GridBagPanel    m_OfferPanel = null;
    Label           m_toNodeOPLabel = null;
    Label           m_meOPLabel = null;

    public CEClientDisplayArrow(CENetworkActionClientWindow cw){

        m_CWApp = cw;

        CENetwork network = m_CWApp.getNetwork();
        CENode node = (CENode)network.getNodeList().elements().nextElement();
        CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
	// Setup the offer resource type selection area.
        GridBagPanel offerPanel = new GridBagPanel();
        offerPanel.constrain(new Label("Offer"),1,1,4,1);
        m_offerGrp = new CheckboxGroup();
        m_offerBox = new Checkbox[nr.getInitialResources().size()];
        Panel tmpPanel = new Panel(new GridLayout(2,2));
        int tmpCounter = 0;
        Enumeration enm = nr.getInitialResources().elements();
        while (enm.hasMoreElements()){
	    CEResource cer = (CEResource)enm.nextElement();
	    m_offerBox[tmpCounter] = new Checkbox(cer.getLabel().substring(0,1),m_offerGrp,false);
	    m_offerBox[tmpCounter].addItemListener(this);
	    tmpPanel.add(m_offerBox[tmpCounter]);
	    tmpCounter++;
	}
        for (int x=tmpCounter;x<4;x++){
	    tmpPanel.add(new Label(" "));
	}
        offerPanel.constrain(tmpPanel,1,2,4,4);
	constrain(new BorderPanel(offerPanel,BorderPanel.FRAME),1,1,4,4);
	// Setup the obtain resource type selection area.
        GridBagPanel obtainPanel = new GridBagPanel();
        obtainPanel.constrain(new Label("Obtain"),1,1,4,1);
        m_demandGrp = new CheckboxGroup();
        m_demandBox = new Checkbox[nr.getInitialResources().size()];
        tmpPanel = new Panel(new GridLayout(2,2));
        tmpCounter = 0;
        enm = nr.getInitialResources().elements();
        while (enm.hasMoreElements()){
	    CEResource cer = (CEResource)enm.nextElement();
	    m_demandBox[tmpCounter] = new Checkbox(cer.getLabel().substring(0,1),m_demandGrp,false);
	    m_demandBox[tmpCounter].addItemListener(this);
	    tmpPanel.add(m_demandBox[tmpCounter]);
	    tmpCounter++;
	}
        for (int x=tmpCounter;x<4;x++){
	    tmpPanel.add(new Label(" "));
	}
        obtainPanel.constrain(tmpPanel,1,2,4,4);
        constrain(new BorderPanel(obtainPanel,BorderPanel.FRAME),5,1,4,4);
        m_OfferPanel = new GridBagPanel();
        m_OfferDonePanel = new GridBagPanel();
        updateArrowButton();
        updateBubbleButton();
        m_offerField = new CENumberTextField(3);
        m_offerField.addActionListener(this);
        m_offerField.setAllowFloat(false);
        m_offerField.setAllowNegative(false);
        m_demandField = new CENumberTextField(3);
        m_demandField.addActionListener(this);
        m_demandField.setAllowFloat(false);
        m_demandField.setAllowNegative(false);
        m_arrowButton.addActionListener(this);
        m_bubbleButton.addActionListener(this);
        createOfferPanel(); 
        initializeOfferDonePanel();
        m_OfferOptionsPanel.add(COMPLETED,m_OfferDonePanel);
        m_OfferOptionsPanel.add(ACTIVE,m_OfferPanel); 
        m_OfferOptionsCard.show(m_OfferOptionsPanel,ACTIVE);
        constrain(m_OfferOptionsPanel,9,1,10,5);
    }

    public void actionPerformed(ActionEvent e){
        Boolean rr = (Boolean)m_CWApp.getNetwork().getExtraData("RoundRunning");
        if ((!rr.booleanValue()) || (m_toNode == null) || (m_edge == null))
            return;
        if ((m_edge.getCompleted()) || (!m_edge.getActive()))
            return;
        String exchType = (String)m_CWApp.getNetwork().getExtraData("TimingMethod");
        if (e.getSource() instanceof GraphicButton){
	    GraphicButton theSource = (GraphicButton)e.getSource();
	    if ((theSource == m_arrowButton) && (m_ActiveArrow == BLACK)){
		int myID = m_edge.getNode1();
		if (m_demandGrp.getSelectedCheckbox() == null)
		    return;
		if (m_offerGrp.getSelectedCheckbox() == null)
		    return;
		
		if (m_toNode.getID() == m_edge.getNode1())
		    myID = m_edge.getNode2();
		CENode myNode = (CENode)m_CWApp.getNetwork().getNode(myID);
		CENodeResource myNr = (CENodeResource)myNode.getExptData("CENodeResource");
		CEResource availOfferRes = (CEResource)myNr.getAvailableResources(m_tmpOfferItem);
		CENodeResource toNr = (CENodeResource)m_toNode.getExptData("CENodeResource");
		CEResource availDemandRes = (CEResource)toNr.getAvailableResources(m_tmpDemandItem);
		CEResource offerResTo = (CEResource)toNr.getAvailableResources(m_tmpOfferItem);
		CEEdgeInteraction ceei = (CEEdgeInteraction)m_edge.getExptData("CEEdgeInteraction");
		CEExchange fromCee = ceei.getOffer(""+m_toNode.getID()+"-"+myID);
		CEExchange cee = new CEExchange();
		CEResource myResDemand = new CEResource(m_toNode.getID(),availDemandRes.getName(),m_tmpDemandItem,m_tmpDemandAmt,availDemandRes.getValue());
		CEResource myResOffer = new CEResource(myNode.getID(),availOfferRes.getName(),m_tmpOfferItem,m_tmpOfferAmt,offerResTo.getValue());
		if (m_tmpDemandAmt > availDemandRes.getResource())
		    return;
		if (m_tmpOfferAmt > availOfferRes.getResource())
		    return;
		int toState = CEEdgeDisplay.NONE;
		CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");
		if (m_edge.getNode1() == m_toNode.getID()){
		    cee.setNode1(myResOffer);
		    cee.setNode2(myResDemand);
		    toState = ceed.getExchangeState1();
		}
		else{
		    cee.setNode1(myResDemand);
		    cee.setNode2(myResOffer);
		    toState = ceed.getExchangeState2();
		}
		if (fromCee == null){
				sendOffer(myID,m_toNode.getID(),myResDemand,myResOffer);
			    }
			else if ((cee.equals(fromCee)) && (toState == CEEdgeDisplay.RED))
			    {
				sendAcceptOffer(myID,m_toNode.getID(),myResDemand,myResOffer);
			    }
			else if (!(cee.equals(fromCee)))
			    {
				sendOffer(myID,m_toNode.getID(),myResDemand,myResOffer);
			    }
			// Otherwise don't send any message since we want them to press the Green bubble.
		    }
		if (theSource == m_bubbleButton)
		    {
			int myID = m_edge.getNode1();

			if (m_toNode.getID() == m_edge.getNode1())
			    myID = m_edge.getNode2();

			CEEdgeInteraction ceei = (CEEdgeInteraction)m_edge.getExptData("CEEdgeInteraction");
			CEExchange cee = ceei.getOffer(""+m_toNode.getID()+"-"+myID);
			CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");

			if (cee == null)
			    return;

			int toState = CEEdgeDisplay.NONE;

			if (m_edge.getNode1() == m_toNode.getID())
			    {
				toState = ceed.getExchangeState1();
			    }
			else
			    {
				toState = ceed.getExchangeState2();
			    }

			if ((toState == CEEdgeDisplay.GREEN) && (exchType.equals("Non-Simultaneous")))
			    {
				if (m_edge.getNode1() == myID)
				    {
					sendCompleteOffer(myID,m_toNode.getID(),(CEResource)cee.getNode1().clone(),(CEResource)cee.getNode2().clone());
				    }
				else
				    {
					sendCompleteOffer(myID,m_toNode.getID(),(CEResource)cee.getNode2().clone(),(CEResource)cee.getNode1().clone());
				    }
			    }
			else if (toState == CEEdgeDisplay.RED)
			    {
				if (m_edge.getNode1() == myID)
				    {
					sendAcceptOffer(myID,m_toNode.getID(),(CEResource)cee.getNode1().clone(),(CEResource)cee.getNode2().clone());
				    }
				else
				    {
					sendAcceptOffer(myID,m_toNode.getID(),(CEResource)cee.getNode2().clone(),(CEResource)cee.getNode1().clone());
				    }
			    }
		    }
            }

        if (e.getSource() instanceof CENumberTextField)
            {
		CENumberTextField theSource = (CENumberTextField)e.getSource();

		if (theSource == m_demandField)
		    {
			m_tmpDemandAmt = theSource.getIntValue();

			updateProfitField();
		    }
		if (theSource == m_offerField)
		    {
			m_tmpOfferAmt = theSource.getIntValue();
			updateProfitField();
		    }
            }
    }

    public void createOfferPanel()
    {
        Label L1, L2;
        Font f1 = new Font("Monospaced",Font.BOLD,20);
        Font f2 = new Font("Monospaced",Font.PLAIN,12);

	// System.err.println("OfferPanelBasic");
        m_meOPLabel = new Label("-");
        m_meOPLabel.setFont(f1);
        m_toNodeOPLabel = new Label("-");
        m_toNodeOPLabel.setFont(f1);

        m_OfferPanel.constrain(m_meOPLabel,1,1,1,3,GridBagConstraints.CENTER);
        GridBagPanel tmpPanel = new GridBagPanel();
        tmpPanel.constrain(new Label("Offer"),1,1,5,1);
        tmpPanel.constrain(m_offerField,6,1,3,1);
        tmpPanel.constrain(m_offerLabel,9,1,9,1);
        tmpPanel.constrain(new Label("Obtain"),1,2,6,1);
        tmpPanel.constrain(m_demandField,7,2,3,1);
        tmpPanel.constrain(m_demandLabel,10,2,7,1);
        tmpPanel.constrain(m_profitLabel,1,3,16,1);

        m_OfferPanel.constrain(tmpPanel,2,1,3,3);
        m_OfferPanel.constrain(m_arrowButton,17,1,3,3,GridBagConstraints.CENTER);
        m_OfferPanel.constrain(m_toNodeOPLabel,20,1,1,3,GridBagConstraints.SOUTH);
        m_OfferPanel.constrain(m_bubbleButton,21,1,5,3);
    }

    public void displayCard(String value)
    {
        m_OfferOptionsCard.show(m_OfferOptionsPanel,value);
    }
    public void disposeOfArrowStuff()
    {

    }

    public int getActiveArrow()
    {
        return m_ActiveArrow;
    }
   
    public CEEdge getEdge()
    {
        return m_edge;
    }
    public GridBagPanel getOfferPanel()
    {
        return m_OfferPanel;
    }
    public GridBagPanel getOfferDonePanel()
    {
        return m_OfferDonePanel;
    }
    public CardLayout getOfferOptionsCard()
    {
        return m_OfferOptionsCard;
    }
    public Panel getOfferOptionsPanel()
    {
        return m_OfferOptionsPanel;
    }
    public CENode getToNode()
    {
        return m_toNode;
    }

    public void initializeOfferDonePanel()
    {
	// System.err.println("OfferPanelDone");
        Image img = m_CWApp.getEOApp().createImage(75,47);
        Graphics g = img.getGraphics();

        g.setColor(Color.white);
        g.fillRect(0,0,75,47);
        g.drawImage(m_CWApp.getImage("Final Arrow"),0,0,75,47,null);
        g.dispose();

        m_OfferDonePanel.setFont(new Font("Monospaced",Font.PLAIN,16));
        m_meODPLabel = new Label("-");
        m_meODPLabel.setFont(new Font("Monospaced",Font.BOLD,18));
        m_OfferDonePanel.constrain(m_meODPLabel,1,1,1,1,GridBagConstraints.CENTER);
        m_meAmtLabel = new FixedLabel(4,"-");
        m_OfferDonePanel.constrain(m_meAmtLabel,2,1,1,1,GridBagConstraints.CENTER);

        m_finalArrowCanvas = new ImageCanvas(img,null);
        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(m_finalArrowCanvas);
        m_OfferDonePanel.constrain(tmpPanel,3,1,8,1,GridBagConstraints.CENTER);

        m_toAmtLabel = new FixedLabel(4,"-",FixedLabel.RIGHT);
        m_OfferDonePanel.constrain(m_toAmtLabel,11,1,1,1,GridBagConstraints.CENTER);
        m_toNodeODPLabel = new Label("-");
        m_toNodeODPLabel.setFont(new Font("Monospaced",Font.BOLD,18));
        m_OfferDonePanel.constrain(m_toNodeODPLabel,12,1,1,1,GridBagConstraints.CENTER);
        m_OfferDonePanel.setFont(new Font("Monospaced",Font.PLAIN,16));
        m_finalArrowCanvas.repaint();
    }

    public void itemStateChanged(ItemEvent e)
    {
        Boolean rr = (Boolean)m_CWApp.getNetwork().getExtraData("RoundRunning");
        if ((!rr.booleanValue()) || (m_toNode == null) || (m_edge == null))
            return;

        if ((m_edge.getCompleted()) || (!m_edge.getActive()))
            return;

        if (e.getSource() instanceof Checkbox)
            {
		Checkbox theSource = (Checkbox)e.getSource();

		if (m_demandGrp.getSelectedCheckbox() == theSource)
		    {
			String cmd = theSource.getLabel();
			m_demandLabel.setText("of "+cmd+" for");
			m_tmpDemandItem = cmd;
			updateProfitField();
		    }
		if (m_offerGrp.getSelectedCheckbox() == theSource)
		    {
			String cmd = theSource.getLabel();
			m_offerLabel.setText("of "+cmd+" to");
			m_tmpOfferItem = cmd;
			updateProfitField();
		    }
            }
    }

    public void repaint()
    {
    }

    private void sendAcceptOffer(int myID, int toID, CEResource demand, CEResource offer)
    {
        Object[] out_args = new Object[4];
        out_args[0] = new Integer(myID);
        out_args[1] = new Integer(toID);
        out_args[2] = demand;
        out_args[3] = offer;
        CEAcceptOfferMsg tmp = new CEAcceptOfferMsg(out_args);
        m_CWApp.getSML().sendMessage(tmp);

        m_ActiveArrow = BLUE;
        updateArrowButton();
        m_CWApp.repaint();
    }
    private void sendCompleteOffer(int myID, int toID, CEResource demand, CEResource offer)
    {
	
        Object[] out_args = new Object[4];
        out_args[0] = new Integer(myID);
        out_args[1] = new Integer(m_toNode.getID());
        out_args[2] = demand;
        out_args[3] = offer;
        CECompleteOfferMsg tmp = new CECompleteOfferMsg(out_args);
        m_CWApp.getSML().sendMessage(tmp);

        m_ActiveArrow = BLUE;
        updateArrowButton();
        m_CWApp.repaint();
    }
    private void sendOffer(int myID, int toID, CEResource demand, CEResource offer)
    {
        Object[] out_args = new Object[4];
        out_args[0] = new Integer(myID);
        out_args[1] = new Integer(toID);
        out_args[2] = demand;
        out_args[3] = offer;
        CEOfferMsg tmp = new CEOfferMsg(out_args);
        m_CWApp.getSML().sendMessage(tmp);

        CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");

        int toState = CEEdgeDisplay.NONE;

        if (m_edge.getNode1() == m_toNode.getID())
            {
		toState = ceed.getExchangeState1();
            }
        else
            {
		toState = ceed.getExchangeState2();
            }

        if ((toState == CEEdgeDisplay.GREEN)  || (toState == CEEdgeDisplay.YELLOW))
            {
		if (m_edge.getNode1() == m_toNode.getID())
		    {
			ceed.setExchangeState(CEEdgeDisplay.RED);
			updateBubbleButton();
		    }
		else
		    {
			ceed.setExchangeState(CEEdgeDisplay.RED);
			updateBubbleButton();
		    }
            }

        m_ActiveArrow = BLUE;
        updateArrowButton();
        m_CWApp.repaint();
    }

    public void setActiveArrow(int value)
    {
        m_ActiveArrow = value;
    }
    public void setEdge(CEEdge edge)
    {
        m_edge = edge;
        if (m_edge != null)
            {
		updateResourceBoxes();

		if (m_edge.getCompleted())
		    {
			CEEdgeInteraction ceei = (CEEdgeInteraction)m_edge.getExptData("CEEdgeInteraction");
			Hashtable exch = ceei.getCompletedExchange(ceei.getExchanges());
			CEExchange cee = (CEExchange)exch.get(""+m_edge.getNode1()+"-"+m_edge.getNode2());
			if (m_toNode.getID() == m_edge.getNode1())
			    {
				m_meAmtLabel.setText(""+cee.getNode2().getIntResource()+" "+cee.getNode2().getLabel());
				m_toAmtLabel.setText(""+cee.getNode1().getIntResource()+" "+cee.getNode1().getLabel());
			    }
			else
			    {
				m_meAmtLabel.setText(""+cee.getNode1().getIntResource()+" "+cee.getNode1().getLabel());
				m_toAmtLabel.setText(""+cee.getNode2().getIntResource()+" "+cee.getNode2().getLabel());
			    }
			m_OfferOptionsCard.show(m_OfferOptionsPanel,COMPLETED);
			m_OfferDonePanel.validate();
		    }
		else
		    {
			CEEdgeInteraction ceei = (CEEdgeInteraction)m_edge.getExptData("CEEdgeInteraction");
			if (m_toNode.getID() == m_edge.getNode1())
			    {
			    }
			else
			    {
			    }
                
			m_ActiveArrow = BLACK;
			updateArrowButton();
			updateBubbleButton();
			updateProfitField();
			m_OfferPanel.validate();
			m_OfferOptionsCard.show(m_OfferOptionsPanel,ACTIVE);
		    }
            }
        else
            {
		m_ActiveArrow = BLACK;
		updateArrowButton();
		updateBubbleButton();
		updateProfitField();
		m_OfferPanel.validate();
		m_OfferOptionsCard.show(m_OfferOptionsPanel,ACTIVE);
            }
    }
    public void setMeODPLabel(String str)
    {
        m_meODPLabel.setText(str);
    }
    public void setMeOPLabel(String str)
    {
        m_meOPLabel.setText(str);
    }
    public void setToNode(CENode Ntemp)
    {
        m_toNode = Ntemp;
        if (m_toNode != null)
            {
		updateResourceBoxes();

		m_toNodeOPLabel.setText(m_toNode.getLabel().substring(0,1));
		m_toNodeODPLabel.setText(m_toNode.getLabel().substring(0,1));
            }
        else
            {
		m_toNodeOPLabel.setText("-");
		m_toNodeODPLabel.setText("-");
            }
    }

    public void updateArrowButton(){
        Image img = m_CWApp.getEOApp().createImage(60,60);
        Graphics g = img.getGraphics();

        g.setColor(Color.lightGray);
        g.fillRect(0,0,60,60);
        g.setColor(Color.black);

        if (m_ActiveArrow == BLACK){
	    g.drawImage(m_CWApp.getImage("Black Arrow"),0,0,m_CWApp.getEOApp().getWB());
	}
        else{
	    g.drawImage(m_CWApp.getImage("Blue Arrow"),0,0,m_CWApp.getEOApp().getWB());
	}
	
        g.dispose();

        if (m_arrowButton == null){
	    m_arrowButton = new GraphicButton(img.getWidth(null)+4,img.getHeight(null)+4,img);
	}
        else{
	    m_arrowButton.setImage(img);
	}
    }
    public void updateBubbleButton(){
        Image img = m_CWApp.getEOApp().createImage(185,90);
        Graphics g = img.getGraphics();

        g.setColor(Color.lightGray);
        g.fillRect(0,0,185,90);
        g.setColor(Color.black);

        g.setFont(new Font("Monospaced",Font.PLAIN,15));

        if (m_edge == null)
            {
		g.drawImage(m_CWApp.getImage("Grey Bubble"),0,0,m_CWApp.getEOApp().getWB());

		g.drawString("Offers - of - to",15,23);
		g.drawString("Obtain - of - for",15,41);
		g.drawString("a profit of -.",15,59);
            }
        else 
            {
		CEEdgeDisplay ceed = (CEEdgeDisplay)m_edge.getExptData("CEEdgeDisplay");
		int es = CEEdgeDisplay.NONE;
		if (m_toNode.getID() == m_edge.getNode1())
		    es = ceed.getExchangeState1();
		else
		    es = ceed.getExchangeState2();

		if (es == CEEdgeDisplay.NONE)
		    {
			g.drawImage(m_CWApp.getImage("Grey Bubble"),0,0,m_CWApp.getEOApp().getWB());
		    }
		else if (es == CEEdgeDisplay.RED)
		    {
			g.drawImage(m_CWApp.getImage("Red Bubble"),0,0,m_CWApp.getEOApp().getWB());
		    }
		else if (es == CEEdgeDisplay.YELLOW)
		    {
			g.drawImage(m_CWApp.getImage("Yellow Bubble"),0,0,m_CWApp.getEOApp().getWB());
		    }
		else if (es == CEEdgeDisplay.GREEN)
		    {
			g.drawImage(m_CWApp.getImage("Green Bubble"),0,0,m_CWApp.getEOApp().getWB());
		    }

		if (es != CEEdgeDisplay.NONE)
		    {
			CEEdgeInteraction ceei = (CEEdgeInteraction)m_edge.getExptData("CEEdgeInteraction");

			if (m_edge.getNode1() == m_toNode.getID())
			    {
				CEExchange cee = ceei.getOffer(""+m_edge.getNode1()+"-"+m_edge.getNode2());
				g.drawString("Offers "+cee.getNode2().getIntResource()+" of "+cee.getNode2().getLabel().substring(0,1)+" to",15,23);
				g.drawString("Obtain "+cee.getNode1().getIntResource()+" of "+cee.getNode1().getLabel().substring(0,1)+" for",15,41);
				g.drawString("a profit of "+ceei.getOfferProfit(m_edge.getNode2(),m_edge.getNode1(),m_edge.getNode2())+".",15,59);
			    }
			else
			    {
				CEExchange cee = ceei.getOffer(""+m_edge.getNode2()+"-"+m_edge.getNode1());
				g.drawString("Offers "+cee.getNode1().getIntResource()+" of "+cee.getNode1().getLabel().substring(0,1)+" to",15,23);
				g.drawString("Obtain "+cee.getNode2().getIntResource()+" of "+cee.getNode2().getLabel().substring(0,1)+" for",15,41);
				g.drawString("a profit of "+ceei.getOfferProfit(m_edge.getNode1(),m_edge.getNode2(),m_edge.getNode1())+".",15,59);
			    }
		    }
		else
		    {
			g.drawString("Offers - of - to",15,23);
			g.drawString("Obtain - of - for",15,41);
			g.drawString("a profit of -.",15,59);
		    }
            }

	g.dispose();

	if (m_bubbleButton != null)
            {
		m_bubbleButton.setImage(img);
            }
        else
            {
		m_bubbleButton = new GraphicButton(img.getWidth(null)+4,img.getHeight(null)+4,img);
            }
    }

    public void updateProfitField(){
	if(m_edge==null || m_toNode==null){
	    m_profitLabel.setText("a profit of -");
	    m_ActiveArrow = BLACK;
	    updateArrowButton();
	    m_OfferPanel.validate();
	    return;
	}
	int myID = m_edge.getNode1();
        if (m_demandGrp.getSelectedCheckbox() == null)
            return;
        if (m_offerGrp.getSelectedCheckbox() == null)
            return;
        if (m_toNode.getID() == m_edge.getNode1())
            myID = m_edge.getNode2();

        CENode myNode = (CENode)m_CWApp.getNetwork().getNode(myID);
        CENodeResource myNr = (CENodeResource)myNode.getExptData("CENodeResource");
        CEResource availOfferRes = (CEResource)myNr.getAvailableResources(m_tmpOfferItem);
        CEResource demandResFrom = (CEResource)myNr.getAvailableResources(m_tmpDemandItem);
        CENodeResource toNr = (CENodeResource)m_toNode.getExptData("CENodeResource");
        CEResource availDemandRes = (CEResource)toNr.getAvailableResources(m_tmpDemandItem);
	if(toNr==null || availDemandRes==null || myNr==null ||myNode==null||availOfferRes==null||demandResFrom==null){
	    m_profitLabel.setText("a profit of -");
	    m_ActiveArrow = BLACK;
	    updateArrowButton();
	    m_OfferPanel.validate();
	    return;
	}

        if (m_tmpDemandAmt > availDemandRes.getResource()){
	    m_profitLabel.setText("a profit of  - .");
	    m_ActiveArrow = BLACK;
	    updateArrowButton();
	    m_OfferPanel.validate();
	    return;
	}
        if (m_tmpOfferAmt > availOfferRes.getResource()){
	    m_profitLabel.setText("a profit of  - .");
	    m_ActiveArrow = BLACK;
	    updateArrowButton();
	    m_OfferPanel.validate();
	    return;
	}
        if ((m_tmpOfferAmt < 0) || (m_tmpDemandAmt < 0)){
	    m_profitLabel.setText("a profit of  - .");
	    m_ActiveArrow = BLACK;
	    updateArrowButton();
	    m_OfferPanel.validate();
	    return;
	}

        int profit = (int)(m_tmpDemandAmt*demandResFrom.getValue() - m_tmpOfferAmt*availOfferRes.getValue());
        m_profitLabel.setText("a profit of "+profit+" .");
        m_ActiveArrow = BLACK;
        updateArrowButton();
        m_OfferPanel.validate();
    }
    public void updateResourceBoxes()
    {
        if (m_toNode == null)
            {
		for (int i=0;i<m_offerBox.length;i++)
		    {
			m_offerBox[i].setForeground(Color.black);
			m_demandBox[i].setForeground(Color.black);
		    }
		validate();
		return;
            }

        CENode myNode = (CENode)m_CWApp.getNetwork().getExtraData("Me");
        
        CENodeResource myNr = (CENodeResource)myNode.getExptData("CENodeResource");
        Enumeration enm = myNr.getAvailableResources().elements();
        while (enm.hasMoreElements())
            {
		CEResource res = (CEResource)enm.nextElement();
		String resLabel = res.getLabel().substring(0,1);
		for (int i=0;i<myNr.getAvailableResources().size();i++)
		    {
			if (m_offerBox[i].getLabel().equals(resLabel))
			    {
				if (res.getResource() > 0)
				    {
					m_offerBox[i].setForeground(CEColor.edgeGreen);
					break;
				    }
				else
				    {
					m_offerBox[i].setForeground(CEColor.edgeRed);
					break;
				    }
			    }
		    }
            }

        CENodeResource toNr = (CENodeResource)m_toNode.getExptData("CENodeResource");
        enm = toNr.getAvailableResources().elements();
        while (enm.hasMoreElements())
            {
		CEResource res = (CEResource)enm.nextElement();
		String resLabel = res.getLabel().substring(0,1);
		for (int i=0;i<myNr.getAvailableResources().size();i++)
		    {
			if (m_demandBox[i].getLabel().equals(resLabel))
			    {
				if (res.getResource() > 0)
				    {
					m_demandBox[i].setForeground(CEColor.edgeGreen);
					break;
				    }
				else
				    {
					m_demandBox[i].setForeground(CEColor.edgeRed);
					break;
				    }
			    }
		    }
            }
        validate();
    }
}
