package util;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import play.db.jpa.Model;

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
	 
	 
	 //Useful when there is a two step query where query the entities first . and then we run second query to either filter or get
	 //other associated entities.
	 public static <T extends Model> String inClause(String propertyName, Collection<T> entities) throws Exception
	 {
		 if(entities.size() == 0)
		 {
			 throw new IllegalArgumentException("Cannot create in clause with empty collection");
		 }
		 StringBuilder sb = new StringBuilder("( ");
		 Iterator<T> eIter  = entities.iterator();
		 while(eIter.hasNext())
		 {
			 Object entity = eIter.next();
			 Field f = entity.getClass().getField(propertyName);
			 Object val = f.get(entity);
			 if(Number.class.isAssignableFrom(val.getClass())) 
			 {
				 sb.append(val.toString());
			 }else if(Timestamp.class.isAssignableFrom(val.getClass()))
			 {
				 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
				 sb.append("'").append(formatter.format((Date)entity)).append("'");
			 }else if(Date.class.isAssignableFrom(val.getClass()))
			 {
				 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				 sb.append("'").append(formatter.format((Date)entity)).append("'");
			 }else
			 {
				 sb.append("'").append(val.toString()).append("'");
			 }
			 if(eIter.hasNext())
			 {
				 sb.append(", ");
			 }
		 }
		 sb.append(" )");
		 return sb.toString();
	 }

}
