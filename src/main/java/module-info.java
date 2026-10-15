module ni.edu.uam.proyectocrudg3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;


    opens ni.edu.uam.proyectocrudg3 to javafx.fxml;
    exports ni.edu.uam.proyectocrudg3;
    exports ni.edu.uam.proyectocrudg3.Aplication;
    opens ni.edu.uam.proyectocrudg3.Aplication to javafx.fxml;
    exports ni.edu.uam.proyectocrudg3.Controller;
    opens ni.edu.uam.proyectocrudg3.Controller to javafx.fxml;
    exports ni.edu.uam.proyectocrudg3.Model;
    opens ni.edu.uam.proyectocrudg3.Model to javafx.base;
}