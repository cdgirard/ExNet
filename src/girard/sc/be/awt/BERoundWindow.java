package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BERoundWindowMsg;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BEPeriod;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/************************************************************************
Tell the sever when ready for the next round.
*************************************************************************/
public class BERoundWindow extends Frame implements ActionListener
    {
    BENetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Button m_ReadyButton;

    public BERoundWindow(BENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();
   
    /***************************************************************
     Display Title, Minimum Number of Exchanges, and Maximum Number of
     Exchanges allowed.
   *****************************************************************/

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("berw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel MainPanel = new GridBagPanel();

        BENetwork net = m_NACWApp.getNetwork();
        BEPeriod bep = net.getActivePeriod();
         
        fillInMainPanel(MainPanel);

        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("berw_wrtcptb")),1,1,4,1,GridBagConstraints.CENTER);
    
        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("berw_ready"));
        m_ReadyButton.addActionListener(this);
        southPanel.constrain(m_ReadyButton,1,2,4,1,GridBagConstraints.CENTER);

        southPanel.constrain(new Label(" "),1,3,4,1);

   // Start Setup for North, South, East, and West Panels.
        GridBagPanel northPanel = new GridBagPanel();
        northPanel.constrain(new Label(" "),1,1,1,1);

        GridBagPanel eastPanel = new GridBagPanel();
        eastPanel.constrain(new Label(" "),1,1,1,1);

        GridBagPanel westPanel = new GridBagPanel();
        westPanel.constrain(new Label(" "),1,1,1,1);
   // End Setup for  North, South, East, and West Panels.

        add("Center",MainPanel);
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
            BENetwork ben = (BENetwork)m_NACWApp.getExpApp().getActiveAction();
            BEPeriod bep = ben.getActivePeriod();
            bep.setCurrentRound(bep.getCurrentRound()+1);
            bep.setCurrentTime(bep.getTime());
            BERoundWindowMsg tmp = new BERoundWindowMsg(null);
            m_NACWApp.getSML().sendMessage(tmp);
            m_NACWApp.removeSubWindow(this);
            m_NACWApp.setMessageLabel("Please wait while others are reading.");
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

/**
 * First try creating a generic display system.
 */
    private void fillInMainPanel(GridBagPanel MainPanel)
        {
        BENetwork net = m_NACWApp.getNetwork();
        BENode me = (BENode)net.getExtraData("Me");   

        BEPeriod bep = net.getActivePeriod();

        String code = bep.getExtraData("Round Window Code");

        boolean flag = true;
        int counter = 1;
        int start = 0;
        int end = 0;
        while (flag)
            {
            while ((code.charAt(end) != '\n') && (end < code.length() - 1))
                {
                end++;
                }
            if (code.charAt(end) == '\n')
                {
                String command = code.substring(start,end);
                counter = processCommand(MainPanel,command,counter);
                }
            else
                {
  // At the end of the code string.
                end++;
                String command = code.substring(start,end);
                counter = processCommand(MainPanel,command,counter);
                flag = false;
                }
            start = end + 1;
            end = start;
            }
        }

    private int processCommand(GridBagPanel MainPanel, String command, int counter)
        {
        BENetwork net = m_NACWApp.getNetwork();
        BENode me = (BENode)net.getExtraData("Me");   

        BEPeriod bep = net.getActivePeriod();

        if (command.equals("Points"))
            {
            if ((net.getCurrentPeriod() == 0) && (bep.getCurrentRound() == 0))
                return counter;
  
            if (net.getExtraData("PntEarnedRound") != null)
                {
                MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("berw_lr")),1,counter,10,1,GridBagConstraints.CENTER);
                counter++;
                int value = ((Double)net.getExtraData("PntEarnedRound")).intValue();
                Label tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("berw_ye")+value);
                tmpLabel.setFont(new Font("Monospaced",Font.BOLD,18));
                if (value <= 0)
                    tmpLabel.setForeground(Color.red);
                else
                    tmpLabel.setForeground(BEColor.COMPLETE_EDGE);

                MainPanel.constrain(tmpLabel,1,counter,10,1,GridBagConstraints.CENTER);
                counter++;
                }
            return counter;
            }
        if (command.equals("ID"))
            {
            MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("berw_ftr")),1,counter,4,1,GridBagConstraints.CENTER);
            counter++;
 
            Label tmpLabel = new Label(m_EOApp.getLabels().getObjectLabel("berw_ya")+me.getLabel());
            tmpLabel.setFont(new Font("Monospaced",Font.BOLD,18));
            tmpLabel.setForeground(Color.black);

            MainPanel.constrain(tmpLabel,1,counter,4,1,GridBagConstraints.CENTER);
            counter++;

            return counter;
            }

        return counter;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/berw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/berw.txt");
        }
    }
