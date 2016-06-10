package girard.sc.expt.sql;

import girard.sc.expt.obj.BaseDataInfo;
import girard.sc.wl.io.WLServerConnection;
import girard.sc.wl.io.msg.WLMessage;
import girard.sc.wl.sql.WLQuery;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 *  Used to load the data saved from an experiment.
 * <p>
 * <br> Started: 09-03-2001
 * <br> Modified: 02-06-2002
 * <p>
 * @author Dudley Girard
 * @version ExNet III 3.3
 * @since JDK1.1
 */

public class LoadDataResultsReq extends WLQuery
    {
/**
 * Needed so we know the values for Expt_Out_ID_INT and Action_Index_INT.
 */
    BaseDataInfo m_bdi;
/**
 * The name of the database table from which to get the data.
 */
    String m_dbTable = "none";

/**
 * The constructor.
 *
 * @param db The key identifier for the database to get the data from.
 * @param dbTable The value for m_dbTable.
 * @param bdi The value for m_bdi.
 * @param wlsc The WLServerConnection that this class object is being called from, needed
 * so we can get in touch with the database.
 * @param wlm The WLMessage that this class object is being called from.
 */
    public LoadDataResultsReq (String db, String dbTable, BaseDataInfo bdi, WLServerConnection wlsc, WLMessage wlm)
        {
        super(db, wlsc, wlm);

        m_bdi = bdi;
        m_dbTable = dbTable;
        }

/**
 * Runs the query on the database table.  Selects all rows from the specified table where
 * the value of m_bdi's variables m_ExptOutID and m_ActionIndex match the database table
 * column values Expt_Out_ID_INT and Action_Index_INT respectively.  Uses a simple Select
 * statement to get the data.
 *
 * @return Returns the ResultSet from running the query.
 */
    public ResultSet runQuery()
        {
        synchronized (m_DBA)
            {
            try 
                {
                if (!createConnection())
                    return null;

                // get a Statement object from the Connection
                //
                
                Statement stmt = m_DB.createStatement();
		System.out.println("in the runQuery method of lddataresReq.java checking "+m_dbTable);
                ResultSet rs;
		// temporary dirty hack!
		// add the columnAction index to the CEExternality Table
		//		if(m_dbTable.equals("CE_Externality_Data_T"))
		//		    rs = stmt.executeQuery("SELECT * FROM "+m_dbTable+" WHERE Expt_Out_ID_INT = "+m_bdi.getExptOutID());
		//		else
		    rs = stmt.executeQuery("SELECT * FROM "+m_dbTable+" WHERE Expt_Out_ID_INT = "+m_bdi.getExptOutID()+" AND Action_Index_INT = "+m_bdi.getActionIndex());

                // closeConnection();
		
		

                return rs;
                }
            catch( Exception e ) 
                {
                m_wlsc.addToLog(e.getMessage());
                closeConnection();
                return null;
                }
            }
        }
/**
 * Does nothing, but required.
 * 
 * @return Returns true.
 */
    public boolean runUpdate() 
        {
        return true;
        }
    }
