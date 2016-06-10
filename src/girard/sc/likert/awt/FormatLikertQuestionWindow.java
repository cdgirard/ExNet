package girard.sc.likert.awt;


import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.expt.awt.SaveBaseActionWindow;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.likert.obj.LikertQuestion;
import girard.sc.ques.awt.BaseQuestionBuilderWindow;
import girard.sc.ques.awt.FormatBaseQuestionWindow;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Used to format likert style questions.
 * <p>
 * Started: 7-29-2002
 * <p>
 * @author Dudley Girard
 */

public class FormatLikertQuestionWindow extends FormatBaseQuestionWindow implements ActionListener
    {
    LikertQuestion m_LQ;

 // Menu Area
    protected MenuBar m_mbar = new MenuBar();
    protected Menu m_File, m_Help;

    Frame m_questionWindow;
    TextArea m_question;
    Label m_rightLabel, m_centerLabel, m_leftLabel;

    List m_fontList;

    NumberTextField m_numRowsField, m_numColField;
    Button m_updateButton;
    NumberTextField m_dwXLocField, m_dwYLocField;

    TextField m_titleField;
 
    TextField m_rightField, m_centerField, m_leftField;
    CheckboxGroup m_rangeField = new CheckboxGroup();

    public FormatLikertQuestionWindow(ExptOverlord app1, BaseQuestionBuilderWindow app2, LikertQuestion lq)
        {
        super(app1,app2,lq);

        m_LQ = lq;

        initializeLabels();

        getContentPane().setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("flqw_title"));
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());

    // Setup Question Window
        setupQuestionWindow();

    // End Setup of Question Window

    // Setup Menubar
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("flqw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("flqw_save"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("flqw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);
 
    // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("flqw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("flqw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);
    // End Setup of Menubar

    // Setup North Panel
        GridBagPanel northPanel = new GridBagPanel();

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_qf")),1,1,10,1);
 
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_location")),1,2,2,1);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_xl")),3,2,2,1);
        m_dwXLocField = new NumberTextField(""+m_LQ.getWinLoc().x,4);
        m_dwXLocField.setAllowNegative(false);
        m_dwXLocField.setAllowFloat(false);
        northPanel.constrain(m_dwXLocField,5,2,2,1);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_yl")),7,2,2,1);
        m_dwYLocField = new NumberTextField(""+m_LQ.getWinLoc().y,4);
        m_dwYLocField.setAllowNegative(false);
        m_dwYLocField.setAllowFloat(false);
        northPanel.constrain(m_dwYLocField,9,2,2,1);

        m_fontList = new List(3,false);
        m_fontList.add("Large");
        m_fontList.add("Medium");
        m_fontList.add("Small");

        if (m_LQ.getWinFont().getSize() == m_EOApp.getMedWinFont().getSize())
            m_fontList.select(0);
        else if (m_LQ.getWinFont().getSize() == m_EOApp.getSmWinFont().getSize())
            m_fontList.select(1);
        else if (m_LQ.getWinFont().getSize() == m_EOApp.getTinyWinFont().getSize())
            m_fontList.select(2);

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_font")),1,3,10,1);
        northPanel.constrain(m_fontList,1,4,10,3);

        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_size")),1,7,2,1);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_rows")),3,7,2,1);
        m_numRowsField = new NumberTextField(""+m_LQ.getWinRows(),4);
        m_numRowsField.setAllowNegative(false);
        m_numRowsField.setAllowFloat(false);
        northPanel.constrain(m_numRowsField,5,7,2,1);
        northPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_cols")),7,7,2,1);
        m_numColField = new NumberTextField(""+m_LQ.getWinColumns(),4);
        m_numColField.setAllowNegative(false);
        m_numColField.setAllowFloat(false);
        northPanel.constrain(m_numColField,9,7,2,1);

    // End Setup of North Panel

    // Start Setup of Center Panel
        GridBagPanel centerPanel = new GridBagPanel();

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_title:")),1,1,2,1);
        m_titleField = new TextField(m_LQ.getTitle(),16);
        centerPanel.constrain(m_titleField,3,1,4,1);

        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_labels")),1,2,2,1,GridBagConstraints.CENTER);
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_left")),1,3,2,1);
        m_leftField = new TextField(m_LQ.getLeft(),8);
        centerPanel.constrain(m_leftField,3,3,2,1);
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_center")),5,3,2,1);
        m_centerField = new TextField(m_LQ.getCenter(),8);
        centerPanel.constrain(m_centerField,7,3,2,1);
        centerPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_right")),9,3,2,1);
        m_rightField = new TextField(m_LQ.getRight(),8);
        centerPanel.constrain(m_rightField,11,3,2,1);
    // End Setup of Center Panel

    // Setup South Panel
        GridBagPanel southPanel = new GridBagPanel();

        southPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("flqw_range")),1,4,2,1);

    // Create the Checkboxes
        boolean rFlag = false;
        if (m_LQ.getRange() == 2)
            rFlag = true;
        Checkbox cb = new Checkbox("2",m_rangeField,rFlag);
        southPanel.constrain(cb,1,5,1,1);

        rFlag = false;
        if (m_LQ.getRange() == 3)
            rFlag = true;
        cb = new Checkbox("3",m_rangeField,rFlag);
        southPanel.constrain(cb,2,5,1,1);

        rFlag = false;
        if (m_LQ.getRange() == 4)
            rFlag = true;
        cb = new Checkbox("4",m_rangeField,rFlag);
        southPanel.constrain(cb,3,5,1,1);

        rFlag = false;
        if (m_LQ.getRange() == 5)
            rFlag = true;
        cb = new Checkbox("5",m_rangeField,rFlag);
        southPanel.constrain(cb,4,5,1,1);

        rFlag = false;
        if (m_LQ.getRange() == 6)
            rFlag = true;
        cb = new Checkbox("6",m_rangeField,rFlag);
        southPanel.constrain(cb,5,5,1,1);

        rFlag = false;
        if (m_LQ.getRange() == 7)
            rFlag = true;
        cb = new Checkbox("7",m_rangeField,rFlag);
        southPanel.constrain(cb,6,5,1,1);

        rFlag = false;
        if (m_LQ.getRange() == 8)
            rFlag = true;
        cb = new Checkbox("8",m_rangeField,rFlag);
        southPanel.constrain(cb,7,5,1,1);

        rFlag = false;
        if (m_LQ.getRange() == 9)
            rFlag = true;
        cb = new Checkbox("9",m_rangeField,rFlag);
        southPanel.constrain(cb,8,5,1,1);
        rFlag = false;

        m_updateButton = new Button(m_EOApp.getLabels().getObjectLabel("flqw_update"));
        m_updateButton.addActionListener(this);
        southPanel.constrain(m_updateButton,1,6,8,1,GridBagConstraints.CENTER);

     // End Setup of South Panel

        getContentPane().add("North",northPanel);
        getContentPane().add("Center",centerPanel);
        getContentPane().add("South",southPanel);

        pack();

        setSize(getPreferredSize());
        validate();

        show();

        m_questionWindow.show();
        }
    
    public void actionPerformed(ActionEvent e) 
        {
        if (getEditMode())
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    
        // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("flqw_exit")))
                {
                m_questionWindow.dispose();

                m_BQBWApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("flqw_help")))
                {
                m_EOApp.helpWindow("ehlp_flqw");
                }

            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("flqw_save")))
                {
                setEditMode(true);

                m_LQ.setQuestion(m_question.getText());
                new SaveBaseActionWindow(m_EOApp,this,m_LQ);
                }
            }
        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_updateButton)
                {
                int columns = m_numColField.getIntValue();
                int rows = m_numRowsField.getIntValue();
                int fontType = m_fontList.getSelectedIndex();

                if ((columns > 10) && (columns < 100) && (rows > 0) && (rows < 80) && (fontType > -1))
                    {
                    m_LQ.setQuestion(m_question.getText());
                    m_LQ.setWinColumns(columns);
                    m_LQ.setWinRows(rows);
                    m_LQ.setWinLoc(m_dwXLocField.getIntValue(),m_dwYLocField.getIntValue());
                    if (fontType == 0)
                        m_LQ.setWinFont(new Font("Monospaced",Font.PLAIN,16));
                    else if (fontType == 1)
                        m_LQ.setWinFont(new Font("Monospaced",Font.PLAIN,14));
                    else
                        m_LQ.setWinFont(new Font("Monospaced",Font.PLAIN,12));

                    m_LQ.setTitle(m_titleField.getText());

                    m_LQ.setRight(m_rightField.getText());
                    m_LQ.setCenter(m_centerField.getText());
                    m_LQ.setLeft(m_leftField.getText());

                    int range = (Integer.valueOf(m_rangeField.getSelectedCheckbox().getLabel())).intValue();
                    m_LQ.setRange(range);

                    m_questionWindow.dispose();
                    setupQuestionWindow();
                    m_questionWindow.show();
                    }
                }
            }
        }

    public boolean getEditMode()
        {
        return m_EditMode;
        }

    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/likert/awt/flqw.txt");
        }

    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/likert/awt/flqw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }

