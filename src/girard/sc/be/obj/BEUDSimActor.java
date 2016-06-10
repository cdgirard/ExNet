package girard.sc.be.obj;

/* Is a simulant actor that acts based on the following: reduce demands if excluded
   and increase demands if included.

   At present only accepts the best offer sent to it.

   Author: Dudley Girard
   Started: 01-28-2002
*/

import girard.sc.be.awt.BEFormatUDSimActorWindow;
import girard.sc.be.io.msg.BEEndRoundMsg;
import girard.sc.expt.awt.SimulantBuilderWindow;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class BEUDSimActor extends BESimActor
    {
    public static int EVEN = 1;

    int     m_inclusionRate = 1;
    int     m_exclusionRate = 1;

    int     m_initialOfferMethod = EVEN;

 // Expt variables
    int     m_presentOffer = -1;
    boolean m_excluded = true;
    boolean m_offersSent = false;

    public BEUDSimActor ()
        {
        super ("beDB", "BEUDSimActor", "This simulant actor is for the BENetworkAction and does the following: reduces demand when excluded and increases demand when included.");
        m_actionType = "BE Network Action";
        }

    public void applySettings(Hashtable h)
        {
        m_inclusionRate = ((Integer)h.get("ir")).intValue();
        m_exclusionRate = ((Integer)h.get("er")).intValue();
        m_initialOfferMethod = ((Integer)h.get("iom")).intValue();
        }

    public Object clone()
        {
        BEUDSimActor sa = new BEUDSimActor();

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

    private void findBestOffer()
        {
        BENode me = (BENode)getNetwork().getExtraData("Me");

      // Find best offer
        Vector validRedOffers = new Vector();
        Vector validGreenOffers = new Vector();

        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            if (edge.getNode1() == me.getID())
                {
                if ((edge.getActive()) && (beer.getExchangeState() != BEEdgeResource.YELLOW))
                    {
                    if (beer.getN2Keep().getIntResource() <= m_presentOffer)
                        {
                        if (beer.getExchangeState() == BEEdgeResource.GREEN)
                            {
                            Object[] out_args = new Object[4];
                            out_args[0] = new Integer(me.getID());
                            out_args[1] = new Integer(edge.getNode2());
                            out_args[2] = new Integer(beer.getN2Give().getIntResource());
                            out_args[3] = new Integer(beer.getN2Keep().getIntResource());

                            validGreenOffers.addElement(out_args);
                            }
                        if (beer.getExchangeState() == BEEdgeResource.RED)
                            {
                            Object[] out_args = new Object[4];
                            out_args[0] = new Integer(me.getID());
                            out_args[1] = new Integer(edge.getNode2());
                            out_args[2] = new Integer(beer.getN2Give().getIntResource());
                            out_args[3] = new Integer(beer.getN2Keep().getIntResource());

                            validRedOffers.addElement(out_args);
                            }
                        }
                    }
                }
            if (edge.getNode2() == me.getID())
                {
                if ((edge.getActive()) && (beer.getExchangeState() != BEEdgeResource.YELLOW))
                    {
                    if (beer.getN1Keep().getIntResource() <= m_presentOffer)
                        {
                        if (beer.getExchangeState() == BEEdgeResource.GREEN)
                            {
                            Object[] out_args = new Object[4];
                            out_args[0] = new Integer(me.getID());
                            out_args[1] = new Integer(edge.getNode1());
                            out_args[2] = new Integer(beer.getN1Give().getIntResource());
                            out_args[3] = new Integer(beer.getN1Keep().getIntResource());

                            validGreenOffers.addElement(out_args);
                            }
                        if (beer.getExchangeState() == BEEdgeResource.RED)
                            {
                            Object[] out_args = new Object[4];
                            out_args[0] = new Integer(me.getID());
                            out_args[1] = new Integer(edge.getNode1());
                            out_args[2] = new Integer(beer.getN1Give().getIntResource());
                            out_args[3] = new Integer(beer.getN1Keep().getIntResource());

                            validRedOffers.addElement(out_args);
                            }
                        }
                    }
                }
            }
     // Sort through valid green offers.
        Vector bestOffers = new Vector();
        enm = validGreenOffers.elements();
        while (enm.hasMoreElements())
            {
            Object[] args = (Object[])enm.nextElement();
            if (bestOffers.size() == 0)
                {
                bestOffers.addElement(args);
                }
            else
                {
                Object[] best = (Object[])bestOffers.elementAt(0);
                int oldO = ((Integer)best[2]).intValue();
                int newO = ((Integer)args[2]).intValue();
                if (newO > oldO)
                    {
                    bestOffers.removeAllElements();
                    bestOffers.addElement(args);
                    }
                else if (newO == oldO)
                    {
                    bestOffers.addElement(args);
                    }
                }
            }

     // Sort through valid red offers.
        enm = validRedOffers.elements();
        while (enm.hasMoreElements())
            {
            Object[] args = (Object[])enm.nextElement();
            if (bestOffers.size() == 0)
                {
                bestOffers.addElement(args);
                }
            else
                {
                Object[] best = (Object[])bestOffers.elementAt(0);
                int oldO = ((Integer)best[2]).intValue();
                int newO = ((Integer)args[2]).intValue();
                if (newO > oldO)
                    {
                    bestOffers.removeAllElements();
                    bestOffers.addElement(args);
                    }
                else if (newO == oldO)
                    {
                    bestOffers.addElement(args);
                    }
                }
            }

        if (bestOffers.size() > 0)
            {
            int off = (int)(Math.random()*bestOffers.size());
            Object[] offer = (Object[])bestOffers.elementAt(off);
            if (validGreenOffers.contains(offer))
                {
                completeExchange(offer);
                }
            else
                {
                acceptOffer(offer);
                enm = getNetwork().getEdgeList().elements();
                while (enm.hasMoreElements())
                    {
                    BEEdge edge = (BEEdge)enm.nextElement();
                    BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                    if ((edge.getNode1() == ((Integer)offer[0]).intValue()) && (edge.getNode2() == ((Integer)offer[1]).intValue()))
                        {
                        beer.setExchangeState(BEEdgeResource.YELLOW);
                        break;
                        }
                    if ((edge.getNode1() == ((Integer)offer[0]).intValue()) && (edge.getNode2() == ((Integer)offer[1]).intValue()))
                        {
                        beer.setExchangeState(BEEdgeResource.YELLOW);
                        break;
                        }
                    }
                }
            }
        }

    public void formatActor(ExptOverlord app, SimulantBuilderWindow sbw)
        {
        new BEFormatUDSimActorWindow(app,sbw,(BEUDSimActor)this.clone());
        }

    public int getExclusionRate()
        {
        return m_exclusionRate;
        }
    public int getInclusionRate()
        {
        return m_inclusionRate;
        }
    public int getInitialOfferMethod()
        {
        return m_initialOfferMethod;
        }
    public  Hashtable getSettings()
        {
        Hashtable h = new Hashtable();

        h.put("ir",new Integer(m_inclusionRate));
        h.put("er",new Integer(m_exclusionRate));
        h.put("iom",new Integer(m_initialOfferMethod));

        return h;
        }

    public void processBEEndRoundMsg(ExptMessage em)
        {
        double per = 0;  // Points earned this round.
        BENode me = (BENode)getNetwork().getExtraData("Me");
        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
            if ((edge.getNode1() == me.getID()) && (beer.getExchange() != null))
                {
                per = per + beer.getExchange().getNode1().getResource();
                }
            if ((edge.getNode2() == me.getID()) && (beer.getExchange() != null))
                {
                per = per + beer.getExchange().getNode2().getResource();
                }
            }
        if (per > 0)
            m_excluded = false;
        else
            m_excluded = true;

        Object[] out_args = new Object[2];
        out_args[0] = new Double(per);
        out_args[1] = new Integer(me.getID());
    
        BEEndRoundMsg tmp = new BEEndRoundMsg(out_args);
        m_SML.sendMessage(tmp);
        }
    public void processBEStartRoundMsg(ExptMessage em)
        {
        getNetwork().setExtraData("RoundRunning",new Boolean(true));
        getNetwork().initializeNetwork();

        if (m_presentOffer == -1)
            {
            Enumeration enm = getNetwork().getEdgeList().elements();
            while (enm.hasMoreElements())
                {
                BEEdge edge = (BEEdge)enm.nextElement();
                BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");
                if (m_presentOffer < beer.getRes().getIntResource()/2)
                    {
                    m_presentOffer = beer.getRes().getIntResource()/2;
                    }
                }
            }
        else
            {
            if (m_excluded)
                m_presentOffer++;
            else
                m_presentOffer--;

            if (m_presentOffer <= 0)
                m_presentOffer = 1;

            m_excluded = true;
            }
        m_offersSent = false;
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

        while (m_flag2)
            {
            BENode me = (BENode)getNetwork().getExtraData("Me");

            boolean running = ((Boolean)getNetwork().getExtraData("RoundRunning")).booleanValue();

            if (running)
                {
            // Send out initial Offers.
                if (!m_offersSent)
                    {
                    sendInitialOffers();
                    }

                int sleepTime = (int)(Math.random()*10000);

                try { sleep(sleepTime); }
                catch (InterruptedException ie) { ; }

                findBestOffer();
                }

            try { sleep(100); }
            catch (InterruptedException ie) { ; }
            }
        m_cleanUpFlag = false;
System.err.println("Hey I stopped running: "+m_cleanUpFlag);
        }

    public void setExclusionRate(int value)
        {
        if (value < 0)
            value = 0;
        m_exclusionRate = value;
        }
    public void setInclusionRate(int value)
        {
        if (value < 0)
            value = 0;
        m_inclusionRate = value;
        }
    public void setInitialOfferMethod(int value)
        {
        m_initialOfferMethod = value;
        }

    private void sendInitialOffers()
        {
        BENode me = (BENode)getNetwork().getExtraData("Me");

        Enumeration enm = getNetwork().getEdgeList().elements();
        while (enm.hasMoreElements())
            {
            BEEdge edge = (BEEdge)enm.nextElement();
            BEEdgeResource beer = (BEEdgeResource)edge.getExptData("BEEdgeResource");

            if (edge.getNode1() == me.getID())
                {
                if ((edge.getActive()) && (beer.getExchangeState() != BEEdgeResource.YELLOW))
                    {
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(me.getID());
                    out_args[1] = new Integer(edge.getNode2());
                    if (m_presentOffer < beer.getRes().getIntResource())
                        {
                        out_args[2] = new Integer(beer.getRes().getIntResource() - m_presentOffer);
                        out_args[3] = new Integer(m_presentOffer);
                        }
                    else
                        {
                        out_args[2] = new Integer(beer.getRes().getIntResource() - 1);
                        out_args[3] = new Integer(1);
                        }

                    sendOffer(out_args);
                    }
                }
            else if (edge.getNode2() == me.getID())
                {
                if ((edge.getActive()) && (beer.getExchangeState() != BEEdgeResource.YELLOW))
                    {
                    Object[] out_args = new Object[4];
                    out_args[0] = new Integer(me.getID());
                    out_args[1] = new Integer(edge.getNode1());
                    if (m_presentOffer < beer.getRes().getIntResource())
                        {
                        out_args[2] = new Integer(beer.getRes().getIntResource() - m_presentOffer);
                        out_args[3] = new Integer(m_presentOffer);
                        }
                    else
                        {
                        out_args[2] = new Integer(beer.getRes().getIntResource() - 1);
                        out_args[3] = new Integer(1);
                        }

                    sendOffer(out_args);
                    }
                }
            }
        m_offersSent = true;
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Sim_Type_ID_INT FROM Simulants_Type_T WHERE Sim_Name_VC = 'BEUDSimActor'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Sim_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Simulants_Type_T SET Actor_OBJ = ? WHERE Sim_Type_ID_INT = "+index);

                BEUDSimActor sa = new BEUDSimActor();

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

                BEUDSimActor sa = new BEUDSimActor();

System.err.println(sa);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                cs.setString(1,sa.getName());
                Vector v = FMSObjCon.addObjectToStatement(2,sa,cs);
                cs.registerOutParameter(3, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("BEUDSimActor Object ID: "+cs.getInt(3));
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