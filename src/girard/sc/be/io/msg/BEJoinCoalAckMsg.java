package girard.sc.be.io.msg;

import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BEStateAction;
import girard.sc.be.obj.BEStaticOffOutputObject;
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
 * This message informs the experimenter when all the subjects are done
 * seeing the results of the voting for their coalition.
 * 
 * <p>
 * <br> Started: 07-19-2003
 * <p>
 * @author Dudley Girard
 */

public class BEJoinCoalAckMsg extends ExptMessage 
    { 
    public BEJoinCoalAckMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Join Coalition Acknowledgment Message");
    
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
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(index);
                    out_args[1] = args[0];
                    out_args[2] = args[1];
                    out_args[3] = args[2];
                    ec.addServerMessage(new BEJoinCoalAckMsg(out_args));
                    return null;
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEJoinCoalAckMsg");
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
        nos.getCoalition().setFormed(((Boolean)this.getArgs()[2]).booleanValue());
        nos.getCoalition().setCoalOffer(((Integer)this.getArgs()[3]).intValue());
        if ((nos.getCoalition().getCoalitionType().equals("Static")) && (nos.getCoalition().getFormed()) && (nos.getCoalition().getJoined()))
            {
            /* update data output here */
            int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
            int cp = naew.getNetwork().getCurrentPeriod() + 1;

            BEStaticOffOutputObject data = new BEStaticOffOutputObject(ew.getExpApp().getExptOutputID(),ew.getExpApp().getActionIndex(),cp,cr,node.getID(),nos.getCoalition().getCoalition(),nos.getCoalition().getCoalOffer());

            Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
            outData.addElement(data);
            }

        boolean flag = true;
        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!ew.getExpApp().getReady(x))
                flag = false;
            }
System.err.println("CAK: "+flag);
        if (flag)
            {
            ew.getExpApp().initializeReady();

            Hashtable nodeOffers = new Hashtable();
            Hashtable nodeFormed = new Hashtable();

            Enumeration enm = ben.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                BENode nTmp = (BENode)enm.nextElement();
                BENodeOrSubNet nos2 = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");

                nodeOffers.put(new Integer(nTmp.getID()),new Integer(nos2.getCoalition().getCoalOffer()));
                nodeFormed.put(new Integer(nTmp.getID()),new Boolean(nos2.getCoalition().getFormed()));
                }
   // No dynamic coalitions, go ahead and send out the vote results.

            Object[] out_args = new Object[2];
            out_args[0] = nodeOffers;
            out_args[1] = nodeFormed;
            BECoalOfferResultMsg tmp = new BECoalOfferResultMsg(out_args);
            ew.getSML().sendMessage(tmp);

            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            besa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }