package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BEStartNextPeriodMsg;
import girard.sc.be.obj.BENetwork;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/****************************************************************************
Tell the sever when ready for the next round.
*****************************************************************************/
public class BEPeriodWindow extends Frame implements ActionListener
    {
    BENetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Button m_ReadyButton;

    public BEPeriodWindow(BENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();
   
    /***************************************************************
       Update the display information for the new period.
   *****************************************************************/
        BENetwork ben = (BENetwork)m_NACWApp.getExpApp().getActiveAction();
        ben.setCurrentPeriod(ben.getCurrentPeriod() + 1);

        ben.initializeNetwork();
 
        m_NACWApp.initializeNetwork();

        m_NACWApp.repaint();

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("bepw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel PeriodReadyPanel = new GridBagPanel();

        PeriodReadyPanel.constrain(new Label("Starting Next Period"),1,1,4,1,GridBagConstraints.CENTER);

        PeriodReadyPanel.constrain(new Label("--------------------"),1,2,4,1,GridBagConstraints.CENTER);

        Label tmpLabel = new Label("Please Identify Your Position.");
        tmpLabel.setFont(new Font("Monospaced",Font.BOLD,18));

        PeriodReadyPanel.constrain(tmpLabel,1,3,4,1,GridBagConstraints.CENTER);

        PeriodReadyPanel.constrain(new Label("As It May Have Changed."),1,4,4,1,GridBagConstraints.CENTER);

        PeriodReadyPanel.constrain(new Label(""),1,5,4,1);

        PeriodReadyPanel.constrain(new Label("To Continue Press the READY Button."),1,6,4,1,GridBagConstraints.CENTER);

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("bepw_ready"));
        m_ReadyButton.addActionListener(this);
        PeriodReadyPanel.constrain(m_ReadyButton,1,7,4,1,GridBagConstraints.CENTER);

    // Start Setup for North, South, East, and West Panels.
        GridBagPanel northPanel = new GridBagPanel();
        northPanel.constrain(new Label(" "),1,1,1,1);

        GridBagPanel southPanel = new GridBagPanel();
        southPanel.constrain(new Label(" "),1,1,1,1);

        GridBagPanel eastPanel = new GridBagPanel();
        eastPanel.constrain(new Label(" "),1,1,1,1);

        GridBagPanel westPanel = new GridBagPanel();
        westPanel.constrain(new Label(" "),1,1,1,1);
   // End Setup for  North, South, East, and West Panels.

        add("Center",PeriodReadyPanel);
        add("North",northPanel);
        add("South",southPanel);
        add("East",eastPanel);
        add("West",westPanel);
        pack();
        show();
        }

/********************************************************************************
Callback for the Ready button on the Ready Message Window
*********************************************************************************/
    public void actionPerformed(ActionEvent e)
        {
        Button theSourceB = null;

        if (e.getSource() instanceof Button)
            theSourceB = (Button)e.getSource();
        
        if (theSourceB == m_ReadyButton)
            {
            m_NACWApp.setMessageLabel("Please wait while others are reading.");
            BEStartNextPeriodMsg tmp = new BEStartNextPeriodMsg(null);
            m_NACWApp.getSML().sendMessage(tmp);
            m_NACWApp.removeSubWindow(this);
            return;
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/bepw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bepw.txt");
        }
    }
