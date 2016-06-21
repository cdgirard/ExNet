package girard.sc.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Extends the Canvas object class into a clickable button that can have an
 * image and text displayed on it.  Draws a border around the button that changes
 * when the button is clicked to give the impression of a button click.  It uses
 * both a MouseListener and MouseMotionListener to know if it has be clicked.
 * <p>
 * <br> Started: 1999
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1  
 */

public class GraphicButton extends Canvas implements MouseListener,MouseMotionListener
    {
/**
 * Used to keep track of any ActionListeners that may be attached to the GraphicButton.
 */
    Vector m_v = new Vector(1);
/**
 * Used to keep track of whether the mouse is inside or outside the boundaries of the
 * GraphicButton.
 */
    int m_GBMouseState;
/**
 * The preferred width and height of the button, which is slightly bigger than the
 * width and height of the Image to give room for the border around the Image.
 */
    int m_width, m_height;
    char m_action;
/**
 * The image that is displayed on the button.
 */
    Image m_ButtonImage;
/**
 * Tells whether to display the GraphicButton as looking raised or depressed.
 */
    int m_ButtonState;

/**
 * The constructor for the GraphicButton. The m_width and m_height is set to 4 pixels
 * bigger than the width and height of the image to make room for the border that will
 * go around the image to give the impression of a button.
 *
 * @param img The Image to be displayed on the button.
 */
    public GraphicButton(Image img)
        {
        m_width   = img.getWidth(null) + 4;
      
        m_height = img.getHeight(null) + 4;

        setSize(m_width,m_height);

        m_ButtonImage = img;
        addMouseListener(this);
        addMouseMotionListener(this);
        m_ButtonState = 0;
        m_GBMouseState = 0;
        repaint();
        }

/**
 * The constructor for the GraphicButton. 
 *
 * @param width The preferred width to make the GraphicButton.
 * @param height The preferred height to make the GraphicButton.
 * @param img The Image to be displayed on the button.
 */
    public GraphicButton(int width, int height, Image img)
        {
        m_width   = width;
      
        m_height = height;

        setSize(m_width,m_height);

        m_ButtonImage = img;
        addMouseListener(this);
        addMouseMotionListener(this);
        m_ButtonState = 0;
        m_GBMouseState = 0;
        repaint();
        }

/**
 * Changes the values of m_width and m_height.
 *
 * @param x The new value for m_width.
 * @param y The new value for m_height.
 */
    public void SetButtonSize(int x, int y)
        {
        m_width = x;
        m_height = y;
        setSize(m_width,m_height);
        }

/**
 * Changes the values of m_width and m_height.
 *
 * @param d The new values for m_width and m_height.
 */
    public void SetButtonSize(Dimension d)
        {
        m_width = d.width;
        m_height = d.height;
        setSize(m_width,m_height);
        }

/**
 * Not used.
 */
    public void mouseMoved(MouseEvent e) {}
/**
 * Not used.
 */
    public void mouseClicked(MouseEvent e) {}
/**
 * Adjusts m_GBMouseState to let the GraphicButton know the mouse is inside
 * its boundaries.
 *
 * @param e The MouseEvent that trigger this function.
 */
    public void mouseEntered(MouseEvent e) 
        {
        m_GBMouseState = 1;
        }
/**
 * Adjusts the m_GBMouseState and m_ButtonState for when the mouse exits the boundaries
 * of the GraphicButton.
 *
 * @param e The MouseEvent that trigger this function.
 */
    public void mouseExited(MouseEvent e)
        {
        m_GBMouseState = 0;
        m_ButtonState = 0;
        repaint();
        }
/**
 * Generates an action event if the mouse is released while still over the GraphicButton
 * after having been pressed.
 *
 * @param e The MouseEvent that trigger this function.
 */
    public void mouseReleased(MouseEvent e) 
        {
        ActionEvent event = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"Button");
        Enumeration e2 = m_v.elements();

        if (m_ButtonState == 0)
            return;

        while (e2.hasMoreElements())
            {
            Object item = e2.nextElement();
            if (item != null)
                ((ActionListener)(item)).actionPerformed(event);
            }
        m_ButtonState = 0;
        repaint();
        }
