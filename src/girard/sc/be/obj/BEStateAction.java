package girard.sc.be.obj;
/* 
   The object that contains the preset color values for BE exchange
   network class objects.

   Author: Dudley Girard
   Started: 1-24-2001
*/

import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

public abstract class BEStateAction implements Serializable
    {

    public BEStateAction ()
        {
        }

    public abstract void executeAction(ExperimenterWindow ew);
    }
