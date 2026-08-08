package com.seanchen.xinchat.core.util.storage

import android.app.Application
import android.os.Parcelable
import com.tencent.mmkv.MMKV
import java.util.Collections
import kotlinx.serialization.json.Json

object MMKVUtils {
    /**
     * MMKV是否已初始化
     */
    private var isInitialized = false

    /**
     * 默认实例
     */
    private val defaultMMKV by lazy {
        checkInitialization()
        MMKV.defaultMMKV()
    }

    private val instanceMap = Collections.synchronizedMap(HashMap<String, MMKV>())

    fun init(application: Application): String {
        val rootDir = MMKV.initialize(application)
        isInitialized = true
        return rootDir
    }

    /**
     * 检查MMKV是否已初始化
     */
    private fun checkInitialization() {
        if (!isInitialized) {
            throw IllegalStateException("MMKVUtils未初始化")
        }
    }

    /**
     * 获取MMKV命名实例
     */
    fun getInstance(
        name: String,
        mode: Int = MMKV.SINGLE_PROCESS_MODE,
        cryptKey: String? = null,
        rootDir: String? = null
    ): MMKV {
        checkInitialization()

        return instanceMap.getOrPut(name) {
            if (cryptKey != null) {
                MMKV.mmkvWithID(name, mode, cryptKey, rootDir)
            } else {
                MMKV.mmkvWithID(name, mode, null, rootDir)
            }
        }
    }

    /**
     * 获取多进程访问的MMKV实例
     */
    fun getMultiProcessInstance(
        name: String,
        cryptKey: String,
        multiProcess: Boolean = false
    ): MMKV {
        val mode = if (multiProcess){
            MMKV.MULTI_PROCESS_MODE
        } else {
            MMKV.SINGLE_PROCESS_MODE
        }
        return getInstance(name, mode, cryptKey)
    }

    /**
     * 获取加密的MMKV实例
     */
    fun getEncryptedInstance(name: String, cryptKey: String, multiProcess: Boolean = false): MMKV {
        val mode = if (multiProcess) {
            MMKV.MULTI_PROCESS_MODE
        } else {
            MMKV.SINGLE_PROCESS_MODE
        }
        return getInstance(name, mode, cryptKey)
    }

    /**
     * 存储Boolean值
     */
    fun putBoolean(key: String, value: Boolean) {
        defaultMMKV.encode(key, value)
    }

    /**
     * 获取Boolean值
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return defaultMMKV.decodeBool(key, defaultValue)
    }

    /**
     * 存储Int值
     */
    fun putInt(key: String, value: Int) {
        defaultMMKV.encode(key, value)
    }

    /**
     * 获取Int值
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return defaultMMKV.decodeInt(key, defaultValue)
    }

    /**
     * 存储Long值
     */
    fun putLong(key: String, value: Long) {
        defaultMMKV.encode(key, value)
    }

    /**
     * 获取Long值
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long{
        return defaultMMKV.decodeLong(key, defaultValue)
    }

    /**
     * 存储Float
     */
    fun putFloat(key: String, value: Float){
        defaultMMKV.encode(key, value)
    }

    /**
     * 获取Float
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return defaultMMKV.decodeFloat(key, defaultValue)
    }

    fun putDouble(key: String, value: Double) {
        defaultMMKV.encode(key, value)
    }

    fun getDouble(key: String, defaultValue: Double = 0.0): Double {
        return defaultMMKV.decodeDouble(key, defaultValue)
    }

    fun putString(key: String, value: String?) {
        defaultMMKV.encode(key, value)
    }

    /**
     * 获取String值
     * 用法示例：val username = MMKVUtils.getString("username", "")
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 存储的String值，如不存在则返回默认值
     * @author Joker.X
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return defaultMMKV.decodeString(key, defaultValue) ?: defaultValue
    }

    /**
     * 存储ByteArray值
     * 用法示例：MMKVUtils.putBytes("data", byteArrayOf(1, 2, 3))
     *
     * @param key 键
     * @param value 值
     * @author Joker.X
     */
    fun putBytes(key: String, value: ByteArray?) {
        defaultMMKV.encode(key, value)
    }

    /**
     * 获取ByteArray值
     * 用法示例：val data = MMKVUtils.getBytes("data")
     *
     * @param key 键
     * @return 存储的ByteArray值，如不存在则返回null
     * @author Joker.X
     */
    fun getBytes(key: String): ByteArray? {
        return defaultMMKV.decodeBytes(key)
    }

