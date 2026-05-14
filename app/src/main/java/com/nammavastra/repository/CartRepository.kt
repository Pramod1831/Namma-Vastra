package com.nammavastra.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.nammavastra.model.Saree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class CartRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("inquiry_cart", Context.MODE_PRIVATE)
    private val authPrefs = context.applicationContext.getSharedPreferences("auth_session", Context.MODE_PRIVATE)
    private val auth = FirebaseAuth.getInstance()
    private val json = Json { ignoreUnknownKeys = true }
    private val serializer = ListSerializer(Saree.serializer())
    private val authStateListener = FirebaseAuth.AuthStateListener {
        _items.value = load(currentKey())
    }
    private val authPrefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "email" || key == "display_name") {
            switchOwner(auth.currentUser?.uid, authPrefs.getString("email", ""))
        }
    }
    private var activeKey = currentKey()
    private val _items = MutableStateFlow(load(activeKey))
    val items: StateFlow<List<Saree>> = _items

    init {
        auth.addAuthStateListener(authStateListener)
        authPrefs.registerOnSharedPreferenceChangeListener(authPrefsListener)
    }

    fun add(item: Saree) {
        val updated = if (_items.value.any { it.id == item.id }) {
            _items.value
        } else {
            _items.value + item
        }
        save(updated)
    }

    fun remove(id: String) {
        save(_items.value.filterNot { it.id == id })
    }

    fun clear() {
        save(emptyList())
    }

    fun contains(id: String): Boolean = _items.value.any { it.id == id }

    private fun save(items: List<Saree>) {
        _items.value = items
        prefs.edit().putString(activeKey, json.encodeToString(serializer, items)).apply()
    }

    private fun load(key: String): List<Saree> {
        val raw = prefs.getString(key, null).orEmpty()
        if (raw.isBlank()) return emptyList()
        return runCatching { json.decodeFromString(serializer, raw) }.getOrDefault(emptyList())
    }

    private fun currentKey(): String {
        val firebaseKey = auth.currentUser?.uid.orEmpty()
        val cachedEmail = authPrefs.getString("email", "").orEmpty().trim().lowercase()
        return keyFor(firebaseKey, cachedEmail)
    }

    fun switchOwner(userId: String?, email: String?) {
        val nextKey = keyFor(userId.orEmpty(), email.orEmpty())
        if (nextKey == activeKey) return
        activeKey = nextKey
        _items.value = load(activeKey)
    }

    private fun keyFor(firebaseKey: String, email: String): String {
        val cachedEmail = email.trim().lowercase()
        val sessionKey = when {
            firebaseKey.isNotBlank() -> firebaseKey
            cachedEmail.isNotBlank() -> cachedEmail.replace("[^a-z0-9@._-]".toRegex(), "_")
            else -> "guest"
        }
        return "items_$sessionKey"
    }
}
