package girard.sc.ce.obj;

import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

/**
 * Base level object used to control the order of things.
 * <p>
 * <br> Started: 02-07-2003
 * <p>
 * @author: Dudley Girard
 */

public abstract class CEStateAction implements Serializable
    {

    public CEStateAction ()
        {
        }

    public abstract void executeAction(ExperimenterWindow ew);
    }
