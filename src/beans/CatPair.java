package beans;

public class CatPair {
	private Cat c1, c2;
	
	public CatPair() {
		
	}

	public CatPair(Cat c1, Cat c2) {
		this.c1 = c1;
		this.c2 = c2;
	}

	public Cat getCat1() {
		return c1;
	}

	public void setCat1(Cat c1) {
		this.c1 = c1;
	}

	public Cat getCat2() {
		return c2;
	}

	public void setCatd2(Cat c2) {
		this.c2 = c2;
	}

}
