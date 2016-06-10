package girard.sc.expt.web;

import girard.sc.awt.GraphicButton;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.awt.ActionBuilderWindow;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.awt.SimulantBuilderWindow;
import girard.sc.web.WebPanel;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Used to provide access to building actions, simulant actors, and experiments.
 * 
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class ConstructPage extends WebPanel implements ActionListener
    {
    GridBagPanel m_spacePanel;
    GridBagPanel m_ExnetConstructPanel;
    ExptOverlord m_EOApp;
    GraphicButton m_SimulantsButton, m_ActionsButton, m_ExperimentsButton; 
    GraphicButton m_HelpButton, m_BackButton;
    int m_buttonWidth = 150;
    int m_buttonHeight = 40; 

    public ConstructPage(ExptOverlord app)
        {
        // Label filler = new Label("                   ");
        // Font f = new Font("TimesRoman",Font.PLAIN,24);
        
        m_EOApp = app;

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ecp_title"));

        m_ExnetConstructPanel = new GridBagPanel();
        m_ExnetConstructPanel.setBackground(m_EOApp.getDispBkgColor());
        m_ExnetConstructPanel.setFont(m_EOApp.getMedLabelFont());

        m_spacePanel = new GridBagPanel();
        m_spacePanel.setBackground(m_EOApp.getDispBkgColor());

        m_ExnetConstructPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ecp_ca")),2,1,3,1,GridBagConstraints.WEST);
        m_ActionsButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_ActionsButton.addActionListener(this);
        m_ExnetConstructPanel.constrain(m_ActionsButton,1,1,1,1,GridBagConstraints.WEST);

        m_ExnetConstructPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ecp_csa")),2,2,3,1,GridBagConstraints.WEST);
        m_SimulantsButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_SimulantsButton.addActionListener(this);
        m_ExnetConstructPanel.constrain(m_SimulantsButton,1,2,1,1,GridBagConstraints.WEST);

        m_ExnetConstructPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ecp_ce")),2,3,3,1,GridBagConstraints.WEST);
        m_ExperimentsButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_ExperimentsButton.addActionListener(this);
        m_ExnetConstructPanel.constrain(m_ExperimentsButton,1,3,1,1,GridBagConstraints.WEST);

        m_ExnetConstructPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ecp_athf")),2,4,3,1,GridBagConstraints.WEST);
        m_HelpButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_HelpButton.addActionListener(this);
        m_ExnetConstructPanel.constrain(m_HelpButton,1,4,1,1,GridBagConstraints.WEST);

        m_ExnetConstructPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ecp_rbttmm")),2,5,3,1,GridBagConstraints.WEST);
        m_BackButton = new GraphicButton(m_buttonWidth,m_buttonHeight,null);
        m_BackButton.addActionListener(this);
        m_ExnetConstructPanel.constrain(m_BackButton,1,5,1,1,GridBagConstraints.WEST);

        loadImages();

        m_spacePanel.constrain(m_ExnetConstructPanel,1,1,60,40,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);

        if (m_EOApp.getWidth() > 640) 
            {
            m_spacePanel.constrain(new Panel(new GridLayout(1,1)),61,1,m_EOApp.getWidth()/10,40,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);
            }
        if (m_EOApp.getHeight() > 480)
            {
            m_spacePanel.constrain(new Panel(new GridLayout(1,1)),1,41,m_EOApp.getWidth()/10,m_EOApp.getHeight()/10,GridBagConstraints.NORTHWEST,GridBagConstraints.BOTH);
            }

        add(m_spacePanel);

        // init();
        this.validate();
// System.err.println(m_LoginButton);
// System.err.println(m_CreateNewUserButton);
        }

    public void actionPerformed (ActionEvent e)
        {
        if (m_EOApp.getEditMode())
            return;

        if (e.getSource() instanceof GraphicButton)
            {
            GraphicButton theSource = (GraphicButton)e.getSource();

            if (theSource == m_ActionsButton)
                {
                // Handle Construct Action Request
                m_EOApp.setEditMode(true);
                new ActionBuilderWindow(m_EOApp);
                }
            if (theSource == m_SimulantsButton)
                {
                // Handle Create Simulant Request
                m_EOApp.setEditMode(true);
                new SimulantBuilderWindow(m_EOApp);
                }
            if (theSource == m_ExperimentsButton)
                {
                // Handle Create Experiment Request
                m_EOApp.setEditMode(true);
                new ExptBuilderWindow(m_EOApp);
                }
            if (theSource == m_HelpButton)
                {
                m_EOApp.helpWindow("ehlp_cp");
                }
            if (theSource == m_BackButton)
                {
                m_EOApp.removeThenAddPanel(this,new OptionsPage(m_EOApp));
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/web/cp.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/web/cp.txt");
        }

    private void loadImages()
        {
        Graphics g;
        int x, y;
        Font f1 = new Font("TimesRoman",Font.BOLD,24);
        Image tmp, tmp2;


        // Initialize Button Image
        tmp = m_EOApp.getButtonImage();

        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);
        
        g = tmp2.getGraphics();

        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setFont(m_EOApp.getLgButtonFont());
        g.setColor(m_EOApp.getButtonLabelColor());     
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("ecp_action").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("ecp_action"),x,y);
     
        m_ActionsButton.setImage(tmp2);

        // Initialize Simulant Button Image
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);

        g = tmp2.getGraphics();

        g.setFont(m_EOApp.getLgButtonFont());
        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setColor(m_EOApp.getButtonLabelColor()); 
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("ecp_simulant").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("ecp_simulant"),x,y);
        
        m_SimulantsButton.setImage(tmp2);

        // Initialize Experiment Button Image
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);

        g = tmp2.getGraphics();

        g.setFont(m_EOApp.getLgButtonFont());
        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setColor(m_EOApp.getButtonLabelColor()); 
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("ecp_experiment").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("ecp_experiment"),x,y);
        
        m_ExperimentsButton.setImage(tmp2);

        // Initialize Help Button Image
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);

        g = tmp2.getGraphics();

        g.setFont(m_EOApp.getLgButtonFont());
        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("ecp_help").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("ecp_help"),x,y);

        m_HelpButton.setImage(tmp2);

        // Initialize Back Button Image
        tmp2 = m_EOApp.createImage(m_buttonWidth-6,m_buttonHeight-6);

        g = tmp2.getGraphics();

        g.setFont(m_EOApp.getLgButtonFont());
        g.drawImage(tmp,0,0,m_buttonWidth-6,m_buttonHeight-6,m_EOApp.getWB());
        g.setColor(m_EOApp.getButtonLabelColor());
        x = (m_buttonWidth - 6 - m_EOApp.getLabels().getObjectLabel("ecp_back").length()*12)/2;
        y = ((m_buttonHeight - 6)/2) + 5;
        g.drawString(m_EOApp.getLabels().getObjectLabel("ecp_back"),x,y);

        m_BackButton.setImage(tmp2);
        }
    }
