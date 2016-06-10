package girard.sc.cc.obj;

/* Acts as the base class for any simulant actors developed for the cc
   network action.

   Author: Dudley Girard
   Started: 7-16-2001
*/

import girard.sc.cc.io.msg.CCAcceptOfferMsg;
import girard.sc.cc.io.msg.CCAfterFuzzyWindowMsg;
import girard.sc.cc.io.msg.CCAfterSanctionWindowMsg;
import girard.sc.cc.io.msg.CCAfterTokenWindowMsg;
import girard.sc.cc.io.msg.CCCompleteOfferMsg;
import girard.sc.cc.io.msg.CCEndRoundMsg;
import girard.sc.cc.io.msg.CCNodeFuzzyMsg;
import girard.sc.cc.io.msg.CCNodeFuzzyWindowMsg;
import girard.sc.cc.io.msg.CCNodeSanctionMsg;
import girard.sc.cc.io.msg.CCNodeSanctionWindowMsg;
import girard.sc.cc.io.msg.CCNodeTokenMsg;
import girard.sc.cc.io.msg.CCNodeTokenWindowMsg;
import girard.sc.cc.io.msg.CCOfferMsg;
import girard.sc.cc.io.msg.CCRoundWindowMsg;
import girard.sc.cc.io.msg.CCStartNetworkActionReqMsg;
import girard.sc.cc.io.msg.CCStartRoundMsg;
import girard.sc.cc.io.msg.CCStopNetworkActionReqMsg;
import girard.sc.cc.io.msg.CCStopRoundMsg;
import girard.sc.cc.io.msg.CCTimeTckMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.StartExptReqMsg;
import girard.sc.expt.obj.SimActor;

import java.awt.event.ActionEvent;
import java.util.Enumeration;

