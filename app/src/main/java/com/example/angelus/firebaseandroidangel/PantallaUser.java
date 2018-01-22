package com.example.angelus.firebaseandroidangel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PantallaUser extends AppCompatActivity {
    private Button miButtonMisProductos, miButtonAnadoProduc, miButtonModificoProduc, miButtonBorroProduc, miButtonBuscoProduc, miButtonTodoTerreno;
    private TextView reCargoUser;
    private TextView miTextMuestraNombre, miTextMuestraDescripción, miTextMuestraCategoria ,miTextMuestraPrecio;
    private EditText miEditextNombre, miEditextDescripción, miEditextPrecio;
    private RadioGroup miRadiogroup;
    private RadioButton miRadioButtonCategoria, miRadioButtonUsuario;
    private Spinner miSpinnerCategoria;
    private String [] misCategorias;
    private ArrayAdapter<String> miArrayAdapter;

//Nuevos elementos para el examen
    private Button miButtonBuscarFavorito, miButtonMostrarFavorito;
    private CheckBox miCheckBox;

    private ListView miListView;
    private Adapter miAdapter;//Invoco a esta clase que he creado para rellenar con sus metodos mi ListView
    private String claveTRansitoria;

    private DatabaseReference bbdd; //DatabaseReferente nos da la referencia a nuestra base de datos
    private DatabaseReference bbdd2;//esta es para los productos de un usuario
    private DatabaseReference bbdd3;//esta es para todos los productos
    private FirebaseAuth miAuth;

    private ArrayList<Producto> miArrayListProductos;
    private ArrayList<Usuario> miArrayListUsuarios;
    private String miStringEditableParaRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla_user);

        Toast.makeText(this, "Ya estas logeado", Toast.LENGTH_SHORT).show();

        //botones que el usuario interaccionará en su perfil
        miButtonMisProductos = (Button)findViewById(R.id.buttonMisProductos);
        miButtonAnadoProduc = (Button)findViewById(R.id.buttonAnadirProducto);
        miButtonModificoProduc = (Button)findViewById(R.id.buttonModificarProducto);
        miButtonBorroProduc = (Button)findViewById(R.id.buttonBorrarProducto);
        miButtonBuscoProduc = (Button)findViewById(R.id.buttonBusquedaProductos);
        miButtonTodoTerreno = (Button)findViewById(R.id.buttonTodoTerreno);//Button todoTerreno importante para los cambios

        reCargoUser = (TextView)findViewById(R.id.textLogotipo);//con este recargamos la pagina, tipo logo

        //TextView que muestran la información de los productos
        miTextMuestraNombre = (TextView)findViewById(R.id.textNombre);
        miTextMuestraDescripción =(TextView)findViewById(R.id.textDescripción);
        miTextMuestraCategoria = (TextView)findViewById(R.id.textCategoría);
        miTextMuestraPrecio = (TextView)findViewById(R.id.textPrecio);

        //EdiText que recogen la información y modifican la base de datos. Tambien hay un spinner

        miEditextNombre = (EditText)findViewById(R.id.editTextNombreProducto);
        miEditextDescripción = (EditText)findViewById(R.id.editTextDescripciónProducto);
        miEditextPrecio = (EditText)findViewById(R.id.editTextPrecioProducto);
        miSpinnerCategoria = (Spinner)findViewById(R.id.spinnerCategoria);//este es el spinner

        //LISTVIEW QUE CARGAREMOS
        miListView = (ListView)findViewById(R.id.listViewZonaCargo);

        //RADIOBUTONS QUE OBTENDREMOS
        miRadiogroup = (RadioGroup) findViewById(R.id.radioGroupTotal);
        miRadioButtonUsuario =(RadioButton)findViewById(R.id.radioButtonPorUsuario);
        miRadioButtonCategoria = (RadioButton)findViewById(R.id.radioButtonPorCategoria);
        miStringEditableParaRadioGroup = "vacio";

        //NUEVOS ELEMENTOS PARA EL EXAMEN
        miButtonBuscarFavorito = (Button)findViewById(R.id.buttonMarcarProductosFavorito) ;
        miButtonMostrarFavorito = (Button)findViewById(R.id.buttonMostrarProductosFavorito) ;
        miCheckBox = (CheckBox)findViewById(R.id.checkBox);




        //AL PRINCIPIO ESTOS CAMPOS ESTAN DESABILITADOS HASTA QUE PRESIONE EL BOTON NECESARIO
        miEditextNombre.setVisibility(View.INVISIBLE);
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);
        miButtonTodoTerreno.setVisibility(View.INVISIBLE);
        miRadiogroup.setVisibility(View.INVISIBLE);

        miTextMuestraNombre.setVisibility(View.INVISIBLE);
        miTextMuestraDescripción.setVisibility(View.INVISIBLE);
        miTextMuestraCategoria.setVisibility(View.INVISIBLE);
        miTextMuestraPrecio.setVisibility(View.INVISIBLE);


        //relleno el spinner con la lista de categorias para PRODUCTOS disponibles;
        misCategorias = new String[]{"Tecnologia", "Coches","Hogar","Varios"};
        miArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, misCategorias);


        miArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//con esta linea el spinner sale hacia arriba
        miSpinnerCategoria.setPrompt("Escoge categoria");//intentamos ponerle un titulo al spinner, pero no conseguimos que pase

        miSpinnerCategoria.setAdapter(miArrayAdapter);//introducimos el arrayAdapter en el spinner

        buscoMisProductos();

        miListView.setEnabled(true);
        miListView.setClickable(true);
                miListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {//ESTO ES PARA QUE CAMBIE EL CONTENIDO DE UNOS DE LOS LINEARLAYOUT QUE CONTIENE INFO
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {//ESTE METODO NO FUNCIONA POR ALGUNE XTRAÑO MOTIVO

                        Toast.makeText(PantallaUser.this, "hola : "+position, Toast.LENGTH_SHORT).show(); //tengo un problema es que no me deja entrar aqui

                        Producto pr1 = (Producto)adapterView.getItemAtPosition(position);

                        Toast.makeText(PantallaUser.this, ""+pr1.getNombre(), Toast.LENGTH_SHORT).show();

                        String nombre = miArrayListProductos.get(position).getNombre();
                        String descripcion =  miArrayListProductos.get(position).getDescripcion();
                        String categoria = miArrayListProductos.get(position).getCategoria();
                        String precio = miArrayListProductos.get(position).getPrecio();
                        miTextMuestraNombre.setText(nombre);
                        miTextMuestraDescripción.setText(descripcion);
                        miTextMuestraCategoria.setText(categoria);
                        miTextMuestraPrecio.setText(precio);

                    }
                });




        //EMPEZAMOS CON LA MANDANGA

        miButtonMisProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

            buscoMisProductos();

            }
        });

        miButtonAnadoProduc.setOnClickListener(new View.OnClickListener() {//Listener para visualizar los campos de añadir producto
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

             anadoProductoCamposVisibles();
            }
        });
        miButtonBorroProduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

             borroProductoCamposVisibles();


            }
        });
        miButtonModificoProduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                miEditextNombre.setEnabled(true);//pongamoslo habilitado por si las moscas
                modificoCamposProductosactivar();

            }
        });

        miButtonBuscoProduc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                buscoCamposProductosActivar();
            }
        });
        miButtonMostrarFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                favoritoMostrarTodosEnListView();



            }
        });

        miRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {//AQUI TENEMOS LOS CAMBIOS de VIEWS SEGUN ESCOJA BUSCANDO EN EL RADIOGROUP
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(miRadioButtonUsuario.isChecked()){
                    miEditextNombre.setVisibility(View.VISIBLE);
                    miEditextNombre.setEnabled(true);
                    miSpinnerCategoria.setVisibility(View.INVISIBLE);

                    miStringEditableParaRadioGroup = "usuario";

                }
                if(miRadioButtonCategoria.isChecked()){
                    miSpinnerCategoria.setVisibility(View.VISIBLE);
                    miSpinnerCategoria.setEnabled(true);
                    miEditextNombre.setVisibility(View.INVISIBLE);

                    miStringEditableParaRadioGroup = "categoria";

                }
            }
        });

        miButtonBuscarFavorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                limpioCamposProductos();//limpiamos primero
                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios


                favoritoPongoCamposActivos();


            }
        });
                //BUTTON MULTIUSOSDEPENDIENDO DE LA ACCIÓN AQUI HAY MUCHA MANDANGA
                miButtonTodoTerreno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (miButtonTodoTerreno.getText().toString().equals("AÑADIR")) {
                            if (hayAlgunCampoVacio() == true) {//Si nos fijamos, llamamos a un metodo que hemos creado hayAlgunCampoVacio(), que devuelve True si estan tots completos

                                String nombre = miEditextNombre.getText().toString();
                                String descripcion = miEditextDescripción.getText().toString();//Recogemos los valores que necesitamos para nuestro nuevo producto
                                String categoria = miSpinnerCategoria.getSelectedItem().toString();
                                String precio = miEditextPrecio.getText().toString();

                                //A la hora de crear las tablas decimos que se tiene que posicionar como nodo en el UID del usuario, por lo tanto siempre crearemos los objetos adentro
                                miAuth = FirebaseAuth.getInstance();
                                FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                                String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                                bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                                //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
                                bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

                                Producto nuevoProducto = new Producto(nombre, descripcion, categoria, precio);
                                String clave = bbdd2.push().getKey();//recogemos clave para dar nombre a la nueva tabla hija

                                bbdd2.child(clave).setValue(nuevoProducto);//entonces a la hija(.child), de nombre clave, le plantamos tot el objeto a piñon con .setValue(Producto)


                                //PARTE EXTRA PARA BUSCAR, aunque estemos en añadir , DEBIDO A QUE ES MAS FÁCIL CREAR NODOS APARTE, TAMBIEN CREAREMOS UN NODO CON TODOS LOS PRODUCTOS, FUERA DE UN USUARIO
                                bbdd3 = FirebaseDatabase.getInstance().getReference("todosproductos");

                                bbdd3.child(clave).setValue(nuevoProducto);


                                Toast.makeText(PantallaUser.this, "Producto añadido", Toast.LENGTH_SHORT).show();

                                limpioCamposProductos();//limpiamos primero
                                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                            }
                        }

                        if (miButtonTodoTerreno.getText().toString().equals("BORRAR")) {

                            if (!TextUtils.isEmpty(miEditextNombre.getText().toString())) {

                                miAuth = FirebaseAuth.getInstance();
                                FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                                String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                                bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                                //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
                                bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

                                final String nombreMomentaneo = miEditextNombre.getText().toString();
                                Query q = bbdd2.orderByChild("nombre").equalTo(nombreMomentaneo); //para borrar necesitamos hacer una query de todos los valores en el apartado "nombre", que sean iguales al Editext que hemos recogido

                                q.addListenerForSingleValueEvent(new ValueEventListener() { //entonces haremos un addListenerForSingleValueEvent que llevará por parámetro un DataSnapshot que es por decirlo de alguna manera, las tablas que coinciden con la query
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int contador = 0;

                                        for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {

                                            String clave = dataSnapshot3.getKey(); //recogemos la clave que coincide con la query

                                            DatabaseReference ref = bbdd2.child(clave); //hacemos que un cursor momentaneo referencie donde esta esa clave dentro de bbdd2

                                            ref.removeValue(); //ya tenemos nuestra tabla referenciada y lista, la borramos con removeValue()

                                            Toast.makeText(PantallaUser.this, "El producto de nombre: " + nombreMomentaneo + " se ha borrado correctamente", Toast.LENGTH_SHORT).show();
                                            contador = 1;
                                        }
                                        if (contador == 0) {
                                            Toast.makeText(PantallaUser.this, "NO EXISTE EL PRODUCTO O ESTÁ MAL ESCRITO", Toast.LENGTH_SHORT).show();
                                        }
                                        contador = 0;

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            } else {
                                Toast.makeText(PantallaUser.this, "El campo nombre esta vacio", Toast.LENGTH_SHORT).show();
                            }

                            limpioCamposProductos();//limpiamos primero
                            anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                        }
                        if (miButtonTodoTerreno.getText().toString().equals("MODIFICAR")) {


                            if (!TextUtils.isEmpty(miEditextNombre.getText().toString())) {

                                miAuth = FirebaseAuth.getInstance();
                                FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                                String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                                bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                                //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
                                bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

                                final String nombreMomentaneo = miEditextNombre.getText().toString();
                                Query q = bbdd2.orderByChild("nombre").equalTo(nombreMomentaneo); //para borrar necesitamos hacer una query de todos los valores en el apartado "nombre", que sean iguales al Editext que hemos recogido

                                q.addListenerForSingleValueEvent(new ValueEventListener() { //entonces haremos un addListenerForSingleValueEvent que llevará por parámetro un DataSnapshot que es por decirlo de alguna manera, las tablas que coinciden con la query
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int contador = 0;

                                        for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {

                                            String clave = dataSnapshot3.getKey(); //recogemos la clave que coincide con la query

                                            DatabaseReference ref = bbdd2.child(clave); //hacemos que un cursor momentaneo referencie donde esta esa clave dentro de bbdd2

                                            Producto miProd = dataSnapshot3.getValue(Producto.class);//recogemos un objeto de tipo Producto para que se sepan los campos


                                            pasoTransitorioHaciaNingunaParte(miProd); //DESDE ESTE METODO PREPARAMOS LOS EDITEXT CON LOS VALORES ORIGINALES, PARA QUE SOLO CAMBIE LOS QUE NECESITE


                                            Toast.makeText(PantallaUser.this, "El producto de nombre: " + nombreMomentaneo + " esta listo, cambia los valores que desees", Toast.LENGTH_SHORT).show();
                                            contador = 1;
                                        }
                                        if (contador == 0) {
                                            Toast.makeText(PantallaUser.this, "NO EXISTE EL PRODUCTO O ESTÁ MAL ESCRITO", Toast.LENGTH_SHORT).show();
                                        }
                                        contador = 0;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            } else {
                                Toast.makeText(PantallaUser.this, "El campo nombre esta vacio", Toast.LENGTH_SHORT).show();
                            }


                        }
                        if (miButtonTodoTerreno.getText().toString().equals("GUARDAR CAMBIOS")) {
                            if (hayAlgunCampoVacio() == true) {
                                miAuth = FirebaseAuth.getInstance();
                                FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                                String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                                bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                                //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
                                bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

                                final String nombreMomentaneo = miEditextNombre.getText().toString();//ESTE ES EL CAMPO QUE SE LE PASA A LA QUERY

                                final String descripcionMomentanea = miEditextDescripción.getText().toString();//ESTOS CAMPOS LOS RECOJO ANTES DEL addLISTENERFORSINGLE para pasarselos al nuevo objeto(!!BIS-5-!!)
                                final String categoriaMomentanea = miSpinnerCategoria.getSelectedItem().toString();
                                final String precioMomentaneo = miEditextPrecio.getText().toString();

                                Query q = bbdd2.orderByChild("nombre").equalTo(nombreMomentaneo); //para borrar necesitamos hacer una query de todos los valores en el apartado "nombre", que sean iguales al Editext que hemos recogido

                                q.addListenerForSingleValueEvent(new ValueEventListener() { //entonces haremos un addListenerForSingleValueEvent que llevará por parámetro un DataSnapshot que es por decirlo de alguna manera, las tablas que coinciden con la query
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        int contador = 0;

                                        for (DataSnapshot dataSnapshot3 : dataSnapshot.getChildren()) {

                                            String clave = dataSnapshot3.getKey(); //recogemos la clave que coincide con la query

                                            DatabaseReference ref = bbdd2.child(clave); //hacemos que un cursor momentaneo referencie donde esta esa clave dentro de bbdd2

                                            Producto miProd = new Producto(nombreMomentaneo, descripcionMomentanea, categoriaMomentanea, precioMomentaneo);//DE ESTE OBJETO HABLABA EN EL (!!BIS-5-!!)

                                            ref.setValue(miProd);//Y finalmente le cambiamos tot el objeto de golpe, debido a que asi podemos modificar varios campos

                                            Toast.makeText(PantallaUser.this, "El producto de nombre: " + nombreMomentaneo + " Ha sido modificado", Toast.LENGTH_SHORT).show();
                                            contador = 1;
                                        }
                                        if (contador == 0) {
                                            Toast.makeText(PantallaUser.this, "NO EXISTE EL PRODUCTO O ESTÁ MAL ESCRITO", Toast.LENGTH_SHORT).show();
                                        }
                                        contador = 0;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                            limpioCamposProductos();//limpiamos primero
                            anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                        }

                        if ((miButtonTodoTerreno.getText().toString().equals("BUSCAR"))) {

                            if (!miStringEditableParaRadioGroup.equals("vacio")) {

                                if (miStringEditableParaRadioGroup.equals("categoria")) {
                                    String categoria = miSpinnerCategoria.getSelectedItem().toString();
                                    buscoTodosLosProductosPorCategoria(categoria);//le enviamos la categoria del spinner para que llene el ListView con esa categoria
                                }
                                if (miStringEditableParaRadioGroup.equals("usuario")) {

                                    String usuario = miEditextNombre.getText().toString();
                                    //Usuario user =  buscoTodosLosproductosPorUsuario(usuario).get(0);
                                    //Toast.makeText(PantallaUser.this, ""+user.getAlias(), Toast.LENGTH_SHORT).show();
                                    buscoTodosLosproductosPorUsuario(usuario);

                                }

                            } else {
                                Toast.makeText(PantallaUser.this, "No has seleccionado el radioButton", Toast.LENGTH_SHORT).show();
                            }


                            limpioCamposProductos();//limpiamos primero
                            anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                        }

                        if(miButtonTodoTerreno.getText().toString().equals("ESCOGE FAVORITO")){
                            if(miCheckBox.isChecked()){

                                final String nombreProductoAbuscar = miEditextNombre.getText().toString();

                                miAuth = FirebaseAuth.getInstance();

                                bbdd = FirebaseDatabase.getInstance().getReference("todosproductos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE todosproductos.....

                                bbdd.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {


                                        miArrayListProductos = new ArrayList<Producto>();

                                        for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                                            Producto producto = dataSnapshot1.getValue(Producto.class);

                                            if(producto.getNombre().equals(nombreProductoAbuscar))//si esta categoria es igual a la del producto que observamos, entonces la guardamos en el arrayList que le pondremos al adapter
                                                miArrayListProductos.add(producto);
                                                claveTRansitoria = "";
                                                claveTRansitoria = dataSnapshot1.getKey(); //importante esta clave para guardar el objeto favorito con la misma llave
                                        }
                                        try {
                                            String nombre = miArrayListProductos.get(0).getNombre();
                                            String descripcion = miArrayListProductos.get(0).getDescripcion();
                                            String categoria = miArrayListProductos.get(0).getCategoria();
                                            String precio = miArrayListProductos.get(0).getPrecio();
                                            miEditextNombre.setText(nombre);
                                            miEditextDescripción.setText(descripcion);

                                            int numTransitorio = 0;
                                            switch (categoria){
                                                case "Hogar":
                                                    numTransitorio = 2;
                                                    break;
                                                case "Tecnologia":
                                                    numTransitorio = 0;
                                                    break;
                                                case "Coches":
                                                    numTransitorio = 1;
                                                    break;
                                                case "Varios":
                                                    numTransitorio = 3;
                                                    break;

                                            }

                                            miSpinnerCategoria.setSelection(numTransitorio);
                                            miEditextPrecio.setText(precio);

                                            miEditextNombre.setVisibility(View.VISIBLE);
                                            miEditextNombre.setEnabled(false);
                                            miEditextDescripción.setVisibility(View.VISIBLE);
                                            miEditextDescripción.setEnabled(false);
                                            miSpinnerCategoria.setVisibility(View.VISIBLE);
                                            miSpinnerCategoria.setEnabled(false);
                                            miEditextPrecio.setVisibility(View.VISIBLE);
                                            miEditextPrecio.setEnabled(false);

                                            miButtonTodoTerreno.setText("GUARDAR FAVORITO");

                                        }catch (Exception e){
                                        Toast.makeText(PantallaUser.this, "Algo ha pasado", Toast.LENGTH_SHORT).show();}
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });




                            }else{
                                Toast.makeText(PantallaUser.this, "No has seleccionado el checkBox", Toast.LENGTH_SHORT).show();
                            }




                        }
                        if(miButtonTodoTerreno.getText().toString().equals("GUARDAR FAVORITO")){
                            if(miCheckBox.isChecked()){


                                final String nombreMomentaneo = miEditextNombre.getText().toString();

                                final String descripcionMomentanea = miEditextDescripción.getText().toString();//ESTOS CAMPOS LOS RECOJO ANTES DEL addLISTENERFORSINGLE para pasarselos al nuevo objeto(!!BIS-5-!!)
                                final String categoriaMomentanea = miSpinnerCategoria.getSelectedItem().toString();
                                final String precioMomentaneo = miEditextPrecio.getText().toString();


                                miAuth = FirebaseAuth.getInstance();
                                FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
                                String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


                                bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
                                //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
                                bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....
                                bbdd3 = bbdd2.child("favoritos");


                                Producto nuevoProducto = new Producto(nombreMomentaneo, descripcionMomentanea, categoriaMomentanea, precioMomentaneo);

                                bbdd3.child(claveTRansitoria).setValue(nuevoProducto);


                                Toast.makeText(PantallaUser.this, "Producto favorito añadido", Toast.LENGTH_SHORT).show();

                                limpioCamposProductos();//limpiamos primero
                                anadoProductoCamposInVisibles();//volvemos a poner los campos invisibles para evitar lios

                            }
                        }



                    }
                });


    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    private void buscoMisProductos(){

        miAuth = FirebaseAuth.getInstance();
        FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
        String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....

        bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
        //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
        bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....

        bbdd2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                miArrayListProductos = new ArrayList<Producto>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Producto producto = dataSnapshot1.getValue(Producto.class);

                    miArrayListProductos.add(producto);
                }

                miAdapter = new Adapter(getApplicationContext(), miArrayListProductos);//veamos que hay una clase creada nuestra llamada Adapter, que recibe un arrayList de objetos Producto
                miListView.setAdapter(miAdapter);//finalmente le colocamos el adaptador al listView


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void buscoTodosLosProductosPorCategoria(final String categoriaFinalisima){//recibimos una categoria del spinner

        miAuth = FirebaseAuth.getInstance();

        bbdd = FirebaseDatabase.getInstance().getReference("todosproductos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE todosproductos.....


        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                miArrayListProductos = new ArrayList<Producto>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Producto producto = dataSnapshot1.getValue(Producto.class);

                    if(producto.getCategoria().equals(categoriaFinalisima))//si esta categoria es igual a la del producto que observamos, entonces la guardamos en el arrayList que le pondremos al adapter
                    miArrayListProductos.add(producto);
                }

                miAdapter = new Adapter(getApplicationContext(), miArrayListProductos);//veamos que hay una clase creada nuestra llamada Adapter, que recibe un arrayList de objetos Producto
                miListView.setAdapter(miAdapter);//finalmente le colocamos el adaptador al listView


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void buscoTodosLosproductosPorUsuario(final String usuarioFinal){

        miArrayListUsuarios = new ArrayList<Usuario>();
        final Usuario miUserFinalisimo = new Usuario();

        miAuth = FirebaseAuth.getInstance();

        bbdd = FirebaseDatabase.getInstance().getReference("usuarios");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE usuarios.....
        bbdd.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Usuario usuario = dataSnapshot1.getValue(Usuario.class);


                    if(usuario.getAlias().equals(usuarioFinal)) {

                        miArrayListUsuarios.add(usuario);
                    }
                }
                if(miArrayListUsuarios.size() == 0){
                    Toast.makeText(PantallaUser.this, "No existe ningun usuario llamado asi", Toast.LENGTH_SHORT).show();
                }

                if(miArrayListUsuarios.size()>0) {
                    Toast.makeText(getApplicationContext(), "" + miArrayListUsuarios.get(0).getAlias(), Toast.LENGTH_SHORT).show();
                    Usuario user =  miArrayListUsuarios.get(0);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void favoritoMostrarTodosEnListView(){

        miAuth = FirebaseAuth.getInstance();
        FirebaseUser miUser = miAuth.getCurrentUser();//recogemos el objeto FirebaseUser actual
        String miUIDUSER = miUser.getUid().toString();//para obtener su UID, un valor que nos ayudará unas lineas mas adelante....


        bbdd = FirebaseDatabase.getInstance().getReference("productos");//UNA VEZ ESTAMOS REFERENCIANDO LA TABLA PADRE PRODUCTOS.....
        //.....PARA GARANTIZAR QUE UN USUARIO SOLO PUEDA AÑADIR PRODUCTOS PROPIOS.....
        bbdd2 = bbdd.child(miUIDUSER);//.....HACEMOS QUE AÑADA LOS PRODUCTOS ¡¡SOLO!! DENTRO DE UNA TABLA PADRE QUE ES EL UID DEL USUSARIO LOGEADO EN ESE MOMENTO....
        bbdd3 = bbdd2.child("favoritos");

        bbdd3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                miArrayListProductos = new ArrayList<Producto>();

                for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren()){

                    Producto producto = dataSnapshot1.getValue(Producto.class);

                    miArrayListProductos.add(producto);
                }

                miAdapter = new Adapter(getApplicationContext(), miArrayListProductos);//veamos que hay una clase creada nuestra llamada Adapter, que recibe un arrayList de objetos Producto
                miListView.setAdapter(miAdapter);//finalmente le colocamos el adaptador al listView


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void anadoProductoCamposVisibles(){
        miEditextNombre.setVisibility(View.VISIBLE);
        miEditextDescripción.setVisibility(View.VISIBLE);
        miEditextPrecio.setVisibility(View.VISIBLE);
        miSpinnerCategoria.setVisibility(View.VISIBLE);
        miButtonTodoTerreno.setText("AÑADIR");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);
    }
    private void anadoProductoCamposInVisibles(){
        miEditextNombre.setVisibility(View.INVISIBLE);
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);
        miButtonTodoTerreno.setVisibility(View.INVISIBLE);
        miRadiogroup.setVisibility(View.INVISIBLE);
        miCheckBox.setVisibility(View.INVISIBLE);
    }
    private void borroProductoCamposVisibles(){
        miEditextNombre.setVisibility(View.VISIBLE);//SOLO ESTE ES VISIBLE
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);
        miButtonTodoTerreno.setText("BORRAR");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);//BUENO, APARTE DEL BOTON
    }
    private void limpioCamposProductos(){
        miEditextNombre.setText("");
        miEditextDescripción.setText("");
        miEditextPrecio.setText("");
        miSpinnerCategoria.setSelected(false);
        miButtonTodoTerreno.setText("TODOTERRENO");
        miCheckBox.setVisibility(View.INVISIBLE);

    }
    private void modificoCamposProductosactivar(){

        miEditextNombre.setVisibility(View.VISIBLE);
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);
        miButtonTodoTerreno.setText("MODIFICAR");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);

    }
    private void buscoCamposProductosActivar(){

        miEditextNombre.setVisibility(View.INVISIBLE);
        miEditextDescripción.setVisibility(View.INVISIBLE);
        miEditextPrecio.setVisibility(View.INVISIBLE);
        miSpinnerCategoria.setVisibility(View.INVISIBLE);//campito
        miButtonTodoTerreno.setText("BUSCAR");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);
        miRadiogroup.setVisibility(View.VISIBLE);

    }
    private void pasoTransitorioHaciaNingunaParte(Producto producto){
        miEditextNombre.setText(producto.getNombre());
        miEditextNombre.setEnabled(false);

        miEditextDescripción.setText(producto.getDescripcion());
        miEditextDescripción.setVisibility(View.VISIBLE);

        int numTransitorio = 0;
        switch (producto.getCategoria()){
            case "Hogar":
                numTransitorio = 2;
                break;
            case "Tecnologia":
                numTransitorio = 0;
            break;
            case "Coches":
                numTransitorio = 1;
                break;
            case "Varios":
                numTransitorio = 3;
                break;

        }

        miSpinnerCategoria.setSelection(numTransitorio,true);
        miSpinnerCategoria.setVisibility(View.VISIBLE);

        miEditextPrecio.setText(producto.getPrecio());
        miEditextPrecio.setVisibility(View.VISIBLE);

        miButtonTodoTerreno.setText("GUARDAR CAMBIOS");//COMO VEMOS ESTE PUNTO ES CLAVE A LA HORA DE DESPUES ARRIBA DISCREPAR POR EL TEXTO DEL BOTON

    }
    public void favoritoPongoCamposActivos(){
        miEditextNombre.setVisibility(View.VISIBLE);
        miButtonTodoTerreno.setText("ESCOGE FAVORITO");
        miButtonTodoTerreno.setVisibility(View.VISIBLE);
        miCheckBox.setVisibility(View.VISIBLE);
    }

    private boolean hayAlgunCampoVacio(){

        String nombre = miEditextNombre.getText().toString();
        String descripcion = miEditextDescripción.getText().toString();
        String categoria = miSpinnerCategoria.getSelectedItem().toString();
        String precio = miEditextPrecio.getText().toString();
        boolean todoCorrecto = false;

        if (!TextUtils.isEmpty(nombre)) {
            if (!TextUtils.isEmpty(descripcion)) {
                if (!TextUtils.isEmpty(categoria)) {
                    if (!TextUtils.isEmpty(precio)) {

                        todoCorrecto = true;
                    } else {
                        Toast.makeText(this, "Campo nombre vacio", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Campo descripción vacio", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Campo categoría vacio", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Campo precio vacio", Toast.LENGTH_SHORT).show();
        }
        return todoCorrecto;
    }

}
