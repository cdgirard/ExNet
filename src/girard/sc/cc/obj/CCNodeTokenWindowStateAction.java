package girard.sc.cc.obj;
/* 
   The action that tiggers the action of nodes sending tokens to
   other nodes.

   Author: Dudley Girard
   Started: 7-3-2001
*/

import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.io.msg.CCNodeTokenWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;
import java.util.Enumeration;

public class CCNodeTokenWindowStateAction extends CCStateAction implements Serializable
    {

    public CCNodeTokenWindowStateAction ()
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

            CCNodeTokens nt1 = (CCNodeTokens)n1.getExptData("CCNodeTokens");
            CCNodeTokens nt2 = (CCNodeTokens)n2.getExptData("CCNodeTokens");

            tmpEdge.setCompleted(false);

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

        if (flag)
            {
            CCNodeTokenWindowMsg tmp = new CCNodeTokenWindowMsg(null);
            ew.getSML().sendMessage(tmp);
            }
        else
            {
            CCStateAction ccsa = ((CCNetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            ccsa.executeAction(ew);
            }
        }
    }
