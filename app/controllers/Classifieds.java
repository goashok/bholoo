package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;


import models.Category;
import models.City;
import models.Classified;
import play.Logger;
import play.cache.Cache;
import play.data.binding.As;
import play.data.binding.types.FileArrayBinder;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.libs.Codec;
import play.mvc.*;
import util.CityParser;
import util.LocationPref;
import util.Table;
import util.Table.Column;
import util.Table.Row;

@With({LocationController.class})
public class Classifieds extends Controller {

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
    	LocationPref locationPref = LocationController.getLocation();   
    	render(categoriesTable, subCategoryMap, locationPref);
    }

	@Before(only="enter")
	public static void auth() throws Throwable {
		//put redirect url in flash for secure module to redirect to.
		flash.put("url", "/classifieds/enter");
		if(session.get("username") == null) { Secure.login(); }
	}
	
    public static void list(long categoryId, String categoryName, int page, boolean isParent) throws Exception {
    	List<Classified> classifieds;
    	LocationPref locationPref = LocationController.getLocation();
    	List<City> neighbors = controllers.City.neighborsByZip(locationPref.zip, locationPref.radius);
    	StringBuilder inZipClause = new StringBuilder("(");
    	Iterator<City> nIter = neighbors.iterator();
    	while(nIter.hasNext()) {
    		inZipClause.append("'").append(nIter.next().zip).append("'");
    		if(nIter.hasNext()) inZipClause.append(",");
    	}
    	inZipClause.append(")");
    	
    	Logger.info("Finding classifieds within %d miles of %s for user %s", locationPref.radius, locationPref.city, session.get("username"));
    	Logger.debug("Following zip codes will be considered for classifieds %s" + inZipClause.toString());
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
    		classifieds =  Classified.find("categoryId in " + inClause.toString()  + " and zip in " + inZipClause.toString() + " order by postedAt desc").fetch(page, 100);
    	}else {
    		classifieds =  Classified.find("categoryId = " + categoryId + " order by postedAt desc").fetch(page, 100);
    	}    	    
    	render(categoryName, classifieds, locationPref);
    }
    
    public static void mylist() {
    	LocationPref locationPref = LocationController.getLocation();
    	System.out.println("Finding mylist for user " + session.get("username"));
    	List<Classified> classifieds = Classified.find("byPostedBy", session.get("username")).fetch();
    	System.out.println("mylist() Found size = " + classifieds.size());
    	render(classifieds, locationPref);
    }
        
    public static void enter() {
    	String randomID = Codec.UUID();
    	List<Category> categories = Category.find("parentName is not null and type = 'classifieds' order by name").fetch();
	    LocationPref locationPref = (LocationPref) Cache.get(request.remoteAddress);
    	render("Classifieds/edit.html", randomID, categories, locationPref);
    }
    
    public static void post(
    		long id,
            @Required(message="Title is required") String title, 
            @Required(message="Description is required") String description, 
            @Required(message="City is required") String city,            
            @Phone(message="Invalid phone number") String phone,
            @Required(message="Please type the code") String code,
            @Required(message="Category is required") long categoryId, 
            @As(binder = FileArrayBinder.class) Object boundFiles,
            double price,
            String randomID) 
    {
    	System.out.println("classified id: " + id);
    	File[] files = (File[]) boundFiles;
    	//System.out.println("File Sizes" + files.length);
        validation.equals(
            code, Cache.get(randomID)
        ).message("Invalid code. Please type it again");
       // validation.max(files.length, 4).message("Cannot upload more than 4 files");
        if(validation.hasErrors()) {
        	params.flash();
        	 //User user = new User(SecureSocial.getCurrentUser());
        	List<Category> categories = Category.find("parentName is not null and type = 'classifieds' order by name").fetch();
        	LocationPref locationPref = LocationController.getLocation();
            render("Classifieds/edit.html",  randomID, categories, locationPref);
        }
        models.City classifiedCity = CityParser.cityByNameState.get(city.replaceAll(", ", ""));
        Classified classified; 
        if(id != 0) {
        	classified = Classified.findById(id);        	
        	classified.city = city;
        	classified.zip = classifiedCity.zip;
        	classified.description = description;
        	classified.phone = phone;
        	classified.price = price;
        	classified.title = title;
        	classified.save();
        }else {
        	classified = new Classified(title, description, price, city, classifiedCity.zip, categoryId, phone, session.get("username"));
        	classified.save();
        }
           
        try {
        	int i=1;
	        for (File file : files) { 
	            FileInputStream is = new FileInputStream(file); 
	            String dirPath = "/temp/bholoodata/" + classified.id ;
	            
	            File directory = new File(dirPath);
	            directory.mkdir();
	            	            
	            String original =  i+file.getName().substring(file.getName().indexOf('.'));
	            
	            FileOutputStream fos =  new FileOutputStream(new File(directory, original));
	            IOUtils.copy(is,fos); 
	            fos.close();
	            i++;
	        }  
        }catch(Exception e) {
        	Logger.error(e, "Unable to save images for classified id=%s" + classified.id);
        }
        flash.success("Thanks for posting %s, Following posting has been submitted", "testUser");
        Cache.delete(randomID);
        view(classified.id, title, description, city, phone, price);
    }
    
    
    public static void view(long id, String title, String description, String city, String phone, double price) {
    	LocationPref locationPref = LocationController.getLocation();
    	render(title, description, city, phone, price, locationPref);
    }
    
    public static void edit(long id) {
    	String randomID = Codec.UUID();
    	LocationPref locationPref = LocationController.getLocation();
    	List<Category> categories = Category.findAllSubcategories();
    	Classified classified = Classified.findById(id);
    	render(randomID, classified, categories, locationPref);
    }
    
    public static void delete(long id) {
    	Classified classified = Classified.findById(id);
    	if(classified != null) {
    	classified.delete();
    	}
    	mylist();
    }
}
