/**
 * 
 */
package hu.balazsgrill.gallery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author balazs.grill
 *
 */
public class Auxiliary {

	/**
	 * 
	 */
	private Auxiliary() {
	}
	
	public static void emit(File targetDir) throws IOException{
		saveData("folder.png", targetDir);
		saveData("gallery.css", targetDir);
		saveData("gallery.js", targetDir);
		saveData("camera-photo.png", targetDir);
	}
	
	public static InputStream getData(String data){
		return Auxiliary.class.getResourceAsStream("/hu/balazsgrill/gallery/data/"+data);
	}
	
	private static void saveData(String data, File targetDir) throws IOException{
		InputStream stream = getData(data);
		File target = new File(targetDir, data);
		FileOutputStream out = new FileOutputStream(target);
		try{
			int size = stream.available();
			byte[] buffer = new byte[Math.min(1024, size)];
			int read = 0;
			while(size > read){
				int r = stream.read(buffer);
				if (r == -1) throw new IOException("Unexpected EOF at "+read+" ("+data+")");
				out.write(buffer, 0, r);
				read += r;
			}
			stream.read(buffer);
		}finally{
			out.close();
			stream.close();
		}
	}

}
