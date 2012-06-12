package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.data.validation.Phone;
import play.db.jpa.Model;

@Entity
@Table(name="tb_business")
public class Business extends Model{
	
	public String name;
	@Phone
	public String phone;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="tb_business_categories", joinColumns=@JoinColumn(name="business_id", referencedColumnName="id"), inverseJoinColumns=@JoinColumn(name="category_id", referencedColumnName="id"))
	public List<Category> categories;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="tb_business_addresses", joinColumns=@JoinColumn(name="business_id", referencedColumnName="id"), inverseJoinColumns=@JoinColumn(name="address_id", referencedColumnName="id"))
	public List<Address> addresses;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="tb_business_hours", joinColumns=@JoinColumn(name="business_id", referencedColumnName="id"), inverseJoinColumns=@JoinColumn(name="hours_id", referencedColumnName="id"))
	public List<Hours> hours;
	

}
