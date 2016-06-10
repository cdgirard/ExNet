package girard.sc.be.obj;
/* 
   The object that contains the preset color values for BE exchange
   network class objects.

   Author: Dudley Girard
   Started: 1-24-2001
*/

import girard.sc.be.io.msg.BERoundWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

public class BERoundWindowStateAction extends BEStateAction implements Serializable
    {
    BEPeriod m_bep;

    public BERoundWindowStateAction (BEPeriod bep)
        {
        m_bep = bep;
        }

    public void executeAction(ExperimenterWindow ew)
        {
        m_bep.setCurrentTime(m_bep.getTime());
        BERoundWindowMsg tmp = new BERoundWindowMsg(null);
        ew.getSML().sendMessage(tmp);
        }
    }
