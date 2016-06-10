package girard.sc.qa.io.msg;

import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.EndExptReqMsg;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.qa.awt.QuestionnaireExperimenterWindow;
import girard.sc.qa.obj.Questionnaire;

import java.util.Vector;

/**
 * StopQuestionnaireReqMsg: Used to stop a Questionnaire when its in the middle
 * of being run.
 * <p>
 * Started: 08-12-2002
 * <p>
 *
 * @author Dudley Girard
 */


public class StopQuestionnaireReqMsg extends ExptMessage 
    { 
    public StopQuestionnaireReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        cw.getExpApp().setExptStopping(true); // We are stopping the experiment.
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: Stop Questionnaire Request Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    ec.sendToAllUsers(new StopQuestionnaireReqMsg(args));
                    ec.sendToAllObservers(new StopQuestionnaireReqMsg(args));
                    ec.addServerMessage(new StopQuestionnaireReqMsg(args));
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
            err_args[1] = new String("StopQuestionnaireReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        ew.getExpApp().stopActiveSimActors();
System.err.println("Done stopping SimActors");
System.err.flush();

        QuestionnaireExperimenterWindow qew = (QuestionnaireExperimenterWindow)ew;

         /* Save gathered output data here */
        Vector outData = ((Questionnaire)qew.getExpApp().getActiveAction()).getData();
        qew.saveOutputResults("qaDB",outData);

        qew.savePayResults();

        EndExptReqMsg tmp = new EndExptReqMsg(null);
        ew.getSML().sendMessage(tmp);
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;  

        ow.getExpApp().setExptStopping(true);
        }
    }