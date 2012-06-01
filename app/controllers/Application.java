package controllers;

import play.*;
import play.cache.Cache;
import play.data.binding.As;
import play.data.binding.types.FileArrayBinder;
import play.data.validation.Phone;
import play.data.validation.Required;
import play.libs.Codec;
import play.libs.Images;
import play.mvc.*;
import play.vfs.VirtualFile;
import util.Table;
import util.Table.Column;
import util.Table.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

import org.apache.commons.io.IOUtils;

import models.*;

public class Application extends Controller {

    public static void index() {  
    	Logger.info("Headers => \n" + request.headers);
    	Classifieds.index();
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
    
    public static void testbootstrapjavascript() {
    	render();
    }
    
}