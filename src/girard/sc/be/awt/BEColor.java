package girard.sc.be.awt;

import java.awt.Color;

/* 
   The object that contains the preset color values for BE exchange
   network class objects.

   Author: Dudley Girard
   Started: 1-24-2001
*/

public class BEColor extends Color
    {
    public static final Color ACTIVE_EDGE = Color.black;
    public static final Color INACTIVE_EDGE = new Color(150,30,55);
    public static final Color COMPLETE_EDGE = new Color(30,150,55);
    public static final Color NODE = Color.black;
    public static final Color INCLUSIVE = Color.blue;
    public static final Color EXCLUSIVE = new Color(220,173,39);
    public static final Color NULL = new Color(145,96,48);
    public static final Color NULL_INCLUSIVE = new Color(55,74,99);
    public static final Color INCLUSIVE_EXCLUSIVE = new Color(150,30,150);
    public static final Color NS_LETT = new Color(255,255,200);
    public static final Color SINGLE_CONN = Color.gray;
    public static final Color edgeRed = Color.red;
    public static final Color edgeYellow = new Color(150, 150, 30);
    public static final Color edgeBlack = Color.black;
    public static final Color edgeGreen = new Color(30,150,55);

    public BEColor ()
        {
        super(0,0,0);
        }
    }
