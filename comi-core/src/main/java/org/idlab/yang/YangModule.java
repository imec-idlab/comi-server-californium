package org.idlab.yang;

public class YangModule {
	
	public static class Leaf<T>{
		private T element;
		public T getLeafValue() {
			return element;
		}

		public void setLeafValue(T element) {
			this.element = element;
		}

		public Leaf(T someElement) {
			this.element = someElement;
		}
	}
	
	public class Bits{
		private byte options;
		public void setBitValue(int pos, boolean value) {
			setBit(options,pos,value);
		}
		public boolean getBitValue(int pos) {
			return getBit(options,pos);
		}
	}
	
	public final static byte setBit(byte _byte,int bitPosition,boolean bitValue)
	{
	    if (bitValue)
	        return (byte) (_byte | (1 << bitPosition));
	    return (byte) (_byte & ~(1 << bitPosition));
	}

	public final static Boolean getBit(byte _byte, int bitPosition)
	{
	    return (_byte & (1 << bitPosition)) != 0;
	}
}
