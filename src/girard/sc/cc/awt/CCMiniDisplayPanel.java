package girard.sc.cc.awt;
/* 
   

   Author: Dudley Girard
   Started: 
*/

import girard.sc.awt.BorderPanel;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.Rectangle;


public class CCMiniDisplayPanel extends BorderPanel
    {

    public CCMiniDisplayPanel(Panel center)
        {
        super(center);
        }

    public CCMiniDisplayPanel(Panel center, int style)
        {
        super(center,style);
        }

    public void setBounds(Rectangle r)
        {
        if (r.width == r.height)
            {
            super.setBounds(r.x,r.y,r.width,r.height);
            }
        if (r.width > r.height)
            {
            r.x = r.x + r.width - r.height;
            r.width = r.height;
            super.setBounds(r.x,r.y,r.width,r.height);
            }
        if (r.height > r.width)
            {
            r.height = r.width;
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
