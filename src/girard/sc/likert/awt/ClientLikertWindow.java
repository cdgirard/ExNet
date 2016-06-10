package girard.sc.likert.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptErrorMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.likert.obj.LikertQuestion;
import girard.sc.ques.awt.ClientBaseQuestionWindow;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;

/**
 * Used to display LikertQuestions, which display a question and have
 * a scale range to choose the answer from, to the subjects.
 * <p>
 * Started: 08-04-2002
 * Modified: 04-03-2003
 * <p>
 * @author Dudley Girard
 */

public class ClientLikertWindow extends ClientBaseQuestionWindow
    {
    LikertQuestion m_lq;

    Button m_okButton;

    TextArea m_information;

    CheckboxGroup m_answer = new CheckboxGroup();
    Checkbox[] m_ans;

    public ClientLikertWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3, int question)
        {
        super(app1,app2,app3,question);

        m_lq = (LikertQuestion)m_bq;

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_lq.getTitle());
        getContentPane().setFont(m_lq.getWinFont());

   // Setup North Area
        TextArea q = new TextArea(m_lq.getQuestion(),m_lq.getWinRows(),m_lq.getWinColumns(),TextArea.SCROLLBARS_NONE);
        q.setEditable(false);
        q.setFont(m_lq.getWinFont());
        q.setBackground(m_EOApp.getDispBkgColor());

        getContentPane().add("North",q);
   // End Setup of North Area

   // Setup Central Area
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(""),1,1,3,1); 

        centerPanel.constrain(new Label(m_lq.getLeft()),1,2,1,1,GridBagConstraints.WEST);

        centerPanel.constrain(new Label(m_lq.getCenter()),2,2,1,1,GridBagConstraints.CENTER);

        centerPanel.constrain(new Label(m_lq.getRight()),3,2,1,1,GridBagConstraints.EAST);

        getContentPane().add("Center",centerPanel);
    // End Setup of Central Area

    // Start Setup of South Area
        GridBagPanel southPanel = new GridBagPanel();

        m_ans = new Checkbox[m_lq.getRange()];

        Panel tmpPanel;
        for (int i=1;i<m_lq.getRange();i++)
            {
            tmpPanel = new Panel();
            tmpPanel.setLayout(new GridLayout(2,1));
            tmpPanel.add(new Label(""+i));
            m_ans[i-1] = new Checkbox("",m_answer,false);
            tmpPanel.add(m_ans[i-1]);
            southPanel.constrain(tmpPanel,i*2-1,1,1,2,GridBagConstraints.CENTER);

            tmpPanel = new Panel();
            tmpPanel.setLayout(new GridLayout(2,1));
            tmpPanel.add(new Label("-"));
            tmpPanel.add(new Label(""));
            southPanel.constrain(tmpPanel,i*2,1,1,2,GridBagConstraints.CENTER);
            }
        tmpPanel = new Panel();
        tmpPanel.setLayout(new GridLayout(2,1));
        tmpPanel.add(new Label(""+m_lq.getRange()));
        m_ans[m_lq.getRange()-1] = new Checkbox("",m_answer,false);
        tmpPanel.add(m_ans[m_lq.getRange()-1]);
        southPanel.constrain(tmpPanel,m_lq.getRange()*2-1,1,1,1,GridBagConstraints.CENTER);

        m_okButton = new Button("Next");
        m_okButton.addActionListener(this);
        southPanel.constrain(m_okButton,1,3,m_lq.getRange()*2,1,GridBagConstraints.CENTER);

        getContentPane().add("South",southPanel);
   // End Setup of South Area.

        pack();
        setLocation(m_lq.getWinLoc().x,m_lq.getWinLoc().y);
        show();
        }
    
    public void actionPerformed(ActionEvent e) 
        {
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if ((theSource == m_okButton) && (m_answer.getSelectedCheckbox() != null))
                {
                for (int i=0;i<m_lq.getRange();i++)
                    {
                    if (m_ans[i].getState())
                        {
                        int m = i+1;
                        removeQuestionWindow(new String(""+m),i);
                        return;
                        }
                    }
                }
            }
        if (e.getSource() instanceof ExptMessage)
            {
            synchronized(m_SML)
                {
                ExptMessage em = (ExptMessage)e.getSource();

                if (em instanceof ExptErrorMsg)
                    {
                    String str = (String)em.getArgs()[0];
                    new ErrorDialog(str);
                    }
                else
                    {
                    em.getClientResponse(this);
                    }
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/likert/awt/clw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/likert/awt/clw.txt");
        }
    }
