package girard.sc.expt.awt;

import girard.sc.awt.FixedList;
import girard.sc.awt.GridBagPanel;
import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.io.msg.SimActorTypeListReqMsg;
import girard.sc.expt.obj.SimActor;
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
 * Lists all possible SimActors that you can work with.  Goes out to the database
 * and retrieves the list of SimActors available for editting.
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1 
*/

public class SimulantBuilderWindow extends Frame implements ActionListener,ItemListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    ExptOverlord m_EOApp;
   
/**
 * Menu Area
 */
    MenuBar m_mbar = new MenuBar();
    Menu m_File, m_View, m_Help;

/**
 * Contains all the possible SimActors that can be formatted.
 *
 */
    Vector m_availableActors = new Vector();

/**
 * Displays the list of SimActors contained in m_availableActors.
 */
    FixedList m_SimActorList;
/**
 * The SimActor type the user has selected to edit or create.
 */
    SimActor m_activeActor = null;
/**
 * The index in m_availableActors where m_activeActor is located.
 */
    int m_actorIndex = -1;

/**
 * Displays the description of the SimActor selected in m_SimActorList.
 */
    TextArea m_desc = new TextArea("",4,25,TextArea.SCROLLBARS_VERTICAL_ONLY);

/**
 * Set to true when don't want actions in the SimulantBuilderWindow to be processed;
 * Set to true when editting a SimActor object.
 *
 */
    boolean m_EditMode = false;

/**
 * The Constructor.
 *
 * @param app The ExptOverlord to set m_EOApp to.
 */
    public SimulantBuilderWindow(ExptOverlord app)
        {
        m_EOApp = app;

        initializeLabels();

        setLayout(new BorderLayout());
        setBackground(m_EOApp.getDispBkgColor());
        setTitle(m_EOApp.getLabels().getObjectLabel("sbw_title"));
        setFont(m_EOApp.getMedWinFont());

        m_mbar.setFont(m_EOApp.getSmWinFont());

    // Setup North Area
        setMenuBar(m_mbar);
    
        MenuItem tmpMI;

        m_File = new Menu(m_EOApp.getLabels().getObjectLabel("sbw_file"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("sbw_create"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("sbw_exit"));
        tmpMI.addActionListener(this);
        m_File.add(tmpMI);

        m_mbar.add(m_File);

    // Help Menu

        m_Help = new Menu(m_EOApp.getLabels().getObjectLabel("sbw_help"));

        tmpMI = new MenuItem(m_EOApp.getLabels().getObjectLabel("sbw_help"));
        tmpMI.addActionListener(this);
        m_Help.add(tmpMI);

        m_mbar.add(m_Help);

    // Setup List of Actions
        GridBagPanel centerGBPanel = new GridBagPanel();

        loadAvailableActors();

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbw_al")),1,1,4,1);
        m_SimActorList = new FixedList(8,false,1,16);
        m_SimActorList.addItemListener(this);
        centerGBPanel.constrain(m_SimActorList,1,2,4,8);

        Enumeration enm = m_availableActors.elements();
        while (enm.hasMoreElements())
            {
            SimActor sa = (SimActor)enm.nextElement();
            String[] str = new String[1];
            str[0] = sa.getActorName();
            m_SimActorList.addItem(str);
            }

        centerGBPanel.constrain(new Label(m_EOApp.getLabels().getObjectLabel("sbw_description")),1,10,4,1);

        m_desc.setEditable(false);
        centerGBPanel.constrain(m_desc,1,11,4,4);

    // End Setup List of Actions


        add("Center",centerGBPanel);

        pack();
        show();
        }

/**
 * Processes any ActionEvents for the SimulantBuilderWindow.
 * <br>Exit -> Properly disposes of the window.
 * <br>Create -> Calls the formatActor() function for the presently selected SimActor.
 * <br>Help -> Will call up the help file for the SimulantBuilderWindow.
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
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("sbw_create")))
                {
                if (m_activeActor != null)
                    {
                    m_EditMode = true;
                    SimActor sa = (SimActor)m_activeActor.clone();
                    sa.formatActor(m_EOApp,this);
                    return;
                    }
                }
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("sbw_exit")))
                {
                m_EOApp.setEditMode(false);
                removeLabels();
                dispose();
                return;
                }
       // Help Menu
            if (theSource.getLabel().equals(m_EOApp.getLabels().getObjectLabel("sbw_help")))
                {
                m_EOApp.helpWindow("ehlp_sbw");
                return;
                }
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
        m_EOApp.initializeLabels("girard/sc/expt/awt/sbw.txt");
        }  

/**
 * Handles any ItemEvents associated with this class object.
 *
 * @param e The ItemEvent that triggered this function.
 */
    public void itemStateChanged(ItemEvent e)
        {
        if (e.getSource() instanceof FixedList)
            {
            FixedList theSource = (FixedList)e.getSource();

            if (theSource == m_SimActorList)
                {
                m_actorIndex = theSource.getSelectedIndex();
                if (m_actorIndex != -1)
                    {
                    m_activeActor = (SimActor)m_availableActors.elementAt(m_actorIndex);
                    m_desc.setText(m_activeActor.getActorDesc());
                    }
                }
            }
         }

/**
 * Loads in the list of all the SimActor types that can be editted or created.
 * Does this by sending a SimActorTypeListReqMsg to the server.  Stores the returned
 * list in m_availableActors.
 *
 * @see girard.sc.expt.io.msg.SimActorTypeListReqMsg
 */
    public void loadAvailableActors()
        {
        SimActorTypeListReqMsg tmp = new SimActorTypeListReqMsg(null);
        ExptMessage em = m_EOApp.sendExptMessage(tmp);

        if (em instanceof SimActorTypeListReqMsg)
            {
            Object[] in_args = em.getArgs();
            m_availableActors = (Vector)in_args[0];
            }
        }

/**
 * Used to update the WebResourceBundle by removing any entries for this window.
 *
 * @see girard.sc.web.WebResourceBundle
 */
    public void removeLabels()
        {
        m_EOApp.removeLabels("girard/sc/expt/awt/sbw.txt");
        }

/**
 * Changes the value of m_EditMode.
 *
 * @param value The new value to set m_EditMode to.
 */
    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
/**
 * Changes the ExptOverlord that m_EOApp is set to.
 *
 * @param obj The new ExptOverlord to set m_EOApp to.
 */
    public void setEOApp(ExptOverlord obj)
        {
        m_EOApp = obj;
        }
    }
