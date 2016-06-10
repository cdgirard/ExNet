package girard.sc.expt.io.msg;

/* Used to load a previously saved experiment that was built by the
   user.

Author: Dudley Girard
Started: 3-10-2001
Last Modified: 4-28-2001
*/

import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.sql.ExptActionTypesListReq;
import girard.sc.expt.sql.LoadExptActionsReq;
import girard.sc.expt.sql.LoadExptReq;
import girard.sc.expt.sql.LoadOtherObjectsReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Vector;

public class LoadExptReqMsg extends ExptMessage 
    { 
    public LoadExptReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof Experiment))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect data type for load experiment message.");
            err_args[1] = new String("LoadExptReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Experiment ee = (Experiment)args[0];

        LoadExptReq tmp = new LoadExptReq(ee.getExptID(),wlgsc,this);

        try
            {
            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Failed to get type information from databse - LoadExptReq.");
                err_args[1] = new String("LoadExptReqMsg");
                return new ExptErrorMsg(err_args);
                }

            if (rs.next())
                {
                ee = new Experiment(rs);

        // First we load in the different base actions.
                ExptActionTypesListReq tmp2 = new ExptActionTypesListReq(wlgsc,this);

                ResultSet rs2 = tmp2.runQuery();

                if (rs2 == null)
                    {
                    Object[] err_args = new Object[2];
                    err_args[0] = new String("Failed to get type information from databse - ExptActionTypesListReq.");
                    err_args[1] = new String("LoadExptReqMsg");
                    return new ExptErrorMsg(err_args);
                    }

                Hashtable actions = new Hashtable();

                while(rs2.next()) 
                    {
                    int id = rs2.getInt("Action_Type_ID_INT");
                    ObjectInputStream ois = new ObjectInputStream(rs2.getBinaryStream("Action_OBJ"));
                    try
                        {
                        ExperimentAction ea = (ExperimentAction)ois.readObject();
                        ea.setActionType(id);
                        actions.put(new Integer(id),ea);
                        }
                    catch (Exception e)
                        {
                        wlgsc.addToLog(e.getMessage());
                        }
                    }

        // Load the list of actions attached to this experiment.
                LoadExptActionsReq tmp3 = new LoadExptActionsReq(ee.getExptID(),wlgsc,this);

                ResultSet rs3 = tmp3.runQuery();

                if (rs3 == null)
                    {
                    Object[] err_args = new Object[2];
                    err_args[0] = new String("Failed to get type information from databse - LoadExptActionsReq.");
                    err_args[1] = new String("LoadExptReqMsg");
                    return new ExptErrorMsg(err_args);
                    }

                while (rs3.next())
                    {
                    ObjectInputStream ois = new ObjectInputStream(rs3.getBinaryStream("Action_OBJ"));
                    Hashtable data = (Hashtable)ois.readObject();
              // Before we construct the object we need to load any other objects needed.
                    Hashtable objects = new Hashtable();

                    Vector types = (Vector)data.get("Types");
                    if (types.size() > 0)
                        {
                        LoadOtherObjectsReq tmp4 = new LoadOtherObjectsReq(types,wlgsc,this);

                        ResultSet rs4 = tmp4.runQuery();

                        if (rs4 == null)
                            {
                            Object[] err_args = new Object[2];
                            err_args[0] = new String("Failed to get type information from databse - LoadOtherObjectsReq.");
                            err_args[1] = new String("LoadExptReqMsg");
                            return new ExptErrorMsg(err_args);
                            }

                        while (rs4.next())
                            {
                            String objName = (String)rs4.getString("Object_Name_VC");
                            ObjectInputStream ois2 = new ObjectInputStream(rs4.getBinaryStream("Object_OBJ"));
                            Object tmpObj = ois2.readObject();
                            objects.put(objName,tmpObj);
                            }
                        }
                    data.put("Objects",objects);

                    Integer type = (Integer)data.get("Type");
                    ExperimentAction ea = (ExperimentAction)((ExperimentAction)actions.get(type)).clone();
                    ea.applySettings(data);

                    int index = rs3.getInt("Action_Index_INT");

                    ee.addAction(ea,index);
                    }
                Object[] out_args = new Object[1];
                out_args[0] = ee;
                return new LoadExptReqMsg(out_args);
                }
            else
                {
                Object[] err_args = new Object[2];
                err_args[0] = new String("Experiment does not exist");
                err_args[1] = new String("LoadExptReqMsg");
                return new ExptErrorMsg(err_args);
                }
            }
        catch (Exception e)
            {
            wlgsc.addToLog(e.getMessage());
            Object[] err_args = new Object[2];
            err_args[0] = new String("Error retreving experiment "+e);
            err_args[1] = new String("LoadExptReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
