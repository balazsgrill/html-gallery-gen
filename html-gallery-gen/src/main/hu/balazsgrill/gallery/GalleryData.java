/**
 * 
 */
package hu.balazsgrill.gallery;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author balazs.grill
 *
 */
public class GalleryData implements IGenerationTask<GalleryData>{

	private final File sourceDir;
	private final File targetDir;
	
	private final GalleryData parent;
	private final String name;
	
	private final List<GalleryData> subGalleries = new LinkedList<>();
	private final List<ImageData> images = new LinkedList<>();
	
	private final GalleryOptions options;
	
	@Override
	public String toString() {
		return (parent != null ? parent.toString() : "") + "/" + name +"/";
	}
	
	/**
	 * 
	 */
	public GalleryData(GalleryOptions options) {
		this.parent = null;
		this.name = "Gallery";
		this.options = options;
		this.sourceDir = options.sourceDir;
		this.targetDir = options.targetDir;
	}
	
	private File getIndexHtml(){
		return new File(targetDir, "index.html");
	}
	
	private static String asciizeString(String value){
		String decomposed = Normalizer.normalize(value, Form.NFD);
	    // removing diacritics
	    String removed = decomposed.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		StringBuilder sb = new StringBuilder();
		for(char c : removed.toCharArray()){
			if (Character.isWhitespace(c)){
				sb.append("_");
			}else{
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	public GalleryData(GalleryData parent, String name) throws MalformedURLException {
		this.parent = parent;
		this.name = name;
		this.options = parent.options;
		this.sourceDir = new File(parent.sourceDir, name);
		this.targetDir = new File(parent.targetDir, asciizeString(name));
	}
	
	public List<GalleryData> getSubGalleries() {
		return subGalleries;
	}
	
	public List<ImageData> getImages() {
		return images;
	}
	
	public GalleryOptions getOptions() {
		return options;
	}

	public void collectData(){
		subGalleries.clear();
		images.clear();
		
		for(File f : sourceDir.listFiles()){
			/* Ignore hidden files */
			if (!f.getName().startsWith(".")){
				if (f.isDirectory()){
					try {
						subGalleries.add(new GalleryData(this, f.getName()));
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (ImageData.isImage(f)){
					images.add(new ImageData(this, f));
				}
			}
		}
		
		for(GalleryData subg : subGalleries){
			subg.collectData();
		}
		
		Collections.sort(subGalleries);
		Collections.sort(images);
	}
	
	public File getTargetDir() {
		return targetDir;
	}
	
	@Override
	public boolean isDirty(){
		if (!targetDir.exists()) return true;
		
		long indexTime = getIndexHtml().lastModified();
		long srcTime = sourceDir.lastModified();
		if (srcTime > indexTime) return true;
		
		return false;
	}

	@Override
	public void commit() throws IOException {
		targetDir.mkdirs();
		
		if (parent == null){
			/* Put utility files next to root gallery */
			Auxiliary.emit(targetDir);
		}
		
		Map<String, String> indexParams = new HashMap<>();
		indexParams.put("gallery.css", options.rootUrl+"gallery.css");
		indexParams.put("gallery.js", options.rootUrl+"gallery.js");
		indexParams.put("favicon", options.rootUrl+"camera-photo.png");
		indexParams.put("name", name);
		
		StringBuilder imageData = new StringBuilder();
		imageData.append("[\n");
		for(ImageData id : images){
			imageData.append("\t{image: \"");imageData.append(id.getTargetFile().getName());imageData.append("\", origin: \"");
			imageData.append(id.getTargetFile().getName());imageData.append("\"},\n");
		}
		imageData.append("]");
		indexParams.put("images-data", imageData.toString());
		
		StringBuilder links = new StringBuilder();
		if (parent != null){
			/* Link to parent */
			Map<String, String> ps = new HashMap<>();
			ps.put("link", "..");
			ps.put("name", "..");
			ps.put("folder", options.rootUrl+"folder.png");
			links.append(Template.getTemplate(Template.LINK_GALLERY).emit(ps));
		}
		for(GalleryData subg : subGalleries){
			Map<String, String> ps = new HashMap<>();
			ps.put("link", subg.name);
			ps.put("name", subg.name);
			ps.put("folder", options.rootUrl+"folder.png");
			links.append(Template.getTemplate(Template.LINK_GALLERY).emit(ps));
		}
		for(int i=0;i<images.size();i++){
			ImageData imd = images.get(i);
			Map<String, String> ps = new HashMap<>();
			ps.put("image", imd.getTargetFile().getName());
			ps.put("thumb", imd.getThumbFile().getName());
			ps.put("name", imd.getTargetFile().getName());
			ps.put("index", i+"");
			links.append(Template.getTemplate(Template.LINK_IMAGE).emit(ps));
		}
		indexParams.put("image-list", links.toString());
		
		Template.getTemplate(Template.INDEX_HTML).emitToFile(indexParams, getIndexHtml());
	}

	@Override
	public int compareTo(GalleryData o) {
		String name = this.name;
		String oname = o.name;
		return name.compareTo(oname);
	}
}
