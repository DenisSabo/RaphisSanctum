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
}
