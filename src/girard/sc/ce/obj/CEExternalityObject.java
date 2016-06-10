/*
 * Created on Oct 21, 2004
 * This object stores the actual externality information.
 * A default object is created for every CEExpt. 
 */
package girard.sc.ce.obj;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Vector;
/**
 * @author </i>Murali</i>dhar Narumanchi
 * This is the first implementation. The design of this class is as follows:
 * The hashtable h contains the entire externality information.
 *  Key = Node that causes the externality
 *  Object = A vector V containing ExObj.
 *  we need a vector because, one node can affect multiple nodes.
 * 
 * In the next iteration, 
 * - the externality could also depend on the edge from which the payment is coming.
 *  E.g., node 1 has 5 edges and if 1 node 2 gets 10% of whatever 1 incurs on edges a and b. 
 * - the design has to changed to make it consistent with the 
 * existing OO-design. Every node should store the externality information locally. 
 * 
 */
public class CEExternalityObject implements Serializable {

	/**
	 * The hash table that contains all the externality information. 
	 */
	protected Hashtable h; // the key is the affecting node

	public CEExternalityObject(){
		h  = new Hashtable();
	}
	
	public void addExternality(String affectingNode, String affectedNode, float AVal, float BVal){
		// get the data from the hashTable in the first place
		Vector v = (Vector)h.get(affectingNode);
		if(v==null){
			v = new Vector();
			// create a new object
			iExternalityInfo inew = new iExternalityInfo(affectedNode,AVal,BVal);
			v.add(inew);
			// add the to the hash table
			h.put(affectingNode,v);
		}
		else{
			// Vector is not null, there is already some data in it.
			int size = v.size();
			boolean present = false;
			for(int i =0;i<size;i++){
				iExternalityInfo iEI = (iExternalityInfo) v.elementAt(i);
				if(iEI.checkNode(affectedNode)){ // is it already present?
					present = true;
					iEI.setAValue(AVal);
					iEI.setBValue(BVal);
					break;
				}
			}
			if(!present){
				iExternalityInfo iEI = new iExternalityInfo(affectedNode,AVal,BVal);
				v.add(iEI);
			}
		}
	}

	/**
	 * @param affectingNode is the Node that is causing the externality
	 * @return the vector containing the affected Nodes and their externality
	 */
	public Vector getExternality(String affectingNode){
		Vector retVec = (Vector)h.get(affectingNode);
		return retVec;
	}
	
	public iExternalityInfo getExternality(String affectingNode, String affected){
		iExternalityInfo iei = null;
		Vector v = (Vector)h.get(affectingNode);
		if(v==null)
			return null;
		int size = v.size();
		for(int i =0;i<size;i++){
			iExternalityInfo tmpiei = (iExternalityInfo)v.elementAt(i);
			if(tmpiei.checkNode(affected)){
				iei = tmpiei;
				break;
			}
		}
		return iei;
	}
}
