package girard.sc.qa.io.msg;

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
import girard.sc.qa.awt.QAExptStartWindow;
import girard.sc.qa.awt.QuestionnaireObserverWindow;
import girard.sc.ques.obj.BaseQuestion;

import java.util.Hashtable;
import java.util.Vector;

/**
 * The start message for the Questionnaire application.
 * <p> 
 * Started: 08-04-2002
 * <p>
 * @author Dudley Girard
 */

public class StartQuestionnaireReqMsg extends ExptMessage 
    { 
    public StartQuestionnaireReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Hashtable))
            {
            new ErrorDialog("Wrong argument type. - StartQuestionnaireReqMsg");
            return;
            }

        ExptOverlord eo = cw.getEOApp();
        ClientExptInfo cei = cw.getExpApp();
        ExptMessageListener ml = cw.getSML();

        Hashtable h = (Hashtable)args[0];

        cei.setActiveAction(h);

        cw.setWatcher(false);

        Hashtable extraData = (Hashtable)h.get("ExtraData");
        Hashtable iw = (Hashtable)extraData.get("InitialWindow");
        String a = (String)iw.get("Activate");
        if (a.equals("Yes"))
            {
            new QAExptStartWindow(eo,cei,ml);
            }
        else
            {
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
                        err_args[1] = new String("StartQuestionnaireReqMsg");
                        return new ExptErrorMsg(err_args);
                        }

                    Vector quesV = (Vector)((Hashtable)args[0]).get("Questionaire");
                    Vector transV = (Vector)((Hashtable)args[0]).get("Transitions");
                    Hashtable extraData = (Hashtable)((Hashtable)args[0]).get("ExtraData");

                    for (int x=0;x<ec.getNumUsers();x++)
                        {
                        Hashtable h = new Hashtable();
                        h.put("Questionaire",quesV.elementAt(x));
                        h.put("Transitions",transV.elementAt(x));
                        h.put("ExtraData",extraData);
                        Object[] out_args = new Object[1];
                        out_args[0] = h;
                        ec.addUserMessage(new StartQuestionnaireReqMsg(out_args),x);
                        }

                    ec.sendToAllObservers(new StartQuestionnaireReqMsg(args));
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
            err_args[1] = new String("StartQuestionnaireReqMsg");
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

        if (!(args[0] instanceof Hashtable))
            {
            new ErrorDialog("Wrong argument type.");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();

        oei.setActiveAction(args[0]);

        ow.setWatcher(false);
        
        new QuestionnaireObserverWindow(eo,oei,ml);
        }
    }