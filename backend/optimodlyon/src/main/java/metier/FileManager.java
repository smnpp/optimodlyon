/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Singleton.java to edit this template
 */
package metier;

/**
 *
 * @author jassirhabba
 */
public class FileManager {
    
    private FileManager() {
    }
    
    public static FileManager getInstance() {
        return FileManagerHolder.INSTANCE;
    }
    
    private static class FileManagerHolder {

        private static final FileManager INSTANCE = new FileManager();
    }
}
