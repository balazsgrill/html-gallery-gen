/**
 * 
 */
package hu.balazsgrill.gallery;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author balazs.grill
 *
 */
public class Template {

	public static final String INDEX_HTML = "index.tpl";
	public static final String LINK_GALLERY = "gallery-link.tpl";
	public static final String LINK_IMAGE = "image-link.tpl";
	
	private static final Map<String, Template> templates = new HashMap<String, Template>();
	
	public static Template getTemplate(String templateName) throws IOException{
		Template tpl = templates.get(templateName);
		if (tpl == null){
			tpl = new Template(templateName);
			templates.put(templateName, tpl);
		}
		return tpl;
	}
	
	private final String[] data; 
	
	/**
	 * @throws IOException 
	 * 
	 */
	public Template(String templateName) throws IOException {
		InputStream stream = getClass().getResourceAsStream("/hu/balazsgrill/gallery/templates/"+templateName);
		if (stream == null) throw new IllegalArgumentException("Template not found: "+templateName);
		byte[] data = null;
		try{
			data = new byte[stream.available()];
			stream.read(data);
		}finally{
			stream.close();
		}
		String template = new String(data, StandardCharsets.UTF_8);
		List<String> items = new LinkedList<>();
		
		int index = 0;
		while(index != -1){
			int start = template.indexOf("{{", index);
			if (start > index){
				items.add(template.substring(index, start));
			}
			
			if (start != -1){
				int end = template.indexOf("}}", start+2);
				index = end+2;
				items.add(template.substring(start, end+2));
			}else{
				items.add(template.substring(index));
				index = -1;
			}
		}
		
		this.data = items.toArray(new String[items.size()]);
	}

	public void emitToFile(Map<String, String> values, File target) throws IOException{
		FileOutputStream stream = null;
		try{
			stream = new FileOutputStream(target);
			String data = emit(values);
			stream.write(data.getBytes(StandardCharsets.UTF_8));
			stream.flush();
		}finally{
			if (stream != null){
				stream.close();
			}
		}
	}
	
	public String emit(Map<String, String> values){
		StringBuilder sb = new StringBuilder();
		
		for(String element : data){
			if (element.startsWith("{{")){
				String key = element.substring(2, element.length()-2);
				String value = values.get(key);
				if (value != null){
					sb.append(value);
				}else{
					sb.append(element);
				}
			}else{
				sb.append(element);
			}
		}
		
		return sb.toString();
	}
	
}
