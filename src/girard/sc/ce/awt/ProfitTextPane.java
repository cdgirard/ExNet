package girard.sc.ce.awt;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * 
 * <p>
 * <br> Started: 03-21-2003
 * <p>
 *
 * @author Dudley Girard
 * @since JDK1.4 
 */

public class ProfitTextPane extends JTextPane
    {

    public ProfitTextPane()
        {
        super();
        }

    public void addString(String str)
        {
        StyledDocument newText = getStyledDocument();
        try
            {
            newText.insertString(newText.getLength(),str,getStyle("labels"));
            }
        catch (BadLocationException ble)
            {
            System.err.println("Couldn't insert text.");
            }
        }
    public void addProfit(String id, int profit)
        {
        StyledDocument newText = getStyledDocument();
        try
            {
            if (profit > 0)
                {
                newText.insertString(newText.getLength(),id+": "+profit+"\n",getStyle("gain"));
                }
            else
                {
                newText.insertString(newText.getLength(),id+": "+profit+"\n",getStyle("loss"));
                }
            }
        catch (BadLocationException ble)
            {
            System.err.println("Couldn't insert profit.");
            }
        }

    public void clearText()
        {
        StyledDocument newText = getStyledDocument();
        try
            {
            newText.remove(0,newText.getLength());
            }
        catch (BadLocationException ble)
            {
            System.err.println("Couldn't remove text.");
            }
        }

    public void initializeStyles()
        {
        Style d = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = addStyle("regular",d);
        StyleConstants.setFontFamily(d,"Monospaced");
        StyleConstants.setBold(d,true);
        StyleConstants.setFontSize(regular,14);

        Style s = addStyle("gain",regular);
        StyleConstants.setForeground(s,CEColor.edgeGreen);

        s = addStyle("loss",regular);
        StyleConstants.setForeground(s,Color.red);

        s = addStyle("labels",regular);
        StyleConstants.setForeground(s,Color.black);
        StyleConstants.setUnderline(s,true);
        }

    
    }