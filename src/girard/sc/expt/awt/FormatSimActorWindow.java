package girard.sc.expt.awt;

import girard.sc.expt.obj.SimActor;
import girard.sc.expt.web.ExptOverlord;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/* The base window off of which any window created to format a SimActor should
 * be extended from.  Allows the programmer to make use of the LoadSimActorWindow
 * and SaveSimActorWindow.
 * <p>
 * <br> Started: 2-27-2001
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.1
 * @since JDK1.1
 * @see girard.sc.expt.awt.SaveSimActorWindow
 * @see girard.sc.expt.awt.LoadSimActorWindow
 */

public abstract class FormatSimActorWindow extends Frame implements ActionListener
    {
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    protected ExptOverlord m_EOApp;
/**
 * The link to the SimulantBuilderWindow;  This allows you to reset its m_EditMode
 * variable for the SimulantBuilderWindow to false when done formating;  Otherwise
 * the SimulantBuilderWindow doesn't know when to start processing action events again.
 * 
 */
    protected SimulantBuilderWindow m_SBWApp;
/**
 * The SimActor object being formatted.
 *
 */
    protected SimActor m_activeActor;

/**
 * Set to true when you don't want actions in the FormatSimActorWindow to be processed;
 * Usually set to true when loading or saving SimActor objects.
 *
 */
    boolean m_EditMode = false;

/**
 * The constructor.
 *
 * @param app1 The active ExptOverlord, will be assigned to m_EOApp.
 * @param app2 The active SimulantBuilderWindow, will be assigned to m_SBWApp.
 * @param app3 The SimActor type that the user will be formatting, assigned to m_activeActor.
 */
    public FormatSimActorWindow(ExptOverlord app1, SimulantBuilderWindow app2, SimActor app3)
        {
        super();

        m_EOApp = app1; /* Need to make pretty buttons. */
        m_SBWApp = app2; /* Need so can unset edit mode */
        m_activeActor = app3; /* Makes referencing easier */

        }

/**
 * Abstract class that must be created.  Used to process any ActionEvents sent to
 * class object.
 */
    public abstract void actionPerformed (ActionEvent e);

/**
 * @return Returns the SimActor, m_activeActor.
 */
    public SimActor getActiveActor()
        {
        return m_activeActor;
        }
/**
 * @return Returns the value of m_EditMode.
 */
    public boolean getEditMode()
        {
        return m_EditMode;
        }

/**
 * Abstract class that must be created.  Called to load in the label settings for
 * the class object.
 */
    public abstract void initializeLabels();

/**
 * Abstract class that must be created.  Called to remove any label settings for
 * the class object that were loaded in via the initializeLabels() function.
 */
    public abstract void removeLabels();

/**
 * Used by LoadSimActorWindow to update the FormatSimActorWindow variable m_activeActor
 * after a SimActor file has be loaded.  Can be overridden if the programmer wants to
 * update the user display for their window.
 *
 * @param sa The SimActor to set m_activeActor to.
 */
    public void setActiveActor(SimActor sa)
        {
        m_activeActor = sa;
        }
/**
 * Changes the value of m_EditMode.
 *
 * @param The new value to set m_EditMode to.
 */
    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
    }
