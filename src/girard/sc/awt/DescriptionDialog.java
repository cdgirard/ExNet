package girard.sc.awt;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Basic Dialog window used to display some text.  Goes away when the
 * button attached to the window is clicked.  Does not have to be in
 * the forefront.
 * <p>
 * <br> Started: 2001
 * <br> Modified: 10-17-2002
 * <p>
 *
 * @author Dudley Girard
 * @version SC AWT Toolkit
 * @since JDK1.1 
 */

public class DescriptionDialog extends Dialog implements ActionListener
    {
/**
 * The button used to dispose of the DescriptionDialog.
 *
 */ 
    Button m_MessageOK = new Button("OK");

/**
 * Creates a DescriptionDialog.
 *
 * @param Msg The message to display in the DescriptionDialog.
 */
    public DescriptionDialog(String Msg)
        {
        super(new Frame(),"Description Dialog",true);
 
        TextArea tmpArea;
        
        setLayout(new GridLayout(1,1));
        setFont(new Font("Monospaced",Font.BOLD,14));

        GridBagPanel messagePanel = new GridBagPanel();

        tmpArea = new TextArea(Msg,10,40,TextArea.SCROLLBARS_NONE);
        tmpArea.setFont(new Font("Monospaced",Font.PLAIN,14));
        tmpArea.setEditable(false);
        messagePanel.constrain(tmpArea,1,1,4,4);

        m_MessageOK.addActionListener(this);
        messagePanel.constrain(m_MessageOK,1,5,4,1,GridBagConstraints.CENTER);

        add(messagePanel);
        pack();
        show();
        }

/**
 * Creates a DescriptionDialog.
 *
 * @param Msg The message to display in the DescriptionDialog.
 * @param f The font to display the message in.
 */
    public DescriptionDialog(String Msg, Font f)
        {
        super(new Frame(),"Description Dialog",true);
 
        TextArea tmpArea;

        setFont(new Font("Monospaced",Font.BOLD,14));
        setLayout(new GridLayout(1,1));

        GridBagPanel messagePanel = new GridBagPanel();

        tmpArea = new TextArea(Msg,10,40,TextArea.SCROLLBARS_NONE);
        tmpArea.setFont(f);
        tmpArea.setEditable(false);
        messagePanel.constrain(tmpArea,1,1,4,4);

        m_MessageOK.addActionListener(this);
        messagePanel.constrain(m_MessageOK,1,5,4,1,GridBagConstraints.CENTER);

        add(messagePanel);
        pack();
        show();
        }

/**
 * Processes the ActionEvents.  In this case just processes the m_MessageOK button
 * being pressed.
 *
 * @param e The ActionEvent that occurred.
 */
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
