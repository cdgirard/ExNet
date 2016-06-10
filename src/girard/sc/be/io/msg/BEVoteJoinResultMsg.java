package girard.sc.be.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.be.awt.BEJoinStaticCoalResWindow;
import girard.sc.be.awt.BENetworkActionClientWindow;
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
 * This message informs the clients who joined and who didn't.  And Therefore
 * which coalitions actually formed.
 * 
 * <p>
 * <br> Started: 07-09-2003
 * <p>
 * @author Dudley Girard
 */

public class BEVoteJoinResultMsg extends ExptMessage 
    { 
    public BEVoteJoinResultMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if ((!cw.getExpApp().getExptRunning()) || (cw.getExpApp().getExptStopping()))
                return;

        if (!(cw instanceof BENetworkActionClientWindow))
            {
            new ErrorDialog("Wrong Client Window. - BEVoteJoinResultMsg");
            return;
            }

        BENetworkActionClientWindow nacw = (BENetworkActionClientWindow)cw;

        Hashtable theVotes = (Hashtable)getArgs()[0];

        Enumeration enm = nacw.getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();

            BENodeOrSubNet nos = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

            if (nos.getCoalition().getCoalitionType().equals("Static"))
                {
                Boolean vote = (Boolean)theVotes.get(new Integer(tmpNode.getID()));
                //-kar-
                System.out.println("BEVoteJoinResultMsg.getClientResponse() Node "+tmpNode.getID()+" voted: "+ vote);	;
                //-kar-
                nos.getCoalition().setJoined(vote.booleanValue());
                }
            else if (nos.getCoalition().getCoalitionType().equals("Dynamic"))
                {
                }
            }

        BENode node = (BENode)nacw.getNetwork().getExtraData("Me");
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        
        if (nos.getCoalition().getCoalitionType().equals("Dynamic"))
            {
            
            }
        else if (nos.getCoalition().getCoalitionType().equals("Static"))
            {
            int votes = nos.getCoalition().getNumJoinVotes(nacw.getNetwork());
            int members = nos.getCoalition().getNumCoalMembers(nacw.getNetwork());
            nos.getCoalition().setFormed(votes,members);

            if (nos.getCoalition().getFormed())
                {
                if (nos.getCoalition().getJoined())
                    {
                    //-kar-
                    System.out.println("Opening BEJoinStaticCoalResWindow(nacw)");
                    //-kar-
                    nacw.addSubWindow(new BEJoinStaticCoalResWindow(nacw));
                    }
                else
                    {
                    Object[] out_args = new Object[3];
                    out_args[0] = new Integer(node.getID());
                    out_args[1] = new Boolean(nos.getCoalition().getFormed());
                    out_args[2] = new Integer(0);
                    BEJoinCoalAckMsg tmp = new BEJoinCoalAckMsg(out_args);
                    nacw.getSML().sendMessage(tmp);
                    nacw.setMessageLabel("Please wait while others are deciding.");
                    }
                }
            else 
                {
                nacw.addSubWindow(new BEJoinStaticCoalResWindow(nacw));
                }
            }
        else
            {
            Object[] out_args = new Object[3];
            out_args[0] = new Integer(node.getID());
            out_args[1] = new Boolean(false);
            out_args[2] = new Integer(0);
            BEJoinCoalAckMsg tmp = new BEJoinCoalAckMsg(out_args);
            nacw.getSML().sendMessage(tmp);
            nacw.setMessageLabel("Please wait while others are deciding.");
            }
        nacw.repaint();
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: BE Vote Join Result Message");
    
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
                        err_args[1] = new String("BEVoteJoinResultMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    //-kar-    
                    Hashtable h = (Hashtable)args[0];
                    Enumeration enm = h.keys();
                    Integer ii = new Integer(-1);
                    while (enm.hasMoreElements()){
                    	ii = (Integer)enm.nextElement();
                    System.out.println("Sending to all Users: Node "+ii+" voted "+(Boolean)h.get(ii))	;
                    	}
                    //-kar-
                    ec.sendToAllUsers(new BEVoteJoinResultMsg(args));
                    ec.sendToAllObservers(new BEVoteJoinResultMsg(args));
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
            err_args[1] = new String("BEVoteJoinResultMsg");
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