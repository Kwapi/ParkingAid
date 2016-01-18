package Framework;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * FileIO is a class that provides data persistence functionality. It is achieved through saving Serialized objects to internal storage as custom files.
 * These objects can later be retrieved by specifying the filename.
 * @author Michal Zak
 */
public class FileIO {

    /**
     * Serializes an object and saves it to a file
     * @param objectToSave  - Object implementing Serializable that we want to store
     * @param context   - Android Context (Activity/Application)
     * @param fileName -  name of the newly created file containing the object
     */
    public static void saveToFile(Object objectToSave, Context context, String fileName) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(objectToSave);
            objectOutputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates an object by reading it from a file
     * @param context - Android Context (Activity/Application)
     * @param fileName  - the name under which the file is stored
     * @return  retrieved Object (needs to be cast)
     */
    public static Object readFromFile(Context context, String fileName) {
        Object objectToRead = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            objectToRead = objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return objectToRead;
    }

    /**
     * Removes the object - true if successful
     * @param context - Android Context (Activity/Application)
     * @param fileName  - the name under which the file is stored
     * @return  true if successful, false if not
     */
    public static boolean deleteFile(Context context, String fileName){
        return context.deleteFile(fileName);
    }
}
