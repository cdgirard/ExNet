package girard.sc.tut.obj;

import girard.sc.expt.awt.SimulantBuilderWindow;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.StartExptReqMsg;
import girard.sc.expt.obj.SimActor;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;
import girard.sc.tut.awt.FormatTutorialSimActorWindow;
import girard.sc.tut.io.msg.FinishedTutorialMsg;
import girard.sc.tut.io.msg.NextTutorialPageReqMsg;
import girard.sc.tut.io.msg.StartTutorialActionReqMsg;
import girard.sc.tut.io.msg.StopTutorialActionReqMsg;

import java.awt.event.ActionEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

public class TutorialSimActor extends SimActor
    {
    public static final String DB = "tpDB";
    public static final String NAME = "TutorialSimActor";
    public static final String DESC = "A Simulant for the Tutorial Action.";

    protected boolean m_flag1 = true; /* Keep running flag. */
    protected boolean m_flag2 = true;
    protected boolean m_cleanUpFlag = false;
    protected boolean m_exptStopping = false;

    public TutorialSimActor ()
        {
        super (DB,NAME,DESC);
        m_actionType = "Tutorial Action";
        }
    
    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof ExptMessage)
            {
            ExptMessage em = (ExptMessage)e.getSource();

            synchronized(m_SML)
                {
                if (em instanceof StartTutorialActionReqMsg)
                    {
                    processStartTutorialActionReqMsg(em);
                    }
                if (em instanceof StopTutorialActionReqMsg)
                    {
                    processStopTutorialActionReqMsg(em);
                    }
                if (em instanceof StartExptReqMsg)
                    {
                    processStartExptReqMsg(em);
                    }
                }
            }
        }

    public void applySettings(Hashtable h)
        {
        }

    public Object clone()
        {
        TutorialSimActor sa = new TutorialSimActor();

        sa.applySettings(this.getSettings());
        sa.setActorID(m_actorID);
        sa.setActorTypeID(m_actorTypeID);
        sa.setActionType(m_actionType);
        sa.setActorName(new String(m_actorName));
        sa.setActorDesc(new String(m_actorDesc));
        sa.setUser(m_user);
        sa.setUserID(m_userID);

        return sa;
        }

    public void formatActor(ExptOverlord app, SimulantBuilderWindow sbw)
        {
        new FormatTutorialSimActorWindow(app,sbw,(TutorialSimActor)this.clone());
        }

    public  Hashtable getSettings()
        {
        Hashtable h = new Hashtable();

        return h;
        }

    public void processStartTutorialActionReqMsg(ExptMessage em)
        {
        m_activeAction = em.getArgs()[0];
        m_flag1 = false;
        }
    public void processStopTutorialActionReqMsg(ExptMessage em)
        {
        m_exptStopping = true; // We are stopping the experiment.

        StopTutorialActionReqMsg tmp = new StopTutorialActionReqMsg(null);
        m_SML.sendMessage(tmp);
        }
    public void processStartExptReqMsg(ExptMessage em)
        {
        StartExptReqMsg tmp = new StartExptReqMsg(null);
        m_SML.sendMessage(tmp);
        }

    public void run()
        {
System.err.println("Hey I'm running");

        m_cleanUpFlag = true;

        while (m_flag1)
            {
        // Wait for the network to be sent.
            try { sleep(500); }
            catch (InterruptedException ie) { ; }
            }

        int counter = 0;
        boolean doneFlag = false;

        while (m_flag2)
            {
            try { sleep(1000); }
            catch (InterruptedException ie) { ; }

            if (!m_exptStopping)
                {
                if (counter < ((Vector)m_activeAction).size())
                    {
                    NextTutorialPageReqMsg tmp = new NextTutorialPageReqMsg(null);
                    m_SML.sendMessage(tmp);
                    }
                else if (!doneFlag)
                    {
                    FinishedTutorialMsg tmp = new FinishedTutorialMsg(null);
                    m_SML.sendMessage(tmp);
                    doneFlag = true;
                    }
                }
            if (!doneFlag)
                counter++;
            }
        m_cleanUpFlag = false;
System.err.println("Hey I stopped running: "+m_cleanUpFlag);
        }

    public boolean stopSimActor()
        {
System.err.println(""+m_user+" - Clean1: "+m_cleanUpFlag);
        m_SML.removeActionListener(this);
        m_flag1 = false;
        m_flag2 = false;
System.err.println(""+m_user+" - Clean2: "+m_cleanUpFlag);
        int counter = 0;  // So we don't loop forever if the sim actor refuses to stop for some reason.
        while(m_cleanUpFlag)
            {
            try { sleep(500); }
            catch (InterruptedException ie) { ; }
            System.err.println(""+m_user+" - Clean3: "+m_cleanUpFlag);
            if (counter > 3)
                return false;
            counter++; 
            }
        return true;  // Successfully stopped.
        }

    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Sim_Type_ID_INT FROM Simulants_Type_T WHERE Sim_Name_VC = '"+NAME+"'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Sim_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Simulants_Type_T SET Actor_OBJ = ? WHERE Sim_Type_ID_INT = "+index);

                TutorialSimActor sa = new TutorialSimActor();

System.err.println(sa);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,sa,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Actor, ID
                CallableStatement cs = con.prepareCall("{call up_insert_JSimulantType (?, ?, ?)}");

                TutorialSimActor sa = new TutorialSimActor();

System.err.println(sa);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                cs.setString(1,sa.getName());
                Vector v = FMSObjCon.addObjectToStatement(2,sa,cs);
                cs.registerOutParameter(3, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("TutorialSimActor Object ID: "+cs.getInt(3));
                FMSObjCon.cleanUp(v);
                }
            }
        catch( Exception e ) 
            {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(0);
            }
        }
    }
