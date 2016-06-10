package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedLabel;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.awt.NumberTextField;
import girard.sc.expt.io.msg.ExptActionTypesListReqMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.Experiment;
import girard.sc.expt.obj.ExperimentAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This is used to form a group of ExperimentActions into an Experiment.
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.4
 * @since JDK1.1
 * @see girard.sc.expt.obj.ExperimentAction
 * @see girard.sc.expt.obj.Experiment
 */

public class ExptBuilderWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    protected ExptOverlord m_EOApp;
/**
 * The Experiment that is presently being editting or created.
 *
 */
    protected Experiment m_ExpApp = new Experiment();

   // Menu Area
    private MenuBar m_mbar = new MenuBar();
    private Menu m_File, m_Edit, m_Format, m_Help;

    private NumberTextField m_numUsersField;
    private FixedLabel m_setNumUsersLabel;
    private Button m_setNumUsersButton;

/**
 * The possible ExperimentActions that can be added to the Experiment.
 *
 */
    protected Vector m_availableActions = new Vector();
/**
 * The descriptions of the ExperimentActions that can be added to the Experiment.
 *
 */
    protected Vector m_actionDescriptions = new Vector();

    FixedList m_ExperimentList;
    ExperimentAction m_exptAction = null;
    int m_exptIndex = -1;

    Checkbox m_changeOrder;

    TextArea m_exptDesc = new TextArea("",6,25,TextArea.SCROLLBARS_NONE);
    TextArea m_actDesc = new TextArea("",4,25,TextArea.SCROLLBARS_NONE);
 
    protected boolean m_canChangeNumUsers = true;
    protected Checkbox m_autoStartBox;
/**
 * Determines whether the window is active or not, if the window is not active 
 * (set to true), then no menu or button commands are processed.
 *
 */
    protected boolean m_EditMode = false;

    public ExptBuilderWindow(ExptOverlord app)
        {
        m_EOApp = app;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("ebw_title"));
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());

        loadAvailableActions();

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("ebw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_new"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_open"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_save"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_delete"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

   // Edit Menu
        m_Edit = new Menu(m_EOApp.getLabels().getObjectLabel("ebw_edit"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_aa"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_ia"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        m_Edit.addSeparator();

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_da"));
        tmpMI.addActionListener(this);
        m_Edit.add(tmpMI);

        m_mbar.add(m_Edit);
 
    // Format Menu
        m_Format = new Menu(m_EOApp.getLabels().getObjectLabel("ebw_format"));      

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_fa"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI); 

        m_Format.addSeparator();

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_ee"));
        tmpMI.addActionListener(this);
        m_Format.add(tmpMI);

         m_mbar.add(m_Format);

    // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("ebw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("ebw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);

    // Setup List of Actions In Experiment
        GridBagPanel centerGBPanel = new GridBagPanel();

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ebw_ea")),1,1,4,1);
        int[] elCol = { 35, 3};
        m_ExperimentList = new FixedList(8,false,2,elCol);
        m_ExperimentList.addItemListener(this);

        centerGBPanel.constrain(m_ExperimentList,1,2,4,8);

        m_changeOrder = new Checkbox(m_EOApp.getLabels().getObjectLabel("ebw_co"),false);
        centerGBPanel.constrain(m_changeOrder,1,10,3,1);

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ebw_noust")),1,11,3,1);
        m_setNumUsersLabel = new FixedLabel(4,"1");
        centerGBPanel.constrain(m_setNumUsersLabel,4,11,1,1);

        m_setNumUsersButton = new Button(m_EOApp.getLabels().getObjectLabel("ebw_set"));
        m_setNumUsersButton.addActionListener(this);
        centerGBPanel.constrain(m_setNumUsersButton,1,12,2,1);

        m_numUsersField = new NumberTextField(""+m_ExpApp.getNumUsers());
        centerGBPanel.constrain(m_numUsersField,3,12,1,1);

        m_autoStartBox = new Checkbox(m_EOApp.getLabels().getObjectLabel("ebw_eas"),false);
        centerGBPanel.constrain(m_autoStartBox,1,13,3,1);
    // End Setup List of Actions in Experiment

    // Start setup for east panel
       GridBagPanel eastGBPanel = new GridBagPanel();
       
       eastGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ebw_ed")),1,1,4,1);

       eastGBPanel.constrain(m_exptDesc,1,2,4,6);

       eastGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("ebw_ad")),1,8,4,1);

        m_actDesc.setEditable(false);
        eastGBPanel.constrain(m_actDesc,1,9,4,4);
    // End Setup for east panel.

        add("East",eastGBPanel);
        add("Center",centerGBPanel);

        pack();
        show();
        }

