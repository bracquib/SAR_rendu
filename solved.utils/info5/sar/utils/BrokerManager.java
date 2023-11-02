package info5.sar.utils;

import java.util.HashMap;

/**
 * This generic class represents a broker manager that can manage brokers of a specific type.
 *
 * @param <T> The type of brokers this manager handles.
 */
public class BrokerManager<T> {
    /**
     * Static HashMap to store instances of BrokerManager for different broker types.
     * The key is the class of the broker type, and the value is the corresponding BrokerManager instance.
     */
    private static final HashMap<Class<?>, BrokerManager<?>> instances = new HashMap<>();

    /**
     * Instance-specific HashMap to store brokers of type T.
     * The key is the name of the broker, and the value is the broker instance.
     */
    private HashMap<String, T> brokers = new HashMap<>();

    /**
     * Private constructor to prevent direct instantiation.
     * Initialize the brokers HashMap.
     */
    private BrokerManager() {
        brokers = new HashMap<>();
    }

    /**
     * Gets or creates an instance of BrokerManager for a specific broker type.
     *
     * @param <T>         The type of brokers this manager handles.
     * @param brokerClass The class representing the type of brokers.
     * @return An instance of BrokerManager for the specified broker type.
     */
    public static synchronized <T> BrokerManager<T> getInstance(Class<T> brokerClass) {
        // Try to get an existing instance from the static HashMap based on the brokerClass
        BrokerManager<T> instance = (BrokerManager<T>) instances.get(brokerClass);

        // If no instance exists for the specified brokerClass, create a new one
        if (instance == null) {
            instance = new BrokerManager<>();

            // Store the new instance in the static HashMap with brokerClass as the key
            instances.put(brokerClass, instance);
        }

        // Return the instance, either existing or newly created
        return instance;
    }

    /**
     * Registers a broker with a specified name.
     *
     * @param name   The name to associate with the broker.
     * @param broker The broker instance to register.
     * @return The previously registered broker with the same name, if any.
     */
    public T registerBroker(String name, T broker) {
        // Add the broker to the brokers HashMap with the specified name
        return brokers.put(name, broker);
    }

    /**
     * Retrieves a broker by its name.
     *
     * @param name The name of the broker to retrieve.
     * @return The broker associated with the specified name, or null if not found.
     */
    public T getBroker(String name) {
        // Retrieve and return the broker associated with the specified name
        return brokers.get(name);
    }
}
