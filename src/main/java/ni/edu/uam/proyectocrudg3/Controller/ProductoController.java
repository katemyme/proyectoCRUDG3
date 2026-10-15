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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    // Botón "Nuevo" del FXML: prepara el formulario para registrar otro producto.
    @FXML
    private void Nuevo_producto(ActionEvent event) {
        limpiarFormulario(); // Deja todos los campos del formulario vacíos.
    }

    // Botón "Guardar" del FXML: registra un producto nuevo (Create).
    @FXML
    private void guardar_producto(ActionEvent event) {
        guardarProducto(); // Ejecuta el INSERT del CRUD.
    }

    // Botón "Actualizar" del FXML: modifica el producto seleccionado (Update).
    @FXML
    private void Actualizar_producto(ActionEvent event) {
        actualizarProducto(); // Ejecuta el UPDATE del CRUD.
    }

    // Botón "Eliminar" del FXML: desactiva el producto seleccionado (Delete).
    @FXML
    private void Eliminar_producto(ActionEvent event) {
        eliminarProducto(); // Ejecuta la eliminación lógica (estado = 0).
    }

    // Botón "Buscar" del FXML: aplica los filtros de búsqueda.
    @FXML
    private void Buscar_producto(ActionEvent event) {
        buscarProductos(); // Ejecuta la búsqueda parametrizada.
    }

    // Botón "Limpiar filtros" del FXML: quita los filtros y muestra todos los productos.
    @FXML
    private void limpiarFiltros(ActionEvent event) {
        txtBuscar.clear();                  // Borra el texto de búsqueda.
        cmbFiltroCategoria.setValue(null);  // Quita la categoría del filtro.
        dpDesde.setValue(null);             // Quita la fecha inicial.
        dpHasta.setValue(null);             // Quita la fecha final.
        txtPrecioMin.clear();               // Borra el precio mínimo.
        txtPrecioMax.clear();               // Borra el precio máximo.
        chkConExistencias.setSelected(false); // Desmarca "solo con existencias".
        cmbOrdenar.setValue("Nombre");      // Regresa al orden por defecto.
        cargarProductos();                  // Vuelve a cargar todos los productos activos.
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

    // Muestra un mensaje al usuario (advertencia, información, etc.).
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);   // Crea la ventana de alerta del tipo indicado.
        alerta.setTitle(titulo);          // Título de la ventana.
        alerta.setHeaderText(null);       // Sin encabezado, solo el mensaje.
        alerta.setContentText(mensaje);   // Texto que verá el usuario.
        alerta.showAndWait();             // Muestra la alerta y espera a que la cierre.
    }

    // La tabla tiene UNIQUE en codigo y en nombre; el mensaje del error dice cuál se violó.
    private String mensajeDuplicado(SQLException e) {
        if (e.getMessage() != null && e.getMessage().contains("productos_nombre_key")) {
            return "Ya existe un producto con ese nombre."; // Se repitió el nombre.
        }
        return "El código ingresado ya existe.";             // Se repitió el código.
    }

    // Revisa que el formulario tenga datos válidos antes de guardar o actualizar.
    private boolean validarFormulario() {
        // El código es obligatorio.
        if (txtCodigo.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dato requerido", "Ingrese el código del producto.");
            txtCodigo.requestFocus(); // Lleva el cursor al campo con el problema.
            return false;             // El formulario no es válido.
        }

        // El nombre es obligatorio.
        if (txtNombre.getText().isBlank()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dato requerido", "Ingrese el nombre del producto.");
            txtNombre.requestFocus(); // Lleva el cursor al nombre.
            return false;             // El formulario no es válido.
        }

        try {
            // Intenta convertir el precio a número; si no es número lanza NumberFormatException.
            BigDecimal precio = new BigDecimal(txtPrecio.getText().trim());
            // El precio debe ser mayor que cero (la tabla tiene CHECK precio > 0).
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Precio incorrecto", "El precio debe ser mayor que cero.");
                txtPrecio.requestFocus(); // Lleva el cursor al precio.
                return false;             // El formulario no es válido.
            }
        } catch (NumberFormatException e) { // El texto del precio no era un número.
            mostrarAlerta(Alert.AlertType.WARNING, "Precio incorrecto", "El precio debe ser numérico.");
            txtPrecio.requestFocus(); // Lleva el cursor al precio.
            return false;             // El formulario no es válido.
        }

        try {
            // Intenta convertir el stock a entero.
            int stock = Integer.parseInt(txtStock.getText().trim());
            // El stock no puede ser negativo.
            if (stock < 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Stock incorrecto", "El stock no puede ser negativo.");
                txtStock.requestFocus(); // Lleva el cursor al stock.
                return false;            // El formulario no es válido.
            }
        } catch (NumberFormatException e) { // El texto del stock no era un entero.
            mostrarAlerta(Alert.AlertType.WARNING, "Stock incorrecto", "El stock debe ser un número entero.");
            txtStock.requestFocus(); // Lleva el cursor al stock.
            return false;            // El formulario no es válido.
        }

        // La fecha de creación es obligatoria.
        if (dpFecha.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dato requerido", "Seleccione la fecha de creación.");
            dpFecha.requestFocus(); // Lleva el cursor a la fecha.
            return false;           // El formulario no es válido.
        }

        // Se debe elegir una categoría.
        if (cmbCategoria.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dato requerido", "Seleccione una categoría.");
            cmbCategoria.requestFocus(); // Lleva el cursor a la categoría.
            return false;                // El formulario no es válido.
        }

        // Se debe elegir una marca.
        if (cmbMarca.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Dato requerido", "Seleccione una marca.");
            cmbMarca.requestFocus(); // Lleva el cursor a la marca.
            return false;            // El formulario no es válido.
        }

        return true; // Todos los datos son válidos.
    }

    /*
     * idExcluir permite ignorar el producto que se está actualizando.
     * Para un registro nuevo se envía null.
     */
    private boolean existeCodigo(String codigo, Integer idExcluir) {
        // Busca si otro producto ya usa ese código.
        String sql = """
            SELECT 1                                         -- solo interesa saber si existe
            FROM catalogos.productos                         -- tabla de productos
            WHERE codigo = ?                                 -- mismo código
              AND (?::integer IS NULL OR id_productos <> ?)  -- excepto el producto que se edita
            """;

        try (
                // Abre la conexión con PostgreSQL.
                Connection conexion =
                        DatabaseConecction.getConnection();

                // Prepara la consulta con parámetros.
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {
            ps.setString(1, codigo);                    // Parámetro 1: código a buscar.
            ps.setObject(2, idExcluir, Types.INTEGER);  // Parámetro 2: id a excluir (puede ser null).
            ps.setObject(3, idExcluir, Types.INTEGER);  // Parámetro 3: el mismo id para comparar.

            // Ejecuta la consulta.
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true si encontró al menos una fila, es decir, el código ya existe.
            }

        } catch (SQLException e) { // Error al consultar la base de datos.
            mostrarErrorBaseDatos(
                    "No se pudo verificar el código.",
                    e
            );
            return false; // PostgreSQL validará el UNIQUE al guardar.
        }
    }

    // Deja el formulario vacío y quita la selección de la tabla.
    private void limpiarFormulario() {
        txtCodigo.clear();          // Borra el código.
        txtNombre.clear();          // Borra el nombre.
        txtDescripcion.clear();     // Borra la descripción.
        txtPrecio.clear();          // Borra el precio.
        txtStock.clear();           // Borra el stock.
        dpFecha.setValue(null);     // Quita la fecha.
        cmbCategoria.setValue(null); // Quita la categoría.
        cmbMarca.setValue(null);    // Quita la marca.
        tablaProductos.getSelectionModel().clearSelection(); // Ningún producto queda seleccionado.
        txtCodigo.requestFocus();   // Pone el cursor en el código para escribir de nuevo.
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

    /*
     * CREATE del CRUD: inserta un producto nuevo en catalogos.productos.
     */
    @FXML
    private void guardarProducto() {
        // Si algún dato del formulario es inválido, no se guarda.
        if (!validarFormulario()) {
            return; // Sale del método sin hacer el INSERT.
        }



        // Toma el código escrito y le quita los espacios de los extremos.
        String codigo =
                txtCodigo.getText().trim();



        /*
         * Validación preventiva de UNIQUE.
         */
        // Se envía null porque es un producto nuevo y no hay id que excluir.
        if (existeCodigo(codigo, null)) {
            // Avisa que el código ya está registrado.
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código duplicado",
                    "El código " + codigo +
                            " ya pertenece a otro producto."
            );
            txtCodigo.requestFocus(); // Lleva el cursor al campo código.
            return;                   // Cancela el guardado.
        }



        // Sentencia INSERT; cada ? se reemplaza después con un valor seguro.
        String sql = """
                INSERT INTO catalogos.productos       -- tabla donde se inserta
                (
                    codigo,                           -- código único del producto
                    nombre,                           -- nombre del producto
                    descripcion,                      -- descripción opcional
                    precio,                           -- precio de venta
                    stock,                            -- cantidad en existencia
                    fecha_creacion,                   -- fecha de registro
                    id_categoria,                     -- llave foránea a categorías
                    id_marca,                         -- llave foránea a marcas
                    estado                            -- 1 = activo
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1)    -- 8 parámetros y estado activo
                """;



        // try-with-resources cierra la conexión y la sentencia automáticamente.
        try (
                // Abre la conexión con PostgreSQL.
                Connection conexion =
                        DatabaseConecction.getConnection();
                // Precompila el INSERT; los parámetros evitan la inyección SQL.
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {
            // Parámetro 1: código.
            ps.setString(
                    1,
                    codigo
            );
            // Parámetro 2: nombre sin espacios sobrantes.
            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );
            // Parámetro 3: descripción.
            ps.setString(
                    3,
                    txtDescripcion.getText().trim()
            );
            // Parámetro 4: precio como BigDecimal (exacto para dinero).
            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    )
            );
            // Parámetro 5: stock convertido a entero.
            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );
            // Parámetro 6: fecha del DatePicker convertida a java.sql.Date.
            ps.setDate(
                    6,
                    Date.valueOf(
                            dpFecha.getValue()
                    )
            );
            // Parámetro 7: id de la categoría elegida en el ComboBox.
            ps.setInt(
                    7,
                    cmbCategoria.getValue()
                            .getIdCategoria()
            );
            // Parámetro 8: id de la marca elegida en el ComboBox.
            ps.setInt(
                    8,
                    cmbMarca.getValue()
                            .getIdMarca()
            );



            // Ejecuta el INSERT y devuelve cuántas filas se insertaron.
            int filasAfectadas =
                    ps.executeUpdate();



            // Si se insertó una fila, el registro fue exitoso.
            if (filasAfectadas > 0) {
                // Informa al usuario que se guardó.
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro guardado",
                        "El producto fue registrado correctamente."
                );
                limpiarFormulario(); // Deja el formulario listo para otro producto.
                cargarProductos();   // Recarga la tabla para mostrar el nuevo producto.
            }
        } catch (SQLException e) { // Captura cualquier error de la base de datos.
            /*
             * Aunque JavaFX realizó previamente la comprobación,
             * PostgreSQL continúa siendo la autoridad final.
             *
             * SQLState 23505 corresponde normalmente a una
             * violación de UNIQUE en PostgreSQL.
             */
            if ("23505".equals(e.getSQLState())) {
                // Avisa qué dato está repetido (código o nombre).
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Registro duplicado",
                        mensajeDuplicado(e)
                );
            } else { // Cualquier otro error de base de datos.
                // Muestra el detalle técnico del error.
                mostrarErrorBaseDatos(
                        "No se pudo guardar el producto.",
                        e
                );
            }
        }
    }



    /*
     * UPDATE del CRUD: modifica el producto seleccionado en la tabla.
     */
    @FXML
    private void actualizarProducto() {

        // Obtiene el producto que el usuario seleccionó en la tabla.
        Producto productoSeleccionado =
                tablaProductos.getSelectionModel()
                        .getSelectedItem();


        // Si no hay producto seleccionado no se puede actualizar.
        if (productoSeleccionado == null) {

            // Pide al usuario que seleccione un producto.
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto del TableView antes de actualizar."
            );

            return; // Sale sin actualizar.
        }


        // Si algún dato del formulario es inválido, no se actualiza.
        if (!validarFormulario()) {
            return; // Sale sin actualizar.
        }


        // Código escrito en el formulario, sin espacios en los extremos.
        String codigo =
                txtCodigo.getText().trim();


        /*
         * Se comprueba que otro producto diferente
         * no esté utilizando el mismo código.
         */
        // Se excluye el id del producto seleccionado para no compararlo consigo mismo.
        if (existeCodigo(
                codigo,
                productoSeleccionado.getIdProducto()
        )) {

            // Avisa que el código lo usa otro producto.
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Código duplicado",
                    "El código ingresado pertenece a otro producto."
            );

            txtCodigo.requestFocus(); // Lleva el cursor al código.

            return; // Cancela la actualización.
        }


        // Sentencia UPDATE con 9 parámetros.
        String sql = """
                UPDATE catalogos.productos    -- tabla que se modifica

                SET
                    codigo = ?,               -- nuevo código
                    nombre = ?,               -- nuevo nombre
                    descripcion = ?,          -- nueva descripción
                    precio = ?,               -- nuevo precio
                    stock = ?,                -- nuevo stock
                    fecha_creacion = ?,       -- nueva fecha
                    id_categoria = ?,         -- nueva categoría
                    id_marca = ?              -- nueva marca

                WHERE id_productos = ?        -- solo el producto seleccionado
                """;


        // try-with-resources cierra la conexión y la sentencia automáticamente.
        try (
                // Abre la conexión con PostgreSQL.
                Connection conexion =
                        DatabaseConecction.getConnection();

                // Precompila el UPDATE.
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            // Parámetro 1: código.
            ps.setString(
                    1,
                    codigo
            );

            // Parámetro 2: nombre.
            ps.setString(
                    2,
                    txtNombre.getText().trim()
            );

            // Parámetro 3: descripción.
            ps.setString(
                    3,
                    txtDescripcion.getText().trim()
            );

            // Parámetro 4: precio como BigDecimal.
            ps.setBigDecimal(
                    4,
                    new BigDecimal(
                            txtPrecio.getText().trim()
                    )
            );

            // Parámetro 5: stock como entero.
            ps.setInt(
                    5,
                    Integer.parseInt(
                            txtStock.getText().trim()
                    )
            );

            // Parámetro 6: fecha convertida a java.sql.Date.
            ps.setDate(
                    6,
                    Date.valueOf(
                            dpFecha.getValue()
                    )
            );

            // Parámetro 7: id de la categoría elegida.
            ps.setInt(
                    7,
                    cmbCategoria.getValue()
                            .getIdCategoria()
            );

            // Parámetro 8: id de la marca elegida.
            ps.setInt(
                    8,
                    cmbMarca.getValue()
                            .getIdMarca()
            );

            // Parámetro 9: id del producto que se va a modificar (WHERE).
            ps.setInt(
                    9,
                    productoSeleccionado.getIdProducto()
            );


            // Ejecuta el UPDATE y devuelve cuántas filas cambiaron.
            int filasAfectadas =
                    ps.executeUpdate();


            // Si cambió una fila, la actualización fue exitosa.
            if (filasAfectadas > 0) {

                // Informa al usuario.
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Registro actualizado",
                        "El producto fue actualizado correctamente."
                );

                limpiarFormulario(); // Limpia el formulario.

                cargarProductos(); // Recarga la tabla con los datos nuevos.
            }

        } catch (SQLException e) { // Captura errores de la base de datos.

            // 23505 = el código o el nombre chocan con una restricción UNIQUE.
            if ("23505".equals(e.getSQLState())) {

                // Avisa qué dato ya lo usa otro producto (código o nombre).
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Registro duplicado",
                        mensajeDuplicado(e)
                );

            } else { // Cualquier otro error.

                // Muestra el detalle técnico del error.
                mostrarErrorBaseDatos(
                        "No se pudo actualizar el producto.",
                        e
                );
            }
        }
    }



    /*
     * DELETE del CRUD: eliminación lógica, el producto pasa a estado = 0
     * y deja de mostrarse, pero no se borra de la base de datos.
     */
    @FXML
    private void eliminarProducto() {

        // Obtiene el producto seleccionado en la tabla.
        Producto productoSeleccionado =
                tablaProductos.getSelectionModel()
                        .getSelectedItem();


        // Si no hay producto seleccionado no se puede eliminar.
        if (productoSeleccionado == null) {

            // Pide al usuario que seleccione un producto.
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Producto no seleccionado",
                    "Seleccione un producto antes de eliminarlo."
            );

            return; // Sale sin eliminar.
        }


        /*
         * Se solicita confirmación para evitar que el usuario
         * desactive accidentalmente un registro.
         */
        // Crea una alerta de confirmación con botones Aceptar y Cancelar.
        Alert confirmacion =
                new Alert(Alert.AlertType.CONFIRMATION);

        // Título de la ventana.
        confirmacion.setTitle(
                "Confirmar eliminación"
        );

        // Encabezado del mensaje.
        confirmacion.setHeaderText(
                "Eliminar producto"
        );

        // Pregunta que incluye el nombre del producto.
        confirmacion.setContentText(
                "¿Desea desactivar el producto "
                        + productoSeleccionado.getNombre()
                        + "?"
        );


        // Muestra la confirmación y guarda el botón que presionó el usuario.
        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();


        // Si cerró la ventana o no presionó Aceptar, se cancela.
        if (
                respuesta.isEmpty()
                        ||
                        respuesta.get() != ButtonType.OK
        ) {

            return; // Sale sin eliminar.
        }


        // UPDATE que desactiva el producto (eliminación lógica).
        String sql = """
                UPDATE catalogos.productos    -- tabla de productos
                SET estado = 0                -- 0 = inactivo / eliminado
                WHERE id_productos = ?        -- solo el producto seleccionado
                """;


        // try-with-resources cierra la conexión y la sentencia automáticamente.
        try (
                // Abre la conexión con PostgreSQL.
                Connection conexion =
                        DatabaseConecction.getConnection();

                // Precompila la sentencia.
                PreparedStatement ps =
                        conexion.prepareStatement(sql)
        ) {

            // Parámetro 1: id del producto a desactivar.
            ps.setInt(
                    1,
                    productoSeleccionado.getIdProducto()
            );


            // Ejecuta la sentencia y devuelve cuántas filas cambiaron.
            int filasAfectadas =
                    ps.executeUpdate();


            // Si cambió una fila, la eliminación fue exitosa.
            if (filasAfectadas > 0) {

                // Informa al usuario.
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Producto eliminado",
                        "El producto fue desactivado correctamente."
                );

                limpiarFormulario(); // Limpia el formulario.

                cargarProductos(); // Recarga la tabla; el producto ya no aparece.
            }

        } catch (SQLException e) { // Captura errores de la base de datos.

            // Muestra el detalle técnico del error.
            mostrarErrorBaseDatos(
                    "No se pudo eliminar el producto.",
                    e
            );
        }
    }



    /*
     * BÚSQUEDA PARAMETRIZADA: arma la consulta solo con los filtros
     * que el usuario llenó y envía los valores con PreparedStatement.
     */
    @FXML
    private void buscarProductos() {
        /*
         * La consulta inicia con los INNER JOIN necesarios
         * y con una condición permanente:
         *
         * p.estado = 1
         *
         * De esta forma nunca mostramos productos
         * eliminados lógicamente.
         */
        // StringBuilder permite ir agregando condiciones a la consulta base.
        StringBuilder sql = new StringBuilder("""
                SELECT
                    p.id_productos,                        -- id del producto
                    p.codigo,                              -- código
                    p.nombre,                              -- nombre
                    p.descripcion,                         -- descripción
                    p.precio,                              -- precio
                    p.stock,                               -- existencias
                    p.fecha_creacion,                      -- fecha de registro
                    p.id_categoria,                        -- id de la categoría
                    c.nombre AS categoria,                 -- nombre de la categoría
                    m.id_marca,                            -- id de la marca
                    m.nombre AS marca,                     -- nombre de la marca
                    p.estado                               -- 1 = activo
                FROM catalogos.productos p                 -- tabla principal (alias p)
                INNER JOIN catalogos.categorias c          -- une con categorías (alias c)
                    ON p.id_categoria = c.id_categorias    -- relación producto-categoría
                INNER JOIN catalogos.marcas m              -- une con marcas (alias m)
                    ON p.id_marca = m.id_marca             -- relación producto-marca
                WHERE p.estado = 1                         -- solo productos activos
                """);



        /*
         * Aquí almacenaremos los valores que posteriormente
         * serán enviados al PreparedStatement.
         */
        List<Object> parametros =
                new ArrayList<>();
        // Texto del campo de búsqueda sin espacios en los extremos.
        String texto =
                txtBuscar.getText().trim();



        // Solo se filtra por texto si el usuario escribió algo.
        if (!texto.isBlank()) {
            /*
             * OR permite que el texto coincida con código
             * o nombre.
             *
             * ILIKE permite búsquedas parciales sin importar
             * mayúsculas y minúsculas.
             */
            sql.append("""

                    AND (
                        p.codigo ILIKE ?      -- coincidencia parcial en el código
                        OR p.nombre ILIKE ?   -- o coincidencia parcial en el nombre
                    )
                    """);



            // Valor del primer ?: el texto entre % significa "contiene".
            parametros.add(
                    "%" + texto + "%"
            );
            // Valor del segundo ?: el mismo texto para el nombre.
            parametros.add(
                    "%" + texto + "%"
            );
        }
        // Categoría elegida en el filtro (null si no eligió ninguna).
        Categoria categoria =
                cmbFiltroCategoria.getValue();



        // Solo se filtra por categoría si se eligió una.
        if (categoria != null) {
            sql.append("""

                    AND p.id_categoria = ?    -- productos de esa categoría
                    """);
            // Valor del ?: id de la categoría elegida.
            parametros.add(
                    categoria.getIdCategoria()
            );
        }
        // Fecha inicial del filtro (null si está vacía).
        LocalDate fechaDesde =
                dpDesde.getValue();



        // Solo se filtra si hay fecha inicial.
        if (fechaDesde != null) {
            sql.append("""

                    AND p.fecha_creacion >= ?    -- creados desde esa fecha
                    """);
            parametros.add(fechaDesde); // Valor del ?: fecha inicial.
        }
        // Fecha final del filtro (null si está vacía).
        LocalDate fechaHasta =
                dpHasta.getValue();



        // Solo se filtra si hay fecha final.
        if (fechaHasta != null) {
            sql.append("""

                    AND p.fecha_creacion <= ?    -- creados hasta esa fecha
                    """);
            parametros.add(fechaHasta); // Valor del ?: fecha final.
        }



        /*
         * Verificamos que las fechas tengan sentido.
         *
         * "Desde" no debería ser posterior a "Hasta".
         */
        if (
                fechaDesde != null
                        &&
                        fechaHasta != null
                        &&
                        fechaDesde.isAfter(fechaHasta)
        ) {
            // Avisa que el rango de fechas está al revés.
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de fechas incorrecto",
                    "La fecha inicial no puede ser posterior a la fecha final."
            );
            return; // Cancela la búsqueda.
        }
        // Precio mínimo; queda null si el campo está vacío.
        BigDecimal precioMinimo = null;



        // Solo se filtra si escribió un precio mínimo.
        if (!txtPrecioMin.getText().isBlank()) {
            try {
                // Convierte el texto a número.
                precioMinimo =
                        new BigDecimal(
                                txtPrecioMin
                                        .getText()
                                        .trim()
                        );



                // El precio mínimo no puede ser negativo.
                if (
                        precioMinimo.compareTo(
                                BigDecimal.ZERO
                        ) < 0
                ) {
                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio incorrecto",
                            "El precio mínimo no puede ser negativo."
                    );
                    return; // Cancela la búsqueda.
                }



                sql.append("""

                        AND p.precio >= ?    -- precio mayor o igual al mínimo
                        """);
                parametros.add(precioMinimo); // Valor del ?: precio mínimo.
            } catch (NumberFormatException e) { // El texto no era un número.
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio mínimo debe ser numérico."
                );
                txtPrecioMin.requestFocus(); // Lleva el cursor al precio mínimo.
                return;                      // Cancela la búsqueda.
            }
        }
        // Precio máximo; queda null si el campo está vacío.
        BigDecimal precioMaximo = null;



        // Solo se filtra si escribió un precio máximo.
        if (!txtPrecioMax.getText().isBlank()) {
            try {
                // Convierte el texto a número.
                precioMaximo =
                        new BigDecimal(
                                txtPrecioMax
                                        .getText()
                                        .trim()
                        );



                // El precio máximo no puede ser negativo.
                if (
                        precioMaximo.compareTo(
                                BigDecimal.ZERO
                        ) < 0
                ) {
                    mostrarAlerta(
                            Alert.AlertType.WARNING,
                            "Precio incorrecto",
                            "El precio máximo no puede ser negativo."
                    );
                    return; // Cancela la búsqueda.
                }



                sql.append("""

                        AND p.precio <= ?    -- precio menor o igual al máximo
                        """);
                parametros.add(precioMaximo); // Valor del ?: precio máximo.
            } catch (NumberFormatException e) { // El texto no era un número.
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Precio incorrecto",
                        "El precio máximo debe ser numérico."
                );
                txtPrecioMax.requestFocus(); // Lleva el cursor al precio máximo.
                return;                      // Cancela la búsqueda.
            }
        }



        /*
         * Validamos que el precio mínimo no sea superior
         * al precio máximo.
         */
        if (
                precioMinimo != null
                        &&
                        precioMaximo != null
                        &&
                        precioMinimo.compareTo(
                                precioMaximo
                        ) > 0
        ) {
            // Avisa que el rango de precios está al revés.
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Rango de precios incorrecto",
                    "El precio mínimo no puede ser mayor que el precio máximo."
            );
            return; // Cancela la búsqueda.
        }



        // Si marcó la casilla, solo se muestran productos con stock.
        if (chkConExistencias.isSelected()) {
            sql.append("""

                    AND p.stock > 0    -- solo con existencias
                    """);
        }




        // Criterio de orden elegido en el ComboBox.
        String orden =
                cmbOrdenar.getValue();



        // Si no eligió ninguno, se ordena por nombre.
        if (orden == null) {
            orden = "Nombre";
        }



        // Agrega el ORDER BY según la opción elegida.
        switch (orden) {
            case "Precio menor a mayor" ->
                    sql.append("""

                            ORDER BY p.precio ASC    -- más barato primero
                            """);
            case "Precio mayor a menor" ->
                    sql.append("""

                            ORDER BY p.precio DESC    -- más caro primero
                            """);
            case "Fecha más reciente" ->
                    sql.append("""

                            ORDER BY p.fecha_creacion DESC    -- más nuevo primero
                            """);
            case "Fecha más antigua" ->
                    sql.append("""

                            ORDER BY p.fecha_creacion ASC    -- más viejo primero
                            """);
            case "Stock mayor a menor" ->
                    sql.append("""

                            ORDER BY p.stock DESC    -- más existencias primero
                            """);
            case "Stock menor a mayor" ->
                    sql.append("""

                            ORDER BY p.stock ASC    -- menos existencias primero
                            """);
            default ->
                    sql.append("""

                            ORDER BY p.nombre ASC    -- orden alfabético
                            """);
        }

        // Vacía la lista solo cuando los filtros ya pasaron las validaciones.
        listaProductos.clear();

        // try-with-resources cierra la conexión y la sentencia automáticamente.
        try (
                // Abre la conexión con PostgreSQL.
                Connection conexion =
                        DatabaseConecction.getConnection();
                // Precompila la consulta que se armó con el StringBuilder.
                PreparedStatement ps =
                        conexion.prepareStatement(
                                sql.toString()
                        )
        ) {
            /*
             * Recorremos la lista de parámetros para
             * asignarlos dinámicamente al PreparedStatement.
             */
            for (
                    int i = 0;
                    i < parametros.size();
                    i++
            ) {
                // Valor que corresponde al ? número i + 1.
                Object valor =
                        parametros.get(i);



                // Las fechas se convierten a java.sql.Date.
                if (valor instanceof LocalDate fecha) {
                    ps.setDate(
                            i + 1,
                            Date.valueOf(fecha)
                    );
                // Los precios se envían como BigDecimal.
                } else if (valor instanceof BigDecimal numero) {
                    ps.setBigDecimal(
                            i + 1,
                            numero
                    );
                // Textos y enteros se envían tal cual.
                } else {
                    ps.setObject(
                            i + 1,
                            valor
                    );
                }
            }



            // Ejecuta la consulta y recorre cada fila del resultado.
            try (
                    ResultSet rs =
                            ps.executeQuery()
            ) {
                while (rs.next()) {
                    // Convierte la fila en un Producto y lo agrega a la lista.
                    listaProductos.add(
                            crearProductoDesdeResultSet(rs)
                    );
                }
            }



            // Muestra la lista filtrada en la tabla.
            tablaProductos.setItems(
                    listaProductos
            );
            // Si ningún producto cumple los filtros, se avisa.
            if (listaProductos.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sin resultados",
                        "No existen productos que coincidan con los filtros aplicados."
                );
            }
        } catch (SQLException e) { // Captura errores de la base de datos.
            // Muestra el detalle técnico del error.
            mostrarErrorBaseDatos(
                    "No se pudo realizar la búsqueda.",
                    e
            );
        }
    }
}