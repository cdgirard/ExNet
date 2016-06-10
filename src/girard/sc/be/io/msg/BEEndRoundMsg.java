package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BEEndRoundWindow;
import girard.sc.be.awt.BENetworkActionClientWindow;
import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.awt.BENetworkActionObserverWindow;
import girard.sc.be.obj.BECoalNEOutputObject;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BEEdgeResource;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeExchange;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BENodeSanctions;
import girard.sc.be.obj.BEOfferOutputObject;
import girard.sc.be.obj.BEPeriod;
import girard.sc.be.obj.BEStateAction;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Vector;

public class BEEndRoundMsg extends ExptMessage
{
    public BEEndRoundMsg(Object args[])
    {
        super(args);
    }

    public void getClientResponse(ClientWindow cw)
    {
        if (cw instanceof BENetworkActionClientWindow)
        {
            final BENetworkActionClientWindow tmp = (BENetworkActionClientWindow) cw;
            tmp.setMessageLabel("");
            tmp.addSubWindow(new BEEndRoundWindow(tmp));
        }
        else
        {
            new ErrorDialog("Wrong Client Window. - BEEndRoundMsg");
        }
    }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
    {
        Object[] args = this.getArgs();

        // System.err.println("ESR: End Round Message");

        ExptComptroller ec = esc.getExptIndex();
        int index = esc.getUserNum();

        if (ec != null)
        {
            synchronized (ec)
            {
                if (index == ExptComptroller.EXPERIMENTER)
                {
                    if (!ec.allRegistered())
                    {
                        Object[] err_args = new Object[2];
                        err_args[0] = new String("Least one user not registered.");
                        err_args[1] = new String("BEEndRoundMsg");
                        return new ExptErrorMsg(err_args);
                    }
                    ec.sendToAllUsers(new BEEndRoundMsg(args));
                    return null;
                }
                else
                {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[3];
                    out_args[0] = new Integer(index);
                    out_args[1] = args[0];
                    out_args[2] = args[1];
                    ec.addServerMessage(new BEEndRoundMsg(out_args));
                    ec.sendToAllObservers(new BEEndRoundMsg(out_args));
                    return null;
                }
            }
        }
        else
        {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("BEEndRoundMsg");
            return new ExptErrorMsg(err_args);
        }
    }

    public void getExperimenterResponse(ExperimenterWindow ew)
    {
        Integer index = (Integer) this.getArgs()[0];
        Double per = (Double) this.getArgs()[1];
        Integer nodeID = (Integer) this.getArgs()[2];

        if (ew.getExpApp().getExptRunning())
            ew.getExpApp().setReady(true, index.intValue());

        /* Update earnings for this user here. */
        BENetwork ben = (BENetwork) ew.getExpApp().getActiveAction().getAction();
        double[] pen = (double[]) ben.getExtraData("PntEarnedNetwork");
        pen[index.intValue()] = per.doubleValue() + pen[index.intValue()];

        BENode node = (BENode) ben.getNode(nodeID.intValue());
        //	BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        // got this from Dudley
        BENodeExchange bene = (BENodeExchange) node.getExptData("BENodeExchange");
        int cr = -1;
        int cp = -1;
        Vector outData = new Vector();
        if (bene instanceof BENodeOrSubNet)
        {

            BENodeOrSubNet nos = (BENodeOrSubNet) bene;
            //		<rest of code here>
            /* update data output here */

            cr = ben.getActivePeriod().getCurrentRound() + 1;
            cp = ben.getCurrentPeriod() + 1;
            BECoalNEOutputObject data = new BECoalNEOutputObject(ew.getExpApp().getExptOutputID(), ew.getExpApp().getActionIndex(), cp, cr, node.getID(), nos.getCoalition().getCoalition(),
                    (int) per.doubleValue());
            outData = (Vector) ben.getExtraData("Data");
            outData.addElement(data);

        }

        boolean flag = true;
        for (int x = 0; x < ew.getExpApp().getNumUsers(); x++)
        {
            if (!ew.getExpApp().getReady(x))
                flag = false;
        }
        if (flag)
        {
            ew.getExpApp().initializeReady();
            BEPeriod bep = ben.getActivePeriod();

            /* update the data output here */
            BENetworkActionExperimenterWindow naew = (BENetworkActionExperimenterWindow) ew;

            Enumeration enm = naew.getNetwork().getEdgeList().elements();
            while (enm.hasMoreElements())
            {
                BEEdge edge = (BEEdge) enm.nextElement();
                BEEdgeResource beer = (BEEdgeResource) edge.getExptData("BEEdgeResource");
                if (beer.getExchange() != null)
                {
                    int exptID = ew.getExpApp().getExptOutputID();
                    int ai = naew.getExpApp().getActionIndex();
                    int n1 = edge.getNode1();
                    int n2 = edge.getNode2();
                    double n1Get = beer.getExchange().getNode1().getResource();
                    double n2Get = beer.getExchange().getNode2().getResource();
                    int tt = beer.getExchange().getTTime();
                    long rt = beer.getExchange().getRTime();

                    BEOfferOutputObject data2 = new BEOfferOutputObject(exptID, ai, cp, cr, n1, n2, n1Get, n2Get, "Final", tt, rt);
                    outData.addElement(data2);
                }
            }

            /* Save gathered output data here */
            if (naew.saveOutputResults("beDB", (Vector) ben.getExtraData("Data")))
            {
                Vector v = (Vector) ben.getExtraData("Data");
                v.removeAllElements();
            }
            else
            {
                /* Something bad happened. */
            }

            bep.setCurrentRound(bep.getCurrentRound() + 1);

            if (bep.getCurrentRound() < bep.getRounds())
            {
                ben.setExtraData("CurrentState", new Double(0));
                BEStateAction besa = ((BENetworkAction) ew.getExpApp().getActiveAction()).getNextStateAction();
                besa.executeAction(ew);
                return;
            }
            else
            {
                /* Either Start a New period or end the experiment */
                ben.setCurrentPeriod(ben.getCurrentPeriod() + 1);

                if (ben.getCurrentPeriod() < ben.getNumPeriods())
                {
                    BEStartNextPeriodMsg tmp = new BEStartNextPeriodMsg(null);
                    ew.getSML().sendMessage(tmp);
                    return;
                }
                else
                {

                    /* Possibly write some data to a temporary table for summing up earnings later */
                    naew.savePayResults();

                    ew.getExpApp().startNextAction(ew);
                }
            }
        }
    }

    public void getObserverResponse(ObserverWindow ow)
    {
        if (!ow.getExpApp().getJoined())
            return;

        if (!(ow instanceof BENetworkActionObserverWindow))
        {
            new ErrorDialog("Wrong Observer Window. - BEEndRoundMsg");
            return;
        }

        Integer index = (Integer) this.getArgs()[0];
        Double per = (Double) this.getArgs()[1];

        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        /* Update earnings for this user here. */
        BENetwork ben = (BENetwork) ow.getExpApp().getActiveAction();
        double[] pen = (double[]) ben.getExtraData("PntEarnedNetwork");
        pen[index.intValue()] = per.doubleValue() + pen[index.intValue()];
    }
}
