package girard.sc.be.awt;

import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BEEdgeResource;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
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
 * This allows a user to adjust the resource pool amounts on the edges
 * of a BENetworkAction.
 * <p>
 * <br> Started: 07-27-2001
 * <br> Modified: 10-02-2002
 * <p>
 * @author Dudley Girard
 */

public class BEEditRPWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    BEFormatNetworkActionWindow m_EBWApp;
    BENetwork m_activeNetwork;

  // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    CheckboxGroup m_resourceInfoGrp = new CheckboxGroup();

    FixedList m_EdgeList;
    FixedLabel m_EdgeLabel, m_N1Label, m_N2Label;
    NumberTextField m_ResourceField;
    NumberTextField m_InitDemand1Field;
    CheckboxGroup m_reset1Group = new CheckboxGroup();
    Checkbox m_reset1Yes, m_reset1No;
    NumberTextField m_InitDemand2Field;
    CheckboxGroup m_reset2Group = new CheckboxGroup();
    Checkbox m_reset2Yes, m_reset2No;
    Button m_SetResButton;

    // ExnetCanvas ECApp;

    int m_ListIndex = -1;
    BEEdge m_EdgeIndex;
    BEEdgeResource m_ResourceIndex = null;

    public BEEditRPWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_EBWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("beerpw_title"));
        setFont(m_EOApp.getSmWinFont());
  
    // Start Setup for Menubar
       m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

    // File Menu

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("beerpw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("beerpw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

     // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("beerpw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("beerpw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End Setup for Menubar.

    // Start setup for the North Panel

        GridBagPanel NorthPanel = new GridBagPanel();

        BEEdge tmp = (BEEdge)m_activeNetwork.getEdgeList().elementAt(0);
        BEEdgeResource tmpER = (BEEdgeResource)tmp.getExptData("BEEdgeResource");

        NorthPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_dri")),1,1,4,1,GridBagConstraints.CENTER);
        if (tmpER.getDisplayResource())
            {
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("beerpw_yes"),m_resourceInfoGrp,true),1,2,2,1,GridBagConstraints.CENTER);
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("beerpw_no"),m_resourceInfoGrp,false),3,2,2,1,GridBagConstraints.CENTER);
            }
        else
            {
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("beerpw_yes"),m_resourceInfoGrp,false),1,2,2,1,GridBagConstraints.CENTER);
            NorthPanel.constrain(new Checkbox(m_EOApp.getLabels().getObjectLabel("beerpw_no"),m_resourceInfoGrp,true),3,2,2,1,GridBagConstraints.CENTER);
            }
    // End setup for the North Panel

        GridBagPanel MainPanel = new GridBagPanel();

    // List of Edges and their Resources

        Label tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("beerpw_eridr"));
        tmpLabel.setFont(m_EOApp.getMedWinFont());
        MainPanel.constrain(tmpLabel,1,1,8,1,GridBagConstraints.CENTER);

        int[] tmpIntArray = {13,5,9,7};
        m_EdgeList = new FixedList(6,false,4,tmpIntArray,FixedList.CENTER);
        m_EdgeList.setFont(m_EOApp.getMedWinFont());

        Enumeration enm = m_activeNetwork.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge Etemp = (BEEdge)enm.nextElement();
            m_EdgeList.addItem(BuildEdgeListEntry(Etemp));
            }

        m_EdgeList.addItemListener(this);
        MainPanel.constrain(m_EdgeList,1,2,8,4,GridBagConstraints.CENTER); 

    //Minimum Exchange Buttons

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_edge")),1,6,4,1,GridBagConstraints.CENTER);

        m_EdgeLabel = new FixedLabel(10,"");
        MainPanel.constrain(m_EdgeLabel,5,6,4,1);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_resources")),1,7,4,1,GridBagConstraints.CENTER);

        m_ResourceField = new NumberTextField(5);
        m_ResourceField.setAllowFloat(false);
        m_ResourceField.setAllowNegative(false);
        MainPanel.constrain(m_ResourceField,5,7,4,1);
 
        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_node")),1,8,2,1);
        m_N1Label = new FixedLabel(10,"");
        MainPanel.constrain(m_N1Label,3,8,2,1);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_id")),1,9,2,1);

        m_InitDemand1Field = new NumberTextField(5);
        m_InitDemand1Field.setAllowFloat(false);
        m_InitDemand1Field.setAllowNegative(false);
        MainPanel.constrain(m_InitDemand1Field,3,9,2,1);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_rd")),5,8,4,1,GridBagConstraints.CENTER);
        m_reset1Yes = new Checkbox(m_EOApp.getLabels().getObjectLabel("beerpw_yes"),m_reset1Group,false);
        m_reset1No = new Checkbox(m_EOApp.getLabels().getObjectLabel("beerpw_no"),m_reset1Group,false);
        MainPanel.constrain(m_reset1Yes,5,9,2,1,GridBagConstraints.CENTER);
        MainPanel.constrain(m_reset1No,7,9,2,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_node")),1,10,2,1);
        m_N2Label = new FixedLabel(10,"");
        MainPanel.constrain(m_N2Label,3,10,2,1);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_id")),1,11,2,1);

        m_InitDemand2Field = new NumberTextField(5);
        m_InitDemand2Field.setAllowFloat(false);
        m_InitDemand2Field.setAllowNegative(false);
        MainPanel.constrain(m_InitDemand2Field,3,11,2,1);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beerpw_rd")),5,10,4,1,GridBagConstraints.CENTER);
        m_reset2Yes = new Checkbox(m_EOApp.getLabels().getObjectLabel("beerpw_yes"),m_reset2Group,false);
        m_reset2No = new Checkbox(m_EOApp.getLabels().getObjectLabel("beerpw_no"),m_reset2Group,false);
        MainPanel.constrain(m_reset2Yes,5,11,2,1,GridBagConstraints.CENTER);
        MainPanel.constrain(m_reset2No,7,11,2,1,GridBagConstraints.CENTER);

        m_SetResButton = new Button(m_EOApp.getLabels().getObjectLabel("beerpw_sr"));
        m_SetResButton.addActionListener(this);
        m_SetResButton.setFont(m_EOApp.getMedWinFont());
        MainPanel.constrain(m_SetResButton,1,12,8,1,GridBagConstraints.CENTER);

        add("North",NorthPanel);
        add("Center",MainPanel);
        pack();
        show();
 
        m_EdgeList.setSize(m_EdgeList.getPreferredSize());
        setSize(getPreferredSize());
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (m_ListIndex != -1) 
                {
                if (theSource == m_SetResButton)
                    {
                    try
                        {
                        int res = Integer.valueOf(m_ResourceField.getText()).intValue();
                        int id1 = Integer.valueOf(m_InitDemand1Field.getText()).intValue();
                        int id2 = Integer.valueOf(m_InitDemand2Field.getText()).intValue();
                    
                        if ((res > 1) && (id1 < res) && (id1 > 0) && (id2 > 0) && (id2 < res))
                            {
                            m_ResourceIndex.getRes().setResource(res);
                            m_ResourceIndex.setN1InitialDemand(id1);
                            m_ResourceIndex.setN1ResetDemand(m_reset1Yes.getState());
                            m_ResourceIndex.setN2InitialDemand(id2);
                            m_ResourceIndex.setN2ResetDemand(m_reset2Yes.getState());
                            m_EdgeList.replaceItem(BuildEdgeListEntry(m_EdgeIndex),m_ListIndex);
                            m_EdgeList.select(m_ListIndex);
                            }
                        }
                    catch (NumberFormatException mfe) { }
                    }
                }
            }

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

       // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("beerpw_exit")))
                {
                Enumeration enm = m_activeNetwork.getEdgeList().elements();
                while (enm.hasMoreElements())
                    {
                    BEEdge edge = (BEEdge)enm.nextElement();
                    BEEdgeResource er = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    if (m_resourceInfoGrp.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("beerpw_yes")))
                        {
                        er.setDisplayResource(true);
                        }
                    else
                        {
                        er.setDisplayResource(false);
                        }
                    }
                m_EBWApp.setEditMode(false);
                removeLabels();
                dispose();
                }

         // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("beerpw_help")))
                {
                m_EOApp.helpWindow("ehlp_beerpw");
                }
            }
        }
   
    public String[] BuildEdgeListEntry(BEEdge Etemp)
        {
        BEEdgeResource er = (BEEdgeResource)Etemp.getExptData("BEEdgeResource");
        BENode n1 = (BENode)m_activeNetwork.getNode(Etemp.getNode1());
        BENode n2 = (BENode)m_activeNetwork.getNode(Etemp.getNode2());

        String rd1 = new String("N");
        String rd2 = new String("N");

        if (er.getN1ResetDemand())
            rd1 = "Y";
        if (er.getN2ResetDemand())
            rd2 = "Y";

        String[] str = new String[4];

        String str1 = new String(""+n1.getLabel()+"("+n1.getID()+")");
        String str2 = new String(""+n2.getLabel()+"("+n2.getID()+")");

        str[0] = new String(str1+"-"+str2);
        str[1] = new String(""+er.getRes().getIntResource());
        str[2] = new String(""+er.getN1InitialDemand()+" / "+er.getN2InitialDemand());
        str[3] = new String(""+rd1+" / "+rd2);

        return str;
        }

    public String getEdgeLabel(BEEdge Etemp)
        {
        BENode n1 = (BENode)m_activeNetwork.getNode(Etemp.getNode1());
        BENode n2 = (BENode)m_activeNetwork.getNode(Etemp.getNode2());

        String str = new String(""+n1.getLabel()+"("+n1.getID()+")-"+n2.getLabel()+"("+n2.getID()+")");
        return str;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/beerpw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if ((theSource == m_EdgeList) && (theSource.getSelectedIndex() > -1))
                {
                m_ListIndex = theSource.getSelectedIndex();
                m_EdgeIndex = (BEEdge)m_activeNetwork.getEdgeList().elementAt(m_ListIndex);
                m_ResourceIndex = (BEEdgeResource)m_EdgeIndex.getExptData("BEEdgeResource");

                m_EdgeLabel.setText(getEdgeLabel(m_EdgeIndex));

                BENode n1 = (BENode)m_activeNetwork.getNode(m_EdgeIndex.getNode1());
                BENode n2 = (BENode)m_activeNetwork.getNode(m_EdgeIndex.getNode2());

                m_N1Label.setText(n1.getLabel());
                m_N2Label.setText(n2.getLabel());
                m_ResourceField.setText(""+m_ResourceIndex.getRes().getIntResource());

                m_InitDemand1Field.setText(""+m_ResourceIndex.getN1InitialDemand());
                if (m_ResourceIndex.getN1ResetDemand())
                    m_reset1Yes.setState(true);
                else
                    m_reset1No.setState(true);

                m_InitDemand2Field.setText(""+m_ResourceIndex.getN2InitialDemand());
                if (m_ResourceIndex.getN1ResetDemand())
                    m_reset2Yes.setState(true);
                else
                    m_reset2No.setState(true);
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/beerpw.txt");
        }
    }
