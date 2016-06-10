package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CEPeriod;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

/**
 * Lets the subjects and observers know that the round has actually started.
 * The subjects will be able to start negotiating.
 * <p>
 * <br> Started: 02-10-2003
 * <p>
 * @author Dudley Girard
 */

public class CEStartRoundMsg extends ExptMessage 
    { 
    public CEStartRoundMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (cw instanceof CENetworkActionClientWindow)
            {
            CENetworkActionClientWindow nacw = (CENetworkActionClientWindow)cw;

            CENetwork cen = (CENetwork)cw.getExpApp().getActiveAction();
            cen.setExtraData("RoundRunning",new Boolean(true));
            cen.initializeNetwork();
            nacw.setMessageLabel("");
            nacw.updateMyCmdPanel((CENode)cen.getExtraData("Me"));
            nacw.updatePartnerCmdPanel(nacw.getArrow().getToNode());
            CEEdge edge = nacw.getArrow().getEdge();
            nacw.getArrow().setEdge(edge);
            nacw.repaint();
            }
        else
            {
            new ErrorDialog("Wrong ClientWindow. - CEStartRoundMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CE Start Round Message");
// System.err.flush();
    
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
                        err_args[1] = new String("CEStartRoundMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CEStartRoundMsg(args));
                    ec.sendToAllObservers(new CEStartRoundMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(index);
                    ec.addServerMessage(new CEStartRoundMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CEStartRoundMsg");
            return new ExptErrorMsg(err_args);
            }
        }

// Should not get called
    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CEStartRoundMsg");
            }

        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
                return;
        
        CENetworkActionObserverWindow naow = (CENetworkActionObserverWindow)ow;

        CENetwork cen = (CENetwork)ow.getExpApp().getActiveAction();
        cen.setExtraData("RoundRunning",new Boolean(true));
        CEPeriod cep = cen.getActivePeriod();
        cep.setCurrentRound(cep.getCurrentRound() + 1);
        cep.setCurrentTime(cep.getTime());
        naow.setPeriodLabel(cen.getCurrentPeriod());
        naow.setRoundLabel(cep.getCurrentRound());
        cen.initializeNetwork();
        naow.repaint();
        }
    }