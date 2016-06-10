package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.awt.BENetworkActionObserverWindow;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BEEdgeDisplay;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeSanction;
import girard.sc.be.obj.BENodeSanctions;
import girard.sc.be.obj.BESanctionsOutputObject;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This message signifies a sanction or reward being sent between two subjects.
 * <p>
 * <br> Started: 09-19-2002
 * <p>
 * @author Dudley Girard
 */

public class BENodeSanctionMsg extends ExptMessage 
    { 
    public BENodeSanctionMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (!(cw instanceof BENetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - BENodeSanctionMsg");
            return;
            }

        BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;
        BENetwork ben = (BENetwork)nacw.getExpApp().getActiveAction();

        Boolean rr = (Boolean)ben.getExtraData("RoundRunning");

        if ((!nacw.getExpApp().getExptRunning()) || (rr.booleanValue()) || (nacw.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)this.getArgs()[0]).intValue();
        int to = ((Integer)this.getArgs()[1]).intValue();
        boolean msg = ((Boolean)this.getArgs()[2]).booleanValue();

        BENode fromNode = (BENode)ben.getNode(from);
        BENode toNode = (BENode)ben.getNode(to);

        BENodeSanctions fromBens = (BENodeSanctions)fromNode.getExptData("BENodeSanctions");
        BENodeSanction fromSanction = (BENodeSanction)fromBens.getSanction(to);
        BENodeSanctions toBens = (BENodeSanctions)toNode.getExptData("BENodeSanctions");

        fromBens.sanctionSent(to,msg);

        Enumeration enm = ben.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeDisplay beed = (BEEdgeDisplay)edge.getExptData("BEEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    beed.setN1Display(0,"");
                    beed.setN1Display(1,""+fromSanction.getRewardValue());
                    beed.setExchangeState1(BEEdgeDisplay.GREEN);
                    beed.setN1Display(2,"DOT");
                    }
                else
                    {
                    beed.setN1Display(0,"");
                    beed.setN1Display(1,""+fromSanction.getSanctionValue());
                    if (fromSanction.getSanctionValue() != 0)
                        beed.setExchangeState1(BEEdgeDisplay.RED);
                    else
                        beed.setExchangeState1(BEEdgeDisplay.BLACK);
                    beed.setN1Display(2,"DOT");
                    }

                if (!toBens.hasSanction(from))
                    {
                    beed.setN2Display(0,"");
                    beed.setN2Display(1,"");
                    beed.setN2Display(2,"");
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
                if (msg)
                    {
                    beed.setN2Display(0,"");
                    beed.setN2Display(1,""+fromSanction.getRewardValue());
                    beed.setExchangeState2(BEEdgeDisplay.GREEN);
                    beed.setN2Display(2,"DOT");
                    }
                else
                    {
                    beed.setN2Display(0,"");
                    beed.setN2Display(1,""+fromSanction.getSanctionValue());
                    if (fromSanction.getSanctionValue() != 0)
                        beed.setExchangeState2(BEEdgeDisplay.RED);
                    else
                        beed.setExchangeState2(BEEdgeDisplay.BLACK);
                    beed.setN2Display(2,"DOT");
                    }

                if (!toBens.hasSanction(from))
                    {
                    beed.setN1Display(0,"");
                    beed.setN1Display(1,"");
                    beed.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toBens.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            }

        enm = ben.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            BENode n1 = (BENode)ben.getNode(tmpEdge.getNode1());
            BENode n2 = (BENode)ben.getNode(tmpEdge.getNode2());

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

        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Node Sanction Message");
    
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
                        err_args[1] = new String("BENodeSanctionMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BENodeSanctionMsg(args));
                    ec.sendToAllObservers(new BENodeSanctionMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    ec.addServerMessage(new BENodeSanctionMsg(args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BENodeSanctionMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Object[] args = this.getArgs();

        BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;
        BENetwork ben = naew.getNetwork();
        Boolean rr = (Boolean)ben.getExtraData("RoundRunning");
        
        if ((rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        boolean msg = ((Boolean)args[2]).booleanValue();

        BENode fromNode = (BENode)ben.getNode(from);
        BENode toNode = (BENode)ben.getNode(to);

        BENodeSanctions fromBens = (BENodeSanctions)fromNode.getExptData("BENodeSanctions");
        BENodeSanction fromSanction = (BENodeSanction)fromBens.getSanction(to);
        BENodeSanctions toBens = (BENodeSanctions)toNode.getExptData("BENodeSanctions");
        
        fromBens.sanctionSent(to,msg);

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeDisplay beed = (BEEdgeDisplay)edge.getExptData("BEEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    beed.setN1Display(0,""+fromSanction.getRewardValue());
                    beed.setExchangeState1(BEEdgeDisplay.GREEN);
                    beed.setN1Display(1,"R");
                    beed.setN1Display(2,"*");
                    }
                else
                    {
                    beed.setN1Display(0,""+fromSanction.getSanctionValue());
                    beed.setExchangeState1(BEEdgeDisplay.RED);
                    beed.setN1Display(1,"S");
                    beed.setN1Display(2,"*");
                    }

                if (!toBens.hasSanction(from))
                    {
                    beed.setN2Display(0,"");
                    beed.setN2Display(1,"");
                    beed.setN2Display(2,"");
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
                if (msg)
                    {
                    beed.setN2Display(0,""+fromSanction.getRewardValue());
                    beed.setExchangeState2(BEEdgeDisplay.GREEN);
                    beed.setN2Display(1,"R");
                    beed.setN2Display(2,"*");
                    }
                else
                    {
                    beed.setN2Display(0,""+fromSanction.getSanctionValue());
                    beed.setExchangeState2(BEEdgeDisplay.RED);
                    beed.setN2Display(1,"S");
                    beed.setN2Display(2,"*");
                    }

                if (!toBens.hasSanction(from))
                    {
                    beed.setN1Display(0,"");
                    beed.setN1Display(1,"");
                    beed.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toBens.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            }

      /* update data output here */
        int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
        int cp = naew.getNetwork().getCurrentPeriod() + 1;

        BESanctionsOutputObject data = null;
        if (msg)
            data = new BESanctionsOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cp,cr,from,to,fromSanction.getRewardValue(),msg);
        else
            data = new BESanctionsOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cp,cr,from,to,fromSanction.getSanctionValue(),msg);

        Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
        outData.addElement(data);
     /* end update for data output */

   // update active settings for edges.

        enm = naew.getNetwork().getEdgeList().elements();
        boolean flag = false;
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            BENode n1 = (BENode)naew.getNetwork().getNode(tmpEdge.getNode1());
            BENode n2 = (BENode)naew.getNetwork().getNode(tmpEdge.getNode2());

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

            if (tmpEdge.getActive())
                flag = true;
            }

        naew.repaint();

        Object[] out_args = args;
        BENodeSanctionMsg tmp = new BENodeSanctionMsg(out_args);
        naew.getSML().sendMessage(tmp);

        if (flag)
            return;

        BEAfterSanctionWindowMsg tmp2 = new BEAfterSanctionWindowMsg(null);
        naew.getSML().sendMessage(tmp2);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof BENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - BENodeSanctionMsg");
            return;
            }

        Object[] args = this.getArgs();

        BENetworkActionObserverWindow naow = (BENetworkActionObserverWindow)ow;
        BENetwork ben = naow.getNetwork();
        Boolean rr = (Boolean)ben.getExtraData("RoundRunning");
        
        if ((rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        boolean msg = ((Boolean)args[2]).booleanValue();

        BENode fromNode = (BENode)ben.getNode(from);
        BENode toNode = (BENode)ben.getNode(to);

        BENodeSanctions fromBens = (BENodeSanctions)fromNode.getExptData("BENodeSanctions");
        BENodeSanction fromSanction = (BENodeSanction)fromBens.getSanction(to);
        BENodeSanctions toBens = (BENodeSanctions)toNode.getExptData("BENodeSanctions");

        fromBens.sanctionSent(to,msg);

        Enumeration enm = naow.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeDisplay beed = (BEEdgeDisplay)edge.getExptData("BEEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    beed.setN1Display(0,""+fromSanction.getRewardValue());
                    beed.setExchangeState1(BEEdgeDisplay.GREEN);
                    beed.setN1Display(1,"R");
                    beed.setN1Display(2,"*");
                    }
                else
                    {
                    beed.setN1Display(0,""+fromSanction.getSanctionValue());
                    beed.setExchangeState1(BEEdgeDisplay.RED);
                    beed.setN1Display(1,"S");
                    beed.setN1Display(2,"*");
                    }

                if (!toBens.hasSanction(from))
                    {
                    beed.setN2Display(0,"");
                    beed.setN2Display(1,"");
                    beed.setN2Display(2,"");
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
                if (msg)
                    {
                    beed.setN2Display(0,""+fromSanction.getRewardValue());
                    beed.setExchangeState2(BEEdgeDisplay.GREEN);
                    beed.setN2Display(1,"R");
                    beed.setN2Display(2,"*");
                    }
                else
                    {
                    beed.setN2Display(0,""+fromSanction.getSanctionValue());
                    beed.setExchangeState2(BEEdgeDisplay.RED);
                    beed.setN2Display(1,"S");
                    beed.setN2Display(2,"*");
                    }

                if (!toBens.hasSanction(from))
                    {
                    beed.setN1Display(0,"");
                    beed.setN1Display(1,"");
                    beed.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toBens.canSendSanction(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            }

        enm = naow.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            BENode n1 = (BENode)naow.getNetwork().getNode(tmpEdge.getNode1());
            BENode n2 = (BENode)naow.getNetwork().getNode(tmpEdge.getNode2());

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

        naow.repaint();
        }
    }