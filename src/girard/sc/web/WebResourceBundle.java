package girard.sc.web;

import java.util.Hashtable;

/**
 * Used to store lists of strings based on language.  Allows you to store a word or
 * phrase in one or more languages.  I have no clue yet how to implement it so that
 * it can use special characters outside the roman alphabet.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 */

public class WebResourceBundle
    {
/**
 * The Key under which to store all English labels.
 */
    public static String ENGLISH = "English";
/**
 * The Key under which to store all French labels.
 */
    public static String FRENCH = "French";
/**
 * The Key under which to store all German labels.
 */
    public static String GERMAN = "German";
/**
 * The Key under which to store all Russian labels.
 */
    public static String RUSSIAN = "Russian";
/**
 * The Key under which to store all Dutch labels.
 */
    public static String DUTCH = "Dutch";
/**
 * The Key under which to store all Spainish labels.
 */
    public static String SPAINISH = "Spainish";
/**
 * The Key under which to store all Chinese labels.
 */
    public static String CHINESE = "Chinese";
/**
 * The Key under which to store all Japanese labels.
 */
    public static String JAPANESE = "Japanese";

/**
 * The present active language Key; Defaults to ENGLISH.
 */
    String m_activeLanguage = ENGLISH;
/**
 * Where all the labels for all the different languages are store;  Contains a seperate
 * Hashtable for each language Key;  Each language Hashtable contains Strings for the
 * labels differientiated by the specific key associated with that String.
 */
    Hashtable m_lang = new Hashtable();

/**
 * The constructor.  Initializes the m_lang by putting in a Hashtable for each
 * language.
 */
    public WebResourceBundle()
        {
        Hashtable labels = new Hashtable();
        m_lang.put(ENGLISH,labels);

        labels = new Hashtable();
        m_lang.put(FRENCH,labels);

        labels = new Hashtable();
        m_lang.put(GERMAN,labels);
   
        labels = new Hashtable();
        m_lang.put(RUSSIAN,labels);
 
        labels = new Hashtable();
        m_lang.put(DUTCH,labels);
 
        labels = new Hashtable();
        m_lang.put(SPAINISH,labels);
  
        labels = new Hashtable();
        m_lang.put(CHINESE,labels);

        labels = new Hashtable();
        m_lang.put(JAPANESE,labels);
        }

/**
 * Adds a new Label to a specified Hashtable in m_lang.
 *
 * @param lang The language Hashtable in m_lang to add the new label to.
 * @param key The key associated with the label.
 * @param label The label associated with the key.
 */
    public void addObjectLabel(String lang, String key, String label)
        {
    	if(key.equalsIgnoreCase("elhp_cesew")){
    		System.err.println("wow!");
    		System.err.println("loading - "+key+":"+label);
    	}
    	if ((lang == null) || (key == null) || (label == null))
            {
            System.err.println("NULL (lang, key, or label) value(s) - Check your label file for errors!!");
            return;
            }
        Hashtable labels = (Hashtable)m_lang.get(lang);
 
        if (labels == null)
            {
            System.err.println("NULL (label) value - Check your label file for errors!!");
            return;
            }

        if (!labels.containsKey(key))
            {
            labels.put(key,label);
            }
        else
            {
            labels.remove(key);
            labels.put(key,label);
            }
        if(key.equals("elhp_cesew"))
        	System.out.println("got out the val:"+labels.get(key)+" for key"+key);
        }

/**
 * @return Returns the value of m_activeLanguague.
 */
    public String getActiveLanguage()
        {
        return m_activeLanguage;
        }
/**
 * NOTE: We don't need the language as it uses the value of m_activeLanguage.  If
 * no entry exists for a key of a language other than English then it attempts to return
 * the label for that key in English.  If no such entry exists then it throws a
 * NullPointerException error.
 *
 * @param key The key value associated with the label to be retrieved.
 * @return Returns the label associated with the key that was passed in to the function.
 */
    public String getObjectLabel(String key)
        {
        Hashtable labels = (Hashtable)m_lang.get(m_activeLanguage);
        Object obj = labels.get(key);
        if(key.equalsIgnoreCase("elhp_cesew"))
        	System.out.println("hee hee");
        
        if (obj == null)
            {
            labels = (Hashtable)m_lang.get("English");
            obj = labels.get(key);
            }
        if (obj == null)
            {
            System.err.println("No such entry in WebResourceBundle for: "+key);
            return new String("-NULL-");
            }
        return (String)obj;
        }

/**
 * Removes a label for a specific language from m_lang.
 *
 * @param lang The language key for the language Hashtable we want to remove a label from.
 * @param key The key value for the label we want to remove.
 */
    public void removeObjectLabel(String lang, String key)
        {
        if ((lang == null) || (key == null))
            {
            System.err.println("NULL (lang or key) value(s) - Check your label file for errors!!");
            return;
            }
        Hashtable labels = (Hashtable)m_lang.get(lang);

        if (labels == null)
            {
            System.err.println("NULL (label) value - Check your label file for errors!!");
            return;
            }
        if(key.equalsIgnoreCase("elhp_cesew"))
            System.out.println("removing the key "+key);	
        if (labels.containsKey(key))
            labels.remove(key);
        }

/**
 * Sets the value of m_activeLanguage.
 *
 * @param str The new value to set m_activeLanguage to.
 */
    public void setActiveLanguague(String str)
        {
        m_activeLanguage = str;
        }     
    }