package girard.sc.ce.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.ce.io.msg.CERoundWindowMsg;
import girard.sc.ce.obj.CEExternalityObject;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEPeriod;
import girard.sc.ce.obj.CEResource;
import girard.sc.ce.obj.iExternalityInfo;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * CE Network Action: Tell the clients a new round is starting, how much they
 * made last round, and what position they are in the network.
 * <p>
 * <br> Started: 02-10-2003
 * <p>
 *
 * @author Dudley Girard
 */

public class CERoundWindow extends Frame implements ActionListener{
    CENetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Button m_ReadyButton;
    boolean externality = false; // used to determine if I'm incurring externality
    CENetwork net;
    CENode me;

    public CERoundWindow(CENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();
        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("cerw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        net = m_NACWApp.getNetwork();
        me = (CENode)net.getExtraData("Me");
        int initialWorth = 0;
        double externalityValue=0.0;
        //nvm

        if(net.getExtraData("CEExternality")!=null)
          externalityValue = getExternality();
        CENodeResource nr = (CENodeResource)me.getExptData("CENodeResource");
        Enumeration enm = nr.getInitialResources().elements();
        while (enm.hasMoreElements())
            {
            CEResource cer = (CEResource)enm.nextElement();
            initialWorth = initialWorth + (int)(cer.getValue()*cer.getResource());
            }

        GridBagPanel MainPanel = new GridBagPanel();
	int x_axis = 1; //nvm - you moron, use this...remember you
			//spent over three hours just because you
			//didn't realize that you were not changing
			//the line number in the parameter to MainPanel.constrain
        CEPeriod cep = net.getActivePeriod();

        if ((net.getCurrentPeriod() == 0) && (cep.getCurrentRound() == 0))
            {
            // Do nothing.
            }
        else
            {
		
        	System.err.println("Printing the Extradata");
        	net.printExtraData();
            if (net.getExtraData("PntEarnedRound") != null)
                {
		    MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cerw_lr")),1,x_axis++,10,1,GridBagConstraints.CENTER);
		    MainPanel.constrain(new Label("Your Initial Worth Last Round: "+initialWorth),1,x_axis++,10,1,GridBagConstraints.CENTER);
		    MainPanel.constrain(new Label("Your End Worth Last Round: "+((Double)net.getExtraData("PntEarnedRound")).intValue()),1,x_axis++,10,1,GridBagConstraints.CENTER);
		    if(externality){
                	MainPanel.constrain(new Label("You incurred an externality of: "+externalityValue),1,x_axis++,10,1,GridBagConstraints.CENTER);
			
		    }
		    int value = ((Double)net.getExtraData("PntEarnedRound")).intValue() - initialWorth + (int)externalityValue;
		    Label tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("cerw_ye")+value);
		    tmpLabel.setFont(new Font("Monospaced",Font.BOLD,18));

		    if (value <= 0)
			tmpLabel.setForeground(Color.red);
		    else
			tmpLabel.setForeground(CEColor.edgeGreen);
		    
		    MainPanel.constrain(tmpLabel,1,x_axis++,10,1,GridBagConstraints.CENTER);
                }
            }

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cerw_ftr")),1,x_axis++,4,1,GridBagConstraints.CENTER);

        Label tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("cerw_ya")+me.getLabel());
        tmpLabel.setFont(new Font("Monospaced",Font.BOLD,18));
        tmpLabel.setForeground(Color.black);

        MainPanel.constrain(tmpLabel,1,x_axis++,4,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cerw_wrtcptb")),1,x_axis++,4,1,GridBagConstraints.CENTER);
    
        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("cerw_ready"));
        m_ReadyButton.addActionListener(this);
        MainPanel.constrain(m_ReadyButton,1,x_axis++,4,1,GridBagConstraints.CENTER);

   // Start Setup for North, South, East, and West Panels.
        GridBagPanel northPanel = new GridBagPanel();
        northPanel.constrain(new Label(" "),1,1,1,1);

        GridBagPanel southPanel = new GridBagPanel();
        southPanel.constrain(new Label(" "),1,1,1,1);

        GridBagPanel eastPanel = new GridBagPanel();
        eastPanel.constrain(new Label(" "),1,1,1,1);

        GridBagPanel westPanel = new GridBagPanel();
        westPanel.constrain(new Label(" "),1,1,1,1);
   // End Setup for  North, South, East, and West Panels.

        add("Center",MainPanel);
        add("North",northPanel);
        add("South",southPanel);
        add("East",eastPanel);
        add("West",westPanel);
        pack();
        show();
        }

    /**
     * This method calculates the externality incurred by this Node.
     * @return the externality value
     */
    public double getExternality(){
    	double externalityValue = 0;
    	CEExternalityObject ceeo = (CEExternalityObject)net.getExtraData("CEExternality");
    	if(ceeo==null)
    		return 0;
    	String myLabel = me.getLabel();
    	Hashtable nl = net.getNodeList();
    	// check if myLabel is incurring any externality
    	Enumeration en = nl.elements();
    	while(en.hasMoreElements()){
	    CENode otherNode = (CENode)en.nextElement();
	    /*
	      if(OtherNode.getLabel().equals(myLabel))
	      continue;// no point comparing myself
	      but what if some researcher wants a situation where a node gets a "bonus" externality of its own
	      profit?
	    */
	    // if the affecting node has not performed any
	    // exchanges, it has no right to impose
	    // externality - thx to Jacob for pointing out the
	    // error :-)
	    if(!otherNode.performedExchange())
		continue;
	    String oLabel = otherNode.getLabel();
	    iExternalityInfo iei = ceeo.getExternality(oLabel,myLabel);
	    if(iei!=null){
    	    	// note - externalities could cancel out and give a value of 0. 
    	    	// Hence the var should be set true if I incur any externality at all.
		externality =true;
		// calculate the initial worth of the other Node
		CENodeResource onr = (CENodeResource)otherNode.getExptData("CENodeResource");
		Enumeration enum1 = onr.getInitialResources().elements();
		double otherInitialWorth = 0;
		while (enum1.hasMoreElements()){
		    CEResource cer = (CEResource)enum1.nextElement();
		    otherInitialWorth = otherInitialWorth + (int)(cer.getValue()*cer.getResource());
		}
		double finalWorth = onr.getAvailableWorth();
		double otherNodeProfit = finalWorth - otherInitialWorth;
		double thisExternality = otherNodeProfit*iei.getAValue() + iei.getBValue();
		externalityValue+=thisExternality;
	    }
    	}
    	return externalityValue;
    }
    
/********************************************************************************
Callback for the Ready button on the Ready Message Window
*********************************************************************************/
    public void actionPerformed(ActionEvent e)
        {
        Button theSourceB = null;

        if (e.getSource() instanceof Button)
            theSourceB = (Button)e.getSource();
        
        if (theSourceB == m_ReadyButton)
            {
            CENetwork cen = (CENetwork)m_NACWApp.getExpApp().getActiveAction();
	    Enumeration enum1 = cen.getNodeList().elements();
	    while(enum1.hasMoreElements())
		((CENode)enum1.nextElement()).resetExchanged();
	    


            CEPeriod cep = cen.getActivePeriod();
            cep.setCurrentTime(cep.getTime());
            CERoundWindowMsg tmp = new CERoundWindowMsg(null);
            m_NACWApp.getSML().sendMessage(tmp);
            m_NACWApp.removeSubWindow(this);
            m_NACWApp.setMessageLabel("Please wait while others are reading.");
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/cerw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cerw.txt");
        }
    }
