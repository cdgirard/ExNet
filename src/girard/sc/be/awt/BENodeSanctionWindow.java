package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BENodeSanctionMsg;
import girard.sc.be.obj.BENetwork;
import girard.sc.be.obj.BENode;
import girard.sc.be.obj.BENodeSanction;
import girard.sc.be.obj.BENodeSanctions;
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

/**
 * This window allows the subject to send sanctions to 
 * predetermined other nodes.
 * <p>
 * <br> Started: 09-19-2002
 * <p>
 * @author: Dudley Girard
 */

public class BENodeSanctionWindow extends Frame implements ActionListener,ItemListener
    {
    BENetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Vector m_Rewards = new Vector();
    Vector m_Sanctions = new Vector();

    Button m_ReadyButton;

    public BENodeSanctionWindow(BENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("bensw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        BENetwork ben = (BENetwork)m_NACWApp.getExpApp().getActiveAction();
        BENode node = (BENode)ben.getExtraData("Me");
        BENodeSanctions ns = (BENodeSanctions)node.getExptData("BENodeSanctions");

        GridBagPanel tmpPanel = new GridBagPanel();

        int counter = 2;
        Enumeration enm = ns.getSanctions().elements();
        while (enm.hasMoreElements())
            {
            BENodeSanction sanction = (BENodeSanction)enm.nextElement();
            BENode toNode = (BENode)ben.getNode(sanction.getToNode());

            if ((sanction.getRewardValue() != 0) && (sanction.getSanctionValue() == 0))
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bensw_wyltsar")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bensw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("bensw_worth")+" "+sanction.getRewardValue()+" "+m_EOApp.getLabels().getObjectLabel("bensw_points")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("bensw_yes"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("bensw_no"),false);
                cbn.addItemListener(this);
                m_Sanctions.addElement(cbn);
                m_Rewards.addElement(cby);

                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            else if ((sanction.getRewardValue() == 0) && (sanction.getSanctionValue() != 0))
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bensw_wyltsaf")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bensw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("bensw_worth")+" "+sanction.getSanctionValue()+" "+m_EOApp.getLabels().getObjectLabel("bensw_points")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("bensw_yes"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("bensw_no"),false);
                cbn.addItemListener(this);
                m_Sanctions.addElement(cbn);
                m_Rewards.addElement(cby);

                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            else
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bensw_wyltsarf")),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bensw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("bensw_worth")+" "+sanction.getRewardValue()+"/"+sanction.getSanctionValue()+" "+m_EOApp.getLabels().getObjectLabel("bensw_points")),1,counter,4,1);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("bensw_reward"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("bensw_fine"),false);
                cbn.addItemListener(this);
                m_Sanctions.addElement(cbn);
                m_Rewards.addElement(cby);

                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            }

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bensw_wrtsmptsb")),1,counter,4,1,GridBagConstraints.CENTER);
        counter++;    

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("bensw_send"));
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

                BENetwork ben = (BENetwork)m_NACWApp.getExpApp().getActiveAction();
                BENode node = (BENode)ben.getExtraData("Me");
                BENodeSanctions ns = (BENodeSanctions)node.getExptData("BENodeSanctions");

                for (int i=0;i<m_Rewards.size();i++)
                    {
                    BENodeSanction sanction = (BENodeSanction)ns.getSanctions().elementAt(i);
                    Checkbox cby = (Checkbox)m_Rewards.elementAt(i);

                    Object[] out_args = new Object[3];
                    out_args[0] = new Integer(node.getID()); // From
                    out_args[1] = new Integer(sanction.getToNode()); // To
                    if (cby.getState())
                        {
                        if (sanction.getRewardValue() != 0)
                            out_args[2] = new Boolean(true);
                        else if (sanction.getSanctionValue() != 0)
                            out_args[2] = new Boolean(false);
                        else
                            out_args[2] = new Boolean(true);
                        }
                    else
                        {
                        if (sanction.getRewardValue() != 0)
                            out_args[2] = new Boolean(false);
                        else if (sanction.getSanctionValue() != 0)
                            out_args[2] = new Boolean(true);
                        else
                            out_args[2] = new Boolean(false);
                        }
                    BENodeSanctionMsg tmp = new BENodeSanctionMsg(out_args);
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
        m_EOApp.initializeLabels("girard/sc/be/awt/bensw.txt");
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
        m_EOApp.removeLabels("girard/sc/be/awt/bensw.txt");
        }
    }
