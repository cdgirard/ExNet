package girard.sc.qa.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.qa.io.msg.QABeginQuestionnaireReqMsg;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Allows an experimenter to get the Questionnaire going.
 * <p>
 * <br> Started: 10-14-2002
 * <p>
 *
 * @author Dudley Girard
 */

public class QABeginExperimentWindow extends Frame implements ActionListener
    {
    QuestionnaireExperimenterWindow m_NAEWApp;
    ExptOverlord m_EOApp;

    Button m_ResumeButton;

    public QABeginExperimentWindow(QuestionnaireExperimenterWindow app)
        {
        super();
        m_NAEWApp = app;
        m_EOApp = m_NAEWApp.getEOApp();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("qabew_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel MainPanel = new GridBagPanel();

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("qabew_einp")),1,1,4,1,GridBagConstraints.CENTER);

        MainPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("qabew_pptre")),1,2,4,1,GridBagConstraints.CENTER);
    
        m_ResumeButton = new Button(m_EOApp.getLabels().getObjectLabel("qabew_resume"));
        m_ResumeButton.addActionListener(this);
        MainPanel.constrain(m_ResumeButton,1,3,4,1,GridBagConstraints.CENTER);

        add(MainPanel);
        pack();
        show();
        }


    public void actionPerformed(ActionEvent e)
        {

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if (theSource == m_ResumeButton)
                {
                synchronized(m_NAEWApp.getSML())
                    {
                    QABeginQuestionnaireReqMsg tmp = new QABeginQuestionnaireReqMsg(null); 
                    m_NAEWApp.getSML().sendMessage(tmp);

                    dispose();
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
        m_EOApp.initializeLabels("girard/sc/qa/awt/qabew.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/qa/awt/qabew.txt");
        }
    }