package girard.sc.ce.obj;

import girard.sc.expt.obj.DataOutputObject;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;

/**
 * A customized data object for saving a node's resources after a round
 * of bargaining.   
 * <p>
 * <br> Started: 02-21-2003
 * <p>
 * @author Dudley Girard
 */

public class CEEndRoundResOutputObject extends DataOutputObject implements Serializable
    {
    int     m_exptID;  /* Unique identifier for this experiment. */
    int     m_actionIndex;
    int     m_period;
    int     m_round;
    int     m_node;
    String  m_res;
    double  m_resAmt;
    
    public CEEndRoundResOutputObject ()
        {
        }
    public CEEndRoundResOutputObject(int exptID, int ai, int p, int r, int n, CEResource res)
        {
        m_exptID = exptID;
        m_actionIndex = ai;
        m_period = p;
        m_round = r;
        m_node = n;
        m_res = res.getLabel();
        m_resAmt = res.getResource();
        }
    public CEEndRoundResOutputObject(ResultSet rs)
        {
        try
            {
            m_exptID = rs.getInt("Expt_Out_ID_INT");
            m_actionIndex = rs.getInt("Action_Index_INT");
            m_period = rs.getInt("Period_INT");
            m_round = rs.getInt("Round_INT");
            m_node = rs.getInt("Node_INT");
            m_res = rs.getString("Node_Res_VC");
            m_resAmt = rs.getDouble("Res_Amt_FLT");
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
        cs.setInt(5,m_node);
        cs.setString(6,m_res);
        cs.setFloat(7,(float)m_resAmt);
        }

    public int getActionIndex()
        {
        return m_actionIndex;
        }
    public int getExptID()
        {
        return m_exptID;
        }
    public int getNode()
        {
        return m_node;
        }
/**
 * Expt_Out_ID_INT, Action_Index, Period_INT, Round_INT, Node_INT,
 * Node_Res_VC, Res_Amt_FLT
 */
    public String getInsertFormat()
        { 
        return new String("{call up_insert_JCEResourceData (?, ?, ?, ?, ?, ?, ?)}");
        }
    public int getPeriod()
        {
        return m_period;
        }
    public String getRes()
        {
        return m_res;
        }
    public double getResAmt()
        {
        return m_resAmt;
        }
    public int getRound()
        {
        return m_round;
        }
        
    public void setActionIndex(int value)
        {
        m_actionIndex = value;
        }
    public void setExptID(int value)
        {
        m_exptID = value;
        }
    public void setNode(int value)
        {
        m_node = value;
        }
    public void setPeriod(int value)
        {
        m_period = value;
        }
    public void setRes(String value)
        {
        m_res = value;
        }
    public void setResAmt(double value)
        {
        m_resAmt = value;
        }
    public void setRound(int value)
        {
        m_round = value;
        }
    }