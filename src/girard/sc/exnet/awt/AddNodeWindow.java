package girard.sc.exnet.awt;

import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.exnet.obj.NetworkBuilder;
import girard.sc.exnet.obj.Node;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class AddNodeWindow extends Frame implements ActionListener,WindowListener
    {
    NetworkBuilder m_NBApp;
    NetworkCanvas m_NCApp;
    
    int m_locX, m_locY;
    TextField m_NodeField;
    
    GraphicButton m_OKButton, m_CancelButton;

    int m_buttonWidth = 80;
    int m_buttonHeight = 30;

    public AddNodeWindow(int x, int y, NetworkBuilder app1, NetworkCanvas app2)
        {
        super("Add Node Window");
       
        m_NBApp = app1;
        m_NCApp = app2;

        initializeLabels();
        addWindowListener(this);

        m_NBApp.getNBW().setEditMode(true);
        m_locX = x;
        m_locY = y;
         
        setBackground(m_NBApp.getEO().getWinBkgColor());
        setLayout(new BorderLayout());
        setTitle(m_NBApp.getEO().getLabels().getObjectLabel("anw_title"));

        // Node Loc Label Panel

        GridBagPanel NodeLocLabelPanel = new GridBagPanel();

        NodeLocLabelPanel.setFont(m_NBApp.getEO().getMedLabelFont());

        NodeLocLabelPanel.constrain(new Label(m_NBApp.getEO().getLabels().getObjectLabel("anw_nlab")),1,1,2,1);

        // Node Label Panel

        GridBagPanel NodeLabelPanel = new GridBagPanel();

        NodeLabelPanel.setFont(m_NBApp.getEO().getMedLabelFont());
        m_NodeField = new TextField("A",7);
        NodeLabelPanel.constrain(m_NodeField,1,1,2,1,GridBagConstraints.WEST);

        NodeLabelPanel.constrain(new Label("           "),1,2,2,1);

        
     // Node Button Panel

        GridBagPanel NBPanel = new GridBagPanel();

        m_OKButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_OKButton.addActionListener(this);
        NBPanel.constrain(m_OKButton,1,1,2,1,GridBagConstraints.CENTER);

        m_CancelButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_CancelButton.addActionListener(this);
        NBPanel.constrain(m_CancelButton,3,1,2,1,GridBagConstraints.CENTER);        

        loadImages();

        add("North",NodeLocLabelPanel);
        add("Center",NodeLabelPanel);
        add("South",NBPanel);
       
        pack();
        this.setLocation(m_NBApp.getEO().getWidth()/2,m_NBApp.getEO().getHeight()/2);
        show();
        }

    public void actionPerformed (ActionEvent e)
        {

        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            if (theSource == m_OKButton)
                {
                // Handle OK
                m_NBApp.addNode(new Node(m_NodeField.getText(),new Point(m_locX,m_locY)));
                m_NBApp.getNBW().setEditMode(false);
                m_NCApp.repaint();
                m_NCApp.setEditMode(false);
                dispose();
                }
            if (theSource == m_CancelButton)
                {
                // Handle Cancel
                m_NBApp.getNBW().setEditMode(false);
                m_NCApp.setEditMode(false);
                dispose();
                }
            }
        }

    public void initializeLabels()
        {
        m_NBApp.getEO().initializeLabels("girard/sc/exnet/awt/anw.txt");
        }

    public void removeLabels()
        {
        m_NBApp.getEO().removeLabels("girard/sc/exnet/awt/anw.txt");
        }

    public void windowActivated(WindowEvent e)
        {
        }
    public void windowClosed(WindowEvent e) 
        {
        }
    public void windowClosing(WindowEvent e) 
        {
        }
    public void windowDeactivated(WindowEvent e) 
        {
        toFront();
        }
    public void windowDeiconified(WindowEvent e) { }
    public void windowIconified(WindowEvent e)  { }
    public void windowOpened(WindowEvent e) { }

    private void loadImages()
        {
        Graphics g;
        int x, y;
        Image tmp, tmp2;

        // Initialize Button Image
        tmp = m_NBApp.getEO().getButtonImage();

        tmp2 = m_NBApp.getEO().createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_NBApp.getEO().getWB());
        g.setFont(m_NBApp.getEO().getSmButtonFont());
        g.setColor(m_NBApp.getEO().getButtonLabelColor());     
        x = (m_buttonWidth - 6 - m_NBApp.getEO().getLabels().getObjectLabel("anw_ok").length()*9)/2;
        y = ((m_buttonHeight - 6)/2) + 4;
        g.drawString(m_NBApp.getEO().getLabels().getObjectLabel("anw_ok"),x,y);

        m_OKButton.setImage(tmp2);

        // Initialize Tutorial Button Image
        tmp2 = m_NBApp.getEO().createImage(m_buttonWidth-6,m_buttonHeight-6);

        g = tmp2.getGraphics();

        g.setFont(m_NBApp.getEO().getSmButtonFont());
        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_NBApp.getEO().getWB());
        g.setColor(m_NBApp.getEO().getButtonLabelColor());       
        x = (m_buttonWidth - 6 - m_NBApp.getEO().getLabels().getObjectLabel("anw_cancel").length()*9)/2;
        y = ((m_buttonHeight - 6)/2) + 4;
        g.drawString(m_NBApp.getEO().getLabels().getObjectLabel("anw_cancel"),x,y);
        
        m_CancelButton.setImage(tmp2);
        }
    }
