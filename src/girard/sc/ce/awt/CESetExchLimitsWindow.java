package girard.sc.ce.awt;

import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.awt.SortedFixedList;
import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
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

/**
 * Used to set limits on the number of exchanges for the CENetworkAction.
 * <p>
 * <br> Started: 01-24-2003
 * <p>
 * @author Dudley Girard
 */

public class CESetExchLimitsWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    CEFormatNetworkActionWindow m_CWApp;
    CENetwork m_activeNetwork;

    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    SortedFixedList m_NodeList, m_EdgeList;
    FixedLabel m_NodeLabel, m_EdgeLabel;
    NumberTextField m_NodeMaxField, m_EdgeMaxField;
    
    Button m_NodeUpdateButton, m_EdgeUpdateButton;

    CENode m_NodeIndex = null;
    CEEdge m_EdgeIndex = null;
    int m_NodeListIndex = -1;
    int m_EdgeListIndex = -1;

    public CESetExchLimitsWindow(ExptOverlord app1, CEFormatNetworkActionWindow app2, CENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_CWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ceselw_title"));
        setFont(m_EOApp.getMedWinFont());

  // File Menu Options
        setMenuBar(m_mbar);

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("ceselw_file"));

        MenuItem tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ceselw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);
        m_mbar.setFont(m_EOApp.getSmWinFont());

  // Help Menu Options

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ceselw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ceselw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);

  // Start Setup for the West Panel

        GridBagPanel westPanel = new GridBagPanel(); 

        Label tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("ceselw_nm"));
        tmpLabel.setFont(m_EOApp.getMedWinFont());
        westPanel.constrain(tmpLabel,1,1,4,1,GridBagConstraints.CENTER);

        int[] tmpArray = { 9 , 5 };
        m_NodeList = new SortedFixedList(5,false,2,tmpArray,FixedList.CENTER);
        m_NodeList.setFont(m_EOApp.getMedWinFont());

        Enumeration enm = m_activeNetwork.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode Ntemp = (CENode)enm.nextElement();
            m_NodeList.addItem(BuildNodeListEntry(Ntemp));
            }

        m_NodeList.addItemListener(this);
        westPanel.constrain(m_NodeList,1,2,4,5,GridBagConstraints.CENTER); 

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceselw_node")),1,7,2,1,GridBagConstraints.CENTER);
        m_NodeLabel = new FixedLabel(10,"");
        westPanel.constrain(m_NodeLabel,3,7,2,1);

        westPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceselw_ea")),1,8,2,1);
        m_NodeMaxField = new NumberTextField("",5);
        m_NodeMaxField.setAllowFloat(false);
        m_NodeMaxField.setAllowNegative(true);
        westPanel.constrain(m_NodeMaxField,3,8,2,1);

        m_NodeUpdateButton = new Button(m_EOApp.getLabels().getObjectLabel("ceselw_update"));
        m_NodeUpdateButton.addActionListener(this);
        westPanel.constrain(m_NodeUpdateButton,1,9,4,1,GridBagConstraints.CENTER);

  // End Setup for the West Panel


  // Start Setup for the East Panel

        GridBagPanel eastPanel = new GridBagPanel();

        tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("ceselw_em"));
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

        eastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceselw_edge:")),1,7,2,1,GridBagConstraints.CENTER);
        m_EdgeLabel = new FixedLabel(10,"");
        eastPanel.constrain(m_EdgeLabel,3,7,2,1);

        eastPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ceselw_ea")),1,8,2,1);
        m_EdgeMaxField = new NumberTextField("",5);
        m_EdgeMaxField.setAllowFloat(false);
        m_EdgeMaxField.setAllowNegative(true);
        eastPanel.constrain(m_EdgeMaxField,3,8,2,1);
        
        m_EdgeUpdateButton = new Button(m_EOApp.getLabels().getObjectLabel("ceselw_update"));
        m_EdgeUpdateButton.addActionListener(this);
        eastPanel.constrain(m_EdgeUpdateButton,1,9,4,1,GridBagConstraints.CENTER);
  // End Setup for the East Panel
       

        add("West",westPanel);
        add("East",eastPanel);

        pack();

        m_NodeList.setSize(m_NodeList.getPreferredSize());
        m_EdgeList.setSize(m_EdgeList.getPreferredSize());
        setSize(getPreferredSize());

        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if ((theSource == m_EdgeUpdateButton) && (m_EdgeListIndex > -1))
                {
                CEEdgeInteraction ei = (CEEdgeInteraction)m_EdgeIndex.getExptData("CEEdgeInteraction");
                ei.setContinuous(m_EdgeMaxField.getIntValue());
                if (ei.getContinuous() < -1)
                    ei.setContinuous(-1);
                m_EdgeList.replaceItem(BuildEdgeListEntry(m_EdgeIndex),m_EdgeListIndex);
                m_EdgeList.select(m_EdgeListIndex);
                }
            if ((theSource == m_NodeUpdateButton) && (m_NodeListIndex > -1))
                {
                CENodeResource nr = (CENodeResource)m_NodeIndex.getExptData("CENodeResource");
                nr.setMax(m_NodeMaxField.getIntValue());
                if (nr.getMax() < -1)
                    nr.setMax(-1);
                m_NodeList.replaceItem(BuildNodeListEntry(m_NodeIndex),m_NodeListIndex);
                m_NodeList.select(m_NodeListIndex);
                }
            }
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ceselw_done")))
                {
                removeLabels();
                m_CWApp.setEditMode(false);
                dispose();
                return;
                }
    // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ceselw_help")))
                {
                m_EOApp.helpWindow("ehlp_ceselw");
                }
            }
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

        str[0] = new String(""+Ntemp.getLabel()+" - "+Ntemp.getID());
        str[1] = new String(""+nr.getMax());

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
            String str = new String(""+n.getLabel()+" - "+n.getID());
            if (str.equals(node))
                return n;
            }
         return null;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/ceselw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if ((theSource == m_EdgeList) && (theSource.getSelectedIndex() > -1))
                {
                m_EdgeListIndex = theSource.getSelectedIndex();
                m_EdgeIndex = findEdge(m_EdgeList.getSelectedSubItem(0));
                CEEdgeInteraction ei = (CEEdgeInteraction)m_EdgeIndex.getExptData("CEEdgeInteraction");
                m_EdgeLabel.setText(m_EdgeList.getSelectedSubItem(0));
                m_EdgeMaxField.setText(""+ei.getContinuous());
                }
            if ((theSource == m_NodeList) && (theSource.getSelectedIndex() > -1))
                {
                m_NodeListIndex = theSource.getSelectedIndex();
                m_NodeIndex = findNode(m_NodeList.getSelectedSubItem(0));
                CENodeResource cr = (CENodeResource)m_NodeIndex.getExptData("CENodeResource");
                m_NodeLabel.setText(""+m_NodeIndex.getLabel()+" - "+m_NodeIndex.getID());
                m_NodeMaxField.setText(""+cr.getMax());
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/ceselw.txt");
        }
    }
