package girard.sc.cc.obj;

/*   CCNetworkAction: An experiment action that uses the CCNetwork object
     as its base for running a network exchange experiment in which resources
     are tied to the nodes. In addition some nodes have the ability to send 
     warm fuzzies or tokens which in turn can trigger sanctions.

     Author: Dudley Girard
     Started:  05-25-2001
     Modified: 07-24-2001
*/


import girard.sc.cc.awt.CCAddNetworkActionWindow;
import girard.sc.cc.awt.CCFormatNetworkActionWindow;
import girard.sc.cc.awt.CCNetworkActionDataDisplay;
import girard.sc.cc.awt.CCNetworkActionExperimenterWindow;
import girard.sc.cc.io.msg.CCNetworkActionStateMsg;
import girard.sc.cc.io.msg.CCStartNetworkActionReqMsg;
import girard.sc.cc.io.msg.CCStopNetworkActionReqMsg;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetExptStateReqMsg;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

public class CCNetworkAction extends ExperimentAction
    {

    public CCNetworkAction()
        {
        super(null,"CC Network Action");
        m_dataDB = "ccDB";
        } 
    public CCNetworkAction(CCNetwork net)
        {
        super(net,"CC Network Action");
        m_dataDB = "ccDB";
        }

    public boolean allowChangeNumUsers()
        {
        return false;
        }

    public void applySettings(Hashtable h)
        {
        m_ActionType = ((Integer)h.get("Type")).intValue();
        Hashtable network = (Hashtable)h.get("Network");
        Hashtable objects = (Hashtable)h.get("Objects");
        network.put("Objects",objects);

        CCNetwork ccn = new CCNetwork();
        ccn.applySettings(network);

        setAction(ccn);
        m_desc = (String)h.get("Desc");
        if (h.get("DataDB") != null)
            m_dataDB = (String)h.get("DataDB");
        else
            m_dataDB = "ccDB";

        if (h.get("Name") != null)
            m_name = (String)h.get("Name");
        else
            m_name = "CC Network Action";
        }

    public Object clone()
        {
        if (this.getAction() != null)
            {
            CCNetworkAction cna = new CCNetworkAction((CCNetwork)((CCNetwork)this.getAction()).clone());
            cna.setActionType(m_ActionType);
            cna.setDesc(new String(m_desc));
            cna.setDataDB(m_dataDB);
            cna.setName(m_name);
            return cna;
            }
        else
            {
            CCNetworkAction cna = new CCNetworkAction(null);
            cna.setActionType(m_ActionType);
            cna.setDesc(new String(m_desc));
            cna.setDataDB(m_dataDB);
            cna.setName(m_name);
            return cna;
            }
        }

    public void displayData(ExptOverlord app, BaseDataInfo bdi)
        {
        new CCNetworkActionDataDisplay(app,bdi,this);
        }

    public String getDetailName()
        {
        if (m_action == null)
            return getName();
        else
            {
            CCNetwork ccn = (CCNetwork)m_action;
            return getName()+"-"+ccn.getFileName();
            }
        }

    public void formatAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        new CCFormatNetworkActionWindow(app,ebw,this);
        }

    public CCStateAction getNextStateAction()
        {
        CCNetwork ccNet = (CCNetwork)getAction();

        return ccNet.getNextState();
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
 
        settings.put("Type",new Integer(m_ActionType));

        CCNetwork ccn = (CCNetwork)getAction();
        Hashtable data = ccn.getSettings();
        settings.put("Network",data);
        settings.put("Types",data.get("Types"));
        settings.put("Desc",m_desc);
        settings.put("DataDB",m_dataDB);
        settings.put("Name",m_name);

        return settings;
        }

    public void initializeAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        new CCAddNetworkActionWindow(app,ebw,this);
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        return ((CCNetwork)m_action).retrieveData(wlgsc,em,bdi);
        }

/* Make a clone of the present CCNetwork then fill in the needed Extra Data that
   is required for an observer.
*/
    public void sendPresentState(Integer obv, ExperimenterWindow ew)
        {
        Object[] out_args = new Object[1];
        out_args[0] = obv;

        GetExptStateReqMsg tmp = new GetExptStateReqMsg(out_args);
        ew.getSML().sendMessage(tmp);

        CCNetwork ccn = (CCNetwork)getAction();
        CCNetwork obvCcn = (CCNetwork)ccn.clone();
        
        double cs = ((Double)ccn.getExtraData("CurrentState")).doubleValue();
        boolean rr = ((Boolean)ccn.getExtraData("RoundRunning")).booleanValue();
        double[] pay = (double[])ccn.getExtraData("Pay");
        double[] pet = (double[])ccn.getExtraData("PntEarnedNetwork");

        obvCcn.setExtraData("CurrentState",new Double(cs));
        obvCcn.setExtraData("RoundRunning",new Boolean(rr));
        obvCcn.setExtraData("Pay",pay); // Because pay should not change when the experiment is running.
        
        double[] obvPet = new double[pet.length];
        for (int x=0;x<pet.length;x++)
            {
            obvPet[x] = pet[x];
            }
        obvCcn.setExtraData("PntEarnedNetwork",obvPet);

        Object[] out_args2 = new Object[2];
        out_args2[0] = obv;
        out_args2[1] = obvCcn;

        CCNetworkActionStateMsg tmp2 = new CCNetworkActionStateMsg(out_args2);
        ew.getSML().sendMessage(tmp2);
        }

    public void startAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        new CCNetworkActionExperimenterWindow(app1,app2,app3);

        Object[] out_args = new Object[1];
        out_args[0] = getAction();

        CCStartNetworkActionReqMsg tmp = new CCStartNetworkActionReqMsg(out_args);
        
        app3.sendMessage(tmp);
        }

    public void stopAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        app2.setExptStopping(true);
        ((CCNetwork)getAction()).setExtraData("RoundRunning",new Boolean(false));
        app2.initializeReady();
        CCStopNetworkActionReqMsg tmp = new CCStopNetworkActionReqMsg(null);
        app3.sendMessage(tmp);
        }

    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Action_Type_ID_INT FROM Actions_Type_T WHERE Action_Name_VC = 'CCNetworkAction'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Action_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Actions_Type_T SET Action_OBJ = ? WHERE Action_Type_ID_INT = "+index);

                CCNetworkAction cna = new CCNetworkAction();

System.err.println(cna);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,cna,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JActionType (?, ?, ?, ?)}");

                CCNetworkAction cna = new CCNetworkAction();

System.err.println(cna);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to build an exchange network where resources/tokens/fuzzies/sanctions are kept by nodes.");

                cs.setString(1,"CCNetworkAction");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,cna,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CCNetworkAction Object ID: "+cs.getInt(4));
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