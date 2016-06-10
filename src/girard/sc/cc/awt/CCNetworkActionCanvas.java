package girard.sc.cc.awt;

/* Displays the network for the format CCNetworkAction Window.

   Author: Dudley Girard
   Started: 7-25-2001
*/

import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.exnet.obj.NetworkBuilder;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Enumeration;

public class CCNetworkActionCanvas extends Canvas
    {
    ExptOverlord m_EOApp;
    CCNetwork m_CNApp;
    Dimension m_ECdim = new Dimension();
    
    public CCNetworkActionCanvas(ExptOverlord app1, CCNetwork app2)
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
        CCNode Ntemp;
        CCEdge Etemp;
        String str = new String();
        Integer val;
        int i, j;
        int num_dotsx, num_dotsy;
        int xpos, ypos;

        g.setColor(CCColor.ACTIVE_EDGE);
  
        if (m_CNApp.getNumNodes() > 0)
            {
            Enumeration e = m_CNApp.getNodeList().elements();
            while (e.hasMoreElements())
                {
                Ntemp = (CCNode)e.nextElement();
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
                Etemp = (CCEdge)e.nextElement();
                g.drawLine(Etemp.getN1Anchor().x,Etemp.getN1Anchor().y,Etemp.getN2Anchor().x,Etemp.getN2Anchor().y);
                }
            }
        }
    }
