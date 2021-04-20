package banking.db.user

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

// summary table
object UserTable : UUIDTable(columnName = "user_id") {
    val name = varchar("name", 20)
    val userName = varchar("user_name", 50).uniqueIndex()
}

// summary entry
class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(UserTable)

    var name by UserTable.name
    var userName by UserTable.userName
}