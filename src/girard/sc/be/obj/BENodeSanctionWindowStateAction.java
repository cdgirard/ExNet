package girard.sc.be.obj;

import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.io.msg.BENodeSanctionWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;
import java.util.Enumeration;

/** 
 * The action that tiggers the action of nodes sending sanctions to
 * other nodes.
 * <p>
 * <br> Started: 09-19-2002
 * <p>
 * @author: Dudley Girard
 */

public class BENodeSanctionWindowStateAction extends BEStateAction implements Serializable
    {

    public BENodeSanctionWindowStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow)ew;
        
        boolean flag = false;

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge tmpEdge = (BEEdge)enm.nextElement();

            BENode n1 = (BENode)naew.getNetwork().getNode(tmpEdge.getNode1());
            BENode n2 = (BENode)naew.getNetwork().getNode(tmpEdge.getNode2());

            BENodeSanctions ns1 = (BENodeSanctions)n1.getExptData("BENodeSanctions");
            BENodeSanctions ns2 = (BENodeSanctions)n2.getExptData("BENodeSanctions");

            boolean tmpFlag = tmpEdge.getCompleted();

            tmpEdge.setCompleted(false);

            if ((ns1.isEdgeActive(tmpEdge)) && (ns2.isEdgeActive(tmpEdge)))
                {
                tmpEdge.setActive(true);
                }
            else
                {
                tmpEdge.setActive(false);
                }

            tmpEdge.setCompleted(tmpFlag);

            if (tmpEdge.getActive())
                flag = true;
            }

        if (flag)
            {
            BENodeSanctionWindowMsg tmp = new BENodeSanctionWindowMsg(null);
            ew.getSML().sendMessage(tmp);
            }
        else
            {
            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            besa.executeAction(ew);
            }
        }
    }
