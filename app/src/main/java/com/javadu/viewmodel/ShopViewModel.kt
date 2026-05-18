package com.javadu.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javadu.data.database.entities.BonusType
import com.javadu.data.database.entities.ShopItem
import com.javadu.data.database.entities.UserBonus
import com.javadu.data.repository.LessonRepository
import com.javadu.data.repository.UnitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "ShopViewModel"

@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: LessonRepository,
    private val unitRepository: UnitRepository
) : ViewModel() {

    data class ShopState(
        val coins: Int = 0,
        val bonuses: List<UserBonus> = emptyList(),
        val ownedUnits: List<String> = emptyList(),
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
                val ownedUnits = unitRepository.getHiredUnits().firstOrNull()?.map { it.unitId } ?: emptyList()
                _state.value = _state.value.copy(
                    coins = currentCoins,
                    bonuses = bonuses,
                    ownedUnits = ownedUnits,
                    isLoading = false
                )
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val user = repository.currentUser.firstOrNull()
            val userId = user?.id ?: 0L
            val ownedUnits = unitRepository.getHiredUnits().firstOrNull()?.map { it.unitId } ?: emptyList()
            _state.value = _state.value.copy(
                coins = user?.coins ?: 0,
                ownedUnits = ownedUnits
            )
        }
    }

    fun purchaseItem(shopItem: ShopItem) {
        viewModelScope.launch {
            Log.d(TAG, "=== purchaseItem started ===")
            Log.d(TAG, "Item: ${shopItem.name}, Price: ${shopItem.price}, UnitId: ${shopItem.unitId}")
            
            val user = repository.currentUser.firstOrNull()
            val userId = user?.id
            Log.d(TAG, "User: $user, UserId: $userId")
            
            if (userId == null) {
                Log.e(TAG, "User ID is null")
                _state.value = _state.value.copy(purchaseResult = PurchaseResult.Error("Пользователь не найден"))
                return@launch
            }
            val currentCoins = user.coins ?: 0
            Log.d(TAG, "Current Coins: $currentCoins, Item Price: ${shopItem.price}")
            
            if (currentCoins < shopItem.price) {
                Log.e(TAG, "Not enough coins")
                _state.value = _state.value.copy(
                    purchaseResult = PurchaseResult.Error("Недостаточно CodeCoins для покупки \"${shopItem.name}\"")
                )
                return@launch
            }

            val ownedUnits = _state.value.ownedUnits
            Log.d(TAG, "Owned Units: $ownedUnits")
            
            if (shopItem.unitId != null && ownedUnits.contains(shopItem.unitId)) {
                Log.e(TAG, "Unit already owned")
                _state.value = _state.value.copy(
                    purchaseResult = PurchaseResult.Error("У вас уже есть этот юнит!")
                )
                return@launch
            }

            val success = if (shopItem.unitId != null) {
                Log.d(TAG, "Calling hireUnit for: ${shopItem.unitId}")
                val result = unitRepository.hireUnit(shopItem.unitId, userId)
                Log.d(TAG, "hireUnit result: ${result.isSuccess}, exception: ${result.exceptionOrNull()?.message}")
                if (!result.isSuccess) {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Неизвестная ошибка"
                    _state.value = _state.value.copy(
                        purchaseResult = PurchaseResult.Error("Ошибка покупки юнита: $errorMsg")
                    )
                }
                result.isSuccess
            } else if (shopItem.bonusType != null) {
                Log.d(TAG, "Calling purchaseBonus for: ${shopItem.bonusType}")
                repository.purchaseBonus(userId, shopItem.bonusType, shopItem.price)
            } else {
                Log.e(TAG, "Unknown item type")
                false
            }

            if (success) {
                Log.d(TAG, "Purchase successful")
                val newItemName = shopItem.name
                val newCoins = repository.getUserCoins(userId)
                val newOwnedUnits = unitRepository.getHiredUnits().firstOrNull()?.map { it.unitId } ?: emptyList()
                Log.d(TAG, "New Coins: $newCoins, New Owned Units: $newOwnedUnits")
                _state.value = _state.value.copy(
                    coins = newCoins,
                    ownedUnits = newOwnedUnits,
                    purchaseResult = PurchaseResult.Success(newItemName)
                )
            } else {
                Log.e(TAG, "Purchase failed")
                _state.value = _state.value.copy(
                    purchaseResult = PurchaseResult.Error("Не удалось купить \"${shopItem.name}\"")
                )
            }
            Log.d(TAG, "=== purchaseItem ended ===")
        }
    }

    fun clearPurchaseResult() {
        _state.value = _state.value.copy(purchaseResult = null)
    }
}
