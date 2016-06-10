package girard.sc.qa.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.qa.awt.QuestionnaireExperimenterWindow;
import girard.sc.qa.awt.QuestionnaireObserverWindow;

/**
 * Informs the experimenter as to the progress the subjects are making.
 * <p>
 * Started: 08-04-2002
 * <p>
 * @author Dudley Girard
 */

public class NextQuestionNoticeMsg extends ExptMessage 
    { 
    public NextQuestionNoticeMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: Next Question Notice Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    ec.sendToAllObservers(new NextQuestionNoticeMsg(args));
                    return null;
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[2];
                    out_args[0] = new Integer(index);
                    out_args[1] = args[0];
                    ec.addServerMessage(new NextQuestionNoticeMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("NextQuestionNoticeMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        Integer ques = (Integer)this.getArgs()[1];
        
   // System.err.println("NQNM Rec.");

        if (!(ew instanceof QuestionnaireExperimenterWindow))
            {
            new ErrorDialog("Wrong Experimenter Window.");
            return;
            }

        if ((ew.getExpApp().getExptRunning()) && (!ew.getExpApp().getExptStopping()))
            {
    // System.err.println("NQNM Processing...");

            QuestionnaireExperimenterWindow qew = (QuestionnaireExperimenterWindow)ew;

            qew.updateUserQuestionIndex(index.intValue(),ques.intValue());

   // To let any observers know what is going on.
            Object[] out_args = new Object[2];
            out_args[0] = index;
            out_args[1] = ques;
            NextQuestionNoticeMsg tmp = new NextQuestionNoticeMsg(out_args);
            ew.getSML().sendMessage(tmp);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        Integer index = (Integer)this.getArgs()[0];
        Integer ques = (Integer)this.getArgs()[1];

        if (!(ow instanceof QuestionnaireObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window.");
            return;
            }

        if ((ow.getExpApp().getExptRunning()) && (!ow.getExpApp().getExptStopping()))
            {
            QuestionnaireObserverWindow qow = (QuestionnaireObserverWindow)ow;

            qow.updateUserQuestionIndex(index.intValue(),ques.intValue());
            }
        }
    }