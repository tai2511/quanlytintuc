package Dto;

import java.io.Serializable;

public class Request implements Serializable {

	private ActionName action;
	private Object data;

	private static final long serialVersionUID = 1L;

	public ActionName getAction() {
		return action;
	}

	public void setAction(ActionName action) {
		this.action = action;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
