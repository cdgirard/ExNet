package girard.sc.qa.io.msg;

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.ques.obj.BaseQuestion;

import java.util.Hashtable;
import java.util.Vector;

/**
 * The unpause message for the beginning of the Questionnaire application.
 * <p> 
 * <br>Started: 10-14-2002
 * <p>
 * @author Dudley Girard
 */

public class QABeginQuestionnaireReqMsg extends ExptMessage 
    { 
    public QABeginQuestionnaireReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        ExptOverlord eo = cw.getEOApp();
        ClientExptInfo cei = cw.getExpApp();
        ExptMessageListener ml = cw.getSML();

        Hashtable h = (Hashtable)cei.getActiveAction();

        cw.setWatcher(false);

        Vector questions = (Vector)h.get("Questionaire");
        
        if (questions.size() > 0)
            {
            BaseQuestion bq = (BaseQuestion)questions.elementAt(0);
            bq.showQuestion(eo,cei,ml,0);

            Object[] out_args = new Object[1];
            out_args[0] = new Integer(1);
            NextQuestionNoticeMsg tmp = new NextQuestionNoticeMsg(out_args);
            ml.sendMessage(tmp);
            }
        else
            {
            // Popup a please wait while others finish window.
            BaseQuestion.createWaitWindow(eo,cei,ml);
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();
    
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
                        err_args[1] = new String("QABeginQuestionnaireReqMsg");
                        return new ExptErrorMsg(err_args);
                        }

                    ec.sendToAllUsers(new QABeginQuestionnaireReqMsg(args));
                    ec.sendToAllObservers(new QABeginQuestionnaireReqMsg(args));
                    return null;
                    }
                else
                    {
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("QABeginQuestionnaireReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }