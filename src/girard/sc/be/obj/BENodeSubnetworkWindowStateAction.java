package girard.sc.be.obj;
/* 
   The object that contains the preset color values for BE exchange
   network class objects.

   Author: Dudley Girard
   Started: 1-24-2001
*/

import girard.sc.be.io.msg.BENodeSNWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

public class BENodeSubnetworkWindowStateAction extends BEStateAction implements Serializable
    {

    public BENodeSubnetworkWindowStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        BENodeSNWindowMsg tmp = new BENodeSNWindowMsg(null);
        ew.getSML().sendMessage(tmp);
        }
    }