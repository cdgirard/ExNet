package girard.sc.exnet.awt;

import girard.sc.exnet.obj.Edge;
import girard.sc.exnet.obj.ExnetColor;
import girard.sc.exnet.obj.NetworkBuilder;
import girard.sc.exnet.obj.Node;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;

public class NetworkCanvas extends Canvas implements MouseListener,MouseMotionListener
    {
    NetworkBuilder m_NBApp;
    Node m_NT1,m_NT2;
    int m_numPts = 0;
    int[] m_x = new int[2];
    int[] m_y = new int[2];
    int m_ActiveTool;
    boolean m_EditMode = false;
    Dimension m_ECdim = new Dimension();
    
    public NetworkCanvas(int width, int height, NetworkBuilder app)
        {
        m_NBApp = app;
        m_ECdim.width = NetworkBuilder.WIDTH;
        m_ECdim.height = NetworkBuilder.HEIGHT; 
        setBackground(m_NBApp.getEO().getDispBkgColor());
        setSize(width,height);
        addMouseListener(this);
        addMouseMotionListener(this);
        }

    public boolean getEditMode()
        {
        return m_EditMode;
        }

    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}


    public void mousePressed(MouseEvent e) 
        {
        boolean flag;
        Node Ntemp, Ntemp2; 
        Edge Etemp, Etemp2;
        //Handle Actions Here

        if (getEditMode())
            return;

/*  if last button pushed was Add Node, draw the node  */
        if ( m_ActiveTool == 1 )
            {
            m_numPts = 0;
            /*  get the coords of the button press, and draw a node  */
            m_x[0] = e.getX();
            m_y[0] = e.getY();
            m_x[0] = m_x[0]/75;
            m_x[0] = (m_x[0]*75);
            m_y[0] = m_y[0]/75;
            m_y[0] = (m_y[0]*75);
            flag = true;
            Enumeration enm = m_NBApp.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                Ntemp = (Node)enm.nextElement();

                /* See if within area for node Ntemp */
                if (( m_x[0] >= ( Ntemp.getXpos() - 10 )) &&  ( m_x[0] <= ( Ntemp.getXpos() + 10 )) &&  ( m_y[0] >= ( Ntemp.getYpos() - 10 ))
                       &&  ( m_y[0] <= ( Ntemp.getYpos() + 10 )))
                    {
                    flag = false;
                    break;
                    }
                }
            if (flag)
                {
                if ((m_x[0] < (m_ECdim.width - 50)) && (m_x[0] > 50) && (m_y[0] < (m_ECdim.height - 50)) && (m_y[0] > 50))
                    {
                    setEditMode(true);
                    new AddNodeWindow(m_x[0],m_y[0],m_NBApp,this);
                    }
                }
            }
/*  if last button pushed was Add Edge  */
        if ((m_ActiveTool == 2) && (m_NBApp.getNumNodes() > 1))
            {
            flag = false;
            m_x[m_numPts] = e.getX();
            m_y[m_numPts] = e.getY();
            Enumeration enm = m_NBApp.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                Ntemp = (Node)enm.nextElement();
                /* See if within area for node Ntemp */
                if (( m_x[m_numPts] >= ( Ntemp.getXpos() - 10 )) &&  ( m_x[m_numPts] <= ( Ntemp.getXpos() + 10 ))
                      &&  ( m_y[m_numPts] >= ( Ntemp.getYpos() - 10 )) &&  ( m_y[m_numPts] <= ( Ntemp.getYpos() + 10 )))
                    {
                    m_x[m_numPts] = Ntemp.getXpos() + 6;
                    m_y[m_numPts] = Ntemp.getYpos() + 6;
                    if (m_numPts == 0)
                        {
                        m_NT1 = Ntemp;
                        m_numPts++;
                        break;
                        }
                    else
                        {
                        if (m_NT1 != Ntemp) /* Make sure end node not start node */
                            {
                            m_NT2 = Ntemp;
                            m_numPts++;
                            break;
                            }
                        }
                    }
                if (!enm.hasMoreElements())
                    flag = true;
                }
/* If didn't click within any of the nodes reset all, must start over */
            if (flag)
                {
                m_numPts = 0;
                return;
                }
            if (m_numPts == 2)
                {
                if (LegalEdge())
                    {
                    m_NBApp.addEdge(new Edge(m_NT1.getID(),m_NT2.getID(),new Point(m_x[0],m_y[0]),new Point(m_x[1],m_y[1])));
                    repaint();
                    }
                m_numPts = 0;
                }
            }
