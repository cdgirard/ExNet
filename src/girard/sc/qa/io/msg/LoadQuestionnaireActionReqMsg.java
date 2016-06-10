package girard.sc.qa.io.msg;

import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.sql.LoadBaseActionReq;
import girard.sc.ques.obj.BaseQuestion;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class LoadQuestionnaireActionReqMsg extends ExptMessage 
    { 
    public LoadQuestionnaireActionReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Vector))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadQuestionnaireActionReqMsg");
            err_args[1] = new String("LoadQuestionnaireActionReqMsg");
            return new ExptErrorMsg(null);
            }

        Vector qa = (Vector)args[0];

        Vector newQa = new Vector();
        Enumeration enm = qa.elements();
        while (enm.hasMoreElements())
            {
            Vector newUserQa = new Vector();
            Vector userQa = (Vector)enm.nextElement();
            Enumeration enum2 = userQa.elements();
            while (enum2.hasMoreElements())
                {
                BaseQuestion bq = (BaseQuestion)((BaseQuestion)enum2.nextElement()).clone();
                try 
                    {
                    Hashtable h = new Hashtable();
                    h.put("FileName",bq.getFileName());
                    h.put("Desc",bq.getDesc());
                    if (bq.getAppName() != null)
                        {
                        h.put("App Name",bq.getAppName());
                        h.put("App ID",bq.getAppID());
                        }
                    h.put("UserID",new Integer(bq.getUserID()));

                    LoadBaseActionReq tmp = new LoadBaseActionReq(h,bq.getDB(),bq.getDBTable(),wlgsc,this);

                    ResultSet rs = tmp.runQuery();

                    if (rs.next())
                        {
                        bq.applyResultSet(rs);
                        }
                    }
                catch( Exception e ) { wlgsc.addToLog(e.getMessage()); }
                newUserQa.addElement(bq);
                }
            newQa.addElement(newUserQa);
            }
        Object[] out_args = new Object[1];
        out_args[0] = newQa;
        return new LoadQuestionnaireActionReqMsg(out_args);
        }
    }
