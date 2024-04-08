module com.couchbase.lite.java.colors {
    requires javafx.controls;
    requires javafx.fxml;

    requires couchbase.lite.java.ee;
    requires annotation;

    opens com.couchbase.lite.java.colors to javafx.fxml;

    exports com.couchbase.lite.java.colors;
}