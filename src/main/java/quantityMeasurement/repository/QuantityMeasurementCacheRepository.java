package quantityMeasurement.repository;

import quantityMeasurement.entity.QuantityMeasurementEntity;

import java.io.*;
import java.util.List;

class AppendableObjectOutputStream extends ObjectOutputStream {
    public AppendableObjectOutputStream(OutputStream out) throws IOException
    {
        super(out);
    }

    @Override
    protected void writeStreamHeader() throws IOException {
        // Don't write header when appending to existing file
        // Only write header if file is new/empty
        File file = new File(QuantityMeasurementCacheRepository.FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            super.writeStreamHeader();
        } else {
            reset(); // Just reset instead of writing header
        }
    }
}

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository{
    // In-memory cache to store QuantityMeasurementEntity objects for quick access into the file system.
    public static final String FILE_NAME = "quantity_measurement_repo.ser";

    // Holds the cached QuantityMeasurementEntity objects in memory for quick access
    List<QuantityMeasurementEntity> quantityMeasurementEntityCache;

    // Singleton instance of the repository
    private static QuantityMeasurementCacheRepository instance;

    // Private constructor to prevent instantiation from outside the class
    private QuantityMeasurementCacheRepository() {
        // Initialize the in-memory cache
        quantityMeasurementEntityCache = new java.util.ArrayList<>();
        // Load any existing data from disk
        loadFromDisk();
    }

    // Get the singleton instance of the QuantityMeasurementCacheRepository.
    public static QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) {
            instance = new QuantityMeasurementCacheRepository();
        }
        return instance;
    }

    //Adds the entity to the in-memory cache and persists it to disk.
    @Override
    public void save(QuantityMeasurementEntity entity) {
        quantityMeasurementEntityCache.add(entity);
        saveToDisk(entity);
    }

    // Retrieves all QuantityMeasurementEntity instances from the cache.
    @Override
    public List<QuantityMeasurementEntity> getAllMeasurements() {
        return quantityMeasurementEntityCache;
    }

    private void saveToDisk(QuantityMeasurementEntity entity) {
        // Append the new entity to the existing file without overwriting previous data
        try (
                FileOutputStream fos = new FileOutputStream(FILE_NAME, true);
                AppendableObjectOutputStream oos = new AppendableObjectOutputStream(fos)
        ) {
            oos.writeObject(entity);
        } catch (IOException e) {
            System.err.println("Error saving entity: " + e.getMessage());
        }
    }

    // Method to load the in-memory cache from disk when the repository is initialized
    private void loadFromDisk() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }
        try (
                FileInputStream fis = new FileInputStream(FILE_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            while (true) {
                try {
                    QuantityMeasurementEntity entity = (QuantityMeasurementEntity) ois.readObject();
                    quantityMeasurementEntityCache.add(entity);
                } catch (EOFException e) {
                    // End of file reached
                    break;
                }
            }
            System.out.println("Loaded " + quantityMeasurementEntityCache.size() +
                    " quantity measurement entities from storage");
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println(
                    "Error loading quantity measurement entities: " + ex.getMessage()
            );
        }
    }

    // Utility method for tests — clear in-memory cache and delete file
    public void clearAll() {
        quantityMeasurementEntityCache.clear();
        File file = new File(FILE_NAME);
        if (file.exists()) file.delete();
    }

    // Main method for testing purposes
    public static void main(String[] args) {
        QuantityMeasurementCacheRepository repo = QuantityMeasurementCacheRepository.getInstance();
        System.out.println("Repository instance: " + repo);
        System.out.println("Measurements count:  " + repo.getAllMeasurements().size());
    }
}
