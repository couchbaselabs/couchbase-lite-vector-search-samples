package com.couchbase.lite.java.colors;

import javafx.fxml.FXML;
import javafx.scene.control.Label;


public class ColorsController {
    @FXML
    private Label searchText;

    @FXML
    protected void onSearch() {
        searchText.setText("Searching...");
    }
}