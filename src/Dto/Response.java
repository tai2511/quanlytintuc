package Dto;

import java.io.Serializable;

public class Response implements Serializable {

	private Object data;

	private static final long serialVersionUID = 1L;

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
