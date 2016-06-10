package girard.sc.cc.awt;

/* Displays the list of fuzzy messages that were sent.

   Author: Dudley Girard
   Started: 7-4-2001
   Modified: 7-6-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.cc.io.msg.CCAfterTokenWindowMsg;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeResource;
import girard.sc.cc.obj.CCNodeToken;
import girard.sc.cc.obj.CCNodeTokens;
import girard.sc.cc.obj.CCPeriod;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class CCAfterTokenWindow extends Frame implements ActionListener
    {
    CCNetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;

    TextArea m_tokensSentArea = new TextArea(10,80);

    Button m_ReadyButton;

    public CCAfterTokenWindow(CCNetworkActionClientWindow app, int coinToss)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccatw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel tmpPanel = new GridBagPanel();
        
        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccatw_tokens")),1,1,4,1,GridBagConstraints.CENTER);

        CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();

        CCPeriod ccp = ccn.getPeriod();

        m_tokensSentArea.setEditable(false);

        StringBuffer str = new StringBuffer("");

        int counter = 2;
        Enumeration enm = ccn.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");
            for (int i=0;i<nt.getTokens().size();i++)
                {
                str.append("-------------------------------\n");
                CCNodeToken token = (CCNodeToken)nt.getTokens().elementAt(i);
                CCNode toNode = (CCNode)ccn.getNode(token.getToNode());
                int percent = (int)(token.getPercent()*100);
                int ph; // Present have percent chance.

                ph = (int)((token.getTokens()*percent*1.0)/(ccn.getPeriod().getCurrentRound()*1.0));

                str.append("\n");
                if ((token.getYesValue() != 0) && (token.getNoValue() == 0))
                    {
                    if (token.getMsg())
                        {
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_atwsf")+" '"+node.getLabel()+"' ");
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_to")+" '"+toNode.getLabel()+"'.");
                        }
                    else
                        {
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_ntwsf")+" '"+node.getLabel()+"' ");
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_to")+" '"+toNode.getLabel()+"'.");
                        }
                    str.append("\n");
                    str.append("    ");
                    str.append(m_EOApp.getLabels().getObjectLabel("ccatw_pr")+" "+token.getYesValue());
                    str.append("\n");
                    str.append("    ");
                    str.append(m_EOApp.getLabels().getObjectLabel("ccatw_ccorr")+" "+ph+"% (max possible: "+percent+"%)");
                    str.append("\n");
                    }
                else if ((token.getNoValue() != 0) && (token.getYesValue() == 0))
                    {
                    if (token.getMsg())
                        {
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_ntwsf")+" '"+node.getLabel()+"' ");
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_to")+" '"+toNode.getLabel()+"'.");
                        }
                    else
                        {
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_atwsf")+" '"+node.getLabel()+"' ");
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_to")+" '"+toNode.getLabel()+"'.");
                        }
                    str.append("\n");
                    str.append("    ");
                    str.append(m_EOApp.getLabels().getObjectLabel("ccatw_pf")+" "+token.getNoValue());
                    str.append("\n");
                    str.append("    ");
                    int negPH = 100 - ph;
                    str.append(m_EOApp.getLabels().getObjectLabel("ccatw_ccorf")+" "+negPH+"% (max possible: 100%)");
                    str.append("\n");
                    }
                else
                    {
                    if (token.getMsg())
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_aprtwsf")+" '"+node.getLabel()+"' ");
                    else
                        str.append(m_EOApp.getLabels().getObjectLabel("ccatw_apftwsf")+" '"+node.getLabel()+"' ");

                    str.append(m_EOApp.getLabels().getObjectLabel("ccatw_to")+" '"+toNode.getLabel()+"'.");
                    str.append("\n");
                    str.append("    ");
                    str.append(m_EOApp.getLabels().getObjectLabel("ccatw_pr")+" "+token.getYesValue()+" \\ "+m_EOApp.getLabels().getObjectLabel("ccatw_pf")+" "+token.getNoValue());
                    str.append("\n");
                    str.append("    ");
                    str.append(m_EOApp.getLabels().getObjectLabel("ccatw_ccorr")+" "+ph+"% (max possible: "+percent+"%)");
                    str.append("\n");
                    str.append("    ");
                    int negPH = 100 - ph;
                    str.append(m_EOApp.getLabels().getObjectLabel("ccatw_ccorf")+" "+negPH+"% (max possible: 100%)");
                    str.append("\n");
                    }
            
                if (ccp.getCurrentRound() == ccp.getRounds()) // It's the last round compute the outcomes.
                    {
                    CCNodeResource nr = (CCNodeResource)toNode.getExptData("CCNodeResource");

                    if (coinToss <= ph)
                        {
                        if (token.getYesValue() != 0)
                            {
                            str.append("'"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccatw_gro")+" "+token.getYesValue()+" "+m_EOApp.getLabels().getObjectLabel("ccatw_points"));
                            nr.setActiveBank(nr.getActiveBank()+token.getYesValue());
                            }
                        else
                            {
                            str.append("'"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccatw_afo")+" "+token.getNoValue()+" "+m_EOApp.getLabels().getObjectLabel("ccatw_points"));
                            }
                        }
                    else
                        {
                        if (token.getNoValue() != 0)
                            {
                            str.append(toNode.getLabel()+m_EOApp.getLabels().getObjectLabel("ccatw_gfo")+" "+token.getNoValue()+" "+m_EOApp.getLabels().getObjectLabel("ccatw_points"));
                            nr.setActiveBank(nr.getActiveBank()+token.getNoValue());
                            }
                        else
                            {
                            str.append("'"+toNode.getLabel()+"' "+m_EOApp.getLabels().getObjectLabel("ccatw_ftrro")+" "+token.getYesValue()+" "+m_EOApp.getLabels().getObjectLabel("ccatw_points"));
                            }
                        }
                    str.append("\n");
                    }
                }
            
            }
        str.append("-------------------------------\n");
        m_tokensSentArea.setText(str.toString());

        tmpPanel.constrain(m_tokensSentArea,1,counter,4,4);
        counter = counter + 4;

        CCNode me = (CCNode)ccn.getExtraData("Me");
        CCNodeResource nr = (CCNodeResource)me.getExptData("CCNodeResource");
        m_NACWApp.setBankLabel(nr.getActiveBank());

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccatw_wrtcptb")),1,counter,4,1,GridBagConstraints.CENTER);
        counter++;    

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ccatw_ready"));
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
                CCAfterTokenWindowMsg tmp = new CCAfterTokenWindowMsg(null);
                m_NACWApp.getSML().sendMessage(tmp);
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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccatw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccatw.txt");
        }
    }
