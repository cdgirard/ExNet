package girard.sc.ce.awt;

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Used to set the Timing Method and Information Level for a CE Network Action.
 * <p>
 * Started: 01-28-2003
 * <p>
 *
 * @author Dudley Girard 
 */

public class CESetInfoAndTimingWindow extends Frame implements ActionListener,ItemListener
    {
    ExptOverlord m_EOApp;
    CEFormatNetworkActionWindow m_FNAWApp;
    CENetwork m_activeNetwork;

    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    SortedFixedList m_nodeList;
    int m_selectedIndex;

/**
 * The CheckboxGroup for setting the timing method.
 */
    CheckboxGroup m_emGroup = new CheckboxGroup();
/**
 * The CheckboxGroup for setting the Information level for a given node.
 */
    CheckboxGroup m_ilGroup = new CheckboxGroup();
    Checkbox[] ilBox = new Checkbox[10];

    public CESetInfoAndTimingWindow(ExptOverlord app1, CEFormatNetworkActionWindow app2, CENetwork app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("cesiatw_title"));
        setFont(m_EOApp.getMedWinFont());

   // Start setup for menubar.
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);
    
        MenuItem tmpMI;
    
    // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("cesiatw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cesiatw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("cesiatw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("cesiatw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End setup for menu bar.

    // Start setup for north panel.
        GridBagPanel northPanel = new GridBagPanel();

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cesiatw_tm")),1,1,4,1,GridBagConstraints.WEST);

        String em = (String)m_activeNetwork.getExtraData("TimingMethod");
        if (em.equals("Simultaneous"))
            {
            Checkbox cb = new Checkbox(m_EOApp.getLabels().getObjectLabel("cesiatw_ns"),m_emGroup,false);
            northPanel.constrain(cb,1,2,2,1);
            cb = new Checkbox(m_EOApp.getLabels().getObjectLabel("cessiatw_simultaneous"),m_emGroup,true);
            northPanel.constrain(cb,3,2,2,1);
            }
        else
            {
            Checkbox cb = new Checkbox(m_EOApp.getLabels().getObjectLabel("cesiatw_ns"),m_emGroup,true);
            northPanel.constrain(cb,1,2,2,1);
            cb = new Checkbox(m_EOApp.getLabels().getObjectLabel("cesiatw_simultaneous"),m_emGroup,false);
            northPanel.constrain(cb,3,2,2,1);
            }

    // End setup for north panel.

    // Start setup for south panel.
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cesiatw_nil")),1,1,10,1,GridBagConstraints.CENTER);

        int[] nl = { 10, 4, 6 };
        m_nodeList = new SortedFixedList(8,false,3,nl,FixedList.CENTER);
        Hashtable h = (Hashtable)m_activeNetwork.getNodeList();
        Enumeration enm = h.elements();
        while (enm.hasMoreElements())
            {
            CENode n = (CENode)enm.nextElement();
            m_nodeList.addItem(BuildNodeListEntry(n));
            }
        m_nodeList.addItemListener(this);
        centerPanel.constrain(m_nodeList,1,2,10,4,GridBagConstraints.CENTER);
    // End Setup for Center Panel

    // Start setup for south panel.
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cesiatw_il")),1,1,10,1,GridBagConstraints.CENTER);

        for (int x=1;x<11;x++)
            {
            ilBox[x-1] = new Checkbox(""+x,m_ilGroup,true);
            ilBox[x-1].addItemListener(this);
            southPanel.constrain(ilBox[x-1],x,2,1,1);
            }
 
        TextArea ta = new TextArea("",5,55,TextArea.SCROLLBARS_VERTICAL_ONLY);
        StringBuffer sb = new StringBuffer("");
        for (int x=1;x<11;x++)
            {
            String str = new String ("cesiatw_l"+x);
            sb.append(m_EOApp.getLabels().getObjectLabel(str)+"\n");
            }
        ta.setText(sb.toString());
        ta.setFont(m_EOApp.getSmWinFont());
        southPanel.constrain(ta,1,5,10,1);
    // End setup for south panel.

        add("North",northPanel);
        add("Center",centerPanel);
        add("South",southPanel);

        pack();

        m_nodeList.setSize(m_nodeList.getPreferredSize());
        setSize(getPreferredSize());

        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

      // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cesiatw_done")))
                {
                if (m_emGroup.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("cesiatw_ns")))
                    {
                    m_activeNetwork.setExtraData("TimingMethod","Non-Simultaneous");
                    }
                else
                    {
                    m_activeNetwork.setExtraData("TimingMethod","Simultaneous");
                    }
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
       // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cesiatw_help")))
                {
                m_EOApp.helpWindow("ehlp_cesiatw");
                }
            }
        }

    public String[] BuildNodeListEntry(CENode n)
        {
        String[] str = new String[3];

        str[0] = n.getLabel();
        str[1] = new String(""+n.getID());
        str[2] = new String(""+n.getInfoLevel());

        return str;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/cesiatw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof Checkbox)
            {
            Checkbox theSource = (Checkbox)e.getSource();

            if (m_selectedIndex > -1)
                {
                int index = (Integer.valueOf(m_nodeList.getSubItem(m_selectedIndex,1))).intValue();
                CENode n = (CENode)m_activeNetwork.getNode(index);
                int il = (Integer.valueOf(theSource.getLabel())).intValue();
                n.setInfoLevel(il);
                m_nodeList.replaceItem(BuildNodeListEntry(n),m_selectedIndex);
                }
            }
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if (theSource == m_nodeList)
                {
                m_selectedIndex = m_nodeList.getSelectedIndex();
                if (m_selectedIndex > -1)
                    {
                    int index = (Integer.valueOf(m_nodeList.getSubItem(m_selectedIndex,1))).intValue();
                    CENode n = (CENode)m_activeNetwork.getNode(index);
                    ilBox[n.getInfoLevel()-1].setState(true);
                    }
                }
            return;
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cesiatw.txt");
        }
    }
