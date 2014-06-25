package hu.balazsgrill.gallery;

public enum ImageOrientation {

	Normal,
	Rotate90,
	Rotate180,
	Rottate270;
	
	public boolean isVertical(){
		return this.equals(Rotate90) || this.equals(Rottate270);
	}
	
}
