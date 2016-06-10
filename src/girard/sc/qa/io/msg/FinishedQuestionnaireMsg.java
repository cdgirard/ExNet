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

import java.util.Vector;

/**
 * This sends the done with Questionnaire message to the experimenter
 * for a client in the Questionnaire.
 * <p>
 * Started: 08-20-2002
 * <p>
 * @author Dudley Girard
 */

public class FinishedQuestionnaireMsg extends ExptMessage 
    { 
    public FinishedQuestionnaireMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: Finished Questionnaire Message");
    
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
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new FinishedQuestionnaireMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("FinishedQuestionnaireMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
        if (!(ew instanceof QuestionnaireExperimenterWindow))
            {
            new ErrorDialog("Wrong Experimenter Window.");
            return;
            }
System.err.println("FQM");
System.err.flush();
        if (ew.getExpApp().getExptRunning())
            ew.getExpApp().setReady(true,index.intValue());

        boolean flag = true;
        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!ew.getExpApp().getReady(x))
                flag = false;
            }
        if (flag)
            {
            ew.getExpApp().initializeReady();

            QuestionnaireExperimenterWindow qew = (QuestionnaireExperimenterWindow)ew;
            /* Possibly write some data to a temporary table for summing up earnings later */
            qew.savePayResults();

            Vector outData = ((Questionnaire)ew.getExpApp().getActiveAction()).getData();
            ew.saveOutputResults("qaDB",outData);
                    
            ew.getExpApp().startNextAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }