package girard.sc.ce.obj;
/**
@author Murali Narumanchi
created on 14th Feb 2005.
This object stores the externality information into the database.
*/
import girard.sc.expt.obj.DataOutputObject;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;

public class CEExternalityOutputObject extends DataOutputObject implements Serializable{
    private int m_exptId;
    private int m_actionIndex;    
    private int period;
    private int m_round;
    private int fNode;      // affecting node
    private String fEdge;   // affecting edge
    private int aNode;      // affected node
    private float externality;

    public CEExternalityOutputObject(){
	
    }
    
    public CEExternalityOutputObject(int eid, int ai, int p, int r, int f, String fe, int an, float e){
	m_exptId = eid;
	m_actionIndex = ai;
	period = p;
	m_round = r;
	fNode = f;      // affecting node
	fEdge = fe;   // affecting edge
	aNode = an;      // affected node
	externality = e;
    }
    
    public CEExternalityOutputObject(ResultSet rs){
	try{
	    m_exptId = rs.getInt("Expt_Out_ID_INT");
	    m_actionIndex = rs.getInt("Action_Index_INT");
	    period = rs.getInt("Period_INT");
	    m_round = rs.getInt("Round_INT");
	    fNode = rs.getInt("From_Node_INT");
	    fEdge = rs.getString("From_Edge_VC");
	    aNode = rs.getInt("Affected_Node_INT");
	    externality = rs.getFloat("Externality_Flt");
	}catch (Exception e){
	    //
	}
    }

    public void formatInsertStatement(CallableStatement cs) throws java.sql.SQLException{
	cs.setInt(1,m_exptId);
	cs.setInt(2,m_actionIndex);
	cs.setInt(3,period);
	cs.setInt(4,m_round);
	cs.setInt(5,fNode);
	cs.setString(6,fEdge);
	cs.setInt(7,aNode);
	cs.setFloat(8,externality);
    }

    public String getInsertFormat(){
	return new String("{call up_insert_JCEExternalityData (?, ?, ?, ?, ?, ?, ?, ?)}");
    }


    /**
     * Gets the value of m_exptId
     *
     * @return the value of m_exptId
     */
    public int getM_exptId()  {
	return this.m_exptId;
    }

    /**
     * Sets the value of m_exptId
     *
     * @param argM_exptId Value to assign to this.m_exptId
     */
    public void setM_exptId(int argM_exptId) {
	this.m_exptId = argM_exptId;
    }


    /**
     * Get the M_actionIndex value.
     * @return the M_actionIndex value.
     */
    public int getM_actionIndex() {
	return m_actionIndex;
    }

    /**
     * Set the M_actionIndex value.
     * @param newM_actionIndex The new M_actionIndex value.
     */
    public void setM_actionIndex(int newM_actionIndex) {
	this.m_actionIndex = newM_actionIndex;
    }

    /**
     * Gets the value of period
     *
     * @return the value of period
     */
    public int getPeriod()  {
	return this.period;
    }

    /**
     * Sets the value of period
     *
     * @param argPeriod Value to assign to this.period
     */
    public void setPeriod(int argPeriod) {
	this.period = argPeriod;
    }

    /**
     * Gets the value of m_round
     *
     * @return the value of m_round
     */
    public int getM_round()  {
	return this.m_round;
    }

    /**
     * Sets the value of m_round
     *
     * @param argM_round Value to assign to this.m_round
     */
    public void setM_round(int argM_round) {
	this.m_round = argM_round;
    }

    /**
     * Gets the value of fNode
     *
     * @return the value of fNode
     */
    public int getFNode()  {
	return this.fNode;
    }

    /**
     * Sets the value of fNode
     *
     * @param argFNode Value to assign to this.fNode
     */
    public void setFNode(int argFNode) {
	this.fNode = argFNode;
    }

    /**
     * Gets the value of fEdge
     *
     * @return the value of fEdge
     */
    public String getFEdge()  {
	return this.fEdge;
    }

    /**
     * Sets the value of fEdge
     *
     * @param argFEdge Value to assign to this.fEdge
     */
    public void setFEdge(String argFEdge) {
	this.fEdge = argFEdge;
    }

    /**
     * Gets the value of aNode
     *
     * @return the value of aNode
     */
    public int getANode()  {
	return this.aNode;
    }

    /**
     * Sets the value of aNode
     *
     * @param argANode Value to assign to this.aNode
     */
    public void setANode(int argANode) {
	this.aNode = argANode;
    }

    /**
     * Gets the value of externality
     *
     * @return the value of externality
     */
    public float getExternality()  {
	return this.externality;
    }

    /**
     * Sets the value of externality
     *
     * @param argExternality Value to assign to this.externality
     */
    public void setExternality(float argExternality) {
	this.externality = argExternality;
    }

}
