package banking.db.account

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import java.util.*

object AccountTable : UUIDTable(columnName = "account_id") {
    val userId = uuid("user_id") // user_id
    val type = varchar("account type", 20) // ----------restrain it to several types
    val balance = double("balance")
}

class Account(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Account>(AccountTable)

    var userId by AccountTable.userId
    var type by AccountTable.type
    var balance by AccountTable.balance
}