package girard.sc.be.obj;

/* Is a simulant actor that acts based on the following variables: concession rate,
   inclusion rate, and exclusion rate.  These can be modified should the actor feel
   spiteful based on the variables: start spite and stop spite.  Additional effects
   can occur if the time effect variable is set as well as how initial offer is 
   computed.

   At present only accepts the best offer sent to it.

   Author: Dudley Girard
   Started: 02-15-2001
   Modified: 05-24-2001
*/

import girard.sc.be.awt.BEFormatCSIESimActorWindow;
import girard.sc.expt.awt.SimulantBuilderWindow;
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

public class BECSIESimActor extends BESimActor
    {
    public static final int RANDOM = 1;
    public static final int DEGREE = 2;

    double  m_concessionRate = 0.5;
    double  m_inclusionRate = 0.5;
    double  m_exclusionRate = 0.5;
    double  m_startSpite = 0.1;
    double  m_stopSpite = 0.9;

    boolean m_timeEffect = true;

    int     m_initialOfferMethod = RANDOM;

    public BECSIESimActor ()
        {
        super ("beDB", "BECSIESimActor", "This simulant actor is for the BENetworkAction and uses the following variables: concession rate, inclusion rate, exclusion rate, and spite.");
        m_actionType = "BE Network Action";
        }

    public void applySettings(Hashtable h)
        {
        m_concessionRate = ((Double)h.get("cr")).doubleValue();
        m_inclusionRate = ((Double)h.get("ir")).doubleValue();
        m_exclusionRate = ((Double)h.get("er")).doubleValue();
        m_startSpite = ((Double)h.get("stas")).doubleValue();
        m_stopSpite = ((Double)h.get("stos")).doubleValue();
        m_initialOfferMethod = ((Integer)h.get("iom")).intValue();
        m_timeEffect = ((Boolean)h.get("te")).booleanValue();
        }

    public Object clone()
        {
        BECSIESimActor sa = new BECSIESimActor();

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
        new BEFormatCSIESimActorWindow(app,sbw,(BECSIESimActor)this.clone());
        }

    public double getConcessionRate()
        {
        return m_concessionRate;
        }
    public double getExclusionRate()
        {
        return m_exclusionRate;
        }
    public double getInclusionRate()
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

        h.put("cr",new Double(m_concessionRate));
        h.put("ir",new Double(m_inclusionRate));
        h.put("er",new Double(m_exclusionRate));
        h.put("te",new Boolean(m_timeEffect));
        h.put("stas",new Double(m_startSpite));
        h.put("stos",new Double(m_stopSpite));
        h.put("iom",new Integer(m_initialOfferMethod));

        return h;
        }
    public double getStartSpite()
        {
        return m_startSpite;
        }
    public double getStopSpite()
        {
        return m_stopSpite;
        }
    public boolean getTimeEffect()
        {
        return m_timeEffect;
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

        while (m_flag2)
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
                        if (beer.getN2Keep().getIntResource() > 0)
                            {
                            if (beer.getExchangeState() == BEEdgeResource.GREEN)
                                {
                                Object[] out_args = new Object[4];
                                out_args[0] = new Integer(me.getID());
                                out_args[1] = new Integer(edge.getNode2());
                                out_args[2] = new Integer(beer.getN2Give().getIntResource());
                                out_args[3] = new Integer(beer.getN2Keep().getIntResource());

                                completeExchange(out_args);
                                }
                            if (beer.getExchangeState() == BEEdgeResource.RED)
                                {
                                Object[] out_args = new Object[4];
                                out_args[0] = new Integer(me.getID());
                                out_args[1] = new Integer(edge.getNode2());
                                out_args[2] = new Integer(beer.getN2Give().getIntResource());
                                out_args[3] = new Integer(beer.getN2Keep().getIntResource());

                                acceptOffer(out_args);
                                beer.setExchangeState(BEEdgeResource.YELLOW);
                                }
                            }
                        else if (counter == 10)
                            {
                            int off = (int)(Math.random()*beer.getRes().getIntResource());
                            if (off == 0)
                                off = 1;
                            if (off == beer.getRes().getIntResource())
                                off--;

                            Object[] out_args = new Object[4];
                            out_args[0] = new Integer(me.getID());
                            out_args[1] = new Integer(edge.getNode2());
                            out_args[2] = new Integer(beer.getRes().getIntResource() - off);
                            out_args[3] = new Integer(off);

                            sendOffer(out_args);
            
                            counter = 0;
                            }
                        }
                    }
                if (edge.getNode2() == me.getID())
                    {
                    if ((edge.getActive()) && (beer.getExchangeState() != BEEdgeResource.YELLOW))
                        {
                        if (beer.getN1Keep().getIntResource() > 0)
                            {
                            if (beer.getExchangeState() == BEEdgeResource.GREEN)
                                {
                                Object[] out_args = new Object[4];
                                out_args[0] = new Integer(me.getID());
                                out_args[1] = new Integer(edge.getNode1());
                                out_args[2] = new Integer(beer.getN1Give().getIntResource());
                                out_args[3] = new Integer(beer.getN1Keep().getIntResource());

                                completeExchange(out_args);
                                }
                            if (beer.getExchangeState() == BEEdgeResource.RED)
                                {
                                Object[] out_args = new Object[4];
                                out_args[0] = new Integer(me.getID());
                                out_args[1] = new Integer(edge.getNode1());
                                out_args[2] = new Integer(beer.getN1Give().getIntResource());
                                out_args[3] = new Integer(beer.getN1Keep().getIntResource());

                                acceptOffer(out_args);
                                beer.setExchangeState(BEEdgeResource.YELLOW);
                                }
                            }
                        else if (counter == 10)
                            {
                            int off = (int)(Math.random()*beer.getRes().getIntResource());
                            if (off == 0)
                                off = 1;
                            if (off == beer.getRes().getIntResource())
                                off--;

                            Object[] out_args = new Object[4];
                            out_args[0] = new Integer(me.getID());
                            out_args[1] = new Integer(edge.getNode1());
                            out_args[2] = new Integer(beer.getRes().getIntResource() - off);
                            out_args[3] = new Integer(off);

                            sendOffer(out_args);
     
                            counter = 0;
                            }
                        }
                    }
                }
            try { sleep(300); }
            catch (InterruptedException ie) { ; }
            counter++;
            if (counter > 15)
                counter = 0;
            }
        m_cleanUpFlag = false;
