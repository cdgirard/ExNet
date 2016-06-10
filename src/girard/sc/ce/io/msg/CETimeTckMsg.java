package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeDisplay;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CEExchange;
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
import java.util.Vector;

/**
 * Lets subjects and observers know that another time tick has
 * passed. Subjects respond back so the server knows when to 
 * move time foward again.
 * <p>
 * <br> Started: 02-10-2003
 * <br> Modified: 02-19-2003
 * <p>
 * @author Dudley Girard
 */

public class CETimeTckMsg extends ExptMessage 
    { 
    public CETimeTckMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof CENetworkActionClientWindow)
            {
            CENetworkActionClientWindow nacw = (CENetworkActionClientWindow)cw;

            CENetwork cen = (CENetwork)nacw.getExpApp().getActiveAction();
            cen.getActivePeriod().setCurrentTime(cen.getActivePeriod().getCurrentTime() - 1);
            nacw.setTimeLabel(cen.getActivePeriod().getCurrentTime());
	    //	    nacw.setProfitLabel(1000);
            CETimeTckMsg tmp = new CETimeTckMsg(null);
            cw.getSML().sendMessage(tmp);
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - CERoundWindowMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

	// System.err.println("ESR: CE Time Tck Message");
    
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
                        err_args[1] = new String("CETimeTckMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CETimeTckMsg(args));
                    ec.sendToAllObservers(new CETimeTckMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CETimeTckMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CETimeTckMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        CENetworkActionExperimenterWindow naew = (CENetworkActionExperimenterWindow)ew;
        boolean[] tick = (boolean[])naew.getNetwork().getExtraData("TimeReady");
        Boolean rr = (Boolean)naew.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        tick[index.intValue()] = true;

        boolean flag = true;

        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!tick[x])
                flag = false;
            }
        if (flag)
            {
            for (int x=0;x<ew.getExpApp().getNumUsers();x++)
                {
                tick[x] = false;
                }

            CENetwork cen = (CENetwork)ew.getExpApp().getActiveAction().getAction();
            cen.getActivePeriod().setCurrentTime(cen.getActivePeriod().getCurrentTime() - 1);
            naew.setTimeLabel(cen.getActivePeriod().getCurrentTime());
            if (cen.getActivePeriod().getCurrentTime() > 0)
                {
                CETimeTckMsg tmp = new CETimeTckMsg(null);
                ew.getSML().sendMessage(tmp);
                }
            else
                {
                /* Update exchanges for Simultaneous method */
                String exchType = (String)cen.getExtraData("TimingMethod");
                if (exchType.equals("Simultaneous"))
                    {
                    computeExchanges(naew,cen);
                    naew.repaint();
                    }
                cen.setExtraData("RoundRunning",new Boolean(false));
                CEStopRoundMsg tmp = new CEStopRoundMsg(null);
                ew.getSML().sendMessage(tmp);
                }
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CETimeTckMsg");
            return;
            }

        CENetworkActionObserverWindow naow = (CENetworkActionObserverWindow)ow;
        Boolean rr = (Boolean)naow.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
                return;

        CENetwork cen = naow.getNetwork();
        cen.getActivePeriod().setCurrentTime(cen.getActivePeriod().getCurrentTime() - 1);
        naow.setTimeLabel(cen.getActivePeriod().getCurrentTime());
        }

    private void computeExchanges(CENetworkActionExperimenterWindow naew, CENetwork cen)
        {
     // Create a random list of the nodes to cycle through.
        Vector nodes = new Vector();
        Enumeration enm = cen.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode n = (CENode)enm.nextElement();
            int loc = (int)(Math.floor(Math.random()*nodes.size()));
            nodes.insertElementAt(new Integer(n.getID()),loc);
            }
        boolean flag = true;
        while (flag)
            {
            int numPE = 0;
            enm = nodes.elements();
            while (enm.hasMoreElements())
                {
                Integer n = (Integer)enm.nextElement();
                      
                Vector potentialExchanges = new Vector();
                Enumeration enum2 = cen.getEdgeList().elements();
                while (enum2.hasMoreElements())
                    {
                    CEEdge edge = (CEEdge)enum2.nextElement();

                    CEEdgeDisplay ceed = (CEEdgeDisplay)edge.getExptData("CEEdgeDisplay");
                    if ((ceed.getExchangeState1() == CEEdgeDisplay.GREEN) || (ceed.getExchangeState2() == CEEdgeDisplay.GREEN))
                        {
                        if (potentialExchanges.size() == 0)
                            {
                            potentialExchanges.addElement(edge);
                            }
                        else
                            {
                            CEEdge tmpE = (CEEdge)potentialExchanges.elementAt(0);
                            CEEdgeInteraction tmpC = (CEEdgeInteraction)tmpE.getExptData("CEEdgeInteraction");
                            CEEdgeInteraction ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");
                            
                            if (tmpC.getOfferProfit(n.intValue(),edge.getNode1(),edge.getNode2()) > ceei.getOfferProfit(n.intValue(),edge.getNode1(),edge.getNode2()))
                                {
                                potentialExchanges.removeAllElements();
                                potentialExchanges.addElement(edge);
                                }
                            else if (tmpC.getOfferProfit(n.intValue(),edge.getNode1(),edge.getNode2()) == ceei.getOfferProfit(n.intValue(),edge.getNode1(),edge.getNode2()))
                                {
                                potentialExchanges.addElement(edge);
                                }
                            }
                        }
                    }

                numPE = numPE + potentialExchanges.size();
                if (potentialExchanges.size() > 0)
                    {
                    int index = (int)Math.floor(Math.random()*potentialExchanges.size());
                    CEEdge edge = (CEEdge)potentialExchanges.elementAt(index);
                    CEEdgeInteraction ceei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");

                    int tt = naew.getNetwork().getActivePeriod().getTime() - naew.getNetwork().getActivePeriod().getCurrentTime();
                    CEExchange cee = ceei.getOffer(edge.getNode1(),edge.getNode2());
                    CEExchange ceeNew = new CEExchange(tt,naew.getPresentTime(),cee.getNode1(),cee.getNode2());
                    ceei.completeExchange(edge.getNode1(),edge.getNode2(),ceeNew,"experimenter");

                    int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
                    int cp = naew.getNetwork().getCurrentPeriod() + 1;

                  //  BEOfferOutputObject data = new BEOfferOutputObject(naew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cp,cr,edge.getNode1(),edge.getNode2(),beer.getN1Keep().getIntResource(),beer.getN2Keep().getIntResource(),"Complete",tt, naew.getPresentTime());

                  //  Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
                  //  outData.addElement(data);

            // Let the subjects know what happened.
            // Store the arguments for the CompleteOffer message we are going to send out.
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(edge.getNode1());
                    out_args[1] = new Integer(edge.getNode2());
                    out_args[2] = ceeNew.getNode1();
                    out_args[3] = ceeNew.getNode2();

                    CECompleteOfferMsg tmp = new CECompleteOfferMsg(out_args);
                    naew.getSML().sendMessage(tmp);
                    }
                enum2 = cen.getEdgeList().elements();
                flag = false;
                while (enm.hasMoreElements())
                    {
                    CEEdge tmpEdge = (CEEdge)enm.nextElement();

                    CEEdgeInteraction tmpCeei = (CEEdgeInteraction)tmpEdge.getExptData("CEEdgeInteraction");

                    tmpCeei.updateActiveState();
                    if (tmpEdge.getActive())
                        tmpCeei.updateOffers();

                    if (tmpEdge.getActive()) 
                        flag = true;
                    }

                if (!flag)
                    break;
                }
            if (numPE == 0)
                flag = false;
            }
        }
    }
