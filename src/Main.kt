package App

import Data.ValidationResult
import Domain.UserManager

fun main() {
    val manager = UserManager()
    var running = true

    while (running) {
        println("=== Меню пользователей ===")
        println("1. Добавить пользователя")
        println("2. Показать всех пользователей")
        println("3. Показать пользователей, отсортированных по имени")
        println("4. Найти пользователя по id")
        println("5. Обновить email пользователя")
        println("6. Удалить пользователя")
        println("7. Показать пользователей по домену email")
        println("0. Выход")
        print("Выберите действие: ")

        when (readLine()?.trim()) {
            "1" -> {
                print("Введите имя: ")
                val name = readLine()?.trim().orEmpty()

                print("Введите email: ")
                val email = readLine()?.trim().orEmpty()

                val user = manager.createUser(name, email)
                if (user == null) {
                    println("Ошибка: перепроверьте имя и email, где-то ошибка")
                } else {
                    val res = manager.addUser(user)
                    manager.printResultAsText(res)
                }
            }

            "2" -> {
                val users = manager.getUsers()
                if (users.isEmpty()) {
                    println("Пользователей нет")
                } else {
                    println("Пользователи:")
                    users.forEach {
                        println("ID: ${it.id}, Имя: ${it.name}, Email: ${it.email}")
                    }
                }
            }

            "3" -> {
                val users = manager.getUsersSortedByName()
                if (users.isEmpty()) {
                    println("Пользователей нет")
                } else {
                    println("Пользователи (отсортированы по имени):")
                    users.forEach {
                        println("ID: ${it.id}, Имя: ${it.name}, Email: ${it.email}")
                    }
                }
            }

            "4" -> {
                print("Введите id: ")
                val id = readLine()?.toIntOrNull()
                if (id == null) {
                    println("Некорректный id")
                } else {
                    val user = manager.getUserById(id)
                    if (user == null) {
                        manager.printResultAsText(ValidationResult.UserNotFound)
                    } else {
                        println("Найден: ID: ${user.id}, Имя: ${user.name}, Email: ${user.email}")
                    }
                }
            }

            "5" -> {
                print("Введите id пользователя для обновления: ")
                val id = readLine()?.toIntOrNull()
                if (id == null) {
                    println("Некорректный id")
                } else {
                    val user = manager.getUserById(id)
                    if (user == null) {
                        manager.printResultAsText(ValidationResult.UserNotFound)
                    } else {
                        println("Введите новое имя")
                        val newName = readLine()?.trim().orEmpty()
                        print("Введите новый email: ")
                        val newEmail = readLine()?.trim().orEmpty()
                        val updated = user.copy(name = newName, email = newEmail)
                        val res = manager.updateUser(updated)
                        manager.printResultAsText(res)
                    }
                }
            }

            "6" -> {
                print("Введите id для удаления: ")
                val id = readLine()?.toIntOrNull()
                if (id == null) {
                    println("Некорректный id")
                } else {
                    val res = manager.deleteUser(id)
                    manager.printResultAsText(res)
                }
            }

            "7" -> {
                print("Введите домен (например, gmail.com): ")
                val domain = readLine()?.trim().orEmpty()
                val users = manager.getUsersByEmailDomain(domain)
                if (users.isEmpty()) {
                    println("Пользователей с таким доменом нет")
                } else {
                    println("Пользователи с доменом $domain:")
                    users.forEach {
                        println("ID: ${it.id}, Имя: ${it.name}, Email: ${it.email}")
                    }
                }
            }

            "0" -> {
                running = false
                println("Выход...")
            }

            else -> println("Неверный выбор")
        }

        println()
    }
}
