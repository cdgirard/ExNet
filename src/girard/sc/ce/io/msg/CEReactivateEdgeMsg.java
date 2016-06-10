package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CEClientDisplayArrow;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;

/**
 * Reactivates an edge where an exchange has been completed so it
 * can be used again.
 * <p>
 * <br> Started: 02-19-2003
 * <p>
 * @author Dudley Girard
 */

public class CEReactivateEdgeMsg extends ExptMessage 
    { 
    public CEReactivateEdgeMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (!(cw instanceof CENetworkActionClientWindow))
            {
            return;
            }

        CENetworkActionClientWindow nacw = (CENetworkActionClientWindow)cw;
        CENetwork cen = (CENetwork)nacw.getExpApp().getActiveAction();

        int from = ((Integer)this.getArgs()[0]).intValue();
        int to = ((Integer)this.getArgs()[1]).intValue();
// Current Round
        int cr = ((Integer)this.getArgs()[2]).intValue();
// Current Period
        int cp = ((Integer)this.getArgs()[3]).intValue();
// Action Index
        int ai = ((Integer)this.getArgs()[4]).intValue();

        Boolean rr = (Boolean)cen.getExtraData("RoundRunning");

        if ((!nacw.getExpApp().getExptRunning()) || (!rr.booleanValue()) || (nacw.getExpApp().getExptStopping()))
            return;

        int cr2 = cen.getActivePeriod().getCurrentRound() + 1;
        int cp2 = cen.getCurrentPeriod() + 1;
        int ai2 = ((Integer)cen.getExtraData("ActionIndex")).intValue();
System.err.println("AI: "+ai2+" "+ai);
        if ((cr != cr2) || (cp != cp2) || (ai != ai2))
            {
            return;
            }
System.err.println("HERE");
        CEEdge edge = null;
        Enumeration enm = cen.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            edge = (CEEdge)enm.nextElement();
            CEEdgeInteraction ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (ceei.isEdgeStillUsable())
                    {
                    ceei.reactivateEdge();
                    } 
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (ceei.isEdgeStillUsable())
                    {
                    ceei.reactivateEdge();
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
                        arrow.setEdge(edge);
                        arrow.updateBubbleButton();
                        }
                    }
                }
            }
        nacw.repaint();
        nacw.validate();  // Make sure any changes are hopefully displayed properly.
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CE Reactivate Edge Message");
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
                        err_args[1] = new String("CEReactivateEdgeMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CEReactivateEdgeMsg(args));
                    ec.sendToAllObservers(new CEReactivateEdgeMsg(args));
                    ec.addServerMessage(new CEReactivateEdgeMsg(args));
                    return null; 
                    }
                else
                    {
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CEReactivateEdgeMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Object[] args = this.getArgs();

        if (!(ew instanceof CENetworkActionExperimenterWindow))
            return;

        CENetworkActionExperimenterWindow naew = (CENetworkActionExperimenterWindow)ew;
        
        Boolean rr = (Boolean)naew.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)this.getArgs()[0]).intValue();
        int to = ((Integer)this.getArgs()[1]).intValue();
// Current Round
        int cr = ((Integer)this.getArgs()[2]).intValue();
// Current Period
        int cp = ((Integer)this.getArgs()[3]).intValue();
// Action Index
        int ai = ((Integer)this.getArgs()[4]).intValue();

        int cr2 = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
        int cp2 = naew.getNetwork().getCurrentPeriod() + 1;
        int ai2 = naew.getExpApp().getActionIndex();

        if ((cr != cr2) || (cp != cp2) || (ai != ai2))
            {
            return;
            }

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CEEdge edge = (CEEdge)enm.nextElement();
            CEEdgeInteraction ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (ceei.isEdgeStillUsable())
                    {
                    ceei.reactivateEdge();
                    } 
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (ceei.isEdgeStillUsable())
                    {
                    ceei.reactivateEdge();
                    }

                break;
                }
            }

        naew.repaint();
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CEReactivateEdgeMsg");
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
// Current Round
        int cr = ((Integer)this.getArgs()[2]).intValue();
// Current Period
        int cp = ((Integer)this.getArgs()[3]).intValue();
// Action Index
        int ai = ((Integer)this.getArgs()[4]).intValue();

        int cr2 = cen.getActivePeriod().getCurrentRound() + 1;
        int cp2 = cen.getCurrentPeriod() + 1;
        int ai2 = ((Integer)cen.getExtraData("ActionIndex")).intValue();

        if ((cr != cr2) || (cp != cp2) || (ai != ai2))
            {
            return;
            }

        CEEdge edge = null;
        Enumeration enm = cen.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            edge = (CEEdge)enm.nextElement();
            CEEdgeInteraction ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (ceei.isEdgeStillUsable())
                    {
                    ceei.reactivateEdge();
                    } 
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (ceei.isEdgeStillUsable())
                    {
                    ceei.reactivateEdge();
                    }

                break;
                }
            }

        naow.repaint();
        }
    }