package girard.sc.wl.sql;

import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

public class WLUpdateAppTokenReq extends WLQuery
    { 
    Hashtable m_appToken;

    public WLUpdateAppTokenReq (Hashtable h, WLServerConnection wlsc, WLMessage wlm)
        {
        super("weblabDB", wlsc, wlm);
        m_appToken = h;
        }

    public ResultSet runQuery()
        {
        return null;
        }

    public boolean runUpdate() 
        {
        try 
            {

            synchronized (m_DBA)
                {    
                createConnection();

                // get a Statement object from the Connection
                //
                //  Token UID, AppParameter, AppValue("Yes" or "No"), new token
                Statement stmt = m_DB.createStatement();
 
                ResultSet rs = stmt.executeQuery("SELECT * FROM App_Tokens_T WHERE AppToken = '"+(String)m_appToken.get("AppToken")+"'");
   
                if (rs.next())
                    {
                    m_appToken.put("UID",new Integer(rs.getString("AppValue")));
                    CallableStatement cs = m_DB.prepareCall("{call up_update_JAppToken (?, ?, ?)}");
                    cs.setString(1,(String)m_appToken.get("AppToken"));
                    cs.setString(2,rs.getString("AppValue"));
                    cs.registerOutParameter(3,java.sql.Types.VARCHAR);
                    cs.execute();
                    m_appToken.put("AppToken",cs.getString(3));
                    return true;
                    }
                }
             // closeConnection();
            return false;
               
            }
        catch( Exception e ) 
            {
            m_wlsc.addToLog(e.getMessage());
            closeConnection();
            return false;
            }
        }
    }
