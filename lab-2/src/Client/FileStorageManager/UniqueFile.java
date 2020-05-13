package Client.FileStorageManager;

import javax.swing.*;

public class UniqueFile {
    private String originalName;
    private int ID;

    public UniqueFile(String fileName, int ID){
        originalName = fileName;
        this.ID = ID;
    }

    private String generateUniqueName() {
        final int N = 20;
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        StringBuilder sb = new StringBuilder(N);
        for (int i = 0; i < N; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
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