/**
 * Adds an ExperimentAction to the Experiment.
 *
 * @param str Doesn't seem to be used for anything anymore.
 * @param obj The ExperimentAction to add to the Experiment.
 */
    public void addAction(String str, ExperimentAction obj)
        {
        String[] s = buildExptListEntry(obj);

        m_ExpApp.addAction(obj);
        m_ExperimentList.addItem(s);
        if (m_canChangeNumUsers)
            {
            m_canChangeNumUsers = obj.allowChangeNumUsers();
            }
        }

/**
 * Processes any action events for the ActionBuilderWindow.
 *
 * @param e The ActionEvent.
 */    
    public void actionPerformed(ActionEvent e) 
        {
        if (getEditMode())
            return;

        if (e.getSource() instanceof Button)
            {
            Button theSource = (Button)e.getSource();

            if (theSource == m_setNumUsersButton)
                {
                if (m_canChangeNumUsers)
                    {
                    int num = Integer.valueOf(m_numUsersField.getText()).intValue();
                    m_ExpApp.setNumUsers(num);
                    m_setNumUsersLabel.setText(""+num);
                    }
                else
                    {
                    String[] str = new String[5];
                    str[0] = m_EOApp.getLabels().getObjectLabel("ebw_utcpnou");
                    str[1] = m_EOApp.getLabels().getObjectLabel("ebw_fte");
                    str[2] = " ";
                    str[3] = m_EOApp.getLabels().getObjectLabel("ebw_praththas");
                    str[4] = m_EOApp.getLabels().getObjectLabel("ebw_nou.");
                    new ErrorDialog(str);
                    }
                }
            }
        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    
        // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_open")))
                {
                setEditMode(true);
                new LoadExptWindow(m_EOApp,this);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_save")))
                {
                if (m_ExperimentList.getItemCount() > 0)
                    {
                    setEditMode(true);
                    m_ExpApp.setExptDesc(m_exptDesc.getText());
                    if (m_autoStartBox.getState())
                        m_ExpApp.setExtraData("AutoStart","true");
                    else
                        m_ExpApp.setExtraData("AutoStart","false");
                    new SaveExptWindow(m_EOApp,this);
                    }
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_delete")))
                {
                setEditMode(true);
                new DeleteExptWindow(m_EOApp,this);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_new")))
                {
                setExpApp(new Experiment());
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_exit")))
                {
                m_EOApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
       // Edit Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_aa")))
                {
                setEditMode(true);
                new AddActionWindow(m_EOApp,this);
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_ia")))
                {
                setEditMode(true);
                new ImportActionWindow(m_EOApp,this);
                }
            if ((m_exptIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_da"))))
                {
                m_exptAction = null;
                m_ExpApp.removeAction(m_exptIndex);
                m_ExperimentList.deselect(m_exptIndex);
                m_ExperimentList.remove(m_exptIndex);
                m_actDesc.setText("");
                m_exptIndex = -1;
                m_canChangeNumUsers = true;
                Enumeration enm = m_ExpApp.getActions().elements();
                while (enm.hasMoreElements())
                    {
                    ExperimentAction ea = (ExperimentAction)enm.nextElement();
                    if (!ea.allowChangeNumUsers())
                        {
                        m_canChangeNumUsers = false;
                        break;
                        }
                    }
                }
            if ((m_exptIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_fa"))))
                {
                setEditMode(true);

                m_exptAction.formatAction(m_EOApp,this);
                }
         // Format Menu.
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_ee")))
                {
                setEditMode(true);

                new FormatEndExperimentWindow(m_EOApp,this,m_ExpApp);
                }

         // Help Menu.
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("ebw_help")))
                {
                m_EOApp.helpWindow("ehlp_ecw");
                }
            }
        }

    public String[] buildExptListEntry(ExperimentAction obj)
        {
        String[] s = { obj.getDetailName(), "N" };

        if (obj.allowChangeNumUsers())
            s[1] = new String("Y");

        return s;
        }

    public boolean canChangeNumUsers()
        {
        return m_canChangeNumUsers;
        }

    public Vector getAvailableActions()
        {
        return m_availableActions;
        }
    public Vector getActionDescriptions()
        {
        return m_actionDescriptions;
        }
    public boolean getEditMode()
        {
        return m_EditMode;
        }
    public Experiment getExpApp()
        {
        return m_ExpApp;
        }
    public ExptOverlord getEOApp()
        {
        return m_EOApp;
        }

/**
 * Used to update the WebResourceBundle with any new entries for this window.
 *
 * @see girard.sc.web.WebResourceBundle
 */
    public void initializeLabels()
        {
        m_EOApp.initializeLabels("girard/sc/expt/awt/ebw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_ExperimentList)
                {
                if ((m_exptIndex != -1) && (m_changeOrder.getState()) && (theSource.getSelectedIndex() != -1) && (theSource.getSelectedIndex() != m_exptIndex))
                    {
                    int index2 = theSource.getSelectedIndex();
                    ExperimentAction ea1 = (ExperimentAction)m_ExpApp.getAction(m_exptIndex);
                    ExperimentAction ea2 = (ExperimentAction)m_ExpApp.getAction(index2);

                    String[] str = buildExptListEntry(ea1);
                    m_ExperimentList.replaceItem(str,index2);
                    m_ExpApp.getActions().setElementAt(ea1,index2);
                    str = buildExptListEntry(ea2);
                    m_ExperimentList.replaceItem(str,m_exptIndex);
                    m_ExpApp.getActions().setElementAt(ea2,m_exptIndex);
                    m_changeOrder.setState(false);
                    m_ExperimentList.select(index2);
                    }
                m_exptIndex = theSource.getSelectedIndex();
                if (m_exptIndex != -1)
                    {
                    m_exptAction = (ExperimentAction)m_ExpApp.getAction(m_exptIndex);
                    String str = m_exptAction.getDesc();
                    m_actDesc.setText(str);
                    }
                }
            }
         }

/**
 * Loads a list of available ExperimentActions that can be used to build the Experiment.
 * Uses an ExptActionTypesListReqMsg to get the list of ExperimentActions from the
 * database.
 *
 * @see girard.sc.expt.io.msg.ExptActionTypesListReqMsg
 */
    public void loadAvailableActions()
        {
        ExptActionTypesListReqMsg tmp = new ExptActionTypesListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em == null)
            {
            return;
            }

        if (em instanceof ExptActionTypesListReqMsg)
            {
            Object[] in_args = em.getArgs();
            m_availableActions = (Vector)in_args[0];
            m_actionDescriptions = (Vector)in_args[1];
            }
        else
            {
            new ErrorDialog((String)em.getArgs()[0]);
            }
        }

/**
 * Used to update the WebResourceBundle by removing any entries for this window.
 *
 * @see girard.sc.web.WebResourceBundle
 */
    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/ebw.txt");
        }

/**
 * Checks to make sure that the number of users needed is the same as the
 * Experiment.
 *
 * @param value The number to compare the number of users the Experiment is set to.
 * @return Returns true if the numbers are the same or if the number of users for the Experiment is set -1.
 */
    public boolean sameNumUsers(int value)
        {
        if (m_ExpApp.getNumUsers() == -1)
            return true;
        if (m_ExpApp.getNumUsers() == value)
            return true;
        return false;
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    public void setEOApp(ExptOverlord obj)
        {
        m_EOApp = obj;
        }
    public void setExpApp(Experiment obj)
        {
        m_ExpApp = obj;
        updateDisplay();
        }

    public void updateDisplay()
        {
        m_ExperimentList.removeAll();
        Enumeration enm = m_ExpApp.getActions().elements();
        m_canChangeNumUsers = true;
        while(enm.hasMoreElements())
            {
            ExperimentAction ea = (ExperimentAction)enm.nextElement();

            if (!ea.allowChangeNumUsers())
                m_canChangeNumUsers = false;

            String[] str = buildExptListEntry(ea);
            m_ExperimentList.addItem(str);
            }
        m_exptIndex = -1; 
        m_exptAction = null;
        m_exptDesc.setText(m_ExpApp.getExptDesc());
        m_actDesc.setText("");

        m_setNumUsersLabel.setText(""+m_ExpApp.getNumUsers());

        m_autoStartBox.setState(false);
        if (m_ExpApp.getExtraData("AutoStart") != null)
            {
            String str = (String)m_ExpApp.getExtraData("AutoStart");
            if (str.equals("true"))
                m_autoStartBox.setState(true);
            }
        }
    }
