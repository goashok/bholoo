package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="tb_address")
public class Address extends Model {

	public String address1;
	public String address2;
	public String city;
	public String state;
	public String zip;
	public String phone;
	public String fax;
}
