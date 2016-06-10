package girard.sc.cc.obj;
/* 
   This object stores the sanction output data for a CCNetworkAction.

   Author: Dudley Girard
   Started: 7-25-2001
*/

import girard.sc.expt.obj.DataOutputObject;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;

public class CCSanctionsOutputObject extends DataOutputObject implements Serializable
    {
    int     m_exptID;  /* Unique identifier for this experiment. */
    int     m_actionIndex;
    int     m_round;
    int     m_fromNode;
    int     m_toNode;
    int     m_toAmt;
    boolean m_offerType;  /* false -> Sanction or true -> Reward */
    
    
    public CCSanctionsOutputObject ()
        {
        }
    public CCSanctionsOutputObject(int exptID, int ai, int r, int fn, int tn, int ta, boolean off)
        {
        m_exptID = exptID;
        m_actionIndex = ai;
        m_round = r;
        m_fromNode = fn;
        m_toNode = tn;
        m_toAmt = ta;
        m_offerType = off;
        }
    public CCSanctionsOutputObject(ResultSet rs)
        {
        try
            {
            m_exptID = rs.getInt("Expt_Out_ID_INT");
            m_actionIndex = rs.getInt("Action_Index_INT");
            m_round = rs.getInt("Round_INT");
            m_fromNode = rs.getInt("From_Node_INT");
            m_toNode = rs.getInt("To_Node_INT");
            m_toAmt = rs.getInt("To_Node_Amt_INT");
            m_offerType = rs.getBoolean("Offer_Type_BIT");
            }
        catch(Exception e) 
            {
            }
        }

    public void formatInsertStatement(CallableStatement cs) throws java.sql.SQLException 
        {
        cs.setInt(1,m_exptID);
        cs.setInt(2,m_actionIndex);
        cs.setInt(3,m_round);
        cs.setInt(4,m_fromNode);
        cs.setInt(5,m_toNode);
        cs.setInt(6,m_toAmt);
        cs.setBoolean(7,m_offerType);
        }

    
    public int getActionIndex()
        {
        return m_actionIndex;
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
    // Expt_Out_ID_INT, Action_Index, Round_INT, From_Node_INT, To_Node_INT, To_Node_Amt_INT, Offer_Type_BIT
        return new String("{call up_insert_JCCExptSanctionData (?, ?, ?, ?, ?, ?, ?)}");
        }
    public boolean getOfferType()
        {
        return m_offerType;
        }
    public int getRound()
        {
        return m_round;
        }
    public int getToAmt()
        {
        return m_toAmt;
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
    public void setOfferType(boolean value)
        {
        m_offerType = value;
        }
    public void setRound(int value)
        {
        m_round = value;
        }
    public void setToAmt(int value)
        {
        m_toAmt = value;
        }
    public void setToNode(int value)
        {
        m_toNode = value;
        }
    }
