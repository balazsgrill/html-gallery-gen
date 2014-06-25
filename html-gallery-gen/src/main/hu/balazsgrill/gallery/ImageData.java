/**
 * 
 */
package hu.balazsgrill.gallery;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

/**
 * @author balazs.grill
 *
 */
public class ImageData implements IGenerationTask<ImageData>{
	
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
	
	@Override
	public String toString() {
		return sourceFile.getName();
	}
	
	public ImageOrientation getOrientation() throws ImageProcessingException, IOException{
		Metadata metadata = ImageMetadataReader.readMetadata(sourceFile);
		// obtain the Exif directory
		ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);

		// query the tag's value
		Integer orientation = directory.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
		if (orientation != null){
			switch(orientation.intValue()){
			case 1:
				return ImageOrientation.Normal;
			case 8:
				return ImageOrientation.Rotate90;
			case 3:
				return ImageOrientation.Rotate180;
			case 6:
				return ImageOrientation.Rottate270;
			}
		}
		return ImageOrientation.Normal;
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
	
	public static BufferedImage rescale(BufferedImage source, Point size, ImageOrientation orientation){
		int x = orientation.isVertical() ? size.y : size.x;
		int y = orientation.isVertical() ? size.x : size.y;
		BufferedImage scaledImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		double sx = ((double)size.x)/((double)source.getWidth());
		double sy = ((double)size.y)/((double)source.getHeight());
		
		AffineTransform transform = AffineTransform.getScaleInstance(sx, sy);
		
		switch(orientation){
		case Rotate180:
			transform.quadrantRotate(2, ((double)x)/2, ((double)y)/2);
			break;
		case Rotate90:
			transform.quadrantRotate(1, ((double)x)/2, ((double)y)/2);
			break;
		case Rottate270:
			transform.quadrantRotate(-1, ((double)x)/2, ((double)y)/2);
			break;
		case Normal:
		default:
			break;
		
		}
		
		graphics2D.drawImage(source, transform, null);
		graphics2D.dispose();
		return scaledImage;
	}
	
	@Override
	public void commit() throws Exception{
		BufferedImage source = ImageIO.read(sourceFile);
		Point size = new Point(source.getWidth(), source.getHeight());
		Point targetSize = rescale(size, options.targetDiagonal);
		Point thumbSize = rescale(size, options.thumbDiagonal);
		
		ImageOrientation orientation = getOrientation();
		BufferedImage target = rescale(source, targetSize, orientation);
		BufferedImage thumb = rescale(source, thumbSize, orientation);
		
		ImageIO.write(target, "jpg", targetFile);
		ImageIO.write(thumb, "jpg", thumbFile);
	}

	@Override
	public int compareTo(ImageData o) {
		String name = toString();
		String oname = o.toString();
		return name.compareTo(oname);
	}
}
