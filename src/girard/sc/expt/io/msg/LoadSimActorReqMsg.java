package girard.sc.expt.io.msg;

import girard.sc.expt.obj.SimActor;
import girard.sc.expt.sql.LoadSimActorReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.util.Hashtable;

public class LoadSimActorReqMsg extends ExptMessage 
    { 
    public LoadSimActorReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof String) || !(args[1] instanceof SimActor))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadSimActorReqMsg");
            err_args[1] = new String("LoadSimActorReqMsg");
            return new ExptErrorMsg(null);
            }

        String fileName = (String)args[0];
        SimActor sa = (SimActor)args[1];
        Hashtable ag = (Hashtable)args[2];

        try 
            { 
            LoadSimActorReq tmp = new LoadSimActorReq(sa.getDB(),fileName,sa.getActorTypeID(),ag,wlgsc,this);

            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from databse.");
                err_args[1] = new String("LoadSimActorReqMsg");
                return new ExptErrorMsg(err_args);
                }

            if (rs.next())
                {
                sa.initializeActor(rs);
   
                Object[] out_args = new Object[1];
                out_args[0] = sa;

                return new LoadSimActorReqMsg(out_args);
                }
            else
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Something bad happened in LoadSimActorReqMsg");
                err_args[1] = new String("LoadSimActorReqMsg");
                return new ExptErrorMsg(err_args);
                }
            }
        catch( Exception e )
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadSimActorReqMsg");
            err_args[1] = new String("LoadSimActorReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
