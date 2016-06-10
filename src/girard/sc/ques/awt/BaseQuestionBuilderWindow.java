package girard.sc.ques.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.SortedFixedList;
import girard.sc.expt.awt.ActionBuilderWindow;
import girard.sc.expt.awt.BaseActionFormatWindow;
import girard.sc.expt.awt.DeleteBaseActionWindow;
import girard.sc.expt.awt.LoadBaseActionWindow;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.ques.io.msg.BaseQuestionTypesListReqMsg;
import girard.sc.ques.obj.BaseQuestion;

import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

public class BaseQuestionBuilderWindow extends BaseActionFormatWindow implements ActionListener,ItemListener
    {
    BaseQuestion m_baseQuestion;

   // Menu Area
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_Help;

    Vector m_availableTypes = new Vector();
    Vector m_typeDescriptions = new Vector();

    SortedFixedList m_typeList;
    BaseQuestion m_activeType = null;
    int m_typeIndex = -1;

    TextArea m_typeDesc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

    public BaseQuestionBuilderWindow(ExptOverlord app1, ActionBuilderWindow app2, BaseQuestion app3)
        {
        super(app1,app2,app3);

        m_baseQuestion = app3;

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("bqbw_title"));
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("bqbw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bqbw_new"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bqbw_open"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bqbw_delete"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bqbw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);
 
    // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("bqbw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("bqbw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);

        GridBagPanel mainPanel = new GridBagPanel();

    // Setup List of Types
        GridBagPanel tmpGBPanel = new GridBagPanel();

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bqbw_tpt")),1,1,4,1);
        m_typeList = new SortedFixedList(8,false,1,25);
        m_typeList.addItemListener(this);

        loadAvailableTypes();

        tmpGBPanel.constrain(m_typeList,1,2,4,8);

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("bqbw_description")),1,10,4,1);

        tmpGBPanel.constrain(m_typeDesc,1,11,4,4);

        mainPanel.constrain(tmpGBPanel,1,1,1,1);
    // End Setup List of Types

        getContentPane().add("Center",mainPanel);

        pack();
        show();
        }
    
    public void actionPerformed(ActionEvent e) 
        {
        if (getEditMode())
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    
        // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bqbw_exit")))
                {
                m_ABWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if ((m_typeIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bqbw_new"))))
                {
                setEditMode(true);

                ((BaseQuestion)m_activeType.clone()).formatQuestion(m_EOApp,this);
                }
            if ((m_typeIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bqbw_open"))))
                {
                setEditMode(true);
               
                new LoadBaseActionWindow(m_EOApp,this,m_activeType);
                }
            if ((m_typeIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bqbw_delete"))))
                {
                setEditMode(true);

                new DeleteBaseActionWindow(m_EOApp,this,m_activeType);
                }
        // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("bqbw_help")))
                {
                m_EOApp.helpWindow("ehlp_bqbw");
                }
            }
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/ques/awt/bqbw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof SortedFixedList)
            {
            SortedFixedList theSource = (SortedFixedList)e.getSource();

            if (theSource == m_typeList)
                {
                m_typeIndex = theSource.getSelectedIndex();
                if (m_typeIndex != -1)
                    {
                    m_activeType = (BaseQuestion)m_availableTypes.elementAt(m_typeIndex);
                    String str = (String)m_typeDescriptions.elementAt(m_typeIndex);
                    m_typeDesc.setText(str);
                    }
                }
            }
         }

    public void loadAvailableTypes()
        {
        BaseQuestionTypesListReqMsg tmp = new BaseQuestionTypesListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof BaseQuestionTypesListReqMsg)
            {
            Object[] in_args = em.getArgs();
            Vector availableTypes = (Vector)in_args[0];
            Vector typeDescriptions = (Vector)in_args[1];

            for (int i=0;i<availableTypes.size();i++)
                {
                String[] str = new String[1];
                BaseQuestion bq = (BaseQuestion)availableTypes.elementAt(i);

                str[0] = bq.getName();
                m_typeList.addItem(str);
                m_availableTypes.insertElementAt(bq,m_typeList.last);
                m_typeDescriptions.insertElementAt(typeDescriptions.elementAt(i),m_typeList.last);
                }
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/ques/awt/bqbw.txt");
        }

/**
 * We have to override the setActiveBaseAction function so that we use
 * the format page function of the TutorialPages.
 */
    public void setActiveBaseAction(BaseAction ba)
        {
        BaseQuestion bq = (BaseQuestion)ba;
        bq.formatQuestion(m_EOApp,this);
        }
    }
