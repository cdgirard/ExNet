package girard.sc.be.awt;

import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.exnet.obj.NetworkBuilder;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Enumeration;

public class BENetworkActionCanvas extends Canvas
    {
    ExptOverlord m_EOApp;
    BENetwork m_BNApp;
    Dimension m_ECdim = new Dimension();
    
    public BENetworkActionCanvas(ExptOverlord app1, BENetwork app2)
        {
        m_EOApp = app1;
        m_BNApp = app2;
        m_ECdim.width = NetworkBuilder.WIDTH;
        m_ECdim.height = NetworkBuilder.HEIGHT; 
        setSize(m_ECdim.width,m_ECdim.height);
        }

    public void paint(Graphics g)
        {
        Font f1 = m_EOApp.getMedLabelFont();
        Font f2 = m_EOApp.getTinyLabelFont();
        BENode Ntemp;
        BEEdge Etemp;
        String str = new String();
        Integer val;
        int i, j;
        int num_dotsx, num_dotsy;
        int xpos, ypos;

        g.setColor(BEColor.ACTIVE_EDGE);
  
        if (m_BNApp.getNumNodes() > 0)
            {
            Enumeration e = m_BNApp.getNodeList().elements();
            while (e.hasMoreElements())
                {
                Ntemp = (BENode)e.nextElement();
                g.fillOval(Ntemp.getXpos(),Ntemp.getYpos(),12,12);
                g.setFont(f1);
                g.drawString(str.valueOf(Ntemp.getLabel()),Ntemp.getXpos()-8,Ntemp.getYpos()-5);
                g.setFont(f2);
                val = new Integer(Ntemp.getID());
                xpos = Ntemp.getXpos()+ Ntemp.getLabel().length()*11 - 6;
                g.drawString(val.toString(),xpos,Ntemp.getYpos()-15);
                }
            }

        if (m_BNApp.getNumEdges() > 0)
            {
            Enumeration e = m_BNApp.getEdgeList().elements();
            while (e.hasMoreElements())
                {
                Etemp = (BEEdge)e.nextElement();
                g.drawLine(Etemp.getN1Anchor().x,Etemp.getN1Anchor().y,Etemp.getN2Anchor().x,Etemp.getN2Anchor().y);
                }
            }
        }
    }