System.err.println("Hey I stopped running: "+m_cleanUpFlag);
        }

    public void setConcessionRate(double value)
        {
        if (value > 1.0)
            value = 1.0;
        if (value < 0)
            value = 0;
        m_concessionRate = value;
        }
    public void setExclusionRate(double value)
        {
        if (value > 1.0)
            value = 1.0;
        if (value < 0)
            value = 0;
        m_exclusionRate = value;
        }
    public void setInclusionRate(double value)
        {
        if (value > 1.0)
            value = 1.0;
        if (value < 0)
            value = 0;
        m_inclusionRate = value;
        }
    public void setInitialOfferMethod(int value)
        {
        m_initialOfferMethod = value;
        }
    public void setStartSpite(double value)
        {
        if (value > 1.0)
            value = 1.0;
        if (value < 0)
            value = 0;
        m_startSpite = value;
        }
    public void setStopSpite(double value)
        {
        if (value > 1.0)
            value = 1.0;
        if (value < 0)
            value = 0;
        m_stopSpite = value;
        }
    public void setTimeEffect(boolean value)
        {
        m_timeEffect = value;
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Sim_Type_ID_INT FROM Simulants_Type_T WHERE Sim_Name_VC = 'BECSIESimActor'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Sim_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Simulants_Type_T SET Actor_OBJ = ? WHERE Sim_Type_ID_INT = "+index);

                BECSIESimActor sa = new BECSIESimActor();

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

                BECSIESimActor sa = new BECSIESimActor();

System.err.println(sa);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                cs.setString(1,sa.getName());
                Vector v = FMSObjCon.addObjectToStatement(2,sa,cs);
                cs.registerOutParameter(3, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("BECSIESimActor Object ID: "+cs.getInt(3));
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