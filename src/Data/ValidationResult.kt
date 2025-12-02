package Data

sealed class ValidationResult {
    object Success : ValidationResult()
    object UserNotFound : ValidationResult()
    object InvalidFormat : ValidationResult()
    object UnknownError : ValidationResult()
}