package girard.sc.expt.io.msg;

/* JoinExptReqMsg: Is the message used to process join requests made by
   possible subjects and observers to join an experiment.

Author: Dudley Girard
Started: ??-??-2000
Modified: 5-4-2001
*/

import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.io.obj.ObserverComptroller;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.obj.ExptUserData;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.sql.ExptUserInfoReq;

import java.sql.ResultSet;

public class JoinExptReqMsg extends ExptMessage 
    { 
    public JoinExptReqMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();
        
// System.err.println("ESR: Exnet Join Experiment Request Message");
// System.err.flush();

        if (!(args[0] instanceof Long)  && !(args[1] instanceof Integer) && !(args[2] instanceof String))
            {
            // Return an error msg.
            Object[] err_args = new Object[2];
            err_args[0] = new String("Incorrect Object Types1");
            err_args[1] = new String("JoinExptReqMsg");
            return new ExptErrorMsg(err_args);
            }

        Long cei = (Long)args[0];
        int index = ((Integer)args[1]).intValue();
        String pass = (String)args[2];

        ExptComptroller ec = esc.getActiveExpt(cei);

        synchronized(ec)
            {
            if (index != ExptComptroller.OBSERVER)
                {
                if (!ec.getRegistered(index)) // If no one else has taken this spot and...
                    {
                    if ((ec.getPassword(index).equals(pass)) || (ec.getPassword(index).length() == 0)) // if we have the right password or if none is needed...
                        {
                        ExptUserInfoReq tmp = new ExptUserInfoReq(esc,this);

                        try
                            {
                            ResultSet rs = tmp.runQuery();
                            if (rs != null)
                                {
                                if (rs.next())
                                    {
                                    ExptUserData eud = new ExptUserData();
                                    eud.setFirstName(rs.getString("First_Name_VC"));
                                    eud.setMi(rs.getString("Mi_VC"));
                                    eud.setLastName(rs.getString("Last_Name_VC"));

                                    ec.addSubject(index,eud);
                                    ec.setRegistered(true,index);
                                    esc.setExptIndex(ec);
                                    esc.setUserNum(index);
                                    esc.setData(new Integer(index));
                                    }
                                }
                            else
                                {
                                Object[] err_args = new Object[2];
                                err_args[0] = new String("Unable to retrieve user data.");
                                err_args[1] = new String("JoinExptReqMsg");
                                return new ExptErrorMsg(err_args);
                                }
                            }
                        catch( Exception e ) 
                            {
                            Object[] err_args = new Object[2];
                            err_args[0] = e.getMessage();
                            err_args[1] = new String("JoinExptReqMsg");
                            return new ExptErrorMsg(err_args);
                            }

                        ClientExptInfo cInfo = new ClientExptInfo(ec);
                        cInfo.setUserIndex(index);

                        Object[] out_args = new Object[1];
                        out_args[0] = cInfo;
                        return new JoinExptReqMsg(out_args);
                        } 
                    else  // Incorrect password.
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Join attempt failed.");
                        err_args[1] = new String("JoinExptReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    } 
                else   // Requested position already taken.
                    {
                    Object[] err_args = new Object[2];
                    err_args[0] = new String("Join attempt failed.");
                    err_args[1] = new String("JoinExptReqMsg");
                    return new ExptErrorMsg(err_args);
                    }
                }
            else if (ec.getAllowObservers()) // Want to connect as an observer. 
                {
                if ((ec.getObserverPass().equals(pass)) || (ec.getObserverPass().length() == 0))
                    {
                    ExptUserInfoReq tmp = new ExptUserInfoReq(esc,this);

                    ObserverExptInfo oei = null;

                    try
                        {
                        ResultSet rs = tmp.runQuery();

                        if (rs != null)
                            {
                            if (rs.next())
                                {
                                esc.setExptIndex(ec);

                                ExptUserData eud = new ExptUserData();
// Don't want them to be able to get the user's password.
                                eud.setFirstName(rs.getString("First_Name_VC"));
                                eud.setMi(rs.getString("Mi_VC"));
                                eud.setLastName(rs.getString("Last_Name_VC"));

                                int observerIndex = esc.getObserverID();

                                ObserverComptroller oc = new ObserverComptroller(eud,observerIndex);
                                ec.addObserver(oc);
                                
                                esc.setUserNum(index);
                                esc.setData(new Integer(observerIndex));
                                oei = new ObserverExptInfo(ec);
                                oei.setObserverID(observerIndex);
                                }
                            }
                        else
                            {
                            Object[] err_args = new Object[2];
                            err_args[0] = new String("Unable to retrieve user data.");
                            err_args[1] = new String("JoinExptReqMsg");
                            return new ExptErrorMsg(err_args);
                            }
                        }
                    catch( Exception e ) 
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = e.getMessage();
                        err_args[1] = new String("JoinExptReqMsg");
                        return new ExptErrorMsg(err_args);
                        }

                    Object[] out_args = new Object[1];
                    out_args[0] = oei;
                    return new JoinExptReqMsg(out_args);
                    }
                else
                    {
                    Object[] err_args = new Object[2];
                    err_args[0] = new String("Join attempt failed.");
                    err_args[1] = new String("JoinExptReqMsg");
                    return new ExptErrorMsg(err_args);
                    }
                }
            Object[] err_args = new Object[2];
            err_args[0] = new String("Join attempt failed.");
            err_args[1] = new String("JoinExptReqMsg");
            return new ExptErrorMsg(err_args);   
            }
        }
    }