/**
 * Does nothing.
 */
    public void mouseDragged(MouseEvent e) {}

/**
 * If the mouse has been pressed over the GraphicButton then represent this
 * by showing the button as depressed.  The button will continue to look so
 * until the mouse button is released or the mouse is moved out of the button
 * area.
 *
 * @param e The MouseEvent that trigger this function.
 */
    public void mousePressed(MouseEvent e)
        {
        if (m_GBMouseState == 1)
            {
            m_ButtonState = 1;
            repaint();
            }
        }

/**
 * Adds an ActionListener to the GraphicButton.
 *
 * @param a The ActionListener to be added.
 */
    public void addActionListener(ActionListener a)
        {
        m_v.addElement(a);
        }

/**
 * Removes an ActionListener from the GraphicButton.
 *
 * @param a The ActionListener to be removed.
 */
    public void removeActionListener(ActionListener a)
        {
        m_v.removeElement(a);
        }

/**
 * Checks to see if any objects are listening for ActionEvents from the GraphicButton.
 *
 * @return Returns true if there are objects listening, false otherwise.
 */
    public boolean hasActionListener()
        {
        if (m_v.size() > 0)
            return true;
        return false;
        }

/**
 * The paint function for the GraphicButton.  If m_ButtonState is 1 then the GraphicButton
 * is painted to look depressed, if it is 0 the GraphicButton is painted to look raised.
 *
 * @param g The Graphics object for the GraphicButton.
 */
    public void paint(Graphics g)
        {
        Image img = this.createImage(m_width,m_height);
        Graphics g2 = img.getGraphics();

        int xpts1[] = { m_width, 0, 0, 2, 2,m_width-2 };
        int ypts1[] = { 0, 0, m_height, m_height-2, 2, 2 };
        int xpts2[] = { m_width, m_width, 0, 2, m_width-2,m_width-2 };
        int ypts2[] = { 0, m_height, m_height, m_height-2, m_height-2, 2 };

        int pts = xpts1.length;

        Polygon poly1 = new Polygon(xpts1,ypts1,pts);
        Polygon poly2 = new Polygon(xpts2,ypts2,pts);

        Color c1 = new Color(105,105,105);
        Color c3 = new Color(235,235,235);

        if (m_ButtonImage != null)
            g2.drawImage(m_ButtonImage,2,2,m_width-4,m_height-4,null); 

        if (m_ButtonState == 0)
            {
            g2.setColor(c3);
            g2.fillPolygon(poly1);
            g2.setColor(c1);
            g2.fillPolygon(poly2);
            }
        if (m_ButtonState == 1)
            {
            g2.setColor(c1);
            g2.fillPolygon(poly1);
            g2.setColor(c3);
            g2.fillPolygon(poly2);
            }

        g2.dispose();

        if (img != null)
            {
            g.drawImage(img,0,0,null);
            }
        }

/**
 * Moves and resizes this component to conform to the new bounding rectangle r. 
 * This component's new position is specified by r.x and r.y, and its new size 
 * is specified by r.width and r.height.
 *
 * @param r  The new bounding rectangle for this component.
 */
    public void setBounds(Rectangle r)
        {
        super.setBounds(r.x,r.y,m_width,m_height);
        }
/**
 * Moves and resizes this component. The new location of the top-left corner 
 * is specified by x and y, and the new size is specified by width and height.
 *
 * @param x  The new x-coordinate of this component. 
 * @param y  The new y-coordinate of this component. 
 * @param width  The new width of this component. 
 * @param height  The new height of this component.
 */
    public void setBounds(int x, int y, int width, int height)
        {
        super.setBounds(x,y,m_width,m_height);
        }
/**
 * Used to change the Image that is to be drawn on the GraphicButton.
 *
 * @param img The new m_ButtonImage.
 */ 
    public void setImage(Image img)
        {
        m_ButtonImage = img;
        repaint();
        }
/**
 * Calls the paint function.
 *
 * @param g The Graphics object for the GraphicButton.
 */
    public void update(Graphics g)
        {
        paint(g);
        }
    }
