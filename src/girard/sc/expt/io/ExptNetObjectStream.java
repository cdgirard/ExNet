//
// General class for 
//

package girard.sc.expt.io;

import girard.sc.expt.io.msg.ExptMessage;
import girard.sc.expt.web.ExptOverlord;
import girard.sc.io.NetObjectStream;
import girard.sc.io.msg.TCPMessage;

import java.io.IOException;

public class ExptNetObjectStream extends NetObjectStream
    {
    ExptOverlord m_EOApp;

    public ExptNetObjectStream(String host, int port,ExptOverlord eo)
        {
        super(host,port);

        m_EOApp = eo;
        }

    public void sendMessage(TCPMessage msg) throws IOException
        {
        if (msg instanceof ExptMessage)
            {
            m_EOApp.initializeWLMessage((ExptMessage)msg);
            }

        if (m_out != null)
            {
            m_out.writeObject(msg);
            m_out.flush();
            }
        }
    }
