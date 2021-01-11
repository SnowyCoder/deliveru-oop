package it.unimore.s273693.deliveru.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import java.util.UUID;

/**
 * Class that contains user-related data.
 */
@Data
public class User {
    /**
     * JSON/Full Constructor.
     * Accepts all parameters (also UUID), in most cases you want to use {@link User#User(String, String, String)}}
     *
     * @param id The UUID
     * @param username The username
     * @param password The encoded password
     * @param address The address
     */
    @JsonCreator
    public User(
            @NonNull @JsonProperty("id") UUID id,
            @NonNull @JsonProperty("username") String username,
            @NonNull @JsonProperty("password") String password,
            @NonNull @JsonProperty("address") String address) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.address = address;
    }

    /**
     * Programmer-friendly constructor.
     *
     * @param username The username
     * @param password The encoded password
     * @param address The address
     */
    public User(String username, String password, String address) {
        this(UUID.randomUUID(), username, password, address);
    }

    /**
     * User ID (randomly generated).
     *
     * @return ID
     */
    private final UUID id;

    /**
     * User name.
     *
     * @return name
     */
    private final String username;

    /**
     * User encoded password.
     *
     * @return Encoded password
     */
    private final String password;

    /**
     * User address.
     *
     * @return Address
     */
    private final String address;
}
