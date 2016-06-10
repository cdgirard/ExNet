package girard.sc.cc.awt;

/* This manages the buttons displayed on the client screen for sending
   and accepting offers in a CCNetworkAction.

   Author: Dudley Girard
   Started: 5-24-2001
*/

import girard.sc.awt.FixedLabel;
import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.ImageCanvas;
import girard.sc.cc.io.msg.CCAcceptOfferMsg;
import girard.sc.cc.io.msg.CCCompleteOfferMsg;
import girard.sc.cc.io.msg.CCOfferMsg;
import girard.sc.cc.obj.CCExchange;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;

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

public class CCClientDisplayArrow implements ActionListener
    {
    public static final int BLACK = 1;
    public static final int BLUE = 2;
    public static final String COMPLETED = "Completed Panel";
    public static final String ACTIVE = "Active Panel"; 

    CCNetworkActionClientWindow m_CWApp;
    CCNode    	m_myNode = null; /* My node */
    CCNode        m_toNode = null; /* Which node the offers are being sent to via the Arrow */
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
    Label           m_meNodeODPLabel = null;
    FixedLabel      m_toAmtLabel = null;
    FixedLabel      m_meAmtLabel = null;
    ImageCanvas     m_finalArrowCanvas = null;

    GridBagPanel    m_OfferPanel = null;
    Label           m_toNodeOPLabel = null;
    Label           m_meNodeOPLabel = null;

    public CCClientDisplayArrow(CCNetworkActionClientWindow cw)
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
        Boolean rr = (Boolean)m_CWApp.getNetwork().getExtraData("RoundRunning");
        if ((!rr.booleanValue()) || (m_toNode == null))
            return;

        if (tradeStatus() != 0)
            return;

        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            if ((theSource == m_arrowButton) && (m_ActiveArrow == BLACK))
                {
                CCNodeResource myCcnr = (CCNodeResource)m_myNode.getExptData("CCNodeResource");
                CCNodeResource toCcnr = (CCNodeResource)m_toNode.getExptData("CCNodeResource");
                CCExchange theirOffer = myCcnr.getOffer(m_toNode.getID());
                CCExchange myOffer = toCcnr.getOffer(m_myNode.getID());

                if (myOffer != null)
                    {
                    myOffer.getNode1().setResource(m_tmpKeep);
                    myOffer.getNode2().setResource(m_tmpGive);
                    }
                else
                    {
                    myOffer = new CCExchange(m_tmpKeep,m_myNode.getID(),m_tmpGive,m_toNode.getID());
                    toCcnr.addOffer(myOffer);
                    }

                if (theirOffer == null)
                    {
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(m_myNode.getID());
                    out_args[1] = new Integer(m_toNode.getID());
                    out_args[2] = new Integer(m_tmpKeep);
                    out_args[3] = new Integer(m_tmpGive);

                    myOffer.setExchangeState(CCExchange.RED);

                    CCOfferMsg tmp = new CCOfferMsg(out_args);
                    m_CWApp.getSML().sendMessage(tmp);

                    m_ActiveArrow = BLUE;
                    updateArrowButton();
                    m_CWApp.repaint();
                    }
                else 
                    {
                    if ((myOffer.getNode1().getIntResource() == theirOffer.getNode2().getIntResource()) && (theirOffer.getExchangeState() == CCExchange.RED))
                        {
                        Object[] out_args = new Object[4];
                        out_args[0] = new Integer(m_myNode.getID());
                        out_args[1] = new Integer(m_toNode.getID());
                        out_args[2] = new Integer(m_tmpKeep);
                        out_args[3] = new Integer(m_tmpGive);

                        CCAcceptOfferMsg tmp = new CCAcceptOfferMsg(out_args);
                        m_CWApp.getSML().sendMessage(tmp);

                        theirOffer.setExchangeState(CCExchange.YELLOW);
                        myOffer.setExchangeState(CCExchange.GREEN);

                        m_ActiveArrow = BLUE;
                        updateBubbleButton();
                        updateArrowButton();
                        m_CWApp.repaint();
                        }
                    else if (myOffer.getNode1().getIntResource() != theirOffer.getNode2().getIntResource())
                        {
                        Object[] out_args = new Object[4];
                        out_args[0] = new Integer(m_myNode.getID());
                        out_args[1] = new Integer(m_toNode.getID());
                        out_args[2] = new Integer(m_tmpKeep);
                        out_args[3] = new Integer(m_tmpGive);

                        myOffer.setExchangeState(CCExchange.RED);

                        CCOfferMsg tmp = new CCOfferMsg(out_args);
                        m_CWApp.getSML().sendMessage(tmp);

                        if ((theirOffer.getExchangeState() == CCExchange.GREEN)  || (theirOffer.getExchangeState() == CCExchange.YELLOW))
                            {
                            theirOffer.setExchangeState(CCExchange.RED);
                            updateBubbleButton();
                            }

                        m_ActiveArrow = BLUE;
                        updateArrowButton();
                        m_CWApp.repaint();
                        }
                    }
       // Otherwise don't send any message since we want them to press the Green bubble.
                }
            if (theSource == m_bagButton)
                {
                if (m_tmpGive > 0)
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
                CCNodeResource myCcnr = (CCNodeResource)m_myNode.getExptData("CCNodeResource");
                CCNodeResource toCcnr = (CCNodeResource)m_toNode.getExptData("CCNodeResource");
                CCExchange theirOffer = myCcnr.getOffer(m_toNode.getID());
                CCExchange myOffer = toCcnr.getOffer(m_myNode.getID());

                if (myOffer != null)
                    {
                    myOffer.getNode1().setResource(theirOffer.getNode2().getIntResource());
                    myOffer.getNode2().setResource(theirOffer.getNode1().getIntResource());
                    }
                else
                    {
                    myOffer = new CCExchange(theirOffer.getNode2().getIntResource(),m_myNode.getID(),theirOffer.getNode1().getIntResource(),m_toNode.getID());
                    toCcnr.addOffer(myOffer);
                    }


                if (theirOffer.getExchangeState() == CCExchange.GREEN)
                    {
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(m_myNode.getID());
                    out_args[1] = new Integer(m_toNode.getID());
                    out_args[2] = new Integer(myOffer.getNode1().getIntResource());
                    out_args[3] = new Integer(myOffer.getNode2().getIntResource());
                    CCCompleteOfferMsg tmp = new CCCompleteOfferMsg(out_args);
                    m_CWApp.getSML().sendMessage(tmp);

                    m_CWApp.repaint();
                    }
                if (theirOffer.getExchangeState() == CCExchange.RED)
                    {
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(m_myNode.getID());
                    out_args[1] = new Integer(m_toNode.getID());
                    out_args[2] = new Integer(myOffer.getNode1().getIntResource());
                    out_args[3] = new Integer(myOffer.getNode2().getIntResource());
                    CCAcceptOfferMsg tmp = new CCAcceptOfferMsg(out_args);
                    m_CWApp.getSML().sendMessage(tmp);

                    theirOffer.setExchangeState(CCExchange.YELLOW);
                    updateBubbleButton();
                    m_CWApp.repaint();
                    }
                }
            if (theSource == m_otherButton)
                {
                if (m_tmpKeep > 0)
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
        m_meNodeOPLabel = new Label("-");
        m_meNodeOPLabel.setFont(f1);
        m_toNodeOPLabel = new Label("-");
        m_toNodeOPLabel.setFont(f1);

        m_OfferPanel.constrain(m_meNodeOPLabel,1,1,1,2,GridBagConstraints.CENTER);
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
   
    public CCNode getMyNode()
        {
        return m_myNode;
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
    public CCNode getToNode()
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

        // m_OfferDonePanel.removeAll();
        m_OfferDonePanel.setFont(m_CWApp.getEOApp().getMedLabelFont());
        m_meNodeODPLabel = new Label("-");
        m_OfferDonePanel.constrain(m_meNodeODPLabel,1,1,1,1,GridBagConstraints.CENTER);
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
        // m_finalArrowCanvas.repaint();
        }

    public void repaint()
        {
        }

    public void setActiveArrow(int value)
        {
        m_ActiveArrow = value;
        }
    public void setMyNode(CCNode Ntemp)
        {
        m_myNode = Ntemp;
        if (m_myNode != null)
            {
            m_meNodeOPLabel.setText(m_myNode.getLabel().substring(0,1));
            m_meNodeODPLabel.setText(m_myNode.getLabel().substring(0,1));
            }
        else
            {
            m_meNodeOPLabel.setText("-");
            m_meNodeODPLabel.setText("-");
            }
        updateDisplayArrow();
        }
    public void setTmpKeep(int value)
        {
        m_tmpKeep = value;
        }
    public void setTmpGive(int value)
        {
        m_tmpGive = value;
        }
    public void setToNode(CCNode Ntemp)
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
        updateDisplayArrow();
        }

    public int tradeStatus()
        {
        if ((m_myNode != null) && (m_toNode != null))
            {
            CCNodeResource myCcnr = (CCNodeResource)m_myNode.getExptData("CCNodeResource");
            CCNodeResource toCcnr = (CCNodeResource)m_toNode.getExptData("CCNodeResource");

            if ((myCcnr.canTradeWith(m_toNode.getID())) || (toCcnr.canTradeWith(m_myNode.getID())))
                {
                if (myCcnr.tradeCompletedWith(m_toNode.getID()))
                    {
                    return 1; // These two nodes have already completed a trade.
                    }
                else
                    {
                    if ((myCcnr.canNegoiateWith(m_toNode.getID())) && (toCcnr.canNegoiateWith(m_myNode.getID())))
                        {
                        return 0; // These two nodes can negoiate.
                        }
                    else
                        {
                        return -1; // These two nodes can not negoiate for whatever reason.
                        }
                    }
                }
            else
                {
                return -1; // These two nodes can not trade with each other.
                }
            }
        else
            {
            return -1; // One or both of the node objects are null.
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

        if (tradeStatus() == 0)
            {
            g.drawString(String.valueOf(m_tmpKeep),28,15);
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

        if (tradeStatus() != 0)
            {
            g.drawImage(m_CWApp.getImage("Grey Bubble"),0,0,m_CWApp.getEOApp().getWB());

            g.setColor(Color.lightGray);
            g.fillRect(10,10,20,20);
            }
        else 
            {
            CCNodeResource myCcnr = (CCNodeResource)m_myNode.getExptData("CCNodeResource");
            CCNodeResource toCcnr = (CCNodeResource)m_toNode.getExptData("CCNodeResource");
            CCExchange cce = myCcnr.getOffer(m_toNode.getID());

            boolean bubbleFlag = true;

            if (cce == null)
                {
                g.drawImage(m_CWApp.getImage("Grey Bubble"),0,0,null);
                bubbleFlag = false;
                }
            else if (cce.getExchangeState() == CCExchange.NONE)
                {
                g.drawImage(m_CWApp.getImage("Grey Bubble"),0,0,null);
                bubbleFlag = false;
                }
            else if (cce.getExchangeState() == CCExchange.RED)
                {
                g.drawImage(m_CWApp.getImage("Red Bubble"),0,0,null);
                }
            else if (cce.getExchangeState() == CCExchange.YELLOW)
                {
                g.drawImage(m_CWApp.getImage("Yellow Bubble"),0,0,null);
                }
            else if (cce.getExchangeState() == CCExchange.GREEN)
                {
                g.drawImage(m_CWApp.getImage("Green Bubble"),0,0,null);
                }

            if (bubbleFlag)
                {
            //    if ((myCcnr.getPointPool() > 0) && (toCcnr.getPointPool() > 0))
             //       {
                g.drawString(m_toNode.getLabel().substring(0,1),15,43);
                g.drawString(String.valueOf(cce.getNode1().getIntResource()),35,43); // How much they want.
                g.drawString(String.valueOf(cce.getNode2().getIntResource()),35,25); // How much offering to me.
            //        }
           /*     else if (myCcnr.getPointPool() > 0)
                    {
                    g.setColor(Color.lightGray);
                    g.fillRect(10,10,20,20);
                    g.setColor(Color.black);
                    g.drawString(m_toNode.getLabel().substring(0,1),15,35);
                    g.drawString(String.valueOf(cce.getNode1().getIntResource()),35,35); // How much they want.
                    }
                else
                    {
                    g.setColor(Color.lightGray);
                    g.fillRect(10,10,20,20);
                    g.setColor(Color.black);
                    g.drawImage(m_CWApp.getImage("Money Bag"),10,20,null);
                    g.drawString(String.valueOf(cce.getNode2().getIntResource()),35,35); // How much offering to me.
                    } */
                }
            else
                {
                g.setColor(Color.lightGray);
                g.fillRect(10,10,20,20);
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
    public void updateDisplayArrow()
        {
        int value = tradeStatus();

        if (value != -1)
            {
            CCNodeResource myCcnr = (CCNodeResource)m_myNode.getExptData("CCNodeResource");
            CCNodeResource toCcnr = (CCNodeResource)m_toNode.getExptData("CCNodeResource");

            if (value == 1) // An Exchange has been completed.
                {
                CCExchange cce = myCcnr.getCompletedExchange(m_toNode.getID());
  
                m_meAmtLabel.setText(String.valueOf(cce.getNode1().getIntResource()));
                m_toAmtLabel.setText(String.valueOf(cce.getNode2().getIntResource()));

                m_CWApp.getOfferOptionsCard().show(m_CWApp.getOfferOptionsPanel(),COMPLETED);
                // m_OfferDonePanel.validate();
                }
            else // Value must be 0
                {
                int poolTotal = myCcnr.getPointPool() + toCcnr.getPointPool();
                CCExchange myOffer = toCcnr.getOffer(m_myNode.getID());
                if (m_tmpKeep < 1)
                    {
                    m_tmpKeep = poolTotal/2;
                    m_tmpGive =  poolTotal - m_tmpKeep;
                    }
                else if (m_tmpKeep+m_tmpGive == poolTotal)
                    {
                    // If amount of resources same as before don't change.
                    }
                else if (myOffer == null)
                    {
                    // If we have made no offers and the size of resource pools changed, then set give keep to half and half.
                    m_tmpKeep = poolTotal/2;
                    m_tmpGive = poolTotal - m_tmpKeep;
                    }
                else 
                    {
                    // If we made an offer already and resource pools changed, set it to that old offer first.
                    m_tmpKeep = myOffer.getNode1().getIntResource();
                    m_tmpGive = myOffer.getNode2().getIntResource();
                    }
                 
                m_ActiveArrow = BLACK;
                updateOtherButton();
                updateBubbleButton();
                updateBagButton();
                updateArrowButton();
                // m_OfferPanel.validate();
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
            // m_OfferPanel.validate();
            m_CWApp.getOfferOptionsCard().show(m_CWApp.getOfferOptionsPanel(),ACTIVE);
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

        if (tradeStatus() == 0)
            {
            g.drawString(String.valueOf(m_tmpGive),28,15);
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

