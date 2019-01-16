package beans;

public class CatMashBean {

	private CatPair catPair;
	
	public CatMashBean() {
		
	}

	public void generateRandomCats(ResultBean resultBean) {
		catPair=resultBean.getRandomCatPair();
	}
	
	public String getFirstCatId() {
		return catPair.getCat1().getId();
	}
	
	public String getFirstCatUrl() {
		return catPair.getCat1().getUrl();
	}
	
	public String getSecondCatId() {
		return catPair.getCat2().getId();
	}
	
	public String getSecondCatUrl() {
		return catPair.getCat2().getUrl();
	}
	
}
