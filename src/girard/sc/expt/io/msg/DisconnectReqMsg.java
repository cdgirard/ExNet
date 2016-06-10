package girard.sc.expt.io.msg;

/* DisconnectReqMsg: This disconnects the server, since just closing
   the connections on one end is not enough.

Author: Dudley Girard
Started: 8-9-2001
*/

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;

public class DisconnectReqMsg extends ExptMessage 
    { 
    public DisconnectReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow ow)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        esc.setFlag(false);

        return null;
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }