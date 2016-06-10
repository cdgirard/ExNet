package girard.sc.ce.obj;

import girard.sc.ce.io.msg.CERoundWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

/**
 * The sends out a message to display the start round windows
 * to the clients.
 * <p>
 * <br> Started: 02-10-2003
 * <p>
 *
 * @author: Dudley Girard
 */

public class CERoundWindowStateAction extends CEStateAction implements Serializable
    {
    CEPeriod m_cep;

    public CERoundWindowStateAction (CEPeriod cep)
        {
        m_cep = cep;
        }

    public void executeAction(ExperimenterWindow ew)
        {
        m_cep.setCurrentTime(m_cep.getTime());
        CERoundWindowMsg tmp = new CERoundWindowMsg(null);
        ew.getSML().sendMessage(tmp);
        }
    }
