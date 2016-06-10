package girard.sc.ce.awt;

import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.awt.SortedFixedList;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CEExternalityObject;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.iExternalityInfo;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;
/**
 * This class defines the externality information in a CENetworkAction.
 * <p>
 * <br> Started: 05-10-2004
 * <p>
 * @author <i>Murali</i>dhar Narumanchi
 */

public class CEExternalityInformation extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    CEFormatNetworkActionWindow m_CWApp;
    CENetwork m_activeNetwork;

    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    SortedFixedList m_NodeList;
	SortedFixedList m_affectedNodeList;
    SortedFixedList m_EdgeList;
    FixedLabel m_NodeLabel, m_EdgeLabel;
    FixedLabel m_affectedNodeLabel;
    NumberTextField m_NodeAValue,m_NodeBValue; // the values in Ax+B - the linear externality values
    
    Button m_ExternalityUpdateButton;

    CENode m_NodeIndex = null;
    CENode m_AffectedNodeIndex = null;
    
    CEEdge m_EdgeIndex = null;
    int m_NodeListIndex = -1;
    int m_EdgeListIndex = -1;
    int m_AffectedNodeListIndex = -1;

    public CEExternalityInformation(ExptOverlord app1, CEFormatNetworkActionWindow app2, CENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_CWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("cesew_title"));
        setFont(m_EOApp.getMedWinFont());

  // File Menu Options
        setMenuBar(m_mbar);

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("cesew_file"));

        MenuItem tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cesew_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);
        m_mbar.setFont(m_EOApp.getSmWinFont());

  // Help Menu Options

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("cesew_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cesew_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);

  // Start Setup for the West Panel
        GridBagPanel westPanel = new GridBagPanel(); 

        Label tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("cesew_nodes"));
        tmpLabel.setFont(m_EOApp.getMedWinFont());
        westPanel.constrain(tmpLabel,1,1,4,1,GridBagConstraints.CENTER);

        int[] tmpArray = { 5 , 5 };
        m_NodeList = new SortedFixedList(5,false,2,tmpArray,FixedList.CENTER);
        m_NodeList.setFont(m_EOApp.getMedWinFont());

        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode Ntemp = (CENode)enm.nextElement();
            m_NodeList.addItem(BuildNodeListEntryForNodes(Ntemp));
            }
        m_NodeList.addItemListener(this);
        westPanel.constrain(m_NodeList,1,2,4,5,GridBagConstraints.CENTER); 

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cesew_node")),1,7,2,1,GridBagConstraints.CENTER);
        m_NodeLabel = new FixedLabel(10,"");
        westPanel.constrain(m_NodeLabel,3,7,2,1);
       
  // End Setup for the West Panel


  // Start Setup for the center Panel

        GridBagPanel centerPanel = new GridBagPanel();
        Label tmpLabl = new Label(m_EOApp.getLabels().getObjectLabel("cesew_an"));
        tmpLabl.setFont(m_EOApp.getMedWinFont());
        centerPanel.constrain(tmpLabl,1,1,4,1,GridBagConstraints.CENTER);

        int[] tmpArrayEast = { 5 , 5 };
        m_affectedNodeList = new SortedFixedList(5,false,2,tmpArrayEast,FixedList.CENTER);
        m_affectedNodeList.setFont(m_EOApp.getMedWinFont());

        Enumeration enum2 = m_activeNetwork.getNodeList().elements();
        while (enum2.hasMoreElements())
            {
            CENode Ntemp = (CENode)enum2.nextElement();
            m_affectedNodeList.addItem(BuildNodeListEntryForNodes(Ntemp));
            }
        m_affectedNodeList.addItemListener(this);
        centerPanel.constrain(m_affectedNodeList,1,2,4,5,GridBagConstraints.CENTER); 

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cesew_node")),1,7,2,1,GridBagConstraints.CENTER);
        m_affectedNodeLabel = new FixedLabel(10,"");
        centerPanel.constrain(m_affectedNodeLabel,3,7,2,1);

  // End Setup for the center Panel
 
        /*
         * nvm:::
         * include the following code in the next iteration 
         * where the externality depends on the edge also.
         */

        /* ****************
        //setup the right panel for the nodes that are affected.
        GridBagPanel eastPanel = new GridBagPanel(); 

        tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("cesew_edges"));
        tmpLabel.setFont(m_EOApp.getMedWinFont());
        eastPanel.constrain(tmpLabel,1,1,4,1,GridBagConstraints.CENTER);

        int[] tmpIntArray2 = {13 , 5};
        m_EdgeList = new SortedFixedList(5,false,2,tmpIntArray2,FixedList.CENTER);
        m_EdgeList.setFont(m_EOApp.getMedWinFont());

        enm = m_activeNetwork.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge Etemp = (CEEdge)enm.nextElement();
            m_EdgeList.addItem(BuildEdgeListEntry(Etemp));
            }

        m_EdgeList.addItemListener(this);
        eastPanel.constrain(m_EdgeList,1,2,4,5,GridBagConstraints.CENTER); 

        eastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cesew_edge:")),1,7,2,1,GridBagConstraints.CENTER);
        m_EdgeLabel = new FixedLabel(10,"");

        eastPanel.constrain(m_EdgeList,3,7,2,1);        
  // End Setup for the east Panel
        **************/
 // setup the south panel
        GridBagPanel southPanel = new GridBagPanel(); 

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cesew_ac")),1,8,2,1);
        m_NodeAValue= new NumberTextField("",5);
        m_NodeAValue.setAllowFloat(true);
        m_NodeAValue.setAllowNegative(true);
        southPanel.constrain(m_NodeAValue,3,8,2,1);
        
        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cesew_bc")),5,8,2,1);
        m_NodeBValue= new NumberTextField("",5);
        m_NodeBValue.setAllowFloat(true);
        m_NodeBValue.setAllowNegative(true);
        southPanel.constrain(m_NodeBValue,7,8,2,1);
        
        m_ExternalityUpdateButton = new Button(m_EOApp.getLabels().getObjectLabel("cesew_update"));
        m_ExternalityUpdateButton.addActionListener(this);
        southPanel.constrain(m_ExternalityUpdateButton,4,9,4,1,GridBagConstraints.CENTER);
  
        // end south panel.

        
        
        add("West",westPanel);
        add("Center",centerPanel);
