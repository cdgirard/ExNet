package girard.sc.be.obj;

import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.io.msg.BEJoinCoalitionWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;
import java.util.Enumeration;


/** 
 * The action that tiggers the action of allowing nodes to join or
 * form coalitions.
 * <p>
 * <br> Started: 07-07-2003
 * <p>
 * @author: Dudley Girard
 */

public class BEJoinCoalitionWindowStateAction extends BEStateAction implements Serializable
    {

    public BEJoinCoalitionWindowStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;
        
        boolean flag = false;

        ew.getExpApp().initializeReady();

        Enumeration enm = naew.getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();

            BENodeOrSubNet nos = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

            if (!nos.getCoalition().getCoalitionType().equals("None"))
                {
                flag = true;
                break;
                }
            }

        if (flag)
            {
            BEJoinCoalitionWindowMsg tmp = new BEJoinCoalitionWindowMsg(null);
            ew.getSML().sendMessage(tmp);
            }
        else
            {
            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            besa.executeAction(ew);
            }
        }
    }
