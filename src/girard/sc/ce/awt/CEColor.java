package girard.sc.ce.awt;

import java.awt.Color;

/** 
 * The object that contains the preset color values for CE exchange
 * network class objects.
 * <p>
 * <br> Started: 01-16-2003
 * <p>
 * @author Dudley Girard
 */

public class CEColor extends Color
    {
    public static final Color ACTIVE_EDGE = Color.black;
    public static final Color INACTIVE_EDGE = new Color(150,30,55);
    public static final Color COMPLETE_EDGE = Color.green;
    public static final Color NODE = Color.black;
    public static final Color edgeRed = Color.red;
    public static final Color edgeYellow = new Color(150, 150, 30);
    public static final Color edgeBlack = Color.black;
    public static final Color edgeGreen = new Color(30,150,55);

    public CEColor ()
        {
        super(0,0,0);
        }
    }