package girard.sc.be.awt;


import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BEBeginNetworkActionReqMsg;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Allows an experiment to get the BE Network action going.
 * <p>
 * <br> Started: 10-11-2002
 * <p>
 *
 * @author Dudley Girard
 */

public class BEBeginExperimentWindow extends Frame implements ActionListener
    {
    BENetworkActionExperimenterWindow m_NAEWApp;
    ExptOverlord m_EOApp;

    Button m_ResumeButton;

    public BEBeginExperimentWindow(BENetworkActionExperimenterWindow app)
        {
        super();
        m_NAEWApp = app;
        m_EOApp = m_NAEWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("bebew_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel MainPanel = new GridBagPanel();

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bebew_einp")),1,1,4,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bebew_pptre")),1,2,4,1,GridBagConstraints.CENTER);
    
        m_ResumeButton = new Button(m_EOApp.getLabels().getObjectLabel("bebew_resume"));
        m_ResumeButton.addActionListener(this);
        MainPanel.constrain(m_ResumeButton,1,3,4,1,GridBagConstraints.CENTER);

        add(MainPanel);
        pack();
        show();
        }


    public void actionPerformed(ActionEvent e)
        {

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if (theSource == m_ResumeButton)
                {
                synchronized(m_NAEWApp.getSML())
                    {
                    BEBeginNetworkActionReqMsg tmp = new BEBeginNetworkActionReqMsg(null); 
                    m_NAEWApp.getSML().sendMessage(tmp);

                    m_NAEWApp.removeSubWindow(this);
                    }
                }
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/bebew.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bebew.txt");
        }
    }