package girard.sc.ce.io.msg;

import girard.sc.awt.ErrorDialog;
import girard.sc.ce.awt.CENetworkActionClientWindow;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.awt.CENetworkActionObserverWindow;
import girard.sc.ce.obj.CEEndRoundResOutputObject;
import girard.sc.ce.obj.CEExternalityObject;
import girard.sc.ce.obj.CEExternalityOutputObject;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENetworkAction;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEPeriod;
import girard.sc.ce.obj.CEResource;
import girard.sc.ce.obj.CEStateAction;
import girard.sc.ce.obj.iExternalityInfo;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ObserverWindow;
import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.obj.ExptComptroller;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Lets the client and observers know that the round is over and to report
 * earnings for the round to the experimenter.  The experimenter then either
 * starts a new round, a new period, or the next experiment action.
 * <p>
 * <br> Started: 02-20-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEndRoundMsg extends ExptMessage 
    { 
    public CEEndRoundMsg (Object args[])
        {
        super(args);
        }

    public void getClientResponse(ClientWindow cw)
        {
        if (cw instanceof CENetworkActionClientWindow)
            {
            CENetworkActionClientWindow nacw = (CENetworkActionClientWindow)cw;
            /* Compute earnings totals here and update display labels */

            double per = 0;  // Points earned round.
            Double pep = (Double)nacw.getNetwork().getExtraData("PntEarnedPeriod");
            Double pen = (Double)nacw.getNetwork().getExtraData("PntEarnedNetwork");
	    Hashtable ph = (Hashtable)nacw.getNetwork().getExtraData("ProfitSoFar");
            CENode me = (CENode)nacw.getNetwork().getExtraData("Me");
            CENodeResource nr = (CENodeResource)me.getExptData("CENodeResource");
            per = nr.getAvailableWorth();
            nacw.getNetwork().setExtraData("PntEarnedRound",new Double(per));
            nacw.getNetwork().setExtraData("PntEarnedPeriod",new Double(per + pep.doubleValue()));
            nacw.getNetwork().setExtraData("PntEarnedNetwork",new Double(per + pen.doubleValue()));

            CENetwork cen = (CENetwork)nacw.getExpApp().getActiveAction();
            CEPeriod cep = cen.getActivePeriod();
            cep.setCurrentRound(cep.getCurrentRound()+1);

	    //
            Object[] out_args = new Object[1];
            out_args[0] = new Double(per);
            CEEndRoundMsg tmp = new CEEndRoundMsg(out_args);
            cw.getSML().sendMessage(tmp);
            }
        else
            {
            new ErrorDialog("Wrong Client Window. - CEEndRoundMsg");
            }
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();

// System.err.println("ESR: CE End Round Message");
    
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
                        err_args[1] = new String("CEEndRoundMsg");
                        return new ExptErrorMsg(err_args);
                        }
                    ec.sendToAllUsers(new CEEndRoundMsg(args));
                    return null; 
                    }
                else
                    {
                    if (!ec.allRegistered())
                        return null;
                    Object[] out_args = new Object[2];
                    out_args[0] = new Integer(index);
                    out_args[1] = args[0];
                    ec.addServerMessage(new CEEndRoundMsg(out_args));
                    ec.sendToAllObservers(new CEEndRoundMsg(out_args));
                    return null;
                    }
                }
            }
        else
            {
            Object[] err_args = new Object[2];
            err_args[0] = new String("No experiment.");
            err_args[1] = new String("CEEndRoundMsg");
            return new ExptErrorMsg(err_args);
            }
        }

	public void getExperimenterResponse(ExperimenterWindow ew){

	    Integer index = (Integer)this.getArgs()[0];
	    Double per = (Double)this.getArgs()[1];
	    
	    if (ew.getExpApp().getExptRunning())
		ew.getExpApp().setReady(true,index.intValue());

	    /////// clean up this dirty mess that you've created.....////
	    /////// read this about a month after 15th Feb - You definitely won't understand it
	    // at that point clean it.

	    /* Update earnings for this user here. */
	    CENetwork cen = (CENetwork)ew.getExpApp().getActiveAction().getAction();
	    double[] pen = (double[])cen.getExtraData("PntEarnedNetwork"); //do I have to adjust profit here? -probably yes
	    Hashtable profitTable = (Hashtable)cen.getExtraData("ProfitEarnedNetwork");
	    pen[index.intValue()] = per.doubleValue() + pen[index.intValue()];
	    if(profitTable==null)
		profitTable = new Hashtable(); // on the first time...

	    boolean flag = true;
	    for (int x=0;x<ew.getExpApp().getNumUsers();x++){
		if (!ew.getExpApp().getReady(x))
		    flag = false;
	    }
	    if (flag){

		ew.getExpApp().initializeReady();
		CEPeriod cep = cen.getActivePeriod();
		    
		/* update the data output here */
		CENetworkActionExperimenterWindow naew = (CENetworkActionExperimenterWindow)ew;
		    
		Vector outData = (Vector)naew.getNetwork().getExtraData("Data");
		    
		Enumeration enm = naew.getNetwork().getNodeList().elements();
		while (enm.hasMoreElements()){

		    CENode node = (CENode)enm.nextElement();
		    CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
		    Enumeration enum2 = nr.getAvailableResources().elements();
		    while (enum2.hasMoreElements()){
			CEResource cer = (CEResource)enum2.nextElement();
			int exptID = ew.getExpApp().getExptOutputID();
			int ai = naew.getExpApp().getActionIndex();
			int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
			int cp = naew.getNetwork().getCurrentPeriod() + 1;
				    
			CEEndRoundResOutputObject data = new CEEndRoundResOutputObject(exptID,ai,cp,cr,node.getID(),cer);
			outData.addElement(data);
                    }
		    // adding the profit here...
		    Double d = (Double)profitTable.get(node.getLabel());
		    double cumProfit=0.0;
		    if(d!=null)
			cumProfit = d.doubleValue();
		    cumProfit+=nr.getNetProfit();
		    profitTable.put(node.getLabel(),new Double(cumProfit));
		    System.err.println("adding profit for node:"+node.getLabel()+"="+cumProfit);
                }

		int exptID = ew.getExpApp().getExptOutputID();
		int ai = naew.getExpApp().getActionIndex();
		int cr = naew.getNetwork().getActivePeriod().getCurrentRound() + 1;
		int cp = naew.getNetwork().getCurrentPeriod() + 1;

		/*  **** update externality data here ***   */
		/* 
		   note: For the user, we're displaying the individual and sum of externalities.
		   but in the database we're storing individual externalities only.(not the cumulative externality)
		   e.g., if A's getting externality from B and C, this individual information will be stored in the database.
		   but not the sum of B+C.
		*/
		/* the following functionality has been replicated from girard.sc.ce.awt.RoundWindow.getExternality() */
		CEExternalityObject ceeo = (CEExternalityObject)cen.getExtraData("CEExternality");
		if(ceeo!=null){
		    // get nodes
		    Hashtable nl = cen.getNodeList();
		    Hashtable nlist2 = cen.getNodeList();
		    Enumeration nodeIter = nl.elements();
		    System.out.println("ok...checking for externality");
		    while(nodeIter.hasMoreElements()){
			//get label
			CENode affectedNode = (CENode)nodeIter.nextElement();
			String affected = affectedNode.getLabel();
			// get the list of nodes 
			Enumeration ni2 = nlist2.elements();
			System.err.println("checking externality on "+affected);
			while(ni2.hasMoreElements()){
			    CENode affectingNode = (CENode)ni2.nextElement();
			    String affecting = affectingNode.getLabel();
			    System.err.println("checking the externality imposed by "+affecting);
			    //		    check if I've encountered an externality due to this node.
			    if(!affectingNode.performedExchange())
				continue;
			    System.err.println("hurray");
			    iExternalityInfo iei = ceeo.getExternality(affecting,affected);
			    if(iei!=null){
				// calculate the externality on me
				System.err.println("iei not null");
				CENodeResource nr = (CENodeResource)affectingNode.getExptData("CENodeResource");
				Enumeration affEnum = nr.getInitialResources().elements();
				double initialWorth = 0;
				while(affEnum.hasMoreElements()){
				    CEResource ceres = (CEResource)affEnum.nextElement();
				    initialWorth+=(int)(ceres.getValue()*ceres.getResource());
				}
				double finalWorth = nr.getAvailableWorth();
				double affProfit = finalWorth - initialWorth;
				double externalityValue = affProfit*iei.getAValue()+iei.getBValue();
				// add it to the profit...
				double myProfit = ((Double)profitTable.get(affected)).doubleValue();
				profitTable.put(affected,new Double(myProfit+externalityValue));
				System.err.println("adding externality in the table for:"+affected+myProfit+" "+externalityValue+ "to a value of "+myProfit);
				CEExternalityOutputObject ceeoo  = new  CEExternalityOutputObject(exptID, ai, cp, cr, affectingNode.getID(), "all", affectedNode.getID(),(float)externalityValue);
				outData.add(ceeoo);
				// add the dataitem to the database
			    }
			    else
				System.err.println(" why is iei null?");
			}
		    }
		}
		else { // no externality!!
		}

		//now update the profitTable
		cen.setExtraData("ProfitEarnedNetwork",profitTable);
		System.err.println("ok..ok...now calling the reset");
		//now reset the exchanged info - need for externality.
		// get nodes
		Enumeration enum1 = cen.getNodeList().elements();
		while(enum1.hasMoreElements())
		    ((CENode)enum1.nextElement()).resetExchanged();

		/* Save gathered output data here */
		if (naew.saveOutputResults("ccDB",(Vector)cen.getExtraData("Data"))){

		    Vector v = (Vector)cen.getExtraData("Data");
		    v.removeAllElements();
		}
		else{
		    /* Something bad happened. */
		}

		cep.setCurrentRound(cep.getCurrentRound() + 1);

		if (cep.getCurrentRound() < cep.getRounds()){
		    cen.setExtraData("CurrentState",new Double(0));
		    CEStateAction cesa = ((CENetworkAction)ew.getExpApp().getActiveAction()).getNextStateAction();
		    cesa.executeAction(ew);
		    return;
		}
		else{
		    /* Either Start a New period or end the experiment */
		    cen.setCurrentPeriod(cen.getCurrentPeriod() + 1);
		    
		    if (cen.getCurrentPeriod() < cen.getNumPeriods())
			{
			    CEStartNextPeriodMsg tmp = new CEStartNextPeriodMsg(null);
			    ew.getSML().sendMessage(tmp);
			    return;
			}
		    else 
			{
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

        if (!(ow instanceof CENetworkActionObserverWindow))
            {
            new ErrorDialog("Wrong Observer Window. - CEEndRoundMsg");
            return;
            }

        Integer index = (Integer)this.getArgs()[0];
        Double per = (Double)this.getArgs()[1];
        
        if ((!ow.getExpApp().getExptRunning()) || (ow.getExpApp().getExptStopping()))
            return;

        CENetwork cen = (CENetwork)ow.getExpApp().getActiveAction();
        double[] pen = (double[])cen.getExtraData("PntEarnedNetwork");
        pen[index.intValue()] = per.doubleValue() + pen[index.intValue()];
        }
    }
