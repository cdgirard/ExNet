package girard.sc.tp.obj;

import girard.sc.expt.awt.ActionBuilderWindow;
import girard.sc.expt.io.ExptMessageListener;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.obj.ClientExptInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.FMSObjCon;
import girard.sc.tp.awt.TutorialPageBuilderWindow;
import girard.sc.tp.awt.WaitWindow;
import girard.sc.tut.awt.FormatTutorialActionWindow;
import girard.sc.tut.io.msg.FinishedTutorialMsg;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

public class TutorialPage extends BaseAction
    {
  
    public TutorialPage()
        {
        super("TutorialPage","tbDB","Tutorial_Page_Type_T");
        }
    public TutorialPage(String name, String db, String dbTable)
        {
        super(name,db,dbTable);
        }

    public void applySettings(Hashtable h)
        {
        super.applySettings(h);
        }

    public Object clone()
        {
        TutorialPage tp = new TutorialPage();

        return tp;
        }

    public static void createWaitWindow(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        new WaitWindow(app1,app2,app3);

        FinishedTutorialMsg tmp = new FinishedTutorialMsg(null);
        app3.sendMessage(tmp);
        }

    public void formatAction(ExptOverlord app, ActionBuilderWindow abw)
        {
        new TutorialPageBuilderWindow(app,abw,this);
        }
    public void formatPage(ExptOverlord app, TutorialPageBuilderWindow tpbw) 
        {
        tpbw.setEditMode(false);
        }
    public void formatPage(ExptOverlord app, FormatTutorialActionWindow ftaw) 
        {
        ftaw.setEditMode(false);
        }

    public final String getInsertFormat()
        {
    /* ID_INT, App_ID, App_Name_VC, Name_VC, Desc_VC, Settings_OBJ */
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

   /* 
Most likely to be removed soon. 2-21-2002
    public final void initializePage(ResultSet rs)
        {
        try 
            {
            m_userID = rs.getInt("ID_INT");
            m_tutorialPageTypeID = rs.getInt("Tut_Page_Type_ID_INT");
            m_pageName = rs.getString("Tut_Page_Name_VC");
            m_pageDesc = rs.getString("Tut_Page_Desc_VC");
            
            ObjectInputStream ois = new ObjectInputStream(rs.getBinaryStream("Settings_OBJ"));
            Hashtable h = (Hashtable)ois.readObject();
     
            applySettings(h);
            }
        catch(SQLException sqle) { System.err.println(sqle); }
        catch(IOException ioe) { System.err.println(ioe); }
        catch(ClassNotFoundException cnfe) { System.err.println(cnfe); }
        } */

    public void startPage(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
        {
        }

    public void stopPage(ExptOverlord app1, ClientExptInfo app2, ExptMessageListener app3)
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
                    
            ResultSet rs = stmt.executeQuery("SELECT Base_Action_Type_ID_INT FROM Base_Actions_Type_T WHERE Base_Action_Name_VC = 'TutorialPage'");

            if (rs.next())
                {
                // The entry already exists so we merely want to update it.
                int index = rs.getInt("Base_Action_Type_ID_INT");

                PreparedStatement ps = con.prepareStatement("UPDATE Base_Actions_Type_T SET Base_Action_OBJ = ? WHERE Base_Action_Type_ID_INT = "+index);

                TutorialPage tp = new TutorialPage();

System.err.println(tp+" "+tp.getName());
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                Vector v = FMSObjCon.addObjectToStatement(1,tp,ps);
                ps.executeUpdate();
                FMSObjCon.cleanUp(v);
                }
            else
                {
                // Name, Desc, Network, id
                CallableStatement cs = con.prepareCall("{call up_insert_JBaseActionType (?, ?, ?, ?)}");

                TutorialPage tp = new TutorialPage();

System.err.println(tp);
                // Because MS SQL doesn't support Java Objects we have to write the object to a 
                // file first, then read it from the file as a binary stream.

                String str = new String("Allows you to build pages for a tutorial action.");

                cs.setString(1,"TutorialPage");
                cs.setString(2,str);
                Vector v = FMSObjCon.addObjectToStatement(3,tp,cs);
                cs.registerOutParameter(4, java.sql.Types.INTEGER);
                cs.execute();
System.err.println("TutorialPage Object ID: "+cs.getInt(4));
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
