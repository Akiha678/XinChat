package com.seanchen.xinchat.core.navigation

interface NavigationResultKey<T> {
    /**
     * 底层用于结果分发的字符串 key。
     *
     * 默认实现使用 Key 对象自身的完全限定类名，保证全局唯一且无需手写字符串。
     */
    val key: String
        get() = this::class.java.name

    /**
     * 将结果对象序列化为可分发的底层类型。
     *
     * 默认实现为透传（即直接存储原始对象），适用于 Boolean、Int、String 等基础类型。
     * 复杂类型可以在具体的 Key 中重写此方法，例如序列化为 JSON 字符串。
     */
    fun serialize(value: T): Any = value as Any

    /**
     * 从结果分发流中还原结果对象。
     *
     * 默认实现为简单强转，复杂类型的 Key 需要重写以配合 [serialize]。
     */
    fun deserialize(raw: Any): T = raw as T
}
