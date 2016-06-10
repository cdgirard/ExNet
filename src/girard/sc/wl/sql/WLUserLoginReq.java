package girard.sc.wl.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.ResultSet;
import java.sql.Statement;

public class WLUserLoginReq extends WLQuery
    { 
    Object[] m_loginArgs; // User Login, User Password, User ID

    public WLUserLoginReq (Object[] loginArgs, WLServerConnection wlsc, WLMessage wlm)
        {
        super("weblabDB", wlsc, wlm);
        m_loginArgs = loginArgs;
        }

    public ResultSet runQuery()
        {
        return null;
        }

/* 
   Does not actually update the database any, but the return value
   type for the function is what is useful.
*/
    public boolean runUpdate() 
        { 
        try 
            {
            ResultSet rs = null;

            synchronized (m_DBA)
                {
                createConnection();

                // get a Statement object from the Connection
                //
                Statement stmt = m_DB.createStatement();
 
                rs = stmt.executeQuery("SELECT * FROM Users_T WHERE User_ID_VC = '"+(String)m_loginArgs[0]+"'");
                
             // closeConnection();
                }
            m_loginArgs[2] = new Integer(-1);

            if (rs.next())
                {
                String realPass = rs.getString("Pword_VC");
                String pass = (String)m_loginArgs[1];
                if (pass.equals(realPass))
                    {
                    m_loginArgs[2] = new Integer(rs.getInt("ID_INT"));
                    return true;
                    }
                else
                    {
                    return false;
                    }
                }
            else
                {
                return false;
                }
            }
        catch( Exception e ) 
            {
            m_wlsc.addToLog(e.getMessage());
            closeConnection();
            return false;
            }
        }
    }
