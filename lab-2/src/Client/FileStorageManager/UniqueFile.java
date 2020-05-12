package Client.FileStorageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UniqueFile {
    private String originalName;
    private String uniqueName;
    private byte[] fileData;

    public UniqueFile(String fileName) throws IOException {
        originalName = fileName;
        uniqueName = generateUniqueName();
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

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }
}
