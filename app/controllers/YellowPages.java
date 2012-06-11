package controllers;

import java.util.List;

import models.Address;
import models.Business;
import models.Category;
import play.mvc.*;
import util.Table;
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

}
