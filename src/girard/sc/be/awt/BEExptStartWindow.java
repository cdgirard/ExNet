package girard.sc.be.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.be.io.msg.BEStartNetworkActionReqMsg;
import girard.sc.be.obj.BENetwork;
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

public class BEExptStartWindow extends Frame implements ActionListener
    {
    BENetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    BENetwork m_network;
    
    Button m_ReadyButton;

    public BEExptStartWindow(BENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();
        m_network = m_NACWApp.getNetwork();

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("beesw_title"));
        setFont(m_EOApp.getMedWinFont());
        setBackground(m_EOApp.getWinBkgColor());

        GridBagPanel centerPanel = new GridBagPanel();

        Hashtable windowSettings = (Hashtable)m_network.getExtraData("InitialWindow");

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
            m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("beesw_ready"));
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
                m_NACWApp.setMessageLabel("Please wait while others are reading.");
                BEStartNetworkActionReqMsg tmp = new BEStartNetworkActionReqMsg(null);
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
        m_EOApp.initializeLabels("girard/sc/be/awt/beesw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/be/awt/beesw.txt");
        }
    }
