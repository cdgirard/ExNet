package girard.sc.tut.obj;

import girard.sc.awt.ErrorDialog;
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
import girard.sc.tp.obj.TutorialPage;
import girard.sc.tut.awt.FormatTutorialActionWindow;
import girard.sc.tut.awt.TutorialActionExperimenterWindow;
import girard.sc.tut.io.msg.LoadTutorialActionReqMsg;
import girard.sc.tut.io.msg.StartTutorialActionReqMsg;
import girard.sc.tut.io.msg.StopTutorialActionReqMsg;
import girard.sc.tut.io.msg.TutorialActionStateMsg;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/*   TutorialAction: An experiment action that uses TutorialPage objects
     (which is stores in a Vector) as its base for running a tutorial 
     for the subjects.

     Author: Dudley Girard
     Started: 11-15-2001
     Modified: 2-28-2002
*/

public class TutorialAction extends ExperimentAction
    {
    protected String m_detailName = new String("Tutorial Action");

    Hashtable m_currentPage = new Hashtable();

    public TutorialAction()
        {
        super(new Vector(),"Tutorial Action");
        m_dataDB = "tpDB";
        } 
    public TutorialAction(Vector v)
        {
        super(v,"Tutorial Action");
        m_dataDB = "tpDB";
        }

    public boolean allowChangeNumUsers()
        {
        return false;
        }

    public void applySettings(Hashtable h)
        {
        m_ActionType = ((Integer)h.get("Type")).intValue();
        Vector pages = (Vector)h.get("TutorialPages");
        Hashtable objects = (Hashtable)h.get("Objects");
        
        Vector actPages = (Vector)m_action;
        Enumeration enm = pages.elements();
        while (enm.hasMoreElements())
            {
            Vector actUserTut = new Vector();
            Vector userTut = (Vector)enm.nextElement();
            Enumeration enum2 = userTut.elements();
            while (enum2.hasMoreElements())
                {
                Hashtable h2 = (Hashtable)enum2.nextElement();
                String type = (String)h2.get("Name");
// System.err.println("Type: "+type+" tp: "+objects.get(type));
                TutorialPage tp = (TutorialPage)(((TutorialPage)objects.get(type)).clone());
                tp.applySettings(h2);
                actUserTut.addElement(tp);
                }
            actPages.addElement(actUserTut);
            }

        m_desc = (String)h.get("Desc");
        m_detailName = (String)h.get("DetailName");
        if (h.get("DataDB") != null)
            m_dataDB = (String)h.get("DataDB");
        else
            m_dataDB = "tpDB";

        if (h.get("Name") != null)
            m_name = (String)h.get("Name");
        else
            m_name = "Tutorial Action";
        }

    public Object clone()
        {
        if (this.getAction() != null)
            {
            TutorialAction ta = new TutorialAction();
            Vector taV = (Vector)ta.getAction();
            Vector v = (Vector)m_action;
            Enumeration enm = v.elements();
            while (enm.hasMoreElements())
                {
                Vector taUserTut = new Vector();
                Vector userTut = (Vector)enm.nextElement();
                Enumeration enum2 = userTut.elements();
                while (enum2.hasMoreElements())
                    {
                    TutorialPage tp = (TutorialPage)enum2.nextElement();
                    taUserTut.addElement(tp.clone());
                    }
                taV.addElement(taUserTut);
                }
            ta.setActionType(m_ActionType);
            ta.setDesc(new String(m_desc));
            ta.setDataDB(m_dataDB);
            ta.setName(m_name);
            return ta;
            }
        else
            {
            TutorialAction ta = new TutorialAction();
            ta.setActionType(m_ActionType);
            ta.setDesc(new String(m_desc));
            ta.setDataDB(m_dataDB);
            ta.setName(m_name);
            return ta;
            }
        }

    public void displayData(ExptOverlord app, BaseDataInfo bdi)
        {
        app.setEditMode(false);
        }

    public Hashtable getCurrentPage()
        {
        return m_currentPage;
        }
    public String getDetailName()
        {
        return m_detailName;
        }

    public void formatAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        new FormatTutorialActionWindow(app,ebw,this);
        }

    public Hashtable getSettings()
        {
        Hashtable settings = new Hashtable();
 
        settings.put("Type",new Integer(m_ActionType));

        Vector v = new Vector();
        Hashtable tmpTypes = new Hashtable();
        Enumeration enm = ((Vector)m_action).elements();
        while (enm.hasMoreElements())
            {
            Vector userTut = new Vector();
            Vector pages = (Vector)enm.nextElement();
            Enumeration enum2 = pages.elements();
            while (enum2.hasMoreElements())
                {
                TutorialPage tp = (TutorialPage)enum2.nextElement();
                tmpTypes.put(tp.getName(),tp.getName());
                userTut.addElement(tp.getSettings());
                }
            v.addElement(userTut);
            }

        Vector types = new Vector();
        enm = tmpTypes.elements();
        while (enm.hasMoreElements())
            {
            types.addElement(enm.nextElement());
            }

        settings.put("TutorialPages",v);
        settings.put("Types",types);
        settings.put("Desc",m_desc);
        settings.put("DataDB",m_dataDB);
        settings.put("Desc",m_desc);
        settings.put("Name",m_name);
        settings.put("DetailName",m_detailName);

        return settings;
        }

