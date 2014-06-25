/**
 * 
 */
package hu.balazsgrill.gallery;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author balazs.grill
 *
 */
public class TaskExecutor {

	private final List<IGenerationTask<?>> tasks;
	
	/**
	 * 
	 */
	public TaskExecutor(GalleryData data) {
		tasks = new LinkedList<>();
		collect(data);
	}
	
	private void collect(GalleryData data){
		tasks.add(data);
		tasks.addAll(data.getImages());
		for(GalleryData sg : data.getSubGalleries()){
			collect(sg);
		}
	}

	public void execute(boolean force){
		for(IGenerationTask<?> task : tasks){
			if (force || task.isDirty()){
				System.out.println(task.toString());
				try {
					task.commit();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
