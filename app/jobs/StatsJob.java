package jobs;

import java.util.List;

import models.Stats;
import models.Contribution;
import models.StatsType;
import models.EntityType;
import play.Logger;
import play.jobs.Job;

public class StatsJob extends Job<Stats> {

	private EntityType entityType;
	private long entityId;
	private StatsType statsType;
	private String contributor;
	
	private double rating;
	
	public StatsJob(StatsType statsType, EntityType entityType, long entityId, String contributor) {
		this.statsType = statsType;
		this.entityType = entityType;
		this.entityId = entityId;
		this.contributor = contributor;
	}
	
	public StatsJob(StatsType statsType, EntityType entityType, long entityId, String contributor, double rating) {
		this(statsType, entityType, entityId, contributor);
		this.rating = rating;
	}
	
	public void doJob() {
		Contribution contribution = Contribution.find("contributor = '" + contributor + "' and entityStats.entityType = " + entityType.ordinal() + 
											" and entityStats.entityTypeId = " + entityId + " and statsType = " + statsType.ordinal()).first();
		if(contribution != null)
		{
			Logger.info("Contributor %s has already contributed to %s ", contributor, statsType.name());
			//Already contributed
			return;
		}
		Stats stats = Stats.find("byEntityTypeAndEntityTypeId", entityType.ordinal(), entityId).first();
		if(stats == null) {
			Logger.info("No stats found for %s with id %d", entityType.name(), entityId);
			stats = new Stats();
			stats.entityType = entityType.ordinal();
			stats.entityTypeId = entityId;
		}else {
			Logger.info("Found  entityStats " + stats);
			
		}
		contribution = new Contribution(contributor, statsType.ordinal());
		//link for id propagation
		contribution.entityStats = stats;
		stats.contributions.add(contribution);
		switch(statsType) 
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
		case Ratings: 	stats.cumulativeRatings = recalculate(rating, stats);
						stats.ratings++;
						break;
		}
		stats.save();
		Logger.info("Saved entityStats " + stats);
    }
	
	private static double recalculate(double rating, Stats stats) {
		double totalRatings = (stats.cumulativeRatings * stats.ratings) + rating;
		long totalNumRatings = stats.ratings + 1;
		double recalculated = (double) totalRatings/totalNumRatings;
		return Math.round(recalculated *2)/2d; 
	}
}
