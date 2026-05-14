package com.nammavastra.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nammavastra.model.Saree
import com.nammavastra.repository.CartRepository
import kotlinx.coroutines.flow.StateFlow

class CartViewModel(
    private val repository: CartRepository
) : ViewModel() {
    val items: StateFlow<List<Saree>> = repository.items

    fun add(item: Saree) = repository.add(item)

    fun remove(id: String) = repository.remove(id)

    fun clear() = repository.clear()

    fun contains(id: String): Boolean = repository.contains(id)

    fun switchOwner(userId: String?, email: String?) = repository.switchOwner(userId, email)
}

class CartViewModelFactory(
    private val repository: CartRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CartViewModel(repository) as T
    }
}
