package girard.sc.be.io.msg;

/* Lets subjects and observers know that another time tick has
   passed. Subjects respond back so the server knows when to 
   move time foward again.

Author: Dudley Girard
Started: 1-1-2001
Modified: 4-26-2001
Modified: 5-18-2001
*/

import girard.sc.awt.ErrorDialog;
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

public class BETimeTckMsg extends ExptMessage 
    { 
    public BETimeTckMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof BENetworkActionClientWindow)
            {
            BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;

            BENetwork ben = (BENetwork)nacw.getExpApp().getActiveAction();
            ben.getActivePeriod().setCurrentTime(ben.getActivePeriod().getCurrentTime() - 1);
            nacw.setTimeLabel(ben.getActivePeriod().getCurrentTime());
            BETimeTckMsg tmp = new BETimeTckMsg(null);
            cw.getSML().sendMessage(tmp);
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - BERoundWindowMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Time Tck Message");
    
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
                        err_args[1] = new String("BETimeTckMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BETimeTckMsg(args));
                    ec.sendToAllObservers(new BETimeTckMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new BETimeTckMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BETimeTckMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;
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

            BENetwork ben = (BENetwork)ew.getExpApp().getActiveAction().getAction();
            ben.getActivePeriod().setCurrentTime(ben.getActivePeriod().getCurrentTime() - 1);
            naew.setTimeLabel(ben.getActivePeriod().getCurrentTime());

            if (ben.getActivePeriod().getCurrentTime() > 0)
                {
                BETimeTckMsg tmp = new BETimeTckMsg(null);
                ew.getSML().sendMessage(tmp);
                }
            else
                {
                /* Update exchanges for Simultaneous method */
                String exchType = (String)ben.getExtraData("ExchangeMethod");
                if (exchType.equals("Simultaneous"))
                    {
                    computeExchanges(naew,ben);
                    naew.repaint();
                    }

                ben.setExtraData("RoundRunning",new Boolean(false));
                BEStopRoundMsg tmp = new BEStopRoundMsg(null);
                ew.getSML().sendMessage(tmp);
                }
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof BENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - BETimeTckMsg");
            return;
            }

        BENetworkActionObserverWindow naow = (BENetworkActionObserverWindow)ow;
        Boolean rr = (Boolean)naow.getNetwork().getExtraData("RoundRunning");

        if ((!rr.booleanValue()) || (!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
                return;

        BENetwork ben = naow.getNetwork();
        ben.getActivePeriod().setCurrentTime(ben.getActivePeriod().getCurrentTime() - 1);
        naow.setTimeLabel(ben.getActivePeriod().getCurrentTime());
        }

    private void computeExchanges(BENetworkActionExperimenterWindow naew, BENetwork ben)
        {
     // Create a random list of the nodes to cycle through.
        Vector nodes = new Vector();
        Enumeration enm = ben.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode n = (BENode)enm.nextElement();
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
                Enumeration enum2 = ben.getEdgeList().elements();
                while (enum2.hasMoreElements())
                    {
                    BEEdge edge = (BEEdge)enum2.nextElement();
                    BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    if ((edge.getActive()) && (beer.getN1Keep().getResource() == beer.getN2Give().getResource()) && (beer.getN1Keep().getResource() > 0))
                        {
                        if (edge.getNode1() == n.intValue())
                            {
                            if (potentialExchanges.size() == 0)
                                {
                                potentialExchanges.addElement(edge);
                                }
                            else
                                {
                                BEEdge tmpE = (BEEdge)potentialExchanges.elementAt(0);
                                BEEdgeResource tmpB = (BEEdgeResource)tmpE.getExptData("BEEdgeResource");
                                if (beer.getN1Keep().getResource() > tmpB.getN1Keep().getResource())
                                    {
                                    potentialExchanges.removeAllElements();
                                    potentialExchanges.addElement(edge);
                                    }
                                else if (beer.getN1Keep().getResource() == tmpB.getN1Keep().getResource())
                                    {
                                    potentialExchanges.addElement(edge);
                                    }
                                }
                            }
                        if (edge.getNode2() == n.intValue())
                            {
                            if (potentialExchanges.size() == 0)
                                {
                                potentialExchanges.addElement(edge);
                                }
                            else
                                {
                                BEEdge tmpE = (BEEdge)potentialExchanges.elementAt(0);
                                BEEdgeResource tmpB = (BEEdgeResource)tmpE.getExptData("BEEdgeResource");
                                if (beer.getN2Keep().getResource() > tmpB.getN2Keep().getResource())
                                    {
                                    potentialExchanges.removeAllElements();
                                    potentialExchanges.addElement(edge);
                                    }
                                else if (beer.getN2Keep().getResource() == tmpB.getN2Keep().getResource())
                                    {
                                    potentialExchanges.addElement(edge);
                                    }
                                }
                            }
                        }
                    }
                numPE = numPE + potentialExchanges.size();
                if (potentialExchanges.size() > 0)
                    {
                    int index = (int)Math.floor(Math.random()*potentialExchanges.size());
                    BEEdge edge = (BEEdge)potentialExchanges.elementAt(index);
                    BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");

                    int tt = naew.getNetwork().getActivePeriod().getTime() - naew.getNetwork().getActivePeriod().getCurrentTime();
                    beer.completeExchange(tt,naew.getPresentTime(),beer.getN1Keep().getIntResource(),beer.getN2Keep().getIntResource());

                    int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
                    int cp = naew.getNetwork().getCurrentPeriod() + 1;

                    BEOfferOutputObject data = new BEOfferOutputObject(naew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cp,cr,edge.getNode1(),edge.getNode2(),beer.getN1Keep().getIntResource(),beer.getN2Keep().getIntResource(),"Complete",tt, naew.getPresentTime());

                    Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
                    outData.addElement(data);

              // Store the arguments for the CompleteOffer message we are going to send out.
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(edge.getNode1());
                    out_args[1] = new Integer(edge.getNode2());
                    out_args[2] = new Integer(beer.getN1Keep().getIntResource());
                    out_args[3] = new Integer(beer.getN2Keep().getIntResource());

                  // update active settings for edges.
                    BENode n1 = (BENode)ben.getNode(edge.getNode1());
                    BENode n2 = (BENode)ben.getNode(edge.getNode2());

                    BENodeExchange exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
                    BENodeExchange exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");
 
                    exch1.updateNetwork(edge);
                    exch2.updateNetwork(edge);

            // Let the subjects know what happened.
                    BECompleteOfferMsg tmp = new BECompleteOfferMsg(out_args);
                    naew.getSML().sendMessage(tmp);
                    }
                enum2 = ben.getEdgeList().elements();
                flag = false;
                while (enum2.hasMoreElements())
                    {
                    BEEdge tmpEdge = (BEEdge)enum2.nextElement();

                    BENode n1 = (BENode)ben.getNode(tmpEdge.getNode1());
                    BENode n2 = (BENode)ben.getNode(tmpEdge.getNode2());

                    BENodeExchange exch1 = (BENodeExchange)n1.getExptData("BENodeExchange");
                    BENodeExchange exch2 = (BENodeExchange)n2.getExptData("BENodeExchange");

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

                if (!flag)
                    break;
                }
            if (numPE == 0)
                flag = false;
            }
        }
    }