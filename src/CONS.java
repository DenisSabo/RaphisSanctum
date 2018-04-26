public interface CONS {
    public static final char ENTER = '\n';
    public static final int EOF = -1;

    public static boolean checkFileEnding(String path, String expectedEnding){
        String extension = "";

        int i = path.lastIndexOf('.');
        if (i > 0) {
            extension = path.substring(i+1);
        }
        if(expectedEnding.equals(extension)) return true;
        else return false;
    }

    // Gets filename without ending
    public static String getFileName(String filenameWithEnding){
        String filename = "";

        int i = filenameWithEnding.lastIndexOf('.');
        if (i > 0) {
            filename = filenameWithEnding.substring(0, i);
            return filename;
        }
        return null;
    }
}
