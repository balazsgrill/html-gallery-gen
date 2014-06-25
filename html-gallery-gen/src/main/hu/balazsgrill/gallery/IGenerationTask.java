/**
 * 
 */
package hu.balazsgrill.gallery;


/**
 * @author balazs.grill
 *
 */
public interface IGenerationTask<T extends IGenerationTask<T>> extends Comparable<T> {

	public boolean isDirty();
	
	public void commit() throws Exception;
	
}
