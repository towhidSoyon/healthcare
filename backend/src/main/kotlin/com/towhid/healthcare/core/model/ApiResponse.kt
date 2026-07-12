package com.towhid.healthcare.core.model

import kotlinx.serialization.Serializable

@Serializable
sealed class ApiResponse<out T>

@Serializable
data class SuccessResponse<out T>(
    val success: Boolean = true,
    val message: String? = null,
    val data: T? = null
) : ApiResponse<T>()

@Serializable
data class ErrorResponse(
    val success: Boolean = false,
    val message: String,
    val errorCode: String? = null
) : ApiResponse<Nothing>()

@Serializable
data class HealthResponse(
    val success: Boolean = true,
    val message: String = "Backend is running",
    val version: String = "1.0.0"
)
