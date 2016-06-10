package girard.sc.be.awt;

import girard.sc.be.io.msg.*;
import girard.sc.expt.web.*;
import girard.sc.awt.*;
import girard.sc.be.obj.*;
import java.util.*;
import girard.sc.io.msg.*;
import java.awt.*;
import java.awt.event.*;

public class BEEndRoundWindow extends Frame implements ActionListener, WindowFocusListener
{
    int MAX_TIMES_FRONT;

    int timesMovedToFront;

    BENetworkActionClientWindow m_NACWApp;

    BEEndRoundMsg m_msg;

    ExptOverlord m_EOApp;

    Button m_ReadyButton;

    public BEEndRoundWindow(final BENetworkActionClientWindow app)
    {
        this.MAX_TIMES_FRONT = 5;
        this.timesMovedToFront = 0;
        this.addWindowFocusListener(this);
        this.m_NACWApp = app;
        this.m_EOApp = this.m_NACWApp.getEOApp();
        this.initializeLabels();
        this.constructMsg();
        this.setLayout(new BorderLayout());
        this.setTitle("End of Round");
        this.setFont(this.m_EOApp.getMedWinFont());
        this.setBackground(this.m_EOApp.getWinBkgColor());
        final GridBagPanel MainPanel = new GridBagPanel();
        final BENetwork net = this.m_NACWApp.getNetwork();
        final BEPeriod bep = net.getActivePeriod();
        this.fillInMainPanel(MainPanel);
        final GridBagPanel southPanel = new GridBagPanel();
        southPanel.constrain(new Label(this.m_EOApp.getLabels().getObjectLabel("berw_wrtcptb")), 1, 1, 4, 1, 10);
        (this.m_ReadyButton = new Button(this.m_EOApp.getLabels().getObjectLabel("berw_ready"))).addActionListener(this);
        southPanel.constrain(this.m_ReadyButton, 1, 2, 4, 1, 10);
        southPanel.constrain(new Label(" "), 1, 3, 4, 1);
        final GridBagPanel northPanel = new GridBagPanel();
        northPanel.constrain(new Label(" "), 1, 1, 1, 1);
        final GridBagPanel eastPanel = new GridBagPanel();
        eastPanel.constrain(new Label(" "), 1, 1, 1, 1);
        final GridBagPanel westPanel = new GridBagPanel();
        westPanel.constrain(new Label(" "), 1, 1, 1, 1);
        this.add("Center", MainPanel);
        this.add("North", northPanel);
        this.add("South", southPanel);
        this.add("East", eastPanel);
        this.add("West", westPanel);
        this.pack();
        this.show();
    }

    private void constructMsg()
    {
        this.m_NACWApp.getNetwork().setExtraData("CurrentState", new Double(2.0));
        final Enumeration enum1 = this.m_NACWApp.getNetwork().getNodeList().elements();
        while (enum1.hasMoreElements())
        {
            final BENode tmpNode = (BENode)enum1.nextElement();
            final BENodeExchange ne = (BENodeExchange) tmpNode.getExptData("BENodeExchange");
            ne.getResourcesEarned(this.m_NACWApp.getNetwork());
        }
        final BENode me = (BENode) this.m_NACWApp.getNetwork().getExtraData("Me");
        double per = 0.0;
        double pep = ((Double) this.m_NACWApp.getNetwork().getExtraData("PntEarnedPeriod")).doubleValue();
        double pen = ((Double) this.m_NACWApp.getNetwork().getExtraData("PntEarnedNetwork")).doubleValue();
        final BENodeExchange ne2 = (BENodeExchange) me.getExptData("BENodeExchange");
        per = ne2.getResourcesEarned(this.m_NACWApp.getNetwork());
        if (me.exptDataContains("BENodeSanctions"))
        {
            final BENodeSanctions ns = (BENodeSanctions) me.getExptData("BENodeSanctions");
            per += ns.getResourcesEarned(this.m_NACWApp.getNetwork());
        }
        this.m_NACWApp.getNetwork().setExtraData("PntEarnedRound", new Double(per));
        this.m_NACWApp.getNetwork().setExtraData("PntEarnedPeriod", new Double(per + pep));
        this.m_NACWApp.getNetwork().setExtraData("PntEarnedNetwork", new Double(per + pen));
        final Object[] out_args = { new Double(per), new Integer(me.getID()) };
        this.m_msg = new BEEndRoundMsg(out_args);
    }

    public void actionPerformed(final ActionEvent e)
    {
        Button theSourceB = null;
        if (e.getSource() instanceof Button)
        {
            theSourceB = (Button) e.getSource();
        }
        if (theSourceB == this.m_ReadyButton)
        {
            this.m_NACWApp.getSML().sendMessage(this.m_msg);
            this.m_NACWApp.removeSubWindow(this);
            this.m_NACWApp.setMessageLabel("Please wait while others are reading.");
        }
    }

    public void dispose()
    {
        this.removeLabels();
        super.dispose();
    }

