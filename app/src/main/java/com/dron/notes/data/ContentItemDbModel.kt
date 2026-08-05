package com.dron.notes.data

import kotlinx.serialization.Serializable


@Serializable
sealed interface ContentItemDbModel {
    @Serializable
    data class Text(val content: String): ContentItemDbModel
    @Serializable
    data class Image(val url: String): ContentItemDbModel
}