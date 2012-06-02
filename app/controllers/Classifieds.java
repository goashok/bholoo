package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import models.Category;
import models.City;
import models.Classified;
import models.Document;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.data.binding.As;
import play.data.binding.types.FileArrayBinder;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.libs.Codec;
import play.libs.MimeTypes;
import play.modules.s3blobs.S3Blob;
import play.mvc.*;
import play.vfs.VirtualFile;
import util.CityParser;
import util.IPUtil;
import util.LocationPref;
import util.Table;
import util.Table.Column;
import util.Table.Row;

@With({LocationController.class})
public class Classifieds extends Controller {

	private static final String IMAGE_UPLOAD_ROOT_DIR_PROPERTY = "classifieds.image.root.dir";
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
	
   /* public static void list(long categoryId, String categoryName, int page, boolean isParent) throws Exception {
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
    		classifieds =  Classified.find("categoryId = " + categoryId  +  " and zip in " + inZipClause.toString() + " order by postedAt desc").fetch(page, 100);
    	}    	    
    	render(categoryName, classifieds, locationPref);
    }
    */
    public static void list(long categoryId, String categoryName, int page, boolean isParent) throws Exception {
    	LocationPref locationPref = LocationController.getLocation();
    	String categoryFilter = categoryQueryFiler(categoryId, categoryName, isParent);
    	String zipFilter = zipQueryFilter();
    	List<Classified> classifieds = Classified.find(categoryFilter + " and zip in " + zipFilter + " " + classifiedOrderQuerySuffix()).fetch(page, 100);
    	
    	Logger.info("Finding classifieds within %d miles of %s for user %s", locationPref.radius, locationPref.city, session.get("username"));
    	Logger.debug("Following zip codes will be considered for classifieds %s" + zipFilter);
    	
    	render(categoryName, classifieds, locationPref, categoryId, page, isParent);
    }
        
    
    private static String categoryQueryFiler(long categoryId, String categoryName, boolean isParent) throws Exception {
    	StringBuilder categoryFilter = new StringBuilder("categoryId ");
    	
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
    		categoryFilter.append(" in ").append(inClause.toString());
    	}else {
    		categoryFilter.append(" = " + categoryId);
    	}
    	
