package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="tb_city")
public class City extends Model {
    
	public String name;
	
	@ManyToOne
	public State state;
}
