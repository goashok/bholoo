package controllers;

import static models.EntityState.Active;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Address;
import models.Business;
import models.Category;
import models.Classified;
import models.EntityType;
import models.Stats;
import play.Logger;
import play.mvc.*;
import util.LocationPref;
import util.QueryUtil;
import util.Table;
import util.UserUtil;
import util.Table.Column;
import util.Table.Row;

@With(LocationController.class)
public class YellowPages extends Controller {
	
	@Before(priority=0)
	public static void setMainNavTab()
	{
		session.put("mainNavTab", "YellowPages");
	}

    
    public static void list(long categoryId, String categoryName, int page, boolean isParent) throws Exception
    {
    	/*LocationPref locationPref = (LocationPref) renderArgs.get("locationPref");
    	String categoryFilter = QueryUtil.categoryQueryFiler(categoryId, categoryName, EntityType.YellowPages, isParent, false, "cat.id");
    	String zipFilter = QueryUtil.zipQueryFilter(true, "addr.zip");
    	String entityStateFilter = QueryUtil.entityStateFilter(true, "b.entityState", Active);
    	String query = "select distinct b from Business as b join b.categories as cat join b.addresses as addr where " + categoryFilter + " " + zipFilter + " " + entityStateFilter + " " + businessOrderQuerySuffix();
    	Logger.info("YellowPages.list() query %s", query);
    	List<Business> businesses = Business.find(query).fetch(page, 100);
    	Table<Business> businessesTable = tabular(businesses);    	   
    	Logger.info("Finding %s within %d miles of %s for user %s", EntityType.YellowPages.name(), locationPref.radius, locationPref.city, UserUtil.userNameOrIp(request, session));
    	if(Logger.isDebugEnabled())
    	{
    		Logger.debug("Following zip codes will be considered for classifieds %s" + zipFilter);
    	}
    	*//**
    	 * Set Ratings for these businesses
    	 *//*
    	setRatings(businesses);
    	render(categoryName, businessesTable, categoryId, page, isParent);*/
    	all(categoryId, categoryName, page, isParent);
    }

    private static void setRatings(List<Business> businesses) throws Exception
    {
    	List<Stats> ratings = new ArrayList<Stats>();
    	Map<Long, Stats> ratingsByBusiness = new HashMap<Long, Stats>();
    	if(businesses.size() > 0) 
    	{
    		List<Stats> found = Stats.find("select s from Stats s where s.entityType = " + EntityType.YellowPages() + " and s.entityTypeId in " + QueryUtil.inClause("id", businesses)).fetch();
    		for(Stats stats : found)
    		{
    			ratingsByBusiness.put(stats.entityTypeId, stats);
    		}
    	}
    	for(Business business : businesses)
    	{
    		Stats stats = ratingsByBusiness.get(business.id);
    		if(stats != null)
    		{
    			business.ratings = stats.cumulativeRatings;
    		}
    	}
    }
	private static Table<Business> tabular(List<Business> businesses) {
		Table<Business> businessesTable = new Table<Business>();
    	Row<Business> row = null; //new Row<Business>();
    	//businessesTable.addRow(row);
    	for(int i=0; i<businesses.size(); i++) {
    		if(i%3 == 0) {
    			row = new Row<Business>();
    			businessesTable.addRow(row);
    		}
    		Business business = businesses.get(i);    		
    		row.addColumn(new Column<Business>(business));    		
    	}
		return businessesTable;
	}
	
	public static void alpha(long categoryId, String categoryName, int page, boolean isParent, String alphaRange) throws Exception
	{
		String query;
		String zipFilter = QueryUtil.zipQueryFilter(true, "addr.zip");
    	String entityStateFilter = QueryUtil.entityStateFilter(true, "b.entityState", Active);
    	String categoryFilter = QueryUtil.categoryQueryFiler(categoryId, categoryName, EntityType.YellowPages, isParent, false, "cat.id");
    	session.put("ypQuickTab", "alpha");
		if(alphaRange == null)
		{
			renderArgs.put("alphaRange", "all");
    		query = "select distinct b from Business as b join b.categories as cat join b.addresses as addr where " + categoryFilter + " " + zipFilter + " " + entityStateFilter + " " + businessOrderQuerySuffix();
	    	renderList(categoryId, categoryName, page, isParent, query);
		}else
		{
			//TODO: Change to do alphabetical query
			renderArgs.put("alphaRange", alphaRange);
			String[] namesLike = alphaRange.split(",");
			String namesLikeFilter = namesLikeQuery(true,"b.name", namesLike);
	    	query = "select distinct b from Business as b join b.categories as cat join b.addresses as addr where " + categoryFilter + " " + zipFilter + " " + entityStateFilter + " " + namesLikeFilter + " " + businessOrderQuerySuffix();
			renderList(categoryId, categoryName, page, isParent, query);
		}
	}
	
