package girard.sc.expt.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.ShutdownExptReqMsg;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class ClientEndExptWindow extends Frame implements ActionListener
    {
    ClientWindow m_CWApp;
    ExptOverlord m_EOApp;
    
    Button m_ReadyButton;

    public ClientEndExptWindow(ClientWindow app, Hashtable details)
        {
        super();
        m_CWApp = app;
        m_EOApp = m_CWApp.getEOApp();

        m_CWApp.getSML().addActionListener(this);
        m_CWApp.getSML().removeActionListener(m_CWApp);

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ceew_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel centerPanel = new GridBagPanel();

        String fontName = (String)details.get("FontName");
        int fontType = ((Integer)details.get("FontType")).intValue();
        int fontSize = ((Integer)details.get("FontSize")).intValue();

        setFont(new Font(fontName,fontType,fontSize));

        int counter = 1;
        String str = (String)details.get("Message");
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
    
        String cont = (String)details.get("Continue");
        if (cont.equals("Client"))
            {
            m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ceew_ready"));
            m_ReadyButton.addActionListener(this);
            centerPanel.constrain(m_ReadyButton,1,counter,4,1,GridBagConstraints.CENTER);
            }

        add(centerPanel);
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
                m_CWApp.setWatcher(false);
                dispose();
                }
            }

        if (e.getSource() instanceof ExptMessage)
            {
            synchronized(m_CWApp.getSML())
                {
                ExptMessage em = (ExptMessage)e.getSource();

                if (em instanceof ShutdownExptReqMsg)
                    {
                    m_CWApp.setWatcher(false);
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
        m_EOApp.initializeLabels("girard/sc/expt/awt/ceew.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/ceew.txt");
        }
    }
