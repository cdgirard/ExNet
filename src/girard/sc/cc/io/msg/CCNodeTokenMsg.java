package girard.sc.cc.io.msg;

/* This message signifies a token being sent between two subjects.

   Author: Dudley Girard
   Started: 7-4-2001
   Modified: 7-25-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCCoinTossOutputObject;
import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCEdgeDisplay;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeToken;
import girard.sc.cc.obj.CCNodeTokens;
import girard.sc.cc.obj.CCTokensOutputObject;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Vector;

public class CCNodeTokenMsg extends ExptMessage 
    { 
    public CCNodeTokenMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (!(cw instanceof CCNetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - CCNodeTokenMsg");
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

        CCNodeTokens fromCcnt = (CCNodeTokens)fromNode.getExptData("CCNodeTokens");
        CCNodeTokens toCcnt = (CCNodeTokens)toNode.getExptData("CCNodeTokens");

        fromCcnt.tokenSent(to,msg);

        CCNodeToken fromToken = fromCcnt.getToken(to);

        Enumeration enm = ccn.getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    if (fromToken.getYesValue() != 0)
                        cced.setN1Display(0,"1");  // We are potentially getting the reward.
                    else
                        cced.setN1Display(0,"0"); // We are potentially avoiding a fine.

                    cced.setExchangeState1(CCEdgeDisplay.GREEN);
                    }
                else
                    {
                    if (fromToken.getNoValue() != 0)
                        cced.setN1Display(0,"1");  // We are getting a potential fine. 
                    else
                        cced.setN1Display(0,"0");   // We are missing getting the potential reward.

                    cced.setExchangeState1(CCEdgeDisplay.RED);
                    }
                cced.setN1Display(1,"T");
                cced.setN1Display(2,"DOT");
                
                if (!toCcnt.hasToken(from))
                    {
                    cced.setN2Display(0,"");
                    cced.setN2Display(1,"");
                    cced.setN2Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcnt.canSendToken(from))
                    {
                    edge.setCompleted(true);
                    }
                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (msg)
                    {
                    if (fromToken.getYesValue() != 0)
                        cced.setN2Display(0,"1");  // We are potentially getting the reward.
                    else
                        cced.setN2Display(0,"0"); // We are potentially avoiding a fine.

                    cced.setExchangeState2(CCEdgeDisplay.GREEN);
                    }
                else
                    {
                    if (fromToken.getNoValue() != 0)
                        cced.setN2Display(0,"1");  // We are getting a potential fine. 
                    else
                        cced.setN2Display(0,"0");   // We are missing getting the potential reward.

                    cced.setExchangeState2(CCEdgeDisplay.RED);
                    }
                cced.setN2Display(1,"T");
                cced.setN2Display(2,"DOT");
                
                if (!toCcnt.hasToken(from))
                    {
                    cced.setN1Display(0,"");
                    cced.setN1Display(1,"");
                    cced.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcnt.canSendToken(from))
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

            CCNodeTokens nt1 = (CCNodeTokens)n1.getExptData("CCNodeTokens");
            CCNodeTokens nt2 = (CCNodeTokens)n2.getExptData("CCNodeTokens");

            if ((nt1.isEdgeActive(tmpEdge)) && (nt2.isEdgeActive(tmpEdge)))
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

// System.err.println("ESR: CC Node Token Message");
    
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
                        err_args[1] = new String("CCNodeTokenMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCNodeTokenMsg(args));
                    ec.sendToAllObservers(new CCNodeTokenMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    ec.addServerMessage(new CCNodeTokenMsg(args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCNodeTokenMsg");
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

        CCNodeTokens fromCcnt = (CCNodeTokens)fromNode.getExptData("CCNodeTokens");
        CCNodeTokens toCcnt = (CCNodeTokens)toNode.getExptData("CCNodeTokens");

        fromCcnt.tokenSent(to,msg);

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    cced.setN1Display(0,"1");
                    cced.setExchangeState1(CCEdgeDisplay.GREEN);
                    }
                else
                    {
                    cced.setN1Display(0,"0");
                    cced.setExchangeState1(CCEdgeDisplay.RED);
                    }
                cced.setN1Display(1,"T");
                cced.setN1Display(2,"*");

                if (!toCcnt.hasToken(from))
                    {
                    cced.setN2Display(0,"");
                    cced.setN2Display(1,"");
                    cced.setN2Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcnt.canSendToken(from))
                    {
                    edge.setCompleted(true);
                    }

                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (msg)
                    {
                    cced.setN2Display(0,"1");
                    cced.setExchangeState2(CCEdgeDisplay.GREEN);
                    }
                else
                    {
                    cced.setN2Display(0,"0");
                    cced.setExchangeState2(CCEdgeDisplay.RED);
                    }
                cced.setN2Display(1,"T");
                cced.setN2Display(2,"*");

                if (!toCcnt.hasToken(from))
                    {
                    cced.setN1Display(0,"");
                    cced.setN1Display(1,"");
                    cced.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcnt.canSendToken(from))
                    {
                    edge.setCompleted(true);
                    }

                break;
                }
            }

      /* update data output here */
        CCNodeToken fromToken = fromCcnt.getToken(to);
        int cr = naew.getNetwork().getPeriod().getCurrentRound();

        CCTokensOutputObject data = new CCTokensOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),cr,from,to,msg,fromToken.getTokens());

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

            CCNodeTokens nt1 = (CCNodeTokens)n1.getExptData("CCNodeTokens");
            CCNodeTokens nt2 = (CCNodeTokens)n2.getExptData("CCNodeTokens");

            if ((nt1.isEdgeActive(tmpEdge)) && (nt2.isEdgeActive(tmpEdge)))
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
        CCNodeTokenMsg tmp = new CCNodeTokenMsg(out_args);
        naew.getSML().sendMessage(tmp);

        if (flag)
            return;

        // Write the initial final outcome results to data file here?

        // ESApp.ESData.ComputeFinalResults();  Work on this when we start worrying about data.

        int coinToss = (int)(100*Math.random());
        Object[] out_args2 = new Object[1];
        out_args2[0] = new Integer(coinToss);
        CCAfterTokenWindowMsg tmp2 = new CCAfterTokenWindowMsg(out_args2);
        naew.getSML().sendMessage(tmp2);

     // If the last round write all the coin tosses values to the data file.
        if (ccn.getPeriod().getCurrentRound() == ccn.getPeriod().getRounds())
            {
            enm = ccn.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                CCNode node = (CCNode)enm.nextElement();

                CCNodeTokens ccnt = (CCNodeTokens)node.getExptData("CCNodeTokens");

                Enumeration enum2 = ccnt.getTokens().elements();
                while (enum2.hasMoreElements())
                    {
                    CCNodeToken token = (CCNodeToken)enum2.nextElement();
                    CCCoinTossOutputObject data2 = new CCCoinTossOutputObject(ew.getExpApp().getExptOutputID(),naew.getExpApp().getActionIndex(),from,to,coinToss);
                    outData.addElement(data2);
                    }
                }
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CCNetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CCNodeTokenMsg");
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

        CCNodeTokens fromCcnt = (CCNodeTokens)fromNode.getExptData("CCNodeTokens");
        CCNodeTokens toCcnt = (CCNodeTokens)toNode.getExptData("CCNodeTokens");

        fromCcnt.tokenSent(to,msg);

        Enumeration enm = naow.getNetwork().getEdgeList().elements();
        while(enm.hasMoreElements())
            {
            CCEdge edge = (CCEdge)enm.nextElement();
            CCEdgeDisplay cced = (CCEdgeDisplay)edge.getExptData("CCEdgeDisplay");

            if ((from == edge.getNode1()) && (to == edge.getNode2()))
                {
                if (msg)
                    {
                    cced.setN1Display(0,"1");
                    cced.setExchangeState1(CCEdgeDisplay.GREEN);
                    }
                else
                    {
                    cced.setN1Display(0,"0");
                    cced.setExchangeState1(CCEdgeDisplay.RED);
                    }
                cced.setN1Display(1,"T");
                cced.setN1Display(2,"*");

                if (!toCcnt.hasToken(from))
                    {
                    cced.setN2Display(0,"");
                    cced.setN2Display(1,"");
                    cced.setN2Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcnt.canSendToken(from))
                    {
                    edge.setCompleted(true);
                    }

                break;
                }
            if ((from == edge.getNode2()) && (to == edge.getNode1()))
                {
                if (msg)
                    {
                    cced.setN2Display(0,"1");
                    cced.setExchangeState2(CCEdgeDisplay.GREEN);
                    }
                else
                    {
                    cced.setN2Display(0,"0");
                    cced.setExchangeState2(CCEdgeDisplay.RED);
                    }
                cced.setN2Display(1,"T");
                cced.setN2Display(2,"*");

                if (!toCcnt.hasToken(from))
                    {
                    cced.setN1Display(0,"");
                    cced.setN1Display(1,"");
                    cced.setN1Display(2,"");
                    edge.setCompleted(true);
                    }
                else if (!toCcnt.canSendToken(from))
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

            CCNodeTokens nt1 = (CCNodeTokens)n1.getExptData("CCNodeTokens");
            CCNodeTokens nt2 = (CCNodeTokens)n2.getExptData("CCNodeTokens");

            if ((nt1.isEdgeActive(tmpEdge)) && (nt2.isEdgeActive(tmpEdge)))
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