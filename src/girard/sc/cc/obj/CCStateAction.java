package girard.sc.cc.obj;
/* 
   The object that contains the preset color values for BE exchange
   network class objects.

   Author: Dudley Girard
   Started: 05-29-2001
*/

import girard.sc.expt.awt.ExperimenterWindow;

import java.io.Serializable;

public abstract class CCStateAction implements Serializable
    {

    public CCStateAction ()
        {
        }

    public abstract void executeAction(ExperimenterWindow ew);
    }
