package girard.sc.be.obj;
/* 
   Used to limit information that is displayed on the screen.

   Author: Dudley Girard
   Started: 4-30-2000
   Edittd:  1-21-2001
*/

import java.io.Serializable;

public class BEInformationLevel implements Cloneable,Serializable
    {
    public static final int MIN = 1;
    public static final int MAX = 10;
    int    m_maxInfoLevel = 10;
    int    m_infoLevel = 1;

    public BEInformationLevel ()
        {
        }
    
    public Object clone()
        {
        BEInformationLevel beil = new BEInformationLevel();
        beil.setMaxInfoLevel(m_maxInfoLevel);
        beil.setInfoLevel(m_infoLevel);
        return beil;
        }

    public int getInfoLevel()
        {
        return m_infoLevel;
        }
    public int getMaxInfoLevel()
        {
        return m_maxInfoLevel;
        }

    public void setInfoLevel(int value)
        {
        if ((value <= m_maxInfoLevel) && (value >= MIN))
            m_infoLevel = value;
        }
    public void setMaxInfoLevel(int value)
        {
        if ((value <= MAX) && (value >= MIN))
            m_maxInfoLevel = value;
        }
    }
