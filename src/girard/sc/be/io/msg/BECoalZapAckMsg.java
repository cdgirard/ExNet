package girard.sc.be.io.msg;

import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BEStateAction;
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
 * seeing the results of the zapping for their coalition.
 * 
 * <p>
 * <br> Started: 07-29-2003
 * <p>
 * @author Dudley Girard
 */

public class BECoalZapAckMsg extends ExptMessage 
    { 
    public BECoalZapAckMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Coalition Zap Acknowledgment Message");
    
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
                    ec.addServerMessage(new BECoalZapAckMsg(out_args));
                    return null;
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BECoalZapAckMsg");
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
        if (ben.getExtraData("StaticZapResults") == null)
            {
            ben.setExtraData("StaticZapResults",new Hashtable());
            }
        if (ben.getExtraData("DynamicZapResults") == null)
            {
            ben.setExtraData("DynamicZapResults",new Hashtable());
            }
        Hashtable staticZapResults = (Hashtable)ben.getExtraData("StaticZapResults");
        Hashtable dynamicZapResults = (Hashtable)ben.getExtraData("DynamicZapResults");

        String coalType = (String)this.getArgs()[2];
        if (coalType.equals("Static"))
            {
            staticZapResults.put(this.getArgs()[1],this.getArgs()[3]);
            }
        else if (coalType.equals("Dynamic"))
            {
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

    // Adjust the earnings for all those nodes that were in static coalitions.
            Enumeration enm = staticZapResults.keys();
            while (enm.hasMoreElements())
                {
                Integer coalID = (Integer)enm.nextElement();

                BENode node = null;
                Enumeration enum2 = ben.getNodeList().elements();
                while (enum2.hasMoreElements())
                    {
                    node = (BENode)enum2.nextElement();
                    BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
                    if (nos.getCoalition().getCoalition() == coalID.intValue())
                        break;
                    }
    // First zap the earnings of the zapped free riders.
                Vector zappedFreeRiders = (Vector)staticZapResults.get(coalID);
                enum2 = zappedFreeRiders.elements();
                while (enum2.hasMoreElements())
                    {
                    Integer nodeID = (Integer)enum2.nextElement();
System.err.println("NID: "+nodeID);
                    BENode frNode = (BENode)ben.getNode(nodeID);
                    BENodeOrSubNet nos = (BENodeOrSubNet)frNode.getExptData("BENodeExchange");
                    nos.getCoalition().reduceFreeRiderEarnings(frNode,ben);
                    }
   // Have coalition members pay for the costs if there was zapping.
                if (zappedFreeRiders.size() > 0)
                    {
                    BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
                    nos.getCoalition().assessCosts(nos,ben);
                    }

        // Reset these two for the next round.
                ben.setExtraData("DynamicZapResults",new Hashtable());
                ben.setExtraData("StaticZapResults",new Hashtable());

                BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
                besa.executeAction(ew);
                }
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }