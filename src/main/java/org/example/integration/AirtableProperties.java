package org.example.integration;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

class AirtableProperties {

    static final ResourceBundle resourceBundle = PropertyResourceBundle.getBundle("airtable");

    public static String tablesUrl() {
        return resourceBundle.getString("airtable.url.tables");
    }
    public static String tableUrl() {
        return resourceBundle.getString("airtable.url.table");
    }
    public static String token() {
        return resourceBundle.getString("airtable.token");
    }
}
