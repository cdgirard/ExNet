package girard.sc.qa.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.expt.awt.ClientWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.qa.io.msg.NextQuestionNoticeMsg;
import girard.sc.ques.obj.BaseQuestion;

import java.awt.Button;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

public class QAExptStartWindow extends ClientWindow implements ActionListener
    {
    Hashtable m_qa;
    
    Button m_ReadyButton;

    public QAExptStartWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        super(app1,app2,app3);

        m_qa = (Hashtable)m_ExpApp.getActiveAction();

        initializeLabels();

        getContentPane().setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("qaesw_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());
        getContentPane().setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel centerPanel = new GridBagPanel();

        Hashtable extraData = (Hashtable)m_qa.get("ExtraData");
        Hashtable windowSettings = (Hashtable)extraData.get("InitialWindow");

        String fontName = (String)windowSettings.get("FontName");
        int fontType = ((Integer)windowSettings.get("FontType")).intValue();
        int fontSize = ((Integer)windowSettings.get("FontSize")).intValue();

        setFont(new Font(fontName,fontType,fontSize));

        int counter = 1;
        String str = (String)windowSettings.get("Message");
        StringBuffer strB = new StringBuffer("");
        for (int i=0;i<str.length();i++)
            {
            if (str.charAt(i) != '\n')
                {
                strB.append(str.charAt(i));
                }
            else
                {
                centerPanel.constrain(new Label(strB.toString()),1,counter,4,1,GridBagConstraints.CENTER);
                counter++;
                strB = new StringBuffer("");
                }
            }
        if (strB.length() > 0)
            {
            centerPanel.constrain(new Label(strB.toString()),1,counter,4,1,GridBagConstraints.CENTER);
            counter++;
            }
    
        String cont = (String)windowSettings.get("Continue");
        if (cont.equals("Client"))
            {
            m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("qaesw_ready"));
            m_ReadyButton.addActionListener(this);
            centerPanel.constrain(m_ReadyButton,1,counter,4,1,GridBagConstraints.CENTER);
            }

        getContentPane().add(centerPanel);
        pack();
        show();
        }

    public void actionPerformed(ActionEvent e)
        {

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if (theSource == m_ReadyButton)
                {
                setWatcher(false);

                Vector questions = (Vector)m_qa.get("Questionaire");
        
                if (questions.size() > 0)
                    {
                    BaseQuestion bq = (BaseQuestion)questions.elementAt(0);
                    bq.showQuestion(m_EOApp,m_ExpApp,m_SML,0);

                    Object[] out_args = new Object[1];
                    out_args[0] = new Integer(1);
                    NextQuestionNoticeMsg tmp = new NextQuestionNoticeMsg(out_args);
                    m_SML.sendMessage(tmp);
                    }
                else
                    {
                    // Popup a please wait while others finish window.
                    BaseQuestion.createWaitWindow(m_EOApp,m_ExpApp,m_SML);
                    }
                }
            }
        }

    public void cleanUpWindow()
        {
        removeLabels();
        }

    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/qa/awt/qaesw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/qa/awt/qaesw.txt");
        }
    }
