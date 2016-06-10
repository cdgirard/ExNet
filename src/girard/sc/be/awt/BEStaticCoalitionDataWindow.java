package girard.sc.be.awt;

import girard.sc.awt.JGridBagPanel;
import girard.sc.be.obj.BECoalNEOutputObject;
import girard.sc.be.obj.BENetworkAction;
import girard.sc.be.obj.BEStaticOffOutputObject;
import girard.sc.be.obj.BEStaticVJOutputObject;
import girard.sc.be.obj.BEStaticVZOutputObject;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.obj.DataOutputObject;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Used to display the static coalition data from a BENetworkAction to an experimenter.
 * <p>
 * <br> Started: 08-05-2003
 * <p>
 * @author Dudley Girard
 */

public class BEStaticCoalitionDataWindow extends JFrame implements ActionListener
    {
    BENetworkActionDataDisplay m_DDApp;
    BENetworkAction m_NApp;
    ExptOverlord m_EOApp;
    BaseDataInfo m_bdi;
    
    MenuBar m_menuBar = new MenuBar();
    Menu m_fileMenu, m_helpMenu;

    public BEStaticCoalitionDataWindow(BENetworkAction app1, ExptOverlord app2, BENetworkActionDataDisplay app3, BaseDataInfo bdi)
        {
        super();
        m_NApp = app1;
        m_EOApp =app2;
        m_DDApp = app3;
        m_bdi = bdi;

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("bescdw_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());
        getContentPane().setBackground(m_EOApp.getWinBkgColor());

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_menuBar);

     // Setup Menu options
        MenuItem tmpItem;

    // File Menu
        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("bescdw_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("bescdw_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

   // Help Menu
        m_helpMenu = new Menu(m_EOApp.getLabels().getObjectLabel("bescdw_help"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("bescdw_help"));
        tmpItem.addActionListener(this);
        m_helpMenu.add(tmpItem);

        m_menuBar.add(m_helpMenu);
        
     // End setup for Menu options

     // Setup the Central Panel
        JTabbedPane centralPane = new JTabbedPane();

        centralPane.addTab("Join Data",createVoteJoinDataPanel());
        centralPane.addTab("Offer Data",createOfferDataPanel());
        centralPane.addTab("Zap Data",createZapDataPanel());
        centralPane.addTab("Earnings Data",createEarningsDataPanel());

        centralPane.setSelectedIndex(3);

        getContentPane().add("Center",centralPane);
        pack();
        show();
 
        // centralPane.setSelectedIndex(0);
        // centralPane.validate();
        }

    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bescdw_exit")))
                {
                m_DDApp.setEditMode(false);
                this.dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bescdw_help")))
                {
                m_EOApp.helpWindow("ehlp_bescdw");
                return;
                }
            }
        }

    public String buildColumnEntry(String str, int width)
        {
        int x, k, m;
        StringBuffer entry = new StringBuffer("");

        if (width > str.length())
            {
            entry.append(str);
            m = width - str.length();
            for (k=0;k<m;k++)
                {
                entry.append(" ");    
                }
            }
        else
            entry.append(str.substring(0,width));

        return entry.toString();
        }
    public String buildNEEntry(int[] columnWidths, BECoalNEOutputObject obj)
        {
        StringBuffer entry = new StringBuffer("");
        String[] entries = new String[columnWidths.length];

        entries[0] = new String(""+obj.getExptID());
        entries[1] = new String(""+obj.getActionIndex());
        entries[2] = new String(""+obj.getPeriod());
        entries[3] = new String(""+obj.getRound());
        entries[4] = new String(""+obj.getNode());
        entries[5] = new String(""+obj.getCoalition());
        entries[6] = new String(""+obj.getTotalEarnings());

        for (int x=0;x<columnWidths.length;x++)
            {
            entry.append(buildColumnEntry(entries[x],columnWidths[x]));
            }

        return entry.toString();
        }
    public String buildOffEntry(int[] columnWidths, BEStaticOffOutputObject obj)
        {
        StringBuffer entry = new StringBuffer("");
        String[] entries = new String[columnWidths.length];

        entries[0] = new String(""+obj.getExptID());
        entries[1] = new String(""+obj.getActionIndex());
        entries[2] = new String(""+obj.getPeriod());
        entries[3] = new String(""+obj.getRound());
        entries[4] = new String(""+obj.getNode());
        entries[5] = new String(""+obj.getCoalition());
        entries[6] = new String(""+obj.getOffer());

        for (int x=0;x<columnWidths.length;x++)
            {
            entry.append(buildColumnEntry(entries[x],columnWidths[x]));
            }

        return entry.toString();
        }
    public String buildVJEntry(int[] columnWidths, BEStaticVJOutputObject obj)
        {
        StringBuffer entry = new StringBuffer("");
        String[] entries = new String[columnWidths.length];

        entries[0] = new String(""+obj.getExptID());
        entries[1] = new String(""+obj.getActionIndex());
        entries[2] = new String(""+obj.getPeriod());
        entries[3] = new String(""+obj.getRound());
        entries[4] = new String(""+obj.getNode());
        entries[5] = new String(""+obj.getCoalition());
        entries[6] = new String(""+obj.getVote());

        for (int x=0;x<columnWidths.length;x++)
            {
            entry.append(buildColumnEntry(entries[x],columnWidths[x]));
            }

        return entry.toString();
        }
    public String buildVZEntry(int[] columnWidths, BEStaticVZOutputObject obj)
        {
        StringBuffer entry = new StringBuffer("");
        String[] entries = new String[columnWidths.length];

        entries[0] = new String(""+obj.getExptID());
        entries[1] = new String(""+obj.getActionIndex());
        entries[2] = new String(""+obj.getPeriod());
        entries[3] = new String(""+obj.getRound());
        entries[4] = new String(""+obj.getNode());
        entries[5] = new String(""+obj.getCoalition());
        entries[6] = new String(""+obj.getVote());

        for (int x=0;x<columnWidths.length;x++)
            {
            entry.append(buildColumnEntry(entries[x],columnWidths[x]));
            }

        return entry.toString();
        }

    public JGridBagPanel createEarningsDataPanel()
        {
        int[] columnWidths = {10, 7, 7, 6, 6, 11, 10};
        String[] headings = {"ID", "Action", "Period", "Round", "Node", "Coalition", "Earnings"};
        JGridBagPanel enPanel = new JGridBagPanel();

        TextArea tmpText = new TextArea(20,80);
        tmpText.setFont(m_EOApp.getMedWinFont());
        tmpText.setEditable(false);

        Hashtable data = m_bdi.getActionData();
        Hashtable edgeData = (Hashtable)data.get("Node Data");
        Hashtable offerData = (Hashtable)edgeData.get("BENodeExchange");
        Vector votes = (Vector)offerData.get("NE");

        StringBuffer str = new StringBuffer("");
        
        str.append(getHeadings(headings,columnWidths)+"\n");

        Enumeration enm = votes.elements();
        while (enm.hasMoreElements())
            {
            DataOutputObject doo = (DataOutputObject)enm.nextElement();
            if (doo instanceof BECoalNEOutputObject)
                {
                BECoalNEOutputObject tmp = (BECoalNEOutputObject)doo;
                str.append(buildNEEntry(columnWidths,tmp)+"\n");
                }
            }

        tmpText.setText(str.toString());

        enPanel.constrain(tmpText,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

        return enPanel;
        }
    public JGridBagPanel createOfferDataPanel()
        {
        int[] columnWidths = {10, 7, 7, 6, 6, 11, 7};
        String[] headings = {"ID", "Action", "Period", "Round", "Node", "Coalition", "Offer"};
        JGridBagPanel offPanel = new JGridBagPanel();

        TextArea tmpText = new TextArea(20,80);
        tmpText.setFont(m_EOApp.getMedWinFont());
        tmpText.setEditable(false);

        Hashtable data = m_bdi.getActionData();
        Hashtable edgeData = (Hashtable)data.get("Node Data");
        Hashtable offerData = (Hashtable)edgeData.get("BENodeExchange");
        Vector votes = (Vector)offerData.get("Off");

        StringBuffer str = new StringBuffer("");
        
        str.append(getHeadings(headings,columnWidths)+"\n");

        Enumeration enm = votes.elements();
        while (enm.hasMoreElements())
            {
            DataOutputObject doo = (DataOutputObject)enm.nextElement();
            if (doo instanceof BEStaticOffOutputObject)
                {
                BEStaticOffOutputObject tmp = (BEStaticOffOutputObject)doo;
                str.append(buildOffEntry(columnWidths,tmp)+"\n");
                }
            }

        tmpText.setText(str.toString());

        offPanel.constrain(tmpText,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

        return offPanel;
        }
    public JGridBagPanel createVoteJoinDataPanel()
        {
        int[] columnWidths = {10, 7, 7, 6, 6, 11, 6};
        String[] headings = {"ID", "Action", "Period", "Round", "Node", "Coalition", "Vote"};
        JGridBagPanel vjPanel = new JGridBagPanel();

        TextArea tmpText = new TextArea(20,80);
        tmpText.setFont(m_EOApp.getMedWinFont());
        tmpText.setEditable(false);

        Hashtable data = m_bdi.getActionData();
        Hashtable edgeData = (Hashtable)data.get("Node Data");
        Hashtable offerData = (Hashtable)edgeData.get("BENodeExchange");
        Vector votes = (Vector)offerData.get("VJ");

        StringBuffer str = new StringBuffer("");
        
        str.append(getHeadings(headings,columnWidths)+"\n");

        Enumeration enm = votes.elements();
        while (enm.hasMoreElements())
            {
            DataOutputObject doo = (DataOutputObject)enm.nextElement();
            if (doo instanceof BEStaticVJOutputObject)
                {
                BEStaticVJOutputObject tmp = (BEStaticVJOutputObject)doo;
                str.append(buildVJEntry(columnWidths,tmp)+"\n");
                }
            }

        tmpText.setText(str.toString());

        vjPanel.constrain(tmpText,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

        return vjPanel;
        }
    public JGridBagPanel createZapDataPanel()
        {
        int[] columnWidths = {10, 7, 7, 6, 6, 11, 6};
        String[] headings = {"ID", "Action", "Period", "Round", "Node", "Coalition", "Zap"};
        JGridBagPanel vzPanel = new JGridBagPanel();

        TextArea tmpText = new TextArea(20,80);
        tmpText.setFont(m_EOApp.getMedWinFont());
        tmpText.setEditable(false);

        Hashtable data = m_bdi.getActionData();
        Hashtable edgeData = (Hashtable)data.get("Node Data");
        Hashtable offerData = (Hashtable)edgeData.get("BENodeExchange");
        Vector votes = (Vector)offerData.get("VZ");

        StringBuffer str = new StringBuffer("");
        
        str.append(getHeadings(headings,columnWidths)+"\n");

        Enumeration enm = votes.elements();
        while (enm.hasMoreElements())
            {
            DataOutputObject doo = (DataOutputObject)enm.nextElement();
            if (doo instanceof BEStaticVZOutputObject)
                {
                BEStaticVZOutputObject tmp = (BEStaticVZOutputObject)doo;
                str.append(buildVZEntry(columnWidths,tmp)+"\n");
                }
            }

        tmpText.setText(str.toString());

        vzPanel.constrain(tmpText,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH);

        return vzPanel;
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public String getHeadings(String[] headings, int[] columnWidths)
        {
        StringBuffer heading = new StringBuffer("");

        for (int x=0;x<columnWidths.length;x++)
            {
            heading.append(buildColumnEntry(headings[x],columnWidths[x]));
            }

        return heading.toString();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/bescdw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bescdw.txt");
        }
    }
