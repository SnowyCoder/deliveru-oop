package it.unimore.s273693.deliveru.db;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * States of a delivery,
 * some of them are only available to the insured deliveries,
 * use {@link #isInsuranceRequired()} to be sure.
 */
@RequiredArgsConstructor
public enum DeliveryState {
    IN_PREPARATION  ("In preparation", false),
    IN_TRANSIT      ("In transit", false),
    RECEIVED        ("Received", false),
    FAILED          ("Failed", false),
    REFUND_REQUIRED ("Refund required", true),
    REFUND_PAID     ("Refund paid", true);

    /**
     * User-friendly name.
     *
     * @return State name
     */
    @Getter
    private final String name;

    /**
     * True only if this state can only be applied to an insured delivery.
     *
     * @return whether this state can be used only in an insured delivery
     */
    @Getter
    private final boolean insuranceRequired;

    @Override
    public String toString() {
        return name;
    }
}
