package models;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.persistence.Entity;

import play.db.jpa.Model;
import play.libs.MimeTypes;
import play.modules.s3blobs.S3Blob;

@Entity
public class Document extends Model
{
    public String fileName;	
    public S3Blob file;
    public String comment;
    
    public Document() {
    	
    }
    
    public Document(File document) throws FileNotFoundException {
    	file = new S3Blob();
    	file.set(new FileInputStream(document), MimeTypes.getContentType(document.getName()));
    }
}
