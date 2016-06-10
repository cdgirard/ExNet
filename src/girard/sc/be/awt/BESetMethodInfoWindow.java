package girard.sc.be.awt;

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
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
 * Used to set the Exchange Method and Information Level for a BE Network Action.
 * <p>
 * Started: 1-20-2002
 * Modified: 8-7-2002
 * <p>
 *
 * @author Dudley Girard 
 */

public class BESetMethodInfoWindow extends Frame implements ActionListener, ItemListener
{
    ExptOverlord m_EOApp;

    BEFormatNetworkActionWindow m_FNAWApp;

    BENetwork m_activeNetwork;

    MenuBar m_mbar = new MenuBar();

    Menu m_File, m_Help;

    SortedFixedList m_nodeList;

    int m_selectedIndex;

    CheckboxGroup m_emGroup = new CheckboxGroup();

    CheckboxGroup m_ilGroup = new CheckboxGroup();

    Checkbox[] ilBox = new Checkbox[10];

    CheckboxGroup m_edGroup;

    Checkbox[] edGrpBox = new Checkbox[3];

    public BESetMethodInfoWindow(ExptOverlord app1, BEFormatNetworkActionWindow app2, BENetwork app3)
    {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_FNAWApp = app2; /* Need so can unset edit mode */
        m_activeNetwork = app3; /* Makes referencing easier */

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("besmiw_title"));
        setFont(m_EOApp.getMedWinFont());

