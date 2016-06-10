package girard.sc.ce.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.ce.io.msg.CEStartNextPeriodMsg;
import girard.sc.ce.obj.CENetwork;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Displays a window letting the subjects know a new period is starting and
 * to check that they may have changed positions.
 * <p>
 * <br> Started: 02-20-2003
 * <p>
 * @author Dudley Girard
 */

public class CEPeriodWindow extends Frame implements ActionListener
    {
    CENetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Button m_ReadyButton;

    public CEPeriodWindow(CENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();
   
    /***************************************************************
       Update the display information for the new period.
   *****************************************************************/
        CENetwork cen = (CENetwork)m_NACWApp.getExpApp().getActiveAction();
        cen.setCurrentPeriod(cen.getCurrentPeriod() + 1);

        cen.initializeNetwork();
 
        m_NACWApp.initializeNetwork();

        m_NACWApp.repaint();

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("cepw_title"));
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

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("cepw_ready"));
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

/**
 * Callback for the Ready button on the Period Window
 */
    public void actionPerformed(ActionEvent e)
        {
        Button theSourceB = null;

        if (e.getSource() instanceof Button)
            theSourceB = (Button)e.getSource();
        
        if (theSourceB == m_ReadyButton)
            {
            m_NACWApp.setMessageLabel("Please wait while others are reading.");
            CEStartNextPeriodMsg tmp = new CEStartNextPeriodMsg(null);
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
        m_EOApp.initializeLabels("girard/sc/ce/awt/cepw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cepw.txt");
        }
    }
