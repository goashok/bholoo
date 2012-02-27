package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name="tb_state")
public class State extends Model {
    public String code;
    public String name;
    
    @ManyToOne
    public Country country;
}
