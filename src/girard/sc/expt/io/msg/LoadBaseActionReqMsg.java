package girard.sc.expt.io.msg;

import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.sql.LoadBaseActionReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.util.Hashtable;

/**
 * Used to load a specific BaseAction.
 * <p>
 * <br> Modified: 10-08-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class LoadBaseActionReqMsg extends ExptMessage 
    {
/**
 * The constructor function.  The Object array passed in should have two objects.
 * The first object is a String giving the name of the BaseAction, the second Object
 * is a copy of the BaseAction type you are loading.
 *
 * @param args[] The Object array of information needed to load the BaseAction.
 */
    public LoadBaseActionReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Used to set the action to preform for a WLGeneralServerConnection.  Gets the
 * filename and the BaseAction from the m_args[] variable.  Uses this information to
 * initialize a LoadBaseActionReq.  Upon getting the ResultSet for the BaseAction, it
 * uses applyResultSet to initialize the BaseAction object with the proper settings.
 * <p>
 * @param wlgsc The WLGeneralServerConnection that received the message.
 * @return Returns a LoadBaseActionReqMsg with the retrieved BaseAction, otherwise returns an ExptErrorMsg.
 * @see girard.sc.expt.obj.BaseAction
 * @see girard.sc.expt.sql.LoadBaseActionReq
 * @see girard.sc.io.msg.TCPMessage#m_args
 * @see java.sql.ResultSet
 */
    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Hashtable) || !(args[1] instanceof BaseAction))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadBaseActionReqMsg");
            err_args[1] = new String("LoadBaseActionReqMsg");
            return new ExptErrorMsg(null);
            }

        Hashtable fileInfo = (Hashtable)args[0];
        BaseAction ba = (BaseAction)args[1];

        try 
            { 
            LoadBaseActionReq tmp = new LoadBaseActionReq(fileInfo,ba.getDB(),ba.getDBTable(),wlgsc,this);

            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from databse - LoadBaseActionReq.");
                err_args[1] = new String("LoadBaseActionReqMsg");
                return new ExptErrorMsg(err_args);
                }

            if (rs.next())
                {
                ba.applyResultSet(rs);
   
                Object[] out_args = new Object[1];
                out_args[0] = ba;

                return new LoadBaseActionReqMsg(out_args);
                }
            else
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Something bad happened in LoadBaseActionReqMsg");
                err_args[1] = new String("LoadBaseActionReqMsg");
                return new ExptErrorMsg(err_args);
                }
            }
        catch( Exception e )
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadBaseActionReqMsg: "+e);
            err_args[1] = new String("LoadBaseActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
