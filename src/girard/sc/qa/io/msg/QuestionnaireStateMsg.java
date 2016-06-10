package girard.sc.qa.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.qa.awt.QuestionnaireObserverWindow;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * QuestionnaireStateMsg: Sends the present state of a Questionnaire to
 * an observer that has just joined in the middle of an experiment.
 * <p>
 * Started: 08-12-2002
 * <p>
 *
 * @author Dudley Girard
 */


public class QuestionnaireStateMsg extends ExptMessage 
    { 
    public QuestionnaireStateMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {  
// System.err.println("ESR: Questionnaire State Message");
// System.err.flush();

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {                    
                    ec.addObserverMessage(new QuestionnaireStateMsg(this.getArgs()),(Integer)this.getArgs()[0]);
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment to get info on.");
            err_args[1] = new String("QuestionnaireStateMsg");
            return new ExptErrorMsg(err_args);
            }

        Object[] err_args = new Object[2];
        err_args[0] = new String("Should not be here.");
        err_args[1] = new String("QuestionnaireStateMsg");
        return new ExptErrorMsg(err_args);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        Object[] args = this.getArgs();

        if (!(args[1] instanceof Hashtable) || !(args[2] instanceof Hashtable))
            {
            new ErrorDialog("Wrong argument type. - QuestionnaireStateMsg");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();

        oei.setReadyToStart(true);
        oei.setExptRunning(true);

        oei.setActiveAction(args[1]);
        ow.setWatcher(false);
        
        QuestionnaireObserverWindow tmpWin = new QuestionnaireObserverWindow(eo,oei,ml);

        Hashtable h = (Hashtable)args[2];
        Enumeration enm = h.keys();
        while(enm.hasMoreElements())
            {
            Integer user = (Integer)enm.nextElement();
            Integer ques = (Integer)h.get(user);
            tmpWin.updateUserQuestionIndex(user.intValue(),ques.intValue());
            }
        }
    }