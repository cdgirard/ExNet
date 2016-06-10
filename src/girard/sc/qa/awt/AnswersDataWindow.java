package girard.sc.qa.awt;

import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.qa.obj.Questionnaire;
import girard.sc.ques.obj.AnswerOutputObject;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Used to display the answer data from a Questionnaire to an experimenter.
 * <p>
 * <br> Started: 08-23-2002
 * <p>
 * @author Dudley Girard
 */


public class AnswersDataWindow extends Frame implements ActionListener
    {
    QuestionnaireDataDisplay m_DDApp;
    Questionnaire m_QApp;
    ExptOverlord m_EOApp;
    BaseDataInfo m_bdi;
    
    MenuBar m_menuBar = new MenuBar();
    Menu m_fileMenu, m_helpMenu;

    public AnswersDataWindow(Questionnaire app1, ExptOverlord app2, QuestionnaireDataDisplay app3, BaseDataInfo bdi)
        {
        super();
        m_QApp = app1;
        m_EOApp =app2;
        m_DDApp = app3;
        m_bdi = bdi;

        initializeLabels();

        setLayout(new GridLayout(1,1));
        setTitle(m_EOApp.getLabels().getObjectLabel("adw_title"));
        setFont(new Font("Monospaced",Font.PLAIN,14));
        setBackground(m_EOApp.getWinBkgColor());

        m_menuBar.setFont(m_EOApp.getSmWinFont());

        setMenuBar(m_menuBar);

     // Setup Menu options
        MenuItem tmpItem;

    // File Menu
        m_fileMenu = new Menu(m_EOApp.getLabels().getObjectLabel("adw_file"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("adw_exit"));
        tmpItem.addActionListener(this);
        m_fileMenu.add(tmpItem);

        m_menuBar.add(m_fileMenu);

   // Help Menu
        m_helpMenu = new Menu(m_EOApp.getLabels().getObjectLabel("adw_help"));

        tmpItem = new MenuItem(m_EOApp.getLabels().getObjectLabel("adw_help"));
        tmpItem.addActionListener(this);
        m_helpMenu.add(tmpItem);

        m_menuBar.add(m_helpMenu);
        
     // End setup for Menu options

        Panel MainPanel = new Panel(new GridLayout(1,1));

        TextArea tmpText = new TextArea(20,80);
        tmpText.setEditable(false);

        Hashtable data = m_bdi.getActionData();

        StringBuffer str = new StringBuffer("");
        // str.append(getHeadings()+"\n");

        for (int i=0;i<data.size();i++)
            {
            Vector ans = (Vector)data.get(""+i);

            str.append(" "+i+" ");
            str.append(" "+m_bdi.getExptOutID()+" ");
            str.append(" "+m_bdi.getActionIndex()+" ");

            Enumeration enm = ans.elements();
            while (enm.hasMoreElements())
                {
                AnswerOutputObject aoo = (AnswerOutputObject)enm.nextElement();
                str.append(buildColumnEntry(aoo.getAnswer(),7));
                }
            str.append("\n");
            }

        tmpText.setText(str.toString());

        MainPanel.add(tmpText);

        add(MainPanel);
        pack();
        show();
        }

    public void actionPerformed(ActionEvent e)
        {
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("adw_exit")))
                {
                m_DDApp.setEditMode(false);
                this.dispose();
                return;
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("adw_help")))
                {
                m_EOApp.helpWindow("ehlp_adw");
                return;
                }
            }
        }

    public String buildColumnEntry(String str, int width)
        {
        int x, k, m;
        StringBuffer entry = new StringBuffer("");

        if (width > str.length())
            {
            
            m = (int)((width - str.length())/2);
            for (k=0;k<m;k++)
                {
                entry.append(" ");    
                }
            entry.append(str);
            for (k=m+str.length();k<width;k++)
                {
                entry.append(" ");
                }
            }
        else
            entry.append(str.substring(0,width));

        return entry.toString();
        }
    
    public void dispose()
        {
        removeLabels();
        super.dispose();
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/qa/awt/adw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/qa/awt/adw.txt");
        }
    }
