package compilador;

public class Tag {
	String name;
	int dir;
	
	public Tag(String name){
		this.name = name;
		this.dir = -1;
	}
	public Tag(String name, int dir){
		this.name = name;
		this.dir = dir;
	}

}
