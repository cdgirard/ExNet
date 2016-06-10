package girard.sc.awt;

/* Creates a text field set to not editable that can be so that it's background */
/* can be changed and such that only numbers can be placed in it. */
/* or Monospaced.  */
/* Coded By: Dudley Girard */
/* Last Modified: 7-24-2000 */

import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class NumberTextField extends TextField implements KeyListener
    {
    int size;
    boolean m_allowNegative = true;
    boolean m_allowFloat = true;
    boolean m_EditMode = true;

    public NumberTextField(int n)
        {
        super(n);

        setEditable(false);
        addKeyListener(this);
        }
    public NumberTextField(String str)
        {
        super(str);
        
        setEditable(false);
        addKeyListener(this);
        }

    public NumberTextField(String str, int n)
        {
        super(str,n);

        setEditable(false);
        addKeyListener(this);
        }

    public int getIntValue()
        {
        Double d = new Double(getText());
     
        return (int)d.doubleValue();
        }
    public float getFloatValue()
        {
        Double d = new Double(getText());
     
        return (float)d.doubleValue();
        }
    public double getDoubleValue()
        {
        Double d = new Double(getText());
     
        return d.doubleValue();
        }

    public void keyReleased(KeyEvent e) {}

    public void keyPressed(KeyEvent e)  
        {
        if (!m_EditMode)
            return;

        if (e.getSource() instanceof TextField)
            {
            TextField theSource = (TextField)e.getSource();
            String str = new String(theSource.getText());
            char[] tmp = {e.getKeyChar()};

            if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE)
                {
                if (str.length() > 0)
                    {
                    int x = theSource.getCaretPosition();
                
                    StringBuffer testNum = new StringBuffer(getText()); // This is the test string to see if it is going to be a valid number.
                    if (x == str.length())
                        {
                        testNum = new StringBuffer(str.substring(0,str.length()-1));
                        }
                    else if (x != 0)
                        {
                        testNum = new StringBuffer(str.substring(0,x-1)+str.substring(x,str.length()));
                        }
                    if (!validNumber(testNum))
                        return;

                    if (x == str.length())
                        theSource.setText(str.substring(0,str.length()-1));
                    else if (x != 0)
                        theSource.setText(str.substring(0,x-1)+str.substring(x,str.length()));
                    if (x != 0)
                        theSource.setCaretPosition(x-1);
                    }
                }
            else if (e.getKeyCode() == KeyEvent.VK_TAB)
                return;
            else if (e.getKeyCode() == KeyEvent.VK_DELETE)
                {
                if (str.length() > 0)
                    {
                    int x = theSource.getCaretPosition();
                
                    StringBuffer testNum = new StringBuffer(getText()); // This is the test string to see if it is going to be a valid number.
                    if (x == 0)
                        {
                        testNum = new StringBuffer(str.substring(1,str.length()));
                        }
                    else if (x != str.length())
                        {
                        testNum = new StringBuffer(str.substring(0,x)+str.substring(x+1,str.length())); 
                        }

                    if (!validNumber(testNum))
                        return;


                    if (x == 0)
                        theSource.setText(str.substring(1,str.length()));
                    else if (x != str.length())
                        theSource.setText(str.substring(0,x)+str.substring(x+1,str.length()));
                    theSource.setCaretPosition(x);
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
                StringBuffer testNum = new StringBuffer(""); // This is the test string to see if it is going to be a valid number.
                if (getSelectedText().length() > 0)
                    {
                    int start = getSelectionStart();
                    int end = getSelectionEnd();
                    testNum = new StringBuffer(str.substring(0,start)+tmp[0]+str.substring(end,str.length()));
                    }
                else
                    {
                    testNum = new StringBuffer(theSource.getText());
                    int x = theSource.getCaretPosition();
                    testNum.insert(x,tmp); 
                    }
                if (!validNumber(testNum))
                    return;
                    
       // The user has selected a section of text that is to be replaced by the
       // typed in character.
                if (getSelectedText().length() > 0)
                    {
                    int start = getSelectionStart();
                    int end = getSelectionEnd();
                    theSource.setText(str.substring(0,start)+str.substring(end,str.length()));
                    theSource.setCaretPosition(start);
                    }
                StringBuffer strBuff = new StringBuffer(theSource.getText());
                int x = theSource.getCaretPosition();
                strBuff.insert(x,tmp); 
                theSource.setText(strBuff.toString());
                theSource.setCaretPosition(x+1);
                }
            }
        }

    public void keyTyped(KeyEvent e) {}

    public void setAllowFloat(boolean value)
        {
        m_allowFloat = value;
        }
    public void setAllowNegative(boolean value)
        {
        m_allowNegative = value;
        }
    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }

    private boolean validNumber(StringBuffer testNum)
        {
        try
            {
            if (m_allowFloat)
                {
                Double tmpDbl = new Double(""+testNum.toString());
                if ((!m_allowNegative) && (tmpDbl.doubleValue() < 0))
                    return false;
                }
            else
                {
                Integer tmpInt = new Integer(""+testNum.toString());
                if ((!m_allowNegative) && (tmpInt.intValue() < 0))
                    return false;
                }
            }
        catch(NumberFormatException nfe) { return false; }

        return true;
        }
    }