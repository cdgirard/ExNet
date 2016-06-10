package girard.sc.be.io.msg;

import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BEStaticVZOutputObject;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This message informs the experimenter who zapped and who didn't.
 * 
 * <p>
 * <br> Started: 07-25-2003
 * <p>
 * @author Dudley Girard
 */

public class BEVoteZapMsg extends ExptMessage 
    { 
    public BEVoteZapMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Vote Zap Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    // Do nothing.
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[3];
                    out_args[0] = new Integer(index);
                    out_args[1] = args[0];
                    out_args[2] = args[1];
                    ec.addServerMessage(new BEVoteZapMsg(out_args));
                    return null;
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEVoteZapMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];

        if ((!ew.getExpApp().getExptRunning()) || (ew.getExpApp().getExptStopping()))
            return;

        ew.getExpApp().setReady(true,index.intValue());

        BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;
        BENetwork ben = (BENetwork)ew.getExpApp().getActiveAction().getAction();
        BENode node = (BENode)ben.getNode((Integer)this.getArgs()[1]);
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
// Record whether that node joined its coalition or not.
        if (nos.getCoalition().getCoalitionType().equals("Dynamic"))
            {
            nos.getCoalition().setZapped(((Boolean)this.getArgs()[2]).booleanValue());
            }
        else if (nos.getCoalition().getCoalitionType().equals("Static"))
            {
            nos.getCoalition().setZapped(((Boolean)this.getArgs()[2]).booleanValue());

            if (nos.getCoalition().getJoined())
                {
                /* update data output here */
                int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
                int cp = naew.getNetwork().getCurrentPeriod() + 1;

                BEStaticVZOutputObject data = new BEStaticVZOutputObject(ew.getExpApp().getExptOutputID(),ew.getExpApp().getActionIndex(),cp,cr,node.getID(),nos.getCoalition().getCoalition(),nos.getCoalition().getZapped());

                Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
                outData.addElement(data);
                }
            }

        boolean flag = true;
        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!ew.getExpApp().getReady(x))
                flag = false;
            }
        if (flag)
            {
            ew.getExpApp().initializeReady();

            Hashtable nodeVotes = new Hashtable();
            Enumeration enm = ben.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                BENode nTmp = (BENode)enm.nextElement();
                BENodeOrSubNet nos2 = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");
                nodeVotes.put(new Integer(nTmp.getID()),new Boolean(nos2.getCoalition().getZapped()));
                }
   // Send out the vote results.
            Object[] out_args = new Object[1];
            out_args[0] = nodeVotes;
            BEVoteZapResultMsg tmp = new BEVoteZapResultMsg(out_args);
            ew.getSML().sendMessage(tmp);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }