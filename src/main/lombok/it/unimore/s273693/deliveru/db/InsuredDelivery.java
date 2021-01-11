package it.unimore.s273693.deliveru.db;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Special delivery type, this has also an insured value.
 */
@ToString
public class InsuredDelivery extends Delivery {
    /**
     * Value to return to the sender if the delivery fails.
     *
     * @return insured value
     */
    @Getter
    private final BigInteger insuredValue;

    /**
     * The JSON constructor (also accepts the state).
     *
     * @param code The code
     * @param sender The sender code
     * @param date THe insertion date
     * @param destination The destination
     * @param weight The weight in kilograms
     * @param insuredValue The insured value
     * @param state The state
     */
    @JsonCreator
    public InsuredDelivery(
            @JsonProperty("code") UUID code,
            @JsonProperty("sender") UUID sender,
            @JsonProperty("date") LocalDate date,
            @JsonProperty("destination") String destination,
            @JsonProperty("weight") double weight,
            @JsonProperty("insuredValue") BigInteger insuredValue,
            @JsonProperty("state") DeliveryState state) {
        super(code, sender, date, destination, weight, state);
        if (insuredValue.signum() < 0) {
            throw new IllegalArgumentException("Ensured value must be positive");
        }
        this.insuredValue = insuredValue;
    }

    /**
     * Main constructor.
     *
     * @param code The code
     * @param sender The sender code
     * @param date THe insertion date
     * @param destination The destination
     * @param weight The weight in kilograms
     * @param insuredValue The insured value
     */
    public InsuredDelivery(UUID code, UUID sender, LocalDate date, String destination, double weight, BigInteger insuredValue) {
        this(code, sender, date, destination, weight, insuredValue, DeliveryState.IN_PREPARATION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setState(DeliveryState newState) {
        if (this.isInFinalState()) {
            throw new IllegalStateException("Cannot reassign a state to a finalized delivery");
        }
        // Removed insurance check
        this.stateProperty.setValue(newState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @JsonIgnore
    public boolean isInFinalState() {
        var state = this.stateProperty.get();
        if (state == DeliveryState.FAILED) return false;
        if (state == DeliveryState.REFUND_PAID) return true;
        return super.isInFinalState();
    }
}
