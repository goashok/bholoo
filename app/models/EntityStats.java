package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class EntityStats extends Model
{
	/**
	 * Type of Entity
	 * @see EntityType
	 */
	public int entityType;
	/**
	 * id of the entity for the above type
	 */
	public long entityTypeId;
	
	/**
	 * number of page views
	 */
	public long hits;
	
	/**
	 * number of likes
	 */
	public long likes;
	/**
	 * number of reported abuses
	 */
	public long abuses;
	/**
	 * number of times shared with other users
	 */
	public long shares;
	
	/**
	 * number of times reported as spam
	 */
	public long spams;
	
	/**
	 * number of times users have replied
	 */
	public long replies;
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
	public List<EntityStatsContribution> contributions = new ArrayList<EntityStatsContribution>();
	
	@Override
	public String toString() {
		return "EntityStats [entityType=" + EntityType.fromOrdinal(entityType).name() + ", entityTypeId="
				+ entityTypeId + ", hits=" + hits + ", likes=" + likes
				+ ", abuses=" + abuses + ", shares=" + shares +  ", spams=" + spams + "]";
	}
	
	
}
