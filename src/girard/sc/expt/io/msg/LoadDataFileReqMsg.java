package girard.sc.expt.io.msg;

import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.sql.LoadOtherObjectsReq;
import girard.sc.expt.sql.LoadOutActionSettingsReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLUserNameReq;

import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Loads the data for a specific ExperimentAction that was part of an Experiment.
 * <p>
 * <br> Started: 10-25-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1  
 */

public class LoadDataFileReqMsg extends ExptMessage 
    { 
    public LoadDataFileReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        if (!(m_args[0] instanceof Integer) || !(m_args[1] instanceof Integer))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadDataFileReqMsg");
            err_args[1] = new String("LoadDataFileReqMsg");
            return new ExptErrorMsg(err_args);
            }

        int exptOutID = ((Integer)m_args[0]).intValue();
        int actionIndex = ((Integer)m_args[1]).intValue();
        ExperimentAction ea = (ExperimentAction)m_args[2];

        try
            {
  /* Need to load the actual settings for the action then load it allowing for special table
     entries to be loaded. */
            BaseDataInfo bdi = new BaseDataInfo();
            bdi.setExptOutID(exptOutID);
            bdi.setActionIndex(actionIndex);

            LoadOutActionSettingsReq tmp = new LoadOutActionSettingsReq(bdi,wlgsc,this);

            ResultSet rs = tmp.runQuery();
	    
            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from database.");
                err_args[1] = new String("LoadDataFileReqMsg");
                return new ExptErrorMsg(err_args);
                }

            if (rs.next())
                {
                Hashtable data, settings;
                String userName;

                bdi.setActionObjectIndex(rs.getInt("Action_Object_Index_INT"));
                bdi.setDateRun(rs.getTimestamp("Date_Run_DATE"));
                bdi.setActionDB(rs.getString("Action_DB_VC"));
                bdi.setActionDesc(rs.getString("Action_Desc_VC"));
                bdi.setActionDetailName(rs.getString("Action_Name_VC"));
                bdi.setGeneralAccess(rs.getBoolean("General_Access_BIT"));
                bdi.setPracticeData(rs.getBoolean("Practice_Action_BIT"));

                ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Action_OBJ"));
                settings = (Hashtable)ois.readObject();

             // Before we construct the object we need to load any other objects needed.

                Hashtable objects = new Hashtable();
                Vector types = (Vector)settings.get("Types");
                if (types.size() > 0)
                    {
                    LoadOtherObjectsReq tmp2 = new LoadOtherObjectsReq(types,wlgsc,this);

                    ResultSet rs2 = tmp2.runQuery();

                    while (rs2.next())
                        {
                        String objName = (String)rs2.getString("Object_Name_VC");
                        ObjectInputStream ois2 = new ObjectInputStream(rs2.getBinaryStream("Object_OBJ"));
                        Object tmpObj = ois2.readObject();
                        objects.put(objName,tmpObj);
                        }
                    }
                settings.put("Objects",objects);

                ea.applySettings(settings);

                bdi.setAction(ea);

                bdi.setActionData(ea.retrieveData(wlgsc,this,bdi));

                WLUserNameReq tmp2 = new WLUserNameReq(bdi.getUserID(),wlgsc,this);
		System.err.println("setting the m_bdi here....");
                ResultSet rs2 = tmp2.runQuery();
                if (rs2 != null)
                    {
                    if (rs2.next())
                        {
                        String last = rs2.getString("Last_Name_VC");
                        String first = rs2.getString("First_Name_VC");
                        bdi.setUserName(new String(first+" "+last));

                        Object[] out_args = new Object[1];
                        out_args[0] = bdi;

                        return new LoadDataFileReqMsg(out_args);
                        }
                    }
                }
        
            }
        catch( Exception e )
            {
            wlgsc.addToLog(e.getMessage());
            Object[] err_args = new Object[2];
            err_args[0] = new String("Something bad happened in LoadDataFileReqMsg");
            err_args[1] = new String("LoadDataFileReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Object[] err_args = new Object[2];
        err_args[0] = new String("Something bad happened in LoadDataFileReqMsg");
        err_args[1] = new String("LoadDataFileReqMsg");
        return new ExptErrorMsg(err_args);
        }
    }
