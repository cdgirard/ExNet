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
import girard.sc.be.obj.BENodeExchange;
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
 * This message signifies a completed exchange between two subjects.
 * <p>
 * <br> Started: 3-15-2001
 * <br> Modified: 4-25-2001
 * <br> Modified: 5-18-2001
 * <p>
 * @author Dudley Girard
 */


public class BECompleteOfferMsg extends ExptMessage 
    { 
    public BECompleteOfferMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (cw instanceof BENetworkActionClientWindow)
            {
            BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;
            BENetwork ben = (BENetwork)nacw.getExpApp().getActiveAction();

            int from = ((Integer)this.getArgs()[0]).intValue();
            int to = ((Integer)this.getArgs()[1]).intValue();
            int fromKeep = ((Integer)this.getArgs()[2]).intValue();
            int toKeep = ((Integer)this.getArgs()[3]).intValue();
            Boolean rr = (Boolean)ben.getExtraData("RoundRunning");
            BENode myNode = (BENode)ben.getExtraData("Me");

            if ((!nacw.getExpApp().getExptRunning()) || (!rr.booleanValue()) || (nacw.getExpApp().getExptStopping()))
                return;

            BEEdge edge = null;
            BEEdgeResource beer = null;
            Enumeration enm = ben.getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                edge = (BEEdge)enm.nextElement();
                if ((edge.getNode1() == to) && (edge.getNode2() == from))
                    {
                    if (edge.getActive())  // This should not matter, but we will leave it in just in case.
                        {
                        beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                        int tt = ben.getActivePeriod().getTime() - ben.getActivePeriod().getCurrentTime();
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
                        int tt = ben.getActivePeriod().getTime() - ben.getActivePeriod().getCurrentTime();
                        beer.completeExchange(tt,0,fromKeep,toKeep);
                        break;
                        }
                    else
                        {
                        return;
                        }
                    }
                }

            BENode n1 = (BENode)ben.getNode(from);
            BENode n2 = (BENode)ben.getNode(to);

            BENodeExchange exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
            BENodeExchange exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");
 
            exch1.updateNetwork(edge);
            exch2.updateNetwork(edge);

            enm = nacw.getNetwork().getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                BEEdge tmpEdge = (BEEdge)enm.nextElement();

                n1 = (BENode)ben.getNode(tmpEdge.getNode1());
                n2 = (BENode)ben.getNode(tmpEdge.getNode2());

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

            // Is it a deal completed along one of my relations and is that display arrow active?
            BEClientDisplayArrow arrow = nacw.getArrow();
            if (arrow.getToNode() != null)
                {
                if ((to == arrow.getToNode().getID()) && (from == myNode.getID()))
                    {
                    arrow.setEdge(edge);
                    }
                if ((to == myNode.getID()) && (from == arrow.getToNode().getID()))
                    {
                    arrow.setEdge(edge);
                    }
                }
            nacw.repaint();
            nacw.validate();  // Make sure any changes are hopefully displayed properly.
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - BECompleteOfferMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Complete Offer Message");
    
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
                        err_args[1] = new String("BECompleteOfferMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BECompleteOfferMsg(args));
                    ec.sendToAllObservers(new BECompleteOfferMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = args;
                    ec.addServerMessage(new BECompleteOfferMsg(out_args));
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

        BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;
        
        Boolean rr = (Boolean)naew.getNetwork().getExtraData("RoundRunning");
        
        if ((!rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        int fromKeep = ((Integer)args[2]).intValue();
        int toKeep = ((Integer)args[3]).intValue();

        BEEdge edge = null;
        BEEdgeResource beer = null;
        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            edge = (BEEdge)enm.nextElement();
            beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                int tt = naew.getNetwork().getActivePeriod().getTime() - naew.getNetwork().getActivePeriod().getCurrentTime();
                beer.completeExchange(tt,naew.getPresentTime(),fromKeep,toKeep);
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                int tt = naew.getNetwork().getActivePeriod().getTime() - naew.getNetwork().getActivePeriod().getCurrentTime();
                beer.completeExchange(tt,naew.getPresentTime(),toKeep,fromKeep);
                break;
                }
            }

      /* update data output here */
        int tt = naew.getNetwork().getActivePeriod().getTime() - naew.getNetwork().getActivePeriod().getCurrentTime();
        int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
        int cp = naew.getNetwork().getCurrentPeriod() + 1;

        BEOfferOutputObject data = new BEOfferOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cp,cr,from,to,fromKeep,toKeep,"Complete",tt, naew.getPresentTime());

        Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
        outData.addElement(data);
     /* end update for data output */

   // update active settings for edges.
        BENode n1 = (BENode)naew.getNetwork().getNode(from);
        BENode n2 = (BENode)naew.getNetwork().getNode(to);

        BENodeExchange exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
        BENodeExchange exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");
 
        exch1.updateNetwork(edge);
        exch2.updateNetwork(edge);

        enm = naew.getNetwork().getEdgeList().elements();
        boolean flag = false;
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            n1 = (BENode)naew.getNetwork().getNode(tmpEdge.getNode1());
            n2 = (BENode)naew.getNetwork().getNode(tmpEdge.getNode2());

            exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
            exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");

            if ((exch1.isEdgeActive(tmpEdge)) && (exch2.isEdgeActive(tmpEdge)))
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
        BECompleteOfferMsg tmp = new BECompleteOfferMsg(out_args);
        naew.getSML().sendMessage(tmp);

        if (flag)
            return;

        naew.getNetwork().setExtraData("RoundRunning",new Boolean(false));
    /* This is needed to cover for TCK messages */
        
         boolean[] tick = (boolean[])naew.getNetwork().getExtraData("TimeReady");
         for (int x=0;x<tick.length;x++)
             {
             tick[x] = false;
             }

        BEStopRoundMsg tmp2 = new BEStopRoundMsg(null);
        ew.getSML().sendMessage(tmp2);

        // Write the initial final outcome results to data file here?

        // ESApp.ESData.ComputeFinalResults();  Work on this when we start worrying about data.
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof BENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - BECompleteOfferMsg");
            return;
            }

        Object[] args = this.getArgs();

        BENetworkActionObserverWindow naow = (BENetworkActionObserverWindow)ow;
        
        Boolean rr = (Boolean)naow.getNetwork().getExtraData("RoundRunning");
        
        if ((!rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        int fromKeep = ((Integer)args[2]).intValue();
        int toKeep = ((Integer)args[3]).intValue();

        BEEdge edge = null;
        BEEdgeResource beer = null;

        Enumeration enm = naow.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            edge = (BEEdge)enm.nextElement();
            beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                int tt = naow.getNetwork().getActivePeriod().getTime() - naow.getNetwork().getActivePeriod().getCurrentTime();
                beer.completeExchange(tt,0,fromKeep,toKeep);
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (!edge.getActive())
                    {
                    return;
                    }
                int tt = naow.getNetwork().getActivePeriod().getTime() - naow.getNetwork().getActivePeriod().getCurrentTime();
                beer.completeExchange(tt,0,toKeep,fromKeep);
                break;
                }
            }

   // update active settings for edges.
        BENode n1 = (BENode)naow.getNetwork().getNode(from);
        BENode n2 = (BENode)naow.getNetwork().getNode(to);

        BENodeExchange exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
        BENodeExchange exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");
 
        exch1.updateNetwork(edge);
        exch2.updateNetwork(edge);

        enm = naow.getNetwork().getEdgeList().elements();
        boolean flag = false;
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            n1 = (BENode)naow.getNetwork().getNode(tmpEdge.getNode1());
            n2 = (BENode)naow.getNetwork().getNode(tmpEdge.getNode2());

            exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
            exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");

            if ((exch1.isEdgeActive(tmpEdge)) && (exch2.isEdgeActive(tmpEdge)))
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

        naow.repaint();
        }
    }