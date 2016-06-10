package girard.sc.cc.io.msg;

/*  This message signifies the end of the round.

    Author: Dudley Girard
    Started: 7-10-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNetworkAction;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.cc.obj.CCPeriod;
import girard.sc.cc.obj.CCStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Vector;

public class CCEndRoundMsg extends ExptMessage 
    { 
    public CCEndRoundMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (cw instanceof CCNetworkActionClientWindow)
            {
            CCNetworkActionClientWindow nacw = (CCNetworkActionClientWindow)cw;
            /* Compute earnings totals here and update display labels */
            CCNode me = (CCNode)nacw.getNetwork().getExtraData("Me");
            CCNodeResource nr = (CCNodeResource)me.getExptData("CCNodeResource");
            
            Double pep = (Double)nacw.getNetwork().getExtraData("PntEarnedPeriod");
            Double pen = (Double)nacw.getNetwork().getExtraData("PntEarnedNetwork");
            double per = nr.getActiveBank() - pep.doubleValue();  // Points earned round.

            nacw.setBankLabel(nr.getActiveBank());
            nacw.getNetwork().setExtraData("PntEarnedRound",new Double(per));
            nacw.getNetwork().setExtraData("PntEarnedPeriod",new Double(per + pep.doubleValue()));
            nacw.getNetwork().setExtraData("PntEarnedNetwork",new Double(per + pen.doubleValue()));

            Object[] out_args = new Object[1];
            out_args[0] = nacw.getNetwork().getExtraData("PntEarnedNetwork");
            CCEndRoundMsg tmp = new CCEndRoundMsg(out_args);
            cw.getSML().sendMessage(tmp);
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - CCEndRoundMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC End Round Message");
    
        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
            {
            synchronized(ec)
                {
                if (index == ExptComptroller.EXPERIMENTER)
                    {
                    if (!ec.allRegistered())
                        {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Least one user not registered.");
                        err_args[1] = new String("CCEndRoundMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCEndRoundMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[2];
                    out_args[0] = new Integer(index);
                    out_args[1] = args[0];
                    ec.addServerMessage(new CCEndRoundMsg(out_args));
                    ec.sendToAllObservers(new CCEndRoundMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCEndRoundMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        Double points = (Double)this.getArgs()[1];
        
        if (ew.getExpApp().getExptRunning())
            ew.getExpApp().setReady(true,index.intValue());

     /* Update earnings for this user here. */
        CCNetwork ccn = (CCNetwork)ew.getExpApp().getActiveAction().getAction();
        double[] pen = (double[])ccn.getExtraData("PntEarnedNetwork");
        pen[index.intValue()] = points.doubleValue();

        boolean flag = true;
        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!ew.getExpApp().getReady(x))
                flag = false;
            }
        if (flag)
            {
            ew.getExpApp().initializeReady();
            CCPeriod ccp = ccn.getPeriod();
            
          /* update the data output here */
            CCNetworkActionExperimenterWindow naew = (CCNetworkActionExperimenterWindow)ew;

         /* Save gathered output data here */
            if (naew.saveOutputResults("ccDB",(Vector)ccn.getExtraData("Data")))
                {
                Vector v = (Vector)ccn.getExtraData("Data");
                v.removeAllElements();
                }
            else
                {
                /* Something bad happened. */
                }

            ccp.setCurrentRound(ccp.getCurrentRound() + 1);

            if (ccp.getCurrentRound() < ccp.getRounds())
                {
                ccn.setExtraData("CurrentState",new Double(0));
                CCStateAction ccsa = ((CCNetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
                ccsa.executeAction(ew);
                return;
                }
            else  /*  Stop this Action see if there is another one to start */
                {
        /* Possibly write some data to a temporary table for summing up earnings later */
                naew.savePayResults();
                    
                ew.getExpApp().startNextAction(ew);
                }
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CCNetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CCEndRoundMsg");
            return;
            }

        Integer index = (Integer)this.getArgs()[0];
        Double per = (Double)this.getArgs()[1];
        
        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

     /* Update earnings for this user here. */
        CCNetwork ccn = (CCNetwork)ow.getExpApp().getActiveAction();
        double[] pen = (double[])ccn.getExtraData("PntEarnedNetwork");
        pen[index.intValue()] = per.doubleValue() + pen[index.intValue()];
        }
    }