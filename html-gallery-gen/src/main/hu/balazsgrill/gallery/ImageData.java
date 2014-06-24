/**
 * 
 */
package hu.balazsgrill.gallery;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * @author balazs.grill
 *
 */
public class ImageData implements IGenerationTask{

	private static final String[] IMG_EXTS = new String[]{".jpg", ".jpeg", ".png", ".tiff", ".tif"};
	
	public static boolean isImage(File file){
		if (file.isFile()){
			String name = file.getName().toLowerCase();
			for(String ext : IMG_EXTS){
				if (name.endsWith(ext)){
					return true;
				}
			}
		}
		return false;
	}
	
	private final File sourceFile;
	private final File targetFile;
	private final File thumbFile; 
	private final GalleryOptions options;
	
	public File getTargetFile() {
		return targetFile;
	}
	
	public File getThumbFile() {
		return thumbFile;
	}
	
	/**
	 * 
	 */
	public ImageData(GalleryData parent, File f) {
		this.sourceFile = f;
		this.targetFile = new File(parent.getTargetDir(), f.getName());
		this.thumbFile = new File(parent.getTargetDir(), "thumb_"+f.getName());
		this.options = parent.getOptions();
	}

	@Override
	public boolean isDirty(){
		long srcMod = sourceFile.lastModified();
		long trgMod = targetFile.lastModified();
		long tmbMod = thumbFile.lastModified();
		
		return (srcMod > trgMod) || (srcMod > tmbMod);
	}

	
	public static Point rescale(Point size, int diagonal){
		double srcDiagonal = size.distance(0, 0);
		double scale = diagonal/srcDiagonal;
		
		return new Point(
				(int)Math.round(size.x*scale), 
				(int)Math.round(size.y*scale));
	}
	
	public static BufferedImage rescale(BufferedImage source, Point size){
		BufferedImage scaledImage = new BufferedImage(size.x, size.y, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		graphics2D.drawImage(source, 0, 0, size.x, size.y, null);
		graphics2D.dispose();
		return scaledImage;
	}
	
	@Override
	public void commit() throws IOException{
		BufferedImage source = ImageIO.read(sourceFile);
		Point size = new Point(source.getWidth(), source.getHeight());
		Point targetSize = rescale(size, options.targetDiagonal);
		Point thumbSize = rescale(size, options.thumbDiagonal);
		
		BufferedImage target = rescale(source, targetSize);
		BufferedImage thumb = rescale(source, thumbSize);
		
		ImageIO.write(target, "jpg", targetFile);
		ImageIO.write(thumb, "jpg", thumbFile);
	}
}
