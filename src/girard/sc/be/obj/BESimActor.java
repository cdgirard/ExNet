package girard.sc.be.obj;

import girard.sc.be.io.msg.BEAcceptOfferMsg;
import girard.sc.be.io.msg.BEAfterSanctionWindowMsg;
import girard.sc.be.io.msg.BEBeginNetworkActionReqMsg;
import girard.sc.be.io.msg.BECompleteOfferMsg;
import girard.sc.be.io.msg.BEEndRoundMsg;
import girard.sc.be.io.msg.BENodeOrSNWindowMsg;
import girard.sc.be.io.msg.BENodeSNWindowMsg;
import girard.sc.be.io.msg.BENodeSanctionMsg;
import girard.sc.be.io.msg.BENodeSanctionWindowMsg;
import girard.sc.be.io.msg.BEOfferMsg;
import girard.sc.be.io.msg.BERoundWindowMsg;
import girard.sc.be.io.msg.BEStartNetworkActionReqMsg;
import girard.sc.be.io.msg.BEStartNextPeriodMsg;
import girard.sc.be.io.msg.BEStartRoundMsg;
import girard.sc.be.io.msg.BEStopNetworkActionReqMsg;
import girard.sc.be.io.msg.BEStopRoundMsg;
import girard.sc.be.io.msg.BETimeTckMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.StartExptReqMsg;
import girard.sc.expt.obj.SimActor;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Hashtable;

