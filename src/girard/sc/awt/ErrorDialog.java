package girard.sc.awt;

import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Basic Dialog window used to display an error message.  Goes away when the
 * button attached to the window is clicked.  Set so that have to click the
 * button to continue.
 * <p>
 * <br> Started: 2001
 * <br> Modified: 10-18-2002
 * <p>
 *
 * @author Dudley Girard
 * @version SC AWT Toolkit
 * @since JDK1.1 
 */

public class ErrorDialog extends Dialog implements ActionListener
    {
/**
 * The panel to place the text field for the error message and
 * the continue button.
 *
 */
    GridBagPanel m_ErrorPanel = new GridBagPanel();
/**
 * The font for the ErrorDialog window.
 *
 */
    Font m_DialogFont = new Font("Monospaced",Font.BOLD,16);
/**
 * The font to display the error message in.
 *
 */
    Font m_ErrorFont = new Font("Monospaced",Font.PLAIN,16);
/**
 * The button used to dispose of the ErrorDialog.
 *
 */
    Button m_ErrorOK = new Button("OK");

/**
 * Creates an ErrorDialog.
 *
 * @param ErrMsg The error message to display in the ErrorDialog.
 */
    public ErrorDialog(String ErrMsg)
        {
        super(new Frame(),"Error Dialog",false);
 
        Label tmpLabel;
        
        setFont(m_DialogFont);
        setLayout(new GridLayout(1,1));
        setBackground(Color.lightGray);

        m_ErrorPanel.constrain(new Label("Error Message"),1,1,10,1);

        tmpLabel = new Label(ErrMsg);
        tmpLabel.setFont(m_ErrorFont);
        m_ErrorPanel.constrain(tmpLabel,1,2,10,1);

        m_ErrorOK.addActionListener(this);
        m_ErrorPanel.constrain(m_ErrorOK,1,3,10,1,GridBagConstraints.CENTER);

        add(m_ErrorPanel);
        pack();
        show();
        validate();
        }

/**
 * Creates an ErrorDialog.
 *
 * @param ErrMsg The arrary of error messages to display in the ErrorDialog.
 */
    public ErrorDialog(String[] ErrMsg)
        {
        super(new Frame(),"Error Dialog",true);
 
        Label tmpLabel;
        int i;
        
        setFont(m_DialogFont);
        setLayout(new GridLayout(1,1));
        m_ErrorPanel.constrain(new Label("Error Message"),1,1,10,1);

        for (i=0;i<ErrMsg.length;i++)
            {
            tmpLabel = new Label(ErrMsg[i]);
            tmpLabel.setFont(m_ErrorFont);
            m_ErrorPanel.constrain(tmpLabel,1,i+2,10,1);
            }

        m_ErrorOK.addActionListener(this);
        m_ErrorPanel.constrain(m_ErrorOK,1,ErrMsg.length+2,10,1,GridBagConstraints.CENTER);

        add(m_ErrorPanel);
        pack();
        show();
        }

/**
 * Creates an ErrorDialog.
 *
 * @param ErrMsg The error message to display in the ErrorDialog.
 * @param f The font to display the ErrMsg in.
 */
    public ErrorDialog(String ErrMsg, Font f)
        {
        super(new Frame(),"Error Dialog",true);
 
        Label tmpLabel;
        
        m_ErrorFont = f;

        setFont(m_DialogFont);
        setLayout(new GridLayout(1,1));
        m_ErrorPanel.constrain(new Label("Error Message"),1,1,10,1);

        tmpLabel = new Label(ErrMsg);
        tmpLabel.setFont(m_ErrorFont);
        m_ErrorPanel.constrain(tmpLabel,1,2,10,1);

        m_ErrorOK.addActionListener(this);
        m_ErrorPanel.constrain(m_ErrorOK,1,3,10,1,GridBagConstraints.CENTER);

        add(m_ErrorPanel);
        pack();
        show();
        }

/**
 * Creates an ErrorDialog.
 *
 * @param ErrMsg The array of error messages to display in the ErrorDialog.
 * @param f The font to display the ErrMsg in.
 */
    public ErrorDialog(String[] ErrMsg, Font f)
        {
        super(new Frame(),"Error Dialog",true);
 
        Label tmpLabel;
        int i;
        
        m_ErrorFont = f;

        setFont(m_DialogFont);
        setLayout(new GridLayout(1,1));
        m_ErrorPanel.constrain(new Label("Error Message"),1,1,10,1);

        for (i=0;i<ErrMsg.length;i++)
            {
            tmpLabel = new Label(ErrMsg[i]);
            tmpLabel.setFont(m_ErrorFont);
            m_ErrorPanel.constrain(tmpLabel,1,i+1,1,1);
            }

        m_ErrorOK.addActionListener(this);
        m_ErrorPanel.constrain(m_ErrorOK,1,ErrMsg.length+1,1,1,GridBagConstraints.CENTER);

        add(m_ErrorPanel);
        pack();
        show();
        }

/**
 * Processes the ActionEvents.  In this case just processes the m_ErrorOK button
 * being pressed.
 *
 * @param e The ActionEvent that occurred.
 */
    public void actionPerformed (ActionEvent e)
        {
        Button theSource = (Button)e.getSource();
       
        if (theSource == m_ErrorOK)
            {
            // Handle OK
            dispose();
            }
        }
    }
