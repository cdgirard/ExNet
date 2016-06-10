package girard.sc.ce.obj;

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

public class CEPeriod extends NetworkComponent 
    {
    public static final double BEGIN_ROUND_STATE = 0; // Nothing should ever occur before this state for a round.
    public static final double START_ROUND_STATE = 1; // This stands for the show start next round window.
    public static final double END_ROUND_STATE = 2; // Nothing should ever occur after this state for a round.

    int            m_period = -1;
    int            m_rounds = -1;
    int            m_time = -1;
    int            m_currentRound = -1;
    int            m_currentTime = -1;
    int[]          m_userOrder = null;  // The value is the node for, while the index is the index of the user num.

    public CEPeriod ()
        {
        }
    public CEPeriod (Hashtable nodes)
        {
        m_userOrder = new int[nodes.size()];
        int i = 0;
        Enumeration enm = nodes.keys();
        while (enm.hasMoreElements())
            {
            m_userOrder[i] = ((Integer)enm.nextElement()).intValue();
            i++;
            }
        }
    public CEPeriod (int p, int r, int t, Hashtable nodes)
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
        }
    public CEPeriod (int p, int r, int t, int[] nodes)
        {
        m_period = p;
        m_rounds = r;
        m_time = t;
        m_userOrder = new int[nodes.length];

        for (int i=0;i<nodes.length;i++)
            {
            m_userOrder[i] = nodes[i];
            }
        }

    public void applySettings(Hashtable h)
        {
        m_period = ((Integer)h.get("Period")).intValue();
        m_rounds = ((Integer)h.get("Rounds")).intValue();
        m_time = ((Integer)h.get("Time")).intValue();
        m_userOrder = (int[])h.get("UserOrder");
        }

    public Object clone()
        {
        CEPeriod cep = new CEPeriod(m_period,m_rounds,m_time,m_userOrder);

        cep.setCurrentRound(m_currentRound);
        cep.setCurrentTime(m_currentTime);

        return cep;
        }

   // Draw info on a canvas using the graphic for the client screen.
    public void drawClient(Graphics g, Vector info) {}

  // Draw info on a canvas using the graphic for the observer screen.
    public void drawObserver(Graphics g, Vector info) {}

  // Draw info on a canvas using the graphic for the experimenter screen.
    public void drawExpt(Graphics g, Vector info) {}

    public String getComponentName()
        {
        return "CE Period";
        }
    public int getCurrentRound()
        {
        return m_currentRound;
        }
    public int getCurrentTime()
        {
        return m_currentTime;
        }
    public double getNextValidState(CENetwork cen)
        {
        double currentState = ((Double)cen.getExtraData("CurrentState")).doubleValue();
        Boolean rr = (Boolean)cen.getExtraData("RoundRunning");

        if (currentState < BEGIN_ROUND_STATE) // This should really never occur.
            return BEGIN_ROUND_STATE;
        if (currentState < START_ROUND_STATE)
            return START_ROUND_STATE;
        if  ((currentState < END_ROUND_STATE) && (!rr.booleanValue()))
            return END_ROUND_STATE;
        return 100;
        }
    public CEStateAction getNextState(CENetwork cen)
        {
        double currentState = ((Double)cen.getExtraData("CurrentState")).doubleValue();

        if (currentState < START_ROUND_STATE)
            {
            return new CERoundWindowStateAction(this);
            }
        if (currentState < END_ROUND_STATE)
            {
            return new CEEndRoundStateAction();
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

        return settings;
        }
    public int getTime()
        {
        return m_time;
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
	    System.err.println("retrieving teh data from retrieveData");
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
