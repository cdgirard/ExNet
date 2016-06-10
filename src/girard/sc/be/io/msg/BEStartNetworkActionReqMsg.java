package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BEExptStartWindow;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENetworkActionObserverWindow;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BEStateAction;
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

/**
 * The start message for the BENetwork Action.
 * <p>
 * <br> Started: 2-12-2001
 * <br> Modified: 4-26-2001
 * <br> Modified: 10-10-2002
 * <p>
 * @author Dudley Girard
 */


public class BEStartNetworkActionReqMsg extends ExptMessage 
    { 
    public BEStartNetworkActionReqMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        Object[] args = this.getArgs();

        if (!(args[0] instanceof BENetwork))
            {
            new ErrorDialog("Wrong argument type. - BEStartNetworkActionReqMsg");
            return;
            }

        ExptOverlord eo = cw.getEOApp();
        ClientExptInfo cei = cw.getExpApp();
        ExptMessageListener ml = cw.getSML();
        BENetwork ben = (BENetwork)args[0];

        ben.setCurrentPeriod(0);
        ben.getActivePeriod().setCurrentRound(0);
        ben.getActivePeriod().setCurrentTime(ben.getActivePeriod().getTime());
        ben.setExtraData("Index",new Integer(cei.getUserIndex()));
        ben.setExtraData("RoundRunning",new Boolean(false));
        ben.setExtraData("PntEarnedRound",new Double(0));
        ben.setExtraData("PntEarnedPeriod",new Double(0));
        ben.setExtraData("PntEarnedNetwork",new Double(0));
        ben.setExtraData("CurrentState",new Double(0));
        cei.setActiveAction(ben);  // In this case a BENetwork

        cw.setWatcher(false);
        
        BENetworkActionClientWindow nacw = new BENetworkActionClientWindow(eo,cei,ml);

        nacw.addSubWindow(new BEExptStartWindow(nacw));
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

 // System.err.println("ESR: Start Network Action Request Message");
    
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
                        err_args[1] = new String("BEStartNetworkActionReqMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BEStartNetworkActionReqMsg(args));
                    ec.sendToAllObservers(new BEStartNetworkActionReqMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new BEStartNetworkActionReqMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEStartNetworkActionReqMsg");
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
            BENetwork ben = (BENetwork)ew.getExpApp().getActiveAction().getAction();
            
            ben.setExtraData("CurrentState",new Double(0));  // What current place in the action is the experiment.
            ben.setExtraData("RoundRunning",new Boolean(false));  // Is the current round active flag.

            boolean[] tick = new boolean[ew.getExpApp().getNumUsers()];
            for (int x=0;x<tick.length;x++)
                {
                tick[x] = false;
                }
            ben.setExtraData("TimeReady",tick); // The flags for when to increment the tick time.
            
            ben.setCurrentPeriod(0);
            ben.getActivePeriod().setCurrentRound(0);

            BEStateAction besa = ((BENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
            besa.executeAction(ew);
            }
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        Object[] args = this.getArgs();

        if (!(args[0] instanceof BENetwork))
            {
            new ErrorDialog("Wrong argument type.");
            return;
            }

        ExptOverlord eo = ow.getEOApp();
        ObserverExptInfo oei = ow.getExpApp();
        ExptMessageListener ml = ow.getSML();
        BENetwork ben = (BENetwork)args[0];

        ben.setCurrentPeriod(0);
        ben.getActivePeriod().setCurrentRound(0);
        ben.getActivePeriod().setCurrentTime(ben.getActivePeriod().getTime());
        ben.setExtraData("Index",new Integer(oei.getObserverID()));
        ben.setExtraData("RoundRunning",new Boolean(false));

        double[] pen = new double[ben.getNumNodes()];
        for (int x=0;x<pen.length;x++)
            {
            pen[x] = 0;
            }
        ben.setExtraData("PntEarnedNetwork",pen);

        oei.setActiveAction(ben);  // In this case a BENetwork

        ow.setWatcher(false);
        
        new BENetworkActionObserverWindow(eo,oei,ml);
        }
    }