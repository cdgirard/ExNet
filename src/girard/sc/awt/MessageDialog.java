package girard.sc.awt;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MessageDialog extends Dialog implements ActionListener
    {
    GridBagPanel m_MessagePanel = new GridBagPanel();
    Font m_DialogFont = new Font("Monospaced",Font.BOLD,16);
    Font m_MessageFont = new Font("Monospaced",Font.PLAIN,16);
    Button m_MessageOK = new Button("OK");

    public MessageDialog(String ErrMsg)
        {
        super(new Frame(),"Message Dialog",true);
 
        Label tmpLabel;
        
        setFont(m_DialogFont);

        tmpLabel = new Label(ErrMsg);
        tmpLabel.setFont(m_MessageFont);
        m_MessagePanel.constrain(tmpLabel,1,2,10,1);

        m_MessageOK.addActionListener(this);
        m_MessagePanel.constrain(m_MessageOK,1,3,10,1,GridBagConstraints.CENTER);

        add(m_MessagePanel);
        pack();
        show();
        }

    public MessageDialog(String[] ErrMsg)
        {
        super(new Frame(),"Message Dialog",true);
 
        Label tmpLabel;
        int i;
        
        setFont(m_DialogFont);

        for (i=0;i<ErrMsg.length;i++)
            {
            tmpLabel = new Label(ErrMsg[i]);
            tmpLabel.setFont(m_MessageFont);
            m_MessagePanel.constrain(tmpLabel,1,i+2,10,1);
            }

        m_MessageOK.addActionListener(this);
        m_MessagePanel.constrain(m_MessageOK,1,ErrMsg.length+2,10,1,GridBagConstraints.CENTER);

        add(m_MessagePanel);
        pack();
        show();
        }

    public MessageDialog(String ErrMsg, Font f)
        {
        super(new Frame(),"Message Dialog",true);
 
        Label tmpLabel;
        
        m_MessageFont = f;

        setFont(m_DialogFont);

        tmpLabel = new Label(ErrMsg);
        tmpLabel.setFont(m_MessageFont);
        m_MessagePanel.constrain(tmpLabel,1,2,10,1);

        m_MessageOK.addActionListener(this);
        m_MessagePanel.constrain(m_MessageOK,1,3,10,1,GridBagConstraints.CENTER);

        add(m_MessagePanel);
        pack();
        show();
        }

    public MessageDialog(String[] ErrMsg, Font f)
        {
        super(new Frame(),"Message Dialog",true);
 
        Label tmpLabel;
        int i;
        
        m_MessageFont = f;

        setFont(m_DialogFont);

        for (i=0;i<ErrMsg.length;i++)
            {
            tmpLabel = new Label(ErrMsg[i]);
            tmpLabel.setFont(m_MessageFont);
            m_MessagePanel.constrain(tmpLabel,1,i+1,1,1);
            }

        m_MessageOK.addActionListener(this);
        m_MessagePanel.constrain(m_MessageOK,1,ErrMsg.length+1,1,1,GridBagConstraints.CENTER);

        add(m_MessagePanel);
        pack();
        show();
        }

    public void actionPerformed (ActionEvent e)
        {
        Button theSource = (Button)e.getSource();
       
        if (theSource == m_MessageOK)
            {
            // Handle OK
            dispose();
            }
        }
    }
