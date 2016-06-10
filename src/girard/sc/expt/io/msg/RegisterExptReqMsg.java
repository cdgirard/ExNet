package girard.sc.expt.io.msg;

import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExptUserData;
import girard.sc.expt.sql.ExptUserInfoReq;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterExptReqMsg extends ExptMessage 
    { 
    public RegisterExptReqMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();
        
// System.err.println("ESR: Exnet Register Experiment Request Message");
// System.err.flush();

        if (!(args[0] instanceof Experiment))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("RegisterExptReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Experiment ee = (Experiment)args[0];

        ExptUserInfoReq tmp = new ExptUserInfoReq(esc,this);
        ResultSet rs = tmp.runQuery();
        ExptUserData eud = new ExptUserData();

        try
            {
            if (rs != null)
                {
                if (rs.next())
                    {
			// Don't want them to be able to get the user's password.
			eud.setFirstName(rs.getString("First_Name_VC"));
                    eud.setMi(rs.getString("Mi_VC"));
                    eud.setLastName(rs.getString("Last_Name_VC"));
                    }
/*                else
                    {
                    // Return an error msg.
                    Object[] err_args = new Object[2];
                    err_args[0] = new String("This user doesn't exist:1");
                    err_args[1] = new String("RegisterExptReqMsg");
                    return new ExptErrorMsg(err_args);
                    }
*/              }
            else
                {
                // Return an error msg.
                Object[] err_args = new Object[2];
                err_args[0] = new String("This user doesn't exist:2");
                err_args[1] = new String("RegisterExptReqMsg");
                return new ExptErrorMsg(err_args);
                }
            }
        catch(SQLException sqle) 
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String(""+sqle);
            err_args[1] = new String("RegisterExptReqMsg");
            return new ExptErrorMsg(err_args); 
            }

        ExptComptroller ec = new ExptComptroller(esc.getExptUID(), ee.getExptName(), eud.getFirstName()+" "+eud.getLastName(), ee.getNumUsers());

        ee.initializeRegistered();
        ec.setPassword(ee.getPassword());
        ec.setRegistered(ee.getRegistered());
        ec.setHumanUser(ee.getHumanUser());
        ec.setObserverPass(ee.getObserverPass());
        ec.setAllowObservers(ee.getAllowObservers());

        esc.addActiveExpt(ec);
        esc.setExptIndex(ec);
        esc.setUserNum(-1);
        Object[] out_args = new Object[2];
        out_args[0] = ee;
        out_args[1] = ec.getExptUID();

        return new RegisterExptReqMsg(out_args);
        }
    }
