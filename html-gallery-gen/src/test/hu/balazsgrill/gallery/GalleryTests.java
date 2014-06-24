package hu.balazsgrill.gallery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
		assertEquals(3, root.getImages().size());
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
		opts.rootUrl = "/gallery/";
		opts.sourceDir = srcDir;
		opts.targetDir = trgDir;
		
		GalleryData root = new GalleryData(opts);
		root.collectData();
		
		TaskExecutor executor = new TaskExecutor(root);
		executor.execute(true);
		
	}
	
}