	public static String namesLikeQuery(boolean prefixAnd, String property, String...namesLike)
	{
		StringBuilder sb = prefixAnd ? new StringBuilder("and ( ") : new StringBuilder("");
		Iterator<String> iter = Arrays.asList(namesLike).iterator();
		while(iter.hasNext())
		{
			sb.append(property).append(" like '").append(iter.next()).append("%' ");
			if(iter.hasNext())
			{
				sb.append(" or ");
			}
		}
		sb.append(" )");
		return sb.toString();
	}
    
    public static void all(long categoryId, String categoryName, int page, boolean isParent) throws Exception
    {
    	String zipFilter = QueryUtil.zipQueryFilter(true, "addr.zip");
    	String entityStateFilter = QueryUtil.entityStateFilter(true, "b.entityState", Active);
    	String query;
    	if(categoryId == 0)
    	{
    		query = "select distinct b from Business as b join b.categories as cat join b.addresses as addr where " + zipFilter + " " + entityStateFilter + " " + businessOrderQuerySuffix();
    	}else
    	{
    		String categoryFilter = QueryUtil.categoryQueryFiler(categoryId, categoryName, EntityType.YellowPages, isParent, false, "cat.id");
    		query = "select distinct b from Business as b join b.categories as cat join b.addresses as addr where " + categoryFilter + " " + zipFilter + " " + entityStateFilter + " " + businessOrderQuerySuffix();
    	}
    	session.put("ypQuickTab", "all");
    	renderList(categoryId, categoryName, page, isParent, query);
    }

	private static void renderList(long categoryId, String categoryName, int page, boolean isParent, String query) throws Exception 
		{
		LocationPref locationPref = (LocationPref) renderArgs.get("locationPref");
		Logger.info("YellowPages.renderList() query %s", query);
    	List<Business> businesses = Business.find(query).fetch(page, 100);
    	Table<Business> businessesTable = tabular(businesses);    	   
    	Logger.info("Finding %s within %d miles of %s for user %s", EntityType.YellowPages.name(), locationPref.radius, locationPref.city, UserUtil.userNameOrIp(request, session));
    	
    	/**
    	 * Get Ratings for these businesses
    	 */
    	setRatings(businesses);
    	
    	render("YellowPages/list.html",categoryName, businessesTable, categoryId, page, isParent);
	}
    
    public static void allRated(int page) throws Exception
    {
    	rated(0,null,page,false);
    }
    
    public static void rated(long categoryId, String categoryName, int page, boolean isParent) throws Exception
    {
    	String zipFilter = QueryUtil.zipQueryFilter(true, "addr.zip");
    	String entityStateFilter = QueryUtil.entityStateFilter(true, "b.entityState", Active);
    	String query;
    	if(categoryId == 0)
    	{
    		query = "select distinct b from Business as b join b.categories as cat join b.addresses as addr where " + zipFilter + " " + entityStateFilter + " and exists (select 1 from Stats s where s.entityType = " + EntityType.YellowPages() + " and s.entityTypeId = b.id) " + businessOrderQuerySuffix();
    	}else
    	{
    		String categoryFilter = QueryUtil.categoryQueryFiler(categoryId, categoryName, EntityType.YellowPages, isParent, false, "cat.id");
    		query = "select distinct b from Business as b join b.categories as cat join b.addresses as addr where " + categoryFilter + " " + zipFilter + " " + entityStateFilter + " and exists (select 1 from Stats s where s.entityType = " + EntityType.YellowPages() + " and s.entityTypeId = b.id) " + businessOrderQuerySuffix();
    	}
    	session.put("ypQuickTab", "rated");
    	renderList(categoryId, categoryName, page, isParent, query);
    }
    
    
    
        
    
    private static String businessOrderQuerySuffix() {
		return "order by b.name asc";
	}

	public static void index() {
    	Map<String, List<Category>> subCategoryMap = new HashMap<String, List<Category>>();
    	List<Category> categories = Category.find("parentName is null and type = 'YellowPages' order by name").fetch();
    	Table<Category> categoriesTable = new Table<Category>();
    	Row<Category> row = new Row<Category>();
    	categoriesTable.addRow(row);
    	for(int i=0; i<categories.size(); i++) {
    		if(i%4 == 0) {
    			row = new Row<Category>();
    			categoriesTable.addRow(row);
    		}
    		Category category = categories.get(i);
    		row.addColumn(new Column<Category>(category));
    		List<Category> subCategories = Category.find("parentName = '" + category.name + "' and type='YellowPages' " + " order by name").fetch();
    		subCategoryMap.put(category.name, subCategories);
    	}    	
    	render(categoriesTable, subCategoryMap);
    }

}
