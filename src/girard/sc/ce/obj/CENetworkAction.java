package girard.sc.ce.obj;

import girard.sc.ce.awt.CEAddNetworkActionWindow;
import girard.sc.ce.awt.CEBeginExperimentWindow;
import girard.sc.ce.awt.CEFormatNetworkActionWindow;
import girard.sc.ce.awt.CENetworkActionDataDisplay;
import girard.sc.ce.awt.CENetworkActionExperimenterWindow;
import girard.sc.ce.io.msg.CENetworkActionStateMsg;
import girard.sc.ce.io.msg.CEStartNetworkActionReqMsg;
import girard.sc.ce.io.msg.CEStopNetworkActionReqMsg;
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
 * CENetworkAction: An experiment action that uses the CENetwork object
 * as its base for running a network exchange experiment in which resources
 * are tied to the nodes. 
 * <p>
 * <br> Started: 07-23-2002
 * <br> Modified: 01-16-2003
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1 
 */


public class CENetworkAction extends ExperimentAction
    {

    public CENetworkAction()
        {
        super(null,"CE Network Action");
        m_dataDB = "ccDB";
        } 
    public CENetworkAction(CENetwork net)
        {
        super(net,"CE Network Action");
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

        CENetwork cen = new CENetwork();
        cen.applySettings(network);

        setAction(cen);
        m_desc = (String)h.get("Desc");
        if (h.get("DataDB") != null)
            m_dataDB = (String)h.get("DataDB");
        else
            m_dataDB = "ccDB";

        if (h.get("Name") != null)
            m_name = (String)h.get("Name");
        else
            m_name = "CE Network Action";
        }

    public Object clone()
        {
        if (this.getAction() != null)
            {
            CENetworkAction cna = new CENetworkAction((CENetwork)((CENetwork)this.getAction()).clone());
            cna.setActionType(m_ActionType);
            return cna;
            }
        else
            {
            CENetworkAction cna = new CENetworkAction(null);
            cna.setActionType(m_ActionType);
            return cna;
            }
        }

    public void displayData(ExptOverlord app, BaseDataInfo bdi)
        {
	    System.err.println("in the displayData Method of the CENetworkAction");
        new CENetworkActionDataDisplay(app,bdi,this);
        }

    public String getDetailName()
        {
        if (m_action == null)
            return getName();
        else
            {
            CENetwork cen = (CENetwork)m_action;
            return getName()+"-"+cen.getFileName();
            }
        }

    public void formatAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        new CEFormatNetworkActionWindow(app,ebw,this);
        }

    public CEStateAction getNextStateAction()
        {
        CENetwork ceNet = (CENetwork)getAction();
        return ceNet.getNextState();
        }
    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
 
        settings.put("Type",new Integer(m_ActionType));

        CENetwork cen = (CENetwork)getAction();
        Hashtable data = cen.getSettings();
        settings.put("Network",data);
        settings.put("Types",data.get("Types"));
        settings.put("Desc",m_desc);
        settings.put("DataDB",m_dataDB);
        settings.put("Name",m_name);

        return settings;
        }

    public void initializeAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        new CEAddNetworkActionWindow(app,ebw,this);
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        return ((CENetwork)m_action).retrieveData(wlgsc,em,bdi);
        }

/**
 * Make a clone of the present CENetwork then fill in the needed Extra Data that
 * is required for an observer.
 */
    public void sendPresentState(Integer obv, ExperimenterWindow ew)
        {
        Object[] out_args = new Object[1];
        out_args[0] = obv;

        GetExptStateReqMsg tmp = new GetExptStateReqMsg(out_args);
        ew.getSML().sendMessage(tmp);

        CENetwork cen = (CENetwork)getAction();
        CENetwork obvCen = (CENetwork)cen.clone();
        
        double cs = ((Double)cen.getExtraData("CurrentState")).doubleValue();
        boolean rr = ((Boolean)cen.getExtraData("RoundRunning")).booleanValue();
        double[] pay = (double[])cen.getExtraData("Pay");
        double[] pet = (double[])cen.getExtraData("PntEarnedNetwork");
	Hashtable profEarned = (Hashtable)cen.getExtraData("ProfitEarnedNetwork");

        obvCen.setExtraData("CurrentState",new Double(cs));
        obvCen.setExtraData("RoundRunning",new Boolean(rr));
  // Because pay should not change when the experiment is running.
        obvCen.setExtraData("Pay",pay); 
        obvCen.setExtraData("ActionIndex",new Integer(ew.getExpApp().getActionIndex()));
        obvCen.setExtraData("ExperimentName",ew.getExpApp().getExptName());
        double[] obvPet = new double[pet.length];
	for (int x=0;x<pet.length;x++){
            obvPet[x] = pet[x];
	}
	obvCen.setExtraData("PntEarnedNetwork",obvPet);

	Hashtable obvProfEarned = (Hashtable)profEarned.clone();
	obvCen.setExtraData("ProfitEarnedNetwork",obvProfEarned);

        Object[] out_args2 = new Object[2];
        out_args2[0] = obv;
        out_args2[1] = obvCen;

        CENetworkActionStateMsg tmp2 = new CENetworkActionStateMsg(out_args2);
        ew.getSML().sendMessage(tmp2);
        }

    public void startAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        CENetworkActionExperimenterWindow ew = new CENetworkActionExperimenterWindow(app1,app2,app3);

        CENetwork cen = (CENetwork)getAction();

  // Keeps track of the total number of points earned by each user.
        double[] pen = new double[ew.getExpApp().getNumUsers()];
        for (int x=0;x<pen.length;x++)
            {
            pen[x] = 0;
            }
        cen.setExtraData("PntEarnedNetwork",pen);
	cen.setExtraData("ProfitEarnedNetwork",new Hashtable());
        cen.setExtraData("ActionIndex",new Integer(app2.getActionIndex()));
        cen.setExtraData("ExperimentName",app2.getExptName());

  // Is where all the output is kept until being sent to the database.
        cen.setExtraData("Data",new Vector());  

        Object[] out_args = new Object[1];
        out_args[0] = getAction();

        CEStartNetworkActionReqMsg tmp = new CEStartNetworkActionReqMsg(out_args);
        app3.sendMessage(tmp);

        if (cen.getExtraData("InitialWindow") != null)
            {
            Hashtable initialWinSettings = (Hashtable)cen.getExtraData("InitialWindow");
            String str = (String)initialWinSettings.get("Continue");
            if (str.equals("Experimenter"))
                ew.addSubWindow(new CEBeginExperimentWindow(ew));
            }
        }

    public void stopAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        app2.setExptStopping(true);
        ((CENetwork)getAction()).setExtraData("RoundRunning",new Boolean(false));
        app2.initializeReady();
        CEStopNetworkActionReqMsg tmp = new CEStopNetworkActionReqMsg(null);
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Action_Type_ID_INT FROM Actions_Type_T WHERE Action_Name_VC = 'CENetworkAction'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Action_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Actions_Type_T SET Action_OBJ = ? WHERE Action_Type_ID_INT = "+index);

                CENetworkAction cna = new CENetworkAction();

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

                CENetworkAction cna = new CENetworkAction();

System.err.println(cna);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to build an exchange network where commodities, that may be traded, are owned by the nodes.");

                cs.setString(1,"CENetworkAction");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,cna,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("CENetworkAction Object ID: "+cs.getInt(4));
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
