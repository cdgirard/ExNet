package girard.sc.be.awt;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/** 
 * Creates a JTextField set to not editable so that it's background
 * can be changed and such that only numbers can be placed in it.  Generates
 * action events on any changes, not just the return key being pressed.
 * <p>
 * <br> Started: 02-13-2003
 * <p>
 * @author Dudley Girard
 */

public class BENumberJTextField extends JTextField implements KeyListener
    {
    int size;

    public BENumberJTextField(int n)
        {
        super(n);

        addKeyListener(this);
        }
    public BENumberJTextField(String str)
        {
        super(str);

        addKeyListener(this);
        }

    public BENumberJTextField(String str, int n)
        {
        super(str,n);

        addKeyListener(this);
        }

    protected Document createDefaultModel()
        {
        return new NumbersOnlyDocument();
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


    public void keyReleased(KeyEvent e)
        {
        if (e.getSource() instanceof JTextField)
            {
            JTextField theSource = (JTextField)e.getSource();
            String str = new String(theSource.getText());
            char[] tmp = {e.getKeyChar()};

            if (e.getKeyCode() != KeyEvent.VK_ENTER)
                {
                ActionListener[] als = getActionListeners();

                for (int i=0;i<als.length;i++)
                    {
                    als[i].actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"Text Changed"));
                    }
System.err.println(getText());
                }
            }
        }

    public void keyPressed(KeyEvent e)  
        {
        }

    public void keyTyped(KeyEvent e) {}

    public void setAllowFloat(boolean value)
        {
        NumbersOnlyDocument nod = (NumbersOnlyDocument)getDocument();
        nod.setAllowFloat(value);
        }
    public void setAllowNegative(boolean value)
        {
        NumbersOnlyDocument nod = (NumbersOnlyDocument)getDocument();
        nod.setAllowNegative(value);
        }
    public void setMax(double value)
        {
        NumbersOnlyDocument nod = (NumbersOnlyDocument)getDocument();
        nod.setMax(value);
        }
    public void setMaxAmount(boolean value)
        {
        NumbersOnlyDocument nod = (NumbersOnlyDocument)getDocument();
        nod.setMaxAmount(value);
        }
    public void setMin(double value)
        {
        NumbersOnlyDocument nod = (NumbersOnlyDocument)getDocument();
        nod.setMin(value);
        }
    public void setMinAmount(boolean value)
        {
        NumbersOnlyDocument nod = (NumbersOnlyDocument)getDocument();
        nod.setMinAmount(value);
        }
    }

class NumbersOnlyDocument extends PlainDocument
    {
    boolean m_allowNegative = true;
    boolean m_allowFloat = true;
    boolean m_maxAmount = true;
    boolean m_minAmount = true;
    double m_max = 100;
    double m_min = 0;

    public NumbersOnlyDocument()
        {
        }

    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
        {
        if (str == null)
            {
            return;
            }

        StringBuffer documentText = new StringBuffer(getText(0,getLength()));
        documentText.insert(offs,str);

        if (validNumber(documentText))
            {
            super.insertString(offs, str, a);
            }
        }

    public void setAllowFloat(boolean value)
        {
        m_allowFloat = value;
        }
    public void setAllowNegative(boolean value)
        {
        m_allowNegative = value;
        }
    public void setMax(double value)
        {
        m_max = value;
        }
    public void setMaxAmount(boolean value)
        {
        m_maxAmount = value;
        }
    public void setMin(double value)
        {
        m_min = value;
        }
    public void setMinAmount(boolean value)
        {
        m_minAmount = value;
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
                if ((m_maxAmount) && (tmpDbl.doubleValue() > m_max))
                    return false;
                if ((m_minAmount) && (tmpDbl.doubleValue() < m_min))
                    return false;
                }
            else
                {
                Integer tmpInt = new Integer(""+testNum.toString());
                if ((!m_allowNegative) && (tmpInt.intValue() < 0))
                    return false;
                if ((m_maxAmount) && (tmpInt.intValue() > m_max))
                    return false;
                if ((m_minAmount) && (tmpInt.intValue() < m_min))
                    return false;
                }
            }
        catch(NumberFormatException nfe) { return false; }

        return true;
        }
    }