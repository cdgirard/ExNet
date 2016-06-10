package girard.sc.io;
/* 
   Writes an object to a file so that it can then be read and stored in
   MS's SQL Server DB.

   Author: Dudley Girard
   Started: 2-12-2001
*/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.util.Vector;

public class FMSObjCon{
    
    public FMSObjCon (){
    }

    public static Vector addObjectToStatement(int index, Object obj, PreparedStatement ps){
        Vector v = new Vector();
	
        try{
            // Because MS SQL doesn't support Java Objects we have to write the object to a 
            // file first, then read it from the file as a binary stream.
	    // long pt = Calendar.getInstance().getTime().getTime();
	    // int rn = 0;
	    // boolean flag = true;
	    // while (flag)  // Just to make sure we don't create a filename that is already in use.
	    //    {
	    //       rn = (int)(10000000*Math.random());
	    //       File f = new File(""+pt+"_"+rn+".obj");
	    //       if (!f.exists())
	    //            flag = false;
	    //    }
	    
            ByteArrayOutputStream BytesOut = new ByteArrayOutputStream();
	    ObjectOutputStream ObjectOut = new ObjectOutputStream(BytesOut);
	    ObjectOut.writeObject(obj);
	    ObjectOut.flush();
	    ObjectOut.close();
	    BytesOut.flush();
	    BytesOut.close();
	    
	    byte[] buf = BytesOut.toByteArray();
	    
	    ByteArrayInputStream BytesIn = new ByteArrayInputStream(buf);
	    ps.setBinaryStream(index, BytesIn, buf.length);
	    
            v.addElement(BytesIn);
	}
        catch (Exception e) { System.err.println(e); }
	
        return v;
    }
    
    public static void cleanUp(Vector v){
        try {
            ByteArrayInputStream BytesIn = (ByteArrayInputStream)v.elementAt(0);
	    BytesIn.close();
	}
        catch (Exception e) { System.err.println(e); }
    }
}
