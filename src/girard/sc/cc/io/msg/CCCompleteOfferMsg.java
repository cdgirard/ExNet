package girard.sc.cc.io.msg;

/* This message signifies a completed exchange between two subjects.

   Author: Dudley Girard
   Started: 6-27-2001
   Modified: 7-25-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCClientDisplayArrow;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCEdgeDisplay;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.cc.obj.CCOffersOutputObject;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Vector;

public class CCCompleteOfferMsg extends ExptMessage 
    { 
    public CCCompleteOfferMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (!(cw instanceof CCNetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - CCCompleteOfferMsg");
            return;
            }

        CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;
        CCNetwork ccn = (CCNetwork)nacw.getExpApp().getActiveAction();
            
        Boolean rr = (Boolean)ccn.getExtraData("RoundRunning");

        if ((!nacw.getExpApp().getExptRunning()) || (!rr.booleanValue()) || (nacw.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)this.getArgs()[0]).intValue();
        int to = ((Integer)this.getArgs()[1]).intValue();
        int fromKeep = ((Integer)this.getArgs()[2]).intValue();
        int toKeep = ((Integer)this.getArgs()[3]).intValue();

        CCNode fromNode = (CCNode)ccn.getNode(from);
        CCNode toNode = (CCNode)ccn.getNode(to);

        CCNodeResource fromCcnr = (CCNodeResource)fromNode.getExptData("CCNodeResource");
        CCNodeResource toCcnr = (CCNodeResource)toNode.getExptData("CCNodeResource");

        int tt = ccn.getPeriod().getTime() - ccn.getPeriod().getCurrentTime();

        fromCcnr.completeExchange(tt,0,fromKeep,from,toKeep,to);
        toCcnr.completeExchange(tt,0,toKeep,to,fromKeep,from);

        CCNode me = (CCNode)ccn.getExtraData("Me");
        if (me.getID() == from)
            {
            nacw.setPointsLabel(fromCcnr.getAvailablePoints());
            }
        if (me.getID() == to)
            {
            nacw.setPointsLabel(toCcnr.getAvailablePoints());
            }

        Enumeration enm = ccn.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                cced.setExchangeState1(CCEdgeDisplay.COMPLETED);

                cced.setN1Display(0,""+fromKeep);
                cced.setN1Display(1,""+toKeep);
                cced.setN1Display(2,"DOT");

            // Update the N2 Displays as well.
                cced.setExchangeState2(CCEdgeDisplay.COMPLETED);

                cced.setN2Display(0,""+toKeep);
                cced.setN2Display(1,""+fromKeep);
                cced.setN2Display(2,"DOT");


                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                cced.setExchangeState2(CCEdgeDisplay.COMPLETED);

                cced.setN2Display(0,""+fromKeep);
                cced.setN2Display(1,""+toKeep);
                cced.setN2Display(2,"DOT");

                // Update the N1 Displays as well.
                cced.setExchangeState1(CCEdgeDisplay.COMPLETED);

                cced.setN1Display(0,""+toKeep);
                cced.setN1Display(1,""+fromKeep);
                cced.setN1Display(2,"DOT");

                break;
                }
            }

        enm = ccn.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)ccn.getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)ccn.getNode(tmpEdge.getNode2());

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

        // Is it a deal completed along one of my relations and is that display arrow active?
        CCClientDisplayArrow arrow = nacw.getArrow();
        if (arrow != null)
            {
            arrow.updateDisplayArrow();
            }
        nacw.repaint();
        nacw.validate();  // Make sure any changes are hopefully displayed properly.
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Complete Offer Message");
    
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
                        err_args[1] = new String("CCCompleteOfferMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCCompleteOfferMsg(args));
                    ec.sendToAllObservers(new CCCompleteOfferMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = args;
                    ec.addServerMessage(new CCCompleteOfferMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCCompleteOfferMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Object[] args = this.getArgs();

        CCNetworkActionExperimenterWindow naew = (CCNetworkActionExperimenterWindow)ew;
        
        Boolean rr = (Boolean)naew.getNetwork().getExtraData("RoundRunning");
        
        if ((!rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        int fromKeep = ((Integer)args[2]).intValue();
        int toKeep = ((Integer)args[3]).intValue();

        CCNode fromNode = (CCNode)naew.getNetwork().getNode(from);
        CCNode toNode = (CCNode)naew.getNetwork().getNode(to);

        CCNodeResource fromCcnr = (CCNodeResource)fromNode.getExptData("CCNodeResource");
        CCNodeResource toCcnr = (CCNodeResource)toNode.getExptData("CCNodeResource");

        if ((fromCcnr.canNegoiateWith(toNode.getID())) && (toCcnr.canNegoiateWith(fromNode.getID())) && (!fromCcnr.tradeCompletedWith(toNode.getID())))
            {
            int tt = naew.getNetwork().getPeriod().getTime() - naew.getNetwork().getPeriod().getCurrentTime();
            long ct = naew.getPresentTime();

            fromCcnr.completeExchange(tt,ct,fromKeep,from,toKeep,to);
            toCcnr.completeExchange(tt,ct,toKeep,to,fromKeep,from);

            Enumeration enm = naew.getNetwork().getEdgeList().elements();
            while(enm.hasMoreElements())
                {
                CCEdge edge = (CCEdge)enm.nextElement();
                CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

                if ((from == edge.getNode1()) && (to == edge.getNode2()))
                    {
                    cced.setN1Display(0,""+fromKeep);
                    cced.setN1Display(1,""+toKeep);
                    cced.setN1Display(2,"*");
                    cced.setExchangeState1(CCEdgeDisplay.COMPLETED);
                    cced.setN2Display(0,""+toKeep);
                    cced.setN2Display(1,""+fromKeep);
                    cced.setN2Display(2,"*");
                    cced.setExchangeState2(CCEdgeDisplay.COMPLETED);
                    break;
                    }
                if ((from == edge.getNode2()) && (to == edge.getNode1()))
                    {
                    cced.setN2Display(0,""+fromKeep);
                    cced.setN2Display(1,""+toKeep);
                    cced.setN2Display(2,"*");
                    cced.setExchangeState2(CCEdgeDisplay.COMPLETED);
                    cced.setN1Display(0,""+toKeep);
                    cced.setN1Display(1,""+fromKeep);
                    cced.setN1Display(2,"*");
                    cced.setExchangeState1(CCEdgeDisplay.COMPLETED);
                    break;
                    }
                }
            }
        else
            {
            return; // Offer not valid don't process it.
            }

      /* update data output here */
        int tt = naew.getNetwork().getPeriod().getTime() - naew.getNetwork().getPeriod().getCurrentTime();
        int cr = naew.getNetwork().getPeriod().getCurrentRound();

        CCOffersOutputObject data = new CCOffersOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cr,from,to,fromKeep,toKeep,"Complete",tt, naew.getPresentTime());

        Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
        outData.addElement(data);
     /* end update for data output */

   // update active settings for edges.

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        boolean flag = false;
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)naew.getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)naew.getNetwork().getNode(tmpEdge.getNode2());

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
                    CCEdgeDisplay ed = (CCEdgeDisplay)tmpEdge.getExptData("CCEdgeDisplay");
                    ed.initializeNetwork();
                    }
                tmpEdge.setActive(false);
                }

            if (tmpEdge.getActive())
                flag = true;
            }

        naew.repaint();

        Object[] out_args = args;
        CCCompleteOfferMsg tmp = new CCCompleteOfferMsg(out_args);
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

        CCStopRoundMsg tmp2 = new CCStopRoundMsg(null);
        ew.getSML().sendMessage(tmp2);

        // Write the initial final outcome results to data file here?

        // ESApp.ESData.ComputeFinalResults();  Work on this when we start worrying about data.
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CCNetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CCCompleteOfferMsg");
            return;
            }

        Object[] args = this.getArgs();

        CCNetworkActionObserverWindow naow = (CCNetworkActionObserverWindow)ow;
        
        Boolean rr = (Boolean)naow.getNetwork().getExtraData("RoundRunning");
        
        if ((!rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        int from = ((Integer)args[0]).intValue();
        int to = ((Integer)args[1]).intValue();
        int fromKeep = ((Integer)args[2]).intValue();
        int toKeep = ((Integer)args[3]).intValue();

        CCNode fromNode = (CCNode)naow.getNetwork().getNode(from);
        CCNode toNode = (CCNode)naow.getNetwork().getNode(to);

        CCNodeResource fromCcnr = (CCNodeResource)fromNode.getExptData("CCNodeResource");
        CCNodeResource toCcnr = (CCNodeResource)toNode.getExptData("CCNodeResource");

        int tt = naow.getNetwork().getPeriod().getTime() - naow.getNetwork().getPeriod().getCurrentTime();

        fromCcnr.completeExchange(tt,0,fromKeep,from,toKeep,to);
        toCcnr.completeExchange(tt,0,toKeep,to,fromKeep,from);

        Enumeration enm = naow.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                cced.setN1Display(0,""+fromKeep);
                cced.setN1Display(1,""+toKeep);
                cced.setN1Display(2,"*");
                cced.setExchangeState1(CCEdgeDisplay.COMPLETED);
                cced.setN2Display(0,""+toKeep);
                cced.setN2Display(1,""+fromKeep);
                cced.setN2Display(2,"*");
                cced.setExchangeState2(CCEdgeDisplay.COMPLETED);
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                cced.setN2Display(0,""+fromKeep);
                cced.setN2Display(1,""+toKeep);
                cced.setN2Display(2,"*");
                cced.setExchangeState2(CCEdgeDisplay.COMPLETED);
                cced.setN1Display(0,""+toKeep);
                cced.setN1Display(1,""+fromKeep);
                cced.setN1Display(2,"*");
                cced.setExchangeState1(CCEdgeDisplay.COMPLETED);
                break;
                }
            }

        enm = naow.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)naow.getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)naow.getNetwork().getNode(tmpEdge.getNode2());

            CCNodeResource exch1 = (CCNodeResource)n1.getExptData("CCNodeResource");
            CCNodeResource exch2 = (CCNodeResource)n2.getExptData("CCNodeResource");

            if ((exch1.isEdgeActive(tmpEdge)) && (exch2.isEdgeActive(tmpEdge)))
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