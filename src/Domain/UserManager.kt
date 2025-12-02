package Domain

import Data.User
import Data.UserRepository
import Data.ValidationResult

class UserManager: UserRepository() {

    fun createUser(name: String, email: String): User?{
        val repo = getUsers()

        if(!isValidEmail(email) || name == "" || repo.find { it.email == email } != null){
            return null
        }

        var id = -1
        if(repo.isEmpty()) id = 1
        if(repo.size == 1) id = 2

        for(i in 0..<repo.size - 1){
            if(repo[i].id + 1 != repo[i+1].id){
                id = i+2
                break
            }
        }
        if(id == -1){
            id = repo.last().id + 1
        }
        return User(id, name, email)
    }

    fun printResultAsText(result: ValidationResult){
        when(result){
            ValidationResult.Success -> println("Операция выполнена успешно")
            ValidationResult.UserNotFound -> println("Пользователь не найден")
            ValidationResult.InvalidFormat -> println("Неверный формат ввода")
            ValidationResult.UnknownError -> println("Неизвестная ошибка")
        }
    }
}