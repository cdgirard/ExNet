package girard.sc.gtp.obj;

import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.gtp.awt.ClientGTPWindow;
import girard.sc.gtp.awt.FormatGenericTutPageWindow;
import girard.sc.io.FMSObjCon;
import girard.sc.tp.awt.TutorialPageBuilderWindow;
import girard.sc.tp.obj.TutorialPage;

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
 * Is the generic tutorial page object for dislaying a page of text with an
 * image potentially attached to it.
 * <p>
 * Started: 11-04-2001
 * <br>Last Modified: 01-15-2002
 *
 * @author Dudley Girard
 */

public class GenericTutorialPage extends TutorialPage
    {
    public static final String NO_IMAGE = new String("-NONE-");

    protected static final String OBJ_NAME = new String("Generic Tutorial Page");
    protected static final String DB = new String("tpDB");
    protected static final String DB_TABLE = new String("GTP_T");

    protected String m_instructions = new String("");
    protected String m_imageTitle = NO_IMAGE;
    protected String m_imageLocation = new String("");
    protected int m_winColumns = 50;
    protected int m_winRows = 10;
    protected String m_winFontName = new String("Monospaced");
    protected int m_winFontStyle = Font.PLAIN;
    protected int m_winFontSize = 12;
    protected Point m_winLoc = new Point(0,0);
    protected Point m_imgLoc = new Point(0,0);
  
    public GenericTutorialPage()
        {
        super(OBJ_NAME,DB,DB_TABLE);

        m_desc = new String("Allows you to build a simple page with a window of text and an image attached to it.");
        }
    public GenericTutorialPage(String inst, String title, String loc)
        {
        super(OBJ_NAME,DB,DB_TABLE);

        m_desc = new String("Allows you to build a simple page with a window of text and an image attached to it.");

        m_instructions = inst;
        m_imageTitle = title;
        m_imageLocation = loc;        
        }

    public void applySettings(Hashtable h)
        {
        super.applySettings(h);

        m_instructions = (String)h.get("Instructions");
        m_imageTitle = (String)h.get("ImageTitle");
        m_imageLocation = (String)h.get("ImageLocation");
        m_imgLoc = (Point)h.get("ImageWinLoc");

        if (h.get("TutWinCol") != null)
            m_winColumns = ((Integer)h.get("TutWinCol")).intValue();
        if (h.get("TutWinRows") != null)
            m_winRows = ((Integer)h.get("TutWinRows")).intValue();
        if (h.get("TutWinFontName") != null)
            {
            m_winFontName = (String)h.get("TutWinFontName");
            m_winFontStyle = ((Integer)h.get("TutWinFontType")).intValue();
            m_winFontSize = ((Integer)h.get("TutWinFontSize")).intValue();
            }
        m_winLoc = (Point)h.get("TutWinLoc");

        m_db = DB;
        m_dbTable = DB_TABLE;
        }

    public Object clone()
        {
        GenericTutorialPage tp = new GenericTutorialPage();

        tp.applySettings(this.getSettings());

        return tp;
        }

    public void formatPage(ExptOverlord app, TutorialPageBuilderWindow tpbw) 
        {
        new FormatGenericTutPageWindow(app,tpbw,this);
        }
    
    public Point getImgLoc()
        {
        return m_imgLoc;
        }
    public String getImageLocation()
        {
        return m_imageLocation;
        }
    public String getImageTitle()
        {
        return m_imageTitle;
        }
    public String getInstructions()
        {
        return m_instructions;
        }
    public Hashtable getSettings()
        {
        Hashtable settings = super.getSettings();

        settings.put("Instructions",m_instructions);
        settings.put("ImageTitle",m_imageTitle);
        settings.put("ImageLocation",m_imageLocation);
        settings.put("ImageWinLoc",m_imgLoc);
        settings.put("TutWinCol",new Integer(m_winColumns));
        settings.put("TutWinRows",new Integer(m_winRows));
        settings.put("TutWinFontName",m_winFontName);
        settings.put("TutWinFontType",new Integer(m_winFontStyle));
        settings.put("TutWinFontSize",new Integer(m_winFontSize));
        settings.put("TutWinLoc",m_winLoc);
       
        return settings;
        }
    public String getStoredProcedure()
        {
        return new String("up_insert_JGTP");
        }
    public int getUserID()
        {
        return m_userID;
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

    public void setImageLocation(String str)
        {
        m_imageLocation = str;
        }
    public void setImageTitle(String str)
        {
        m_imageTitle = str;
        }
    public void setImgLoc(int x, int y)
        {
        m_imgLoc.x = x;
        m_imgLoc.y = y;
        }
    public void setInstructions(String str)
        {
        m_instructions = str;
        }
    public void setUserID(int value)
        {
        m_userID = value;
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

    public void startPage(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        new ClientGTPWindow(app1,app2,app3);
        }

    public void stopPage(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        }

    public static void updateDBEntry(Connection con)
        {
        int dbType = 1; /* 0 -> exptDB, 1 -> tpDB */

        try
            { 
       // Test which database I'm accessing.
            Statement stmt = con.createStatement();
                    
            ResultSet rs = stmt.executeQuery("SELECT Name_VC FROM Tutorial_Page_Type_T WHERE Name_VC = '"+OBJ_NAME+"'");
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
                    
                ResultSet rs = stmt.executeQuery("SELECT Name_VC FROM Tutorial_Page_Type_T WHERE Name_VC = '"+OBJ_NAME+"'");

                if (rs.next())
                    {
                    // The entry already exists so we merely want to update it.
                    String file = rs.getString("Name_VC");

                    PreparedStatement ps = con.prepareStatement("UPDATE Tutorial_Page_Type_T SET Tut_Page_OBJ = ? WHERE Name_VC = '"+file+"'");

                    GenericTutorialPage gtp = new GenericTutorialPage();

System.err.println(gtp+" "+gtp.getName());
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                    Vector v = FMSObjCon.addObjectToStatement(1,gtp,ps);
                    ps.executeUpdate();
                    FMSObjCon.cleanUp(v);
                    }
                else
                    {
                // Name, Desc, TutorialPage, id
                    CallableStatement cs = con.prepareCall("{call up_insert_JTutorialPageType (?, ?, ?)}");

                    GenericTutorialPage gtp = new GenericTutorialPage();

System.err.println(gtp);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                    String str = new String("Allows you to build a simple page with a window of text and an image attached to it.");

                    cs.setString(1,OBJ_NAME);
                    cs.setString(2,str);
                    Vector v = FMSObjCon.addObjectToStatement(3,gtp,cs);
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

                    GenericTutorialPage gtp = new GenericTutorialPage();

System.err.println(gtp+" "+gtp.getName());
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                    Vector v = FMSObjCon.addObjectToStatement(1,gtp,ps);
                    ps.executeUpdate();
                    FMSObjCon.cleanUp(v);
                    }
                else
                    {
                // Name, Desc, TutorialPage, id
                    CallableStatement cs = con.prepareCall("{call up_insert_JOtherObject (?, ?, ?, ?)}");

                    GenericTutorialPage gtp = new GenericTutorialPage();

System.err.println(gtp);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                    String str = new String("Allows you to build a simple page with a window of text and an image attached to it.");

                    cs.setString(1,"Generic Tutorial Page");
                    cs.setString(2,str);
                    Vector v = FMSObjCon.addObjectToStatement(3,gtp,cs);
                    cs.registerOutParameter(4, java.sql.Types.INTEGER);
                    cs.execute();
System.err.println("GTP Object ID: "+cs.getInt(4));
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
