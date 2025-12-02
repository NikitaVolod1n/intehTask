package Data

open class UserRepository {
    private val repo = mutableListOf<User>()

    private val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    protected fun isValidEmail(email: String): Boolean = email.matches(EMAIL_REGEX)

    fun addUser(user: User): ValidationResult {
        val exists = repo.any { it.id == user.id || it.email == user.email }
        return if(!exists){
            repo.add(user)
            repo.sortBy { it.id }
            ValidationResult.Success
        }
        else{
            ValidationResult.UnknownError
        }
    }

    fun getUsers(): List<User>{
        return repo
    }

    fun getUserById(id: Int): User?{
        return repo.firstOrNull { id == it.id }
    }

    fun updateUser(user: User): ValidationResult{
        val index = repo.indexOfFirst { user.id == it.id }
        if(index == -1 || !isValidEmail(user.email) || user.name == ""){
            return ValidationResult.InvalidFormat
        }
        repo[index].name = user.name
        repo[index].email = user.email
        return ValidationResult.Success
    }

    fun deleteUser(id: Int): ValidationResult{
        val user = repo.find { id == it.id }
        if(user == null){
            return ValidationResult.UserNotFound
        }
        repo.remove(user)
        return ValidationResult.Success
    }

    fun getUsersSortedByName(): List<User> = repo.sortedBy { it.name }

    fun getUsersByEmailDomain(domain: String): List<User>{
        val sorted = mutableListOf<User>()
        repo.forEach {
            val ind = it.email.indexOf('@')
            val itDomain = it.email.substring(ind + 1)
            if(itDomain == domain){
                sorted.add(it)
            }
        }
        return sorted
    }
}