public abstract class CCSimActor extends SimActor
    {
    protected boolean m_flag1 = true; /* Keep running flag. */
    protected boolean m_flag2 = true;
    protected boolean m_cleanUpFlag = false;

    public CCSimActor (String db, String name, String desc)
        {
        super (db,name,desc);
        }
    
    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof ExptMessage)
            {
            ExptMessage em = (ExptMessage)e.getSource();

            synchronized(m_SML)
                {
                if (em instanceof CCAcceptOfferMsg)
                    {
                    processCCAcceptOfferMsg(em);
                    }
                if (em instanceof CCAfterFuzzyWindowMsg)
                    {
                    processCCAfterFuzzyWindowMsg(em);
                    }
                if (em instanceof CCAfterSanctionWindowMsg)
                    {
                    processCCAfterSanctionWindowMsg(em);
                    }
                if (em instanceof CCAfterTokenWindowMsg)
                    {
                    processCCAfterTokenWindowMsg(em);
                    }
                if (em instanceof CCCompleteOfferMsg)
                    {
                    processCCCompleteOfferMsg(em);
                    }
                if (em instanceof CCEndRoundMsg)
                    {
                    processCCEndRoundMsg(em);
                    }
                if (em instanceof CCNodeFuzzyMsg)
                    {
                    processCCNodeFuzzyMsg(em);
                    }
                if (em instanceof CCNodeFuzzyWindowMsg)
                    {
                    processCCNodeFuzzyWindowMsg(em);
                    }
                if (em instanceof CCNodeSanctionMsg)
                    {
                    processCCNodeSanctionMsg(em);
                    }
                 if (em instanceof CCNodeSanctionWindowMsg)
                    {
                    processCCNodeSanctionWindowMsg(em);
                    }
                if (em instanceof CCNodeTokenMsg)
                    {
                    processCCNodeTokenMsg(em);
                    }
                if (em instanceof CCNodeTokenWindowMsg)
                    {
                    processCCNodeTokenWindowMsg(em);
                    }
                if (em instanceof CCOfferMsg)
                    {
                    processCCOfferMsg(em);
                    }
                if (em instanceof CCRoundWindowMsg)
                    {
                    processCCRoundWindowMsg(em);
                    }
                if (em instanceof CCStartNetworkActionReqMsg)
                    {
                    processCCStartNetworkActionReqMsg(em);
                    }
                if (em instanceof CCStartRoundMsg)
                    {
                    processCCStartRoundMsg(em);
                    }
                if (em instanceof CCStopRoundMsg)
                    {
                    processCCStopRoundMsg(em);
                    }
                if (em instanceof CCStopNetworkActionReqMsg)
                    {
                    processCCStopNetworkActionReqMsg(em);
                    }
                if (em instanceof CCTimeTckMsg)
                    {
                    processCCTimeTckMsg(em);
                    }
                if (em instanceof StartExptReqMsg)
                    {
                    processStartExptReqMsg(em);
                    }
                }
            }
        }

    public CCNetwork getNetwork()
        {
        return (CCNetwork)m_activeAction;
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
            CCNode node = (CCNode)enm.nextElement();
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

    public void processCCAcceptOfferMsg(ExptMessage em)
        {
        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        int fromKeep = ((Integer)em.getArgs()[2]).intValue();
        int toKeep = ((Integer)em.getArgs()[3]).intValue();

        if (!getRoundRunning())
            return;

        CCNode fromNode = (CCNode)getNetwork().getNode(from);
        CCNode toNode = (CCNode)getNetwork().getNode(to);

        CCNodeResource fromCcnr = (CCNodeResource)fromNode.getExptData("CCNodeResource");
        CCNodeResource toCcnr = (CCNodeResource)toNode.getExptData("CCNodeResource");

        int tt = getNetwork().getPeriod().getTime() - getNetwork().getPeriod().getCurrentTime();

        if (toCcnr.getOffer(from) == null)
            {
            CCExchange fromOffer = new CCExchange(tt,-1,fromKeep,from,toKeep,to);
            fromOffer.setExchangeState(CCExchange.GREEN);
            toCcnr.addOffer(fromOffer);
            }
        else
            {
            CCExchange fromOffer = toCcnr.getOffer(from);
            fromOffer.getNode1().setResource(fromKeep);
            fromOffer.getNode2().setResource(toKeep);
            fromOffer.setExchangeState(CCExchange.GREEN);
            fromOffer.setTTime(tt);
            }
        fromCcnr.getOffer(to).setExchangeState(CCExchange.YELLOW);
        }
    public void processCCAfterFuzzyWindowMsg(ExptMessage em)
        {
        CCAfterFuzzyWindowMsg tmp = new CCAfterFuzzyWindowMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processCCAfterSanctionWindowMsg(ExptMessage em)
        {
        Enumeration enm = getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
// Remember to update the node resource earnings here.
            CCNode node = (CCNode)enm.nextElement();
            CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");

            for (int i=0;i<ns.getSanctions().size();i++)
                {
                CCNodeSanction sanction = (CCNodeSanction)ns.getSanctions().elementAt(i);
                CCNode toNode = (CCNode)getNetwork().getNode(sanction.getToNode());
                CCNodeResource nr = (CCNodeResource)toNode.getExptData("CCNodeResource");

                if (sanction.getMsg()) // Received a reward
                    {
                    nr.setBank(nr.getBank() + sanction.getRewardValue());
                    }
                else  // Received a sanction.
                    {
                    nr.setBank(nr.getBank() + sanction.getSanctionValue());
                    }
                }
            }

        CCAfterSanctionWindowMsg tmp = new CCAfterSanctionWindowMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processCCAfterTokenWindowMsg(ExptMessage em)
        {
  // Not finished yet.

        CCPeriod ccp = getNetwork().getPeriod();

        Enumeration enm = getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");
            for (int i=0;i<nt.getTokens().size();i++)
                {
                CCNodeToken token = (CCNodeToken)nt.getTokens().elementAt(i);
                CCNode toNode = (CCNode)getNetwork().getNode(token.getToNode());
                int percent = (int)(token.getPercent()*100);
                int ph; // Present have percent chance.
                if (getNetwork().getPeriod().getCurrentRound() == 0)
                    ph = -1;
                else
                    ph = (int)((token.getTokens()*percent*1.0)/(ccp.getCurrentRound()*1.0));
            
                if (ccp.getCurrentRound() == ccp.getRounds() - 1) // It's the last round compute the outcomes.
                    {
                    CCNodeResource nr = (CCNodeResource)toNode.getExptData("CCNodeResource");
                    int coinToss = (int)(100*Math.random());
                    if (coinToss <= ph)
                        {
                        nr.setBank(nr.getBank()+token.getYesValue());
                        }
                    else
                        {
                        nr.setBank(nr.getBank()+token.getNoValue());
                        }
                    }
                }
            }

        CCAfterTokenWindowMsg tmp = new CCAfterTokenWindowMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processCCCompleteOfferMsg(ExptMessage em)
        {
        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        int fromKeep = ((Integer)em.getArgs()[2]).intValue();
        int toKeep = ((Integer)em.getArgs()[3]).intValue();

        if (!getRoundRunning())
            return;

        CCNode fromNode = (CCNode)getNetwork().getNode(from);
        CCNode toNode = (CCNode)getNetwork().getNode(to);

        CCNodeResource fromCcnr = (CCNodeResource)fromNode.getExptData("CCNodeResource");
        CCNodeResource toCcnr = (CCNodeResource)toNode.getExptData("CCNodeResource");

        int tt = getNetwork().getPeriod().getTime() - getNetwork().getPeriod().getCurrentTime();

        fromCcnr.completeExchange(tt,0,fromKeep,from,toKeep,to);
        toCcnr.completeExchange(tt,0,toKeep,to,fromKeep,from);

        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)getNetwork().getNode(tmpEdge.getNode2());

            CCNodeResource exch1 = (CCNodeResource)n1.getExptData("CCNodeResource");
            CCNodeResource exch2 = (CCNodeResource)n2.getExptData("CCNodeResource");

            if ((exch1.isEdgeActive(tmpEdge)) && (exch2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                if (!tmpEdge.getCompleted())
                    {
                    exch1.removeOffer(n2.getID());
                    exch2.removeOffer(n1.getID());
                    }
                tmpEdge.setActive(false);
                }
            }
        }
    public void processCCEndRoundMsg(ExptMessage em)
        {
        /* Compute earnings totals here and update display labels */
        CCNode me = (CCNode)getNetwork().getExtraData("Me");
        double per = 0;  // Points earned round.
        Double pep = (Double)getNetwork().getExtraData("PntEarnedPeriod");
        Double pen = (Double)getNetwork().getExtraData("PntEarnedNetwork");
        Enumeration enm = getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            CCNodeResource ccnr = (CCNodeResource)node.getExptData("CCNodeResource");
                    
            per = ccnr.getAvailablePoints();
            ccnr.setBank(ccnr.getBank() + ccnr.getAvailablePoints());
            ccnr.setAvailablePoints(0);
            }

        getNetwork().setExtraData("PntEarnedRound",new Double(per));
        getNetwork().setExtraData("PntEarnedPeriod",new Double(per + pep.doubleValue()));
        getNetwork().setExtraData("PntEarnedNetwork",new Double(per + pen.doubleValue()));

        Object[] out_args = new Object[1];
        out_args[0] = new Double(per);
        CCEndRoundMsg tmp = new CCEndRoundMsg(out_args);
        m_SML.sendMessage(tmp);
        }
    public void processCCNodeFuzzyMsg(ExptMessage em)
        {
        if (getRoundRunning())
            return;

        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        int about = ((Integer)em.getArgs()[2]).intValue();
        boolean msg = ((Boolean)em.getArgs()[3]).booleanValue();

        CCNode fromNode = (CCNode)getNetwork().getNode(from);

        CCNodeFuzzies fromCcnf = (CCNodeFuzzies)fromNode.getExptData("CCNodeFuzzies");

        fromCcnf.fuzzySent(to,about,msg);
        
        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)getNetwork().getNode(tmpEdge.getNode2());

            CCNodeFuzzies nf1 = (CCNodeFuzzies)n1.getExptData("CCNodeFuzzies");
            CCNodeFuzzies nf2 = (CCNodeFuzzies)n2.getExptData("CCNodeFuzzies");

            if ((nf1.isEdgeActive(tmpEdge)) && (nf2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }
            }
        }
    public void processCCNodeFuzzyWindowMsg(ExptMessage em)
        {
        if (getRoundRunning())
            return;

        CCNode node = (CCNode)getNetwork().getExtraData("Me");
        CCNodeFuzzies nf = (CCNodeFuzzies)node.getExptData("CCNodeFuzzies");
        Enumeration enm = nf.getFuzzies().elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy fuzzy = (CCNodeFuzzy)enm.nextElement();

            Object[] out_args = new Object[4];
            out_args[0] = new Integer(node.getID()); // From
            out_args[1] = new Integer(fuzzy.getToNode()); // To
            out_args[2] = new Integer(fuzzy.getAboutNode()); // About
            out_args[3] = new Boolean(true);

            CCNodeFuzzyMsg tmp = new CCNodeFuzzyMsg(out_args);
            m_SML.sendMessage(tmp);
            }
        }
    public void processCCNodeSanctionMsg(ExptMessage em)
        {
        if (getRoundRunning())
            return;

        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        boolean msg = ((Boolean)em.getArgs()[2]).booleanValue();

        CCNode fromNode = (CCNode)getNetwork().getNode(from);
        CCNode toNode = (CCNode)getNetwork().getNode(to);

        CCNodeSanctions fromCcns = (CCNodeSanctions)fromNode.getExptData("CCNodeSanctions");
        CCNodeSanction fromSanction = (CCNodeSanction)fromCcns.getSanction(to);

        CCNodeResource toCcnr = (CCNodeResource)toNode.getExptData("CCNodeResource");

        fromCcns.sanctionSent(to,msg);

        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)getNetwork().getNode(tmpEdge.getNode2());

            CCNodeSanctions ns1 = (CCNodeSanctions)n1.getExptData("CCNodeSanctions");
            CCNodeSanctions ns2 = (CCNodeSanctions)n2.getExptData("CCNodeSanctions");

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
    public void processCCNodeSanctionWindowMsg(ExptMessage em)
        {
        if (getRoundRunning())
            return;

        CCNode node = (CCNode)getNetwork().getExtraData("Me");
        CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");
        Enumeration enm = ns.getSanctions().elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction sanction = (CCNodeSanction)enm.nextElement();

            Object[] out_args = new Object[3];
            out_args[0] = new Integer(node.getID()); // From
            out_args[1] = new Integer(sanction.getToNode()); // To
            out_args[2] = new Boolean(true);

            CCNodeSanctionMsg tmp = new CCNodeSanctionMsg(out_args);
            m_SML.sendMessage(tmp);
            }
        }
    public void processCCNodeTokenMsg(ExptMessage em)
        {
        if (getRoundRunning())
            return;

        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        boolean msg = ((Boolean)em.getArgs()[2]).booleanValue();

        CCNode fromNode = (CCNode)getNetwork().getNode(from);

        CCNodeTokens fromCcnt = (CCNodeTokens)fromNode.getExptData("CCNodeTokens");

        fromCcnt.tokenSent(to,msg);

        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)getNetwork().getNode(tmpEdge.getNode2());

            CCNodeTokens nt1 = (CCNodeTokens)n1.getExptData("CCNodeTokens");
            CCNodeTokens nt2 = (CCNodeTokens)n2.getExptData("CCNodeTokens");

            if ((nt1.isEdgeActive(tmpEdge)) && (nt2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }
            }
        }
    public void processCCNodeTokenWindowMsg(ExptMessage em)
        {
        if (getRoundRunning())
            return;

        CCNode node = (CCNode)getNetwork().getExtraData("Me");
        CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");
        Enumeration enm = nt.getTokens().elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken token = (CCNodeToken)enm.nextElement();

            Object[] out_args = new Object[3];
            out_args[0] = new Integer(node.getID()); // From
            out_args[1] = new Integer(token.getToNode()); // To
            out_args[2] = new Boolean(true);

            CCNodeTokenMsg tmp = new CCNodeTokenMsg(out_args);
            m_SML.sendMessage(tmp);
            }
        }
    public void processCCOfferMsg(ExptMessage em)
        {
        int from = ((Integer)em.getArgs()[0]).intValue();
        int to = ((Integer)em.getArgs()[1]).intValue();
        int fromKeep = ((Integer)em.getArgs()[2]).intValue();
        int toKeep = ((Integer)em.getArgs()[3]).intValue();

        Boolean rr = (Boolean)getNetwork().getExtraData("RoundRunning");

        if (!rr.booleanValue())
            return;

        CCNode fromNode = (CCNode)getNetwork().getNode(from);
        CCNode toNode = (CCNode)getNetwork().getNode(to);

        CCNodeResource fromCcnr = (CCNodeResource)fromNode.getExptData("CCNodeResource");
        CCNodeResource toCcnr = (CCNodeResource)toNode.getExptData("CCNodeResource");

        int tt = getNetwork().getPeriod().getTime() - getNetwork().getPeriod().getCurrentTime();

        if (toCcnr.getOffer(from) == null)
            {
            CCExchange fromOffer = new CCExchange(tt,-1,fromKeep,from,toKeep,to);
            fromOffer.setExchangeState(CCExchange.RED);
            toCcnr.addOffer(fromOffer);
            }
        else
            {
            CCExchange fromOffer = toCcnr.getOffer(from);
            fromOffer.getNode1().setResource(fromKeep);
            fromOffer.getNode2().setResource(toKeep);
            fromOffer.setExchangeState(CCExchange.RED);
            fromOffer.setTTime(tt);
            }
        if (fromCcnr.getOffer(to) != null)
            {
            fromCcnr.getOffer(to).setExchangeState(CCExchange.RED);
            }
        }
    public void processCCRoundWindowMsg(ExptMessage em)
        {
        CCRoundWindowMsg tmp = new CCRoundWindowMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processCCStartNetworkActionReqMsg(ExptMessage em)
        {
        m_activeAction = em.getArgs()[0];
        getNetwork().setExtraData("RoundRunning",new Boolean(false));
        getNetwork().getPeriod().setCurrentRound(0);

        getNetwork().getPeriod().setCurrentTime(getNetwork().getPeriod().getTime());
        getNetwork().setExtraData("Index",new Integer(m_user));
        getNetwork().setExtraData("RoundRunning",new Boolean(false));
        getNetwork().setExtraData("PntEarnedRound",new Double(0));
        getNetwork().setExtraData("PntEarnedPeriod",new Double(0));
        getNetwork().setExtraData("PntEarnedNetwork",new Double(0));

        initializeNetwork();
        m_flag1 = false;
    
        CCStartNetworkActionReqMsg tmp = new CCStartNetworkActionReqMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processCCStartRoundMsg(ExptMessage em)
        {
        getNetwork().setExtraData("RoundRunning",new Boolean(true));
        getNetwork().initializeNetwork();
        }
    public void processCCStopNetworkActionReqMsg(ExptMessage em)
        {
        getNetwork().setExtraData("RoundRunning",new Boolean(false));

        CCStopNetworkActionReqMsg tmp = new CCStopNetworkActionReqMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processCCStopRoundMsg(ExptMessage em)
        {
        getNetwork().setExtraData("RoundRunning",new Boolean(false));

        /* modifing earnings goes here */
        Enumeration enm = getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            CCNodeResource ccnr = (CCNodeResource)node.getExptData("CCNodeResource");
            ccnr.adjustEarnings();
            }

        CCStopRoundMsg tmp = new CCStopRoundMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processCCTimeTckMsg(ExptMessage em)
        {
        if (getRoundRunning())
            {
            CCTimeTckMsg tmp = new CCTimeTckMsg(null);
            m_SML.sendMessage(tmp);
            }
        }
    public void processStartExptReqMsg(ExptMessage em)
        {
        StartExptReqMsg tmp = new StartExptReqMsg(null);
        m_SML.sendMessage(tmp);
        }
    }
