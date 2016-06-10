package girard.sc.ce.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tells an experimenter who needs help.
 * <p>
 * <br> Started: 02-20-2003
 * <p>
 * @author Dudley Girard
 */

public class CEHelpWindow extends Frame implements ActionListener
    {
    CENetworkActionExperimenterWindow m_NAEWApp;
    ExptOverlord m_EOApp;

    Button m_ClearButton;

    public CEHelpWindow(CENetworkActionExperimenterWindow app, int user)
        {
        super();
        m_NAEWApp = app;
        m_EOApp = m_NAEWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("cehw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel MainPanel = new GridBagPanel();

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cehw_user")+user+m_EOApp.getLabels().getObjectLabel("cehw_nh")),1,1,4,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("cehw_pbtc")),1,2,4,1,GridBagConstraints.CENTER);
    
        m_ClearButton = new Button(m_EOApp.getLabels().getObjectLabel("cehw_rw"));
        m_ClearButton.addActionListener(this);
        MainPanel.constrain(m_ClearButton,1,3,4,1,GridBagConstraints.CENTER);

        add(MainPanel);
        pack();
        show();
        }

/**
 * Callback for the User Needs Help Window
 */
    public void actionPerformed(ActionEvent e)
        {

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if (theSource == m_ClearButton)
                {
                synchronized(m_NAEWApp.getSML())
                    {
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
        m_EOApp.initializeLabels("girard/sc/ce/awt/cehw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/cehw.txt");
        }
    }