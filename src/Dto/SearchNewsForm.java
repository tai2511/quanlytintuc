package Dto;

import java.io.Serializable;
import java.time.LocalDate;

public class SearchNewsForm implements Serializable {
	
	private String categoryCode;
	private LocalDate date;

	private static final long serialVersionUID = 1L;
	
	public SearchNewsForm(String categoryCode, LocalDate date) {
		this.categoryCode = categoryCode;
		this.date = date;
	}
	
	public String getCategoryCode() {
		return categoryCode;
	}
	
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
}
