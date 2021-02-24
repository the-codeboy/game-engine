package ml.codeboy.engine.Saving;

import java.io.*;
import java.util.HashMap;

public class SaveFile {
    private String name;
    private File file;
    private HashMap<String,Object>data=new HashMap<>();

    private SaveFile(String name,String path){
        this.name=name;
        file=new File(path+name);
        if(file.exists()){
            try {
                FileInputStream stream=new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            file.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
