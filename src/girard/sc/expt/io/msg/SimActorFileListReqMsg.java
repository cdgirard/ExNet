package girard.sc.expt.io.msg;

import girard.sc.expt.sql.SimActorFileListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class SimActorFileListReqMsg extends ExptMessage 
    { 
    public SimActorFileListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

    // System.err.println("Simulant Actor File List Req Msg");

        if (!(args[0] instanceof Integer) || !(args[1] instanceof String))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("SimActorListReqMsg");
            return new ExptErrorMsg(err_args);
            }

        int simType = ((Integer)args[0]).intValue();
        String db = (String)args[1];
        Vector ags = (Vector)args[2];

        try
            {
            String ag = null;
            Vector simInfo = new Vector();

            SimActorFileListReq tmp = new SimActorFileListReq(db,simType,ag,wlgsc,this);

            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from databse - SimActorFileListReq.");
                err_args[1] = new String("SimActorFileListReqMsg");
                return new ExptErrorMsg(err_args);
                }

            while(rs.next()) 
                {
                Hashtable h = new Hashtable();
                h.put("Sim Name",rs.getString("Sim_Name_VC"));
                h.put("Sim Desc",rs.getString("Sim_Desc_VC"));

                simInfo.addElement(h);
                }

            Enumeration enm = ags.elements();
            while (enm.hasMoreElements())
                {
                Hashtable h = (Hashtable)enm.nextElement();
                ag = (String)h.get("App ID");

                if (ag != null)
                    {
                    SimActorFileListReq tmp2 = new SimActorFileListReq(db,simType,ag,wlgsc,this);

                    ResultSet rs2 = tmp2.runQuery();

                    if (rs2 == null)
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Failed to get type information from databse - SimActorFileListReq.");
                        err_args[1] = new String("SimActorFileListReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
            
                    while(rs2.next()) 
                        {
                        Hashtable h2 = new Hashtable();
                        h2.put("Sim Name",rs2.getString("Sim_Name_VC"));
                        h2.put("Sim Desc",rs2.getString("Sim_Desc_VC"));
                        h2.put("App ID",ag);

                        simInfo.addElement(h2);
                        }
                    }
                }

            Object[] out_args = new Object[1];
            out_args[0] = simInfo;

            return new SimActorFileListReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in SimActorFileListReqMsg");
            err_args[1] = new String("SimActorFileListReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
