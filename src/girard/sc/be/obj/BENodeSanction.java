package girard.sc.be.obj;

import java.io.Serializable;
import java.util.Hashtable;

/** 
 * Is the object class for defining sanctions for 
 * a Node object in a BENetwork.
 * <p>
 * <br> Started: 09-19-2002
 * <p>
 * @author Dudley Girard
 */

public class BENodeSanction implements Cloneable,Serializable
    {
    protected int         m_toNode = -1;  // Who to send the sanction to.
    protected int         m_rewardValue = 0; //  How much the reward value is.
    protected int         m_sanctionValue = 0; // How much the sanction value is.

    protected boolean     m_sent = false; // Have I sent a sanction to this node yet this round?
    protected boolean     m_msg = false; // true -> sent the reward value, false -> sent the sanction value.

    public BENodeSanction()
        {
        }
    public BENodeSanction (int tn, int reward, int sanction)
        {
        m_toNode = tn;
        m_rewardValue = reward;
        m_sanctionValue = sanction;
        }

    public void applySettings(Hashtable h)
        {
        m_toNode = ((Integer)h.get("ToNode")).intValue();
        m_rewardValue = ((Integer)h.get("RewardValue")).intValue();
        m_sanctionValue = ((Integer)h.get("SanctionValue")).intValue();
        }

    public Object clone()
        {
        BENodeSanction sanction = new BENodeSanction(m_toNode,m_rewardValue,m_sanctionValue);

        return sanction;
        }

    public boolean getMsg()
        {
        return m_msg;
        }
    public int getToNode()
        {
        return m_toNode;
        }
    public int getRewardValue()
        {
        return m_rewardValue;
        }
    public int getSanctionValue()
        {
        return m_sanctionValue;
        }
    public boolean getSent()
        {
        return m_sent;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();

        settings.put("ToNode",new Integer(m_toNode));
        settings.put("RewardValue",new Integer(m_rewardValue));
        settings.put("SanctionValue",new Integer(m_sanctionValue));

        return settings;
        }

    public void setMsg(boolean value)
        {
        m_msg = value;
        }
    public void setRewardValue(int value)
        {
        m_rewardValue = value;
        }
    public void setSanctionValue(int value)
        {
        m_sanctionValue = value;
        }
    public void setSent(boolean value)
        {
        m_sent = value;
        }
    }