    /**
     * 存储可序列化对象
     * 用法示例：MMKVUtils.putParcelable("user", userInfo)
     *
     * @param key 键
     * @param value 值，需实现Parcelable接口
     * @author Joker.X
     */
    fun <T : Parcelable> putParcelable(key: String, value: T?) {
        defaultMMKV.encode(key, value)
    }

    /**
     * 获取可序列化对象
     * 用法示例：val user = MMKVUtils.getParcelable("user", UserInfo::class.java)
     *
     * @param key 键
     * @param clazz 对象类型
     * @return 存储的对象，如不存在则返回null
     * @author Joker.X
     */
    fun <T : Parcelable> getParcelable(key: String, clazz: Class<T>): T? {
        return defaultMMKV.decodeParcelable(key, clazz)
    }

    /**
     * 存储任意可序列化对象（基于kotlinx.serialization）
     * 用法示例：MMKVUtils.putObject("cart", cart)
     *
     * @param key 键
     * @param value 值，需加 @Serializable 注解
     * @author Joker.X
     */
    inline fun <reified T> putObject(key: String, value: T) {
        val json = Json.encodeToString(value)
        putString(key, json)
    }

    /**
     * 获取任意可序列化对象（基于kotlinx.serialization）
     * 用法示例：val cart = MMKVUtils.getObject<Cart>("cart")
     *
     * @param key 键
     * @return 存储的对象，如不存在或解析失败则返回null
     * @author Joker.X
     */
    inline fun <reified T> getObject(key: String): T? {
        val json = getString(key, "")
        return if (json.isNotEmpty()) {
            try {
                Json.decodeFromString<T>(json)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * 存储Set<String>集合
     * 用法示例：MMKVUtils.putStringSet("tags", setOf("tag1", "tag2"))
     *
     * @param key 键
     * @param value 值
     * @author Joker.X
     */
    fun putStringSet(key: String, value: Set<String>?) {
        defaultMMKV.encode(key, value)
    }

    /**
     * 获取Set<String>集合
     * 用法示例：val tags = MMKVUtils.getStringSet("tags")
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 存储的Set<String>，如不存在则返回默认值
     * @author Joker.X
     */
    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()): Set<String> {
        return defaultMMKV.decodeStringSet(key, defaultValue) ?: defaultValue
    }

    /**
     * 判断是否包含指定键
     * 用法示例：if (MMKVUtils.containsKey("username")) { ... }
     *
     * @param key 键
     * @return 是否包含该键
     * @author Joker.X
     */
    fun containsKey(key: String): Boolean {
        return defaultMMKV.containsKey(key)
    }

    /**
     * 移除指定键值对
     * 用法示例：MMKVUtils.remove("username")
     *
     * @param key 键
     * @author Joker.X
     */
    fun remove(key: String) {
        defaultMMKV.removeValueForKey(key)
    }

    /**
     * 移除包含指定前缀的所有键值对
     * 用法示例：MMKVUtils.removeValuesForKeys("user_")
     *
     * @param keyPrefix 键前缀
     * @author Joker.X
     */
    fun removeValuesForKeys(keyPrefix: String) {
        val keys = defaultMMKV.allKeys()
        if (keys != null) {
            val keysToRemove = keys.filter { it.startsWith(keyPrefix) }.toTypedArray()
            defaultMMKV.removeValuesForKeys(keysToRemove)
        }
    }

    /**
     * 清除所有数据
     * 用法示例：MMKVUtils.clearAll()
     *
     * @author Joker.X
     */
    fun clearAll() {
        defaultMMKV.clearAll()
    }

    /**
     * 获取所有键名
     * 用法示例：val allKeys = MMKVUtils.getAllKeys()
     *
     * @return 所有键的集合，如果没有则返回空集合
     * @author Joker.X
     */
    fun getAllKeys(): Set<String> {
        return defaultMMKV.allKeys()?.toSet() ?: emptySet()
    }

    /**
     * 获取MMKV实例大小（字节）
     * 用法示例：val size = MMKVUtils.totalSize()
     *
     * @return MMKV实例占用的大小（字节）
     * @author Joker.X
     */
    fun totalSize(): Long {
        return defaultMMKV.totalSize()
    }

    /**
     * 获取MMKV实例中的条目数量
     * 用法示例：val count = MMKVUtils.count()
     *
     * @return MMKV实例中的条目数量
     * @author Joker.X
     */
    fun count(): Long {
        return defaultMMKV.count()
    }
}