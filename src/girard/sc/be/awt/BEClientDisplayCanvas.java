package girard.sc.be.awt;

/* This class allows one to display an Image into a Canvas for adding into a layout */

import girard.sc.be.obj.BEEdge;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.exnet.obj.NetworkBuilder;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;

public class BEClientDisplayCanvas extends Canvas implements MouseListener,MouseMotionListener
    {
    BENetworkActionClientWindow m_CWApp;
    Point m_topLeft = new Point(0,0);
    Point m_bottomRight = new Point (NetworkBuilder.WIDTH,NetworkBuilder.HEIGHT);
    BENetwork m_network;

    // m_mouseState = -1;

    public BEClientDisplayCanvas(BENetworkActionClientWindow cw, BENetwork net)
        {
        m_CWApp = cw;
        m_network = net;

        addMouseListener(this);
        addMouseMotionListener(this);
        }
    public BEClientDisplayCanvas(BENetworkActionClientWindow cw, BENetwork net, Point tl, Point br)
        {
        m_CWApp = cw;
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
        BENode me = (BENode)m_network.getExtraData("Me");
        Enumeration enm = m_network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode n = (BENode)enm.nextElement();
            String str = (String)n.getExtraData("Type");
            if (str.equals("Neighbor"))
                {
                int x = ((Integer)n.getExtraData("XLoc")).intValue();
                int y = ((Integer)n.getExtraData("YLoc")).intValue();
                if ((e.getX() > x - 3) && (e.getX() < x+12) && (e.getY() > y - 11) && (e.getY() < y + 4))
                    {
                    m_CWApp.getArrow().setToNode(n);
                    Enumeration enum2 = m_network.getEdgeList().elements();
                    while (enum2.hasMoreElements())
                        {
                        BEEdge edge = (BEEdge)enum2.nextElement();
                        if ((edge.getNode1() == n.getID()) && (edge.getNode2() == me.getID()))
                            {
                            m_CWApp.getArrow().setEdge(edge);
                            m_CWApp.repaint();
                            break;
                            }
                        if ((edge.getNode2() == n.getID()) && (edge.getNode1() == me.getID()))
                            {
                            m_CWApp.getArrow().setEdge(edge);
                            m_CWApp.repaint();
                            break;
                            }
                        }
                    break;
                    }
                }
            }
        }


    public void paint(Graphics g)
        {
        Dimension dim = this.getSize();
        Image img = this.createImage(dim.width,dim.height);
        Graphics g2 = img.getGraphics();
        Enumeration enm = m_network.getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge e = (BEEdge)enm.nextElement();
            e.updateClientImage(g2,dim,m_topLeft,m_bottomRight,m_network,m_CWApp);
            }

        enm = m_network.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode n = (BENode)enm.nextElement();
            n.updateClientImage(g2,dim,m_topLeft,m_bottomRight,m_network,m_CWApp);
            }

        if (img != null)
            {
            g.drawImage(img,0,0,null);
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