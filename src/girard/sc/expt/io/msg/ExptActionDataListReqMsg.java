package girard.sc.expt.io.msg;

import girard.sc.expt.sql.ExptActionDataListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Loads the data list for the ExperimentActions that made up the Experiment.
 * <p>
 * <br> Started: 10-25-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class ExptActionDataListReqMsg extends ExptMessage 
    { 
    public ExptActionDataListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        if (!(m_args[0] instanceof Integer))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in ExptActionDataListReqMsg");
            err_args[1] = new String("ExptActionDataListReqMsg");
            return new ExptErrorMsg(err_args);
            }

        int exptOutID = ((Integer)m_args[0]).intValue();

        try
            {
  /* Need to load the actual settings for the action then load it allowing for special table
     entries to be loaded. */

            ExptActionDataListReq tmp = new ExptActionDataListReq(exptOutID,wlgsc,this);

            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from databse.");
                err_args[1] = new String("LoadDataFileReqMsg");
                return new ExptErrorMsg(err_args);
                }

            Vector netInfo = new Vector();

            while(rs.next())
                {
                Hashtable h = new Hashtable();
                h.put("Expt_Out_ID_INT",new Integer(exptOutID));
                h.put("Action_Index_INT",new Integer(rs.getInt("Action_Index_INT")));
                h.put("Action_Object_Index_INT",new Integer(rs.getInt("Action_Object_Index_INT")));
                h.put("Action_Name_VC",rs.getString("Action_Name_VC"));
                h.put("Date_Run_DATE",rs.getTimestamp("Date_Run_DATE"));

                netInfo.addElement(h);
                }

            Object[] out_args = new Object[1];
            out_args[0] = netInfo;
            return new ExptActionDataListReqMsg(out_args);
            }
        catch( Exception e )
            {
            wlgsc.addToLog(e.getMessage());
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadDataFileReqMsg");
            err_args[1] = new String("LoadDataFileReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
