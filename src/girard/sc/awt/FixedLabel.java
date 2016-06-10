package girard.sc.awt;

import java.awt.Label;

/**
 * Creates a label of a fixed size, that uses blank spaces to fill the extra space.
 * Only works properly if a Font with static size for letters is used such as Courier
 * or Monospaced.
 * <p>
 * <br> Started: 1999
 * <br> Modified: 5-29-1999
 * <p>
 * @author Dudley Girard
 * @version SC AWT Toolkit
 * @since JDK1.1 
 */

public class FixedLabel extends Label
    {
/**
 * Constant for telling the Label to diplay any text in the center.
 */
    public final static int CENTER = 0;
/**
 * Constant for telling the Label to diplay any text in the left.
 */
    public final static int LEFT = 1;
/**
 * Constant for telling the Label to diplay any text in the right.
 */
    public final static int RIGHT = 2;

/**
 * How many character spaces does the FixedLabel consist of.
 */
    int m_size;
/**
 * How the text in the label is justfied, can be left, right, or center;
 * defaults to left.
 */
    int m_Orientation = LEFT;

/**
 * The constructor, creates a blank FixedLabel of n spaces in length.
 *
 * @param n The m_size to set the FixedLabel to.
 */
    public FixedLabel(int n)
        {
        super();
        m_size = n;
        this.setText(""); 
        }
/**
 * The constructor, creates a blank FixedLabel of n spaces in length.
 *
 * @param n The m_size to set the FixedLabel to.
 * @param ori What m_Orientation to make the FixedLabel.
 */
    public FixedLabel(int n, int ori)
        {
        super();
        m_size = n;
        m_Orientation = ori;
        this.setText(""); 
        }
/**
 * The constructor, creates a FixedLabel of n spaces in length.
 *
 * @param n The m_size to set the FixedLabel to.
 * @param str The initial String to display with the FixedLabel.
 */
    public FixedLabel(int n, String str)
        {
        super();
        m_size = n;
        this.setText(str); 
        }
/**
 * The constructor, creates a FixedLabel of n spaces in length.
 *
 * @param n The m_size to set the FixedLabel to.
 * @param ori What m_Orientation to make the FixedLabel.
 * @param str The initial String to display with the FixedLabel.
 */
    public FixedLabel(int n, String str, int ori)
        {
        super();
        m_size = n;
        m_Orientation = ori;
        this.setText(str); 
        }

/**
 * Returns the text from the FixedLabel without all the spaces included.
 *
 * @return The text attached to the FixedLabel with the spaces removed.
 */
    public String getFixedText()
        {
        return super.getText().trim();
        }

/**
 * Changes the text currently displayed by the FixedLabel.  If the text is too long
 * it will be truncated.
 *
 * @param str The new String to display in the FixedLabel.
 */
    public void setText(String str)
        {
        super.setText(createFixedString(str));
        }

/**
 * Creates the actual String of text to be displayed by padding it with spaces
 * or truncating it if it is too long.
 *
 * @param str The initial String of text that is to be displayed.
 * @return Returns the modified text that is to be displayed.
 */
    private String createFixedString(String str)
        {
        int m, k;
        StringBuffer hold = new StringBuffer("");

        if (m_size > str.length())
            {
            if (m_Orientation == LEFT)
                {
                hold.append(str);
                m = m_size - str.length();
                for (k=0;k<m;k++)
                    {
                    hold.append(" ");    
                    }
                }
            if (m_Orientation == CENTER)
                {
                m = (m_size - str.length())/2;
                for (k=0;k<m;k++)
                    {
                    hold.append(" ");    
                    }
                hold.append(str);
                m = m_size - (m + str.length());
                for (k=0;k<m;k++)
                    {
                    hold.append(" ");    
                    }
                }
            if (m_Orientation == RIGHT)
                {  
                m = m_size - str.length();
                for (k=0;k<m;k++)
                    {
                    hold.append(" ");    
                    }
                hold.append(str);
                }
            }
        else
            {
            hold.append(str.substring(0,m_size));
            }

        return hold.toString();
        }
    }