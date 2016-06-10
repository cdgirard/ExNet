package girard.sc.expt.obj;

import java.io.Serializable;

/**
 * This class object was designed based on the information needed by the Experiment
 * class object for displaying information on subjects or observers
 * in the database.  This information is retrieved from the Web-Lab database.
 *
 * @author Dudley Girard
 * @version ExNet III 3.3
 * @since JDK1.1
 * @see girard.sc.expt.obj.Experiment
 */

public class ExptUserData implements Serializable,Cloneable
    {
/**
 * The given first name of the person this represents.
 */
    protected String	m_FirstName = new String("none");
/**
 * The middle initial of the person this represents.
 */
    protected String	m_Mi = new String("-");
/**
 * The given last name of the person this represents.
 */
    protected String    m_LastName = new String("none");

/**
 * A constructor for the object.
 */
    public ExptUserData()
        {
        }
/**
 * A constructor for the object.
 *
 * @param f The value for m_FirstName.
 * @param m The value for m_Mi.
 * @param l The value for m_LastName.
 */
    public ExptUserData(String f, String m, String l)
        {
        m_FirstName = f;
        m_Mi = m;
        m_LastName = l;
        }

/**
 * Creates a clone of the object.
 *
 * @return Returns a ExptUserData class object that is a copy.
 */
    public Object clone()
        {
        ExptUserData tmp = new ExptUserData();

        if (m_FirstName == null)
            tmp.setFirstName(null);
        else
            tmp.setFirstName(new String(m_FirstName));

        if (m_Mi == null)
            tmp.setMi(null);
        else
            tmp.setMi(new String(m_Mi));

        if (m_LastName == null)
            tmp.setLastName(null);
        else
            tmp.setLastName(new String(m_LastName));

        return tmp;
        }

/**
 * @return Returns the value of m_FirstName.
 */
    public String getFirstName()
        {
        return m_FirstName;
        }
/**
 * @return Returns the value of m_LastName.
 */
    public String getLastName()
        {
        return m_LastName;
        }
/**
 * @return Returns the value of m_Mi.
 */
    public String getMi()
        {
        return m_Mi;
        }

/**
 * Sets the value for m_FirstName.
 *
 * @param str The new value to set m_FirstName to.
 */
    public void setFirstName(String str)
        {
        m_FirstName = str;
        }
/**
 * Sets the value for m_LastName.
 *
 * @param str The new value to set m_LastName to.
 */
    public void setLastName(String str)
        {
        m_LastName = str;
        }
/**
 * Sets the value for m_Mi.
 *
 * @param str The new value to set m_Mi to.
 */
    public void setMi(String str)
        {
        m_Mi = str;
        }
    }