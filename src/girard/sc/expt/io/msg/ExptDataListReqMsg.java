package girard.sc.expt.io.msg;

import girard.sc.expt.sql.ExptDataListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Retreives all the Experiment data files that a user has access to.  Returns the
 * list of Experiment data files as a Vector with each data file's info being stored
 * in a Hashtable.
 * <p>
 * <br> Started: 10-24-2002
 * <p>
 * 
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class ExptDataListReqMsg extends ExptMessage 
    { 
    public ExptDataListReqMsg (Object args[])
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

            ExptDataListReq tmp = new ExptDataListReq(ag,wlgsc,this);

            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from databse - ExptDataListReq.");
                err_args[1] = new String("ExptDataListReqMsg");
                return new ExptErrorMsg(err_args);
                }

            while(rs.next()) 
                {
                netInfo.addElement(fillInExptData(rs));
                }

            Enumeration enm = ags.elements();
            while (enm.hasMoreElements())
                {
                Hashtable h = (Hashtable)enm.nextElement();
                ag = (String)h.get("App ID");

                if (ag != null)
                    {
                    ExptDataListReq tmp2 = new ExptDataListReq(ag,wlgsc,this);

                    ResultSet rs2 = tmp2.runQuery();

                    if (rs2 == null)
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Failed to get type information from databse - ExptDataListReq.");
                        err_args[1] = new String("ExptDataListReqMsg");
                        return new ExptErrorMsg(err_args);
                        }

                    while(rs2.next()) 
                        {
                        netInfo.addElement(fillInExptData(rs2));
                        }
                    }
                }

            Object[] out_args = new Object[1];
            out_args[0] = netInfo;

            return new ExptDataListReqMsg(out_args);
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

    private Hashtable fillInExptData(ResultSet rs)
        {
        Hashtable h = new Hashtable();
        h.put("Expt_Out_ID_INT",new Integer(-1));
        h.put("Expt_Name_VC",new String("None"));
        h.put("Expt_Desc_VC",new String("None"));
        h.put("ID_INT",new Integer(-1));
        h.put("Date_Run_DATE",new Timestamp(0)); 

        try
            {
            h.put("Expt_Out_ID_INT",new Integer(rs.getInt("Expt_Out_ID_INT")));
            h.put("Expt_Name_VC",rs.getString("Expt_Name_VC"));
            h.put("Expt_Desc_VC",rs.getString("Expt_Desc_VC"));
            h.put("ID_INT",new Integer(rs.getInt("ID_INT")));
                      
            if (rs.getString("App_ID") != null)
                {
                h.put("App ID",rs.getString("App_ID"));
                h.put("App Name",rs.getString("App_Name_VC"));
                }
            h.put("Date_Run_DATE",rs.getTimestamp("Date_Run_DATE"));
            InputStream bs = rs.getBinaryStream("Extra_Data_OBJ");
            if (bs != null)
                {
                ObjectInputStream ois = new ObjectInputStream(bs);

                h.put("Extra_Data_OBJ",ois.readObject());
                }
            else
                {
                Hashtable extraData = new Hashtable();
                extraData.put("EndWindow","No");
                h.put("Extra_Data_OBJ",extraData);
                }
            }
        catch( Exception e ) { }

        return h;
        }
    }
