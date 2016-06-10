package girard.sc.wl.io.msg;

import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.sql.WLUpdateAppTokenReq;

import java.util.Hashtable;

public class WLUpdateAppTokenReqMsg extends WLMessage 
    { 
    public WLUpdateAppTokenReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof String))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Wrong data object types");
            err_args[1] = new String("WLUpdateAppTokenReqMsg");
            return new WLErrorMsg(null);
            }

        Hashtable h = new Hashtable();
        h.put("AppToken",args[0]);
        h.put("UID",new Integer(-1));

        WLUpdateAppTokenReq tmp = new WLUpdateAppTokenReq(h,wlgsc,this);

        if (tmp.runUpdate())
            {
            Object[] out_args = new Object[1];
            out_args[0] = h;
            return new WLUpdateAppTokenReqMsg(out_args);
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Unable to process web request.");
            err_args[1] = new String("WLUpdateAppTokenReqMsg");
            return new WLErrorMsg(err_args);
            }
        }
    }