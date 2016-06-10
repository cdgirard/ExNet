package girard.sc.expt.io.msg;

import girard.sc.expt.obj.Experiment;
import girard.sc.expt.sql.ExptFileListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to retrieve the list of Experiments accessible by a user based on user ID and
 * a user's group access.
 * <p>
 * <br> Started: 2001
 * <br> Modified: 10-23-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class ExptFileListReqMsg extends ExptMessage 
    {

/**
 * The constructor, normally a list of access groups for the user is
 * passed in.
 *
 * @param args[] An array of Objects, usually one of which is a Vector of Hashtables
 * listing all the accessible access groups.
 */
    public ExptFileListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        try
            {
            Vector ags = (Vector)args[0];
            String ag = null;
            Vector netInfo = new Vector();

            ExptFileListReq tmp = new ExptFileListReq(ag,wlgsc,this);

            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from databse - ExptFileListReq.");
                err_args[1] = new String("ExptFileListReqMsg");
                return new ExptErrorMsg(err_args);
                }

            while(rs.next()) 
                {
                Experiment exp = new Experiment(rs);
                
                netInfo.addElement(exp);
                }

            Enumeration enm = ags.elements();
            while (enm.hasMoreElements())
                {
                Hashtable h = (Hashtable)enm.nextElement();
                ag = (String)h.get("App ID");

                if (ag != null)
                    {
                    ExptFileListReq tmp2 = new ExptFileListReq(ag,wlgsc,this);

                    ResultSet rs2 = tmp2.runQuery();

                    if (rs2 == null)
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Failed to get type information from databse - ExptFileListReq.");
                        err_args[1] = new String("ExptFileListReqMsg");
                        return new ExptErrorMsg(err_args);
                        }

                    while(rs2.next()) 
                        {
                        Experiment exp = new Experiment(rs2);
                
                        netInfo.addElement(exp);
                        }
                    }
                }

            Object[] out_args = new Object[1];
            out_args[0] = netInfo;

            return new ExptFileListReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in ExnetExptFileListReqMsg");
            err_args[1] = new String("ExptFileListReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
