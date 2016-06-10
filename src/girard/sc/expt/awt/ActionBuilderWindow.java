package girard.sc.expt.awt;

import girard.sc.awt.ErrorDialog;
import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.msg.BaseActionTypesListReqMsg;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;

import java.awt.BorderLayout;
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
 * Lists all possible BaseActions that you can work with.  Goes out to the database
 * and retrieves the list of BaseActions available for editting.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1 
*/

public class ActionBuilderWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    protected ExptOverlord m_EOApp;

   // Menu Area
    protected MenuBar m_mbar = new MenuBar();
    protected Menu m_File, m_Help;

/**
 * Contains all the possible BaseActions that can be formatted.
 *
 */
    protected Vector m_availableActions = new Vector();
/**
 * Contains descriptions of all the BaseActions in m_availableActions.
 *
 * @see girard.sc.expt.awt.ActionBuilderWindow#m_availableActions
 */
    protected Vector m_actionDescriptions = new Vector();

    protected FixedList m_ActionList;
/**
 * The presently selected BaseAction.  This is the BaseAction that the user
 * will work with if they choose to.
 *
 */
    protected BaseAction m_activeAction = null;
    protected int m_actionIndex = -1;
/**
 * Where the description of the presently selected BaseAction is displayed.
 *
 */
    protected TextArea m_actDesc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

/**
 * Set to true when you don't want actions in the ActionBuilderWindow to be processed;
 * Set to true when editting a BaseAction object.
 *
 */
    protected boolean m_EditMode = false;

/**
 * The Constructor.
 *
 * @param app The ExptOverlord to set m_EOApp to.
 */
    public ActionBuilderWindow(ExptOverlord app)
        {
        m_EOApp = app;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("abw_title"));
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("abw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("abw_create"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("abw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);
 
    // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("abw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("abw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);

    // Setup List of Actions
        GridBagPanel tmpGBPanel = new GridBagPanel();

        loadAvailableActions();

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("abw_bal")),1,1,4,1);
        m_ActionList = new FixedList(8,false,1,25);
        m_ActionList.addItemListener(this);

        fillActionList();

        tmpGBPanel.constrain(m_ActionList,1,2,4,8);

        tmpGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("abw_details")),1,10,4,1);

        tmpGBPanel.constrain(m_actDesc,1,11,4,4);

    // End Setup List of Actions

     // End Setup User Fields

        add("Center",tmpGBPanel);

        pack();
        show();
        }

/**
 * Processes any ActionEvents for the ActionBuilderWindow.
 * <br>Exit -> Properly disposes of the window.
 * <br>Create -> Calls the formatAction() function for the presently selected BaseAction.
 * <br>Help -> Will call up the help file for the ActionBuilderWindow.
 *
 * @param e The ActionEvent that triggered this function.
 */    
    public void actionPerformed(ActionEvent e) 
        {
        if (getEditMode())
            return;

        if (e.getSource() instanceof MenuItem)
            {
            MenuItem theSource = (MenuItem)e.getSource();
    
        // File Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("abw_exit")))
                {
                m_EOApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
            if ((m_actionIndex > -1) && (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("abw_create"))))
                {
                setEditMode(true);
                m_activeAction.formatAction(m_EOApp,this);
                }
        // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("abw_help")))
                {
                m_EOApp.helpWindow("ehlp_abw");
                }
            }
        }

    public void fillActionList()
        {
        Enumeration enm = m_availableActions.elements();
        while (enm.hasMoreElements())
            {
            String[] str = new String[1];
            BaseAction ba = (BaseAction)enm.nextElement();

            str[0] = ba.getName();
            m_ActionList.addItem(str);
            }
        }

/**
 * @return Returns the value of m_EditMode.
 */
    public boolean getEditMode()
        {
        return m_EditMode;
        }
/**
 * @return Returns the ExptOverlord, m_EOApp.
 */
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
        m_EOApp.initializeLabels("girard/sc/expt/awt/abw.txt");
        }  

    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_ActionList)
                {
                m_actionIndex = theSource.getSelectedIndex();
                if (m_actionIndex != -1)
                    {
                    m_activeAction = (BaseAction)m_availableActions.elementAt(m_actionIndex);
                    String str = (String)m_actionDescriptions.elementAt(m_actionIndex);
                    m_actDesc.setText(str);
                    }
                }
            }
         }

/**
 * Sends a request to the server requesting a list of all available BaseActions.
 *
 * @see girard.sc.expt.io.msg.BaseActionTypesListReqMsg
 */
    public void loadAvailableActions()
        {
        BaseActionTypesListReqMsg tmp = new BaseActionTypesListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof BaseActionTypesListReqMsg)
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
        m_EOApp.removeLabels("girard/sc/expt/awt/abw.txt");
        }

    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    public void setEOApp(ExptOverlord obj)
        {
        m_EOApp = obj;
        }
    }
