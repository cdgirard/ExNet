package girard.sc.ce.awt;

import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.exnet.obj.NetworkBuilder;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Enumeration;

/**
 * Displays the network for the format CENetworkAction Window.
 * <p>
 * <br> Started: 01-21-2003
 * <p>
 * @author Dudley Girard
 */

public class CENetworkActionCanvas extends Canvas
    {
    ExptOverlord m_EOApp;
    CENetwork m_CNApp;
    Dimension m_ECdim = new Dimension();
    
    public CENetworkActionCanvas(ExptOverlord app1, CENetwork app2)
        {
        m_EOApp = app1;
        m_CNApp = app2;
        m_ECdim.width = NetworkBuilder.WIDTH;
        m_ECdim.height = NetworkBuilder.HEIGHT; 
        setSize(m_ECdim.width,m_ECdim.height);
        }

    public void paint(Graphics g)
        {
        Font f1 = m_EOApp.getMedLabelFont();
        Font f2 = m_EOApp.getTinyLabelFont();
        CENode Ntemp;
        CEEdge Etemp;
        String str = new String();
        Integer val;
        int i, j;
        int num_dotsx, num_dotsy;
        int xpos, ypos;

        g.setColor(CEColor.ACTIVE_EDGE);
  
        if (m_CNApp.getNumNodes() > 0)
            {
            Enumeration e = m_CNApp.getNodeList().elements();
            while (e.hasMoreElements())
                {
                Ntemp = (CENode)e.nextElement();
                g.fillOval(Ntemp.getXpos(),Ntemp.getYpos(),12,12);
                g.setFont(f1);
                g.drawString(str.valueOf(Ntemp.getLabel()),Ntemp.getXpos()-8,Ntemp.getYpos()-5);
                g.setFont(f2);
                val = new Integer(Ntemp.getID());
                xpos = Ntemp.getXpos()+ Ntemp.getLabel().length()*11 - 6;
                g.drawString(val.toString(),xpos,Ntemp.getYpos()-15);
                }
            }

        if (m_CNApp.getNumEdges() > 0)
            {
            Enumeration e = m_CNApp.getEdgeList().elements();
            while (e.hasMoreElements())
                {
                Etemp = (CEEdge)e.nextElement();
                g.drawLine(Etemp.getN1Anchor().x,Etemp.getN1Anchor().y,Etemp.getN2Anchor().x,Etemp.getN2Anchor().y);
                }
            }
        }
    }