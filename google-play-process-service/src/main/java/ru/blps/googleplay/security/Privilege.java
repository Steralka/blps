package ru.blps.googleplay.security;

public final class Privilege {
    private Privilege() {
    }

    public static final String CATALOG_READ = "PRIV_CATALOG_READ";
    public static final String CATALOG_WRITE = "PRIV_CATALOG_WRITE";

    public static final String ACCOUNT_READ_SELF = "PRIV_ACCOUNT_READ_SELF";
    public static final String ACCOUNT_WRITE_SELF = "PRIV_ACCOUNT_WRITE_SELF";
    public static final String ACCOUNT_ADMIN = "PRIV_ACCOUNT_ADMIN";

    public static final String CARD_MANAGE_SELF = "PRIV_CARD_MANAGE_SELF";
    public static final String CARD_ADMIN = "PRIV_CARD_ADMIN";

    public static final String INSTALL_SELF = "PRIV_INSTALL_SELF";
    public static final String INSTALL_ADMIN = "PRIV_INSTALL_ADMIN";
}

