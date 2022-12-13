package rs.ac.bg.etf.kdp.lab;

public class BooleanStatus implements Status {

	public BooleanStatus(boolean status) {
		super();
		this.status = status;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	boolean status;

}
