package girard.sc.awt;

/* This class allows one to display an Image into a Canvas for adding into a layout */

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;

public class ImageCanvas extends Canvas
    {
    Dimension m_size = new Dimension(0,0);  
    Image m_TheImage;
    ImageObserver m_observer = null;

    public ImageCanvas(Image img)
        {

        m_TheImage = img;
        m_size.width = m_TheImage.getWidth(null);
        m_size.height = m_TheImage.getHeight(null);
        setSize(m_size);

        repaint();     
        }

    public ImageCanvas(Image img, ImageObserver io)
        {

        m_TheImage = img;
        m_observer = io;
        m_size.width = m_TheImage.getWidth(null);
        m_size.height = m_TheImage.getHeight(null);
        setSize(m_size);

        repaint();     
        }

    public ImageCanvas(int width, int height, Image img)
        {

        m_size.width = width;
        m_size.height = height;
        setSize(m_size);
        m_TheImage = img;
        repaint();
        }

    public void paint(Graphics g)
        { 
        if (m_TheImage != null)
            {
            g.drawImage(m_TheImage,0,0,getSize().width,getSize().height,m_observer);
            }

        g.dispose();
        }

    public void setBounds(Rectangle r)
        {
        super.setBounds(r.x,r.y,m_TheImage.getWidth(null),m_TheImage.getHeight(null));
        }
    public void setBounds(int x, int y, int width, int height)
        {
        super.setBounds(x,y,m_TheImage.getWidth(null),m_TheImage.getHeight(null));
        }
    public void setImage(Image img)
        {
        m_TheImage = img;
        repaint();
        }

    public void update(Graphics g)
        {
        paint(g);
        }
    }
