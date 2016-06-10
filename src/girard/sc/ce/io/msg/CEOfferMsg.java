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
 * Sends an offer from one actor to another in a CE Network.  The message is initially
 * triggered by clicking the big arrow button attached to the CEClientDisplayArrow.  The
 * message is sent to the experimenter where it is checked to make sure it is still a
 * viable offer (is the edge still active and do both nodes still have the needed
 * resources to complete the exchange).  If viable the experimenter sends the information
 * to all the clients and obeservers letting them know about the offer.
 * <p>
 * <br> Started: 02-14-2003
 * <br> Modified: 02-17-2003
 * <br> Modified: 02-21-2003
 * <p>
 * @author Dudley Girard
 */

public class CEOfferMsg extends ExptMessage 
    { 
    public CEOfferMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (cw instanceof CENetworkActionClientWindow)
            {
            CENetworkActionClientWindow nacw = (CENetworkActionClientWindow)cw;
            CENetwork cen = (CENetwork)nacw.getExpApp().getActiveAction();

            Boolean rr = (Boolean)cen.getExtraData("RoundRunning");

            if ((!nacw.getExpApp().getExptRunning()) || (!rr.booleanValue()) || (nacw.getExpApp().getExptStopping()))
                return;

            int from = ((Integer)this.getArgs()[0]).intValue();
            int to = ((Integer)this.getArgs()[1]).intValue();
            CEResource fromObtain = (CEResource)this.getArgs()[2];
            CEResource toObtain = (CEResource)this.getArgs()[3];

            int tt = cen.getActivePeriod().getTime() - cen.getActivePeriod().getCurrentTime();

            CEEdge edge = null;
            Enumeration enm = cen.getEdgeList().elements();
            while(enm.hasMoreElements())
                {
                edge = (CEEdge)enm.nextElement();
                CEEdgeInteraction ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

                if ((from == edge.getNode1()) && (to == edge.getNode2()))
                    {
                    if (!edge.getActive())
                        {
                        return;
                        }
                    CEExchange cee = new CEExchange(tt,-1,fromObtain,toObtain);
                    ceei.addOffer(from,to,cee,"client","offer");
                    if (ceei.getActive() == 1)
                        {
                        nacw.updateProfitDisplay(edge);
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
                    ceei.addOffer(from,to,cee,"client","offer");
                    if (ceei.getActive() == 1)
                        {
                        nacw.updateProfitDisplay(edge);
                        }
                    break;
                    }
                }

            CENode myNode = (CENode)cen.getExtraData("Me");

            // Was it an offer sent to me by one of my neighbors?
            if (to == myNode.getID())
                {
                if (edge.getActive()) 
                    {
	              CEClientDisplayArrow arrow = nacw.getArrow();
                    if (arrow.getToNode() != null)
                        {
                        if (arrow.getToNode().getID() == from)
                            {
                            arrow.updateBubbleButton();
                            }
                        }
                    }
                }
            
            nacw.repaint();
            nacw.validate();  // Make sure any changes are hopefully displayed properly.
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - CEOfferMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CE Offer Message");
// System.err.flush();
    
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
                        err_args[1] = new String("CEOfferMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CEOfferMsg(args));
                    ec.sendToAllObservers(new CEOfferMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = args;
                    ec.addServerMessage(new CEOfferMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CEOfferMsg");
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

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CEEdge edge = (CEEdge)enm.nextElement();
            CEEdgeInteraction ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,naew.getPresentTime(),fromObtain,toObtain);
                ceei.addOffer(from,to,cee,"experimenter","offer");
                if (ceei.getActive() == 1)
                    {
                    naew.updateProfitDisplay(edge);
                    }
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,naew.getPresentTime(),toObtain,fromObtain);
                ceei.addOffer(from,to,cee,"experimenter","offer");
                if (ceei.getActive() == 1)
                    {
                    naew.updateProfitDisplay(edge);
                    }
                break;
                }
            }

        naew.repaint();
 
        /* update data output here */
        
        int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
        int cp = naew.getNetwork().getCurrentPeriod() + 1;

        CEOfferOutputObject data = new CEOfferOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cp,cr,from,to,fromObtain,toObtain,"Offer",tt, naew.getPresentTime());

        Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
        outData.addElement(data);

        Object[] out_args = args;
        CEOfferMsg tmp = new CEOfferMsg(out_args);
        naew.getSML().sendMessage(tmp);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CEOfferMsg");
            return;
            }

        Object[] args = this.getArgs();

        CENetworkActionObserverWindow naow = (CENetworkActionObserverWindow)ow;
        
        Boolean rr = (Boolean)naow.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        CENetwork cen = (CENetwork)naow.getExpApp().getActiveAction();

        int from = ((Integer)this.getArgs()[0]).intValue();
        int to = ((Integer)this.getArgs()[1]).intValue();
        CEResource fromObtain = (CEResource)this.getArgs()[2];
        CEResource toObtain = (CEResource)this.getArgs()[3];

        int tt = cen.getActivePeriod().getTime() - cen.getActivePeriod().getCurrentTime();

        CEEdge edge = null;
        Enumeration enm = cen.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            edge = (CEEdge)enm.nextElement();
            CEEdgeInteraction ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                CEExchange cee = new CEExchange(tt,-1,fromObtain,toObtain);
                ceei.addOffer(from,to,cee,"observer","offer");
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
                ceei.addOffer(from,to,cee,"observer","offer");
                if (ceei.getActive() == 1)
                    {
                    naow.updateProfitDisplay(edge);
                    }
                break;
                }
            }

        naow.repaint();
        }
    }