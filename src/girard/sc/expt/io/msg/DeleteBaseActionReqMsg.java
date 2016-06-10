package girard.sc.expt.io.msg;

import girard.sc.expt.sql.DeleteBaseActionReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.util.Hashtable;

/**
 * Used to delete a specific BaseAction.
 *
 * <p>
 * <br>Modified: 10-09-2002
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 */

public class DeleteBaseActionReqMsg extends ExptMessage 
    {
/**
 * The constructor function.  The Object array passed in should have three objects.
 * The first object is a String giving the name of the BaseAction, the second Object
 * is the database that it is stored in, and the thrid object is the database table in
 * which the object is stored.
 *
 * @param args[] The Object array of information needed to load the BaseAction.
 */
    public DeleteBaseActionReqMsg (Object args[])
        {
        super(args);
        }

/**
 * Used to set the action to preform for a WLGeneralServerConnection.  Gets the
 * filename, database, and table from the m_args[] variable.  Uses this information to
 * initialize a DeleteBaseActionReq.  Upon successfully using runUpdate, it returns
 * a DeleteBaseActionReqMsg.
 * <p>
 * @param wlgsc The WLGeneralServerConnection that received the message.
 * @return Returns a DeleteBaseActionReqMsg with the retrieved BaseAction, otherwise returns an ExptErrorMsg.
 * @see girard.sc.expt.obj.BaseAction
 * @see girard.sc.expt.sql.DeleteBaseActionReq
 * @see girard.sc.io.msg.TCPMessage#m_args
 */
    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Hashtable) || !(args[1] instanceof String) || !(args[2] instanceof String))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect data type for delete base action message.");
            err_args[1] = new String("DeleteBaseActionReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Hashtable fileInfo = (Hashtable)args[0];
        String db = (String)args[1];
        String dbTable = (String)args[2];

        DeleteBaseActionReq tmp = new DeleteBaseActionReq(fileInfo,db,dbTable,wlgsc,this);

        try
            {
            if (tmp.runUpdate())
                {
                Object[] out_args = new Object[1];
                out_args[0] = fileInfo;
                return new DeleteBaseActionReqMsg(out_args);
                }
            else
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Error deleteing network: DB exception");
                err_args[1] = new String("DeleteBaseActionReqMsg");
                return new ExptErrorMsg(err_args);
                }
            }
        catch (Exception e)
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Error deleting network: "+e);
            err_args[1] = new String("DeleteBaseActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
