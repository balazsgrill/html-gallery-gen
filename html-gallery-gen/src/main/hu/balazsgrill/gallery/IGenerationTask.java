/**
 * 
 */
package hu.balazsgrill.gallery;

import java.io.IOException;

/**
 * @author balazs.grill
 *
 */
public interface IGenerationTask<T extends IGenerationTask<T>> extends Comparable<T> {

	public boolean isDirty();
	
	public void commit() throws IOException;
	
}
