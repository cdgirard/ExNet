package girard.sc.be.obj;

import girard.sc.be.awt.BEAddNetworkActionWindow;
import girard.sc.be.awt.BEBeginExperimentWindow;
import girard.sc.be.awt.BEFormatNetworkActionWindow;
import girard.sc.be.awt.BENetworkActionDataDisplay;
import girard.sc.be.awt.BENetworkActionExperimenterWindow;
import girard.sc.be.io.msg.BENetworkActionStateMsg;
import girard.sc.be.io.msg.BEStartNetworkActionReqMsg;
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

/**
 * BENetworkAction: An experiment action that uses the BENetwork object
 * as its base for running a network exchange experiment in which resources
 * are tied to the edges.
 * <p>
 * <br> Started: 09-26-2000
 * <br> Modified: 5-2-2001
 * <br> Modified: 5-18-2001
 * <p>
 * @author Dudley Girard
 */

public class BENetworkAction extends ExperimentAction
    {

    public BENetworkAction()
        {
        super(null,"BE Network Action");
        m_dataDB = "beDB";
        } 
    public BENetworkAction(BENetwork net)
        {
        super(net,"BE Network Action");
        m_dataDB = "beDB";
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

        BENetwork ben = new BENetwork();
        ben.applySettings(network);

        setAction(ben);
        m_desc = (String)h.get("Desc");
        if (h.get("DataDB") != null)
            m_dataDB = (String)h.get("DataDB");
        else
            m_dataDB = "beDB";

        if (h.get("Name") != null)
            m_name = (String)h.get("Name");
        else
            m_name = "BE Network Action";
        }

    public Object clone()
        {
        if (this.getAction() != null)
            {
            BENetworkAction ben = new BENetworkAction((BENetwork)((BENetwork)this.getAction()).clone());
            ben.setActionType(m_ActionType);
            ben.setDataDB(m_dataDB);
            ben.setDesc(new String(m_desc));
            ben.setName(m_name);
            return ben;
            }
        else
            {
            BENetworkAction ben = new BENetworkAction();
            ben.setActionType(m_ActionType);
            ben.setDataDB(m_dataDB);
            ben.setDesc(new String(m_desc));
            ben.setName(m_name);
            return ben;
            }
        }

    public void displayData(ExptOverlord app, BaseDataInfo bdi)
        {
        new BENetworkActionDataDisplay(app,bdi,this);
        }

    public String getDetailName()
        {
        if (m_action == null)
            return getName();
        else
            {
            BENetwork ben = (BENetwork)m_action;
            return getName()+"-"+ben.getFileName();
            }
        }

    public void formatAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        new BEFormatNetworkActionWindow(app,ebw,this);
        }

    public BEStateAction getNextStateAction()
        {
        BENetwork beNet = (BENetwork)getAction();

        return beNet.getNextState();
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
 
        settings.put("Type",new Integer(m_ActionType));

        BENetwork ben = (BENetwork)getAction();
        Hashtable data = ben.getSettings();
        settings.put("Network",data);
        settings.put("Types",data.get("Types"));
        settings.put("DataDB",m_dataDB);
        settings.put("Desc",m_desc);
        settings.put("Name",m_name);

        return settings;
        }

    public void initializeAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        new BEAddNetworkActionWindow(app,ebw,this);
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        return ((BENetwork)m_action).retrieveData(wlgsc,em,bdi);
        }

    public void sendPresentState(Integer obv, ExperimenterWindow ew)
        {
        Object[] out_args = new Object[1];
        out_args[0] = obv;

        GetExptStateReqMsg tmp = new GetExptStateReqMsg(out_args);
        ew.getSML().sendMessage(tmp);

        BENetwork ben = (BENetwork)getAction();
        BENetwork obvBen = (BENetwork)ben.clone();
        
        double cs = ((Double)ben.getExtraData("CurrentState")).doubleValue();
        boolean rr = ((Boolean)ben.getExtraData("RoundRunning")).booleanValue();
        double[] pay = (double[])ben.getExtraData("Pay");
        double[] pet = (double[])ben.getExtraData("PntEarnedNetwork");

        obvBen.setExtraData("CurrentState",new Double(cs));
        obvBen.setExtraData("RoundRunning",new Boolean(rr));
        obvBen.setExtraData("Pay",pay); // Because pay should not change when the experiment is running.
        
        double[] obvPet = new double[pet.length];
        for (int x=0;x<pet.length;x++)
            {
            obvPet[x] = pet[x];
            }
        obvBen.setExtraData("PntEarnedNetwork",obvPet);

        Object[] out_args2 = new Object[2];
        out_args2[0] = obv;
        out_args2[1] = obvBen;

        BENetworkActionStateMsg tmp2 = new BENetworkActionStateMsg(out_args2);
        ew.getSML().sendMessage(tmp2);
        }

    public void startAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        BENetworkActionExperimenterWindow ew = new BENetworkActionExperimenterWindow(app1,app2,app3);

        BENetwork ben = (BENetwork)getAction();

        double[] pen = new double[ew.getExpApp().getNumUsers()];
        for (int x=0;x<pen.length;x++)
            {
            pen[x] = 0;
            }
        ben.setExtraData("PntEarnedNetwork",pen);  // Keeps track of the total number of points earned by each user.

        ben.setExtraData("Data",new Vector());  // Is where all the output is kept until being sent to the database.

        Object[] out_args = new Object[1];
        out_args[0] = getAction();

        BEStartNetworkActionReqMsg tmp = new BEStartNetworkActionReqMsg(out_args); 
        app3.sendMessage(tmp);

        if (ben.getExtraData("InitialWindow") != null)
            {
            Hashtable initialWinSettings = (Hashtable)ben.getExtraData("InitialWindow");
            String str = (String)initialWinSettings.get("Continue");
            if (str.equals("Experimenter"))
                ew.addSubWindow(new BEBeginExperimentWindow(ew));
            }
        }

    public void stopAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        }

    public static void updateDBEntry(Connection con)
        {
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Action_Type_ID_INT FROM Actions_Type_T WHERE Action_Name_VC = 'BENetworkAction'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Action_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Actions_Type_T SET Action_OBJ = ? WHERE Action_Type_ID_INT = "+index);

                BENetworkAction net = new BENetworkAction();

System.err.println(net);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,net,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JActionType (?, ?, ?, ?)}");

                BENetworkAction net = new BENetworkAction();

System.err.println(net);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to build an exchange network where resource pools are kept on the edges.");

                cs.setString(1,"BENetworkAction");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,net,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("BENetworkAction Object ID: "+cs.getInt(4));
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