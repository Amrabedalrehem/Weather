package com.example.presentation.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Repository
import com.example.data.model.entity.FavouriteLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(val repository: Repository) : ViewModel() {

    private val pendingDeletion = MutableStateFlow<Set<Int>>(emptySet())

    val favourites: StateFlow<List<FavouriteLocation>> =
        repository.getAllFavourites()
            .combine(pendingDeletion) { list, pending ->
                list.filter { it.id !in pending }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    fun markForDeletion(location: FavouriteLocation) {
        pendingDeletion.value += location.id
    }

    fun confirmDelete(location: FavouriteLocation) {
        viewModelScope.launch {
            pendingDeletion.value -= location.id
            repository.delete(location)
        }
    }

    fun undoDelete(location: FavouriteLocation) {
        pendingDeletion.value -= location.id
    }
}

class FavoritesViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(repository) as T
    }
}