import Data.User
import Data.UserRepository
import Data.ValidationResult
import Domain.UserManager
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

@DisplayName("Тесты UserRepository")
class UserRepositoryTest {

    private lateinit var repo: UserRepository
    private lateinit var create: UserManager
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User
    private lateinit var user4: User

    @BeforeEach
    fun setUp() {
        repo = UserRepository()
        create = UserManager()
        user1 = User(1, "Никита", "Nikita@gmail.com")
        user2 = User(2, "Егор", "Egor@yandex.ru")
        user3 = User(3, "Андрей", "Andrey@yandex.ru")
        user4 = User(4, "Женя", "Jenya@yandex.ru")
    }

    @Nested
    @DisplayName("Тесты добавления пользователей")
    inner class AddUserTests {

        @Test
        @DisplayName("Успешное добавление нового пользователя")
        fun addUserSuccess() {
            val result = repo.addUser(user1)
            assertEquals(ValidationResult.Success, result)
            assertEquals(1, repo.getUsers().size)
            assertEquals(user1, repo.getUserById(1))
        }

        @Test
        @DisplayName("Отказ при дублирующемся ID")
        fun addUserDuplicateIdFails() {
            repo.addUser(user1)
            val duplicate = user1.copy(name = "Дубликат")
            val result = repo.addUser(duplicate)
            assertEquals(ValidationResult.UnknownError, result)
            assertEquals(1, repo.getUsers().size)
        }

        @Test
        @DisplayName("Отказ при дублирующемся email")
        fun addUserDuplicateEmailFails() {
            repo.addUser(user1)
            val duplicateEmail = user1.copy(id = 3, name = "Другой")
            val result = repo.addUser(duplicateEmail)
            assertEquals(ValidationResult.UnknownError, result)
            assertEquals(1, repo.getUsers().size)
        }

        @Test
        @DisplayName("Тест промежуточной вставки")
        fun adduserBetweenUsers(){
            repo.addUser(user1)
            repo.addUser(user3)
            repo.addUser(user2)
            val result = repo.addUser(user4)
            assertEquals(ValidationResult.Success, result)
            assertEquals(4, repo.getUsers().size)
        }
    }

    @Nested
    @DisplayName("Тесты обновления пользователей")
    inner class UpdateUserTests {

        @Test
        @DisplayName("Успешное обновление email")
        fun updateUserSuccess() {
            repo.addUser(user1)
            val updated = user1.copy(email = "ivan@newmail.com")
            val result = repo.updateUser(updated)
            assertEquals(ValidationResult.Success, result)
            val user = repo.getUserById(1)
            assertEquals("ivan@newmail.com", user?.email)
        }

        @Test
        @DisplayName("Отказ при невалидном email")
        fun updateUserInvalidEmailFails() {
            repo.addUser(user1)
            val invalid = user1.copy(email = "invalid")
            val result = repo.updateUser(invalid)
            assertEquals(ValidationResult.InvalidFormat, result)
            val user = repo.getUserById(1)
            assertEquals("Nikita@gmail.com", user?.email)
        }

        @Test
        @DisplayName("Отказ при несуществующем пользователе")
        fun updateUserNotFoundFails() {
            val result = repo.updateUser(user1)
            assertEquals(ValidationResult.InvalidFormat, result)
            assertEquals(0, repo.getUsers().size)
        }
    }

    @Nested
    @DisplayName("Тесты удаления пользователей")
    inner class DeleteUserTests {

        @Test
        @DisplayName("Успешное удаление")
        fun deleteUserSuccess() {
            repo.addUser(user1)
            val result = repo.deleteUser(1)
            assertEquals(ValidationResult.Success, result)
            assertNull(repo.getUserById(1))
            assertEquals(0, repo.getUsers().size)
        }

        @Test
        @DisplayName("Отказ при несуществующем пользователе")
        fun deleteUserNotFound() {
            val result = repo.deleteUser(999)
            assertEquals(ValidationResult.UserNotFound, result)
            assertEquals(0, repo.getUsers().size)
        }
    }

    @Nested
    @DisplayName("Тесты поиска")
    inner class SearchTests {

        @Test
        @DisplayName("Поиск существующего пользователя")
        fun getUserByIdFound() {
            repo.addUser(user1)
            repo.addUser(user2)
            assertEquals(user1, repo.getUserById(1))
            assertEquals(user2, repo.getUserById(2))
        }

        @Test
        @DisplayName("Поиск несуществующего пользователя")
        fun getUserByIdNotFound() {
            assertNull(repo.getUserById(999))
        }
    }

    @Nested
    @DisplayName("Тесты сортировки и фильтрации")
    inner class SortFilterTests {

        @Test
        @DisplayName("Сортировка по имени работает")
        fun getUsersSortedByNameWorks() {
            repo.addUser(user2)
            repo.addUser(user1)
            val sorted = repo.getUsersSortedByName()
            assertEquals("Егор", sorted[0].name)
            assertEquals("Никита", sorted[1].name)
        }

        @Test
        @DisplayName("Фильтрация по домену работает")
        fun getUsersByEmailDomainWorks() {
            repo.addUser(user1)
            repo.addUser(user2)
            val gmailUsers = repo.getUsersByEmailDomain("gmail.com")
            assertEquals(1, gmailUsers.size)
            assertEquals("Nikita@gmail.com", gmailUsers[0].email)
        }
    }
}
