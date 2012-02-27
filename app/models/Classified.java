package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name="tb_classifieds")
public class Classified extends Model {
    
	public String title;
	@Lob
	public String description;
	public double price;
	public String city;
	public long categoryId;
	public String phone;
	public String postedBy;
	public Date postedAt;
		
	public Classified() {
		
	}

	public Classified(String title, String description, double price,
			String city, long categoryId, String phone, String postedBy) {
		this.title = title;
		this.description = description;
		this.price = price;
		this.city = city;
		this.categoryId = categoryId;
		this.phone = phone;
		this.postedBy = postedBy;
		this.postedAt = new Timestamp(System.currentTimeMillis());
	}
	
	
}