/* If last button pushed was delete node */
        if ((m_ActiveTool == 3) && (m_NBApp.getNumNodes() > 0))
            {
            m_x[0] = e.getX();
            m_y[0] = e.getY();
            Enumeration enm = m_NBApp.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                Ntemp = (Node)enm.nextElement();
/* See if within area for node Ntemp */
                if (( m_x[0] >= ( Ntemp.getXpos() - 10 )) &&  ( m_x[0] <= ( Ntemp.getXpos() + 10 ))
                      &&  ( m_y[0] >= ( Ntemp.getYpos() - 10 )) &&  ( m_y[0] <= ( Ntemp.getYpos() + 10 )))
                    {
                    m_NBApp.removeNode(Ntemp);
                    
                    repaint();
                    return;
                    }
                }
            }

//  if last button pushed was Delete Edge  
        if ((m_ActiveTool == 4) && (m_NBApp.getNumEdges() > 0))
            {
            m_x[0] = e.getX();
            m_y[0] = e.getY();
            Enumeration enm = m_NBApp.getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                Etemp = (Edge)enm.nextElement();
/* See if within area for Edge Etemp */
                if (( m_x[0] >= ( Etemp.getMidAnchor().x - 10 )) &&  ( m_x[0] <= ( Etemp.getMidAnchor().x + 10 ))
                      &&  ( m_y[0] >= ( Etemp.getMidAnchor().y - 10 )) &&  ( m_y[0] <= ( Etemp.getMidAnchor().y + 10 )))
                    {
                    m_NBApp.removeEdge(Etemp);

                    repaint();
                    return;
                    }
                }
            }       
        }

    public void paint(Graphics g)
        {
        Font f1 = m_NBApp.getEO().getMedLabelFont();
        Font f2 = m_NBApp.getEO().getTinyLabelFont();
        Node Ntemp;
        Edge Etemp;
        String str = new String();
        Integer val;
        int i, j;
        int num_dotsx, num_dotsy;
        int xpos, ypos;

        g.setColor(ExnetColor.ACTIVE_EDGE);

        num_dotsx = m_ECdim.width/50;
        num_dotsy = m_ECdim.height/50;
        for (i=0;i<num_dotsx-1;i++)
            {
            for (j=0;j<num_dotsy-1;j++)
                {
                xpos = i*75+80;
                ypos = j*75+80;
                g.fillOval(xpos,ypos,3,3);
                }
            }
  
        if (m_NBApp.getNumNodes() > 0)
            {
            Enumeration e = m_NBApp.getNodeList().elements();
            while (e.hasMoreElements())
                {
                Ntemp = (Node)e.nextElement();
                g.fillOval(Ntemp.getXpos(),Ntemp.getYpos(),12,12);
                g.setFont(f1);
                g.drawString(str.valueOf(Ntemp.getLabel()),Ntemp.getXpos()-8,Ntemp.getYpos()-5);
                g.setFont(f2);
                val = new Integer(Ntemp.getID());
                xpos = Ntemp.getXpos()+ Ntemp.getLabel().length()*11 - 6;
                g.drawString(val.toString(),xpos,Ntemp.getYpos()-15);
                }
            }

        if (m_NBApp.getNumEdges() > 0)
            {
            Enumeration e = m_NBApp.getEdgeList().elements();
            while (e.hasMoreElements())
                {
                Etemp = (Edge)e.nextElement();
                g.drawLine(Etemp.getN1Anchor().x,Etemp.getN1Anchor().y,Etemp.getN2Anchor().x,Etemp.getN2Anchor().y);
                g.drawOval(Etemp.getMidAnchor().x,Etemp.getMidAnchor().y,5,5);
                }
            }
        }

    public void setActiveTool(int tool)
        {
        m_numPts = 0;
        m_ActiveTool = tool;
        }
    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }

    private boolean LegalEdge()
        {
        Edge Etemp;

        Enumeration e = m_NBApp.getEdgeList().elements();
        while (e.hasMoreElements())
            {
            Etemp = (Edge)e.nextElement();

            if ((Etemp.getNode1() == m_NT1.getID()) && (Etemp.getNode2() == m_NT2.getID()))
                return false;
            if ((Etemp.getNode1() == m_NT2.getID()) && (Etemp.getNode2() == m_NT1.getID()))
                return false;
            } 
        return true;
        }
    }