package girard.sc.qa.obj;

import girard.sc.awt.ErrorDialog;
import girard.sc.expt.awt.ExperimenterWindow;
import girard.sc.expt.awt.ExptBuilderWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.GetExptStateReqMsg;
import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.sql.LoadDataResultsReq;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;
import girard.sc.qa.awt.FormatQuestionnaireWindow;
import girard.sc.qa.awt.QABeginExperimentWindow;
import girard.sc.qa.awt.QuestionnaireDataDisplay;
import girard.sc.qa.awt.QuestionnaireExperimenterWindow;
import girard.sc.qa.io.msg.LoadQuestionnaireActionReqMsg;
import girard.sc.qa.io.msg.QuestionnaireStateMsg;
import girard.sc.qa.io.msg.StartQuestionnaireReqMsg;
import girard.sc.qa.io.msg.StopQuestionnaireReqMsg;
import girard.sc.ques.obj.AnswerOutputObject;
import girard.sc.ques.obj.BaseQuestion;
import girard.sc.wl.io.WLGeneralServerConnection;

import java.awt.Font;
import java.awt.Point;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Questionnaire: An experiment action that uses BaseQuestion objects
 * (which is stores in a Vector) as its base for running a Questionnaire 
 * for the subjects.  Each subject gets its own Questionnaire, as all the
 * individual Questionnaires are stored in a Vector.
 * <p>
 * <br> Started: 7-30-2002
 * <br> Modified: 10-15-2002
 * <p>
 * @author: Dudley Girard
 */

