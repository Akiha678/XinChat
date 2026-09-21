package com.seanchen.xinchat.feature.contact.state

import com.seanchen.xinchat.feature.contact.model.ContactUserModel

data class ContactUiState(
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val sortByOnline: Boolean = false,
    val friends: List<ContactUserUiState> = emptyList(),
    val searchResults: List<ContactUserUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val isSendingFriendRequest: Boolean = false,
    val errorMessage: String? = null,
) {
    /**
     * 当前展示的联系人列表（支持搜索过滤与排序）
     */
    val displayedFriends: List<ContactUserUiState>
        get() {
            val filtered = if (searchQuery.isBlank()) {
                friends
            } else {
                friends.filter {
                    it.displayName.contains(searchQuery, ignoreCase = true) ||
                        it.username.contains(searchQuery, ignoreCase = true) ||
                        it.email.contains(searchQuery, ignoreCase = true)
                }
            }
            return if (sortByOnline) {
                filtered.sortedWith(
                    compareByDescending<ContactUserUiState> { it.isOnline }
                        .thenBy { if (it.sectionLetter == "#") "Z" + 1 else it.sectionLetter }
                        .thenBy { it.displayName }
                )
            } else {
                filtered.sortedWith(
                    compareBy<ContactUserUiState> {
                        if (it.sectionLetter == "#") "Z" + 1 else it.sectionLetter
                    }.thenBy { it.displayName }
                )
            }
        }

    /**
     * 按字母分组的联系人映射
     */
    val groupedFriends: Map<String, List<ContactUserUiState>>
        get() = displayedFriends.groupBy { it.sectionLetter }
}

data class ContactUserUiState(
    val id: Long,
    val displayName: String,
    val username: String = "",
    val email: String = "",
    val avatarColor: Int = 0,
    val isOnline: Boolean = false,
    val lastSeenText: String = "最近上线",
    val sectionLetter: String = "#"
)

fun ContactUserModel.toUiState(
    isOnline: Boolean = false,
    lastSeenText: String = if (isOnline) "在线" else "最近上线"
): ContactUserUiState {
    val letter = extractSectionLetter(displayName.ifBlank { username })
    return ContactUserUiState(
        id = id,
        displayName = displayName.ifBlank { username.ifBlank { email } },
        username = username,
        email = email,
        avatarColor = avatarColor,
        isOnline = isOnline,
        lastSeenText = lastSeenText,
        sectionLetter = letter
    )
}

/**
 * 提取首字母分组（A-Z 或 #）
 */
fun extractSectionLetter(name: String): String {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) return "#"
    val firstChar = trimmed.first()
    return when {
        firstChar in 'A'..'Z' -> firstChar.toString()
        firstChar in 'a'..'z' -> firstChar.uppercaseChar().toString()
        firstChar.isChinese() -> getPinyinFirstLetter(firstChar).toString()
        else -> "#"
    }
}

private fun Char.isChinese(): Boolean = this in '\u4E00'..'\u9FA5'

/**
 * 基于常用字符区间提取中文字符拼音首字母
 */
private fun getPinyinFirstLetter(ch: Char): Char {
    val secPosValueList = intArrayOf(
        1601, 1637, 1833, 2078, 2274, 2302, 2433, 2594, 2787,
        3106, 3212, 3472, 3635, 3722, 3730, 3858, 4027, 4086,
        4390, 4558, 4684, 4925, 5249, 5600
    )
    val firstLetter = charArrayOf(
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
        'T', 'W', 'X', 'Y', 'Z'
    )
    return try {
        val bytes = ch.toString().toByteArray(charset("GB2312"))
        if (bytes.size < 2) return '#'
        val sector = bytes[0].toInt() + 256
        val position = bytes[1].toInt() + 256
        val secPosValue = (sector - 160) * 100 + position - 160
        for (i in 0 until 23) {
            if (secPosValue >= secPosValueList[i] && secPosValue < secPosValueList[i + 1]) {
                return firstLetter[i]
            }
        }
        '#'
    } catch (_: Exception) {
        '#'
    }
}

sealed class ContactState {
    data object Loading : ContactState()

    data object Success : ContactState()

    data class Error(
        val message: String
    ) : ContactState()
}