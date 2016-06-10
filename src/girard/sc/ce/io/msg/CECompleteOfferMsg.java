package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CEClientDisplayArrow;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CEExchange;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CEOfferOutputObject;
import girard.sc.ce.obj.CEResource;
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
 * This message signifies a completed exchange between two subjects.
 * <p>
 * <br> Started: 02-17-2003
 * <p>
 * @author Dudley Girard
 */

public class CECompleteOfferMsg extends ExptMessage 
    { 
    public CECompleteOfferMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (!(cw instanceof CENetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - CECompleteOfferMsg");
            return;
            }

        Object[] args = this.getArgs();

        CENetworkActionClientWindow nacw = (CENetworkActionClientWindow)cw;
        CENetwork cen = (CENetwork)nacw.getExpApp().getActiveAction();

        Boolean rr = (Boolean)cen.getExtraData("RoundRunning");
        CENode myNode = (CENode)cen.getExtraData("Me");

        if ((!nacw.getExpApp().getExptRunning()) || (!rr.booleanValue()) || (nacw.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        CEResource fromObtain = (CEResource)args[2];
        CEResource toObtain = (CEResource)args[3];

        int tt = cen.getActivePeriod().getTime() - cen.getActivePeriod().getCurrentTime();

  // Need to make sure the offer is still valid.
        CENode fromNode = (CENode)cen.getNode(from);
        CENode toNode = (CENode)cen.getNode(to);

        CEExchange ceeCheck = new CEExchange(tt,-1,fromObtain,toObtain);
        if (!ceeCheck.isValidExchange(fromNode,toNode))
            return;

        CEEdge edge = null;
        CEEdgeInteraction ceei = null;
        Enumeration enm = cen.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            edge = (CEEdge)enm.nextElement();
            ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,-1,fromObtain,toObtain);
                ceei.completeExchange(from,to,cee,"client");
                if (ceei.getActive() == 1)
                    {
                    nacw.updateProfitDisplay(edge);
                    }
		fromNode.setExchanged();
		toNode.setExchanged();
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,-1,toObtain,fromObtain);
                ceei.completeExchange(from,to,cee,"client");
                if (ceei.getActive() == 1)
                    {
                    nacw.updateProfitDisplay(edge);
                    }
		fromNode.setExchanged();
		toNode.setExchanged();
                break;
                }
            }

   // update active settings for edges.
        enm = cen.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge tmpEdge = (CEEdge)enm.nextElement();

            CEEdgeInteraction tmpCeei = (CEEdgeInteraction)tmpEdge.getExptData("CEEdgeInteraction");

            tmpCeei.updateActiveState();
            if (tmpEdge.getActive())
                 tmpCeei.updateOffers();
            }

        // Is it a deal completed along one of my relations and is that display arrow active?
        CEClientDisplayArrow arrow = nacw.getArrow();
        if (arrow.getToNode() != null)
            {
            nacw.updatePartnerCmdPanel(arrow.getToNode());
            if ((to == arrow.getToNode().getID()) && (from == myNode.getID()))
                {
                arrow.setEdge(edge);
                }
            if ((to == myNode.getID()) && (from == arrow.getToNode().getID()))
                {
                arrow.setEdge(edge);
                }
            }
        nacw.updateMyCmdPanel(myNode);
        nacw.repaint();
        nacw.validate();  // Make sure any changes are hopefully displayed properly.
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CE Complete Offer Message");
    
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
                        err_args[1] = new String("CECompleteOfferMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CECompleteOfferMsg(args));
                    ec.sendToAllObservers(new CECompleteOfferMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = args;
                    ec.addServerMessage(new CECompleteOfferMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BECompleteOfferMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Object[] args = this.getArgs();

        CENetworkActionExperimenterWindow naew = (CENetworkActionExperimenterWindow)ew;
       
        Boolean rr = (Boolean)naew.getNetwork().getExtraData("RoundRunning");
        
        if ((!rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        CEResource fromObtain = (CEResource)args[2];
        CEResource toObtain = (CEResource)args[3];

        int tt = naew.getNetwork().getActivePeriod().getTime() - naew.getNetwork().getActivePeriod().getCurrentTime();

  // Need to make sure the offer is still valid.
        CENode fromNode = (CENode)naew.getNetwork().getNode(from);
        CENode toNode = (CENode)naew.getNetwork().getNode(to);

        CEExchange ceeCheck = new CEExchange(tt,naew.getPresentTime(),fromObtain,toObtain);
        if (!ceeCheck.isValidExchange(fromNode,toNode))
            return;

        CEEdge edge = null;
        CEEdgeInteraction ceei = null;
        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            edge = (CEEdge)enm.nextElement();
            ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,naew.getPresentTime(),fromObtain,toObtain);
                ceei.completeExchange(from,to,cee,"experimenter");
                if (ceei.getActive() == 1)
                    {
                    naew.updateProfitDisplay(edge);
                    }
		fromNode.setExchanged();
		toNode.setExchanged();
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,naew.getPresentTime(),toObtain,fromObtain);
                ceei.completeExchange(from,to,cee,"experimenter");
                if (ceei.getActive() == 1)
                    {
                    naew.updateProfitDisplay(edge);
                    }
		fromNode.setExchanged();
		toNode.setExchanged();
                break;
                }
            }

      /* update data output here */
        int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
        int cp = naew.getNetwork().getCurrentPeriod() + 1;

        CEOfferOutputObject data = new CEOfferOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cp,cr,from,to,fromObtain,toObtain,"Complete",tt, naew.getPresentTime());

        Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
        outData.addElement(data);
     /* end update for data output */

   // update active settings for edges.

        enm = naew.getNetwork().getEdgeList().elements();
        boolean flag = false;
        while (enm.hasMoreElements())
            {
            CEEdge tmpEdge = (CEEdge)enm.nextElement();

            CEEdgeInteraction tmpCeei = (CEEdgeInteraction)tmpEdge.getExptData("CEEdgeInteraction");

            tmpCeei.updateActiveState();
            if (tmpEdge.getActive())
                 tmpCeei.updateOffers();

            if (tmpEdge.getActive()) 
                flag = true;
            else if (tmpCeei.isEdgeStillUsable())
                {
                flag = true;
                }
            }

        naew.repaint();

        Object[] out_args = args;
        CECompleteOfferMsg tmp = new CECompleteOfferMsg(out_args);
        naew.getSML().sendMessage(tmp);

        String exchType = (String)naew.getNetwork().getExtraData("TimingMethod");

        if ((exchType.equals("Non-Simultaneous")) && (ceei.isEdgeStillUsable()))
            {
            Object[] out_args2 = new Object[5];
            out_args2[0] = new Integer(from);
            out_args2[1] = new Integer(to);
            out_args2[2] = new Integer(cr);
            out_args2[3] = new Integer(cp);
            out_args2[4] = new Integer(naew.getExpApp().getActionIndex());
            CEReactivateEdgeMsg tmp2 = new CEReactivateEdgeMsg(out_args2);
            naew.getSML().addListenRequest(tmp2,5000,1);
            }

        if (flag)
            {
            return;
            }

        naew.getNetwork().setExtraData("RoundRunning",new Boolean(false));
    /* This is needed to cover for TCK messages */
        
         boolean[] tick = (boolean[])naew.getNetwork().getExtraData("TimeReady");
         for (int x=0;x<tick.length;x++)
             {
             tick[x] = false;
             }

        CEStopRoundMsg tmp2 = new CEStopRoundMsg(null);
        ew.getSML().sendMessage(tmp2);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CECompleteOfferMsg");
            return;
            }

        Object[] args = this.getArgs();

        CENetworkActionObserverWindow naow = (CENetworkActionObserverWindow)ow;
        CENetwork cen = (CENetwork)naow.getExpApp().getActiveAction();
        
        Boolean rr = (Boolean)naow.getNetwork().getExtraData("RoundRunning");
        
        if ((!rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        CEResource fromObtain = (CEResource)args[2];
        CEResource toObtain = (CEResource)args[3];

        int tt = cen.getActivePeriod().getTime() - cen.getActivePeriod().getCurrentTime();

  // Need to make sure the offer is still valid.
        CENode fromNode = (CENode)cen.getNode(from);
        CENode toNode = (CENode)cen.getNode(to);

        CEExchange ceeCheck = new CEExchange(tt,-1,fromObtain,toObtain);
        if (!ceeCheck.isValidExchange(fromNode,toNode))
            return;

        CEEdge edge = null;
        CEEdgeInteraction ceei = null;
        Enumeration enm = cen.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            edge = (CEEdge)enm.nextElement();
            ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,-1,fromObtain,toObtain);
                ceei.completeExchange(from,to,cee,"observer");
                if (ceei.getActive() == 1)
                    {
                    naow.updateProfitDisplay(edge);
                    }
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,-1,toObtain,fromObtain);
                ceei.completeExchange(from,to,cee,"observer");
                if (ceei.getActive() == 1)
                    {
                    naow.updateProfitDisplay(edge);
                    }
                break;
                }
            }

   // update active settings for edges.
        enm = cen.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge tmpEdge = (CEEdge)enm.nextElement();

            CEEdgeInteraction tmpCeei = (CEEdgeInteraction)tmpEdge.getExptData("CEEdgeInteraction");

            tmpCeei.updateActiveState();
            if (tmpEdge.getActive())
                 tmpCeei.updateOffers();
            }

        naow.repaint();
        }
    }
