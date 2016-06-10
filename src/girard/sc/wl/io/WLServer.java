package girard.sc.wl.io;

import girard.sc.io.Server;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * The base server class for JAVA applications dealing with Web-Lab.
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
*/

public abstract class WLServer extends Server 
    {
/**
 * Used in the run method for stopping it gracefully.
 *
 */ 
    protected boolean m_flag = true;
/**
 * Tells how many WLServer class objects are running.
 *
 */
    static protected int m_instances = 0;
/**
 * Is a lookup table of all the databases.  Each database is assigned a name
 * which acts as its key value in the Hashtable.
 *
 */
    static protected Hashtable m_dbConnections = new Hashtable();
/**
 * The name of the jdbc driver being used.
 *
 */
    static protected String m_jdbcDriver = new String("com.jnetdirect.jsql.JSQLDriver");

/**
 * The constructor for the WLServer.
 *
 * @param name The name of the WLServer being created.
 * @param threadGroup The name of the ThreadGroup for the WLServer.
 * @param databases The list of databases to add to m_dbConnections.
 */
    public WLServer(String name, String threadGroup, int port, Hashtable databases) 
        {
        // Create our server thread with a name.
        super(name,threadGroup,port); 

        Enumeration enm = databases.keys();
        while (enm.hasMoreElements())
            {
            String str = (String)enm.nextElement();

            if (!m_dbConnections.containsKey(str))
                {
                Hashtable con = (Hashtable)databases.get(str);

                m_dbConnections.put(str,con);
                }
            }
        
        m_instances++;
        }

/**
 * Loads in the jdbc class files needed to do queries and updates.
 *
 */
    public void activateJDBC()
        {
        try 
            {
            Class.forName(m_jdbcDriver).newInstance();
            }
        catch (InstantiationException e) {  e.printStackTrace(); } 
        catch (IllegalAccessException e) { e.printStackTrace(); } 
        catch (ClassNotFoundException e) 
            {
            System.err.println(
                "I could not find the JDBC classes. Did you install\n" +
                "them as directed in the README file?");
            e.printStackTrace();
            }
        }

    public void addDB(String key, Hashtable db)
        {
        m_dbConnections.put(key,db);
        }

/**
 * Gets the address information for a given database from m_dbConnections.
 *
 * @param name The name of the database you want connection informatin for.
 * @return The Hashtable containing the needed information, or null if no information
           for that database is found.
 */
    public Hashtable getDBAddress(String name)
        {
        Hashtable db = null;

        if (m_dbConnections.containsKey(name))
            {
            db = (Hashtable)m_dbConnections.get(name);
            }
        
        return db;
        }

    public abstract void run();

    public void setFlag(boolean value)
        {
        m_flag = value;
        }
    public void setJDBCDriver(String str)
        {
        m_jdbcDriver = str;
        }

/**
 * Used to stop the WLServer.
 *
 */
    public void shutdown()
        {
        m_flag = false;

        m_log.shutdown();

        try { m_socket.close(); }
        catch (Exception e) { }
        }
    } 

