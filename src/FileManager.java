import java.io.*;
import java.util.ArrayList;

// FileManager.java
class FileManager {
    public static void saveDrawing(ArrayList<Shape> shapes, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(shapes);
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Shape> loadDrawing(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (ArrayList<Shape>) ois.readObject();
        }
    }
}
