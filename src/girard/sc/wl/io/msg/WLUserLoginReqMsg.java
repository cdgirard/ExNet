package girard.sc.wl.io.msg;

import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.sql.WLUserLoginReq;

public class WLUserLoginReqMsg extends WLMessage 
    { 
    public WLUserLoginReqMsg (Object args[])
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
            Object[] err_args = new Object[1];
            err_args[0] = new String("Incorrect input types.");
            return new WLErrorMsg(err_args);
            }

        Object[] loginArgs = new Object[3];
        loginArgs[0] = (String)args[0]; // Name
        loginArgs[1] = (String)args[1]; // Password

        WLUserLoginReq tmp = new WLUserLoginReq(loginArgs,wlgsc,this);

        if (tmp.runUpdate())
            {
            return new WLUserLoginReqMsg(null);
            }
        else
            {
            Object[] err_args = new Object[1];
            err_args[0] = new String("Unable to login user.");
            return new WLErrorMsg(err_args);
            }
        }
    }