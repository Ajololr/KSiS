package Client.FileStorageManager;

public class UniqueFile {
    private String originalName;
    private int ID;

    public UniqueFile(String fileName, int ID){
        originalName = fileName;
        this.ID = ID;
    }

    public UniqueFile(String fileName){
        originalName = fileName;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
