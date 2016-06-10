package girard.sc.cc.awt;

/* Displays the list of sanction messages that were sent.

   Author: Dudley Girard
   Started: 7-10-2001
   Modified: 7-18-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.cc.io.msg.CCAfterSanctionWindowMsg;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.cc.obj.CCNodeSanction;
import girard.sc.cc.obj.CCNodeSanctions;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class CCAfterSanctionWindow extends Frame implements ActionListener
    {
    CCNetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;

    Button m_ReadyButton;

    public CCAfterSanctionWindow(CCNetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccasw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel tmpPanel = new GridBagPanel();
        
        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccasw_raf")),1,1,4,1,GridBagConstraints.CENTER);

        CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();

        int counter = 2;
        Enumeration enm = ccn.getNodeList().elements();
        while (enm.hasMoreElements())
            {
// Remember to update the node resource earnings here.
            CCNode node = (CCNode)enm.nextElement();
            CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");

            for (int i=0;i<ns.getSanctions().size();i++)
                {
                CCNodeSanction sanction = (CCNodeSanction)ns.getSanctions().elementAt(i);
                CCNode toNode = (CCNode)ccn.getNode(sanction.getToNode());
                CCNodeResource nr = (CCNodeResource)toNode.getExptData("CCNodeResource");

                if ((sanction.getRewardValue() != 0) && (sanction.getSanctionValue() == 0))
                    {
                    if (sanction.getMsg())
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccasw_saro")+" "+sanction.getRewardValue()+" "+m_EOApp.getLabels().getObjectLabel("ccasw_pt")+" '"+toNode.getLabel()+"'");
                        tmpPanel.constrain(createPrettyLabel(str,CCColor.edgeGreen),1,counter,4,1);
                        counter++;
                        nr.setActiveBank(nr.getActiveBank() + sanction.getRewardValue());
                        }
                    else
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccasw_dnsaro")+" "+sanction.getRewardValue()+" "+m_EOApp.getLabels().getObjectLabel("ccasw_pt")+" '"+toNode.getLabel()+"'");
                        tmpPanel.constrain(createPrettyLabel(str,CCColor.edgeRed),1,counter,4,1);
                        counter++;
                        }
                    }
                else if ((sanction.getRewardValue() == 0) && (sanction.getSanctionValue() != 0))
                    {
                    if (sanction.getMsg())
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccasw_dnsafo")+" "+sanction.getSanctionValue()+" "+m_EOApp.getLabels().getObjectLabel("ccasw_pt")+" '"+toNode.getLabel()+"'");
                        tmpPanel.constrain(createPrettyLabel(str,CCColor.edgeGreen),1,counter,4,1);
                        counter++;
                        }
                    else
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccasw_safo")+" "+sanction.getSanctionValue()+" "+m_EOApp.getLabels().getObjectLabel("ccasw_pt")+" '"+toNode.getLabel()+"'");
                        tmpPanel.constrain(createPrettyLabel(str,CCColor.edgeRed),1,counter,4,1);
                        counter++;
                        nr.setActiveBank(nr.getActiveBank() + sanction.getSanctionValue());
                        }
                    }
                else
                    {
                    if (sanction.getMsg())
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccasw_saro")+" "+sanction.getRewardValue()+" "+m_EOApp.getLabels().getObjectLabel("ccasw_pt")+" '"+toNode.getLabel()+"'");
                        tmpPanel.constrain(createPrettyLabel(str,CCColor.edgeGreen),1,counter,4,1);
                        counter++;
                        nr.setActiveBank(nr.getActiveBank() + sanction.getRewardValue());
                        }
                    else
                        {
                        String str = new String("'"+node.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccasw_safo")+" "+sanction.getSanctionValue()+" "+m_EOApp.getLabels().getObjectLabel("ccasw_pt")+" '"+toNode.getLabel()+"'");
                        tmpPanel.constrain(createPrettyLabel(str,CCColor.edgeRed),1,counter,4,1);
                        counter++;
                        nr.setActiveBank(nr.getActiveBank() + sanction.getSanctionValue());
                        }
                    }
                }
            }

        CCNode me = (CCNode)ccn.getExtraData("Me");
        CCNodeResource nr = (CCNodeResource)me.getExptData("CCNodeResource");
        m_NACWApp.setBankLabel(nr.getActiveBank());

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccasw_wrtcptb")),1,counter,4,1,GridBagConstraints.CENTER);
        counter++;

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ccasw_ready"));
        m_ReadyButton.addActionListener(this);
        tmpPanel.constrain(m_ReadyButton,1,counter,4,1,GridBagConstraints.CENTER);

        add(tmpPanel);
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
                CCAfterSanctionWindowMsg tmp = new CCAfterSanctionWindowMsg(null);
                m_NACWApp.getSML().sendMessage(tmp);
                m_NACWApp.removeSubWindow(this);
                }
            }
        }

    public Panel createPrettyLabel(String str, Color c)
        {
        Label tmpLabel = new Label(str);
        tmpLabel.setForeground(c);

      /*  Image img = m_EOApp.createImage(400,25);
        Graphics g = img.getGraphics();

        g.setColor(m_EOApp.getWinBkgColor());
        g.fillRect(0,0,400,25);
        g.setColor(c);
        g.setFont(m_EOApp.getMedWinFont());
        g.drawString(str,0,20);
        g.dispose();

        ImageCanvas prettyLabel = new ImageCanvas(img,null); */

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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccasw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccasw.txt");
        }
    }