public class Questionnaire extends ExperimentAction
    {
    protected String m_detailName = new String("Questionnaire");

/**
 * Used to keep track of what question each subject is on.
 */
    Hashtable m_currentQuestion = new Hashtable();
/**
 * Used to know how each question transitions after an answer is given.
 */
    Vector m_transitions = new Vector();
/**
 * Used to store the answer data.
 */
    Vector m_data = new Vector();

/**
 * Used to store any additional settings information for the Questionnaire.
 */
    Hashtable m_extraData = new Hashtable();

    public Questionnaire()
        {
        super(new Vector(),"Questionaire");
        m_dataDB = "qaDB";
        } 
    public Questionnaire(Vector qa, Vector trans)
        {
        super(qa,"Questionaire");
        m_dataDB = "qaDB";
        m_transitions = trans;
        }

    public void addTransition(int qa, int ques, Integer trans)
        {
        Vector v = (Vector)m_transitions.elementAt(qa);
        Vector v2 = (Vector)v.elementAt(ques);
        v2.addElement(trans);
        }
    public void addTransition(int qa, int ques, int loc, Integer trans)
        {
        Vector v = (Vector)m_transitions.elementAt(qa);
        Vector v2 = (Vector)v.elementAt(ques);
        v2.insertElementAt(trans,loc);
        }

    public boolean allowChangeNumUsers()
        {
        return false;
        }

    public void applySettings(Hashtable h)
        {
        m_ActionType = ((Integer)h.get("Type")).intValue();
        Vector questions = (Vector)h.get("Questionaire");
        Hashtable objects = (Hashtable)h.get("Objects");
        
        Vector actQues = (Vector)m_action;
        Enumeration enm = questions.elements();
        while (enm.hasMoreElements())
            {
            Vector actUserQa = new Vector();
            Vector userQa = (Vector)enm.nextElement();
            Enumeration enum2 = userQa.elements();
            while (enum2.hasMoreElements())
                {
                Hashtable h2 = (Hashtable)enum2.nextElement();
                String type = (String)h2.get("Name");
// System.err.println("Type: "+type+" tp: "+objects.get(type));
                BaseQuestion bq = (BaseQuestion)(((BaseQuestion)objects.get(type)).clone());
                bq.applySettings(h2);
                actUserQa.addElement(bq);
                }
            actQues.addElement(actUserQa);
            }

        m_desc = (String)h.get("Desc");
        m_detailName = (String)h.get("DetailName");
        if (h.get("DataDB") != null)
            m_dataDB = (String)h.get("DataDB");
        else
            m_dataDB = "none";

        if (h.get("Name") != null)
            m_name = (String)h.get("Name");
        else
            m_name = "Questionaire";

        m_transitions = (Vector)h.get("Transitions");

        if (!h.containsKey("ExtraData"))
            {
            Hashtable iw = new Hashtable();
            iw.put("Message","A New Experiment is About to Start.\nPress the READY button to continue.");
            iw.put("FontName","Monospaced");
            iw.put("FontType",new Integer(Font.PLAIN));
            iw.put("FontSize",new Integer(18));
            iw.put("Loc",new Point(0,0));
            iw.put("Continue","Client");
            iw.put("Activate","No");
            setExtraData("InitialWindow",iw);
            }
        else
            {
            m_extraData = (Hashtable)h.get("ExtraData");
            }
        }

    public Object clone()
        {
        if (this.getAction() != null)
            {
            Questionnaire qa = new Questionnaire();
            Vector qaV = (Vector)qa.getAction();
            Vector v = (Vector)m_action;
            Enumeration enm = v.elements();
            while (enm.hasMoreElements())
                {
                Vector qaUserQa = new Vector();
                Vector userQa = (Vector)enm.nextElement();
                Enumeration enum2 = userQa.elements();
                while (enum2.hasMoreElements())
                    {
                    BaseQuestion bq = (BaseQuestion)enum2.nextElement();
                    qaUserQa.addElement(bq.clone());
                    }
                qaV.addElement(qaUserQa);
                }

            Vector newTrans = qa.getTransitions();
            for (int i=0;i<m_transitions.size();i++)
                {
                Vector oldUserV = (Vector)m_transitions.elementAt(i);
                Vector newUserV = new Vector();
                for (int j=0;j<oldUserV.size();j++)
                    {
                    Vector oldQuesTransV = (Vector)oldUserV.elementAt(j);
                    Vector newQuesTransV = new Vector();
                    for (int m=0;m<oldQuesTransV.size();m++)
                        {
                        Integer trans1 = (Integer)oldQuesTransV.elementAt(m);
                        newQuesTransV.addElement(new Integer(trans1.intValue()));
                        }
                    newUserV.addElement(newQuesTransV);
                    }
                newTrans.addElement(newUserV);
                }

            qa.setActionType(m_ActionType);
            qa.setDesc(new String(m_desc));
            qa.setDataDB(m_dataDB);
            qa.setName(m_name);
            qa.setDetailName(m_detailName);

            if (m_extraData.containsKey("InitialWindow"))
                {
                Hashtable h = (Hashtable)getExtraData("InitialWindow");
                Hashtable hNew = new Hashtable();
                hNew.put("Message",new String((String)h.get("Message")));
                hNew.put("FontName",new String((String)h.get("FontName")));
                hNew.put("FontSize",h.get("FontSize"));
                hNew.put("FontType",h.get("FontType"));
                hNew.put("Loc",new Point((Point)h.get("Loc")));
                hNew.put("Continue",new String((String)h.get("Continue")));
                hNew.put("Activate",new String((String)h.get("Activate")));
                qa.setExtraData("InitialWindow",hNew);
                }

            return qa;
            }
        else
            {
            Questionnaire qa = new Questionnaire();
            qa.setActionType(m_ActionType);
            qa.setDesc(new String(m_desc));
            qa.setDataDB(m_dataDB);
            qa.setName(m_name);
            qa.setDetailName(m_detailName);
            return qa;
            }
        }

    public void displayData(ExptOverlord app, BaseDataInfo bdi)
        {
        new QuestionnaireDataDisplay(app,bdi,this);
        }

    public void formatAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        new FormatQuestionnaireWindow(app,ebw,this);
        }

    public Hashtable getCurrentQuestion()
        {
        return m_currentQuestion;
        }
    public Vector getData()
        {
        return m_data;
        }
    public String getDetailName()
        {
        return m_detailName;
        }
    public Hashtable getExtraData()
        {
        return m_extraData;
        }
    public Object getExtraData(String str)
        {
        if (m_extraData.containsKey(str))
            return m_extraData.get(str);
        return null;
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
            Vector userQa = new Vector();
            Vector questions = (Vector)enm.nextElement();
            Enumeration enum2 = questions.elements();
            while (enum2.hasMoreElements())
                {
                BaseQuestion bq = (BaseQuestion)enum2.nextElement();
                tmpTypes.put(bq.getName(),bq.getName());
                userQa.addElement(bq.getSettings());
                }
            v.addElement(userQa);
            }

        Vector types = new Vector();
        enm = tmpTypes.elements();
        while (enm.hasMoreElements())
            {
            types.addElement(enm.nextElement());
            }

        settings.put("Questionaire",v);
        settings.put("Transitions",m_transitions);
        settings.put("Types",types);
        settings.put("Desc",m_desc);
        settings.put("DataDB",m_dataDB);
        settings.put("Desc",m_desc);
        settings.put("Name",m_name);
        settings.put("DetailName",m_detailName);
        settings.put("ExtraData",m_extraData);

        return settings;
        }
    public Vector getTransitions()
        {
        return m_transitions;
        }

    public void initializeAction(ExptOverlord app, ExptBuilderWindow ebw)
        {
        Vector tut = new Vector();
        Vector trans = new Vector();
        for (int x=0;x<ebw.getExpApp().getNumUsers();x++)
            {
            tut.addElement(new Vector());
            trans.addElement(new Vector());
            }
        setAction(tut);
        m_transitions = trans;

        Hashtable iw = new Hashtable();
        iw.put("Message","A New Experiment is About to Start.\nPress the READY button to continue.");
        iw.put("FontName","Monospaced");
        iw.put("FontType",new Integer(Font.PLAIN));
        iw.put("FontSize",new Integer(18));
        iw.put("Loc",new Point(0,0));
        iw.put("Continue","Client");
        iw.put("Activate","No");
        setExtraData("InitialWindow",iw);

        ebw.addAction(getName(),this);

        ebw.setEditMode(false);
        }