/**
 * Load up all the tutorial pages here.
 */
    public void initializeAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        Object[] out_args = new Object[1];
        out_args[0] = m_action;
        LoadTutorialActionReqMsg tmp = new LoadTutorialActionReqMsg(out_args);
        ExptMessage em = app1.sendExptMessage(tmp);

    // Try one more time if get nothing.
        if (em == null)
            {
            em = app1.sendExptMessage(tmp);
            }

        if (em == null)
            {
            new ErrorDialog("No message returned, error loading Tutorial or connecting.");
            return;
            }

        if (em instanceof LoadTutorialActionReqMsg)
            {
            Object[] args = em.getArgs();
            m_action = args[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }
    public void initializeAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        TutorialAction ta = (TutorialAction)this.clone();

        Vector tut = new Vector();
        for (int x=0;x<ebw.getExpApp().getNumUsers();x++)
            {
            Vector tmpVec = new Vector();
            tut.addElement(tmpVec);
            }
        ta.setAction(tut);

        ebw.addAction(ta.getName(),ta);

        ebw.setEditMode(false);
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        // return ((BENetwork)m_action).retrieveData(wlgsc,em,bdi);
        return new Hashtable();
        }

    public void sendPresentState(Integer obv, ExperimenterWindow ew)
        {
        Object[] out_args = new Object[1];
        out_args[0] = obv;

        GetExptStateReqMsg tmp = new GetExptStateReqMsg(out_args);
        ew.getSML().sendMessage(tmp);

        Object[] out_args2 = new Object[3];
        out_args2[0] = obv;
        out_args2[1] = m_action;
        out_args2[2] = m_currentPage;
        TutorialActionStateMsg tmp2 = new TutorialActionStateMsg(out_args2);
        ew.getSML().sendMessage(tmp2);
        }

    public void setDetailName(String str)
        {
        m_detailName = str;
        }

    public void startAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        TutorialActionExperimenterWindow ew = new TutorialActionExperimenterWindow(app1,app2,app3);

        Object[] out_args2 = new Object[1];
        out_args2[0] = m_action;

        StartTutorialActionReqMsg tmp2 = new StartTutorialActionReqMsg(out_args2);
        
        app3.sendMessage(tmp2);
        }

    public void stopAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        app2.setExptStopping(true);
        app2.initializeReady();
        StopTutorialActionReqMsg tmp = new StopTutorialActionReqMsg(null);
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Action_Type_ID_INT FROM Actions_Type_T WHERE Action_Name_VC = 'Tutorial Action'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Action_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Actions_Type_T SET Action_OBJ = ? WHERE Action_Type_ID_INT = "+index);

                TutorialAction ta = new TutorialAction();

System.err.println(ta);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,ta,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, TutorialAction, id
                CallableStatement cs = con.prepareCall("{call up_insert_JActionType (?, ?, ?, ?)}");

                TutorialAction ta = new TutorialAction();

System.err.println(ta);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to build a tutorial consisting of a set of tutorial pages.");

                cs.setString(1,"Tutorial Action");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,ta,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("TutorialAction Object ID: "+cs.getInt(4));
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