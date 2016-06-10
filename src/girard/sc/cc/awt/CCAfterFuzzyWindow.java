package girard.sc.cc.awt;

/* Displays the list of fuzzy messages that were sent.

   Author: Dudley Girard
   Started: 7-4-2001
   Modified: 7-6-2001
   Modified: 7-18-2001
*/

import girard.sc.awt.GridBagPanel;
import girard.sc.cc.io.msg.CCAfterFuzzyWindowMsg;
import girard.sc.cc.obj.CCNetwork;
import girard.sc.cc.obj.CCNode;
import girard.sc.cc.obj.CCNodeFuzzies;
import girard.sc.cc.obj.CCNodeFuzzy;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

public class CCAfterFuzzyWindow extends Frame implements ActionListener
    {
    CCNetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    
    Button m_ReadyButton;

    public CCAfterFuzzyWindow(CCNetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ccafw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel tmpPanel = new GridBagPanel();

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccafw_mtws")),1,1,4,1,GridBagConstraints.CENTER);

        CCNetwork ccn = (CCNetwork)m_NACWApp.getExpApp().getActiveAction();

        int counter = 2;
        Enumeration enm = ccn.getNodeList().elements();
        while (enm.hasMoreElements())
            {
            CCNode node = (CCNode)enm.nextElement();
            CCNodeFuzzies nf = (CCNodeFuzzies)node.getExptData("CCNodeFuzzies");
            for (int i=0;i<nf.getFuzzies().size();i++)
                {
                CCNodeFuzzy fuzzy = (CCNodeFuzzy)nf.getFuzzies().elementAt(i);
                CCNode toNode = (CCNode)ccn.getNode(fuzzy.getToNode());
                CCNode aboutNode = (CCNode)ccn.getNode(fuzzy.getAboutNode());

                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccafw_from")+" "+node.getLabel()),1,counter,1,1);
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccafw_about")+" "+aboutNode.getLabel()),2,counter,1,1);
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccafw_to")+" "+toNode.getLabel()),3,counter,1,1);
                tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccafw_message")),4,counter,1,1);

                if (fuzzy.getMsg()) // Was a good message about someone.
                    {
                    tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccafw_good")),5,counter,1,1);
                    }
                else  // Was a bad message about someone.
                    {
                    tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccafw_bad")),5,counter,1,1);
                    }
                counter++;
                }
            }

        tmpPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ccafw_wrtcptrb")),1,counter,4,1,GridBagConstraints.CENTER);
        counter++;
    
        m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ccafw_ready"));
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
                CCAfterFuzzyWindowMsg tmp = new CCAfterFuzzyWindowMsg(null);
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
        m_EOApp.initializeLabels("girard/sc/cc/awt/ccafw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/cc/awt/ccafw.txt");
        }
    }
