/**
 * 
 */
package hu.balazsgrill.gallery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author balazs.grill
 *
 */
public class GeneratorMain {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args){
		GalleryOptions options = new GalleryOptions();
		
		boolean force = false;
		
		for(String arg : args){
			if ("-f".equals(arg)){
				force = true;
			}else{
				int s = arg.indexOf('=');
				if (s != -1){
					//Property
					String key = arg.substring(0, s);
					String value = arg.substring(s+1);
					options.setField(key, value);
				}else{
					//Properties file
					File file = new File(arg);
					Properties props = new Properties();
					try(InputStream stream = new FileInputStream(file)){
						props.load(stream);
						options.readProperties(props);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		options.checkValidity();
		
		GalleryData root = new GalleryData(options);
		root.collectData();
		
		TaskExecutor executor = new TaskExecutor(root);
		executor.execute(force);
		
	}

}
