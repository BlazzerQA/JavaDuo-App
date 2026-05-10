package com.javadu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.database.entities.ShopItem
import com.javadu.data.database.entities.UserBonus
import com.javadu.data.repository.LessonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: LessonRepository
) : ViewModel() {

    data class ShopState(
        val coins: Int = 0,
        val bonuses: List<UserBonus> = emptyList(),
        val isLoading: Boolean = true,
        val purchaseResult: PurchaseResult? = null
    )

    sealed class PurchaseResult {
        data class Success(val itemName: String) : PurchaseResult()
        data class Error(val message: String) : PurchaseResult()
    }

    private val _state = MutableStateFlow(ShopState())
    val state: StateFlow<ShopState> = _state

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val user = repository.currentUser.firstOrNull()
            val userId = user?.id ?: 0L

            repository.getUserBonuses(userId).collect { bonuses ->
                val currentCoins = repository.getUserCoins(userId)
                _state.value = _state.value.copy(
                    coins = currentCoins,
                    bonuses = bonuses,
                    isLoading = false
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull()
            _state.value = _state.value.copy(coins = user?.coins ?: 0)
        }
    }

    fun purchaseItem(shopItem: ShopItem) {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull()
            val userId = user?.id
            if (userId == null) {
                _state.value = _state.value.copy(purchaseResult = PurchaseResult.Error("Пользователь не найден"))
                return@launch
            }
            if ((user.coins ?: 0) < shopItem.price) {
                _state.value = _state.value.copy(
                    purchaseResult = PurchaseResult.Error("Недостаточно CodeCoins для покупки \"${shopItem.name}\"")
                )
                return@launch
            }
            val success = repository.purchaseBonus(userId, shopItem.type, shopItem.price)
            if (success) {
                _state.value = _state.value.copy(
                    coins = repository.getUserCoins(userId),
                    purchaseResult = PurchaseResult.Success(shopItem.name)
                )
            } else {
                _state.value = _state.value.copy(
                    purchaseResult = PurchaseResult.Error("Не удалось купить \"${shopItem.name}\"")
                )
            }
        }
    }

    fun clearPurchaseResult() {
        _state.value = _state.value.copy(purchaseResult = null)
    }
}
