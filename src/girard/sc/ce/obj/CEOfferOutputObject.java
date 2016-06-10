package girard.sc.ce.obj;

import girard.sc.expt.obj.DataOutputObject;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;

/**
 * A customized data object for saving the offer output data from a CENetworkAction.   
 * <p>
 * <br> Started: 02-21-2003
 * <p>
 * @author Dudley Girard
 */

public class CEOfferOutputObject extends DataOutputObject implements Serializable
    {
    int     m_exptID;  /* Unique identifier for this experiment. */
    int     m_actionIndex;
    int     m_period;
    int     m_round;
    int     m_fromNode;
    int     m_toNode;
    String  m_fromRes;
    double  m_fromResAmt;
    String  m_toRes;
    double  m_toResAmt;
/**
 * Offer, Accept, or Complete.
 */
    String  m_offerType;
    int     m_tickTime;
/**
 * Should be number of seconds since the network round started.
 */
    long    m_realTime;
    
    public CEOfferOutputObject ()
        {
        }
    public CEOfferOutputObject(int exptID, int ai, int p, int r, int fn, int tn, CEResource fa, CEResource ta, String off, int tt, long rt)
        {
        m_exptID = exptID;
        m_actionIndex = ai;
        m_period = p;
        m_round = r;
        m_fromNode = fn;
        m_toNode = tn;
        m_fromRes = fa.getLabel();
        m_fromResAmt = fa.getResource();
        m_toRes = ta.getLabel();
        m_toResAmt = ta.getResource();
        m_offerType = off;
        m_tickTime = tt;
        m_realTime = rt;
        }
    public CEOfferOutputObject(ResultSet rs)
        {
        try
            {
            m_exptID = rs.getInt("Expt_Out_ID_INT");
            m_actionIndex = rs.getInt("Action_Index_INT");
            m_period = rs.getInt("Period_INT");
            m_round = rs.getInt("Round_INT");
            m_fromNode = rs.getInt("From_Node_INT");
            m_toNode = rs.getInt("To_Node_INT");
            m_fromRes = rs.getString("From_Node_Res_VC");
            m_fromResAmt = rs.getDouble("From_Node_Amt_FLT");
            m_toRes = rs.getString("To_Node_Res_VC");
            m_toResAmt = rs.getDouble("To_Node_Amt_FLT");
            m_offerType = rs.getString("Offer_Type_VC");
            m_tickTime = rs.getInt("Tick_Time_INT");
            m_realTime = rs.getInt("Real_Time_INT");
            }
        catch(Exception e) 
            {
            }
        }

    public void formatInsertStatement(CallableStatement cs) throws java.sql.SQLException 
        {
        cs.setInt(1,m_exptID);
        cs.setInt(2,m_actionIndex);
        cs.setInt(3,m_period);
        cs.setInt(4,m_round);
        cs.setInt(5,m_fromNode);
        cs.setInt(6,m_toNode);
        cs.setString(7,m_fromRes);
        cs.setFloat(8,(float)m_fromResAmt);
        cs.setString(9,m_toRes);
        cs.setFloat(10,(float)m_toResAmt);
        cs.setString(11,m_offerType);
        cs.setInt(12,m_tickTime);
        cs.setInt(13,(int)m_realTime);
        }

    public int getActionIndex()
        {
        return m_actionIndex;
        }
    public int getExptID()
        {
        return m_exptID;
        }
    public String getFromRes()
        {
        return m_fromRes;
        }
    public double getFromResAmt()
        {
        return m_fromResAmt;
        }
    public int getFromNode()
        {
        return m_fromNode;
        }
/**
 * Expt_Out_ID_INT, Action_Index, Period_INT, Round_INT, From_Node_INT, To_Node_INT,
 * From_Node_Res_VC, From_Node_Amt_INT, To_Node_Res_VC, To_Node_Amt_INT, Offer_Type_VC,
 * Tick_Time_INT, Real_Time_INT
 */
    public String getInsertFormat()
        { 
        return new String("{call up_insert_JCEOfferData (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
        }
    public String getOfferType()
        {
        return m_offerType;
        }
    public int getPeriod()
        {
        return m_period;
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
    public String getToRes()
        {
        return m_toRes;
        }
    public double getToResAmt()
        {
        return m_toResAmt;
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
    public void setFromRes(String value)
        {
        m_fromRes = value;
        }
    public void setFromResAmt(double value)
        {
        m_fromResAmt = value;
        }
    public void setFromNode(int value)
        {
        m_fromNode = value;
        }
    public void setOfferType(String value)
        {
        m_offerType = value;
        }
    public void setPeriod(int value)
        {
        m_period = value;
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
    public void setToRes(String value)
        {
        m_toRes = value;
        }
    public void setToResAmt(double value)
        {
        m_toResAmt = value;
        }
    public void setToNode(int value)
        {
        m_toNode = value;
        }
    }
