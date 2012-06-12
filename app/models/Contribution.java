package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="tb_contribution")
public class Contribution extends Model {

	public Contribution() {
		
	}
	
	public Contribution(String contributor, int statsType) {
		this.contributor = contributor;
		this.statsType = statsType;
	}
	/**
	 * contributor. Could be username or clientIp
	 */
	public String contributor;
	
	public int statsType;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	public Stats entityStats;
	
	
}
