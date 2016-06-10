package girard.sc.cc.obj;
/* 
   This state action is used to start a new round by having all clients
   get a round window that gives subjects information on the network.

   Author: Dudley Girard
   Started: 5-24-2001
   Modified: 7-24-2001
*/

import girard.sc.cc.io.msg.CCRoundWindowMsg;
import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

public class CCRoundWindowStateAction extends CCStateAction implements Serializable
    {
    CCPeriod m_ccp;

    public CCRoundWindowStateAction (CCPeriod ccp)
        {
        m_ccp = ccp;
        }

    public void executeAction(ExperimenterWindow ew)
        {
        m_ccp.setCurrentTime(m_ccp.getTime());
        CCRoundWindowMsg tmp = new CCRoundWindowMsg(null);
        ew.getSML().sendMessage(tmp);
        }
    }
