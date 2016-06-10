package girard.sc.cc.obj;

/* Is a simulant actor that acts based on what message it receives.  If it receives
   a positive message then it sends either a token or a reward.  If it receives a 
   negative message then it doesn't send a token or it sends a sanction.

   At present only accepts the best offer sent to it.

   Author: Dudley Girard
   Started: 07-17-2001
*/

import girard.sc.cc.awt.CCFormatMsgSimActorWindow;
import girard.sc.cc.io.msg.CCAcceptOfferMsg;
import girard.sc.cc.io.msg.CCNodeSanctionMsg;
import girard.sc.cc.io.msg.CCNodeTokenMsg;
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

public class CCMsgSimActor extends CCSimActor
    {

    public CCMsgSimActor ()
        {
        super ("ccDB", "CCMsgSimActor", "This simulant actor is for the CCNetworkAction and sends tokens/sanctions/rewards based on messages received.");
        m_actionType = "CC Network Action";
        }

    public void applySettings(Hashtable h)
        {
        }

    public Object clone()
        {
        CCMsgSimActor sa = new CCMsgSimActor();

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
        new CCFormatMsgSimActorWindow(app,sbw,(CCMsgSimActor)this.clone());
        }

    public  Hashtable getSettings()
        {
        Hashtable h = new Hashtable();

        return h;
        }

    public void processCCNodeSanctionWindowMsg(ExptMessage em)
        {
        if (getRoundRunning())
            return;

        CCNode node = (CCNode)getNetwork().getExtraData("Me");
        CCNodeSanctions ns = (CCNodeSanctions)node.getExptData("CCNodeSanctions");
        Enumeration enm = ns.getSanctions().elements();
        while (enm.hasMoreElements())
            {
            CCNodeSanction sanction = (CCNodeSanction)enm.nextElement();

            Object[] out_args = new Object[3];
            out_args[0] = new Integer(node.getID()); // From
            out_args[1] = new Integer(sanction.getToNode()); // To
            out_args[2] = new Boolean(true);

            Enumeration enum2 = getNetwork().getNodeList().elements();
            while (enum2.hasMoreElements())
                {
                CCNode n = (CCNode)enum2.nextElement();
                CCNodeFuzzies nf = (CCNodeFuzzies)n.getExptData("CCNodeFuzzies");
                if (nf.hasFuzzy(sanction.getToNode(),node.getID()))
                    {
                    CCNodeFuzzy fuzzy = nf.getFuzzy(sanction.getToNode(),node.getID());
                    if (fuzzy.getMsg())
                        out_args[2] = new Boolean(true);
                    else
                        out_args[2] = new Boolean(false);
                    }
                }

            CCNodeSanctionMsg tmp = new CCNodeSanctionMsg(out_args);
            m_SML.sendMessage(tmp);
            }
        }
    public void processCCNodeTokenWindowMsg(ExptMessage em)
        {
        if (getRoundRunning())
            return;

        CCNode node = (CCNode)getNetwork().getExtraData("Me");
        CCNodeTokens nt = (CCNodeTokens)node.getExptData("CCNodeTokens");
        Enumeration enm = nt.getTokens().elements();
        while (enm.hasMoreElements())
            {
            CCNodeToken token = (CCNodeToken)enm.nextElement();

            Object[] out_args = new Object[3];
            out_args[0] = new Integer(node.getID()); // From
            out_args[1] = new Integer(token.getToNode()); // To
            out_args[2] = new Boolean(true);

            Enumeration enum2 = getNetwork().getNodeList().elements();
            while (enum2.hasMoreElements())
                {
                CCNode n = (CCNode)enum2.nextElement();
                CCNodeFuzzies nf = (CCNodeFuzzies)n.getExptData("CCNodeFuzzies");
                if (nf.hasFuzzy(token.getToNode(),node.getID()))
                    {
                    CCNodeFuzzy fuzzy = nf.getFuzzy(token.getToNode(),node.getID());
                    if (fuzzy.getMsg())
                        out_args[2] = new Boolean(true);
                    else
                        out_args[2] = new Boolean(false);
                    }
                }

            CCNodeTokenMsg tmp = new CCNodeTokenMsg(out_args);
            m_SML.sendMessage(tmp);
            }
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
            if (getRoundRunning())
                {
                CCNode me = (CCNode)getNetwork().getExtraData("Me");
                CCNodeResource myNR = (CCNodeResource)me.getExptData("CCNodeResource");
            
                Enumeration enm = getNetwork().getEdgeList().elements();
                while (enm.hasMoreElements())
                    {
                    CCEdge edge = (CCEdge)enm.nextElement();
                    if ((edge.getActive()) && (edge.getNode1() == me.getID()))
                        {
                        CCNode them = (CCNode)getNetwork().getNode(edge.getNode1());
                        CCNodeResource theirNR = (CCNodeResource)them.getExptData("CCNodeResource");
                        CCExchange myOffer = theirNR.getOffer(edge.getNode1());
                        CCExchange theirOffer = myNR.getOffer(edge.getNode2());

                        if (theirNR != null)
                            {
                            if (theirOffer.getExchangeState() == CCExchange.RED)
                                {
                                myOffer = new CCExchange(theirOffer.getNode2().getResource(),me.getID(),theirOffer.getNode1().getResource(),them.getID());
                                theirNR.addOffer(myOffer);

                                Object[] out_args = new Object[4];
                                out_args[0] = new Integer(me.getID());
                                out_args[1] = new Integer(them.getID());
                                out_args[2] = new Integer(theirOffer.getNode2().getIntResource());
                                out_args[3] = new Integer(theirOffer.getNode1().getIntResource());

                                CCAcceptOfferMsg tmp = new CCAcceptOfferMsg(out_args);
                                m_SML.sendMessage(tmp);

                                theirOffer.setExchangeState(CCExchange.YELLOW);
                                myOffer.setExchangeState(CCExchange.GREEN);
                                }
                            }
                        }
                    if ((edge.getActive()) && (edge.getNode2() == me.getID()))
                        {
                        CCNode them = (CCNode)getNetwork().getNode(edge.getNode2());
                        CCNodeResource theirNR = (CCNodeResource)them.getExptData("CCNodeResource");
                        CCExchange myOffer = theirNR.getOffer(edge.getNode2());
                        CCExchange theirOffer = myNR.getOffer(edge.getNode1());

                        if (theirOffer != null)
                            {
                            if (theirOffer.getExchangeState() == CCExchange.RED)
                                {
                                myOffer = new CCExchange(theirOffer.getNode2().getResource(),me.getID(),theirOffer.getNode1().getResource(),them.getID());
                                theirNR.addOffer(myOffer);

                                Object[] out_args = new Object[4];
                                out_args[0] = new Integer(me.getID());
                                out_args[1] = new Integer(them.getID());
                                out_args[2] = new Integer(theirOffer.getNode2().getIntResource());
                                out_args[3] = new Integer(theirOffer.getNode1().getIntResource());

                                CCAcceptOfferMsg tmp = new CCAcceptOfferMsg(out_args);
                                m_SML.sendMessage(tmp);

                                theirOffer.setExchangeState(CCExchange.YELLOW);
                                myOffer.setExchangeState(CCExchange.GREEN);
                                }
                            }
                        }
                    }
                }
            try { sleep(100); }
            catch (InterruptedException ie) { ; }
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Sim_Type_ID_INT FROM Simulants_Type_T WHERE Sim_Name_VC = 'CCMsgSimActor'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Sim_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Simulants_Type_T SET Actor_OBJ = ? WHERE Sim_Type_ID_INT = "+index);

                CCMsgSimActor sa = new CCMsgSimActor();

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

                CCMsgSimActor sa = new CCMsgSimActor();

System.err.println(sa);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                cs.setString(1,sa.getName());
                Vector v = FMSObjCon.addObjectToStatement(2,sa,cs);
                cs.registerOutParameter(3, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CCMsgSimActor Object ID: "+cs.getInt(3));
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