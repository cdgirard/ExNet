package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BEAfterSanctionWindowMsg;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeSanction;
import girard.sc.be.obj.BENodeSanctions;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

/**
 * Displays the list of sanction messages that were sent.
 * <p>
 * <br> Started: 09-19-2002
 * <br> Modified: 09-27-2002
 * <p>
 * @author Dudley Girard
 */

public class BEAfterSanctionWindow extends Frame implements ActionListener
    {
    BENetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;

    Button m_ReadyButton;

    public BEAfterSanctionWindow(BENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("beasw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel mainPanel = new GridBagPanel();
        int mainCounter = 1;

        BENetwork ben = (BENetwork)m_NACWApp.getExpApp().getActiveAction();

        boolean payoffFlag = false;
        GridBagPanel payoffPanel = new GridBagPanel();
        payoffPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beasw_op")),1,1,4,1,GridBagConstraints.CENTER);
        int payoffCounter = 2;

        boolean subtractionFlag = false;
        GridBagPanel subtractionPanel = new GridBagPanel();
        subtractionPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beasw_os")),1,1,4,1,GridBagConstraints.CENTER);        
        int subtractionCounter = 2;

        Enumeration enm = ben.getNodeList().elements();
        while (enm.hasMoreElements())
            {
// Remember to update the node resource earnings here.
            BENode node = (BENode)enm.nextElement();
            BENodeSanctions ns = (BENodeSanctions)node.getExptData("BENodeSanctions");

            for (int i=0;i<ns.getSanctions().size();i++)
                {
                BENodeSanction sanction = (BENodeSanction)ns.getSanctions().elementAt(i);
                BENode toNode = (BENode)ben.getNode(sanction.getToNode());

                if ((sanction.getRewardValue() != 0) && (sanction.getSanctionValue() == 0))
                    {
                    payoffFlag = true;
                    if (sanction.getMsg())
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("beasw_sent")+" "+sanction.getRewardValue()+" "+m_EOApp.getLabels().getObjectLabel("beasw_pt")+" '"+toNode.getLabel()+"'");
                        payoffPanel.constrain(createPrettyLabel(str,BEColor.edgeGreen),1,payoffCounter,4,1);
                        payoffCounter++;
                        }
                    else
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("beasw_snpt")+" '"+toNode.getLabel()+"'");
                        payoffPanel.constrain(createPrettyLabel(str,BEColor.edgeBlack),1,payoffCounter,4,1);
                        payoffCounter++;
                        }
                    }
                else if ((sanction.getRewardValue() == 0) && (sanction.getSanctionValue() != 0))
                    {
                    subtractionFlag = true;
                    if (sanction.getMsg())
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("beasw_dnspf")+" '"+toNode.getLabel()+"'s "+m_EOApp.getLabels().getObjectLabel("beasw_total"));
                        subtractionPanel.constrain(createPrettyLabel(str,BEColor.edgeGreen),1,subtractionCounter,4,1);
                        subtractionCounter++;
                        }
                    else
                        {
                        int value = -(sanction.getSanctionValue());
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("beasw_subtracted")+" "+value+" "+m_EOApp.getLabels().getObjectLabel("beasw_pf")+" '"+toNode.getLabel()+"'s "+m_EOApp.getLabels().getObjectLabel("beasw_total"));
                        subtractionPanel.constrain(createPrettyLabel(str,BEColor.edgeRed),1,subtractionCounter,4,1);
                        subtractionCounter++;
                        }
                    }
                else if ((sanction.getRewardValue() == 0) && (sanction.getSanctionValue() == 0))
                    {
                    payoffFlag = true;
                    String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("beasw_snpt")+" '"+toNode.getLabel()+"'");
                    payoffPanel.constrain(createPrettyLabel(str,BEColor.edgeBlack),1,payoffCounter,4,1);
                    payoffCounter++;
                    }
                else
                    {
                    if (sanction.getMsg())
                        {
                        payoffFlag = true;
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("beasw_sent")+" "+sanction.getRewardValue()+" "+m_EOApp.getLabels().getObjectLabel("beasw_pt")+" '"+toNode.getLabel()+"'");
                        payoffPanel.constrain(createPrettyLabel(str,BEColor.edgeGreen),1,payoffCounter,4,1);
                        payoffCounter++;
                        }
                    else
                        {
                        subtractionFlag = true;
                        int value = -(sanction.getSanctionValue());
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("beasw_subtracted")+" "+value+" "+m_EOApp.getLabels().getObjectLabel("beasw_pf")+" '"+toNode.getLabel()+"'s "+m_EOApp.getLabels().getObjectLabel("beasw_total"));
                        subtractionPanel.constrain(createPrettyLabel(str,BEColor.edgeRed),1,subtractionCounter,4,1);
                        subtractionCounter++;
                        }
                    }
                }
            }

        if (payoffFlag)
            {
            mainPanel.constrain(payoffPanel,1,mainCounter,4,payoffCounter,GridBagConstraints.CENTER);
            mainCounter = mainCounter + payoffCounter;
            }
        if (subtractionFlag)
            {
            mainPanel.constrain(subtractionPanel,1,mainCounter,4,subtractionCounter,GridBagConstraints.CENTER);
            mainCounter = mainCounter + subtractionCounter;
            }

        mainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("beasw_wrtcptb")),1,mainCounter,4,1,GridBagConstraints.CENTER);
        mainCounter++;

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("beasw_ready"));
        m_ReadyButton.addActionListener(this);
        mainPanel.constrain(m_ReadyButton,1,mainCounter,4,1,GridBagConstraints.CENTER);

        add(mainPanel);
        pack();
        show();
        }

/********************************************************************************

*********************************************************************************/
    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if (theSource == m_ReadyButton)
                {
                BEAfterSanctionWindowMsg tmp = new BEAfterSanctionWindowMsg(null);
                m_NACWApp.getSML().sendMessage(tmp);
                m_NACWApp.removeSubWindow(this);
                }
            }
        }

    public Panel createPrettyLabel(String str, Color c)
        {
        Label tmpLabel = new Label(str);
        tmpLabel.setFont(new Font("Monospaced",Font.BOLD,16));
        tmpLabel.setForeground(c);

        Panel tmpPanel = new Panel(new GridLayout(1,1));
        tmpPanel.add(tmpLabel);

        return tmpPanel;
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/be/awt/beasw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/beasw.txt");
        }
    }
