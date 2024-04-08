package com.couchbase.lite.java.colors;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class ColorsApplication extends Application {

    public static void main(String[] args) { launch(); }

    @Override
    public void start(Stage stage) throws IOException {
        try {
            URL res = getClass().getResource("");

            System.out.println("===== URL: " + res);

            File file = file = new File(res.getPath());
            for (File f: file.listFiles()) {
                System.out.println("FILE: " + f);
            }


            res = new URL(
                "file:/Users/blakemeike/Desktop/VS-samples/color/resources/src/main/java/com/couchbase/lite/java/colors/main-view.fxml");
            stage.setTitle("Colors!");
            stage.setScene(new Scene(new FXMLLoader(res).load()));
            stage.show();
        }
        catch (Exception e) {
            System.out.println("FAIL");
            e.printStackTrace();
        }
    }
}