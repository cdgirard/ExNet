package girard.sc.awt;

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * Creates a TextField set to not editable that can be so that it's background
 * color can be changed.  A KeyListener is then used so that the ColorTextField
 * is editable.
 * <p>
 * <br> Started: 1999
 * <br> Modified: 5-29-1999
 * <br> Modified: 10-15-2002
 * <p>
 *
 * @author Dudley Girard
 * @version SC AWT Toolkit
 * @since JDK1.1 
 */

public class ColorTextField extends TextField implements KeyListener
    {

/**
 * Constructs a TextField that is not editable, but then adds a KeyListener
 * so that it actually is.
 *
 * @param n The width of the ColorTextField.
 */
    public ColorTextField(int n)
        {
        super(n);

        setEditable(false);
        addKeyListener(this);
        }
/**
 * Constructs a TextField that is not editable, but then adds a KeyListener
 * so that it actually is.
 *
 * @param str The initial String to display in the ColorTextField.
 */
    public ColorTextField(String str)
        {
        super(str);
        
        setEditable(false);
        addKeyListener(this);
        }
/**
 * Constructs a TextField that is not editable, but then adds a KeyListener
 * so that it actually is.
 *
 * @param n The width of the ColorTextField.
 * @param str The initial String to display in the ColorTextField.
 */
    public ColorTextField(String str, int n)
        {
        super(str,n);

        setEditable(false);
        addKeyListener(this);
        }

/**
 * Part of the KeyListener implementation.  Is not used.
 *
 * @param e The KeyEvent that trigger this function.
 */
    public void keyReleased(KeyEvent e) {}

/**
 * Part of the KeyListener implementation.  This is where the text of
 * the ColorTextField gets updated such that it is editable.  Not all 
 * editting or keyboard functions of the TextField class are supported.
 * Presently setup to handle the following special keys: 
 * <br>KeyEvent.VK_BACK_SPACE
 * <br>KeyEvent.VK_TAB
 * <br>KeyEvent.VK_DELETE
 * <br>KeyEvent.VK_LEFT
 * <br>KeyEvent.VK_RIGHT
 * <br>KeyEvent.VK_ENTER -> Triggers an ActionEvent.
 * <br>KeyEvent.VK_SHIFT
 * <br>KeyEvent.VK_ALT
 * <br>KeyEvent.VK_CONTROL
 *
 * @param e The KeyEvent that trigger this function.
 */
    public void keyPressed(KeyEvent e)  
        {
        if (e.getSource() instanceof TextField)
            {
            TextField theSource = (TextField)e.getSource();
            String str = new String(theSource.getText());
            StringBuffer strBuff = new StringBuffer(theSource.getText());
            char[] tmp = {e.getKeyChar()};

            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                {
                if (str.length() > 0)
                    {
                    if (getSelectedText().length() > 0)
                        {
                        int start = getSelectionStart();
                        int end = getSelectionEnd();
                        theSource.setText(str.substring(0,start)+str.substring(end,str.length()));
                        theSource.setCaretPosition(start);
                        }
                    else
                        {
                        int x = theSource.getCaretPosition();
                
                        if (x == str.length())
                            theSource.setText(str.substring(0,str.length()-1));
                        else if (x != 0)
                            theSource.setText(str.substring(0,x-1)+str.substring(x,str.length()));
                        if (x != 0)
                            theSource.setCaretPosition(x-1);
                        }
                    }
                }
            else if (e.getKeyCode() == KeyEvent.VK_TAB)
                return;
            else if (e.getKeyCode() == KeyEvent.VK_DELETE)
                {
                if (str.length() > 0)
                    {
                    if (getSelectedText().length() > 0)
                        {
                        int start = getSelectionStart();
                        int end = getSelectionEnd();
                        theSource.setText(str.substring(0,start)+str.substring(end,str.length()));
                        theSource.setCaretPosition(start);
                        }
                    else
                        {
                        int x = theSource.getCaretPosition();
                
                        if (x == 0)
                            theSource.setText(str.substring(1,str.length()));
                        else if (x != str.length())
                            theSource.setText(str.substring(0,x)+str.substring(x+1,str.length()));
                        theSource.setCaretPosition(x);
                        }
                    }
                }
            else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                {
                }
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                {
                }
            else if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                processActionEvent(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"Enter Key"));
                }
            else if (e.getKeyCode() == KeyEvent.VK_SHIFT)
                {
                }
            else if (e.getKeyCode() == KeyEvent.VK_ALT)
                {
                }
            else if (e.getKeyCode() == KeyEvent.VK_CONTROL)
                {
                }
            else
                { 
                if (getSelectedText().length() > 0)
                    {
                    int start = getSelectionStart();
                    int end = getSelectionEnd();
                    theSource.setText(str.substring(0,start)+str.substring(end,str.length()));
                    theSource.setCaretPosition(start);
                    }
                strBuff = new StringBuffer(theSource.getText());
                int x = theSource.getCaretPosition();
                strBuff.insert(x,tmp); 
                theSource.setText(strBuff.toString());
                theSource.setCaretPosition(x+1);
                }
            }
        }

/**
 * Part of the KeyListener implementation.  Is not used.
 *
 * @param e The KeyEvent that trigger this function.
 */
    public void keyTyped(KeyEvent e) {}
    }