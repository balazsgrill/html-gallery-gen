/**
 * 
 */
package hu.balazsgrill.gallery;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Properties;
import java.util.Map.Entry;

/**
 * @author balazs.grill
 *
 */
public class GalleryOptions {

	public String rootUrl = null;
	public String dataUrl = null;
	
	public File sourceDir = null;
	public File targetDir = null;
	public File dataDir = null;
	
	public int targetDiagonal = 1600;
	
	public int thumbDiagonal = 120;

	public void checkValidity(){
		if (rootUrl == null) throw new IllegalArgumentException("rootUrl is not set!");
		if (sourceDir == null) throw new IllegalArgumentException("sourceDir is not set!");
		if (!sourceDir.exists()) throw new IllegalArgumentException("sourceDir does not exist!");
		if (!sourceDir.isDirectory()) throw new IllegalArgumentException("sourceDir is not a directory!");
		if (targetDir == null) throw new IllegalArgumentException("targetDir is not set!");
		
		if (dataDir != null){
			if (dataUrl == null) throw new IllegalArgumentException("if dataDir is set, dataUrl must be set also!");
		}
	}
	
	private void setField(Field field, String value) throws NumberFormatException, IllegalArgumentException, IllegalAccessException{
		Class<?> fieldType = field.getType();
		
		if (int.class.equals(fieldType)){
			field.setInt(this, Integer.parseInt(value));
		}else
		if (File.class.equals(fieldType)){
			field.set(this, new File(value));
		}
		else
		if (String.class.equals(fieldType)){
			field.set(this, value);
		}
	}
	
	public void setField(String fieldName, String value){
		try {
			Field field = getClass().getField(fieldName);
			setField(field, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void readProperties(Properties props){
		for(Entry<?,?> entry : props.entrySet()){
			String key = entry.getKey()+"";
			String value = entry.getValue()+"";
			setField(key, value);
		}
	}
	
}
