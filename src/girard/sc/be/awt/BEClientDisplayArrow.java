package girard.sc.be.awt;

import girard.sc.awt.FixedLabel;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.ImageCanvas;
import girard.sc.be.io.msg.BEAcceptOfferMsg;
import girard.sc.be.io.msg.BECompleteOfferMsg;
import girard.sc.be.io.msg.BEOfferMsg;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BEEdgeResource;
import girard.sc.be.obj.BEExchange;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BEResource;

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
import java.util.Hashtable;

public class BEClientDisplayArrow implements ActionListener
    {
    public static final int BLACK = 1;
    public static final int BLUE = 2;
    public static final String COMPLETED = "Completed Panel";
    public static final String ACTIVE = "Active Panel";

    BENetworkActionClientWindow m_CWApp;
    BENode        m_toNode = null; /* Which node the offers are being sent to via the Arrow */
    BEEdge        m_edge = null; /* edge the arrow is emulating. */
    int           m_ActiveArrow = BLACK; /* We should still need this */
    int           m_tmpKeep = -1;
    int           m_tmpGive = -1;
    Hashtable     m_nodeButtons = new Hashtable();  /* For the node the arrow set allows offers to be sent to, key is the name of the node */

    GraphicButton   m_bagButton = null;  /* Bag Button */
    GraphicButton   m_otherButton = null; /* The Other button */
    GraphicButton   m_arrowButton = null;
    GraphicButton   m_bubbleButton = null; /* Keeps track of the colored bubbles */
    
    GridBagPanel    m_OfferDonePanel = null;
    Label           m_toNodeODPLabel = null;
    Label           m_meODPLabel = null;
    FixedLabel      m_toAmtLabel = null;
    FixedLabel      m_meAmtLabel = null;
    ImageCanvas     m_finalArrowCanvas = null;

    GridBagPanel    m_OfferPanel = null;
    Label           m_toNodeOPLabel = null;
    Label           m_meOPLabel = null;

    public BEClientDisplayArrow(BENetworkActionClientWindow cw)
        {
        m_CWApp = cw;

        m_OfferPanel = new GridBagPanel();
        m_OfferDonePanel = new GridBagPanel();

        updateArrowButton();
        updateBubbleButton();
        updateBagButton();
        updateOtherButton();

        m_arrowButton.addActionListener(this);
        m_bagButton.addActionListener(this);
        m_bubbleButton.addActionListener(this);
        m_otherButton.addActionListener(this);
            

        createOfferPanel(); 
        initializeOfferDonePanel();
        }

    public void actionPerformed(ActionEvent e)
        {
        int infoLevel = ((Integer)this.m_CWApp.getNetwork().getExtraData("InfoLevel")).intValue();
        Boolean rr = (Boolean)m_CWApp.getNetwork().getExtraData("RoundRunning");
        if ((!rr.booleanValue()) || (m_toNode == null) || (m_edge == null))
            return;

        if ((m_edge.getCompleted()) || (!m_edge.getActive()))
            return;

        String exchType = (String)m_CWApp.getNetwork().getExtraData("ExchangeMethod");

        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            if ((theSource == m_arrowButton) && (m_ActiveArrow == BLACK))
                {
                BEEdgeResource beer = (BEEdgeResource)m_edge.getExptData("BEEdgeResource");
                BEExchange bee = beer.getExchange();

                int myID = -1;

                BEResource myResKeep = null;
                BEResource myResGive = null;

                BEResource theirResKeep = null;
                BEResource theirResGive = null;

                if (m_toNode.getID() == m_edge.getNode1())
                    {
                    myID = m_edge.getNode2();
                    myResKeep = beer.getN2Keep();
                    myResGive = beer.getN2Give();
                    theirResKeep = beer.getN1Keep();
                    theirResGive = beer.getN1Give();
                    }
                else
                    {
                    myID = m_edge.getNode1();
                    myResKeep = beer.getN1Keep();
                    myResGive = beer.getN1Give();
                    theirResKeep = beer.getN2Keep();
                    theirResGive = beer.getN2Give();
                    }

                myResKeep.setResource(m_tmpKeep);
                myResGive.setResource(m_tmpGive);

                if ((myResKeep.getIntResource() == theirResGive.getIntResource()) && (beer.getExchangeState() == BEEdgeResource.RED))
                    {
                    Object[] out_args = new Object[4];  
                    out_args[0] = new Integer(myID);
                    out_args[1] = new Integer(m_toNode.getID());
                    out_args[2] = new Integer(m_tmpKeep);
                    out_args[3] = new Integer(m_tmpGive);
                    
                    if (infoLevel == 11) 
                    {
                        this.m_CWApp.offerAcceptedFlash();
                    }
                    
                    BEAcceptOfferMsg tmp = new BEAcceptOfferMsg(out_args);
                    m_CWApp.getSML().sendMessage(tmp);

                    beer.setExchangeState(BEEdgeResource.YELLOW);
                    m_ActiveArrow = BLUE;
                    updateBubbleButton();
                    updateArrowButton();
                    m_CWApp.repaint();
                    }
                else if (myResKeep.getIntResource() != theirResGive.getIntResource())
                    {
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(myID);
                    out_args[1] = new Integer(m_toNode.getID());
                    out_args[2] = new Integer(m_tmpKeep);
                    out_args[3] = new Integer(m_tmpGive);

                    if (infoLevel == 11) 
                    {
                        this.m_CWApp.offerSentFlash();
                    }
                    
                    BEOfferMsg tmp = new BEOfferMsg(out_args);
                    m_CWApp.getSML().sendMessage(tmp);

                    if ((beer.getExchangeState() == BEEdgeResource.GREEN)  || (beer.getExchangeState() == BEEdgeResource.YELLOW))
                        {
                        beer.setExchangeState(BEEdgeResource.RED);
                        updateBubbleButton();
                        }

                    m_ActiveArrow = BLUE;
                    updateArrowButton();
                    m_CWApp.repaint();
                    }
       // Otherwise don't send any message since we want them to press the Green bubble.
                }
            if (theSource == m_bagButton)
                {
                if (m_tmpGive > 1)
                    {
                    m_tmpGive--;
                    m_tmpKeep++;
                    updateBagButton();
                    updateOtherButton();

                    if (m_ActiveArrow == BLUE)
                        {
                        m_ActiveArrow = BLACK;
                        updateArrowButton();
                        }
                    }
                }
            if (theSource == m_bubbleButton)
                {
                BEEdgeResource beer = (BEEdgeResource)m_edge.getExptData("BEEdgeResource");
                BEExchange bee = beer.getExchange();

                int myID = -1;

                BEResource myResKeep = null;
                BEResource myResGive = null;

                BEResource theirResKeep = null;
                BEResource theirResGive = null;

                if (m_toNode.getID() == m_edge.getNode1())
                    {
                    myID = m_edge.getNode2();
                    myResKeep = beer.getN2Keep();
                    myResGive = beer.getN2Give();
                    theirResKeep = beer.getN1Keep();
                    theirResGive = beer.getN1Give();
                    }
                else
                    {
                    myID = m_edge.getNode1();
                    myResKeep = beer.getN1Keep();
                    myResGive = beer.getN1Give();
                    theirResKeep = beer.getN2Keep();
                    theirResGive = beer.getN2Give();
                    }

                if ((beer.getExchangeState() == BEEdgeResource.GREEN) && (exchType.equals("Consecutive")))
                    {
                    myResKeep.setResource(theirResGive.getResource());
                    myResGive.setResource(theirResKeep.getResource());
                    updateOtherButton();
                    updateBagButton();

                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(myID);
                    out_args[1] = new Integer(m_toNode.getID());
                    out_args[2] = new Integer(theirResGive.getIntResource());
                    out_args[3] = new Integer(theirResKeep.getIntResource());
                    
                    if (infoLevel == 11) 
                    {
                        this.m_CWApp.offerCompletedFlash();
                    }
                    
                    BECompleteOfferMsg tmp = new BECompleteOfferMsg(out_args);
                    m_CWApp.getEOApp().initializeWLMessage(tmp);
                    m_CWApp.getSML().sendMessage(tmp);
                    m_CWApp.repaint();
                    }
                if (beer.getExchangeState() == BEEdgeResource.RED)
                    {
                    myResKeep.setResource(theirResGive.getResource());
                    myResGive.setResource(theirResKeep.getResource());
                    updateOtherButton();
                    updateBagButton();

                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(myID);
                    out_args[1] = new Integer(m_toNode.getID());
                    out_args[2] = new Integer(theirResGive.getIntResource());
                    out_args[3] = new Integer(theirResKeep.getIntResource());
                    
                    if (infoLevel == 11) 
                    {
                        this.m_CWApp.offerAcceptedFlash();
                    }
                    
                    BEAcceptOfferMsg tmp = new BEAcceptOfferMsg(out_args);
                    m_CWApp.getEOApp().initializeWLMessage(tmp);
                    m_CWApp.getSML().sendMessage(tmp);

                    beer.setExchangeState(BEEdgeResource.YELLOW);
                    updateBubbleButton();
                    m_CWApp.repaint();
                    }
                }
            if (theSource == m_otherButton)
                {
                if (m_tmpKeep > 1)
                    {
                    m_tmpGive++;
                    m_tmpKeep--;
                    updateBagButton();
                    updateOtherButton();

                    if (m_ActiveArrow == BLUE)
                        {
                        m_ActiveArrow = BLACK;
                        updateArrowButton();
                        }
                    }
                }
            }
        }

    public void createOfferPanel()
        {
        Label L1, L2;
        Font f1 = m_CWApp.getEOApp().getMedLabelFont();
        Font f2 = m_CWApp.getEOApp().getSmLabelFont();

// System.err.println("OfferPanelBasic");
        m_meOPLabel = new Label("-");
        m_meOPLabel.setFont(f1);
        m_toNodeOPLabel = new Label("-");
        m_toNodeOPLabel.setFont(f1);

        m_OfferPanel.constrain(m_meOPLabel,1,1,1,2,GridBagConstraints.CENTER);
        m_OfferPanel.constrain(m_bagButton,2,1,2,1);
        m_OfferPanel.constrain(m_otherButton,2,2,2,1);
        m_OfferPanel.constrain(m_arrowButton,4,1,3,2);
        m_OfferPanel.constrain(m_toNodeOPLabel,7,1,1,2,GridBagConstraints.SOUTH);
        m_OfferPanel.constrain(m_bubbleButton,8,1,2,2);
        }

    public void disposeOfArrowStuff()
        {

        }

    public int getActiveArrow()
        {
        return m_ActiveArrow;
        }
   
    public BEEdge getEdge()
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
    public int getTmpKeep()
        {
        return m_tmpKeep;
        }
    public int getTmpGive()
        {
        return m_tmpGive;
        }
    public BENode getToNode()
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

        m_OfferDonePanel.setFont(m_CWApp.getEOApp().getMedLabelFont());
        m_meODPLabel = new Label("-");
        m_OfferDonePanel.constrain(m_meODPLabel,1,1,1,1,GridBagConstraints.CENTER);
        m_meAmtLabel = new FixedLabel(3,"-");
        m_OfferDonePanel.constrain(m_meAmtLabel,2,1,1,1,GridBagConstraints.CENTER);

        m_finalArrowCanvas = new ImageCanvas(img,null);
        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(m_finalArrowCanvas);
        m_OfferDonePanel.constrain(tmpPanel,3,1,8,1,GridBagConstraints.CENTER);

        m_toAmtLabel = new FixedLabel(3,"-",FixedLabel.RIGHT);
        m_OfferDonePanel.constrain(m_toAmtLabel,11,1,1,1,GridBagConstraints.CENTER);
        m_toNodeODPLabel = new Label("-");
        m_OfferDonePanel.constrain(m_toNodeODPLabel,12,1,1,1,GridBagConstraints.CENTER);
        m_finalArrowCanvas.repaint();
        }

    public void repaint()
        {
        }

    public void setActiveArrow(int value)
        {
        m_ActiveArrow = value;
        }
    public void setEdge(BEEdge edge)
        {
        m_edge = edge;
        if (m_edge != null)
            {
            if (m_edge.getCompleted())
                {
                BEEdgeResource beer = (BEEdgeResource)m_edge.getExptData("BEEdgeResource");
                BEExchange bee = beer.getExchange();
                if (m_toNode.getID() == m_edge.getNode1())
                    {
                    m_meAmtLabel.setText(String.valueOf((int)bee.getNode2().getIntResource()));
                    m_toAmtLabel.setText(String.valueOf((int)bee.getNode1().getIntResource()));
                    }
                else
                    {
                    m_meAmtLabel.setText(String.valueOf((int)bee.getNode1().getIntResource()));
                    m_toAmtLabel.setText(String.valueOf((int)bee.getNode2().getIntResource()));
                    }
                m_CWApp.getOfferOptionsCard().show(m_CWApp.getOfferOptionsPanel(),COMPLETED);
                m_OfferDonePanel.validate();
                }
            else
                {
                BEEdgeResource beer = (BEEdgeResource)m_edge.getExptData("BEEdgeResource");
                if (m_toNode.getID() == m_edge.getNode1())
                    {
                    if ((m_tmpKeep == -1) || (m_tmpGive == -1))
                        {
                        m_tmpKeep = beer.getN2InitialDemand();
                        m_tmpGive = beer.getRes().getIntResource() - m_tmpKeep;
                        }
                    else if (m_tmpKeep+m_tmpGive == beer.getRes().getIntResource())
                        {
                        // If amount of resources same as before don't change.
                        }
                    else if (beer.getN2Keep().getIntResource() > 0)
                        {
                        // If we made an offer already and resource pools changed, set it to that old offer first.
                        m_tmpKeep = beer.getN2Keep().getIntResource();
                        m_tmpGive = beer.getN2Give().getIntResource();
                        }
                    else
                        {
                        // If we have made no offers and the size of resource pools changed, then set give keep to half and half.
                        m_tmpKeep = beer.getN2InitialDemand();
                        m_tmpGive = beer.getRes().getIntResource() - m_tmpKeep;
                        }
                    }
                else
                    {
                    if ((m_tmpKeep == -1) || (m_tmpGive == -1))
                        {
                        m_tmpKeep = beer.getN1InitialDemand();
                        m_tmpGive = beer.getRes().getIntResource() - m_tmpKeep;
                        }
                    else if (m_tmpKeep+m_tmpGive == beer.getRes().getIntResource())
                        {
                        // If amount of resources same as before don't change.
                        }
                    else if (beer.getN1Keep().getIntResource() > 0)
                        {
                        // If we made an offer already and resource pools changed, set it to that old offer first.
                        m_tmpKeep = beer.getN1Keep().getIntResource();
                        m_tmpGive = beer.getN1Give().getIntResource();
                        }
                    else
                        {
                        // If we have made no offers and the size of resource pools changed, then set give keep to half and half.
                        m_tmpKeep = beer.getN1InitialDemand();
                        m_tmpGive = beer.getRes().getIntResource() - m_tmpKeep;
                        }
                    }
                
                m_ActiveArrow = BLACK;
                updateOtherButton();
                updateArrowButton();
                updateBubbleButton();
                updateBagButton();
                m_OfferPanel.validate();
                m_CWApp.getOfferOptionsCard().show(m_CWApp.getOfferOptionsPanel(),ACTIVE);
                }
            }
        else
            {
            m_tmpKeep = -1;
            m_tmpGive = -1;
            m_ActiveArrow = BLACK;
            updateArrowButton();
            updateBubbleButton();
            updateBagButton();
            updateOtherButton();
            m_OfferPanel.validate();
            m_CWApp.getOfferOptionsCard().show(m_CWApp.getOfferOptionsPanel(),ACTIVE);
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
    public void setTmpKeep(int value)
        {
        m_tmpKeep = value;
        }
    public void setTmpGive(int value)
        {
        m_tmpGive = value;
        }
    public void setToNode(BENode Ntemp)
        {
        m_toNode = Ntemp;
        if (m_toNode != null)
            {
            m_toNodeOPLabel.setText(m_toNode.getLabel().substring(0,1));
            m_toNodeODPLabel.setText(m_toNode.getLabel().substring(0,1));
            }
        else
            {
            m_toNodeOPLabel.setText("-");
            m_toNodeODPLabel.setText("-");
            }
        }

    public void updateArrowButton()
        {
        Image img = m_CWApp.getEOApp().createImage(60,60);
        Graphics g = img.getGraphics();

        g.setColor(Color.lightGray);
        g.fillRect(0,0,60,60);
        g.setColor(Color.black);

        if (m_ActiveArrow == BLACK)
            {
            g.drawImage(m_CWApp.getImage("Black Arrow"),0,0,m_CWApp.getEOApp().getWB());
            }
        else
            {
            g.drawImage(m_CWApp.getImage("Blue Arrow"),0,0,m_CWApp.getEOApp().getWB());
            }

        g.dispose();

        if (m_arrowButton == null)
            {
            m_arrowButton = new GraphicButton(img.getWidth(null)+4,img.getHeight(null)+4,img);
            }
        else
            {
            m_arrowButton.setImage(img);
            }
        }
    public void updateBagButton()
        {
        Image img = m_CWApp.getEOApp().createImage(60,30);
        Graphics g = img.getGraphics();
 
        g.setColor(Color.lightGray);
        g.fillRect(0,0,60,30);
        g.setColor(Color.black);

        g.setFont(m_CWApp.getEOApp().getSmLabelFont());
        
        g.drawImage(m_CWApp.getImage("Money Bag"),5,5,m_CWApp.getEOApp().getWB());

        if (m_edge != null)
            {
            if (m_edge.getActive())
                g.drawString(String.valueOf(m_tmpKeep),28,15);
            else
                g.drawString("-",28,15);
            }
        else
            {
            g.drawString("-",28,15);
            }

        g.dispose();

        if (m_bagButton != null)
            {  
            m_bagButton.setImage(img);
            }
        else
            {
            m_bagButton = new GraphicButton(img.getWidth(null)+4,img.getHeight(null)+4,img);
            }
        }
    public void updateBubbleButton()
        {
        Image img = m_CWApp.getEOApp().createImage(65,65);
        Graphics g = img.getGraphics();

        g.setColor(Color.lightGray);
        g.fillRect(0,0,65,65);
        g.setColor(Color.black);

        g.setFont(m_CWApp.getEOApp().getSmLabelFont());

        if (m_edge == null)
            {
            g.drawImage(m_CWApp.getImage("Grey Bubble"),0,0,m_CWApp.getEOApp().getWB());

            g.drawString("-",15,43);

            g.drawString("-",35,43);
            g.drawString("-",35,25);
            }
        else 
            {
            BEEdgeResource beer = (BEEdgeResource)m_edge.getExptData("BEEdgeResource");

            if (beer.getExchangeState() == BEEdgeResource.NONE)
                {
                g.drawImage(m_CWApp.getImage("Grey Bubble"),0,0,m_CWApp.getEOApp().getWB());
                }
            else if (beer.getExchangeState() == BEEdgeResource.RED)
                {
                g.drawImage(m_CWApp.getImage("Red Bubble"),0,0,m_CWApp.getEOApp().getWB());
                }
            else if (beer.getExchangeState() == BEEdgeResource.YELLOW)
                {
                g.drawImage(m_CWApp.getImage("Yellow Bubble"),0,0,m_CWApp.getEOApp().getWB());
                }
            else if (beer.getExchangeState() == BEEdgeResource.GREEN)
                {
                g.drawImage(m_CWApp.getImage("Green Bubble"),0,0,m_CWApp.getEOApp().getWB());
                }

            g.drawString(m_toNode.getLabel().substring(0,1),15,43);

            if (beer.getExchangeState() != BEEdgeResource.NONE)
                {
                if (m_edge.getNode1() == m_toNode.getID())
                    {
                    g.drawString(String.valueOf(beer.getN1Keep().getIntResource()),35,43);
                    g.drawString(String.valueOf(beer.getN1Give().getIntResource()),35,25);
                    }
                else
                    {
                    g.drawString(String.valueOf(beer.getN2Keep().getIntResource()),35,43);
                    g.drawString(String.valueOf(beer.getN2Give().getIntResource()),35,25);
                    }
                }
            else
                {
                g.drawString("-",35,43);
                g.drawString("-",35,25);
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
    public void updateOtherButton()
        {
        Image img = m_CWApp.getEOApp().createImage(60,30);
        Graphics g = img.getGraphics();

        g.setColor(Color.lightGray);
        g.fillRect(0,0,60,30);
        g.setColor(Color.black);

        g.setFont(m_CWApp.getEOApp().getSmLabelFont());

        if (m_toNode != null)
            g.drawString(m_toNode.getLabel().substring(0,1),5,15);
        else
            g.drawString("-",5,15);

        if (m_edge != null)
            {
            if (m_edge.getActive())
                {
                g.drawString(String.valueOf(m_tmpGive),28,15);
                }
            else
                g.drawString("-",28,15);
            }
        else
            {
            g.drawString("-",28,15);
            }

        g.dispose();

        if (m_otherButton != null)
            {
            m_otherButton.setImage(img);
            }
        else
            {
            m_otherButton = new GraphicButton(img.getWidth(null)+4,img.getHeight(null)+4,img);
            }
        }    
    }

