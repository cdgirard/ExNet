package girard.sc.gtp.awt;

/* This window is used to display an image an experimenter might want to
   attach to a GenericTutorialPage display.

   Author: Dudley Girard
   Started: 11-3-2001
*/

import girard.sc.awt.ImageCanvas;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ImageWindow extends Frame implements ActionListener
    {
    Button m_okButton;

    public ImageWindow(Image img,String title,Point loc)
        {
        super(title);
 
        setLayout(new BorderLayout());

        Panel centerPanel = new Panel(new GridLayout(1,1));
        ImageCanvas tmpCanvas = new ImageCanvas(img);
        centerPanel.add(tmpCanvas);

        Panel southPanel = new Panel(new GridLayout(1,1));
        m_okButton = new Button("OK");
        m_okButton.addActionListener(this);
        southPanel.add(m_okButton);

        add("Center",centerPanel);
        add("South",southPanel);

        pack();
        this.setLocation(loc.x,loc.y);
        show();
        }

    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_okButton)
                {
                this.dispose();
                }
            }
        }
    }
