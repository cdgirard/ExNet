package girard.sc.expt.help;

import girard.sc.awt.ImageCanvas;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;

public class FigureWindow extends Frame
    {

    public FigureWindow(Image img,String title)
        {
        super(title);
 
        ImageCanvas tmpCanvas = new ImageCanvas(img);

        setLayout(new GridLayout(1,1));

        add(tmpCanvas);
        pack();
        show();
        }
    }
