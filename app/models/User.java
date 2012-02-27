package models;

import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
import securesocial.provider.SocialUser;
 
@Entity
@Table(name="tb_user")
public class User extends Model {
 
    public String email;
    public String password;
    public String fullname;
    public boolean isAdmin;
    
    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }
    
    public User(SocialUser sUser) {
    	this.email = sUser.email;
    	this.password = sUser.password;
    	this.fullname = sUser.displayName;
    }
    
    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }
 
}