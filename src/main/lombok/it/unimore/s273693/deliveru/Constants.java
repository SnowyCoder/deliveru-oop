package it.unimore.s273693.deliveru;

/**
 * App constants.
 * this class contains App wise constants like the app name, author and admin credentials.
 */
public final class Constants {
    /**
     * Application name.
     * This will be used in the data and settings paths.
     */
    public static final String APP_NAME = "DeliverU";

    /**
     * Application author.
     * This will be used in the data and settings paths.
     */
    public static final String APP_AUTHOR = "lorenzo_rossi";

    /**
     * Administrator username.
     */
    public static final String ADMIN_USERNAME = "admin";

    /**
     * Administrator password.
     */
    public static final String ADMIN_PASSWORD = "password";

    /**
     * Default settings, this object is immutable (that's why it's along the app constants).
     */
    public static final AppSettings DEFAULT_SETTINGS = new AppSettings(2, true, 6, 0.2);

    // You can't instantiate a singleton (without some reflective black magic of course)
    private Constants() {}
}
