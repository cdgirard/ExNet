package girard.sc.expt.io.msg;

import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLUserLoginReq;

/**
 * Sent to the WebLab server to login an experimenter into the 
 * system.
 * 
 * TODO: Possible merge the ExptSubjectLoginReqMsg and this into one generic message.
 * 
 * @author cdgira
 *
 */
public class ExptExperimenterLoginReqMsg extends ExptMessage
{
    public ExptExperimenterLoginReqMsg(Object args[])
    {
	super(args);
    }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
    {
	String UserName;
	String Password;
	Object[] args = this.getArgs();

	if (!(args[0] instanceof String) || !(args[1] instanceof String))
	{
	    // Return an error msg.
	    Object[] err_args = new Object[2];
	    err_args[0] = new String("Incorrect input types.");
	    err_args[1] = new String("ExptExperimenterLoginReqMsg");
	    return new ExptErrorMsg(err_args);
	}

	Object[] loginArgs = new Object[3];
	loginArgs[0] = (String) args[0]; // User Login
	loginArgs[1] = (String) args[1]; // User Password

	WLUserLoginReq tmp = new WLUserLoginReq(loginArgs, wlgsc, this);

	if (tmp.runUpdate())
	{
	    Object[] out_args = new Object[1];
	    out_args[0] = loginArgs[2];
	    return new ExptExperimenterLoginReqMsg(out_args);
	} else
	{
	    Object[] err_args = new Object[2];
	    err_args[0] = new String("Unable to login user.");
	    err_args[1] = new String("ExptExperimenterLoginReqMsg");
	    return new ExptErrorMsg(err_args);
	}
    }
}