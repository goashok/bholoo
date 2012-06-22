package controllers;

import static models.EntityState.Active;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Address;
import models.Business;
import models.Category;
import models.Classified;
import models.EntityType;
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

    public static void list() {
    	List<Business> businesses = Business.findAll();
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
        render(businessesTable);
    }
    
    public static void list2(long categoryId, String categoryName, int page, boolean isParent) throws Exception
    {
    	LocationPref locationPref = (LocationPref) renderArgs.get("locationPref");
    	String categoryFilter = QueryUtil.categoryQueryFiler(categoryId, categoryName, EntityType.YellowPages, isParent, false, "categories.categoryId");
    	String zipFilter = QueryUtil.zipQueryFilter(true, "addresses.zip");
    	String entityStateFilter = QueryUtil.entityStateFilter(true, Active);
    	List<Business> businesses = Business.find(categoryFilter + " " + zipFilter + " " + entityStateFilter + " " + businessOrderQuerySuffix()).fetch(page, 100);
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
    	Logger.info("Finding %s within %d miles of %s for user %s", EntityType.YellowPages.name(), locationPref.radius, locationPref.city, UserUtil.userNameOrIp(request, session));
    	if(Logger.isDebugEnabled())
    	{
    		Logger.debug("Following zip codes will be considered for classifieds %s" + zipFilter);
    	}
    	
    	render(categoryName, businessesTable, categoryId, page, isParent);
    }
        
    
    private static String businessOrderQuerySuffix() {
		return "order by name desc";
	}

	public static void index() {
    	Map<String, List<Category>> subCategoryMap = new HashMap<String, List<Category>>();
    	List<Category> categories = Category.find("parentName is null and type = 'yellowpages' order by name").fetch();
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
    		List<Category> subCategories = Category.find("parentName = '" + category.name + "' and type='yellowpages' " + " order by name").fetch();
    		subCategoryMap.put(category.name, subCategories);
    	}    	
    	render(categoriesTable, subCategoryMap);
    }

}
