package girard.sc.ques.io.msg;


import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.ques.obj.BaseQuestion;
import girard.sc.ques.sql.BaseQuestionTypesListReq;
import girard.sc.wl.io.WLGeneralServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.util.Vector;

/**
 * Used to get the list of the base question types that can
 * be used in a QuestionaireAction.
 * <p>
 * Started: 11-19-2001
 * <p>
 * @author Dudley Girard 
 */


public class BaseQuestionTypesListReqMsg extends ExptMessage 
    { 
    public BaseQuestionTypesListReqMsg (Object args[])
        {
        super(args);
        }

    public WLMessage getGeneralServerResponse(WLGeneralServerConnection wlgsc)
        {
// System.err.println("Exnet Base Tutorial Page Types List Req Msg");
// System.err.flush();

        BaseQuestionTypesListReq tmp = new BaseQuestionTypesListReq(wlgsc,this);
 
        ResultSet rs = tmp.runQuery();

        if (rs == null)
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("Failed to connect to the databse.");
            err_args[1] = new String("BaseQuestionTypesListReqMsg");
            return new ExptErrorMsg(err_args);
            }

        try
            {
            Vector bqInfo = new Vector();
            Vector bqDesc = new Vector();

            while(rs.next()) 
                {
                String desc = rs.getString("Desc_VC");

                ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Base_Question_OBJ"));
                BaseQuestion bq = (BaseQuestion)ois.readObject();

                bqInfo.addElement(bq);
                bqDesc.addElement(desc);
                }

            Object[] out_args = new Object[2];
            out_args[0] = bqInfo;
            out_args[1] = bqDesc;

            return new BaseQuestionTypesListReqMsg(out_args);
            }
        catch( Exception e ) 
            {
            wlgsc.addToLog(e.getMessage());

            Object[] err_args = new Object[2];
            err_args[0] = new String("Error trying to read ResultSet.");
            err_args[1] = new String("BaseQuestionTypesListReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }
    }
