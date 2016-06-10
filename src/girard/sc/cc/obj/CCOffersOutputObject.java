package girard.sc.cc.obj;
/* 
   This object stores the offer output data for a CCNetworkAction.

   Author: Dudley Girard
   Started: 7-24-2001
*/

import girard.sc.expt.obj.DataOutputObject;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;

public class CCOffersOutputObject extends DataOutputObject implements Serializable
    {
    int     m_exptID;  /* Unique identifier for this experiment. */
    int     m_actionIndex;
    int     m_round;
    int     m_fromNode;
    int     m_toNode;
    double  m_fromAmt;
    double  m_toAmt;
    String  m_offerType;  /* OFR, ACP, CFR, VOTE, etc... */
    int     m_tickTime;
    long    m_realTime;  /* Should be number of seconds since the network round started. */
    
    
    public CCOffersOutputObject ()
        {
        }
    public CCOffersOutputObject(int exptID, int ai, int r, int fn, int tn, double fa, double ta, String off, int tt, long rt)
        {
        m_exptID = exptID;
        m_actionIndex = ai;
        m_round = r;
        m_fromNode = fn;
        m_toNode = tn;
        m_fromAmt = fa;
        m_toAmt = ta;
        m_offerType = off;
        m_tickTime = tt;
        m_realTime = rt;
        }
    public CCOffersOutputObject(ResultSet rs)
        {
        try
            {
            m_exptID = rs.getInt("Expt_Out_ID_INT");
            m_actionIndex = rs.getInt("Action_Index_INT");
            m_round = rs.getInt("Round_INT");
            m_fromNode = rs.getInt("From_Node_INT");
            m_toNode = rs.getInt("To_Node_INT");
            m_fromAmt = rs.getDouble("From_Node_Amt_FLT");
            m_toAmt = rs.getDouble("To_Node_Amt_FLT");
            m_offerType = rs.getString("Offer_Type_VC");
            m_tickTime = rs.getInt("Tick_Time_INT");
            m_realTime = rs.getLong("Real_Time_INT");
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
        cs.setFloat(6,(float)m_fromAmt);
        cs.setFloat(7,(float)m_toAmt);
        cs.setString(8,m_offerType);
        cs.setInt(9,m_tickTime);
        cs.setInt(10,(int)m_realTime);
        }

    public int getActionIndex()
        {
        return m_actionIndex;
        }
    public int getExptID()
        {
        return m_exptID;
        }
    public double getFromAmt()
        {
        return m_fromAmt;
        }
    public int getFromNode()
        {
        return m_fromNode;
        }
    public String getInsertFormat()
        {
    // Expt_Out_ID_INT, Action_Index, Round_INT, From_Node_INT, To_Node_INT, From_Node_Amt_INT, To_Node_Amt_INT, Offer_Type_VC, Tick_Time_INT, Real_Time_INT
        return new String("{call up_insert_JCCExptOfferData (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
        }
    public String getOfferType()
        {
        return m_offerType;
        }
    public long getRealTime()
        {
        return m_realTime;
        }
    public int getRound()
        {
        return m_round;
        }
    public int getTickTime()
        {
        return m_tickTime;
        }
    public double getToAmt()
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
    public void setFromAmt(double value)
        {
        m_fromAmt = value;
        }
    public void setFromNode(int value)
        {
        m_fromNode = value;
        }
    public void setOfferType(String value)
        {
        m_offerType = value;
        }
    public void setRealTime(long value)
        {
        m_realTime = value;
        }
    public void setRound(int value)
        {
        m_round = value;
        }
    public void setTickTime(int value)
        {
        m_tickTime = value;
        }
    public void setToAmt(double value)
        {
        m_toAmt = value;
        }
    public void setToNode(int value)
        {
        m_toNode = value;
        }
    }
