package girard.sc.expt.io.msg;

/* This is used to archive and then delete a specific data action file.

   Author: Dudley Girard
   Started: 9-3-2001
*/

import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.sql.ExptDeleteDataFileReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

public class ExptDeleteDataFileReqMsg extends ExptMessage 
    { 
    public ExptDeleteDataFileReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
        ExperimentAction ea = (ExperimentAction)m_args[0];
        BaseDataInfo bdi = (BaseDataInfo)m_args[1];

        try 
            {
            ExptDeleteDataFileReq tmp = new ExptDeleteDataFileReq(bdi,wlgsc,this);

            tmp.runUpdate();

            return new ExptDeleteDataFileReqMsg(null);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Error with action database entries");
            err_args[1] = new String("ExptDeleteDataFileReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
