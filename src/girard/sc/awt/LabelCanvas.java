package girard.sc.awt;

/* This class allows one to display an Image into a Canvas for adding into a layout */

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

public class LabelCanvas extends Canvas
    {
    Dimension m_size = new Dimension(0,0);  
    Image m_background = null;
    Dimension m_labelLoc = new Dimension(0,0);
    Color m_labelColor = Color.black;
    String m_label = null;
    Font m_font = new Font("Monospaced",Font.BOLD,24);

    public LabelCanvas(Image img, String str)
        {
        Graphics g;

        m_label = str;

        m_background = img;
        m_size.width = m_background.getWidth(null);
        m_size.height = m_background.getHeight(null);
        setSize(m_size);
        repaint();     
        }

    public LabelCanvas(int width, int height, Image img, String str)
        {
        m_size.width = width;
        m_size.height = height;
        setSize(m_size);

        m_background = img;
        m_label = str;

        repaint();
        }

    public void centerLabel()
        {
        m_labelLoc.height = m_size.height/2 + 5;

        m_labelLoc.width = (m_size.width - m_label.length()*15)/2;

        repaint();
        }

    public void setLabel(String str)
        {
        m_label = str;
        repaint();
        }
    public void setLabelColor(Color c)
        {
        m_labelColor = c;
        }
    public void setLabelLoc(int x, int y)
        {
        m_labelLoc.width = x;
        m_labelLoc.height = y;
        }

    public void paint(Graphics g)
        { 
        if (m_background != null)
            {
            g.drawImage(m_background,0,0,m_size.width,m_size.height,null);
            }
        if (m_label != null)
            {
            g.setColor(m_labelColor);
            g.setFont(m_font);
            g.drawString(m_label,m_labelLoc.width,m_labelLoc.height);
            }
        }
    }
