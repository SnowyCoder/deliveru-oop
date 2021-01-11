package it.unimore.s273693.deliveru.db;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.unimore.s273693.deliveru.serialize.LocalDateDeserializer;
import it.unimore.s273693.deliveru.serialize.LocalDateSerializer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.UUID;

/**
 * The delivery data
 *
 * <p>This is mostly a data class, but there's also some logic,
 * The only property that can change is the state, and so it's wrapped in an
 * ObjectProperty (for better JavaFX interaction).
 * It's also checked in the {@link #setState(DeliveryState)} method to check if
 * the operation is permitted.
 *
 * <p>When serialized in JSON the property "type" will be used to check what type
 * of Delivery should be used (to disambiguate from {@link InsuredDelivery})
 */
// JSON Polymorphic disambiguation
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Delivery.class, name = "standard"),
        @JsonSubTypes.Type(value = InsuredDelivery.class, name = "insured"),
})
@Getter
@ToString
public class Delivery {
    /**
     * Universal Unique ID of the delivery.
     *
     * @return The code
     */
    private final UUID code;

    /**
     * Sender's UUID.
     *
     * @return The sender's code
     */
    private final UUID sender;

    /**
     * Insertion date.
     *
     * @return The insertion date
     */
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private final LocalDate date;

    /**
     * Destination.
     *
     * @return The destination
     */
    private final String destination;

    /**
     * Weight in kilograms.
     *
     * @return The weight in kilograms
     */
    private final double weight;

    /**
     * State of the delivery (in a JavaFX Property).
     *
     * @return The state property
     */
    @JsonIgnore
    @Accessors(fluent = true)// stateProperty() (without get)
    protected final ObjectProperty<DeliveryState> stateProperty;

    /**
     * A read only property that indicates whether the delivery is in a final state.
     *
     * @see #isInFinalState()
     * @return is in final state property
     */
    @JsonIgnore
    @Accessors(fluent = true)// isInFinalStateProperty() (without get)
    private final ReadOnlyBooleanProperty isInFinalStateProperty;

    /**
     * JSON Constructor (also accepts state).
     *
     * @param code The code
     * @param sender The sender
     * @param date The date
     * @param destination The destination
     * @param weight The weight
     * @param state The state
     */
    @JsonCreator
    public Delivery(
            @NonNull @JsonProperty("code") UUID code,
            @NonNull @JsonProperty("sender") UUID sender,
            @NonNull @JsonProperty("date") LocalDate date,
            @NonNull @JsonProperty("destination") String destination,
            @JsonProperty("weight") double weight,
            @NonNull @JsonProperty("state") DeliveryState state) {
        if (weight < 0) throw new IllegalArgumentException("Weight cannot be negative");
        this.code = code;
        this.sender = sender;
        this.date = date;
        this.destination = destination;
        this.weight = weight;
        this.stateProperty = new SimpleObjectProperty<>(DeliveryState.IN_PREPARATION);
        this.setState(state);
        var isFinalStateProperty = new SimpleBooleanProperty();
        isFinalStateProperty.bind(Bindings.createBooleanBinding(this::isInFinalState, this.stateProperty));
        this.isInFinalStateProperty = isFinalStateProperty;
    }

    public Delivery(UUID code, UUID sender, LocalDate date, String dest, double weight) {
        this(code, sender, date, dest, weight, DeliveryState.IN_PREPARATION);
    }

    /**
     * Returns true if the current state is final (and so it should not be changed).
     * This is not encoded in the {@link DeliveryState} enum because the same state
     * can be final or not depending on the delivery type
     *
     * @return true if the state is final
     */
    @JsonIgnore
    public boolean isInFinalState() {
        var state = this.stateProperty.get();
        return state == DeliveryState.RECEIVED || state == DeliveryState.FAILED;
    }

    /**
     * Gets the current state.
     *
     * @return the current state
     */
    public DeliveryState getState() {
        return this.stateProperty.get();
    }

    /**
     * Sets the current state.
     * Additional checks are made to ensure that
     * <ul>
     *  <li>The state is supported (you can't add an insured state to a non-insured delivery)</li>
     *  <li>The delivery is not in a final state</li>
     * </ul>
     *
     * @param newState The new state to set
     */
    public void setState(DeliveryState newState) {
        if (newState.isInsuranceRequired()) {
            throw new IllegalArgumentException("Cannot assign an insurance state to a normal delivery");
        }
        if (this.isInFinalState()) {
            throw new IllegalStateException("Cannot reassign a state to a finalized delivery");
        }
        this.stateProperty.setValue(newState);
    }


    @Override
    public int hashCode() {
        // A delivery is identified by its UUID
        return code.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (!(other instanceof Delivery)) return false;
        var o = (Delivery) other;
        return this.code.equals(o.code);
    }
}