//        add("East",eastPanel);
        add("South",southPanel);
        pack();
/*
        m_NodeList.setSize(m_NodeList.getPreferredSize());
        m_EdgeList.setSize(m_EdgeList.getPreferredSize());
        setSize(getPreferredSize());
*/
        show();
        }

    public void actionPerformed (ActionEvent e){
    	
        if (e.getSource() instanceof Button){
        	Button theSource = (Button)e.getSource();
            if (theSource == m_ExternalityUpdateButton) {
                if((m_AffectedNodeListIndex > -1) && (m_NodeListIndex>-1)){
            		CEExternalityObject ceeo = (CEExternalityObject)m_activeNetwork.getExtraData("CEExternality");
            		System.err.println("printing the extra data in the experiment");
            		m_activeNetwork.printExtraData();
                	if(ceeo==null){
            			System.err.println("creating externality for the first time...");
            			System.err.println(m_NodeAValue.getFloatValue());
	            		System.err.println(m_NodeBValue.getFloatValue());
	                	ceeo = new CEExternalityObject();
	                	ceeo.addExternality(m_NodeIndex.getLabel(),m_AffectedNodeIndex.getLabel(),m_NodeAValue.getFloatValue(),m_NodeBValue.getFloatValue());
	                	m_activeNetwork.setExtraData("CEExternality",ceeo);
            		}
            		else{
            			Vector ev = ceeo.getExternality(m_NodeIndex.getLabel());
            			ceeo.addExternality(m_NodeIndex.getLabel(),m_AffectedNodeIndex.getLabel(),
            								m_NodeAValue.getFloatValue(),m_NodeBValue.getFloatValue());
            		}
                }
            }
        }
        
        if (e.getSource() instanceof MenuItem){
            MenuItem theSource = (MenuItem)e.getSource();
            // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cesew_done"))){
                removeLabels();
                m_CWApp.setEditMode(false);
                dispose();
                return;
            }
            // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cesew_help"))){
                m_EOApp.helpWindow("ehlp_cesew");
            }
        }
    }

    public void itemStateChanged(ItemEvent e){
        if (e.getSource() instanceof SortedFixedList){
            SortedFixedList theSource = (SortedFixedList)e.getSource();         

            //affecting Node selected..
            if ((theSource == m_NodeList) && (theSource.getSelectedIndex() > -1)){
                m_NodeListIndex = theSource.getSelectedIndex();
                m_NodeIndex = findNode(m_NodeList.getSelectedSubItem(0));
                m_NodeLabel.setText(m_NodeIndex.getLabel());
                this.updateExternalityLabels();
            }
            // affected Node selected
            if( (theSource==m_affectedNodeList) && (theSource.getSelectedIndex() > -1) ){
            	//nvm - display the value of the node selected
            	m_AffectedNodeListIndex = theSource.getSelectedIndex();
                m_AffectedNodeIndex = findNode(m_affectedNodeList.getSelectedSubItem(0));
                m_affectedNodeLabel.setText(""+m_AffectedNodeIndex.getLabel());
                this.updateExternalityLabels();
            }
            
            /* nvm:
             * ***
             *   The following code will be included after implementing the dependency on the edges.
             * ***
            */
            /*
            if ((theSource == m_EdgeList) && (theSource.getSelectedIndex() > -1))
                {
                m_EdgeListIndex = theSource.getSelectedIndex();
                m_EdgeIndex = findEdge(m_EdgeList.getSelectedSubItem(0));
          //      CEEdgeInteraction ei = (CEEdgeInteraction)m_EdgeIndex.getExptData("CEEdgeInteraction");
                m_EdgeLabel.setText(m_EdgeList.getSelectedSubItem(0));
                }
  
            */          
   
            }
        }

    /**
     * utility method to update the externality Labels 
     * this code was being used at multiple places
     */
    private void updateExternalityLabels(){
        String tmpAVal = "0";
        String tmpBVal = "0";
        if(m_NodeIndex==null || m_AffectedNodeIndex==null){
       		setExtLabelToZero();
       		return;
        }
       	// get the CEExternalityObject.
   		CEExternalityObject ceeo = (CEExternalityObject)m_activeNetwork.getExtraData("CEExternality");
       	if(ceeo==null){
       		setExtLabelToZero();
       		return;
       	}
       	iExternalityInfo eInfo= ceeo.getExternality(m_NodeIndex.getLabel(),m_AffectedNodeIndex.getLabel());
       	if(eInfo==null)
       		setExtLabelToZero();
       	else{
       		tmpAVal = new Float(eInfo.getAValue()).toString();
       		tmpBVal = new Float(eInfo.getBValue()).toString();
	   	}
        m_NodeAValue.setText(tmpAVal);
        m_NodeBValue.setText(tmpBVal);
    }
    
    /**
     * a utility method to avoid code reuse at multiple points
     *
     */
    private void setExtLabelToZero(){
        m_NodeAValue.setText("0");
    	m_NodeBValue.setText("0");
    }


    private String[] BuildEdgeListEntry(CEEdge Etemp)
        {
        CEEdgeInteraction er = (CEEdgeInteraction)Etemp.getExptData("CEEdgeInteraction");
        CENode n1 = (CENode)m_activeNetwork.getNode(Etemp.getNode1());
        CENode n2 = (CENode)m_activeNetwork.getNode(Etemp.getNode2());

        String[] str = new String[2];

        String str1 = new String(""+n1.getLabel()+"("+n1.getID()+")");
        String str2 = new String(""+n2.getLabel()+"("+n2.getID()+")");

        str[0] = new String(str1+"-"+str2);
        str[1] = new String(""+er.getContinuous());

        return str;
        }
    private String[] BuildNodeListEntry(CENode Ntemp)
        {
        String[] str = new String[2];

        CENodeResource nr = (CENodeResource)Ntemp.getExptData("CENodeResource");

        str[0] = new String(""+Ntemp.getLabel()+"("+Ntemp.getID()+")");
        
        str[1] = new String(""+nr.getMax());

        return str;
        }

    private String[] BuildNodeListEntryForNodes(CENode Ntemp)
    {
    String[] str = new String[2];

    CENodeResource nr = (CENodeResource)Ntemp.getExptData("CENodeResource");

    str[0] = new String(""+Ntemp.getLabel()+"("+Ntemp.getID()+")");
    str[1] = new String(" ");
    //nvm - quick and dirty fix. <<<Check>>> why you can't have just an array of size one?
    //str[1] = new String(""+nr.getMax());

    return str;
    }


     
    private CEEdge findEdge(String edge)
        {
        Enumeration enm = m_activeNetwork.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge e = (CEEdge)enm.nextElement();
            CENode n1 = (CENode)m_activeNetwork.getNode(e.getNode1());
            CENode n2 = (CENode)m_activeNetwork.getNode(e.getNode2());

            String str1 = new String(""+n1.getLabel()+"("+n1.getID()+")");
            String str2 = new String(""+n2.getLabel()+"("+n2.getID()+")");

            String str = new String(str1+"-"+str2);

            if (edge.equals(str))
                return e;
            }
        return null;
        }
    private CENode findNode(String node)
        {
        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode n = (CENode)enm.nextElement();
            String str = new String(""+n.getLabel()+"("+n.getID()+")");
            if (str.equals(node))
                return n;
            }
         return null;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/cesew.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cesew.txt");
        }
    }
