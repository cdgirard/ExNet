package girard.sc.exnet.obj;
/* 
   The object that contains the preset color values for exchange
   network class objects.

   Author: Dudley Girard
   Started: 1-24-2001
*/

import java.awt.Color;
import java.io.Serializable;

public class ExnetColor implements Serializable
    {
    public static final Color ACTIVE_EDGE = Color.black;
    public static final Color INACTIVE_EDGE = new Color(0,110,55);
    public static final Color COMPLETE_EDGE = Color.green;
    public static final Color NODE = Color.black;

    public ExnetColor ()
        {
        }
    }
