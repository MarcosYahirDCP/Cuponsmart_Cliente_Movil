package uv.tc.cuponsmart_android.fragments

import Checklist
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uv.tc.cuponsmart_android.CustomAdapter
import uv.tc.cuponsmart_android.R
import uv.tc.cuponsmart_android.databinding.FragmentPromocionesBinding
import uv.tc.cuponsmart_android.interfaces.NotificarClick
import uv.tc.cuponsmart_android.modelo.ConexionWS
import uv.tc.cuponsmart_android.modelo.DAO.PromocionDAO
import uv.tc.cuponsmart_android.modelo.poko.Categoria
import uv.tc.cuponsmart_android.modelo.poko.Empresa
import uv.tc.cuponsmart_android.modelo.poko.Promocion
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.ArrayList


class PromocionesFragment : Fragment(), NotificarClick,  AdapterView.OnItemSelectedListener{
    private  var listaPromociones: ArrayList<Promocion> = ArrayList()
    private var  listaCategorias :ArrayList<Categoria> = ArrayList()
    private var listaEmpresas : ArrayList<Empresa> = ArrayList()
    val gson = Gson()

    private lateinit var binding: FragmentPromocionesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPromocionesBinding.inflate(inflater, container, false)
        //obtenerPromociones()
        personalizarBotones()
        cargarSpiner()
        return binding.root
    }

    private fun personalizarBotones(){
        binding.btnFarmacia.setOnClickListener{
            obtenerPromocionesCategoria(1)
            limpiarRecycler()
        }
        binding.btnCafeteria.setOnClickListener{
            obtenerPromocionesCategoria(2)
            limpiarRecycler()
        }
        binding.btnHeladeria.setOnClickListener{
            obtenerPromocionesCategoria(3)
            limpiarRecycler()
        }
        binding.btnJugueteria.setOnClickListener{
            obtenerPromocionesCategoria(4)
            limpiarRecycler()
        }
        binding.btnBebidas.setOnClickListener{
            obtenerPromocionesCategoria(5)
            limpiarRecycler()
        }
        binding.btnDulceria.setOnClickListener{
            obtenerPromocionesCategoria(6)
            limpiarRecycler()
        }
        binding.btnComidaRapida.setOnClickListener{
            obtenerPromocionesCategoria(7)
            limpiarRecycler()
        }
        binding.btnTodos.setOnClickListener{
            obtenerPromociones()

        }
        binding.etSearch.setOnClickListener {
            binding.etSearch.getBackground().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_ATOP)
            Handler(Looper.getMainLooper()).postDelayed({
                binding.etSearch.getBackground().setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
            }, 100)
            mostrarDatePicker()
        }
    }
    private fun cargarSpiner(){
        PromocionDAO.obtenerEmpresas(requireContext(),"empresa/listaEmpresa"){
            respuesta->
            serializarRespuestaEmpresas(respuesta)
        }
    }
    private fun serializarRespuestaEmpresas(json: String) {
        val typeLista = object : TypeToken<ArrayList<Empresa>>() {}.type
        listaEmpresas = gson.fromJson(json, typeLista)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaEmpresas)
        adapter.setDropDownViewResource(com.google.android.material.R.layout.support_simple_spinner_dropdown_item)
        binding.spEmpresas.adapter = adapter
        binding.spEmpresas.onItemSelectedListener = this
    }
    private fun mostrarDatePicker() {
        val datePicker = DatePickerFragment2 { dia, mes, anio -> onDateSelected(dia, mes, anio) }
        datePicker.show(childFragmentManager,"datepicker")

    }

    //------- METODO QUE ESTABLECE LA FECHA SELECCIONADA
    fun onDateSelected (dia:Int, mes: Int, anio:Int){
        val fechaFormateadaSQL = String.format("%04d-%02d-%02d", anio, mes + 1, dia)
        binding.etSearch.setText(fechaFormateadaSQL)
//        obtenerPromocionesFechaInicio(fechaEcode)

    }
    private fun obtenerPromocionesFechaInicio(fecha: String){
        PromocionDAO.obtenerPromocionesFechaInicio(requireContext(), "promocion/promocionFechaInicio", fecha){
            respuesta->
            serialziarRespuestaFechaInicio(respuesta)
        }
    }
    private fun serialziarRespuestaFechaInicio(json: String){
        val typeLista= object : TypeToken<ArrayList<Promocion>>() {}.type
        listaPromociones = gson.fromJson(json,typeLista)
        cargarInformacionRecycler()
    }
    private fun limpiarRecycler() {
        listaPromociones.clear()
        cargarInformacionRecycler()
    }
    private fun obtenerPromociones(){
        PromocionDAO.obtenerPromociones(requireContext(),"promocion/promociones"){
            respuesta ->
            serializarRespuestaPromociones(respuesta)
        }
    }
    private fun obtenerPromocionesCategoria(idCategoria : Int){
        PromocionDAO.obtenerPromocionesCategoria(requireContext(), "promocion/promocionCategoria", idCategoria){
                respuesta ->
            serializarRespuestaPromociones(respuesta)
        }
    }
    private fun serializarRespuestaPromociones(json : String){
        val typeLista= object : TypeToken<ArrayList<Promocion>>() {}.type
        listaPromociones = gson.fromJson(json,typeLista)
        cargarInformacionRecycler()
    }
    private  fun cargarInformacionRecycler(){
        binding.recyclerPromocion.layoutManager = LinearLayoutManager(context)

        if (listaPromociones.size > 0){
            binding.recyclerPromocion.adapter=CustomAdapter(listaPromociones,this,requireContext())
        } else {
            // Si la lista está vacía, puedes manejarlo según tus necesidades.
            // Puedes mostrar un mensaje, un estado vacío, etc.
            Toast.makeText(requireContext(), "No hay promociones disponibles.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun seleccionarItem(posicion: Int, promocion: Promocion) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val empresaSeleccionada: Empresa = parent?.getItemAtPosition(position) as Empresa
        PromocionDAO.obtenerPromocionesPorEmpresa(requireContext(),"promocion/promocionesPorEmpresa",empresaSeleccionada.idEmpresa!!){
            respuesta->
            limpiarRecycler()
            serializarRespuestaPromociones(respuesta)
        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

}