public class Tag {
    String name;
    int dir;
    int referencedDir;

    public Tag(String name){
        this.name = name;
        this.dir = -1;
        this.referencedDir = -1;
    }
    public Tag(String name, int dir, int referencedDir){
        this.name = name;
        this.dir = dir;
        this.referencedDir = referencedDir;
    }
}
