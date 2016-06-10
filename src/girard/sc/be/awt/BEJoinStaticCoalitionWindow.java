package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BEVoteJoinMsg;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BEPeriod;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Allows the user to choose whether they wish to participate in their
 * static coalition or not this round.
 * <br>
 * <br>Started: 07-07-2003
 * @author Dudley Girard
 */
public class BEJoinStaticCoalitionWindow extends Frame implements ActionListener
    {
    BENetworkActionClientWindow m_CWApp;
    ExptOverlord m_EOApp;

    Button m_yesButton, m_noButton;

    public BEJoinStaticCoalitionWindow(BENetworkActionClientWindow app)
        {
        m_CWApp = app;
        m_EOApp = m_CWApp.getEOApp();

        initializeLabels();

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("bejscw_title"));
        setFont(new Font("Monospaced",Font.PLAIN,15));
        setBackground(m_EOApp.getWinBkgColor());

        BENode node = (BENode)m_CWApp.getNetwork().getExtraData("Me");
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        BENetwork net = m_CWApp.getNetwork();
        BEPeriod bep = net.getActivePeriod();

  // Start Setup for the Center Panel.

        GridBagPanel centerPanel = new GridBagPanel();

        int counter = 1;

        TextArea coalInfo = new TextArea("",15,55,TextArea.SCROLLBARS_VERTICAL_ONLY);

        StringBuffer coalText = new StringBuffer("");

        coalText.append(m_EOApp.getLabels().getObjectLabel("bejscw_nsoyw"));
        if (nos.getCoalition().getSharing())
            {
            coalText.append("\n        ---------------------------");
            coalText.append("\n"+m_EOApp.getLabels().getObjectLabel("bejscw_apebt"));
            coalText.append(" "+m_EOApp.getLabels().getObjectLabel("bejscw_tagmw"));
            coalText.append(" "+m_EOApp.getLabels().getObjectLabel("bejscw_pftri"));
            }

        coalText.append("\n        -----------------------------");

        if (bep.getCurrentRound() != 0)
            {
            
            if (nos.getCoalition().getFormed())
                {
                coalText.append("\n"+m_EOApp.getLabels().getObjectLabel("bejscw_tcsif"));
                if (nos.getCoalition().getSharing())
                    {
                    coalText.append("\n"+m_EOApp.getLabels().getObjectLabel("bejscw_lrti")+getCoalitionMemberEarnings());
                    }
                else
                    {
                    coalText.append("\nYou earned: "+getCoalitionMemberEarnings());
                    }
                }
            else
                {
                coalText.append("\n"+m_EOApp.getLabels().getObjectLabel("bejscw_tcft"));
                }
            coalText.append("\n        -----------------------------");
            }
        
        coalText.append("\n"+m_EOApp.getLabels().getObjectLabel("bejscw_erydw"));
        coalText.append(m_EOApp.getLabels().getObjectLabel("bejscw_agmft"));
        coalText.append(m_EOApp.getLabels().getObjectLabel("bejscw_tgawb"));

        coalText.append("\n        -----------------------------");

        coalText.append("\n"+m_EOApp.getLabels().getObjectLabel("bejscw_tcpm"));
        StringBuffer tmpStr = new StringBuffer("");
        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();
            BENodeOrSubNet tmpNOS = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");
            if (tmpNOS.getCoalition().getCoalitionType().equals(nos.getCoalition().getCoalitionType()))
                {
                if (tmpNOS.getCoalition().getCoalition() == nos.getCoalition().getCoalition())
                    {
                    tmpStr.append(""+tmpNode.getLabel()+" ");
                    }
                }
            }
        coalText.append("\n"+tmpStr.toString());

        if (nos.getCoalition().getZapping())
            {
            coalText.append("\n        -----------------------------");
            int zapPercent = (int)(nos.getCoalition().getZapAmount()*100);
            // Display zap informatin here.
            coalText.append("\nAt the end of the round members of the group decide whether non members");
            coalText.append(" are zapped.  When a non-member is zapped s/he will lose "+zapPercent+"%");
            coalText.append(" of earnings.");

            int zapCost = (int)(nos.getCoalition().getZapCost()*100); 
            if (zapCost > 0)
                {
                coalText.append(" However, to do this will cost coalition member "+zapCost+"% of any resources they earned.");
                }
            }
        coalInfo.setText(coalText.toString());

        centerPanel.constrain(coalInfo,1,1,8,10);
        // centerPanel.constrain(new Label("                                         "),1,11,8,1);
        centerPanel.constrain(new Label("Do you wish to join the group?"),1,11,8,1,GridBagConstraints.CENTER);
  // End Setup of the Center Panel.


  // Start Setup of the South Panel.

        GridBagPanel southPanel = new GridBagPanel();

        m_yesButton = new Button("YES");
        m_yesButton.addActionListener(this);
        southPanel.constrain(m_yesButton,1,1,4,1,GridBagConstraints.CENTER);
        

        m_noButton = new Button("NO");
        m_noButton.addActionListener(this);
        southPanel.constrain(m_noButton,5,1,4,1,GridBagConstraints.CENTER);
  // End Setup for the South Panel.

        add("Center",centerPanel);
        add("South",southPanel);

        pack();
        show();
        }

/**
 * The callback for the yes and no buttons on the BEJoinCoalitionWindow
 */
    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            BENode node = (BENode)m_CWApp.getNetwork().getExtraData("Me");

            if (theSource == m_noButton)
                {
                Object[] out_args = new Object[2];
                out_args[0] = new Integer(node.getID());
                out_args[1] = new Boolean(false);
                //-kar-
                System.out.println("Node "+out_args[0]+": Sending: "+ out_args[1]);
                //-kar-
                BEVoteJoinMsg tmp = new BEVoteJoinMsg(out_args);
                m_CWApp.getSML().sendMessage(tmp);
                m_CWApp.removeSubWindow(this);
                m_CWApp.setMessageLabel("Please wait while others are deciding.");

                return;
                }
            if (theSource == m_yesButton)
                {
                Object[] out_args = new Object[2];
                out_args[0] = new Integer(node.getID());
                out_args[1] = new Boolean(true);
                //-kar-
                System.out.println("Node "+out_args[0]+": Sending: "+ out_args[1]);
                //-kar-
                BEVoteJoinMsg tmp = new BEVoteJoinMsg(out_args);
                m_CWApp.getSML().sendMessage(tmp);
                m_CWApp.removeSubWindow(this);
                m_CWApp.setMessageLabel("Please wait while others are deciding.");

                return;
                }
            }
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public int getCoalitionMemberEarnings()
        {
        BENetwork net = m_CWApp.getNetwork();
        BENode node = (BENode)net.getExtraData("Me");
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        
        if (nos.getCoalition().getJoined())
            {
            return nos.getCoalition().getCoalEarnings();
            }
        else
            {
            Enumeration enm = net.getNodeList().elements();
            while (enm.hasMoreElements())
                {
                BENode nTmp = (BENode)enm.nextElement();
                BENodeOrSubNet nosTmp = (BENodeOrSubNet)nTmp.getExptData("BENodeExchange");
                if (nos.getCoalition().getCoalitionType().equals(nosTmp.getCoalition().getCoalitionType()))
                    {
                    if (nos.getCoalition().getCoalition() == nosTmp.getCoalition().getCoalition())
                        {
                        if (nosTmp.getCoalition().getJoined())
                            {
                            return nosTmp.getCoalition().getCoalEarnings();
                            }
                        }
                    }
                }
            }
        return 0;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/bejscw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/bejscw.txt");
        }
    }