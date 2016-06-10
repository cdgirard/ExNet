package girard.sc.ce.obj;

import girard.sc.ce.io.msg.CEEndRoundMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

/**
 * Informs the CENetworkAction that the round is completly over by
 * sending a BEEndRoundMsg.
 * <p>
 * <br> Started: 02-20-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEndRoundStateAction extends CEStateAction implements Serializable
    {

    public CEEndRoundStateAction ()
        {
        }

    public void executeAction(ExperimenterWindow ew)
        {
        CEEndRoundMsg tmp = new CEEndRoundMsg(null);
        ew.getSML().sendMessage(tmp);
        }
    }
