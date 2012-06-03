package jobs;

import java.util.List;

import models.EntityStats;
import models.EntityStatsContribution;
import models.EntityStatsType;
import models.EntityType;
import play.Logger;
import play.jobs.Job;

public class EntityStatsJob extends Job<EntityStats> {

	private EntityType entityType;
	private long entityId;
	private EntityStatsType entityStatsType;
	private String contributor;
	
	public EntityStatsJob(EntityStatsType entityStatsType, EntityType entityType, long entityId, String contributor) {
		this.entityStatsType = entityStatsType;
		this.entityType = entityType;
		this.entityId = entityId;
		this.contributor = contributor;
	}
	
	public void doJob() {
		EntityStatsContribution contribution = EntityStatsContribution.find("contributor = '" + contributor + "' and entityStats.entityType = " + entityType.ordinal() + 
											" and entityStats.entityTypeId = " + entityId + " and entityStatsType = " + entityStatsType.ordinal()).first();
		if(contribution != null)
		{
			Logger.info("Contributor %s has already contributed ", contributor);
			//Already contributed
			return;
		}
		EntityStats stats = EntityStats.find("byEntityTypeAndEntityTypeId", entityType.ordinal(), entityId).first();
		if(stats == null) {
			Logger.info("No stats found for %s with id %d", entityType.name(), entityId);
			stats = new EntityStats();
			stats.entityType = entityType.ordinal();
			stats.entityTypeId = entityId;
		}else {
			Logger.info("Found  entityStats " + stats);
			
		}
		contribution = new EntityStatsContribution(contributor, entityStatsType.ordinal());
		//link for id propagation
		contribution.entityStats = stats;
		stats.contributions.add(contribution);
		switch(entityStatsType) 
		{
		case Hits:		stats.hits++;
						break;
		case Likes: 	stats.likes++;
						break;
		case Abuses: 	stats.abuses++;
						break;
		case Shares:	stats.shares++;
						break;
		case Spams:		stats.spams++;
						break;
		}
		stats.save();
		Logger.info("Saved entityStats " + stats);
    }
}
