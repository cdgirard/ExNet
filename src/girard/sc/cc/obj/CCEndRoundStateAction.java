package girard.sc.cc.obj;
/* 
   This state action is used to end a round after either time has run out
   or if all exchanges have been completed.

   Author: Dudley Girard
   Started: 7-10-2001
   Modified: 7-24-2001
*/

import girard.sc.cc.io.msg.CCEndRoundMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

public class CCEndRoundStateAction extends CCStateAction implements Serializable
    {

    public CCEndRoundStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        CCEndRoundMsg tmp = new CCEndRoundMsg(null);
        ew.getEOApp().initializeWLMessage(tmp);
        ew.getSML().sendMessage(tmp);
        }
    }
