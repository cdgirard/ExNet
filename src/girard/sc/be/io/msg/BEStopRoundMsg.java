package girard.sc.be.io.msg;

/* Lets the subjects and obeservers know that a round has ended.

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
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeExchange;
import girard.sc.be.obj.BEPeriod;
import girard.sc.be.obj.BEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;

public class BEStopRoundMsg extends ExptMessage 
    { 
    public BEStopRoundMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof BENetworkActionClientWindow)
            {
            BENetwork ben = (BENetwork)cw.getExpApp().getActiveAction();
            ben.setExtraData("RoundRunning",new Boolean(false));
            ben.setExtraData("CurrentState",new Double(1.5));

            /* modifing earnings goes here */
            Enumeration enm = ben.getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                BEEdge edge = (BEEdge)enm.nextElement();
                BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                BENode node1 = (BENode)ben.getNode(edge.getNode1());
                BENode node2 = (BENode)ben.getNode(edge.getNode2());
                BENodeExchange exchN1 = (BENodeExchange)node1.getExptData("BENodeExchange");
                BENodeExchange exchN2 = (BENodeExchange)node2.getExptData("BENodeExchange");
                beer.adjustEarnings(exchN1,exchN2);
                }

            BEStopRoundMsg tmp = new BEStopRoundMsg(null);
            cw.getSML().sendMessage(tmp);
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - BEStopRoundMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Stop Round Message");
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
                        err_args[1] = new String("BEStopRoundMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BEStopRoundMsg(args));
                    ec.sendToAllObservers(new BEStopRoundMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new BEStopRoundMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEStartRoundMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
        if ((!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        ew.getExpApp().setReady(true,index.intValue());

        boolean flag = true;
        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!ew.getExpApp().getReady(x))
                flag = false;
            }
        if (flag)
            {
            ew.getExpApp().initializeReady();
            BENetwork ben = (BENetwork)ew.getExpApp().getActiveAction().getAction();
            BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;

       
       /* Updating all the earnings based on the NodeExchange rules. */
            Enumeration enm = ben.getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                BEEdge edge = (BEEdge)enm.nextElement();
                BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                BENode node1 = (BENode)ben.getNode(edge.getNode1());
                BENode node2 = (BENode)ben.getNode(edge.getNode2());
                BENodeExchange exchN1 = (BENodeExchange)node1.getExptData("BENodeExchange");
                BENodeExchange exchN2 = (BENodeExchange)node2.getExptData("BENodeExchange");
                beer.adjustEarnings(exchN1,exchN2);
                }

            ben.setExtraData("CurrentState",new Double(BEPeriod.END_BARGINING_STATE));  

     // This would be where other things like zap voting could take place before the final end.
            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            besa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof BENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - BEStopRoundMsg");
            return;
            }

        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        BENetwork ben = (BENetwork)ow.getExpApp().getActiveAction();
        ben.setExtraData("RoundRunning",new Boolean(false));

        /* modifing earnings goes here */
        Enumeration enm = ben.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            BENode node1 = (BENode)ben.getNode(edge.getNode1());
            BENode node2 = (BENode)ben.getNode(edge.getNode2());
            BENodeExchange exchN1 = (BENodeExchange)node1.getExptData("BENodeExchange");
            BENodeExchange exchN2 = (BENodeExchange)node2.getExptData("BENodeExchange");
            beer.adjustEarnings(exchN1,exchN2);
            }
        }
    }