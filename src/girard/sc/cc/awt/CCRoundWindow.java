package girard.sc.cc.awt;

/* Gives subjects information that a new round is starting and some info
   on the CCNetwork.

   Author: Dudley Girard
   Started: 7-25-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.cc.io.msg.CCRoundWindowMsg;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCPeriod;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/****************************************************************************
Tell the sever when ready for the next round.
*****************************************************************************/
public class CCRoundWindow extends Frame implements ActionListener
    {
    CCNetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Button m_ReadyButton;

    public CCRoundWindow(CCNetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();
   
    /***************************************************************
     Display Title, Minimum Number of Exchanges, and Maximum Number of
     Exchanges allowed.
   *****************************************************************/

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccrw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        CCNetwork net = m_NACWApp.getNetwork();
        CCNode me = (CCNode)net.getExtraData("Me");

        GridBagPanel MainPanel = new GridBagPanel();

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccrw_ftr")),1,1,4,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccrw_ya")+me.getLabel()),1,2,4,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label("When Ready to Continue Press the Button."),1,3,4,1,GridBagConstraints.CENTER);
    
        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ccrw_ready"));
        m_ReadyButton.addActionListener(this);
        MainPanel.constrain(m_ReadyButton,1,4,4,1,GridBagConstraints.CENTER);

        add(MainPanel);
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
            CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();
            CCPeriod ccp = ccn.getPeriod();
            ccp.setCurrentRound(ccp.getCurrentRound()+1);
            ccp.setCurrentTime(ccp.getTime());
            CCRoundWindowMsg tmp = new CCRoundWindowMsg(null);
            m_NACWApp.getSML().sendMessage(tmp);
            m_NACWApp.removeSubWindow(this);
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccrw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccrw.txt");
        }
    }
