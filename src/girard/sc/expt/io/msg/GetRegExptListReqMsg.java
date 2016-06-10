package girard.sc.expt.io.msg;

import girard.sc.expt.io.ExptServerConnection;
import girard.sc.expt.io.obj.ExptComptroller;
import girard.sc.expt.obj.ClientExptInfo;

import java.util.Enumeration;
import java.util.Hashtable;

public class GetRegExptListReqMsg extends ExptMessage 
    { 
    public GetRegExptListReqMsg (Object args[])
        {
        super(args);
        }

    public ExptMessage getExptServerConnectionResponse(ExptServerConnection esc)
        {
        Object[] args = this.getArgs();
        Object[] err_args = new Object[1];

// System.err.println("ESR: Exnet Register Experiment List Request Message");

        Hashtable tmp = esc.getActiveExpts();
        Hashtable out = new Hashtable();
        Enumeration enm = tmp.elements();
        while (enm.hasMoreElements())
            {
            ExptComptroller ec = (ExptComptroller)enm.nextElement();
            ClientExptInfo cei = new ClientExptInfo(ec);
            out.put(cei.getExptUID(),cei);
            }

        Object[] out_args = new Object[1];
        out_args[0] = out;

        return new GetRegExptListReqMsg(out_args);
        }
    }
