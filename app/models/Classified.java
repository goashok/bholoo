package models;

import play.*;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.db.jpa.*;
import play.modules.s3blobs.S3Blob;

import javax.persistence.*;

import org.hibernate.annotations.Fetch;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Timestamp;
import java.util.*;

@Entity
@Table(name="tb_classifieds")
public class Classified extends Model {
    

	@Required(message="Title is required")
	public String title;
	
	@Required(message="Description is required")
	@Lob	
	public String description;
	
	public double price;
	
	@Required(message="City is required")
	public String city;
	
	public String zip;
	
	@Required(message="Category is required") 
	public long categoryId;
	
	@Phone(message="Invalid phone number")
	public String phone;
	
	public String postedBy;
	
	public Date postedAt;	
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	public List<Document> images = new ArrayList<Document>();

	public Classified() {
		
	}

	public Classified(String title, String description, double price,
			String city, String zip, long categoryId, String phone, String postedBy) {
		this.title = title;
		this.description = description;
		this.price = price;
		this.city = city;
		this.zip = zip;
		this.categoryId = categoryId;
		this.phone = phone;
		this.postedBy = postedBy;
		this.postedAt = new Timestamp(System.currentTimeMillis());
	}
	
	public void addImage(File image) throws FileNotFoundException {
		images.add(new Document(image));
	}
	
	public int numImages() {
		return images.size();
	}
	
	
	public List<Document> getImages() {
		return this.images;
	}
	
}
