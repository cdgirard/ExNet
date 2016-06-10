package girard.sc.ce.awt;

import girard.sc.awt.GridBagPanel;
import girard.sc.ce.io.msg.CEStartNetworkActionReqMsg;
import girard.sc.ce.obj.CENetwork;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Button;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JFrame;

/**
 * Displayed to clients at the start of a CE Network Action.
 * <p>
 * <br> Started: 02-06-2003
 * <p>
 * @author Dudley Girard
 */

public class CEExptStartWindow extends JFrame implements ActionListener
    {
    CENetworkActionClientWindow m_NACWApp;
    ExptOverlord m_EOApp;
    CENetwork m_network;
    
    Button m_ReadyButton;

    public CEExptStartWindow(CENetworkActionClientWindow app)
        {
        super();
        m_NACWApp = app;
        m_EOApp = m_NACWApp.getEOApp();
        m_network = m_NACWApp.getNetwork();

        initializeLabels();

        getContentPane().setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("ceesw_title"));
        getContentPane().setFont(m_EOApp.getMedWinFont());
        getContentPane().setBackground(m_EOApp.getWinBkgColor());

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
            m_ReadyButton = new Button(m_EOApp.getLabels().getObjectLabel("ceesw_ready"));
            m_ReadyButton.addActionListener(this);
            centerPanel.constrain(m_ReadyButton,1,counter,4,1,GridBagConstraints.CENTER);
            }

        getContentPane().add(centerPanel);
        pack();
        show();

        // ((JComponent)getContentPane()).repaint();
        }

    public void actionPerformed(ActionEvent e)
        {

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();
        
            if (theSource == m_ReadyButton)
                {
                m_NACWApp.setMessageLabel("Please wait while others are reading.");
                CEStartNetworkActionReqMsg tmp = new CEStartNetworkActionReqMsg(null);
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
        m_EOApp.initializeLabels("girard/sc/ce/awt/ceesw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ce/awt/ceesw.txt");
        }
    }
