package br.com.fiap.wtcclienteapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.fiap.wtcclienteapp.model.Cliente
import kotlinx.coroutines.launch

class ClienteViewModel : ViewModel() {
    private val repository = ClienteRepository()
    private val _clientes = MutableLiveData<List<Cliente>>()
    val clientes: LiveData<List<Cliente>> = _clientes

    fun filtrarClientes(filtros: Filtros) {
        viewModelScope.launch {
            try {
                val resultado = repository.buscarClientes(filtros)
                _clientes.postValue(resultado)
            } catch (e: Exception) {
                _clientes.postValue(emptyList())
            }
        }
    }
}


