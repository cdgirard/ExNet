package girard.sc.ce.awt;

import girard.sc.ce.obj.CEEndRoundResOutputObject;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENetworkAction;
import girard.sc.ce.obj.CENode;
import girard.sc.ce.obj.CENodeResource;
import girard.sc.ce.obj.CEOfferOutputObject;
import girard.sc.ce.obj.CEResource;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.obj.DataOutputObject;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to display the resource data from a CENetworkAction to an experimenter.
 * <p>
 * <br> Started: 02-26-2003
 * <p>
 * @author Dudley Girard
 */

public class CEViewResourceDataWindow extends Frame implements ActionListener
    {
    CENetworkActionDataDisplay m_DDApp;
    CENetworkAction m_NApp;
    ExptOverlord m_EOApp;
    BaseDataInfo m_bdi;
    
    MenuBar m_menuBar = new MenuBar();
    Menu m_fileMenu, m_helpMenu;

    public CEViewResourceDataWindow(CENetworkAction app1, ExptOverlord app2, CENetworkActionDataDisplay app3, BaseDataInfo bdi)
        {
        super();
        m_NApp = app1;
        m_EOApp =app2;
        m_DDApp = app3;
        m_bdi = bdi;

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("cevrdw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_menuBar);

     // Setup Menu options
        MenuItem tmpItem;

    // File Menu
        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("cevrdw_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("cevrdw_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

   // Help Menu
        m_helpMenu = new Menu(m_EOApp.getLabels().getObjectLabel("cevrdw_help"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("cevrdw_help"));
        tmpItem.addActionListener(this);
        m_helpMenu.add(tmpItem);

        m_menuBar.add(m_helpMenu);
        
     // End setup for Menu options

        Panel MainPanel = new Panel(new GridLayout(1,1));

        TextArea tmpText = new TextArea(20,80);
        tmpText.setEditable(false);

        Hashtable data = m_bdi.getActionData();

	System.err.println("Printing teh data of m_bdi actionData:CEViewResourceDataWindow");
       	Enumeration enum1 = data.keys();
        while(enum1.hasMoreElements()){
	    String s = (String)enum1.nextElement();
	    System.err.println("printing Hashtable with the key:"+s);
	    Hashtable ht = (Hashtable)data.get(s);
	    Enumeration enum2 = ht.keys();
	    while(enum2.hasMoreElements()){
		String elem = (String)enum2.nextElement();
		System.err.println(elem);
	    }
	    System.err.println();
        }

        Hashtable edgeData = (Hashtable)data.get("Node Data");
        Hashtable resData = (Hashtable)edgeData.get("CENodeResource");
	Enumeration enum2 = resData.keys();
	System.out.println("printing the resData");
	while(enum2.hasMoreElements()){
	    System.out.println((String)enum2.nextElement());
	}
        Vector resources = (Vector)resData.get("Resource Data");
        
        StringBuffer str = new StringBuffer("");
        str.append(getHeadings()+"\n");

        Enumeration enm = resources.elements();
        while (enm.hasMoreElements())
            {
            DataOutputObject doo = (DataOutputObject)enm.nextElement();
	    if(doo instanceof CEOfferOutputObject)
		System.err.println("woohoo...found teh offer output at the same place");
	    if (doo instanceof CEEndRoundResOutputObject)
                {
                CEEndRoundResOutputObject tmp = (CEEndRoundResOutputObject)doo;
                str.append(buildOfferEntry(tmp)+"\n");
                }
            }

        tmpText.setText(str.toString());

        MainPanel.add(tmpText);

        add(MainPanel);
        pack();
        show();
        }

    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cevrdw_exit")))
                {
                m_DDApp.setEditMode(false);
                this.dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("cevrdw_help")))
                {
                m_EOApp.helpWindow("ehlp_cevrdw");
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
    public String buildOfferEntry(CEEndRoundResOutputObject obj)
        {
        StringBuffer entry = new StringBuffer("");
        int[] columnWidths = {10, 7, 7, 6, 5, 5, 5, 5};
        String[] entries = new String[columnWidths.length];

        entries[0] = new String(""+obj.getExptID());
        entries[1] = new String(""+obj.getActionIndex());
        entries[2] = new String(""+obj.getPeriod());
        entries[3] = new String(""+obj.getRound());
        entries[4] = new String(""+obj.getNode());
        entries[5] = new String(""+obj.getRes());
        entries[6] = new String(""+(int)obj.getResAmt());

        CENetwork cen = (CENetwork)m_NApp.getAction();
        CENode node = (CENode)cen.getNode(obj.getNode());
        CENodeResource nr = (CENodeResource)node.getExptData("CENodeResource");
        CEResource cer = nr.getInitialResources(obj.getRes());
        entries[7] = new String(""+cer.getIntValue());

        for (int x=0;x<columnWidths.length;x++)
            {
            entry.append(buildColumnEntry(entries[x],columnWidths[x]));
            }

        return entry.toString();
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public String getHeadings()
        {
        int[] columnWidths = {10, 7, 7, 6, 5, 5, 5, 5};
        String[] headings = {"ID", "Action", "Period", "Round", "Node", "Res", "Amt", "Val"};
        StringBuffer heading = new StringBuffer("");

        for (int x=0;x<columnWidths.length;x++)
            {
            heading.append(buildColumnEntry(headings[x],columnWidths[x]));
            }

        return heading.toString();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ce/awt/cevrdw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cevrdw.txt");
        }
    }
