package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="tb_hours")
public class Hours extends Model {
	
	public String schedule;
	public int from;
	public String fromAmPm;
	public int to;
	public String toAmPm;
}
