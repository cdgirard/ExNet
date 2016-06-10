package girard.sc.be.obj;
/* 
   The object that watches for the end of the round at which point it disposes
   of the window it is attached to.

   Author: Dudley Girard
   Started: 1-24-2001
*/

import girard.sc.be.awt.BENetworkActionClientWindow;

import java.awt.Frame;

public class BERRThread extends Thread
    {
    BENetworkActionClientWindow m_NACWApp;
    Frame m_frame;
    double m_stopPoint = 1.5;

    public BERRThread (BENetworkActionClientWindow app1, Frame app2)
        {
        m_NACWApp = app1;
        m_frame = app2;

        start();
        }

    public void run()
        {
        BENetwork ben = m_NACWApp.getNetwork();
        Double presentState = (Double)ben.getExtraData("CurrentState");
        while (presentState.doubleValue() < m_stopPoint)
            {
            try { sleep(500); }
            catch (InterruptedException ie) { ; }
            presentState = (Double)ben.getExtraData("CurrentState");
            }
        m_NACWApp.removeSubWindow(m_frame);
        }

    public void setStopPoint(int value)
        {
        m_stopPoint = value;
        }
    }
