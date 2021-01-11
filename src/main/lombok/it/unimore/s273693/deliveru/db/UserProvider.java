package it.unimore.s273693.deliveru.db;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Stores all of the registered users.
 * Supports queries by UUID and by name, is also in charge of loading and saving the data.
 */
public class UserProvider {
    private static final Logger logger = LogManager.getLogger(UserProvider.class);

    private final Map<UUID, User> usersById = new HashMap<>();
    private final Map<String, User> usersByName = new HashMap<>();

    private UserProvider() {}

    /**
     * Queries the user by its id.
     *
     * @param id the id of the user
     * @return The queried user
     */
    public Optional<User> getUserById(@NonNull UUID id) {
        return Optional.ofNullable(usersById.get(id));
    }

    /**
     * Queries the user by its name.
     *
     * @param name the id of the user
     * @return The queried user
     */
    public Optional<User> getUserByName(String name) {
        return Optional.ofNullable(usersByName.get(normalizeName(name)));
    }

    /**
     * Registers a new user.
     * If the name is already used then "false" is returned
     *
     * @param user The user to be written
     * @return true only if the procedure is successful
     */
    public boolean registerUser(User user) {
        if (this.usersByName.putIfAbsent(user.getUsername(), user) != null) return false;
        if (this.usersById.putIfAbsent(user.getId(), user) != null) throw new RuntimeException("UUID conflict");

        return true;
    }

    /**
     * Removes the user with the provided id.
     * If the id is not present nothing is done.
     *
     * @param id the id of the user to return
     * @return true only if an user has been removed
     */
    public boolean removeUser(UUID id) {
        var user = usersById.remove(id);
        if (user == null) return false;
        usersByName.remove(user.getUsername());
        return true;
    }

    /**
     * Serializes the database into a {@link SerializedDb}.
     *
     * @return the serialized version of this instance
     */
    private SerializedDb serialize() {
        var db = new SerializedDb();
        db.users = new ArrayList<>(this.usersById.values());
        db.version = 1;

        return db;
    }

    /**
     * Clears the data stored in this and loads the serialized version provided as argument.
     *
     * @param db the serialized version to load
     */
    private void deserialize(SerializedDb db) {
        this.usersById.clear();
        this.usersByName.clear();

        if (db.version != 1) {
            logger.error("Failed to load users, incompatible db version: {}", db.version);
            return;
        }

        for (var user : db.users) {
            this.usersById.put(user.getId(), user);
            this.usersByName.put(user.getUsername(), user);
        }

        logger.info("Loaded {} users", db.users.size());
    }

    /**
     * Saves the users data to the provided OutputStream.
     *
     * @param out he outputstream to use
     * @throws IOException when an error occurs while saving the data
     */
    public void save(OutputStream out) throws IOException {
        var mapper = new ObjectMapper();
        mapper.writeValue(out, this.serialize());
        logger.info("Saved {} users", this.usersById.size());
    }

    /**
     * Creates an empty {@link UserProvider}.
     *
     * @return an empty {@link UserProvider}
     */
    public static UserProvider createEmpty() {
        return new UserProvider();
    }

    /**
     * Creates a new {@link UserProvider} instance with the data found in the {@link InputStream}.
     * If the format is not recognized an empty {@link UserProvider} is returned.
     *
     * @param in The data to load
     * @return the newly created instance
     * @throws IOException When an error occurs while reading the file
     */
    public static UserProvider load(InputStream in) throws IOException {
        var res = createEmpty();
        if (in == null) return res;

        var mapper = new ObjectMapper();
        SerializedDb db;
        try {
            db = mapper.readValue(in, SerializedDb.class);
        } catch (JsonParseException | JsonMappingException e) {
            logger.error("Failed to parse JSON", e);
            return res;
        }

        res.deserialize(db);
        return res;
    }

    /**
     * Creates a new {@link UserProvider} instance with the data loaded from the provided file.
     * If the file is not found or it's impossible tor ead it an empty {@link UserProvider} is returned
     *
     * @param file The file to read from
     * @return A new {@link UserProvider} with the loaded data
     */
    public static UserProvider load(File file) {
        try {
            return load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            logger.info("Users file not found");
        } catch (IOException e) {
            logger.error("Error reading users file, using empty db", e);
        }
        return createEmpty();
    }

    /**
     * Normalizes the user name.
     *
     * @param name The name to be normalized
     * @return The normalized name
     */
    private static String normalizeName(String name) {
        return name.toLowerCase(Locale.ENGLISH);
    }

    /**
     * Helper class that will be serialized into the OutputStream.
     */
    private static class SerializedDb {
        public long version;
        public List<User> users;
    }
}
