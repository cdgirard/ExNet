package girard.sc.expt.io.msg;

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;

/**
 * ServerDownMsg:
 * <p>
 * <br> Started: 04-07-2003
 * <p>
 *
 * @author Dudley Girard
 */

public class ServerDownMsg extends ExptMessage 
    { 
    public ServerDownMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        cw.getExpApp().setExptRunning(false);
        cw.setWatcher(false);
        }

/**
 * This message is never actually sent, so never actually called.
 */
    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        return null;
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        ew.getExpApp().setExptRunning(false);
        ew.setWatcher(false);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        ow.getExpApp().setExptRunning(false);
        ow.setWatcher(false);
        }
    }