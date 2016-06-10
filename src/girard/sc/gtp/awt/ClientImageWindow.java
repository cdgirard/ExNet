package girard.sc.gtp.awt;

/* This window is used to display an image 
   attached to a GenericTutorialPage display.

   Author: Dudley Girard
   Started: 01-08-2002
*/

import girard.sc.awt.ImageCanvas;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;

public class ClientImageWindow extends Frame
    {

    public ClientImageWindow(Image img,String title)
        {
        super(title);
 
        setLayout(new BorderLayout());

        Panel centerPanel = new Panel(new GridLayout(1,1));
        ImageCanvas tmpCanvas = new ImageCanvas(img);
        centerPanel.add(tmpCanvas);

        add("Center",centerPanel);

        pack();
  // We don't execute the show till we've set the location in ClientGTPWindow.
        }
    }