/**
 * Setup Question Window
 */
    private void setupQuestionWindow()
        {
        m_questionWindow = new Frame(m_LQ.getTitle());

        m_questionWindow.setLayout(new BorderLayout());

        m_question = new TextArea(m_LQ.getQuestion(),m_LQ.getWinRows(),m_LQ.getWinColumns(),TextArea.SCROLLBARS_VERTICAL_ONLY);
        m_question.setFont(m_LQ.getWinFont());

        m_questionWindow.add("North",m_question);

        GridBagPanel centerPanel = new GridBagPanel();

        m_leftLabel = new Label(m_LQ.getLeft());
        centerPanel.constrain(m_leftLabel,1,1,1,1,GridBagConstraints.WEST);

        m_centerLabel = new Label(m_LQ.getCenter());
        centerPanel.constrain(m_centerLabel,2,1,1,1,GridBagConstraints.CENTER);

        m_rightLabel = new Label(m_LQ.getRight());
        centerPanel.constrain(m_rightLabel,3,1,1,1,GridBagConstraints.EAST);

        m_questionWindow.add("Center",centerPanel);

        GridBagPanel southPanel = new GridBagPanel();

        Panel tmpPanel;

     /*   tmpPanel = new Panel();
        tmpPanel.setLayout(new GridLayout(2,1));
        tmpPanel.add(new Label("1"));
        tmpPanel.add(new Checkbox("",false));
        southPanel.constrain(tmpPanel,1,1,1,2,GridBagConstraints.WEST);

        tmpPanel = new Panel();
        tmpPanel.setLayout(new GridLayout(2,1));
        tmpPanel.add(new Label("-"));
        tmpPanel.add(new Label(""));
        southPanel.constrain(tmpPanel,2,1,1,2,GridBagConstraints.CENTER); */


        for (int i=1;i<m_LQ.getRange();i++)
            {
            tmpPanel = new Panel();
            tmpPanel.setLayout(new GridLayout(2,1));
            tmpPanel.add(new Label(""+i));
            tmpPanel.add(new Checkbox("",false));
            southPanel.constrain(tmpPanel,i*2-1,1,1,2,GridBagConstraints.CENTER);

            tmpPanel = new Panel();
            tmpPanel.setLayout(new GridLayout(2,1));
            tmpPanel.add(new Label("-"));
            tmpPanel.add(new Label(""));
            southPanel.constrain(tmpPanel,i*2,1,1,2,GridBagConstraints.CENTER);
            }
        tmpPanel = new Panel();
        tmpPanel.setLayout(new GridLayout(2,1));
        tmpPanel.add(new Label(""+m_LQ.getRange()));
        tmpPanel.add(new Checkbox("",false));
        southPanel.constrain(tmpPanel,m_LQ.getRange()*2-1,1,1,1,GridBagConstraints.CENTER);

        m_questionWindow.add("South",southPanel);

        m_questionWindow.pack();
        m_questionWindow.setLocation(m_LQ.getWinLoc().x,m_LQ.getWinLoc().y);
        }
    }
