package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="tb_country")
public class Country extends Model {
    
	public String isoCode;
	public String name;
}
