package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BEClientDisplayArrow;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.awt.BENetworkActionObserverWindow;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BEEdgeResource;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BEOfferOutputObject;
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
 * If a subject accepts another subject's offer then this message is sent.
 * <p>
 * Started: 3-15-2001
 * <br>Modified: 4-25-2001
 * <br>Modified: 5-18-2001
 * <p>
 * @author Dudley Girard
 */

public class BEAcceptOfferMsg extends ExptMessage
{
    public BEAcceptOfferMsg(Object args[])
    {
        super(args);
    }

    public void getClientResponse(ClientWindow cw)
    {
        if (cw instanceof BENetworkActionClientWindow)
        {
            BENetworkActionClientWindow nacw = (BENetworkActionClientWindow) cw;
            BENetwork ben = (BENetwork) nacw.getExpApp().getActiveAction();

            int from = ((Integer) this.getArgs()[0]).intValue();
            int to = ((Integer) this.getArgs()[1]).intValue();
            int fromKeep = ((Integer) this.getArgs()[2]).intValue();
            int toKeep = ((Integer) this.getArgs()[3]).intValue();

            Boolean rr = (Boolean) ben.getExtraData("RoundRunning");

            if ((!nacw.getExpApp().getExptRunning()) || (!rr.booleanValue()) || (nacw.getExpApp().getExptStopping()))
                return;

            BEEdge edge = null;
            BEEdgeResource beer = null;
            Enumeration enm = ben.getEdgeList().elements();
            while (enm.hasMoreElements())
            {
                edge = (BEEdge) enm.nextElement();
                if ((edge.getNode1() == to) && (edge.getNode2() == from))
                {
                    if (edge.getActive())
                    {
                        beer = (BEEdgeResource) edge.getExptData("BEEdgeResource");
                        beer.getN2Keep().setResource(fromKeep);
                        beer.getN2Give().setResource(toKeep);
                        nacw.repaint();
                    }
                    break;
                }
                if ((edge.getNode2() == to) && (edge.getNode1() == from))
                {
                    if (edge.getActive())
                    {
                        beer = (BEEdgeResource) edge.getExptData("BEEdgeResource");
                        beer.getN1Keep().setResource(fromKeep);
                        beer.getN1Give().setResource(toKeep);
                        nacw.repaint();
                    }
                    break;
                }
            }

            BENode node = (BENode) ben.getExtraData("Me");

            // Was it an offer sent to me by one of my neighbors?
            if (to == node.getID())
            {
                if (edge.getActive())
                {
                    beer.setExchangeState(BEEdgeResource.GREEN);

                    int infoLevel = ((Integer)ben.getExtraData("InfoLevel")).intValue();
                    
                    if (infoLevel == 11) 
                    {
                        nacw.offerReceivedFlash();
                    }
                    
                    BEClientDisplayArrow arrow = nacw.getArrow();
                    if (arrow.getToNode() != null)
                    {
                        if (arrow.getToNode().getID() == from)
                        {
                            arrow.updateBubbleButton();
                            arrow.setEdge(edge);
                        }
                    }
                }
            }
            nacw.repaint();
            nacw.validate(); // Make sure any changes are hopefully displayed properly.
        }
        else
        {
            new ErrorDialog("Wrong Client Window. - BEOfferMsg");
        }
    }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
    {
        Object[] args = this.getArgs();

        // System.err.println("ESR: Accept Offer Message");

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
        {
            synchronized (ec)
            {
                if (index == ExptComptroller.EXPERIMENTER)
                {
                    if (!ec.allRegistered())
                    {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Least one user not registered.");
                        err_args[1] = new String("BEAcceptOfferMsg");
                        return new ExptErrorMsg(err_args);
                    }
                    ec.sendToAllUsers(new BEAcceptOfferMsg(args));
                    ec.sendToAllObservers(new BEAcceptOfferMsg(args));
                    return null;
                }
                else
                {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = args;
                    ec.addServerMessage(new BEAcceptOfferMsg(out_args));
                    return null;
                }
            }
        }
        else
        {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEAcceptOfferMsg");
            return new ExptErrorMsg(err_args);
        }
    }

    public void getExperimenterResponse(ExperimenterWindow ew)
    {
        Object[] args = this.getArgs();

        BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow) ew;

        Boolean rr = (Boolean) naew.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        int from = ((Integer) args[0]).intValue();
        int to = ((Integer) args[1]).intValue();
        int fromKeep = ((Integer) args[2]).intValue();
        int toKeep = ((Integer) args[3]).intValue();

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
        {
            BEEdge edge = (BEEdge) enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource) edge.getExptData("BEEdgeResource");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
            {
                if (!edge.getActive())
                {
                    return;
                }
                beer.getN1Keep().setResource(fromKeep);
                beer.getN1Give().setResource(toKeep);
                break;
            }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
            {
                if (!edge.getActive())
                {
                    return;
                }
                beer.getN2Keep().setResource(fromKeep);
                beer.getN2Give().setResource(toKeep);
                break;
            }
        }

        naew.repaint();

        /* update data output here */
        int tt = naew.getNetwork().getActivePeriod().getTime() - naew.getNetwork().getActivePeriod().getCurrentTime();
        int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
        int cp = naew.getNetwork().getCurrentPeriod() + 1;
        BEOfferOutputObject data = new BEOfferOutputObject(ew.getExpApp().getExptOutputID(), naew.getExpApp().getActionIndex(), cp, cr, from, to, fromKeep, toKeep, "Accept", tt, naew.getPresentTime());

        Vector outData = (Vector) naew.getNetwork().getExtraData("Data");
        outData.addElement(data);

        Object[] out_args = args;
        BEAcceptOfferMsg tmp = new BEAcceptOfferMsg(out_args);
        naew.getSML().sendMessage(tmp);
    }

    public void getObserverResponse(ObserverWindow ow)
    {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof BENetworkActionObserverWindow))
        {
            new ErrorDialog("Wrong Observer Window. - BEAcceptOfferMsg");
            return;
        }

        Object[] args = this.getArgs();

        BENetworkActionObserverWindow naow = (BENetworkActionObserverWindow) ow;

        Boolean rr = (Boolean) naow.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        int from = ((Integer) args[0]).intValue();
        int to = ((Integer) args[1]).intValue();
        int fromKeep = ((Integer) args[2]).intValue();
        int toKeep = ((Integer) args[3]).intValue();

        Enumeration enm = naow.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
        {
            BEEdge edge = (BEEdge) enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource) edge.getExptData("BEEdgeResource");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
            {
                if (!edge.getActive())
                {
                    return;
                }
                beer.getN1Keep().setResource(fromKeep);
                beer.getN1Give().setResource(toKeep);
                break;
            }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
            {
                if (!edge.getActive())
                {
                    return;
                }
                beer.getN2Keep().setResource(fromKeep);
                beer.getN2Give().setResource(toKeep);
                break;
            }
        }
        naow.repaint();
    }
}