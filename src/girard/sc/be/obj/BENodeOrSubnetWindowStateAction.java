package girard.sc.be.obj;

import girard.sc.be.io.msg.BENodeOrSNWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

/**
 * Displays in detail the ordering and subnetwork info.
 * <p>
 * <br> Started: 09-18-2002
 * <p>
 * @author Dudley Girard
 */

public class BENodeOrSubnetWindowStateAction extends BEStateAction implements Serializable
    {

    public BENodeOrSubnetWindowStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        BENodeOrSNWindowMsg tmp = new BENodeOrSNWindowMsg(null);
        ew.getSML().sendMessage(tmp);
        }
    }