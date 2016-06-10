package girard.sc.expt.obj;

import java.io.Serializable;
import java.sql.CallableStatement;

/** 
 * The base object for storing output data for an ExperimentAction.
 * <p>
 * Started: 7-25-2001
 * LastModified: 02-04-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.2
 * @since JDK1.1  
 */

public abstract class DataOutputObject implements Serializable
    {

/**
 * The Constructor.
 */
    public DataOutputObject ()
        {
        }

/**
 * Used to format the CallableStatement with all the data to write to
 * the database.
 *
 * @param cs The CallableStatement.
 */
    public abstract void formatInsertStatement(CallableStatement cs) throws java.sql.SQLException;

/**
 * Defines the setup for the CallableStatement that will be used to
 * store the data in the database.
 */
    public abstract String getInsertFormat();
    }
