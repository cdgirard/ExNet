package girard.sc.ce.obj;
import java.io.Serializable;
/**
 *  This internal class stores the actual externality.
 */
	public class iExternalityInfo implements Serializable{
		
		/**
		 * The Node that is affected.
		 */
		protected String node ; // node affected

		/**
		 *  The externality is calculated as @param A *profit+@param B
		 */
		protected float AValue;
		protected float BValue;
		/**
		 * Default Constructor
		 * @param s is the affected Node
		 * @param a the A value in A*profit + B
		 * @param b the B value
		 */
		public iExternalityInfo(String s, float a, float b){
			node = s;
			AValue = a;
			BValue = b;
		}
		/**
		 * This method checks if this node is the @param affector
		 * @return
		 */
		public boolean checkNode(String affected){
			if(node.equals(affected))
				return true;
			return false;
		}
		
		/**
		 * @return Returns the aValue.
		 */
		public float getAValue() {
			return AValue;
		}
		/**
		 * @param value The aValue to set.
		 */
		public void setAValue(float value) {
			AValue = value;
		}
		/**
		 * @return Returns the bValue.
		 */
		public float getBValue() {
			return BValue;
		}
		/**
		 * @param value The bValue to set.
		 */
		public void setBValue(float value) {
			BValue = value;
		}
	}
		
