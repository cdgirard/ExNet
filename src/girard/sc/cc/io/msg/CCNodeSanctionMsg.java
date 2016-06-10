package girard.sc.cc.io.msg;

/* This message signifies a sanction or reward being sent between two subjects.

   Author: Dudley Girard
   Started: 7-9-2001
   Modified: 7-25-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCEdgeDisplay;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeSanction;
import girard.sc.cc.obj.CCNodeSanctions;
import girard.sc.cc.obj.CCSanctionsOutputObject;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Vector;

public class CCNodeSanctionMsg extends ExptMessage 
    { 
    public CCNodeSanctionMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (!(cw instanceof CCNetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - CCNodeSanctionMsg");
            return;
            }

        CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;
        CCNetwork ccn = (CCNetwork)nacw.getExpApp().getActiveAction();

        Boolean rr = (Boolean)ccn.getExtraData("RoundRunning");

        if ((!nacw.getExpApp().getExptRunning()) || (rr.booleanValue()) || (nacw.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)this.getArgs()[0]).intValue();
        int to = ((Integer)this.getArgs()[1]).intValue();
        boolean msg = ((Boolean)this.getArgs()[2]).booleanValue();

        CCNode fromNode = (CCNode)ccn.getNode(from);
        CCNode toNode = (CCNode)ccn.getNode(to);

        CCNodeSanctions fromCcns = (CCNodeSanctions)fromNode.getExptData("CCNodeSanctions");
        CCNodeSanction fromSanction = (CCNodeSanction)fromCcns.getSanction(to);
        CCNodeSanctions toCcns = (CCNodeSanctions)toNode.getExptData("CCNodeSanctions");

        fromCcns.sanctionSent(to,msg);

        Enumeration enm = ccn.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    cced.setN1Display(0,""+fromSanction.getRewardValue());
                    cced.setExchangeState1(CCEdgeDisplay.GREEN);
                    if (fromSanction.getRewardValue() != 0)
                        cced.setN1Display(1,"R");
                    else
                        cced.setN1Display(1,"-");
                    cced.setN1Display(2,"DOT");
                    }
                else
                    {
                    cced.setN1Display(0,""+fromSanction.getSanctionValue());
                    cced.setExchangeState1(CCEdgeDisplay.RED);
                    if (fromSanction.getSanctionValue() != 0)
                        cced.setN1Display(1,"F");
                    else
                        cced.setN1Display(1,"-");
                    cced.setN1Display(2,"DOT");
                    }

                if (!toCcns.hasSanction(from))
                    {
                    cced.setN2Display(0,"");
                    cced.setN2Display(1,"");
                    cced.setN2Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcns.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (msg)
                    {
                    cced.setN2Display(0,""+fromSanction.getRewardValue());
                    cced.setExchangeState2(CCEdgeDisplay.GREEN);
                    if (fromSanction.getRewardValue() != 0)
                        cced.setN2Display(1,"R");
                    else
                        cced.setN2Display(1,"-");
                    cced.setN2Display(2,"DOT");
                    }
                else
                    {
                    cced.setN2Display(0,""+fromSanction.getSanctionValue());
                    cced.setExchangeState2(CCEdgeDisplay.RED);
                    if (fromSanction.getSanctionValue() != 0)
                        cced.setN2Display(1,"F");
                    else
                        cced.setN2Display(1,"-");
                    cced.setN2Display(2,"DOT");
                    }

                if (!toCcns.hasSanction(from))
                    {
                    cced.setN1Display(0,"");
                    cced.setN1Display(1,"");
                    cced.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcns.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            }

        enm = ccn.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)ccn.getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)ccn.getNode(tmpEdge.getNode2());

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

        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Node Sanction Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    if (!ec.allRegistered())
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Least one user not registered.");
                        err_args[1] = new String("CCNodeSanctionMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCNodeSanctionMsg(args));
                    ec.sendToAllObservers(new CCNodeSanctionMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    ec.addServerMessage(new CCNodeSanctionMsg(args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCNodeSanctionMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Object[] args = this.getArgs();

        CCNetworkActionExperimenterWindow naew = (CCNetworkActionExperimenterWindow)ew;
        CCNetwork ccn = naew.getNetwork();
        Boolean rr = (Boolean)ccn.getExtraData("RoundRunning");
        
        if ((rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        boolean msg = ((Boolean)args[2]).booleanValue();

        CCNode fromNode = (CCNode)ccn.getNode(from);
        CCNode toNode = (CCNode)ccn.getNode(to);

        CCNodeSanctions fromCcns = (CCNodeSanctions)fromNode.getExptData("CCNodeSanctions");
        CCNodeSanction fromSanction = (CCNodeSanction)fromCcns.getSanction(to);
        CCNodeSanctions toCcns = (CCNodeSanctions)toNode.getExptData("CCNodeSanctions");
        
        fromCcns.sanctionSent(to,msg);

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    cced.setN1Display(0,""+fromSanction.getRewardValue());
                    cced.setExchangeState1(CCEdgeDisplay.GREEN);
                    cced.setN1Display(1,"R");
                    cced.setN1Display(2,"*");
                    }
                else
                    {
                    cced.setN1Display(0,""+fromSanction.getSanctionValue());
                    cced.setExchangeState1(CCEdgeDisplay.RED);
                    cced.setN1Display(1,"S");
                    cced.setN1Display(2,"*");
                    }

                if (!toCcns.hasSanction(from))
                    {
                    cced.setN2Display(0,"");
                    cced.setN2Display(1,"");
                    cced.setN2Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcns.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (msg)
                    {
                    cced.setN2Display(0,""+fromSanction.getRewardValue());
                    cced.setExchangeState2(CCEdgeDisplay.GREEN);
                    cced.setN2Display(1,"R");
                    cced.setN2Display(2,"*");
                    }
                else
                    {
                    cced.setN2Display(0,""+fromSanction.getSanctionValue());
                    cced.setExchangeState2(CCEdgeDisplay.RED);
                    cced.setN2Display(1,"S");
                    cced.setN2Display(2,"*");
                    }

                if (!toCcns.hasSanction(from))
                    {
                    cced.setN1Display(0,"");
                    cced.setN1Display(1,"");
                    cced.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcns.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            }

      /* update data output here */
        int cr = naew.getNetwork().getPeriod().getCurrentRound();

        CCSanctionsOutputObject data = null;
        if (msg)
            data = new CCSanctionsOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cr,from,to,fromSanction.getRewardValue(),msg);
        else
            data = new CCSanctionsOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cr,from,to,fromSanction.getSanctionValue(),msg);

        Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
        outData.addElement(data);
     /* end update for data output */

   // update active settings for edges.

        enm = naew.getNetwork().getEdgeList().elements();
        boolean flag = false;
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)naew.getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)naew.getNetwork().getNode(tmpEdge.getNode2());

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

            if (tmpEdge.getActive())
                flag = true;
            }

        naew.repaint();

        Object[] out_args = args;
        CCNodeSanctionMsg tmp = new CCNodeSanctionMsg(out_args);
        naew.getSML().sendMessage(tmp);

        if (flag)
            return;

        // Write the initial final outcome results to data file here?

        // ESApp.ESData.ComputeFinalResults();  Work on this when we start worrying about data.

        CCAfterSanctionWindowMsg tmp2 = new CCAfterSanctionWindowMsg(null);
        naew.getSML().sendMessage(tmp2);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CCNetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CCNodeSanctionMsg");
            return;
            }

        Object[] args = this.getArgs();

        CCNetworkActionObserverWindow naow = (CCNetworkActionObserverWindow)ow;
        CCNetwork ccn = naow.getNetwork();
        Boolean rr = (Boolean)ccn.getExtraData("RoundRunning");
        
        if ((rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        boolean msg = ((Boolean)args[2]).booleanValue();

        CCNode fromNode = (CCNode)ccn.getNode(from);
        CCNode toNode = (CCNode)ccn.getNode(to);

        CCNodeSanctions fromCcns = (CCNodeSanctions)fromNode.getExptData("CCNodeSanctions");
        CCNodeSanction fromSanction = (CCNodeSanction)fromCcns.getSanction(to);
        CCNodeSanctions toCcns = (CCNodeSanctions)toNode.getExptData("CCNodeSanctions");

        fromCcns.sanctionSent(to,msg);

        Enumeration enm = naow.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    cced.setN1Display(0,""+fromSanction.getRewardValue());
                    cced.setExchangeState1(CCEdgeDisplay.GREEN);
                    cced.setN1Display(1,"R");
                    cced.setN1Display(2,"*");
                    }
                else
                    {
                    cced.setN1Display(0,""+fromSanction.getSanctionValue());
                    cced.setExchangeState1(CCEdgeDisplay.RED);
                    cced.setN1Display(1,"S");
                    cced.setN1Display(2,"*");
                    }

                if (!toCcns.hasSanction(from))
                    {
                    cced.setN2Display(0,"");
                    cced.setN2Display(1,"");
                    cced.setN2Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcns.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (msg)
                    {
                    cced.setN2Display(0,""+fromSanction.getRewardValue());
                    cced.setExchangeState2(CCEdgeDisplay.GREEN);
                    cced.setN2Display(1,"R");
                    cced.setN2Display(2,"*");
                    }
                else
                    {
                    cced.setN2Display(0,""+fromSanction.getSanctionValue());
                    cced.setExchangeState2(CCEdgeDisplay.RED);
                    cced.setN2Display(1,"S");
                    cced.setN2Display(2,"*");
                    }

                if (!toCcns.hasSanction(from))
                    {
                    cced.setN1Display(0,"");
                    cced.setN1Display(1,"");
                    cced.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcns.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            }

        enm = naow.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)naow.getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)naow.getNetwork().getNode(tmpEdge.getNode2());

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

        naow.repaint();
        }
    }