package Model;

import java.io.Serializable;
import java.time.LocalDate;

public class NewsModel implements Serializable {
	
	private String code;
    private String title;
    private String content;
    private String image;
    private String categoryCode;
    private LocalDate createDate;
    
    private static final long serialVersionUID = 1L;

    public NewsModel(String code, String title, String content, String image, String categoryCode, LocalDate createDate) {
        this.code = code;
        this.title = title;
        this.content = content;
        this.image = image;
        this.categoryCode = categoryCode;
        this.createDate = createDate;
    }
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public LocalDate getCreateDate() {
		return createDate;
	}
	public void setCreateDate(LocalDate createDate) {
		this.createDate = createDate;
	}
}
