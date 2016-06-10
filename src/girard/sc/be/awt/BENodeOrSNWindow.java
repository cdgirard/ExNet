package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.awt.ImageCanvas;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BERRThread;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.TextArea;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class BENodeOrSNWindow extends Frame
    {
    BENetworkActionClientWindow m_NACWApp;
    BERRThread m_watcher;  // Round Running thread, that will clean up the window at the end of the round.
    ExptOverlord m_EOApp;

    TextArea m_snInfoArea = new TextArea(20,60);

    public BENodeOrSNWindow(BENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();
   
    /***************************************************************
     Display Title, Minimum Number of Exchanges, and Maximum Number of
     Exchanges allowed.
   *****************************************************************/

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("benosnw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel MainPanel = new GridBagPanel();

     // Create color code display.

        Image img = m_EOApp.createImage(300,100);
        Graphics g = img.getGraphics();

        g.setColor(BEColor.white);
        g.fillRect(0,0,300,100);

        g.setFont(m_EOApp.getMedWinFont());

        g.setColor(BEColor.INACTIVE_EDGE);
        g.fillOval(260,4,12,12);
        g.setColor(BEColor.black);
        g.drawString(m_EOApp.getLabels().getObjectLabel("benosnw_iac"),2,15);

        g.setColor(BEColor.INCLUSIVE);
        g.fillOval(260,24,12,12);
        g.setColor(BEColor.black);
        g.drawString(m_EOApp.getLabels().getObjectLabel("benosnw_ic"),2,35);

        g.setColor(BEColor.EXCLUSIVE);
        g.fillOval(260,44,12,12);
        g.setColor(BEColor.black);
        g.drawString(m_EOApp.getLabels().getObjectLabel("benosnw_ec"),2,55);

        g.setColor(BEColor.INCLUSIVE_EXCLUSIVE);
        g.fillOval(260,64,12,12);
        g.setColor(BEColor.black);
        g.drawString(m_EOApp.getLabels().getObjectLabel("benosnw_iec"),2,75);

        g.setColor(BEColor.NULL);
        g.fillOval(260,84,12,12);
        g.setColor(BEColor.black);
        g.drawString(m_EOApp.getLabels().getObjectLabel("benosnw_nc"),2,95);

        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(new ImageCanvas(img));

        MainPanel.constrain(tmpPanel,1,1,20,10);
     // End create color code display.

        MainPanel.constrain(m_snInfoArea,1,11,20,10);

        StringBuffer str = new StringBuffer("");

        Enumeration enm = m_NACWApp.getNetwork().getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode node = (BENode)enm.nextElement();
            BENodeOrSubNet ns = (BENodeOrSubNet)node.getExptData("BENodeExchange");
            Hashtable data = ns.getSettings();
            
            str.append(m_EOApp.getLabels().getObjectLabel("benosnw_node")+" "+node.getLabel()+" ");
            str.append(m_EOApp.getLabels().getObjectLabel("benosnw_max")+" "+(Integer)data.get("Max")+" ");
            str.append(m_EOApp.getLabels().getObjectLabel("benosnw_min")+" "+(Integer)data.get("Min"));
            
            Vector subnetworks = (Vector)data.get("Subnetworks");
            Enumeration enum2 = subnetworks.elements();
            while (enum2.hasMoreElements())
                {
                Hashtable sdata = (Hashtable)enum2.nextElement();
                str.append(fillInSubnetworkInfo(sdata,"   "));
                str.append("\n");
                }
            str.append("---------------------------------------\n");
            }
        m_snInfoArea.setText(str.toString());

        add(MainPanel);
        pack();
        show();

        m_watcher = new BERRThread(m_NACWApp,this);
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public StringBuffer fillInSubnetworkInfo(Hashtable data,String tabSpace)
        {
        StringBuffer str = new StringBuffer();

        str.append("\n"+tabSpace+m_EOApp.getLabels().getObjectLabel("benosnw_subnetwork")+" "+(Integer)data.get("Subnetwork")+" ");
        
        str.append(" [ "+m_EOApp.getLabels().getObjectLabel("benosnw_nodes"));
        
        int[] nodes = (int[])data.get("Nodes");
        for (int i=0;i<nodes.length;i++)
            {
            BENode n1 = (BENode)m_NACWApp.getNetwork().getNode(nodes[i]);
            str.append(n1.getLabel()+" ");
            }
        str.append("]  ");
        if (data.get("Subnetworks") instanceof Vector)
            {
            str.append(m_EOApp.getLabels().getObjectLabel("benosnw_max")+" "+(Integer)data.get("Max")+"  ");
            str.append(m_EOApp.getLabels().getObjectLabel("benosnw_min")+" "+(Integer)data.get("Min"));
            }
        if (data.get("Subnetworks") instanceof Vector)
            {
            Vector subnetworks = (Vector)data.get("Subnetworks");
            Enumeration enm = subnetworks.elements();
            while (enm.hasMoreElements())
                {
                Hashtable sdata = (Hashtable)enm.nextElement();
                str.append(fillInSubnetworkInfo(sdata,tabSpace+"   "));
                }
            }

        return str;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/benosnw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/benosnw.txt");
        }
    }
