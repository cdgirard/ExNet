package girard.sc.cc.obj;
/* 
   The action that tiggers the action of nodes sending fuzzies to
   other nodes.

   Author: Dudley Girard
   Started: 6-28-2001
*/

import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.io.msg.CCNodeFuzzyWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;
import java.util.Enumeration;

public class CCNodeFuzzyWindowStateAction extends CCStateAction implements Serializable
    {

    public CCNodeFuzzyWindowStateAction ()
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

            CCNodeFuzzies nf1 = (CCNodeFuzzies)n1.getExptData("CCNodeFuzzies");
            CCNodeFuzzies nf2 = (CCNodeFuzzies)n2.getExptData("CCNodeFuzzies");

            tmpEdge.setCompleted(false);
System.err.println("NF1: "+nf1.isEdgeActive(tmpEdge)+"NF2: "+nf2.isEdgeActive(tmpEdge));
            if ((nf1.isEdgeActive(tmpEdge)) && (nf2.isEdgeActive(tmpEdge)))
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
            CCNodeFuzzyWindowMsg tmp = new CCNodeFuzzyWindowMsg(null);
            ew.getSML().sendMessage(tmp);
            }
        else
            {
            CCStateAction ccsa = ((CCNetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            ccsa.executeAction(ew);
            }
        }
    }
