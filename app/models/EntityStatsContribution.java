package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class EntityStatsContribution extends Model {

	public EntityStatsContribution() {
		
	}
	
	public EntityStatsContribution(String contributor, int entityStatsType) {
		this.contributor = contributor;
		this.entityStatsType = entityStatsType;
	}
	/**
	 * contributor. Could be username or clientIp
	 */
	public String contributor;
	
	public int entityStatsType;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	public EntityStats entityStats;
	
	
}
