package girard.sc.exnet.obj;
/* 
   Is the base object class for adding additional functionality 
   to the objects contained within the Network object class.

   Author: Dudley Girard
   Started: 4-30-2000
*/

import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLServerConnection;

import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Panel;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;

public abstract class NetworkComponent implements Serializable,Cloneable
    {
    protected Network m_network;
    protected String m_componentName = "None"; // Then name of the component

    public NetworkComponent()
        {
        }
    public NetworkComponent(String n)
        {
        m_componentName = n;
        }
    public NetworkComponent(Network en)
        {
        m_network = en;
        }
    public NetworkComponent(Network en, String n)
        {
        m_componentName = n;
        m_network = en;
        } 

    public abstract void applySettings(Hashtable h);

    public abstract Object clone();

  // Draw info on a canvas using the graphic for the client screen.
    public abstract void drawClient(Graphics g, Vector locInfo);

  // Draw info on a canvas using the graphic for the observer screen.
    public abstract void drawObserver(Graphics g, Vector locInfo);

  // Draw info on a canvas using the graphic for the experimenter screen.
    public abstract void drawExpt(Graphics g, Vector locInfo);


    public String getComponentName()
        {
        return m_componentName;
        }
  // Return an array of strings explaining the state of the object data
    public Vector getComponentInfo() 
        {
        return new Vector();
        }
  // Stick object info into a panel for displaying.
    public Panel getComponentPanelDisplay() 
        {
        return new Panel(new GridLayout(1,1));
        }
    public abstract Hashtable getSettings();

  // Initialize some or all of the network based on the data values of the object.
    public abstract void initializeNetwork();

  // Reset object data to starting values.
    public abstract void reset();

    public abstract Hashtable retrieveData(WLServerConnection wlsc, ExptMessage em, BaseDataInfo bdi);

    public void setNetwork(Network temp)
        {
        m_network = temp;
        }

  // Update network data based on the action info for this data object.
    public void updateNetwork(Object obj) {}
    }
