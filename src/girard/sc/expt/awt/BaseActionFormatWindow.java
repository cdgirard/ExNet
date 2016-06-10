package girard.sc.expt.awt;

import girard.sc.expt.obj.BaseAction;
import girard.sc.expt.web.ExptOverlord;

import javax.swing.JFrame;

/**
 * This is the base window used for any windows required to format a BaseAction.
 * Allows you to make use of the SaveBaseActionWindow, LoadBaseActionWindow, and
 * DeleteBaseActionWindow to do all saves, loads, and deletes of your BaseAction
 * object.
 * <p>
 * <br>Started: 02-06-2002
 * <br>Modified: 04-01-2003
 * <p>
 *
 * @author Dudley Girard
 * @version ExNet III 3.41
 * @since JDK1.4
 * @see girard.sc.expt.obj.BaseAction#formatAction(ExptOverlord app, ActionBuilderWindow abw)
 * @see girard.sc.expt.awt.SaveBaseActionWindow
 * @see girard.sc.expt.awt.LoadBaseActionWindow
 * @see girard.sc.expt.awt.DeleteBaseActionWindow
 */

public class BaseActionFormatWindow extends JFrame
    {
/**
 * The BaseAction object being formatted.
 *
 */
    protected BaseAction m_BApp;
/**
 * Allows access to ExptOverlord's functions, key among them being the ones
 * dealing with the WebResourceBundle and the sending of ExptMessages.
 *
 */
    protected ExptOverlord m_EOApp;
/**
 * The link to the ActionBuilderWindow;  This allows you to reset its m_EditMode
 * variable to false when done formating;  Otherwise the ActionBuilderWindow doesn't
 * know when to start processing action events again.
 * 
 */
    protected ActionBuilderWindow m_ABWApp;

/**
 * Set to true when you don't want actions in the BaseActionFormatWindow to be processed;
 * Usually set to true when loading, saving, or deleting BaseAction objects.
 *
 */
    protected boolean m_EditMode = false;

/**
 * The constructor.
 *
 * @param app1 The active ExptOverlord, will be assigned to m_EOApp.
 * @param app2 The active ActionBuilderWindow, will be assigned to m_ABWApp.
 * @param app3 The BaseAction type that the user will be formatting, assigned to m_BApp.
 */
    public BaseActionFormatWindow(ExptOverlord app1, ActionBuilderWindow app2, BaseAction app3)
        {
        super();

        m_EOApp = app1;
        m_ABWApp = app2;
        m_BApp = app3;
        }

/**
 * @return Returns the value of m_EditMode.
 */
    public boolean getEditMode()
        {
        return m_EditMode;
        }
/**
 * @return Returns m_EOApp.
 */
    public ExptOverlord getEOApp()
        {
        return m_EOApp;
        }

/**
 * Used to update the WebResourceBundle with any new entries for this window.
 * Normally you will override this function to include the file with your
 * new labels.
 * <p>
 * Example Code: m_EOApp.initializeLabels("girard/sc/expt/awt/dbaw.txt");
 * <p>
 * @see girard.sc.web.WebResourceBundle
 */
    public void initializeLabels() {}

/**
 * Used to update the WebResourceBundle by removing any entries for this window.
 * Normally you will override this function to include the file with your
 * new labels.
 * <p>
 * Example Code: m_EOApp.removeLabels("girard/sc/expt/awt/dbaw.txt");
 * <p>
 * @see girard.sc.web.WebResourceBundle
 */
    public void removeLabels() {}

/**
 * Changes the active BaseActio that is being formatted.  We need this for when a
 * saved BaseAction is loaded using the LoadBaseActionWindow.
 *
 * @param ba The new BaseAction to set m_BApp to.
 * @see girard.sc.expt.awt.LoadBaseActionWindow
 */
    public void setActiveBaseAction(BaseAction ba)
        {
        m_BApp = ba;
        }
/**
 * Used to change the value of m_EditMode.
 * 
 * @value The new value to set m_EditMode to.
 */
    public void setEditMode(boolean value)
        {
        m_EditMode = value;
        }
/**
 * Used to change the active ExptOverlord pointed at by m_EOApp.
 *
 * @param obj The new ExptOverlord to set m_EOApp to.
 */
    public void setEOApp(ExptOverlord obj)
        {
        m_EOApp = obj;
        }
    }