/**
 * Load up all the BaseQuestions here.
 */
    public void initializeAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        Object[] out_args = new Object[1];
        out_args[0] = m_action;
        LoadQuestionnaireActionReqMsg tmp = new LoadQuestionnaireActionReqMsg(out_args);
        ExptMessage em = app1.sendExptMessage(tmp);

    // Try one more time if get nothing.
        if (em == null)
            {
            em = app1.sendExptMessage(tmp);
            }

        if (em == null)
            {
            new ErrorDialog("No message returned, error loading Questionnaire or connecting.");
            return;
            }

        if (em instanceof LoadQuestionnaireActionReqMsg)
            {
            Object[] args = em.getArgs();
            m_action = args[0];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }

    public Hashtable retrieveData(WLGeneralServerConnection wlgsc, ExptMessage em, BaseDataInfo bdi)
        {
        try 
            {
            Hashtable quesData = new Hashtable();

            LoadDataResultsReq tmp = new LoadDataResultsReq("qaDB","Question_Data_T",bdi,wlgsc,em);

            ResultSet rs = tmp.runQuery();

            if (rs == null)
                {
                return quesData;
                }

            while (rs.next())
                {
                AnswerOutputObject aoo = new AnswerOutputObject(rs);

                if (quesData.containsKey(""+aoo.getUserIndex()))
                    {
                    Vector ans = (Vector)quesData.get(""+aoo.getUserIndex());
                    if (aoo.getQuestionIndex() >= ans.size())
                        {
                        ans.addElement(aoo);
                        }
                    else
                        {
                        ans.insertElementAt(aoo,aoo.getQuestionIndex());
                        }
                    }
                else
                    {
                    Vector ans = new Vector();
                    ans.addElement(aoo);
                    quesData.put(""+aoo.getUserIndex(),ans);
                    }
                }
  
            return quesData;
            }
        catch(Exception e) 
            {
            wlgsc.addToLog(e.getMessage());
            return new Hashtable();
            }
        }

    public void sendPresentState(Integer obv, ExperimenterWindow ew)
        {
        Object[] out_args = new Object[1];
        out_args[0] = obv;

        GetExptStateReqMsg tmp = new GetExptStateReqMsg(out_args);
        ew.getSML().sendMessage(tmp);

        Hashtable h = new Hashtable();
        h.put("Questionaire",m_action);
        h.put("Transitions",m_transitions);

        Object[] out_args2 = new Object[3];
        out_args2[0] = obv;
        out_args2[1] = h;
        out_args2[2] = m_currentQuestion;
        QuestionnaireStateMsg tmp2 = new QuestionnaireStateMsg(out_args2);
        ew.getSML().sendMessage(tmp2);
        }

    public void setDetailName(String str)
        {
        m_detailName = str;
        }
    public void setExtraData(Hashtable h)
        {
        m_extraData = h;
        }
    public void setExtraData(String key, Object obj)
        {
        m_extraData.put(key,obj);
        }
    public void setTransitions(Vector v)
        {
        m_transitions = v;
        }

    public void startAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        QuestionnaireExperimenterWindow ew = new QuestionnaireExperimenterWindow(app1,app2,app3);

        Hashtable h = new Hashtable();
        h.put("Questionaire",m_action);
        h.put("Transitions",m_transitions);
        h.put("ExtraData",m_extraData);

        Object[] out_args = new Object[1];
        out_args[0] = h;

        StartQuestionnaireReqMsg tmp = new StartQuestionnaireReqMsg(out_args);
        app3.sendMessage(tmp);

        if (getExtraData("InitialWindow") != null)
            {
            Hashtable initialWinSettings = (Hashtable)getExtraData("InitialWindow");
            String a = (String)initialWinSettings.get("Activate");
            if (a.equals("Yes"))
                {
                String str = (String)initialWinSettings.get("Continue");
                if (str.equals("Experimenter"))
                    new QABeginExperimentWindow(ew);
                }
            }
        }

    public void stopAction(ExptOverlord app1, Experiment app2, ExptMessageListener app3)
        {
        app2.setExptStopping(true);
        app2.initializeReady();
        StopQuestionnaireReqMsg tmp = new StopQuestionnaireReqMsg(null);
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Action_Type_ID_INT FROM Actions_Type_T WHERE Action_Name_VC = 'Questionaire'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Action_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Actions_Type_T SET Action_OBJ = ?, Action_Desc_VC = ? WHERE Action_Type_ID_INT = "+index);

                Questionnaire qa = new Questionnaire();
                String str = new String("Allows you to build a questionnaire consisting of a set of BaseQuestions for each subject.");

System.err.println(qa);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,qa,ps);
                ps.setString(2,str);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Questionnaire, id
                CallableStatement cs = con.prepareCall("{call up_insert_JActionType (?, ?, ?, ?)}");

                Questionnaire qa = new Questionnaire(); 

System.err.println(qa);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to build a questionnaire consisting of a set of BaseQuestions for each subject.");

                cs.setString(1,"Questionaire");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,qa,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("Questionnaire Object ID: "+cs.getInt(4));
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