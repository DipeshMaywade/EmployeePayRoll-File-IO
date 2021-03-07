import java.io.File;

public class FileUtils {
    public static boolean deleteFiles(File contentsToDelete){
        File[] allCounts = contentsToDelete.listFiles();
        if (allCounts != null){
            for (File file : allCounts) {
                deleteFiles(file);
            }
        }
        return contentsToDelete.delete();
    }
}
