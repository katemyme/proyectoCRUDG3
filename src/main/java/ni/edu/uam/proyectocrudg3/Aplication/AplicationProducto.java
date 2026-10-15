package ni.edu.uam.proyectocrudg3.Aplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AplicationProducto extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AplicationProducto.class.getResource("/ni/edu/uam/proyectocrudg3/producto-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load()); // Usa el tamaño definido en el FXML (1120 x 720).
        stage.setTitle("Gestión de productos");
        stage.setScene(scene);
        stage.show();
    }
}
