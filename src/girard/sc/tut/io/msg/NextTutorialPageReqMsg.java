package girard.sc.tut.io.msg;

/* The sends the next tutorial page to a client for the Tutorial Action.

Author: Dudley Girard
Started: 1-08-2002
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.tut.awt.TutorialActionExperimenterWindow;
import girard.sc.tut.awt.TutorialActionObserverWindow;

public class NextTutorialPageReqMsg extends ExptMessage 
    { 
    public NextTutorialPageReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: Next Tutorial Page Request Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    ec.sendToAllObservers(new NextTutorialPageReqMsg(args));
                    return null;
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[2];
                    out_args[0] = new Integer(index);
                    out_args[1] = args[0];
                    ec.addServerMessage(new NextTutorialPageReqMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("NextTutorialPageReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
  // System.err.println("NTPRM Rec.");

        if (!(ew instanceof TutorialActionExperimenterWindow))
            {
            new ErrorDialog("Wrong Experimenter Window.");
            return;
            }

        if ((ew.getExpApp().getExptRunning()) && (!ew.getExpApp().getExptStopping()))
            {
  //  System.err.println("NTPRM Processing...");
            Integer pageIndex;
            TutorialActionExperimenterWindow taew = (TutorialActionExperimenterWindow)ew;

            pageIndex = (Integer)this.getArgs()[1];

            taew.updateUserPageIndex(index.intValue(),pageIndex.intValue());

   // To let any observers know what is going on.
            Object[] out_args = new Object[2];
            out_args[0] = index;
            out_args[1] = pageIndex;
            NextTutorialPageReqMsg tmp = new NextTutorialPageReqMsg(out_args);
            ew.getSML().sendMessage(tmp);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        Integer index = (Integer)this.getArgs()[0];

        if (!(ow instanceof TutorialActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window.");
            return;
            }

        if ((ow.getExpApp().getExptRunning()) && (!ow.getExpApp().getExptStopping()))
            {
            Integer pageIndex;
            TutorialActionObserverWindow taow = (TutorialActionObserverWindow)ow;

            pageIndex = (Integer)this.getArgs()[1];

            taow.updateUserPageIndex(index.intValue(),pageIndex.intValue());
            }
        }
    }