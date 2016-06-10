package girard.sc.io;

/* This is used to run a thread to periodically save log information
   to a log file.

   Author: Dudley Girard
   Started: 9-25-2001
*/

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;

public class ActivityLogger 
    {
    protected RandomAccessFile m_log;
    protected StringBuffer m_logData = new StringBuffer("");
    protected String m_fileName = "Base";

    // Create a ServerSocket to listen for connections on;  start the thread.
    public ActivityLogger(String Sname) 
        {
        // Create our server thread with a name.
        m_fileName = Sname;
        }

    public synchronized void addMessage(String str)
        {
        m_logData.append(str+" - "+Calendar.getInstance().getTime()+"\n");

        updateLog();
        }
    public synchronized void addMessage(Exception e)
        {
        StackTraceElement[] errors = e.getStackTrace();
        m_logData.append("Error - "+Calendar.getInstance().getTime()+"\n");
        m_logData.append("Message: "+e.getMessage()+"\n");
        for (int i=0;i<errors.length;i++)
            {
            m_logData.append(errors[i].toString()+"\n");
            }

        updateLog();
        }

    // Exit with an error message, when an exception occurs.
    public static void fail(Exception e, String msg) 
        {
        System.err.println(msg + ": " +  e);
        System.exit(1);
        }

    public synchronized void updateLog()
        {
        if (m_logData.length() > 0)
            {
            try
                {
                m_log = new RandomAccessFile("log\\" + m_fileName + ".log", "rw");
                m_log.seek(m_log.length());
                m_log.writeBytes(m_logData.toString());
                m_logData = new StringBuffer("");
                m_log.close();
                }
            catch (IOException ioe)
                {
                System.err.println(ioe);
                ioe.printStackTrace();
                }
            }
        }  

    public void shutdown()
        {
        updateLog();
        }
    } 

