package girard.sc.likert.obj;

import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;
import girard.sc.likert.awt.ClientLikertWindow;
import girard.sc.likert.awt.DisplayLikertQuestionWindow;
import girard.sc.likert.awt.FormatLikertQuestionWindow;
import girard.sc.likert.awt.LikertTransitionWindow;
import girard.sc.qa.awt.FormatQuestionnaireWindow;
import girard.sc.ques.awt.BaseQuestionBuilderWindow;
import girard.sc.ques.obj.BaseQuestion;

import java.awt.Font;
import java.awt.Point;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Is the likert question object for asking subjects likert questions.
 * <p>
 * Started: 7-29-2002
 * <p>
 * @author Dudley Girard
 */

public class LikertQuestion extends BaseQuestion
    {
    protected static final String OBJ_NAME = new String("Likert Question");
    protected static final String DB = new String("qaDB");
    protected static final String DB_TABLE = new String("Likert_T");

/**
 * The question to ask the subjects.
 */
    protected String m_question = new String("-");
/**
 * The text to be displayed on the right end of the scale.
 */
    protected String m_right = new String ("High");
/**
 * The text to be displayed in the center of the scale.
 */
    protected String m_center = new String ("Neutral");
/**
 * The text to be displayed on the left end of the scale.
 */
    protected String m_left = new String("Low");
    protected int m_range = 5;

    protected String m_title = new String("Question");
    protected int m_winColumns = 50;
    protected int m_winRows = 10;
    protected String m_winFontName = new String("Monospaced");
    protected int m_winFontStyle = Font.PLAIN;
    protected int m_winFontSize = 12;
    protected Point m_winLoc = new Point(0,0);
  
    public LikertQuestion()
        {
        super(OBJ_NAME,DB,DB_TABLE);

        m_desc = new String("Allows you to build a likert question.");
        }
    public LikertQuestion(String ques, String title)
        {
        super(OBJ_NAME,DB,DB_TABLE);

        m_desc = new String("Allows you to build a likert question.");

        m_question = ques;
        m_title = title;        
        }

    public void applySettings(Hashtable h)
        {
        super.applySettings(h);

        m_question = (String)h.get("Question");
        m_title = (String)h.get("Title");
        m_right = (String)h.get("Right");
        m_left = (String)h.get("Left");
        m_center = (String)h.get("Center");
        m_range = ((Integer)h.get("Range")).intValue();

        m_winColumns = ((Integer)h.get("WinCol")).intValue();
        m_winRows = ((Integer)h.get("WinRows")).intValue();
        m_winFontName = (String)h.get("WinFontName");
        m_winFontStyle = ((Integer)h.get("WinFontType")).intValue();
        m_winFontSize = ((Integer)h.get("WinFontSize")).intValue();
        m_winLoc = (Point)h.get("WinLoc");

        m_db = DB;
        m_dbTable = DB_TABLE;
        }

    public Object clone()
        {
        LikertQuestion lq = new LikertQuestion();

        lq.applySettings(this.getSettings());

        return lq;
        }

    public void displayQuestion()
        {
        new DisplayLikertQuestionWindow(this);
        }

    public void formatQuestion(ExptOverlord app, BaseQuestionBuilderWindow bqbw) 
        {
        new FormatLikertQuestionWindow(app,bqbw,this);
        }
    public void formatTransition(ExptOverlord app, FormatQuestionnaireWindow fqw)
        {
        new LikertTransitionWindow(app, fqw, this);
        }
    
    public String getCenter()
        {
        return m_center;
        }
    public String getLeft()
        {
        return m_left;
        }
    public String getQuestion()
        {
        return m_question;
        }
    public int getRange()
        {
        return m_range;
        }
    public String getRight()
        {
        return m_right;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        settings.put("Question",m_question);
        settings.put("Title",m_title);
        settings.put("Right",m_right);
        settings.put("Left",m_left);
        settings.put("Center",m_center);
        settings.put("Range",new Integer(m_range));
        settings.put("WinCol",new Integer(m_winColumns));
        settings.put("WinRows",new Integer(m_winRows));
        settings.put("WinFontName",m_winFontName);
        settings.put("WinFontType",new Integer(m_winFontStyle));
        settings.put("WinFontSize",new Integer(m_winFontSize));
        settings.put("WinLoc",m_winLoc);
       
        return settings;
        }
    
    public String getStoredProcedure()
        {
        return new String("up_insert_JLikertQuestion");
        }
    public String getTitle()
        {
        return m_title;
        }
    public int getWinColumns()
        {
        return m_winColumns;
        }
    public Font getWinFont()
        {
        return new Font(m_winFontName,m_winFontStyle,m_winFontSize);
        }
    public Point getWinLoc()
        {
        return m_winLoc;
        }
    public int getWinRows()
        {
        return m_winRows;
        }

    public void setCenter(String str)
        {
        m_center = str;
        }
    public void setLeft(String str)
        {
        m_left = str;
        }
    public void setQuestion(String str)
        {
        m_question = str;
        }
    public void setRange(int value)
        {
        if ((value < 2) || (value > 9))
            return;
  
        m_range = value;
        }
    public void setRight(String str)
        {
        m_right = str;
        }
    public void setTitle(String str)
        {
        m_title = str;
        }
    public void setWinColumns(int value)
        {
        m_winColumns = value;
        }
    public void setWinFont(Font value)
        {
        m_winFontName = value.getName();
        m_winFontStyle = value.getStyle();
        m_winFontSize = value.getSize();
        }
    public void setWinLoc(int x, int y)
        {
        m_winLoc.x = x;
        m_winLoc.y = y;
        }
    public void setWinRows(int value)
        {
        m_winRows = value;
        }

    public void showQuestion(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3, int question)
        {
        new ClientLikertWindow(app1,app2,app3,question);
        }

    public static void updateDBEntry(Connection con)
        {
        int dbType = 1; /* 0 -> exptDB, 1 -> quesDB */

        try
            { 
       // Test which database I'm accessing.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Name_VC FROM Base_Question_Type_T WHERE Name_VC = '"+OBJ_NAME+"'");
            }
        catch( Exception e ) 
            {
            dbType = 0;
            }
System.err.println("DBT: "+dbType);
        try
            { 
            // get a Statement object from the Connection
            //
            // Place new Simulant Type objects.

            if (dbType == 1)
                {
                Statement stmt = con.createStatement();
                    
                ResultSet rs = stmt.executeQuery("SELECT Name_VC FROM Base_Question_Type_T WHERE Name_VC = '"+OBJ_NAME+"'");

                if (rs.next())
                    {
                    // The entry already exists so we merely want to update it.
                    String file = rs.getString("Name_VC");

                    PreparedStatement ps = con.prepareStatement("UPDATE Base_Question_Type_T SET Base_Question_OBJ = ? WHERE Name_VC = '"+file+"'");

                    LikertQuestion lq = new LikertQuestion();

System.err.println(lq+" "+lq.getName());
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                    Vector v = FMSObjCon.addObjectToStatement(1,lq,ps);
                    ps.executeUpdate();
                    FMSObjCon.cleanUp(v);
                    }
                else
                    {
                // Name, Desc, TutorialPage, id
                    CallableStatement cs = con.prepareCall("{call up_insert_JBaseQuestionType (?, ?, ?)}");

                    LikertQuestion lq = new LikertQuestion();

System.err.println(lq);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                    String str = new String("Allows you to build a likert question.");

                    cs.setString(1,OBJ_NAME);
                    cs.setString(2,str);
                    Vector v = FMSObjCon.addObjectToStatement(3,lq,cs);
                    cs.execute();
                    FMSObjCon.cleanUp(v);
                    }
                }
            if (dbType == 0)
                {
                Statement stmt = con.createStatement();
                    
                ResultSet rs = stmt.executeQuery("SELECT Object_ID_INT FROM Other_Objects_T WHERE Object_Name_VC = '"+OBJ_NAME+"'");

                if (rs.next())
                    {
                    // The entry already exists so we merely want to update it.
                    int index = rs.getInt("Object_ID_INT");

                    PreparedStatement ps = con.prepareStatement("UPDATE Other_Objects_T SET Object_OBJ = ? WHERE Object_ID_INT = "+index);

                    LikertQuestion lq = new LikertQuestion();

System.err.println(lq+" "+lq.getName());
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                    Vector v = FMSObjCon.addObjectToStatement(1,lq,ps);
                    ps.executeUpdate();
                    FMSObjCon.cleanUp(v);
                    }
                else
                    {
                // Name, Desc, TutorialPage, id
                    CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                    LikertQuestion lq = new LikertQuestion();

System.err.println(lq);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                    String str = new String("Allows you to build a likert question.");

                    cs.setString(1,OBJ_NAME);
                    cs.setString(2,str);
                    Vector v = FMSObjCon.addObjectToStatement(3,lq,cs);
                    cs.registerOutParameter(4, java.sql.Types.INTEGER);
                    cs.execute();
System.err.println("LQ Object ID: "+cs.getInt(4));
                    FMSObjCon.cleanUp(v);
                    }
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
