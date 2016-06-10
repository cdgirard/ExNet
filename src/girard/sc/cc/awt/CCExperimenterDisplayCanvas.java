package girard.sc.cc.awt;

/* This is the main display area for the Experimenters screen for a CCNetworkAction.

   Author: Dudley Girard
   Started: 7-25-2001
 */

import girard.sc.cc.obj.CCEdge;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.exnet.obj.NetworkBuilder;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.Enumeration;

public class CCExperimenterDisplayCanvas extends Canvas
    {
    Point m_topLeft = new Point(0,0);
    Point m_bottomRight = new Point (NetworkBuilder.WIDTH,NetworkBuilder.HEIGHT);
    CCNetwork m_network;

    public CCExperimenterDisplayCanvas(CCNetwork net)
        {
        m_network = net;
        }
    public CCExperimenterDisplayCanvas(CCNetwork net,Point tl, Point br)
        {
        m_network = net;
        m_topLeft = tl;
        m_bottomRight = br;
        }

    public void changeViewingArea(Point tl, Point br)
        {
        m_topLeft.x = tl.x;
        m_topLeft.y = tl.y;

        m_bottomRight.x = br.x;
        m_bottomRight.y = br.y;

        repaint();
        }

    public void paint(Graphics g)
        {
        Dimension dim = this.getSize();
        Image img = this.createImage(dim.width,dim.height);
        Graphics g2 = img.getGraphics();
        Enumeration enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CCEdge e = (CCEdge)enm.nextElement();
            e.updateExperimenterImage(g2,dim,m_topLeft,m_bottomRight);
            }

        enm = m_network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode n = (CCNode)enm.nextElement();
            n.updateExperimenterImage(g2,dim,m_topLeft,m_bottomRight);
            }

        if (img != null)
            {
            g.drawImage(img,0,0,this);
            }
        }

    public void setBottomRight(int x, int y)
        {
        m_bottomRight.x = x;
        m_bottomRight.y = y;
        }
    public void setTopLeft(int x, int y)
        {
        m_topLeft.x = x;
        m_topLeft.y = y;
        }

    public void update(Graphics g)
        {
        paint(g);
        }
    }