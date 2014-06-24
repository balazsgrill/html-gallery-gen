/**
 * 
 */
package hu.balazsgrill.gallery;

import java.io.IOException;

/**
 * @author balazs.grill
 *
 */
public interface IGenerationTask {

	public boolean isDirty();
	
	public void commit() throws IOException;
	
}
