package girard.sc.tut.io.msg;

import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.sql.LoadBaseActionReq;
import girard.sc.tp.obj.TutorialPage;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class LoadTutorialActionReqMsg extends ExptMessage 
    { 
    public LoadTutorialActionReqMsg (Object args[])
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
            err_args[0] = new String("Something bad happened in LoadTutorialActionReqMsg");
            err_args[1] = new String("LoadTutorialActionReqMsg");
            return new ExptErrorMsg(null);
            }

        Vector tut = (Vector)args[0];
        
        Vector newTut = new Vector();
        Enumeration enm = tut.elements();
        while (enm.hasMoreElements())
            {
            Vector newUserTut = new Vector();
            Vector userTut = (Vector)enm.nextElement();
            Enumeration enum2 = userTut.elements();
            while (enum2.hasMoreElements())
                {
                TutorialPage tp = (TutorialPage)((TutorialPage)enum2.nextElement()).clone();
                try 
                    {
                    Hashtable h = new Hashtable();
                    h.put("FileName",tp.getFileName());
                    h.put("Desc",tp.getDesc());
                    if (tp.getAppName() != null)
                        {
                        h.put("App Name",tp.getAppName());
                        h.put("App ID",tp.getAppID());
                        }
                    h.put("UserID",new Integer(tp.getUserID()));

                    LoadBaseActionReq tmp = new LoadBaseActionReq(h,tp.getDB(),tp.getDBTable(),wlgsc,this);

                    ResultSet rs = tmp.runQuery();

                    if (rs.next())
                        {
                        tp.applyResultSet(rs);
                        }
                     }
                catch( Exception e ) { wlgsc.addToLog(e.getMessage()); }
                newUserTut.addElement(tp);
                }
            newTut.addElement(newUserTut);
            }
        Object[] out_args = new Object[1];
        out_args[0] = newTut;
        return new LoadTutorialActionReqMsg(out_args);
        }
    }
