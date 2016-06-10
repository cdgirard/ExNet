package girard.sc.ce.awt;

import girard.sc.ce.obj.CEEdge;
import girard.sc.ce.obj.CEEdgeInteraction;
import girard.sc.ce.obj.CENetwork;
import girard.sc.ce.obj.CENode;
import girard.sc.exnet.obj.NetworkBuilder;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Enumeration;

/**
 * This is the main display area for the Experimenters screen for a CENetworkAction.
 * <p>
 * <br> Started: 01-30-2003
 * <br> Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 */

public class CEExperimenterDisplayCanvas extends Canvas implements MouseListener,MouseMotionListener
    {
    CENetworkActionExperimenterWindow m_CWApp;
    Point m_topLeft = new Point(0,0);
    Point m_bottomRight = new Point (NetworkBuilder.WIDTH,NetworkBuilder.HEIGHT);
    CENetwork m_network;

    public CEExperimenterDisplayCanvas(CENetworkActionExperimenterWindow ew, CENetwork net)
        {
        m_network = net;
        m_CWApp = ew;

        addMouseListener(this);
        addMouseMotionListener(this);
        }
    public CEExperimenterDisplayCanvas(CENetworkActionExperimenterWindow ew,CENetwork net,Point tl, Point br)
        {
        m_CWApp = ew;
        m_network = net;
        m_topLeft = tl;
        m_bottomRight = br;

        addMouseListener(this);
        addMouseMotionListener(this);
        }

    public void changeViewingArea(Point tl, Point br)
        {
        m_topLeft.x = tl.x;
        m_topLeft.y = tl.y;

        m_bottomRight.x = br.x;
        m_bottomRight.y = br.y;

        repaint();
        }

    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}

    public void mousePressed(MouseEvent e)
        {
        Enumeration enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge edge = (CEEdge)enm.nextElement();
            CEEdgeInteraction ei = (CEEdgeInteraction)edge.getExptData("CEEdgeInteraction");
            e.getX();
            e.getY();
            double x = e.getX() - ei.getButtonLoc().x;
            double y = e.getY() - ei.getButtonLoc().y;
            x = x*x;
            y = y*y;
            double dist = Math.sqrt(x+y);
            if (dist < ei.getDotSize())
                {
                Enumeration enum2 = m_network.getEdgeList().elements();
                while (enum2.hasMoreElements())
                    {
                    CEEdge edge2 = (CEEdge)enum2.nextElement();
                    CEEdgeInteraction ei2 = (CEEdgeInteraction)edge2.getExptData("CEEdgeInteraction");
                    ei2.setActive(-1);
                    }
                ei.setActive(1);
                m_CWApp.updateProfitDisplay(edge);
                m_CWApp.repaint();
                return;
                }
            } 
        }

    public void paint(Graphics g)
        {
        Dimension dim = this.getSize();
        BufferedImage img = new BufferedImage(dim.width,dim.height,BufferedImage.TYPE_3BYTE_BGR);
        Graphics g2 = img.getGraphics();
        ((Graphics2D)g2).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)g2).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.white);
        g2.fillRect(0,0,dim.width,dim.height);
        Enumeration enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            CEEdge e = (CEEdge)enm.nextElement();
            e.updateExperimenterImage(g2,dim,m_topLeft,m_bottomRight,m_CWApp);
            }

        enm = m_network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CENode n = (CENode)enm.nextElement();
            n.updateExperimenterImage(g2,dim,m_topLeft,m_bottomRight,m_CWApp);
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