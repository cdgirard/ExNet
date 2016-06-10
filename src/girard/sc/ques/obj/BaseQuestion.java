package girard.sc.ques.obj;

import girard.sc.expt.awt.ActionBuilderWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;
import girard.sc.qa.awt.FormatQuestionnaireWindow;
import girard.sc.qa.awt.QAWaitWindow;
import girard.sc.qa.io.msg.FinishedQuestionnaireMsg;
import girard.sc.ques.awt.BaseQuestionBuilderWindow;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This is the base class for questions.
 *
 * @author Dudley Girard
 */

public class BaseQuestion extends BaseAction
    {
  
    public BaseQuestion()
        {
        super("BaseQuestion","qaDB","Base_Question_Type_T");
        }
    public BaseQuestion(String name, String db, String dbTable)
        {
        super(name,db,dbTable);
        }

    public void applySettings(Hashtable h)
        {
        super.applySettings(h);
        }

    public Object clone()
        {
        BaseQuestion bq = new BaseQuestion();

        return bq;
        }

    public static void createWaitWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        new QAWaitWindow(app1,app2,app3);

        FinishedQuestionnaireMsg tmp = new FinishedQuestionnaireMsg(null);
        app3.sendMessage(tmp);
        }

    public void displayQuestion()
        {
        }

    public void formatAction(ExptOverlord app, ActionBuilderWindow abw)
        {
        new BaseQuestionBuilderWindow(app,abw,this);
        }
    public void formatQuestion(ExptOverlord app, BaseQuestionBuilderWindow bqbw) 
        {
        bqbw.setEditMode(false);
        }
/**
 * For modifying where to go for the next question base on the answer given in
 * this question.
 *
 */
    public void formatTransition(ExptOverlord app, FormatQuestionnaireWindow fqw)
        {
        fqw.setEditMode(false);
        }

    public final String getInsertFormat()
        {
// ID_INT, App_ID, App_Name_VC, Name_VC, Desc_VC, Settings_OBJ
        return new String("{call "+getStoredProcedure()+" (?, ?, ?, ?, ?, ?)}");
        }
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        return settings;
        }
    public String getStoredProcedure()
        {
        return new String("");
        }

    public Vector initializeTransitions()
        {
        return new Vector();
        }

    public void showQuestion(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3, int question)
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Base_Action_Type_ID_INT FROM Base_Actions_Type_T WHERE Base_Action_Name_VC = 'BaseQuestion'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Base_Action_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Base_Actions_Type_T SET Base_Action_OBJ = ? WHERE Base_Action_Type_ID_INT = "+index);

                BaseQuestion bq = new BaseQuestion();

System.err.println(bq+" "+bq.getName());
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,bq,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JBaseActionType (?, ?, ?, ?)}");

                BaseQuestion bq = new BaseQuestion();

System.err.println(bq);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to build pages for a questionnaire action.");

                cs.setString(1,"BaseQuestion");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,bq,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("BaseQuestion Object ID: "+cs.getInt(4));
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
