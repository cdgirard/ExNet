package girard.sc.be.obj;

import girard.sc.exnet.obj.NetworkComponent;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Panel;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to store the settings for a specific period.  Stores how
 * many rounds the period is to run, how many ticks each round will
 * last, where each user is located in the network, and how and what
 * information to display in the round window.
 * <p>
 * <br> Started: Sometime 2000
 * <br> Modified: 03-07-2003
 * <p>
 * @author Dudley Girard
 */

public class BEPeriod extends NetworkComponent 
    {
/**
 * Nothing should ever occur before this state for a round.
 */
    public static final double BEGIN_ROUND_STATE = 0;
/**
 * This stands for the show start next round window.
 */
    public static final double START_ROUND_STATE = 1; 
    public static final double END_BARGINING_STATE = 1.5;
/**
 * Nothing should ever occur after this state for a round.
 */
    public static final double END_ROUND_STATE = 2; 

    int            m_period = -1;
    int            m_rounds = -1;
    int            m_time = -1;
    int            m_currentRound = -1;
    int            m_currentTime = -1;
/**
 * The value stored is the id of a node, while the index the value is 
 * stored at is the user num.
 */
    int[]          m_userOrder = null;
    Hashtable      m_extraData = new Hashtable(); 

    public BEPeriod ()
        {
        String str = new String("Points\nID");
        addExtraData("Round Window Code",str);
        }
    public BEPeriod (Hashtable nodes)
        {
        m_userOrder = new int[nodes.size()];
        int i = 0;
        Enumeration enm = nodes.keys();
        while (enm.hasMoreElements())
            {
            m_userOrder[i] = ((Integer)enm.nextElement()).intValue();
            i++;
            }

        String str = new String("Points\nID");
        addExtraData("Round Window Code",str);
        }
    public BEPeriod (int p, int r, int t, Hashtable nodes)
        {
        m_period = p;
        m_rounds = r;
        m_time = t;
        m_userOrder = new int[nodes.size()];

        int i = 0;
        Enumeration enm = nodes.keys();
        while (enm.hasMoreElements())
            {
            m_userOrder[i] = ((Integer)enm.nextElement()).intValue();
            i++;
            }

        String str = new String("Points\nID");
        addExtraData("Round Window Code",str);
        }
    public BEPeriod (int p, int r, int t, int[] nodes)
        {
        m_period = p;
        m_rounds = r;
        m_time = t;
        m_userOrder = new int[nodes.length];

        for (int i=0;i<nodes.length;i++)
            {
            m_userOrder[i] = nodes[i];
            }

        String str = new String("Points\nID");
        addExtraData("Round Window Code",str);
        }

    public void addExtraData(String key, String obj)
        {
        m_extraData.put(key,obj);
        }

    public void applySettings(Hashtable h)
        {
        m_period = ((Integer)h.get("Period")).intValue();
        m_rounds = ((Integer)h.get("Rounds")).intValue();
        m_time = ((Integer)h.get("Time")).intValue();
        m_userOrder = (int[])h.get("UserOrder");

        if (h.containsKey("ExtraData"))
            {
            m_extraData = (Hashtable)h.get("ExtraData");
            }
        else
            {
            String str = new String("Points\nID");
            addExtraData("Round Window Code",str);
            }

        }

    public Object clone()
        {
        BEPeriod bep = new BEPeriod(m_period,m_rounds,m_time,m_userOrder);

        bep.setCurrentRound(m_currentRound);
        bep.setCurrentTime(m_currentTime);
        Enumeration enm = m_extraData.keys();
        while (enm.hasMoreElements())
            {
            String key = new String((String)enm.nextElement());
            String obj = new String((String)m_extraData.get(key));
            bep.addExtraData(key,obj);
            }

        return bep;
        }

   // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector info) {}

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector info) {}

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector info) {}

    public String getComponentName()
        {
        return "BE Period";
        }
    public int getCurrentRound()
        {
        return m_currentRound;
        }
    public int getCurrentTime()
        {
        return m_currentTime;
        }
    public Hashtable getExtraData()
        {
        return m_extraData;
        }
    public String getExtraData(String key)
        {
        if (m_extraData.containsKey(key))
            {
            return (String)m_extraData.get(key);
            }
        return null;
        }
    public double getNextValidState(BENetwork ben)
        {
        double currentState = ((Double)ben.getExtraData("CurrentState")).doubleValue();
        Boolean rr = (Boolean)ben.getExtraData("RoundRunning");

        if (currentState < BEGIN_ROUND_STATE) // This should really never occur.
            return BEGIN_ROUND_STATE;
        if (currentState < START_ROUND_STATE)
            return START_ROUND_STATE;
        if  ((currentState < END_ROUND_STATE) && (!rr.booleanValue()))
            return END_ROUND_STATE;
        return 100;
        }
    public BEStateAction getNextState(BENetwork ben)
        {
        double currentState = ((Double)ben.getExtraData("CurrentState")).doubleValue();

        if (currentState < START_ROUND_STATE)
            {
            return new BERoundWindowStateAction(this);
            }
        if (currentState < END_ROUND_STATE)
            {
            return new BEEndRoundStateAction();
            }
        return null;
        }
    public int getPeriod()
        {
        return m_period;
        }
    public int getRounds()
        {
        return m_rounds;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
 
        settings.put("Period",new Integer(m_period));
        settings.put("Rounds",new Integer(m_rounds));
        settings.put("Time",new Integer(m_time));
        settings.put("UserOrder",m_userOrder);

        settings.put("ExtraData",m_extraData);

        return settings;
        }
    public int getTime()
        {
        return m_time;
        }
// Returns the user attached to that node.
    public int getUserID(int value)
        {
        for (int i=0;i<m_userOrder.length;i++)
            {
            if (m_userOrder[i] == value)
                return i;
            }
        return -1;
        }
// Returns the node belonging to that user.
    public int getUserNode(int user)
        {
        return m_userOrder[user];
        }
    public int[] getUserOrder()
        {
        return m_userOrder;
        }
    public int getUserOrder(int value)
        {
        return m_userOrder[value];
        }
    public int getNumUsers()
        {
        return m_userOrder.length;
        }
   // Stick object info into a panel for displaying.
    public Panel getObjPanelDisplay() 
        {
        return new Panel(new GridLayout(1,1));
        }
  

  // Initialize some or all of the network based on the data values of the object.
    public void initializeNetwork() {}

  // Reset object data to starting values.
    public void reset() {}

    public Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi)
        {
        return new Hashtable();
        }

    public void setCurrentRound(int value)
        {
        m_currentRound = value;
        }
    public void setCurrentTime(int value)
        {
        m_currentTime = value;
        }
    public void setPeriod(int p)
        {
        m_period = p;
        }
    public void setRounds(int r)
        {
        m_rounds = r;
        }
    public void setTime(int value)
        {
        m_time = value;
        }
    public void setUserOrder(int loc, int value)
        {
        m_userOrder[loc] = value;
        }

    public void swapNodes(int value1, int value2)
        {
        int tmp;

        tmp = m_userOrder[value1];
        m_userOrder[value1] = m_userOrder[value2];
        m_userOrder[value2] = tmp;
        }

  // Update network data based on the action info for this data object.
  //  public void updateNetwork(ExnetAction act) {}
    }