        // Start setup for menubar.
        m_mbar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_mbar);

        MenuItem tmpMI;

        // File Menu Options

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("besmiw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("besmiw_done"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("besmiw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("besmiw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
        // End setup for menu bar.

        // Start setup for north panel.
        GridBagPanel northPanel = new GridBagPanel();

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besmiw_em")), 1, 1, 4, 1, GridBagConstraints.CENTER);

        String em = (String) m_activeNetwork.getExtraData("ExchangeMethod");
        if (em.equals("Simultaneous"))
        {
            Checkbox cb = new Checkbox(m_EOApp.getLabels().getObjectLabel("besmiw_ns"), m_emGroup, false);
            northPanel.constrain(cb, 1, 2, 2, 1);
            cb = new Checkbox(m_EOApp.getLabels().getObjectLabel("besmiw_simultaneous"), m_emGroup, true);
            northPanel.constrain(cb, 3, 2, 2, 1);
        }
        else
        {
            Checkbox cb = new Checkbox(m_EOApp.getLabels().getObjectLabel("besmiw_ns"), m_emGroup, true);
            northPanel.constrain(cb, 1, 2, 2, 1);
            cb = new Checkbox(m_EOApp.getLabels().getObjectLabel("besmiw_simultaneous"), m_emGroup, false);
            northPanel.constrain(cb, 3, 2, 2, 1);
        }

        // End setup for north panel.

        // Start setup for south panel.
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besmiw_nil")), 1, 1, 10, 1, GridBagConstraints.CENTER);

        int[] nl = { 10, 4, 6 };
        m_nodeList = new SortedFixedList(8, false, 3, nl, FixedList.CENTER);
        Hashtable h = (Hashtable) m_activeNetwork.getNodeList();
        Enumeration enm = h.elements();
        while (enm.hasMoreElements())
        {
            BENode n = (BENode) enm.nextElement();
            m_nodeList.addItem(BuildNodeListEntry(n));
        }
        m_nodeList.addItemListener(this);
        centerPanel.constrain(m_nodeList, 1, 2, 10, 4, GridBagConstraints.CENTER);
        // End Setup for Center Panel

        // Start setup for south panel.
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besmiw_il")), 1, 1, 10, 1, GridBagConstraints.CENTER);

        for (int x = 1; x < 11; x++)
        {
            ilBox[x - 1] = new Checkbox("" + x, m_ilGroup, true);
            ilBox[x - 1].addItemListener(this);
            southPanel.constrain(ilBox[x - 1], x, 2, 1, 1);
        }

        southPanel.constrain(new Label(this.m_EOApp.getLabels().getObjectLabel("besmiw_es")), 1, 3, 10, 1, 10);
        (this.edGrpBox[0] = new Checkbox(this.m_EOApp.getLabels().getObjectLabel("besmiw_no"), this.m_edGroup, false)).addItemListener(this);
        southPanel.constrain(this.edGrpBox[0], 1, 4, 3, 1);
        (this.edGrpBox[1] = new Checkbox(this.m_EOApp.getLabels().getObjectLabel("besmiw_lp"), this.m_edGroup, true)).addItemListener(this);
        southPanel.constrain(this.edGrpBox[1], 4, 4, 3, 1);
        (this.edGrpBox[2] = new Checkbox(this.m_EOApp.getLabels().getObjectLabel("besmiw_hp"), this.m_edGroup, true)).addItemListener(this);
        southPanel.constrain(this.edGrpBox[2], 7, 4, 3, 1);

        TextArea ta = new TextArea("", 5, 55, TextArea.SCROLLBARS_VERTICAL_ONLY);
        StringBuffer sb = new StringBuffer("");
        for (int x = 1; x < 11; x++)
        {
            String str = new String("besmiw_l" + x);
            sb.append(m_EOApp.getLabels().getObjectLabel(str) + "\n");
        }
        ta.setText(sb.toString());
        ta.setFont(m_EOApp.getSmWinFont());
        southPanel.constrain(ta, 1, 5, 10, 4);
        // End setup for south panel.

        add("North", northPanel);
        add("Center", centerPanel);
        add("South", southPanel);

        pack();
        setVisible(true);

        m_nodeList.setSize(m_nodeList.getPreferredSize());
        setSize(getPreferredSize());
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() instanceof MenuItem)
        {
            MenuItem theSource = (MenuItem) e.getSource();

            // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("besmiw_done")))
            {
                if (m_emGroup.getSelectedCheckbox().getLabel().equals(m_EOApp.getLabels().getObjectLabel("besmiw_ns")))
                {
                    m_activeNetwork.setExtraData("ExchangeMethod", "Consecutive");
                }
                else
                {
                    m_activeNetwork.setExtraData("ExchangeMethod", "Simultaneous");
                }
                m_FNAWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
            }
            // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("besmiw_help")))
            {
                m_EOApp.helpWindow("ehlp_besmiw");
            }
        }
    }

    public String[] BuildNodeListEntry(BENode n)
    {
        String[] str = new String[3];

        str[0] = n.getLabel();
        str[1] = new String("" + n.getID());
        str[2] = new String("" + n.getInfoLevel());

        return str;
    }

    public void initializeLabels()
    {
        m_EOApp.initializeLabels("girard/sc/be/awt/besmiw.txt");
    }

    public void itemStateChanged(ItemEvent e)
    {
        if (e.getSource() instanceof Checkbox)
        {
            Checkbox theSource = (Checkbox) e.getSource();

            if (m_selectedIndex > -1)
            {
                int index = (Integer.valueOf(m_nodeList.getSubItem(m_selectedIndex, 1))).intValue();
                BENode n = (BENode) m_activeNetwork.getNode(index);
                String box = theSource.getLabel();
                if (box.equals(this.m_EOApp.getLabels().getObjectLabel("besmiw_no")))
                {
                    n.setExtraData("Ed", new Integer(0));
                }
                else if (box.equals(this.m_EOApp.getLabels().getObjectLabel("besmiw_lp")))
                {
                    n.setExtraData("Ed", new Integer(1));
                }
                else if (box.equals(this.m_EOApp.getLabels().getObjectLabel("besmiw_hp")))
                {
                    n.setExtraData("Ed", new Integer(2));
                }
                else
                {
                    int il = (Integer.valueOf(theSource.getLabel())).intValue();
                    n.setInfoLevel(il);
                }
                m_nodeList.replaceItem(BuildNodeListEntry(n), m_selectedIndex);
            }
        }
        if (e.getSource() instanceof SortedFixedList)
        {
            SortedFixedList theSource = (SortedFixedList) e.getSource();

            if (theSource == m_nodeList)
            {
                m_selectedIndex = m_nodeList.getSelectedIndex();
                if (m_selectedIndex > -1)
                {
                    int index = (Integer.valueOf(m_nodeList.getSubItem(m_selectedIndex, 1))).intValue();
                    BENode n = (BENode) m_activeNetwork.getNode(index);
                    ilBox[n.getInfoLevel() - 1].setState(true);
                    if (n.getExtraData("Ed") == null)
                    {
                        this.edGrpBox[0].setState(true);
                    }
                    else
                    {
                        int ed = ((Integer) n.getExtraData("Ed")).intValue();
                        this.edGrpBox[ed].setState(true);
                    }
                }
            }
            return;
        }
    }

    public void removeLabels()
    {
        m_EOApp.removeLabels("girard/sc/be/awt/besmiw.txt");
    }
}
