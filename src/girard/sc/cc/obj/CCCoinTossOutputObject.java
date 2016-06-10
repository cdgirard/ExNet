package girard.sc.cc.obj;
/* 
   This object stores the coin toss for a set of token output data 
   for a CCNetworkAction.

   Author: Dudley Girard
   Started: 7-25-2001
*/
import girard.sc.expt.obj.DataOutputObject;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;

public class CCCoinTossOutputObject extends DataOutputObject implements Serializable
    {
    int     m_exptID;  /* Unique identifier for this experiment. */
    int     m_actionIndex;
    int     m_fromNode;
    int     m_toNode;
    int     m_coinToss;  // Total number of tokens sent so far, counting the one sent this round.
    
    
    public CCCoinTossOutputObject ()
        {
        }
    public CCCoinTossOutputObject(int exptID, int ai, int fn, int tn, int ct)
        {
        m_exptID = exptID;
        m_actionIndex = ai;
        m_fromNode = fn;
        m_toNode = tn;
        m_coinToss = ct;
        }
    public CCCoinTossOutputObject(ResultSet rs)
        {
        try
            {
            m_exptID = rs.getInt("Expt_Out_ID_INT");
            m_actionIndex = rs.getInt("Action_Index_INT");
            m_fromNode = rs.getInt("From_Node_INT");
            m_toNode = rs.getInt("To_Node_INT");
            m_coinToss = rs.getInt("Coin_Toss_INT");
            }
        catch(Exception e) 
            {
            }
        }

    public void formatInsertStatement(CallableStatement cs) throws java.sql.SQLException 
        {
        cs.setInt(1,m_exptID);
        cs.setInt(2,m_actionIndex);
        cs.setInt(3,m_fromNode);
        cs.setInt(4,m_toNode);
        cs.setInt(5,m_coinToss);
        }

    
    public int getActionIndex()
        {
        return m_actionIndex;
        }
    public int getCoinToss()
        {
        return m_coinToss;
        }
    public int getExptID()
        {
        return m_exptID;
        }
    public int getFromNode()
        {
        return m_fromNode;
        }
    public String getInsertFormat()
        {
    // Expt_Out_ID_INT, Action_Index, From_Node_INT, To_Node_INT, Coin_Toss_INT
        return new String("{call up_insert_JCCExptCoinTossData (?, ?, ?, ?, ?)}");
        }
    public int getToNode()
        {
        return m_toNode;
        }
    
    
    public void setActionIndex(int value)
        {
        m_actionIndex = value;
        }
    public void setExptID(int value)
        {
        m_exptID = value;
        }
    public void setFromNode(int value)
        {
        m_fromNode = value;
        }
    public void setToNode(int value)
        {
        m_toNode = value;
        }
    public void setCoinToss(int value)
        {
        m_coinToss = value;
        }
    }
