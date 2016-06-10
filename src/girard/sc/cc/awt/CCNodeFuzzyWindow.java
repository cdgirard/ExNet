package girard.sc.cc.awt;

/* This window allows the subject to send fuzzies about given nodes to 
   predetermined other nodes.

   Author: Dudley Girard
   Started: 6-28-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.cc.io.msg.CCNodeFuzzyMsg;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeFuzzies;
import girard.sc.cc.obj.CCNodeFuzzy;
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

public class CCNodeFuzzyWindow extends Frame implements ActionListener,ItemListener
    {
    CCNetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Vector m_BadMessages = new Vector();
    Vector m_GoodMessages = new Vector();

    Button m_ReadyButton;

    public CCNodeFuzzyWindow(CCNetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccnfw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();
        CCNode node = (CCNode)ccn.getExtraData("Me");
        CCNodeFuzzies nf = (CCNodeFuzzies)node.getExptData("CCNodeFuzzies");

        GridBagPanel tmpPanel = new GridBagPanel();

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnfw_ycsmao")),1,1,4,1,GridBagConstraints.CENTER);

        int counter = 2;
        Enumeration enm = nf.getFuzzies().elements();
        while (enm.hasMoreElements())
            {
            CCNodeFuzzy fuzzy = (CCNodeFuzzy)enm.nextElement();
            CCNode toNode = (CCNode)ccn.getNode(fuzzy.getToNode());
            CCNode aboutNode = (CCNode)ccn.getNode(fuzzy.getAboutNode());

            Checkbox cby = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccnfw_good"),false);
            cby.addItemListener(this);
            Checkbox cbn = new Checkbox(m_EOApp.getLabels().getObjectLabel("ccnfw_bad"),false);
            cbn.addItemListener(this);
            m_BadMessages.addElement(cbn);
            m_GoodMessages.addElement(cby);

            tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnfw_about")+" "+aboutNode.getLabel()),1,counter,1,1);
            tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnfw_to")+" "+toNode.getLabel()),2,counter,1,1);
            tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnfw_message")),3,counter,1,1);
            tmpPanel.constrain(cby,4,counter,1,1);
            tmpPanel.constrain(cbn,5,counter,1,1);
            counter++;
            }

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccnfw_wrtsmptrb")),1,counter,4,1,GridBagConstraints.CENTER);
        counter++;    

        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ccnfw_ready"));
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
                for (int i=0;i<m_GoodMessages.size();i++)
                    {
                    Checkbox y = (Checkbox)m_GoodMessages.elementAt(i);
                    Checkbox n = (Checkbox)m_BadMessages.elementAt(i);
          
                    if ((!y.getState()) && (!n.getState()))
                        {
                        return;
                        }
                    }

                CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();
                CCNode node = (CCNode)ccn.getExtraData("Me");
                CCNodeFuzzies nf = (CCNodeFuzzies)node.getExptData("CCNodeFuzzies");

                for (int i=0;i<m_GoodMessages.size();i++)
                    {
                    CCNodeFuzzy fuzzy = (CCNodeFuzzy)nf.getFuzzies().elementAt(i);
                    Checkbox cby = (Checkbox)m_GoodMessages.elementAt(i);

                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(node.getID()); // From
                    out_args[1] = new Integer(fuzzy.getToNode()); // To
                    out_args[2] = new Integer(fuzzy.getAboutNode()); // About
                    if (cby.getState())
                        {
                        out_args[3] = new Boolean(true);
                        }
                    else
                        {
                        out_args[3] = new Boolean(false);
                        }
                    CCNodeFuzzyMsg tmp = new CCNodeFuzzyMsg(out_args);
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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccnfw.txt");
        }

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof Checkbox)
            {
            Checkbox theSource = (Checkbox)e.getSource();
            
            for (int i=0;i<m_GoodMessages.size();i++)
                {
                Checkbox cby = (Checkbox)m_GoodMessages.elementAt(i);
                Checkbox cbn = (Checkbox)m_BadMessages.elementAt(i);

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
        m_EOApp.removeLabels("girard/sc/cc/awt/ccnfw.txt");
        }
    }