public abstract class BESimActor extends SimActor
    {
    protected boolean m_flag1 = true; /* Keep running flag. */
    protected boolean m_flag2 = true;
    protected boolean m_cleanUpFlag = false;

    public BESimActor (String db, String name, String desc)
        {
        super (db,name,desc);
        }
    
    public void acceptOffer(Object[] args)
        {
        BEAcceptOfferMsg tmp = new BEAcceptOfferMsg(args);
        m_SML.sendMessage(tmp);
        }

    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof ExptMessage)
            {
            ExptMessage em = (ExptMessage)e.getSource();

            synchronized(m_SML)
                {
                if (em instanceof BEAcceptOfferMsg)
                    {
                    processBEAcceptOfferMsg(em);
                    }
                else if (em instanceof BEAfterSanctionWindowMsg)
                    {
                    processBEAfterSanctionWindowMsg(em);
                    }
                else if (em instanceof BEBeginNetworkActionReqMsg)
                    {
                	System.err.println("Sim Actor processed this Message - "+em);
                    processBEBeginNetworkActionReqMsg(em);
                    }
                else if (em instanceof BECompleteOfferMsg)
                    {
                    processBECompleteOfferMsg(em);
                    }
                else if (em instanceof BEEndRoundMsg)
                    {
                	System.err.println("Sim Actor processed this Message - "+em);
                    processBEEndRoundMsg(em);
                    }
                else if (em instanceof BENodeOrSNWindowMsg)
                    {
                    processBENodeOrSNWindowMsg(em);
                    }
                else if (em instanceof BENodeSNWindowMsg)
                    {
                    processBENodeSNWindowMsg(em);
                    }
                else if (em instanceof BENodeSanctionMsg)
                    {
                    processBENodeSanctionMsg(em);
                    }
                else if (em instanceof BENodeSanctionWindowMsg)
                    {
                    processBENodeSanctionWindowMsg(em);
                    }
                else if (em instanceof BEOfferMsg)
                    {
                    processBEOfferMsg(em);
                    }
                else if (em instanceof BERoundWindowMsg)
                    {
                	System.err.println("Sim Actor processed this Message - "+em);
                    processBERoundWindowMsg(em);
                    }
                else if (em instanceof BEStartNetworkActionReqMsg)
                    {
                	System.err.println("Sim Actor processed this Message - "+em);
                    processBEStartNetworkActionReqMsg(em);
                    }
                else if (em instanceof BEStartNextPeriodMsg)
                    {
                    processBEStartNextPeriodMsg(em);
                    }
                else if (em instanceof BEStartRoundMsg)
                    {
                	System.err.println("Sim Actor processed this Message - "+em);
                    processBEStartRoundMsg(em);
                    }
                else if (em instanceof BEStopRoundMsg)
                    {
                	System.err.println("Sim Actor processed this Message - "+em);
                    processBEStopRoundMsg(em);
                    }
                else if (em instanceof BEStopNetworkActionReqMsg)
                    {
                    processBEStopNetworkActionReqMsg(em);
                    }
                else if (em instanceof BETimeTckMsg)
                    {
                    processBETimeTckMsg(em);
                    }
                else if (em instanceof StartExptReqMsg)
                    {
                	System.err.println("Sim Actor processed this Message - "+em);
                    processStartExptReqMsg(em);
                    }
                else
                    {
                    System.err.println("Sim Actor Doesn't Know this Message - "+em);
                    }
                }
            }
        }

    public void completeExchange(Object[] args)
        {
        String type = (String)getNetwork().getExtraData("ExchangeMethod");
        if (type.equals("Consecutive"))
            {
            BECompleteOfferMsg tmp = new BECompleteOfferMsg(args);
            m_SML.sendMessage(tmp);
            }
        }

    public BENetwork getNetwork()
        {
        return (BENetwork)m_activeAction;
        }
    public boolean getRoundRunning()
        {
        Boolean rr = (Boolean)getNetwork().getExtraData("RoundRunning");

        return rr.booleanValue();
        }

    // Called at the very beginning and at the start of each period.
    public void initializeNetwork()
        {
    // Label nodes based on are they me, my neighbor, or other.
        Enumeration enm = getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode node = (BENode)enm.nextElement();
            if (node.isMe(m_user,getNetwork()))
                {
                node.setExtraData("Type","Me");
                getNetwork().setExtraData("Me",node);
                }
            else if (node.isNeighbor(m_user,getNetwork()))
                {
                node.setExtraData("Type","Neighbor");
                }
            else
                {
                node.setExtraData("Type","Other");
                }
            }
        }

    public void processBEAcceptOfferMsg(ExptMessage em)
        {
        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        int fromKeep = ((Integer)em.getArgs()[2]).intValue();
        int toKeep = ((Integer)em.getArgs()[3]).intValue();

        Boolean rr = (Boolean)getNetwork().getExtraData("RoundRunning");

        if (!rr.booleanValue())
            return;

        BEEdge edge = null;
        BEEdgeResource beer = null;
        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            edge = (BEEdge)enm.nextElement();
            if ((edge.getNode1() == to) && (edge.getNode2() == from))
                {
                if (edge.getActive())
                    {
                    beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    beer.getN2Keep().setResource(fromKeep);
                    beer.getN2Give().setResource(toKeep);
                    }
                break;
                }
            if ((edge.getNode2() == to) && (edge.getNode1() == from))
                {
                if (edge.getActive())
                    {
                    beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    beer.getN1Keep().setResource(fromKeep);
                    beer.getN1Give().setResource(toKeep);
                    }
                break;
                }
            }

        BENode node = (BENode)getNetwork().getExtraData("Me");

     // Was it an offer sent to me by one of my neighbors?
        if (to == node.getID())
            {
            if (edge.getActive()) 
                {
                beer.setExchangeState(BEEdgeResource.GREEN);
                }
            }
        }
    public void processBEAfterSanctionWindowMsg(ExptMessage em)
        {
        BEAfterSanctionWindowMsg tmp = new BEAfterSanctionWindowMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processBEBeginNetworkActionReqMsg(ExptMessage em)
        {
        m_flag1 = false;
    
        BEStartNetworkActionReqMsg tmp = new BEStartNetworkActionReqMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processBECompleteOfferMsg(ExptMessage em)
        {
        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        int fromKeep = ((Integer)em.getArgs()[2]).intValue();
        int toKeep = ((Integer)em.getArgs()[3]).intValue();
        Boolean rr = (Boolean)getNetwork().getExtraData("RoundRunning");

        if (!rr.booleanValue())
            return;

        BEEdge edge = null;
        BEEdgeResource beer = null;
        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            edge = (BEEdge)enm.nextElement();
            if ((edge.getNode1() == to) && (edge.getNode2() == from))
                {
                if (edge.getActive())  // This should not matter, but we will leave it in just in case.
                    {
                    beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    int tt = getNetwork().getActivePeriod().getTime() - getNetwork().getActivePeriod().getCurrentTime();
                    beer.completeExchange(tt,0,toKeep,fromKeep);
                    break;
                    }
                else
                    {
                    return;
                    }
                }
            if ((edge.getNode2() == to) && (edge.getNode1() == from))
                {
                if (edge.getActive())
                    {
                    beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    int tt = getNetwork().getActivePeriod().getTime() - getNetwork().getActivePeriod().getCurrentTime();
                    beer.completeExchange(tt,0,fromKeep,toKeep);
                    break;
                    }
                else
                    {
                    return;
                    }
                }
            }

        BENode n1 = (BENode)getNetwork().getNode(from);
        BENode n2 = (BENode)getNetwork().getNode(to);

        BENodeExchange exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
        BENodeExchange exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");
 
        exch1.updateNetwork(edge);
        exch2.updateNetwork(edge);

        enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            n1 = (BENode)getNetwork().getNode(edge.getNode1());
            n2 = (BENode)getNetwork().getNode(edge.getNode2());

            exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
            exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");

            if ((exch1.isEdgeActive(tmpEdge)) && (exch2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                if (!tmpEdge.getCompleted())
                    {
                    BEEdgeResource tmpBeer = (BEEdgeResource)tmpEdge.getExptData("BEEdgeResource");
                    tmpBeer.setExchangeState(BEEdgeResource.NONE);
                    }
                tmpEdge.setActive(false);
                }
            }
        }
    public void processBEEndRoundMsg(ExptMessage em)
        {
        double per = 0;  // Points earned this round.
        BENode me = (BENode)getNetwork().getExtraData("Me");
        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            if ((edge.getNode1() == me.getID()) && (beer.getExchange() != null))
                {
                per = per + beer.getExchange().getNode1().getResource();
                }
            if ((edge.getNode2() == me.getID()) && (beer.getExchange() != null))
                {
                per = per + beer.getExchange().getNode2().getResource();
                }
            }
        Object[] out_args = new Object[2];
        out_args[0] = new Double(per);
        out_args[1] = new Integer(me.getID());
        BEEndRoundMsg tmp = new BEEndRoundMsg(out_args);
        m_SML.sendMessage(tmp);
        }
    public void processBENodeOrSNWindowMsg(ExptMessage em)
        {
        BENodeOrSNWindowMsg tmpMsg = new BENodeOrSNWindowMsg(null);
        m_SML.sendMessage(tmpMsg);
        }
    public void processBENodeSNWindowMsg(ExptMessage em)
        {
        BENodeSNWindowMsg tmp = new BENodeSNWindowMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processBENodeSanctionMsg(ExptMessage em)
        {
        Boolean rr = (Boolean)getNetwork().getExtraData("RoundRunning");

        if (rr.booleanValue())
            return;

        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        boolean msg = ((Boolean)em.getArgs()[2]).booleanValue();

        BENode fromNode = (BENode)getNetwork().getNode(from);
        BENode toNode = (BENode)getNetwork().getNode(to);

        BENodeSanctions fromBens = (BENodeSanctions)fromNode.getExptData("BENodeSanctions");
        BENodeSanction fromSanction = (BENodeSanction)fromBens.getSanction(to);
        BENodeSanctions toBens = (BENodeSanctions)toNode.getExptData("BENodeSanctions");

        fromBens.sanctionSent(to,msg);

        Enumeration enm = getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeDisplay beed = (BEEdgeDisplay)edge.getExptData("BEEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (!toBens.hasSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                else if (!toBens.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (!toBens.hasSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                else if (!toBens.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            }

        enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            BENode n1 = (BENode)getNetwork().getNode(tmpEdge.getNode1());
            BENode n2 = (BENode)getNetwork().getNode(tmpEdge.getNode2());

            BENodeSanctions ns1 = (BENodeSanctions)n1.getExptData("BENodeSanctions");
            BENodeSanctions ns2 = (BENodeSanctions)n2.getExptData("BENodeSanctions");

            if ((ns1.isEdgeActive(tmpEdge)) && (ns2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }
            }
        }
    public void processBENodeSanctionWindowMsg(ExptMessage em)
        {
        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            BENode n1 = (BENode)getNetwork().getNode(tmpEdge.getNode1());
            BENode n2 = (BENode)getNetwork().getNode(tmpEdge.getNode2());

            BENodeSanctions ns1 = (BENodeSanctions)n1.getExptData("BENodeSanctions");
            BENodeSanctions ns2 = (BENodeSanctions)n2.getExptData("BENodeSanctions");

            tmpEdge.setCompleted(false);

            if ((ns1.isEdgeActive(tmpEdge)) && (ns2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }
            }

        BENode node = (BENode)getNetwork().getExtraData("Me");
        BENodeSanctions ns = (BENodeSanctions)node.getExptData("BENodeSanctions");
        if (ns.getSanctions().size() > 0)
            {
            for (int i=0;i<ns.getSanctions().size();i++)
                {
                BENodeSanction sanction = (BENodeSanction)ns.getSanctions().elementAt(i);

                Object[] out_args = new Object[3];
                out_args[0] = new Integer(node.getID()); // From
                out_args[1] = new Integer(sanction.getToNode()); // To
                out_args[2] = new Boolean(true);

                BENodeSanctionMsg tmp = new BENodeSanctionMsg(out_args);
                m_SML.sendMessage(tmp);
                }
            }
        }
    public void processBEOfferMsg(ExptMessage em)
        {
        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        int fromKeep = ((Integer)em.getArgs()[2]).intValue();
        int toKeep = ((Integer)em.getArgs()[3]).intValue();

        Boolean rr = (Boolean)getNetwork().getExtraData("RoundRunning");

        if (!rr.booleanValue())
            return;

        BEEdge edge = null;
        BEEdgeResource beer = null;
        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            edge = (BEEdge)enm.nextElement();
            if ((edge.getNode1() == to) && (edge.getNode2() == from))
                {
                if (edge.getActive())
                    {
                    beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    beer.getN2Keep().setResource(fromKeep);
                    beer.getN2Give().setResource(toKeep);
                    }
                break;
                }
            if ((edge.getNode2() == to) && (edge.getNode1() == from))
                {
                if (edge.getActive())
                    {
                    beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    beer.getN1Keep().setResource(fromKeep);
                    beer.getN1Give().setResource(toKeep);
                    }
                break;
                }
            }

        BENode node = (BENode)getNetwork().getExtraData("Me");

        // Was it an offer sent to me by one of my neighbors?
        if (to == node.getID())
            {
            if (edge.getActive()) 
                { 
                beer.setExchangeState(BEEdgeResource.RED);
                }
            }
        }
    public void processBERoundWindowMsg(ExptMessage em)
        {
        BERoundWindowMsg tmp = new BERoundWindowMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processBEStartNetworkActionReqMsg(ExptMessage em)
        {
        m_activeAction = em.getArgs()[0];
        getNetwork().setExtraData("RoundRunning",new Boolean(false));
        getNetwork().setCurrentPeriod(0);
        initializeNetwork();

        Hashtable windowSettings = (Hashtable)getNetwork().getExtraData("InitialWindow");
        String cont = (String)windowSettings.get("Continue");
        if (cont.equals("Client"))
            {
            m_flag1 = false;
    
            BEStartNetworkActionReqMsg tmp = new BEStartNetworkActionReqMsg(null);
            m_SML.sendMessage(tmp);
            }
        }
    public void processBEStartNextPeriodMsg(ExptMessage em)
        {
        getNetwork().setCurrentPeriod(getNetwork().getCurrentPeriod() + 1);
        initializeNetwork();
    
        BEStartNextPeriodMsg tmp = new BEStartNextPeriodMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processBEStartRoundMsg(ExptMessage em)
        {
        getNetwork().setExtraData("RoundRunning",new Boolean(true));
        getNetwork().initializeNetwork();
        }
    public void processBEStopNetworkActionReqMsg(ExptMessage em)
        {
        getNetwork().setExtraData("RoundRunning",new Boolean(false));

        BEStopNetworkActionReqMsg tmp = new BEStopNetworkActionReqMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processBEStopRoundMsg(ExptMessage em)
        {
        getNetwork().setExtraData("RoundRunning",new Boolean(false));

    /* modifing earnings goes here */
        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            BENode node1 = (BENode)getNetwork().getNode(edge.getNode1());
            BENode node2 = (BENode)getNetwork().getNode(edge.getNode2());
            BENodeExchange exchN1 = (BENodeExchange)node1.getExptData("BENodeExchange");
            BENodeExchange exchN2 = (BENodeExchange)node2.getExptData("BENodeExchange");
            beer.adjustEarnings(exchN1,exchN2);
            }

        BEStopRoundMsg tmp = new BEStopRoundMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processBETimeTckMsg(ExptMessage em)
        {
        if (getRoundRunning())
            {
            BETimeTckMsg tmp = new BETimeTckMsg(null);
            m_SML.sendMessage(tmp);
            }
        }
    public void processStartExptReqMsg(ExptMessage em)
        {
        StartExptReqMsg tmp = new StartExptReqMsg(null);
        m_SML.sendMessage(tmp);
        }

    public void sendOffer(Object[] args)
        {
        BEOfferMsg tmp = new BEOfferMsg(args);
        m_SML.sendMessage(tmp);
        }
    }
