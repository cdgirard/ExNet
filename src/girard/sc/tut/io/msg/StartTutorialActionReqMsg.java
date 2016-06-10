package girard.sc.tut.io.msg;

/* The start message for the Tutorial Action.

Author: Dudley Girard
Started: 1-08-2002
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.tp.obj.TutorialPage;
import girard.sc.tut.awt.TutorialActionObserverWindow;

import java.util.Hashtable;
import java.util.Vector;

public class StartTutorialActionReqMsg extends ExptMessage 
    { 
    public StartTutorialActionReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Vector))
            {
            new ErrorDialog("Wrong argument type. - StartTutorialActionReqMsg");
            return;
            }

        ExptOverlord eo = cw.getEOApp();
        ClientExptInfo cei = cw.getExpApp();
        ExptMessageListener ml = cw.getSML();
        Vector pages = (Vector)args[0];

        Hashtable h = new Hashtable();
        Hashtable extraData = new Hashtable();
        h.put("TutPages",pages);
        h.put("ExtraData",extraData);
        h.put("CurrentPage",new Integer(0));

        cei.setActiveAction(h);  // In this case a TutorialPage

        cw.setWatcher(false);
        
        if (pages.size() > 0)
            {
            TutorialPage tp = (TutorialPage)pages.elementAt(0);
            tp.startPage(eo,cei,ml);

            Object[] out_args = new Object[1];
            out_args[0] = new Integer(1);
            NextTutorialPageReqMsg tmp = new NextTutorialPageReqMsg(out_args);
            ml.sendMessage(tmp);
            }
        else
            {
            // Popup a please wait while others finish window.
            TutorialPage.createWaitWindow(eo,cei,ml);
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: Start Tutorial Action Request Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    if (!ec.allRegistered())
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Least one user not registered.");
                        err_args[1] = new String("StartTutorialActionReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    for (int x=0;x<ec.getNumUsers();x++)
                        {
                        Object[] out_args = new Object[1];
                        out_args[0] = ((Vector)args[0]).elementAt(x);
                        ec.addUserMessage(new StartTutorialActionReqMsg(out_args),x);
                        }
                    ec.sendToAllObservers(new StartTutorialActionReqMsg(args));
                    return null;
                    }
                else
                    {
                    // if (!ec.allRegistered())
                    //     return null;
                    // Object[] out_args = new Object[1];
                    // out_args[0] = new Integer(index);
                    // ec.addServerMessage(new StartTutorialActionReqMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("StartTutorialActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        Object[] args = this.getArgs();

        if (!(args[0] instanceof Vector))
            {
            new ErrorDialog("Wrong argument type.");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();

        oei.setActiveAction(args[0]);  // In this case a BENetwork

        ow.setWatcher(false);
        
        new TutorialActionObserverWindow(eo,oei,ml);
        }
    }