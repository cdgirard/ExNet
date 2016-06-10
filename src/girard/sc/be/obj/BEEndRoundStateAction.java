package girard.sc.be.obj;
/* 
   The object that contains the preset color values for BE exchange
   network class objects.

   Author: Dudley Girard
   Started: 1-24-2001
*/

import girard.sc.be.io.msg.BEEndRoundMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

public class BEEndRoundStateAction extends BEStateAction implements Serializable
    {

    public BEEndRoundStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        BEEndRoundMsg tmp = new BEEndRoundMsg(null);
        ew.getSML().sendMessage(tmp);
        }
    }
