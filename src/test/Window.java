package test;

import java.awt.GridLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class Window extends JFrame
{
    public Window()
    {
        setLayout(new GridLayout(2,1));
        ImageIcon img = new ImageIcon(loadImageFile());
        JLabel label = new JLabel(img);
        add(label);
        
        JTextArea area = new JTextArea();
        String data;

	    data = loadTextFile2();
	    area.setText(data);

        
        add(area);
        
        pack();
        setVisible(true);
    }
    
    public String loadTextFile2()
    {
	StringBuffer dataFile = new StringBuffer();
	
	try
        {
            ClassLoader myCL = Window.class.getClassLoader();
            
            BufferedReader in = new BufferedReader(new InputStreamReader(myCL.getResourceAsStream("help/indexes.txt")));
            
            String inputline = in.readLine(); 
            while(inputline != null )  
                { 
                dataFile.append(inputline+"\n"); 
                inputline = in.readLine(); 
                } 
     
            in.close();
            
            return dataFile.toString();
        }
        catch (Exception x)
        {
            x.printStackTrace();
        }
	return null;
    }

    public Image loadImageFile()
    {
	Image tmp = null;

	try
	{
            ClassLoader myCL = Window.class.getClassLoader();
            
	    tmp = ImageIO.read(myCL.getResourceAsStream("images/background.jpg"));
	} 
	catch (IOException e)
	{

	    e.printStackTrace();
	}

	return tmp;
    }
}
