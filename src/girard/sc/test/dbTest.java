package girard.sc.test;

import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.web.ExptOverlord;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

public class dbTest extends Thread implements ActionListener
    {
    protected Vector m_runSTimes = new Vector();
    protected Vector m_runLTimes = new Vector();
    protected Vector m_accessLTimes = new Vector();

    protected int m_counter = 0;

    protected ExptMessageListener m_SML;
    protected ExptOverlord m_EOApp = new ExptOverlord(null);

    public dbTest(int id)
        {
        m_SML = new ExptMessageListener("weblab.socy.sc.edu",6721,m_EOApp);
        m_SML.addActionListener(this);

        m_EOApp.setUserID(id);

        m_SML.start();
        }

    public void actionPerformed(ActionEvent e)
        {
 System.err.println("HERE: "+m_EOApp.getUserID()+" : "+e.getSource());
        if (e.getSource() instanceof ExptMessage)
            {
            ExptMessage theSource = (ExptMessage)e.getSource();

            if (theSource instanceof LoadTestReqMsg)
                {
                Vector times = (Vector)theSource.getArgs()[1];
                times.addElement(new Long(Calendar.getInstance().getTime().getTime()));

                m_accessLTimes.addElement(times);

                

                m_counter--;
                }
            if (theSource instanceof SaveTestReqMsg)
                {
                // Long startTime = (Long)theSource.getArgs()[0];
                // long totalTime = Calendar.getInstance().getTime().getTime() - startTime.longValue();
                // totalTime = totalTime/10; // 1000 gives seconds
                // m_runSTimes.addElement(new Long(totalTime));
                m_counter--;
                }
            }
        }

    public void run()
        {
        String[] names = { "F1", "F2", "F3", "F4", "F5" };
        Vector[] obj = new Vector[5];

        for (int a=0;a<5;a++)
            {
            obj[a] = new Vector();
            }
        for (int a=0;a<5;a++)
            {
            for (int b=0;b<1000;b++)
                {
                obj[a].addElement(new Double(Math.random()));
                }
            }

        m_counter = 0;
        int index = 0;

   //     index = (int)(Math.random()*1000);

    //    try { sleep(index); }
    //    catch (Exception e) { ; }

        for (int a=0;a<5;a++)
            {
            Vector Stime = new Vector();
            Stime.addElement(new Long(Calendar.getInstance().getTime().getTime()));

            Object[] save_args = new Object[3];
            save_args[0] = names[a];
            save_args[1] = obj[a];
            save_args[2] = Stime;

            SaveTestReqMsg strm = new SaveTestReqMsg(save_args);
            m_SML.sendMessage(strm);

            m_counter++;
            }
if (Math.random() < 1.0)
{
        for (int i=0;i<7;i++)
            {
    System.err.println("Here "+i+" : "+m_EOApp.getUserID());

            try { sleep(750); }
            catch (Exception e) { ; }

            index = (int)(Math.random()*5);

            Vector Ltime = new Vector();
            Ltime.addElement(new Long(Calendar.getInstance().getTime().getTime()));

            Object[] load_args = new Object[2];
            load_args[0] = names[index];
            load_args[1] = Ltime;

            LoadTestReqMsg ltrm = new LoadTestReqMsg(load_args);
            m_SML.sendMessage(ltrm);

            m_counter++;
    //    System.err.println("HERE1");
            try { sleep(750); }
            catch (Exception e) { ; }
    //   System.err.println("HERE2");

            index = (int)(Math.random()*5);

            Vector Stime = new Vector();
            Stime.addElement(new Long(Calendar.getInstance().getTime().getTime()));

            Object[] save_args = new Object[3];
            save_args[0] = names[index];
            save_args[1] = obj[index];
            save_args[2] = Stime;

            SaveTestReqMsg strm = new SaveTestReqMsg(save_args);
            m_SML.sendMessage(strm);

            m_counter++;
            }

        while (m_counter > 0)
            {
            try { sleep(500); }
            catch (Exception e) { ; }
           //  m_counter--;
            }
            
            //Enumeration enumS = m_runSTimes.elements();
           // while (enumS.hasMoreElements())
            //    {
           //     Long time = (Long)enumS.nextElement();
              //  System.err.println("ST: "+time.toString()+" "+m_EOApp.getUserID());
           //     }

            Enumeration enumL = m_accessLTimes.elements();
            int counter = 0;
            while (enumL.hasMoreElements())
                {

                Vector times = (Vector)enumL.nextElement();

                Long msgCreated = (Long)times.elementAt(0);
                Long startProc = (Long)times.elementAt(1);
                Long preDB1 = (Long)times.elementAt(2);
                Long startDB1 = (Long)times.elementAt(3);
                Long endDB1 = (Long)times.elementAt(4);
                Long preDB2 = (Long)times.elementAt(5);
                Long startDB2 = (Long)times.elementAt(6);
                Long endDB2 = (Long)times.elementAt(7);
                Long preDB3 = (Long)times.elementAt(8);
                Long startDB3 = (Long)times.elementAt(9);
                Long endDB3 = (Long)times.elementAt(10);
                Long endProc = (Long)times.elementAt(11);
                Long msgReply = (Long)times.elementAt(12);

                long d1 = msgReply.longValue() - msgCreated.longValue();
                long d2 = preDB1.longValue() - startProc.longValue();
                long d3 = startDB1.longValue() - preDB1.longValue();
                long d4 = endDB1.longValue() - startDB1.longValue();
                long d5 = preDB2.longValue() - endDB1.longValue();
                long d6 = startDB2.longValue() - preDB2.longValue();
                long d7 = endDB2.longValue() - startDB2.longValue();
                long d8 = preDB3.longValue() - endDB2.longValue();
                long d9 = startDB3.longValue() - preDB3.longValue();
                long d10 = endDB3.longValue() - startDB3.longValue();
                long d11 = endProc.longValue() - endDB3.longValue();

                System.err.println("(D)LT: "+m_EOApp.getUserID()+" : "+counter+" : "+d1+" : "+d2+" : "+d3+" : "+d4+" : "+d5+" : "+d6+" : "+d7+" : "+d8+" : "+d9+" : "+d10+" : "+d11);
                counter++;
                }
}
        m_SML.finalize(-1);
        System.err.println("Done: "+m_EOApp.getUserID());
        }
    }