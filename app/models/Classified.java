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
	
	public int entityState;
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY)
	@JoinTable(name="tb_classified_images")
	public List<Document> images = new ArrayList<Document>();

	public void addImage(File image) throws FileNotFoundException {
		images.add(new Document(image));
	}
	
	public int numImages() {
		return images.size();
	}
	
	
	public List<Document> getImages() {
		return this.images;
	}
	
	public EntityState entityState() {
		return EntityState.fromOrdinal(entityState);
	}

	@Override
	public String toString() {
		return "Classified [id=" + id + ", entityState=" + entityState()
				+ ", title=" + title + ", description=" + description
				+ ", price=" + price + ", city=" + city + ", zip=" + zip
				+ ", categoryId=" + categoryId + ", phone=" + phone
				+ ", postedBy=" + postedBy + ", postedAt=" + postedAt
				+ ", images=" + images + "]";
	}

	

	
	
}
