package util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import models.Category;
import models.City;
import models.EntityState;
import models.EntityType;
import controllers.LocationController;

public class QueryUtil {

	public static String zipQueryFilter(boolean prefixAnd, String zipProperty) throws Exception {
		LocationPref locationPref = LocationController.getLocation();
		Set<City> neighbors = controllers.City.neighborsByZip(locationPref.zip,
				locationPref.radius);
		StringBuilder inZipClause = prefixAnd ? new StringBuilder(
				"and " + zipProperty + " in (") : new StringBuilder("zip in (");
		Iterator<City> nIter = neighbors.iterator();
		while (nIter.hasNext()) {
			inZipClause.append("'").append(nIter.next().zip).append("'");
			if (nIter.hasNext())
				inZipClause.append(",");
		}
		inZipClause.append(")");
		return inZipClause.toString();
	}
	
	 public static String categoryQueryFiler(long categoryId, String categoryName, EntityType entityType, boolean isParent, boolean prefixAnd, String categoryProperty) throws Exception
	    {
	    	StringBuilder categoryFilter = prefixAnd ? new StringBuilder("and  ") : new StringBuilder();
	    	categoryFilter.append(categoryProperty).append(" ");
	    	
	    	if(isParent) {
	    		List<Category> childCategories = Category.find("byParentNameAndType", categoryName, entityType.name()).fetch();
	    		StringBuilder inClause = new StringBuilder("(");
	    		Iterator<Category> iter = childCategories.iterator();
	    		while(iter.hasNext()) {
	    			inClause.append(iter.next().id);
	    			if(iter.hasNext()) {
	    				inClause.append(",");
	    			}
	    		}
	    		inClause.append(")");
	    		categoryFilter.append(" in ").append(inClause.toString());
	    	}else {
	    		categoryFilter.append(" = " + categoryId);
	    	}
	    	
			return categoryFilter.toString();
	    }
	 
	 public static String entityStateFilter(boolean prefixAnd, String entityStateProperty, EntityState... entityStates)
	    {
	    	if(entityStates.length == 0) return "";
	    	StringBuilder entityStateFilter = prefixAnd ? new StringBuilder("and " + entityStateProperty) : new StringBuilder(entityStateProperty);
	    	if(entityStates.length > 1)
	    	{
	    		entityStateFilter.append(" in (");
	    		Iterator<EntityState> esIter = Arrays.asList(entityStates).iterator();
	    		while(esIter.hasNext())
	    		{
	    			entityStateFilter.append(esIter.next().ordinal());
	    			if(esIter.hasNext())
	    			{
	    				entityStateFilter.append(", ");
	    			}
	    		}
	    		entityStateFilter.append(")");
	    	}else {
	    		entityStateFilter.append(" = ").append(entityStates[0].ordinal());
	    	}
	    	return entityStateFilter.toString();
	    }
	    

}