    private void fillInMainPanel(final GridBagPanel MainPanel)
    {
        final BENetwork net = this.m_NACWApp.getNetwork();
        final BENode me = (BENode) net.getExtraData("Me");
        final BEPeriod bep = net.getActivePeriod();
        final String code = bep.getExtraData("Round Window Code");
        boolean flag = true;
        int counter = 1;
        int start = 0;
        int end = 0;
        while (flag)
        {
            while (code.charAt(end) != '\n' && end < code.length() - 1)
            {
                ++end;
            }
            if (code.charAt(end) == '\n')
            {
                final String command = code.substring(start, end);
                counter = this.processCommand(MainPanel, command, counter);
            }
            else
            {
                ++end;
                final String command = code.substring(start, end);
                counter = this.processCommand(MainPanel, command, counter);
                flag = false;
            }
            start = ++end;
        }
    }

    private int processCommand(final GridBagPanel MainPanel, final String command, int counter)
    {
        final BENetwork net = this.m_NACWApp.getNetwork();
        final BENode me = (BENode) net.getExtraData("Me");
        final BEPeriod bep = net.getActivePeriod();
        if (command.equals("Points"))
        {
            if (net.getCurrentPeriod() == 0 && bep.getCurrentRound() == 0)
            {
                return counter;
            }
            if (net.getExtraData("PntEarnedRound") != null)
            {
                MainPanel.constrain(new Label(this.m_EOApp.getLabels().getObjectLabel("berw_lr")), 1, counter, 10, 1, 10);
                ++counter;
                final int value = ((Double) net.getExtraData("PntEarnedRound")).intValue();
                final Label tmpLabel = new Label(String.valueOf(this.m_EOApp.getLabels().getObjectLabel("berw_ye")) + value);
                tmpLabel.setFont(new Font("Monospaced", 1, 18));
                if (value <= 0)
                {
                    tmpLabel.setForeground(Color.red);
                }
                else
                {
                    tmpLabel.setForeground(BEColor.COMPLETE_EDGE);
                }
                MainPanel.constrain(tmpLabel, 1, counter, 10, 1, 10);
                ++counter;
            }
            return counter;
        }
        else
        {
            if (!command.equals("Total Points"))
            {
                if (command.equals("Ed Schedule") && me.getExtraData("Ed") != null)
                {
                    int ed = ((Integer) me.getExtraData("Ed")).intValue();
                    if (ed == 2)
                    {
                        if (net.getCurrentPeriod() == 0 && bep.getCurrentRound() == 0)
                        {
                            return counter;
                        }
                        if (net.getExtraData("PntEarnedRound") != null)
                        {
                            MainPanel.constrain(new Label(this.m_EOApp.getLabels().getObjectLabel("berw_lr")), 1, counter, 10, 1, 10);
                            ++counter;
                            int value2 = ((Double) net.getExtraData("PntEarnedRound")).intValue();
                            boolean success = false;
                            if (value2 > 0)
                            {
                                value2 = (value2 - 1) * 10;
                                success = true;
                            }
                            else
                            {
                                final double earnings = Math.random();
                                if (earnings <= 0.01)
                                {
                                    value2 = 110;
                                }
                                else if (earnings <= 0.05)
                                {
                                    value2 = 90;
                                }
                                else if (earnings <= 0.2)
                                {
                                    value2 = 70;
                                }
                                else if (earnings <= 0.8)
                                {
                                    value2 = 50;
                                }
                                else if (earnings <= 0.95)
                                {
                                    value2 = 30;
                                }
                                else if (earnings <= 0.99)
                                {
                                    value2 = 10;
                                }
                                else if (earnings <= 1.0)
                                {
                                    value2 = 0;
                                }
                                Label tmpLabel2 = new Label("You did not reach an agreement on the");
                                MainPanel.constrain(tmpLabel2, 1, counter, 10, 1, 10);
                                ++counter;
                                tmpLabel2 = new Label("last round");
                                MainPanel.constrain(tmpLabel2, 1, counter, 10, 1, 10);
                                ++counter;
                            }
                            Label tmpLabel3 = new Label(String.valueOf(this.m_EOApp.getLabels().getObjectLabel("berw_ye")) + value2);
                            tmpLabel3.setFont(new Font("Monospaced", 1, 18));
                            if (success)
                            {
                                tmpLabel3.setForeground(BEColor.COMPLETE_EDGE);
                            }
                            MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                            ++counter;
                            if (!success)
                            {
                                tmpLabel3 = new Label("from a random ALTERNATIVE NEGOTIATION");
                                MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                                ++counter;
                                tmpLabel3 = new Label("with GAMMA");
                                MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                                ++counter;
                                final int infoLevel = ((Integer) this.m_NACWApp.getNetwork().getExtraData("InfoLevel")).intValue();
                                if (infoLevel == 11)
                                {
                                    this.m_NACWApp.offerRoundWindowBadFlash();
                                }
                            }
                            else
                            {
                                tmpLabel3 = new Label("from your negotiations with the other");
                                MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                                ++counter;
                                tmpLabel3 = new Label("person");
                                MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                                ++counter;
                                final int infoLevel = ((Integer) this.m_NACWApp.getNetwork().getExtraData("InfoLevel")).intValue();
                                if (infoLevel == 11)
                                {
                                    this.m_NACWApp.offerRoundWindowGoodFlash();
                                }
                            }
                        }
                        return counter;
                    }
                    else if (ed == 1)
                    {
                        if (net.getCurrentPeriod() == 0 && bep.getCurrentRound() == 0)
                        {
                            return counter;
                        }
                        if (net.getExtraData("PntEarnedRound") != null)
                        {
                            MainPanel.constrain(new Label(this.m_EOApp.getLabels().getObjectLabel("berw_lr")), 1, counter, 10, 1, 10);
                            ++counter;
                            int value2 = ((Double) net.getExtraData("PntEarnedRound")).intValue();
                            boolean success = false;
                            if (value2 > 0)
                            {
                                value2 = (value2 - 1) * 10;
                                success = true;
                            }
                            else
                            {
                                final double earnings = Math.random();
                                if (earnings <= 0.01)
                                {
                                    value2 = 160;
                                }
                                else if (earnings <= 0.05)
                                {
                                    value2 = 140;
                                }
                                else if (earnings <= 0.2)
                                {
                                    value2 = 120;
                                }
                                else if (earnings <= 0.8)
                                {
                                    value2 = 100;
                                }
                                else if (earnings <= 0.95)
                                {
                                    value2 = 80;
                                }
                                else if (earnings <= 0.99)
                                {
                                    value2 = 60;
                                }
                                else if (earnings <= 1.0)
                                {
                                    value2 = 40;
                                }
                                Label tmpLabel2 = new Label("You did not reach an agreement on the");
                                MainPanel.constrain(tmpLabel2, 1, counter, 10, 1, 10);
                                ++counter;
                                tmpLabel2 = new Label("last round");
                                MainPanel.constrain(tmpLabel2, 1, counter, 10, 1, 10);
                                ++counter;
                            }
                            Label tmpLabel3 = new Label(String.valueOf(this.m_EOApp.getLabels().getObjectLabel("berw_ye")) + value2);
                            tmpLabel3.setFont(new Font("Monospaced", 1, 18));
                            if (success)
                            {
                                tmpLabel3.setForeground(BEColor.COMPLETE_EDGE);
                            }
                            MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                            ++counter;
                            if (!success)
                            {
                                tmpLabel3 = new Label("from a random ALTERNATIVE NEGOTIATION");
                                MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                                ++counter;
                                tmpLabel3 = new Label("with GAMMA");
                                MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                                ++counter;
                                final int infoLevel = ((Integer) this.m_NACWApp.getNetwork().getExtraData("InfoLevel")).intValue();
                                if (infoLevel == 11)
                                {
                                    this.m_NACWApp.offerRoundWindowBadFlash();
                                }
                            }
                            else
                            {
                                tmpLabel3 = new Label("from your negotiations with the other");
                                MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                                ++counter;
                                tmpLabel3 = new Label("person");
                                MainPanel.constrain(tmpLabel3, 1, counter, 10, 1, 10);
                                ++counter;
                                final int infoLevel = ((Integer) this.m_NACWApp.getNetwork().getExtraData("InfoLevel")).intValue();
                                if (infoLevel == 11)
                                {
                                    this.m_NACWApp.offerRoundWindowGoodFlash();
                                }
                            }
                        }
                        return counter;
                    }
                }
                return counter;
            }
            if (net.getCurrentPeriod() == 0 && bep.getCurrentRound() == 0)
            {
                return counter;
            }
            if (net.getExtraData("PntEarnedNetwork") != null)
            {
                final int value = ((Double) net.getExtraData("PntEarnedNetwork")).intValue();
                final Label tmpLabel = new Label(String.valueOf(this.m_EOApp.getLabels().getObjectLabel("berw_tpe")) + value);
                tmpLabel.setFont(new Font("Monospaced", 1, 18));
                if (value <= 0)
                {
                    tmpLabel.setForeground(Color.red);
                }
                else
                {
                    tmpLabel.setForeground(BEColor.COMPLETE_EDGE);
                }
                MainPanel.constrain(tmpLabel, 1, counter, 10, 1, 10);
                ++counter;
            }
            return counter;
        }
    }

    public void initializeLabels()
    {
        this.m_EOApp.initializeLabels("girard/sc/be/awt/berw.txt");
    }

    public void removeLabels()
    {
        this.m_EOApp.removeLabels("girard/sc/be/awt/berw.txt");
    }

    public void windowGainedFocus(final WindowEvent arg0)
    {
    }

    public void windowLostFocus(final WindowEvent arg0)
    {
        if (this.timesMovedToFront < this.MAX_TIMES_FRONT)
        {
            ++this.timesMovedToFront;
            this.toFront();
        }
    }
}
