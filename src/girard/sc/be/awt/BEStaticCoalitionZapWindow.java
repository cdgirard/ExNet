package girard.sc.be.awt;

import girard.sc.awt.FixedJList;
import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BEVoteZapMsg;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeOrSubNet;
import girard.sc.be.obj.BEPeriod;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Allows the user to choose whether they wish to zap free riders
 * in their static coalition or not this round.
 * <br>
 * <br>Started: 07-25-2003
 * @author Dudley Girard
 */
public class BEStaticCoalitionZapWindow extends Frame implements ActionListener
    {
    BENetworkActionClientWindow m_CWApp;
    ExptOverlord m_EOApp;

    FixedJList m_freeRiderList;

    Button m_yesButton, m_noButton;

    public BEStaticCoalitionZapWindow(BENetworkActionClientWindow app)
        {
        m_CWApp = app;
        m_EOApp = m_CWApp.getEOApp();

        initializeLabels();

        setLayout(new BorderLayout());
        setTitle(m_EOApp.getLabels().getObjectLabel("besczw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        BENode node = (BENode)m_CWApp.getNetwork().getExtraData("Me");
        BENodeOrSubNet nos = (BENodeOrSubNet)node.getExptData("BENodeExchange");
        BENetwork net = m_CWApp.getNetwork();
        BEPeriod bep = net.getActivePeriod();

  // Start Setup for the Center Panel.

        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("besczw_paeiz")),1,1,8,1);
        int[] colWidths = { 9, 13, 13 };
        m_freeRiderList = new FixedJList(3,colWidths,FixedJList.CENTER);
        m_freeRiderList.setVisibleRowCount(5);
        m_freeRiderList.setSorted(true);
System.err.println("PS1: "+m_freeRiderList.getPreferredSize().width);
        m_freeRiderList.setListFont(m_EOApp.getMedWinFont());
System.err.println("PS2: "+m_freeRiderList.getPreferredSize().width);
        int[] sortOrder = { 1, 0, 2 };
        m_freeRiderList.setSortOrder(sortOrder);
        m_freeRiderList.setSize(m_freeRiderList.getPreferredSize());
        m_freeRiderList.setBackground(Color.white);
        fillFreeRiderList(nos,net);
        centerPanel.constrain(m_freeRiderList,1,2,8,4);

        int counter = 6;

        if (nos.getCoalition().getZapCost() > 0)
            {
            centerPanel.constrain(new Label("Cost To Zap: "+nos.getCoalition().getZapCost(nos,net)),1,counter++,8,1);
            }
     
        centerPanel.constrain(new Label("Do you wish to zap the free riders?"),1,counter++,8,1);
  // End Setup of the Center Panel.


  // Start Setup of the South Panel.

        GridBagPanel southPanel = new GridBagPanel();

        m_yesButton = new Button("YES");
        m_yesButton.addActionListener(this);
        southPanel.constrain(m_yesButton,1,1,4,1);
        

        m_noButton = new Button("NO");
        m_noButton.addActionListener(this);
        southPanel.constrain(m_noButton,5,1,4,1);
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
                BEVoteZapMsg tmp = new BEVoteZapMsg(out_args);
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
                BEVoteZapMsg tmp = new BEVoteZapMsg(out_args);
                m_CWApp.getSML().sendMessage(tmp);
                m_CWApp.removeSubWindow(this);
                m_CWApp.setMessageLabel("Please wait while others are deciding.");

                return;
                }
            }
        }

    private void fillFreeRiderList(BENodeOrSubNet nos, BENetwork net)
        {
        Enumeration enm = net.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            BENode tmpNode = (BENode)enm.nextElement();
            BENodeOrSubNet tmpNOS = (BENodeOrSubNet)tmpNode.getExptData("BENodeExchange");

            if (nos.getCoalition().getCoalitionType().equals(tmpNOS.getCoalition().getCoalitionType()))
                {
                if (nos.getCoalition().getCoalition() == tmpNOS.getCoalition().getCoalition())
                    {
                    if (!tmpNOS.getCoalition().getJoined())
                        {
                        int stoleAmt = tmpNOS.getCoalition().getEarnedCoalResources(tmpNOS,net);
                        if (stoleAmt > 0)
                            {
                            int afterZap = (int)(stoleAmt*tmpNOS.getCoalition().getZapAmount());
                            String[] str = new String[3];
                            str[0] = tmpNode.getLabel();
                            str[1] = new String(""+stoleAmt);
                            str[2] = new String(""+afterZap);
                            m_freeRiderList.addItem(str);
                            }
                        }
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
        m_EOApp.initializeLabels("girard/sc/be/awt/besczw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/besczw.txt");
        }
    }