public class Token {
    int code;
    String description;
    String info;
    
    public Token(){
    	code = -1;
    	description ="";
    	info = "";
    }
    public Token(int code, String description, String info){
    	this.code = code;
    	this.description = description;
    	this.info = info;
    	
    }
}
