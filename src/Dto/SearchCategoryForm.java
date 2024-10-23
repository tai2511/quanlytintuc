package Dto;

import java.io.Serializable;

public class SearchCategoryForm implements Serializable {

	private String keyword;

	public SearchCategoryForm(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

}
