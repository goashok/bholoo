package models;

import javax.persistence.*;
 
import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.*;
 
@Entity
@Table(name="tb_user")
public class User extends Model {
 	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Required(message="Email is required")
	@Email(message="Invalid email")
    public String email;
	
	@Required(message="Password is required")
    public String password;
	@Transient
	public String passwordCheck;
	@Required(message="Name is required")
    public String fullname;	
	public String phone;
    public boolean isAdmin;
    
    public User() {
    	
    }
    
    public User(String email, String password, String fullname) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
    }
    
   /* public User(SocialUser sUser) {
    	this.email = sUser.email;
    	this.password = sUser.password;
    	this.fullname = sUser.displayName;
    }*/
    
    public static User connect(String email, String password) {
        return find("byEmailAndPassword", email, password).first();
    }
    
    public static User findByEmail(String email) {
    	return find("byEmail", email).first();
    }

	@Override
	public String toString() {
		return "User [email=" + email + ", password=" + password
				+ ", passwordCheck=" + passwordCheck + ", fullname=" + fullname
				+ ", phone=" + phone + "]";
	}
 
    
}