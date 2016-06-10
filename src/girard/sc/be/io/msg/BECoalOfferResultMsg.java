package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BEStaticCoalOfferResWindow;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * This message informs the clients what their coalition wants to offer.
 * 
 * <p>
 * <br> Started: 07-20-2003
 * <p>
 * @author Dudley Girard
 */

public class BECoalOfferResultMsg extends ExptMessage 
    { 
    public BECoalOfferResultMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof BENetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - BECoalOfferResultMsg");
            return;
            }

        BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;

        Hashtable theOffers = (Hashtable)getArgs()[0];
        Hashtable coalFormed = (Hashtable)getArgs()[1];

        Enumeration enm = nacw.getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();

            BENodeOrSubNet nos = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");
            Integer offer = (Integer)theOffers.get(new Integer(tmpNode.getID()));
            Boolean formed = (Boolean)coalFormed.get(new Integer(tmpNode.getID()));
            nos.getCoalition().setCoalOffer(offer.intValue());
            nos.getCoalition().setFormed(formed.booleanValue());
            }


        BENode node = (BENode)nacw.getNetwork().getExtraData("Me");
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        if (nos.getCoalition().getCoalitionType().equals("Dynamic"))
            {
            
            }
        else if (nos.getCoalition().getCoalitionType().equals("Static"))
            {
            if ((nos.getCoalition().getFormed()) && (nos.getCoalition().getJoined()))
                {
                nacw.addSubWindow(new BEStaticCoalOfferResWindow(nacw));
                }
            }

        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Coal Offer Result Message");
    
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
                        err_args[1] = new String("BECoalOfferResultMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new BECoalOfferResultMsg(args));
                    ec.sendToAllObservers(new BECoalOfferResultMsg(args));
                    }
                else
                    {
                    // Do nothing.
                    }
                return null; 
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BECoalOfferResultMsg");
            return new ExptErrorMsg(err_args);
            }
        }

    public void getExperimenterResponse(ExperimenterWindow ew)
        {
        }

    public void getObserverResponse(ObserverWindow ow)
        {
        }
    }