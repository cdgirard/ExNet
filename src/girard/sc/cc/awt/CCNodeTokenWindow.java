package girard.sc.cc.awt;

/* This window allows the subject to send tokens to 
   predetermined other nodes.

   Author: Dudley Girard
   Started: 6-28-2001
   Modified: 7-20-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.cc.io.msg.CCNodeTokenMsg;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeToken;
import girard.sc.cc.obj.CCNodeTokens;
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

public class CCNodeTokenWindow extends Frame implements ActionListener,ItemListener
    {
    CCNetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Vector m_YesTokens = new Vector();
    Vector m_NoTokens = new Vector();

    Button m_ReadyButton;

    public CCNodeTokenWindow(CCNetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccntw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();
        CCNode node = (CCNode)ccn.getExtraData("Me");
        CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");

        GridBagPanel tmpPanel = new GridBagPanel();
        

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccntw_ycst")),1,1,4,1,GridBagConstraints.CENTER);

        int counter = 2;
        Enumeration enm = nt.getTokens().elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken token = (CCNodeToken)enm.nextElement();
            CCNode toNode = (CCNode)ccn.getNode(token.getToNode());
            int percent = (int)(token.getPercent()*100);
            int ph; // Present have percent chance.

            if (ccn.getPeriod().getCurrentRound() == 1)
                ph = 0;
            else
                ph = (int)((token.getTokens()*percent*1.0)/(ccn.getPeriod().getCurrentRound()*1.0 - 1.0));
            
            tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccntw_wyltsat")),1,counter,4,1);
            counter++;
            if ((token.getYesValue() != 0) && (token.getNoValue() == 0))
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccntw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccntw_pr")+" '"+toNode.getLabel()+"' "+token.getYesValue()+" "+m_EOApp.getLabels().getObjectLabel("ccntw_points")),1,counter,4,1);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccntw_yes"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccntw_no"),false);
                cbn.addItemListener(this);
                m_NoTokens.addElement(cbn);
                m_YesTokens.addElement(cby);
                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            else if ((token.getNoValue() != 0) && (token.getYesValue() == 0))
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccntw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccntw_pf")+" '"+toNode.getLabel()+"' "+token.getNoValue()+" "+m_EOApp.getLabels().getObjectLabel("ccntw_points")),1,counter,4,1);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccntw_yes"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccntw_no"),false);
                cbn.addItemListener(this);
                m_NoTokens.addElement(cbn);
                m_YesTokens.addElement(cby);
                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            else
                {
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccntw_to")+" '"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccntw_pr")+" '"+toNode.getLabel()+"' "+token.getYesValue()+" "+m_EOApp.getLabels().getObjectLabel("ccntw_points")),1,counter,4,1);
                counter++;
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccntw_opf")+" '"+toNode.getLabel()+"' "+token.getNoValue()+" "+m_EOApp.getLabels().getObjectLabel("ccntw_points.")),1,counter,4,1);
                counter++;

                Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccntw_reward"),false);
                cby.addItemListener(this);
                Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccntw_fine"),false);
                cbn.addItemListener(this);
                m_NoTokens.addElement(cbn);
                m_YesTokens.addElement(cby);
                tmpPanel.constrain(cby,1,counter,2,1,GridBagConstraints.CENTER);
                tmpPanel.constrain(cbn,3,counter,2,1,GridBagConstraints.CENTER);
                counter++;
                }
            }

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccntw_wrtstptsb")),1,counter,4,1,GridBagConstraints.CENTER);
        counter++;

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ccntw_send"));
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
                for (int i=0;i<m_YesTokens.size();i++)
                    {
                    Checkbox y = (Checkbox)m_YesTokens.elementAt(i);
                    Checkbox n = (Checkbox)m_NoTokens.elementAt(i);
          
                    if ((!y.getState()) && (!n.getState()))
                        {
                        return;
                        }
                    }

                CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();
                CCNode node = (CCNode)ccn.getExtraData("Me");
                CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");

                for (int i=0;i<m_YesTokens.size();i++)
                    {
                    CCNodeToken token = (CCNodeToken)nt.getTokens().elementAt(i);
                    Checkbox cby = (Checkbox)m_YesTokens.elementAt(i);

                    Object[] out_args = new Object[3];
                    out_args[0] = new Integer(node.getID()); // From
                    out_args[1] = new Integer(token.getToNode()); // To
                    if (cby.getState())
                        {
                        if (token.getYesValue() != 0)
                            out_args[2] = new Boolean(true);
                        else
                            out_args[2] = new Boolean(false);
                        }
                    else
                        {
                        if (token.getYesValue() != 0)
                            out_args[2] = new Boolean(false);
                        else
                            out_args[2] = new Boolean(true);
                        }
                    CCNodeTokenMsg tmp = new CCNodeTokenMsg(out_args);
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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccntw.txt");
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof Checkbox)
            {
            Checkbox theSource = (Checkbox)e.getSource();
            
            for (int i=0;i<m_YesTokens.size();i++)
                {
                Checkbox cby = (Checkbox)m_YesTokens.elementAt(i);
                Checkbox cbn = (Checkbox)m_NoTokens.elementAt(i);

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
        m_EOApp.removeLabels("girard/sc/cc/awt/ccntw.txt");
        }
    }
