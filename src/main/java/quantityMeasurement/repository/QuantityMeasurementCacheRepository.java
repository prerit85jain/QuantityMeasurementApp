package quantityMeasurement.repository;

import quantityMeasurement.entity.QuantityMeasurementEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

public class QuantityMeasurementCacheRepository implements IQuantityMeasurementRepository {

    private static final Logger logger = Logger.getLogger(
            QuantityMeasurementCacheRepository.class.getName());

    public static final String FILE_NAME = "quantity_measurement_repo.ser";

    List<QuantityMeasurementEntity> quantityMeasurementEntityCache;
    private static QuantityMeasurementCacheRepository instance;

    private QuantityMeasurementCacheRepository() {
        quantityMeasurementEntityCache = new ArrayList<>();
        loadFromDisk();
        logger.info("QuantityMeasurementCacheRepository initialized.");
    }

    public static QuantityMeasurementCacheRepository getInstance() {
        if (instance == null) instance = new QuantityMeasurementCacheRepository();
        return instance;
    }

    @Override public void save(QuantityMeasurementEntity entity) {
        quantityMeasurementEntityCache.add(entity);
        saveToDisk(entity);
        logger.fine("Entity saved to cache. Total: " + quantityMeasurementEntityCache.size());
    }

    @Override public List<QuantityMeasurementEntity> getAllMeasurements() {
        return new ArrayList<>(quantityMeasurementEntityCache);
    }

    @Override public List<QuantityMeasurementEntity> getMeasurementsByOperation(String operation) {
        return quantityMeasurementEntityCache.stream()
                .filter(e -> operation.equalsIgnoreCase(e.operation))
                .collect(Collectors.toList());
    }

    @Override public List<QuantityMeasurementEntity> getMeasurementsByType(String measurementType) {
        return quantityMeasurementEntityCache.stream()
                .filter(e -> measurementType.equalsIgnoreCase(e.thisMeasurementType))
                .collect(Collectors.toList());
    }

    @Override public int getTotalCount() { return quantityMeasurementEntityCache.size(); }

    @Override public void deleteAll() {
        quantityMeasurementEntityCache.clear();
        File file = new File(FILE_NAME);
        if (file.exists()) file.delete();
        logger.info("All measurements deleted from cache repository.");
    }

    @Override public String getPoolStatistics() {
        return "CacheRepository: in-memory, " + quantityMeasurementEntityCache.size() + " entities.";
    }

    private void saveToDisk(QuantityMeasurementEntity entity) {
        try (FileOutputStream fos = new FileOutputStream(FILE_NAME, true);
             AppendableObjectOutputStream oos = new AppendableObjectOutputStream(fos)) {
            oos.writeObject(entity);
        } catch (IOException e) {
            logger.severe("Error saving to disk: " + e.getMessage());
        }
    }

    private void loadFromDisk() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;
        try (FileInputStream fis = new FileInputStream(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            while (true) {
                try {
                    quantityMeasurementEntityCache.add(
                            (QuantityMeasurementEntity) ois.readObject());
                } catch (EOFException e) { break; }
            }
            logger.info("Loaded " + quantityMeasurementEntityCache.size() + " entities from storage");
        } catch (IOException | ClassNotFoundException ex) {
            logger.severe("Error loading entities: " + ex.getMessage());
        }
    }

    public void clearAll() { deleteAll(); }

    public static void main(String[] args) {
        QuantityMeasurementCacheRepository repo = getInstance();
        logger.info("Count: " + repo.getTotalCount());
    }
}