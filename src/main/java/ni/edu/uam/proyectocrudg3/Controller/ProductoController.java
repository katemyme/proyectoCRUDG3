package ni.edu.uam.proyectocrudg3.Controller;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ni.edu.uam.proyectocrudg3.Model.Categoria;
import ni.edu.uam.proyectocrudg3.Model.Marca;
import ni.edu.uam.proyectocrudg3.Model.Producto;
import ni.edu.uam.proyectocrudg3.connection.DatabaseConecction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ProductoController {

    private final ObservableList<Producto> listaProductos =
            FXCollections.observableArrayList();

    private final ObservableList<Categoria> listaCategorias =
            FXCollections.observableArrayList();

    private final ObservableList<Marca> listaMarcas =
            FXCollections.observableArrayList();

    public void initialize(){
        configurarTableView();
        configurarSeleccionTableView();
        cargarCategorias();
        cargarMarcas();
        cargarOpcionesOrden();
        cargarProductos();
    }

    @FXML
    private TextField txtCodigo;

    @FXML
    private TextField txtPrecio;

    @FXML
    private ComboBox<Categoria> cmbCategoria;

    @FXML
    private DatePicker dpFecha;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtStock;

    @FXML
    private ComboBox<Marca> cmbMarca;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TextField txtBuscar;

    @FXML
    private ComboBox<Categoria> cmbFiltroCategoria;

    @FXML
    private DatePicker dpDesde;

    @FXML
    private DatePicker dpHasta;

    @FXML
    private TextField txtPrecioMin;

    @FXML
    private TextField txtPrecioMax;

    @FXML
    private CheckBox chkConExistencias;

    @FXML
    private ComboBox<String> cmbOrdenar;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnLimpiarFiltros;

    @FXML
    private TableView<Producto> tablaProductos;

    @FXML
    private TableColumn<Producto, String> colCodigo;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colCategoria;

    @FXML
    private TableColumn<Producto, String> colMarca;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TableColumn<Producto, Integer> colStock;

    @FXML
    private TableColumn<Producto, LocalDate> colFecha;

    @FXML
    private void Nuevo_producto(ActionEvent event) {

    }

    @FXML
    private void guardar_producto(ActionEvent event) {

    }

    @FXML
    private void Actualizar_producto(ActionEvent event) {

    }

    @FXML
    private void Eliminar_producto(ActionEvent event) {

    }

    @FXML
    private void Buscar_producto(ActionEvent event) {

    }

    @FXML
    private void limpiarFiltros(ActionEvent event) {

    }

    // Estos son los métodos iniciales para empezar a trabajar

    private void configurarTableView(){
        colCodigo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCodigo()
                ));
        colNombre.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getNombre()
                )
        );
        colCategoria.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getCategoria()
                )
        );
    /*colDescripcion.setCellValueFactory(data ->
            new SimpleStringProperty(
                    data.getValue().getDescripcion()
            )
    );*/
        colMarca.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getMarca()
                )
        );
        colPrecio.setCellValueFactory(data ->
                new SimpleDoubleProperty(
                        data.getValue().getPrecio()
                ).asObject()
        );
        colStock.setCellValueFactory( data ->
                new SimpleIntegerProperty(
                        data.getValue().getStock()
                ).asObject()
        );
        colFecha.setCellValueFactory(data ->
                new SimpleObjectProperty<>(
                        data.getValue().getFechaCreacion()
                )
        );

        tablaProductos.setItems(listaProductos);
    }




    private void configurarSeleccionTableView(){
        tablaProductos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if(newValue != null){
                        cargarProductoEnFormulario(newValue);
                    }
                });
    }

    private void cargarProductoEnFormulario(Producto p){
        txtCodigo.setText(p.getCodigo());
        txtNombre.setText(p.getNombre());
        txtDescripcion.setText(p.getDescripcion());
        txtPrecio.setText(String.valueOf(p.getPrecio()));
        txtStock.setText(String.valueOf(p.getStock()));
        dpFecha.setValue(p.getFechaCreacion());
        for(Categoria categoria: listaCategorias){
            if(categoria.getIdCategoria() == p.getIdCategoria()){
                cmbCategoria.setValue(categoria);
                break;
            }
        }


        for(Marca marca: listaMarcas){
            if(marca.getIdMarca() == p.getIdMarca()){
                cmbMarca.setValue(marca);
                break;
            }
        }
    }

    private void cargarCategorias() {

        listaCategorias.clear();

        String sql = """
            SELECT
                id_categorias,
                nombre
            FROM catalogos.categorias
            WHERE estado = 1
            ORDER BY nombre
            """;

        try (
                Connection conexion =
                        DatabaseConecction.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Categoria categoria =
                        new Categoria(
                                rs.getInt("id_categorias"),
                                rs.getString("nombre")
                        );

                listaCategorias.add(categoria);
            }

            cmbCategoria.setItems(
                    FXCollections.observableArrayList(
                            listaCategorias
                    )
            );

            cmbFiltroCategoria.setItems(
                    FXCollections.observableArrayList(
                            listaCategorias
                    )
            );

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar las categorías.",
                    e
            );
        }
    }



    private void cargarMarcas() {

        listaMarcas.clear();

        String sql = """
            SELECT
                id_marca,
                nombre
            FROM catalogos.marcas
            WHERE estado = 1
            ORDER BY nombre
            """;

        try (
                Connection conexion =
                        DatabaseConecction.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                Marca marca =
                        new Marca(
                                rs.getInt("id_marca"),
                                rs.getString("nombre")
                        );

                listaMarcas.add(marca);
            }

            cmbMarca.setItems(
                    FXCollections.observableArrayList(
                            listaMarcas
                    )
            );

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar las marcas.",
                    e
            );
        }
    }


    private void cargarProductos() {

        listaProductos.clear();

        String sql = """
            SELECT
                p.id_productos,
                p.codigo,
                p.nombre,
                p.descripcion,
                p.precio,
                p.stock,
                p.fecha_creacion,
                p.id_categoria,
                c.nombre AS categoria,
                p.id_marca,
                m.nombre AS marca,
                p.estado
            FROM catalogos.productos p
            INNER JOIN catalogos.categorias c
                ON c.id_categorias = p.id_categoria
            INNER JOIN catalogos.marcas m
                ON m.id_marca = p.id_marca
            WHERE p.estado = 1
            ORDER BY p.nombre
            """;

        try (
                Connection conexion =
                        DatabaseConecction.getConnection();

                PreparedStatement ps =
                        conexion.prepareStatement(sql);

                ResultSet rs =
                        ps.executeQuery()
        ) {

            while (rs.next()) {

                listaProductos.add(
                        crearProductoDesdeResultSet(rs)
                );
            }

        } catch (SQLException e) {

            mostrarErrorBaseDatos(
                    "No se pudieron cargar los productos.",
                    e
            );
        }
    }

    private void cargarOpcionesOrden() {

        cmbOrdenar.setItems(
                FXCollections.observableArrayList(
                        "Nombre",
                        "Precio menor a mayor",
                        "Precio mayor a menor",
                        "Fecha más reciente",
                        "Fecha más antigua",
                        "Stock mayor a menor",
                        "Stock menor a mayor"
                )
        );

        cmbOrdenar.setValue("Nombre");
    }
    private void mostrarErrorBaseDatos(String mensaje, SQLException e) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Error de base de datos");
        alerta.setHeaderText(mensaje);
        alerta.setContentText(e.getMessage());
        alerta.showAndWait();
    }


    private Producto crearProductoDesdeResultSet(
            ResultSet rs
    ) throws SQLException {

        return new Producto(

                rs.getInt(
                        "id_productos"
                ),

                rs.getString(
                        "codigo"
                ),

                rs.getString(
                        "nombre"
                ),

                rs.getString(
                        "descripcion"
                ),

                rs.getDouble(
                        "precio"
                ),

                rs.getInt(
                        "stock"
                ),

                rs.getDate(
                        "fecha_creacion"
                ).toLocalDate(),

                rs.getInt(
                        "id_categoria"
                ),

                rs.getString(
                        "categoria"
                ),

                rs.getInt(
                        "id_marca"
                ),

                rs.getString(
                        "marca"
                ),

                rs.getInt(
                        "estado"
                )
        );
    }
}