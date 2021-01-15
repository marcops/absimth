package absimth.sim.gui.helper;

import javafx.beans.property.SimpleStringProperty;

public class MemoryTableHelper {
	private SimpleStringProperty address;
	private SimpleStringProperty x0;
	private SimpleStringProperty x1;
	private SimpleStringProperty x2;
	private SimpleStringProperty x3;
	private SimpleStringProperty x4;
	private SimpleStringProperty x5;
	private SimpleStringProperty x6;
	private SimpleStringProperty x7;

	public MemoryTableHelper(int add, 
			int a0, int a1, int a2, int a3, int a4, int a5, int a6, int a7) {
		this.address = new SimpleStringProperty(String.format("0x%06X", add));
		this.x0 = new SimpleStringProperty(String.format("0x%06X", a0));
		this.x1 = new SimpleStringProperty(String.format("0x%06X", a1));
		this.x2 = new SimpleStringProperty(String.format("0x%06X", a2));
		this.x3 = new SimpleStringProperty(String.format("0x%06X", a3));
		this.x4 = new SimpleStringProperty(String.format("0x%06X", a4));
		this.x5 = new SimpleStringProperty(String.format("0x%06X", a5));
		this.x6 = new SimpleStringProperty(String.format("0x%06X", a6));
		this.x7 = new SimpleStringProperty(String.format("0x%06X", a7));

	}

	public String getAddress() {
		return address.get();
	}

	public void setAddress(String address) {
		this.address.set(address);
	}

	public SimpleStringProperty nameAddress() {
		return address;
	}

	public String getX0() {
		return x0.get();
	}

	public void setX0(String x) {
		this.x0.set(x);
	}

	public SimpleStringProperty nameX0() {
		return x0;
	}

	public String getX1() {
		return x1.get();
	}

	public void setX1(String x) {
		this.x1.set(x);
	}

	public SimpleStringProperty nameX1() {
		return x1;
	}

	public String getX2() {
		return x2.get();
	}

	public void setX2(String x) {
		this.x2.set(x);
	}

	public SimpleStringProperty nameX2() {
		return x2;
	}

	public String getX3() {
		return x3.get();
	}

	public void setX3(String x) {
		this.x3.set(x);
	}

	public SimpleStringProperty nameX3() {
		return x3;
	}

	public String getX4() {
		return x4.get();
	}

	public void setX4(String x) {
		this.x4.set(x);
	}

	public SimpleStringProperty nameX4() {
		return x4;
	}

	public String getX5() {
		return x5.get();
	}

	public void setX5(String x) {
		this.x5.set(x);
	}

	public SimpleStringProperty nameX5() {
		return x5;
	}

	public String getX6() {
		return x6.get();
	}

	public void setX6(String x) {
		this.x6.set(x);
	}

	public SimpleStringProperty nameX6() {
		return x6;
	}

	public String getX7() {
		return x7.get();
	}

	public void setX7(String x) {
		this.x7.set(x);
	}

	public SimpleStringProperty nameX7() {
		return x7;
	}
}
