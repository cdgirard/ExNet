package girard.sc.cc.obj;
/* 
   The action that tiggers the action of nodes sending sanctions to
   other nodes.

   Author: Dudley Girard
   Started: 7-3-2001
*/

import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.io.msg.CCNodeSanctionWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;
import java.util.Enumeration;

public class CCNodeSanctionWindowStateAction extends CCStateAction implements Serializable
    {

    public CCNodeSanctionWindowStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        CCNetworkActionExperimenterWindow naew = (CCNetworkActionExperimenterWindow)ew;
        
        boolean flag = false;

        Enumeration enm = naew.getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge tmpEdge = (CCEdge)enm.nextElement();

            CCNode n1 = (CCNode)naew.getNetwork().getNode(tmpEdge.getNode1());
            CCNode n2 = (CCNode)naew.getNetwork().getNode(tmpEdge.getNode2());

            CCNodeSanctions ns1 = (CCNodeSanctions)n1.getExptData("CCNodeSanctions");
            CCNodeSanctions ns2 = (CCNodeSanctions)n2.getExptData("CCNodeSanctions");

            tmpEdge.setCompleted(false);

            if ((ns1.isEdgeActive(tmpEdge)) && (ns2.isEdgeActive(tmpEdge)))
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

        if (flag)
            {
            CCNodeSanctionWindowMsg tmp = new CCNodeSanctionWindowMsg(null);
            ew.getSML().sendMessage(tmp);
            }
        else
            {
            CCStateAction ccsa = ((CCNetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            ccsa.executeAction(ew);
            }
        }
    }
