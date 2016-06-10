package girard.sc.cc.awt;

/* This window allows the subject to send sanctions to 
   predetermined other nodes.

   Author: Dudley Girard
   Started: 7-9-2001
   Modified: 9-17-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.cc.io.msg.CCNodeSanctionMsg;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeSanction;
import girard.sc.cc.obj.CCNodeSanctions;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

public class CCNodeSanctionWindow extends Frame implements ActionListener,ItemListener
    {
    CCNetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Vector m_Rewards = new Vector();
    Vector m_Sanctions = new Vector();

    Button m_ReadyButton;

    public CCNodeSanctionWindow(CCNetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccnsw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();
        CCNode node = (CCNode)ccn.getExtraData("Me");
        CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");

        GridBagPanel tmpPanel = new GridBagPanel();
        

        

        int counter = 2;
        Enumeration enm = ns.getSanctions().elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction sanction = (CCNodeSanction)enm.nextElement();
            CCNode toNode = (CCNode)ccn.getNode(sanction.getToNode());

            if ((sanction.getRewardValue() != 0) && (sanction.getSanctionValue() == 0))
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnsw_wyltsar")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnsw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccnsw_worth")+" "+sanction.getRewardValue()+" "+m_EOApp.getLabels().getObjectLabel("ccnsw_points")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccnsw_yes"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccnsw_no"),false);
                cbn.addItemListener(this);
                m_Sanctions.addElement(cbn);
                m_Rewards.addElement(cby);

                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            else if ((sanction.getRewardValue() == 0) && (sanction.getSanctionValue() != 0))
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnsw_wyltsaf")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnsw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccnsw_worth")+" "+sanction.getSanctionValue()+" "+m_EOApp.getLabels().getObjectLabel("ccnsw_points")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccnsw_yes"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccnsw_no"),false);
                cbn.addItemListener(this);
                m_Sanctions.addElement(cbn);
                m_Rewards.addElement(cby);

                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            else
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnsw_wyltsarf")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnsw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccnsw_worth")+" "+sanction.getRewardValue()+"/"+sanction.getSanctionValue()+" "+m_EOApp.getLabels().getObjectLabel("ccnsw_points")),1,counter,4,1);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccnsw_reward"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccnsw_fine"),false);
                cbn.addItemListener(this);
                m_Sanctions.addElement(cbn);
                m_Rewards.addElement(cby);

                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            }

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnsw_wrtsmptsb")),1,counter,4,1,GridBagConstraints.CENTER);
        counter++;    

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ccnsw_send"));
        m_ReadyButton.addActionListener(this);
        tmpPanel.constrain(m_ReadyButton,1,counter,4,1,GridBagConstraints.CENTER);

        add(tmpPanel);
        pack();
        show();
        }

/********************************************************************************
Callback for the Ready button on the Ready Message Window
*********************************************************************************/
    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if (theSource == m_ReadyButton)
                {
                for (int i=0;i<m_Rewards.size();i++)
                    {
                    Checkbox y = (Checkbox)m_Rewards.elementAt(i);
                    Checkbox n = (Checkbox)m_Sanctions.elementAt(i);
          
                    if ((!y.getState()) && (!n.getState()))
                        {
                        return;
                        }
                    }

                CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();
                CCNode node = (CCNode)ccn.getExtraData("Me");
                CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");

                for (int i=0;i<m_Rewards.size();i++)
                    {
                    CCNodeSanction sanction = (CCNodeSanction)ns.getSanctions().elementAt(i);
                    Checkbox cby = (Checkbox)m_Rewards.elementAt(i);

                    Object[] out_args = new Object[3];
                    out_args[0] = new Integer(node.getID()); // From
                    out_args[1] = new Integer(sanction.getToNode()); // To
                    if (cby.getState())
                        {
                        if (sanction.getRewardValue() != 0)
                            out_args[2] = new Boolean(true);
                        else
                            out_args[2] = new Boolean(false);
                        }
                    else
                        {
                        if (sanction.getRewardValue() != 0)
                            out_args[2] = new Boolean(false);
                        else
                            out_args[2] = new Boolean(true);
                        }
                    CCNodeSanctionMsg tmp = new CCNodeSanctionMsg(out_args);
                    m_NACWApp.getSML().sendMessage(tmp);
                    }
                m_NACWApp.removeSubWindow(this);
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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccnsw.txt");
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof Checkbox)
            {
            Checkbox theSource = (Checkbox)e.getSource();
            
            for (int i=0;i<m_Rewards.size();i++)
                {
                Checkbox cby = (Checkbox)m_Rewards.elementAt(i);
                Checkbox cbn = (Checkbox)m_Sanctions.elementAt(i);

                if (cby == theSource)
                    {
                    cby.setState(true);
                    cbn.setState(false);
                    break;
                    }
                if (cbn == theSource)
                    {
                    cby.setState(false);
                    cbn.setState(true);
                    break;
                    }
                }
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccnsw.txt");
        }
    }
