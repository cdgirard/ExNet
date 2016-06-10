package girard.sc.ce.awt;

import girard.sc.awt.BorderPanel;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Rectangle;

/**
 * Keeps the Mini Display Panels used in CE Network Action square.
 * <p>
 * <br> Started: 01-31-2003
 * <p>
 * @author Dudley Girard
 */

public class CEMiniDisplayPanel extends BorderPanel
    {

    public CEMiniDisplayPanel(Panel center)
        {
        super(center);
        }

    public CEMiniDisplayPanel(Panel center, int style)
        {
        super(center,style);
        }

    public void setBounds(Rectangle r)
        {
        if (r.width > r.height*1.2)
            {
            r.x = r.x + r.width - (int)(r.height*1.2);
            r.width = (int)(r.height*1.2);
            super.setBounds(r.x,r.y,r.width,r.height);
            }
        else if (r.height > r.width)
            {
            r.height = r.width;
            super.setBounds(r.x,r.y,r.width,r.height);
            }
        else 
            {
            super.setBounds(r.x,r.y,r.width,r.height);
            }
        }
    public void setBounds(int x, int y, int width, int height)
        {
        if (width == height)
            {
            super.setBounds(x,y,width,height);
            }
        if (width > height)
            {
            x = x + width - height;
            width = height;
            super.setBounds(x,y,width,height);
            }
        if (height > width)
            {
            height = width;
            super.setBounds(x,y,width,height);
            }
        }

    public void setSize(Dimension d)
        {
System.err.println("Went here1");
        }
    public void setSize(int width, int height)
        {
System.err.println("Went here2");
        } 
    }
