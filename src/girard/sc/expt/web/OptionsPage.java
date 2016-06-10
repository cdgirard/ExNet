package girard.sc.expt.web;

import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.web.WebPanel;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

public class OptionsPage extends WebPanel implements ActionListener
    {
    GridBagPanel m_ExnetOptionsPanel = new GridBagPanel();
    GridBagPanel m_spacePanel = new GridBagPanel();
    ExptOverlord m_EOApp;
    GraphicButton m_ConstructButton, m_DataButton, m_HelpButton;
    GraphicButton m_StartButton, m_BackButton;
    int m_buttonWidth = 150;
    int m_buttonHeight = 40;

    public OptionsPage(ExptOverlord app)
        {
        m_EOApp = app;
        
        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("beeop_title"));
 
        m_ExnetOptionsPanel.setBackground(m_EOApp.getDispBkgColor());
        m_ExnetOptionsPanel.setFont(m_EOApp.getMedLabelFont());

        m_spacePanel.setBackground(m_EOApp.getDispBkgColor());
     
    // Setup Play Panel
        m_StartButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_StartButton.addActionListener(this);
        m_ExnetOptionsPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beeop_sae")),2,2,2,1,GridBagConstraints.WEST);
        m_ExnetOptionsPanel.constrain(m_StartButton,1,2,1,1,GridBagConstraints.WEST);        

    // Setup Create Panel

        Panel tmpPanel = new Panel(new GridLayout(2,1));

        m_ConstructButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_ConstructButton.addActionListener(this);
        tmpPanel.add(new Label(m_EOApp.getLabels().getObjectLabel("beeop_caa")));
        tmpPanel.add(new Label(m_EOApp.getLabels().getObjectLabel("beeop_es")));
        m_ExnetOptionsPanel.constrain(tmpPanel,2,3,2,1,GridBagConstraints.WEST);
        m_ExnetOptionsPanel.constrain(m_ConstructButton,1,3,1,1,GridBagConstraints.WEST);

    // Setup Other Panel

        m_DataButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_DataButton.addActionListener(this);
        m_ExnetOptionsPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beeop_atfd")),2,5,2,1,GridBagConstraints.WEST);
        m_ExnetOptionsPanel.constrain(m_DataButton,1,5,1,1,GridBagConstraints.WEST); 

        m_HelpButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_HelpButton.addActionListener(this);
        m_ExnetOptionsPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beeop_athf")),2,6,2,1,GridBagConstraints.WEST);
        m_ExnetOptionsPanel.constrain(m_HelpButton,1,6,1,1,GridBagConstraints.WEST); 

        m_BackButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_BackButton.addActionListener(this);
        m_ExnetOptionsPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beeop_btthp")),2,7,2,1,GridBagConstraints.WEST);
        m_ExnetOptionsPanel.constrain(m_BackButton,1,7,1,1,GridBagConstraints.WEST);   

        loadImages();

        m_spacePanel.constrain(m_ExnetOptionsPanel,1,1,60,40,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);

        if (m_EOApp.getWidth() > 640) 
            {
            m_spacePanel.constrain(new Panel(new GridLayout(1,1)),61,1,m_EOApp.getWidth()/10,40,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);
            }
        if (m_EOApp.getHeight() > 480)
            {
            m_spacePanel.constrain(new Panel(new GridLayout(1,1)),1,41,m_EOApp.getWidth()/10,m_EOApp.getHeight()/10,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);
            }

        add(m_spacePanel);
        }


    public void actionPerformed (ActionEvent e)
        {
        if (e.getSource() instanceof GraphicButton)
            {
            if (m_EOApp.getEditMode())
                return;

            GraphicButton theSource = (GraphicButton)e.getSource();
        
            if (theSource == m_StartButton)
                {
                m_EOApp.removeThenAddPanel(this,new RegisterExperimentPage(m_EOApp));
                }
            if (theSource == m_ConstructButton)
                {
                m_EOApp.removeThenAddPanel(this, new ConstructPage(m_EOApp));
                }
            if (theSource == m_DataButton)
                {
                m_EOApp.removeThenAddPanel(this, new DataPage(m_EOApp));
                }
            if (theSource == m_HelpButton)
                {
                m_EOApp.helpWindow("ehlp_op");
                }
            if (theSource == m_BackButton)
                {
                try { m_EOApp.getWB().getAppletContext().showDocument(new URL(m_EOApp.getBackLink()+"&A="+m_EOApp.getAppToken()+"&Type=4")); }
                catch (MalformedURLException murle) { }
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/web/op.txt");
        }  

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/web/op.txt");
        }


    private void loadImages()
        {
        int x, y;
        Graphics g;
        Image tmp, tmp2;

        // Initialize Button Image
        tmp = m_EOApp.getButtonImage();

    // Create Start Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("beeop_start").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("beeop_start"),x,y);
     
        m_StartButton.setImage(tmp2);

    // Create Tutorials Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("beeop_construct").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("beeop_construct"),x,y);
     
        m_ConstructButton.setImage(tmp2);

    // Create Data Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("beeop_data").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("beeop_data"),x,y);
     
        m_DataButton.setImage(tmp2);

    // Create Help Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("beeop_help").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("beeop_help"),x,y);
     
        m_HelpButton.setImage(tmp2);

    // Create Back Button
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("beeop_back").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("beeop_back"),x,y);
     
        m_BackButton.setImage(tmp2);
        }
    }