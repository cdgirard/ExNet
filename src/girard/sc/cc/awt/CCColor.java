package girard.sc.cc.awt;
/* 
   The object that contains the preset color values for CC exchange
   network class objects.

   Author: Dudley Girard
   Started: 5-29-2001
*/

import java.awt.Color;

public class CCColor extends Color
    {
    public static final Color ACTIVE_EDGE = Color.black;
    public static final Color INACTIVE_EDGE = new Color(150,30,55);
    public static final Color COMPLETE_EDGE = Color.green;
    public static final Color NODE = Color.black;
    public static final Color edgeRed = Color.red;
    public static final Color edgeYellow = new Color(150, 150, 30);
    public static final Color edgeBlack = Color.black;
    public static final Color edgeGreen = new Color(30,150,55);

    public CCColor ()
        {
        super(0,0,0);
        }
    }
