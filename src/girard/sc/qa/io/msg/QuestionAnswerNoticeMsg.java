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
import girard.sc.qa.obj.Questionnaire;
import girard.sc.ques.obj.AnswerOutputObject;

import java.util.Vector;

/**
 * Informs the experimenter as to the answers the subjects are making.
 * <p>
 * Started: 08-06-2002
 * <p>
 * @author Dudley Girard
 */

public class QuestionAnswerNoticeMsg extends ExptMessage 
    { 
    public QuestionAnswerNoticeMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: Question Answer Notice Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    return null;
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(index);
                    out_args[1] = args[0];
                    out_args[2] = args[1];
                    out_args[3] = args[2];
                    ec.addServerMessage(new QuestionAnswerNoticeMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("QuestionAnswerNoticeMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
   // System.err.println("QANM Rec.");

        if (!(ew instanceof QuestionnaireExperimenterWindow))
            {
            new ErrorDialog("Wrong Experimenter Window.");
            return;
            }

        if ((ew.getExpApp().getExptRunning()) && (!ew.getExpApp().getExptStopping()))
            {
    // System.err.println("QANM Processing...");

            QuestionnaireExperimenterWindow qew = (QuestionnaireExperimenterWindow)ew;

            /* update data output here */
            int ui = ((Integer)this.getArgs()[0]).intValue();
            int qi = ((Integer)this.getArgs()[1]).intValue();
            int ti = ((Integer)this.getArgs()[2]).intValue();
            String ans = (String)this.getArgs()[3];
            int tp = (int)qew.getPresentTime();

            AnswerOutputObject data = new AnswerOutputObject(ew.getExpApp().getExptOutputID(),ew.getExpApp().getActionIndex(),ui,qi,ti,ans,tp);

            Vector outData = ((Questionnaire)ew.getExpApp().getActiveAction()).getData();
            outData.addElement(data);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }