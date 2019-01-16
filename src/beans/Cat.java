package beans;

public class Cat {
	private String id, url;
	private int myIndex; //the index of this cat in "catList" in "ResultBean" class.
	private double myScore;
	
	public Cat() {

	}

	public Cat(String id, String url, int i) {
		this.id = id;
		this.url=url;
		this.myIndex = i;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "id="+id+"; url="+url;
	}

	public int getMyIndex() {
		return myIndex;
	}

	public void setMyIndex(int myIndex) {
		this.myIndex = myIndex;
	}

	public double getMyScore() {
		return myScore;
	}

	public void setMyScore(double myScore) {
		this.myScore = myScore;
	}

}
