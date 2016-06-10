package girard.sc.be.obj;

import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.io.msg.BECoalitionZapWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;
import java.util.Enumeration;


/** 
 * The action that tiggers the action of allowing nodes to zap any freeriders.
 * <p>
 * <br> Started: 07-22-2003
 * <p>
 * @author: Dudley Girard
 */

public class BEZapWindowStateAction extends BEStateAction implements Serializable
    {
    BENetworkActionExperimenterWindow m_naew;


    public BEZapWindowStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        m_naew = (BENetworkActionExperimenterWindow)ew;
        
        boolean flag = false;

        ew.getExpApp().initializeReady();

        Enumeration enm = m_naew.getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();
            BENodeOrSubNet nos = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

            if ((nos.getCoalition().getFormed()) && (nos.getCoalition().getZapping()))
                {
                if (nos.getCoalition().areZappableFreeRiders(nos,m_naew.getNetwork()))
                    {
                    flag = true;
                    break;
                    }
                }
            }
System.err.println("ZSA: "+flag);
        if (flag)
            {
            BECoalitionZapWindowMsg tmp = new BECoalitionZapWindowMsg(null);
            ew.getSML().sendMessage(tmp);
            }
        else
            {
            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            besa.executeAction(ew);
            }
        }
    }
