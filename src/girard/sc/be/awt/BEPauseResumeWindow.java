package girard.sc.be.awt;

/* Allows an experiment to unpause an experiment after pausing it.

   Author: Dudley Girard
   Started: 9-7-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/****************************************************************************
Tell the sever when ready for the next round.
*****************************************************************************/
public class BEPauseResumeWindow extends Frame implements ActionListener
    {
    BENetworkActionExperimenterWindow m_NAEWApp;
    ExptOverlord m_EOApp;

    Button m_ResumeButton;

    public BEPauseResumeWindow(BENetworkActionExperimenterWindow app)
        {
        super();
        m_NAEWApp = app;
        m_EOApp = m_NAEWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("beprw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel MainPanel = new GridBagPanel();

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beprw_einp")),1,1,4,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beprw_pptre")),1,2,4,1,GridBagConstraints.CENTER);
    
        m_ResumeButton = new Button(m_EOApp.getLabels().getObjectLabel("beprw_resume"));
        m_ResumeButton.addActionListener(this);
        MainPanel.constrain(m_ResumeButton,1,3,4,1,GridBagConstraints.CENTER);

        add(MainPanel);
        pack();
        show();
        }

/********************************************************************************
Callback for the Pause Resume Window
*********************************************************************************/
    public void actionPerformed(ActionEvent e)
        {

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if (theSource == m_ResumeButton)
                {
                synchronized(m_NAEWApp.getSML())
                    {
                    Enumeration enm = m_NAEWApp.getHeldMessages().elements();
                    while (enm.hasMoreElements())
                        {
                        ExptMessage em = (ExptMessage)enm.nextElement();
                        em.getExperimenterResponse(m_NAEWApp);
                        }
                    m_NAEWApp.getHeldMessages().removeAllElements();
                    m_NAEWApp.setPaused(false);
                    m_NAEWApp.setPauseFlag(false);
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
        m_EOApp.initializeLabels("girard/sc/be/awt/beprw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/beprw.txt");
        }
    }