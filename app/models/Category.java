package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import java.util.*;

@Entity
@Table(name="tb_category")
public class Category extends Model {
    
	public String name;
	public String parentName;
	public String type;
	
	
	public static List<Category> findAllSubcategories() {
		return  Category.find("parentName is not null and type = 'classifieds' order by name").fetch();
	}
	
}
