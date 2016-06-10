package girard.sc.ques.obj;

import girard.sc.expt.obj.DataOutputObject;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;

/** 
 * A customized data object for saving the answer data from a BaseQuestion.   
 * <p>
 * Started: 08-06-2002
 * <p>
 * @author Dudley Girard
 */

public class AnswerOutputObject extends DataOutputObject implements Serializable
    {
/**
 * Unique identifier for this experiment.
 */
    int     m_exptID;
/**
 *
 */
    int     m_actionIndex;
/**
 * Which user does this question belong to.
 */
    int     m_userIndex;
/**
 * For which question is this an answer.
 */
    int     m_questionIndex;
/**
 * Which question index did their answer send them to.
 */
    int     m_transitionIndex;
/**
 * The answer they gave.
 */
    String  m_answer;
/**
 * Should be number of seconds times 10 since the network round started. 
 */
    int     m_realTime;
    
    
    public AnswerOutputObject ()
        {
        }
    public AnswerOutputObject(int exptID, int ai, int ui, int qi, int ti, String ans, int tp)
        {
        m_exptID = exptID;
        m_actionIndex = ai;
        m_userIndex = ui;
        m_questionIndex = qi;
        m_transitionIndex = ti;
        m_answer = ans;
        m_realTime = tp;
        }
    public AnswerOutputObject(ResultSet rs)
        {
        try
            {
            m_exptID = rs.getInt("Expt_Out_ID_INT");
            m_actionIndex = rs.getInt("Action_Index_INT");
            m_userIndex = rs.getInt("User_Index_INT");
            m_questionIndex = rs.getInt("Question_Index_INT");
            m_transitionIndex = rs.getInt("Transition_Index_INT");
            m_answer = rs.getString("Answer_VC");
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
        cs.setInt(3,m_userIndex);
        cs.setInt(4,m_questionIndex);
        cs.setInt(5,m_transitionIndex);
        cs.setString(6,m_answer);
        cs.setInt(7,m_realTime);
        }

    public int getActionIndex()
        {
        return m_actionIndex;
        }
    public String getAnswer()
        {
        return m_answer;
        }
    public int getExptID()
        {
        return m_exptID;
        }
    public String getInsertFormat()
        {
    // Expt_Out_ID_INT, Action_Index, User_Index_INT, Question_Index_INT, Transition_Index_INT, Answer_VC, Real_Time_INT
        return new String("{call up_insert_JAnswerData (?, ?, ?, ?, ?, ?, ?)}");
        }
    public int getQuestionIndex()
        {
        return m_questionIndex;
        }
    public int getRealTime()
        {
        return m_realTime;
        }
    public int getTransitionIndex()
        {
        return m_transitionIndex;
        }
    public int getUserIndex()
        {
        return m_userIndex;
        }
    
    public void setActionIndex(int value)
        {
        m_actionIndex = value;
        }
    public void setAnswer(String str)
        {
        m_answer = str;
        }
    public void setExptID(int value)
        {
        m_exptID = value;
        }
    public void setQuestionIndex(int value)
        {
        m_questionIndex = value;
        }
    public void setRealTime(int value)
        {
        m_realTime = value;
        }
    public void setTransitionIndex(int value)
        {
        m_transitionIndex = value;
        }
    public void setUserIndex(int value)
        {
        m_userIndex = value;
        }
    }