		return categoryFilter.toString();
    }
    
    private static String classifiedOrderQuerySuffix() {
    	return "order by postedAt desc";
    }
    
    private static String zipQueryFilter() throws Exception {
    	LocationPref locationPref = LocationController.getLocation();
    	List<City> neighbors = controllers.City.neighborsByZip(locationPref.zip, locationPref.radius);
    	StringBuilder inZipClause = new StringBuilder("(");
    	Iterator<City> nIter = neighbors.iterator();
    	while(nIter.hasNext()) {
    		inZipClause.append("'").append(nIter.next().zip).append("'");
    		if(nIter.hasNext()) inZipClause.append(",");
    	}
    	inZipClause.append(")");
    	return inZipClause.toString();
    }
    
    private static String priceQueryFilter(double priceFrom, double priceTo, boolean prefixAnd) {
    	StringBuilder priceFilter = new StringBuilder();
    	if(priceFrom >0 || priceTo > 0) {
    		 if(prefixAnd) { 
    			 priceFilter.append(" and "); 
    		 }
    		priceFilter.append("price between " + priceFrom + " and " + (priceTo > 0 ? priceTo : Double.MAX_VALUE));
    	}
    	System.out.println("priceFilter " + priceFilter.toString());
    	return priceFilter.toString();
    }
    
    private static String cityQueryFilter(String city, boolean prefixAnd) {
    	String cityFilter =  (city != null && city.trim().length() > 0) ? "city = '" + city + "'" : "";
    	return prefixAnd && cityFilter.length() > 0 ? " and " + cityFilter : cityFilter;
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
	    LocationPref locationPref = (LocationPref) Cache.get(IPUtil.clientIp(request));
    	render("Classifieds/edit.html", randomID, categories, locationPref);
    }
    
    public static void post2(
    		@Valid Classified classified, 
    		@Required(message="Please type the code") String code, 
    		String randomID, 
    		@As(binder = FileArrayBinder.class) Object boundFiles) throws FileNotFoundException 
    	{
    	System.out.println("classified id: " + classified.id);
    	File[] images = (File[]) boundFiles;
        validation.equals(
            code, Cache.get(randomID)
        ).message("Invalid code. Please type it again");
        validation.max(images.length, 4).message("Cannot upload more than 4 files");
        if(validation.hasErrors()) {
        	params.flash();
        	validation.keep();
        	List<Category> categories = Category.find("parentName is not null and type = 'classifieds' order by name").fetch();
        	LocationPref locationPref = LocationController.getLocation();
            render("Classifieds/edit.html",  randomID, categories, locationPref);
        }
        models.City classifiedCity = CityParser.cityByNameState.get(classified.city.replaceAll(", ", ""));
        
        if(classified.id != null ) {
        	Logger.info("Modifying existing classified %d", classified.id);
        	Classified existingClassified;
        	existingClassified = Classified.findById(classified.id);
        	existingClassified.city = classified.city;
        	existingClassified.zip = classifiedCity.zip;
        	existingClassified.description = classified.description;
        	existingClassified.phone = classified.phone;
        	existingClassified.price = classified.price;
        	existingClassified.title = classified.title;
        	if(images != null && images.length > 0) {
        		Logger.info("boundFiles size %d", ((File[])boundFiles).length);
        		existingClassified.images.clear();
        		addImages(images, existingClassified);
        	}
        	existingClassified.save();
        	
        }else {
        	classified.zip = classifiedCity.zip;
        	classified.postedAt = new Date(System.currentTimeMillis());
        	classified.postedBy = session.get("username");
        	addImages(images, classified);
        	classified.save();
        }
        flash.success("Thanks for posting %s, Following posting has been submitted", session.get("username"));
        Cache.delete(randomID);
        view(classified.id);
    }
    
    private static void addImages(File[] images, Classified classified) throws FileNotFoundException {
	        for(int i=0; i<images.length; i++) {
	             classified.addImage(images[i]);
	        }
    }
    
    
    public static void view(long id) {
    	Classified classified = Classified.findById(id);
    	LocationPref locationPref = LocationController.getLocation();
    	if(locationPref == null) {
    		Logger.warn("locationPref is null");
    	}
    	render(classified, locationPref);
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
    
    public static void getImage(long id, int i) {
    	Classified classified = Classified.findById(id);    	
    	S3Blob image = classified.getImages().get(i-1).file;
    	if(image == null) {
    		Logger.info("image " + i + " is null");
    	}
    	response.setContentTypeIfNotSet(image.type());
    	InputStream in = image.get();
    	if(in == null) {
    		Logger.info("inputstream is null");
    	}
    	renderBinary(in);
    }
    
    public static void search(long categoryId, String categoryName, int page, boolean isParent, double priceFrom, double priceTo, String city) throws Exception {
    	System.out.println(categoryId + ", " + categoryName + ", " + page + ", " + isParent + ", " + priceFrom + ", " + priceTo + ", " + city);
    	LocationPref locationPref = LocationController.getLocation();
    	String categoryFilter = categoryQueryFiler(categoryId, categoryName, isParent);
    	String zipFilter = zipQueryFilter();
    	boolean PREFIX_AND = true;
    	String priceFilter = priceQueryFilter(priceFrom, priceTo, PREFIX_AND);
    	String cityFilter = cityQueryFilter(city, PREFIX_AND);    	
    	List<Classified> classifieds = Classified.find(categoryFilter + " and zip in " + zipFilter + " " + priceFilter + " " + cityFilter + " " +  classifiedOrderQuerySuffix()).fetch(page, 100);
    	
    	Logger.info("Finding classifieds within %d miles of %s for user %s", locationPref.radius, locationPref.city, session.get("username"));
    	Logger.debug("Following zip codes will be considered for classifieds %s" + zipFilter);
    	
    	render("Classifieds/list.html", categoryName, classifieds, locationPref, categoryId, page, isParent);
    }
}
