package girard.sc.awt;


import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class allows one to store a series of Image's similar to that of the CardLayout
 * for panels.  By clicking the mouse button one is able to cycle through the list of images.
 * <p>
 * <br> Started: 1998
 * <br> Modified: 1999
 * <br> Modified: 10-14-2002
 * <p>
 *
 * @author Tim Cook and Dudley Girard
 * @version SC AWT Toolkit
 * @since JDK1.1 
 */

public class CardImage extends Canvas implements MouseListener,MouseMotionListener
    {
/**
 * Used to keep track of any that might be listing for ActionEvents triggered by this object.
 */
    protected Vector v = new Vector();

/**
 * Where the images are stored.
 *
 */
    protected Vector Images = new Vector();

/**
 * What image is presently being shown.
 *
 */
    protected int ImageIndex;

/**
 * How big the drawing area is for the images.
 *
 */
    protected Dimension size = new Dimension(0,0);  

/**
 * The base constructor for the CardImage.  Adds the proper MouseListener to this object.
 */
    public CardImage()
        {
        // super();

        addMouseListener(this);
        addMouseMotionListener(this);
        }

/**
 * Creates a CardImage of a specific size and adds one image to be displayed.  
 * Additionally, adds the proper MouseListener to this object.
 *
 * @param width The width to make the CardImage.
 * @param height The height to make the CardImage.
 * @param img An Image to display using the CardImage.
 * @see girard.sc.awt.CardImage#size
 */
    public CardImage(int width, int height, Image img)
        {
        // super();
        size.width = width;
        size.height = height;
        setSize(size);
        Images.addElement(img);
        ImageIndex = 0;
        
        addMouseListener(this);
        addMouseMotionListener(this);
        repaint();
        }

/**
 * Adds another Image to be displayed through the CardImage.
 *
 * @param img The image to add to be displayed.
 * @see girard.sc.awt.CardImage#Images
 */
    public void addImage(Image img)
        {
        Images.addElement(img);
        }

/**
 * Nothing happens, required by MouseMotionListener.
 *
 * @param e The MouseEvent that trigger it.
 */
    public void mouseMoved(MouseEvent e) {}

/**
 * Nothing happens, required by MouseMotionListener.
 *
 * @param e The MouseEvent that trigger it.
 */
    public void mouseClicked(MouseEvent e) {}

/**
 * Nothing happens, required by MouseMotionListener.
 *
  * @param e The MouseEvent that trigger it.
 */
    public void mouseEntered(MouseEvent e) {}

/**
 * Nothing happens, required by MouseMotionListener.
 *
 * @param e The MouseEvent that trigger it.
 */
    public void mouseExited(MouseEvent e) {}

/**
 * Nothing happens, required by MouseMotionListener.
 *
 * @param e The MouseEvent that trigger it.
 */
    public void mouseReleased(MouseEvent e) {}

/**
 * Nothing happens, required by MouseMotionListener.
 *
 * @param e The MouseEvent that trigger it.
 */
    public void mouseDragged(MouseEvent e) {}

/**
 * Draws the new image and sends an actionPerformed event to any objects that
 * are listening.
 *
 * @param e The MouseEvent that trigger it.
 */
    public void mousePressed(MouseEvent e)
        {
        ActionEvent event = new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"Button");
        Enumeration e2 = v.elements();

        ImageIndex++;
        if (ImageIndex == Images.size())
            ImageIndex = 0;

        while (e2.hasMoreElements())
            {
            ((ActionListener)(e2.nextElement())).actionPerformed(event);
            }
        
        repaint();
        }

/**
 * Adds an ActionListner to this object.
 *
 * @param a The ActionListener for the object that is listening to this object for action events.
 * @see girard.sc.awt.CardImage#v
 */
    public void addActionListener(ActionListener a)
        {
        v.addElement(a);
        }

/**
 * Removes an ActionListner to this object.
 *
 * @param a The ActionListener for the object that was listening to this object for action events.
 * @see girard.sc.awt.CardImage#v
 */
    public void removeActionListener(ActionListener a)
        {
        v.removeElement(a);
        }

/**
 * Checks to see if anyone is listening for ActionEvents on this object.
 *
 * @return Returns true if objects are listening, false otherwise.
 */
    public boolean hasActionListener()
        {
        if (v.size() > 0)
            return true;
        return false;
        }

/**
 * Returns the index of the active image being displayed.
 *
 * @return Returns the index of the active image being displayed.
 */
    public int getImageIndex()
        {
        return ImageIndex;
        }

/**
 * Sets which image to display from the Images Vector.
 *
 * @param value The index in the Images Vector.
 * @see girard.sc.awt.CardImage#Images
 */
    public void setImageIndex(int value)
        {
        if ((value < Images.size()) && (value > -1))
            {
            ImageIndex = value;
            repaint();
            }
        }

/**
 * Paints the active image in the display area for the CardImage.
 *
 * @param g The Graphics object for the display area.
 */
    public void paint(Graphics g)
        {
        Color c1 = new Color(0,0,0);
        Graphics g2;
        Image img;
 
        size = getSize();

        if (Images.size() > 0)
            {
            img = (Image)Images.elementAt(ImageIndex);
            g.drawImage(img,0,0,null);
            }
        g.setColor(c1); 
        g.drawRect(0,0,size.width-1,size.height-1);
        }
    }
