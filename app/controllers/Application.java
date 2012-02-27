package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.*;
import util.Table;
import util.Table.Column;
import util.Table.Row;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	Map<String, List<Category>> subCategoryMap = new HashMap<String, List<Category>>();
    	List<Category> categories = Category.find("parentName is null and type = 'classifieds' order by name").fetch();
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
    		List<Category> subCategories = Category.find("parentName = '" + category.name + "' order by name").fetch();
    		subCategoryMap.put(category.name, subCategories);
    	}
    	
        render(categoriesTable, subCategoryMap);
    }
    
    public static void listClassifieds(long categoryId, String categoryName, int page, boolean isParent) {
    	List<Classified> classifieds;
    	if(isParent) {
    		List<Category> childCategories = Category.find("byParentName", categoryName).fetch();
    		StringBuilder inClause = new StringBuilder("(");
    		Iterator<Category> iter = childCategories.iterator();
    		while(iter.hasNext()) {
    			inClause.append(iter.next().id);
    			if(iter.hasNext()) {
    				inClause.append(",");
    			}
    		}
    		inClause.append(")");
    		classifieds =  Classified.find("categoryId in " + inClause.toString() + " order by postedAt desc").fetch(page, 100);
    	}else {
    		classifieds =  Classified.find("categoryId = " + categoryId + " order by postedAt desc").fetch(page, 100);
    	}
    		
    	render(categoryName, classifieds);
    }
    
    public static void newClassified() {
    	String randomID = Codec.UUID();
    	List<Category> categories = Category.find("parentName is not null and type = 'classifieds' order by name").fetch();
    	render(randomID, categories);
    }
    
    public static void postClassified(
            @Required(message="Title is required") String title, 
            @Required(message="Description is required") String description, 
            @Required(message="City is required") String city,
            @Required(message="Please type the code") String code,
            @Required(message="Category is required") long categoryId,            
            double price,
            String phone,
            String randomID) 
    {
        validation.equals(
            code, Cache.get(randomID)
        ).message("Invalid code. Please type it again");
        if(validation.hasErrors()) {
        	 //User user = new User(SecureSocial.getCurrentUser());
        	List<Category> categories = Category.find("parentName is not null and type = 'classifieds' order by name").fetch();
            render("Application/newClassified.html",  randomID, categories);
        }
        Classified classified = new Classified(title, description, price, city, categoryId, phone, "testUser");
        classified.save();
        flash.success("Thanks for posting %s, Following posting has been submitted", "testUser");
        Cache.delete(randomID);
        viewClassified(classified.id, title, description, city, phone, price);
    }
    
    
    public static void viewClassified(long id, String title, String description, String city, String phone, double price) {
    	render(title, description, city, phone, price);
    }
    
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#000000");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }

    public static void testbootstrap() {
    	render();
    }
}