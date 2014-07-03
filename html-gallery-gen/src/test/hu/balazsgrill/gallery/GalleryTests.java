package hu.balazsgrill.gallery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

public class GalleryTests {

	File workDir;
	
	@Before
	public void setUp() throws IOException{
		workDir = new File(".").getCanonicalFile();
	}
	
	@Test
	public void loadTemplate() throws IOException {
		Template t = Template.getTemplate(Template.INDEX_HTML);
		String result = t.emit(Collections.<String, String>emptyMap());
		assertTrue(result.length() > 0);
	}

	@Test
	public void assertLocation(){
		assertEquals(workDir.getName(), "html-gallery-gen");
	}
	
	@Test
	public void readSource() throws IOException{
		File srcDir = new File(workDir,"/../html-gallery-test/src").getCanonicalFile();
		File trgDir = new File(workDir,"/../html-gallery-test/target").getCanonicalFile();
		
		GalleryOptions opts = new GalleryOptions();
		opts.rootUrl = "/gallery/";
		opts.sourceDir = srcDir;
		opts.targetDir = trgDir;
		
		GalleryData root = new GalleryData(opts);
		root.collectData();
		
		assertEquals(1, root.getSubGalleries().size());
		assertEquals(4, root.getImages().size());
		assertEquals(3, root.getSubGalleries().get(0).getImages().size());
		
	}
	
	@Test
	public void testTemplate() throws IOException{
		Template testtemplate = Template.getTemplate("test.tpl");
		Map<String, String> params = new HashMap<String, String>();
		params.put("param1", "value1");
		params.put("param2", "value2");
		String result = testtemplate.emit(params);
		assertEquals("test1value1test2value2test3", result);
	}
	
	@Test
	public void generate() throws IOException{
		File srcDir = new File(workDir,"/../html-gallery-test/src").getCanonicalFile();
		File trgDir = new File(workDir,"/../html-gallery-test/target").getCanonicalFile();
		
		GalleryOptions opts = new GalleryOptions();
		opts.rootUrl = "/gallery-test/";
		opts.sourceDir = srcDir;
		opts.targetDir = trgDir;
		opts.dataDir = new File(trgDir, "data");
		opts.dataUrl = "/gallery-test/data/";
		
		GalleryData root = new GalleryData(opts);
		root.collectData();
		
		TaskExecutor executor = new TaskExecutor(root);
		executor.execute(true);
		
	}
	
	@Test
	public void rotate() throws Exception{
		InputStream folderIS = Auxiliary.getData("folder.png");
		BufferedImage im = ImageIO.read(folderIS);
		folderIS.close();
		
		BufferedImage result = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = result.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		
		AffineTransform at = AffineTransform.getTranslateInstance(250, 250);
		at.scale(1.5, 1.5);
		
		graphics2D.drawImage(im, at, null);
//		at.translate(0, im.getWidth());
//		at.quadrantRotate(-1);
		at.translate(im.getWidth(), im.getHeight());
		at.quadrantRotate(2);	
		
		graphics2D.drawImage(im, at, null);
		
		graphics2D.dispose();
		File trgDir = new File(workDir,"/../html-gallery-test/target").getCanonicalFile();
		File resultFile = new File(trgDir, "result.png");
		ImageIO.write(result, "png", resultFile);
	}
	
}
