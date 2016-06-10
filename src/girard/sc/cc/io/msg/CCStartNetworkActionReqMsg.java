package girard.sc.cc.io.msg;

/* The start message for the CCNetwork Action.

Author: Dudley Girard
Started: 05-29-2001
*/

import girard.sc.awt.ErrorDialog;
import girard.sc.cc.awt.CCNetworkActionClientWindow;
import girard.sc.cc.awt.CCNetworkActionObserverWindow;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNetworkAction;
import girard.sc.cc.obj.CCStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.obj.ObserverExptInfo;
import girard.sc.expt.web.ExptOverlord;

import java.util.Vector;

public class CCStartNetworkActionReqMsg extends ExptMessage 
    { 
    public CCStartNetworkActionReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof CCNetwork))
            {
            new ErrorDialog("Wrong argument type. - CCStartNetworkActionReqMsg");
            return;
            }

        ExptOverlord eo = cw.getEOApp();
        ClientExptInfo cei = cw.getExpApp();
        ExptMessageListener ml = cw.getSML();
        CCNetwork ccn = (CCNetwork)args[0];

        ccn.getPeriod().setCurrentRound(0);
        ccn.getPeriod().setCurrentTime(ccn.getPeriod().getTime());
        ccn.setExtraData("Index",new Integer(cei.getUserIndex()));
        ccn.setExtraData("RoundRunning",new Boolean(false));
        ccn.setExtraData("PntEarnedRound",new Double(0));
        ccn.setExtraData("PntEarnedPeriod",new Double(0));
        ccn.setExtraData("PntEarnedNetwork",new Double(0));
        cei.setActiveAction(ccn);  // In this case a CCNetwork

        cw.setWatcher(false);
        
        new CCNetworkActionClientWindow(eo,cei,ml);

        CCStartNetworkActionReqMsg tmp = new CCStartNetworkActionReqMsg(null);
        cw.getSML().sendMessage(tmp);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CC Start Network Action Request Message");
    
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
                        err_args[1] = new String("CCStartNetworkActionReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CCStartNetworkActionReqMsg(args));
                    ec.sendToAllObservers(new CCStartNetworkActionReqMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CCStartNetworkActionReqMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CCStartNetworkActionReqMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        Integer index = (Integer)this.getArgs()[0];
        
        if (ew.getExpApp().getExptRunning())
            ew.getExpApp().setReady(true,index.intValue());

        boolean flag = true;
        for (int x=0;x<ew.getExpApp().getNumUsers();x++)
            {
            if (!ew.getExpApp().getReady(x))
                flag = false;
            }
        if (flag)
            {
            ew.getExpApp().initializeReady();
            CCNetwork ccn = (CCNetwork)ew.getExpApp().getActiveAction().getAction();
            
            ccn.setExtraData("CurrentState",new Double(0));  // What current place in the action is the experiment.
            ccn.setExtraData("RoundRunning",new Boolean(false));  // Is the current round active flag.

            boolean[] tick = new boolean[ew.getExpApp().getNumUsers()];
            for (int x=0;x<tick.length;x++)
                {
                tick[x] = false;
                }
            ccn.setExtraData("TimeReady",tick); // The flags for when to increment the tick time.

            ccn.setExtraData("Data",new Vector());  // Is where all the output is kept until being sent to the database.
            
            double[] pen = new double[ew.getExpApp().getNumUsers()];
            for (int x=0;x<pen.length;x++)
                {
                pen[x] = 0;
                }
            ccn.setExtraData("PntEarnedNetwork",pen);  // Keeps track of the total number of points earned by each user.

            ccn.getPeriod().setCurrentRound(0);

            CCStateAction ccsa = ((CCNetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            ccsa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        Object[] args = this.getArgs();

        if (!(args[0] instanceof CCNetwork))
            {
            new ErrorDialog("Wrong argument type.");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();
        CCNetwork ccn = (CCNetwork)args[0];

        ccn.getPeriod().setCurrentRound(0);
        ccn.getPeriod().setCurrentTime(ccn.getPeriod().getTime());
        ccn.setExtraData("Index",new Integer(oei.getObserverID()));
        ccn.setExtraData("RoundRunning",new Boolean(false));

        double[] pen = new double[ccn.getNumNodes()];
        for (int x=0;x<pen.length;x++)
            {
            pen[x] = 0;
            }
        ccn.setExtraData("PntEarnedNetwork",pen);

        oei.setActiveAction(ccn);  // In this case a CCNetwork

        ow.setWatcher(false);
        
        new CCNetworkActionObserverWindow(eo,oei,ml);
        }
    }