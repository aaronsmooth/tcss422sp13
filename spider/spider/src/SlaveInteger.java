
public class SlaveInteger {
	private int myInt;
	
	public SlaveInteger(int i) {
		myInt = i;
	}

	public void decrement() {
		myInt--;
	}
	
	public void increment() {
		myInt++;
	}
	
	public void setVal(int i) {
		myInt = i;
	}
	
	public int getVal() {
		return myInt;
	}
}
