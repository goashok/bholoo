package models;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="tb_hours")
public class Hours extends Model {
	
	public String schedule;
	public int fromHr;
	public String fromAmPm;
	public int toHr;
	public String toAmPm;
	
	@ManyToOne(fetch=FetchType.LAZY)
	public Business business;
}
