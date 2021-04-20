package banking.web.account

import banking.db.account.Account
import banking.db.account.AccountTable
import banking.db.user.UserTable
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

// create account for each user
fun Route.createAccount() {
    route("/createAccount") {
        post {
            val userName = this.context.request.queryParameters["userName"]!!
            val type = this.context.request.queryParameters["type"]!!


            transaction {
                Account.new {
                    this.type = type
                    this.userId = UserTable.select { UserTable.userName eq userName }.single()[UserTable.id].value
                    this.balance = 0.00
                }
            }
            this.context.respond("success")
        }
    }
}

fun Route.getAccountsForUser() {
    route("/sql") {
        get {
            val payeeUserId: UUID = UUID.fromString(this.context.request.queryParameters["payeeUserId"]!!) // user id x 1

            transaction {
                // inner join user and user with userid as key
                // show all available account id first


                (UserTable innerJoin AccountTable)
//                    .slice(UserTable.id, TransactionTable.payee, TransactionTable.payment)
                    .select {UserTable.id eq payeeUserId}
                    .forEach{ println("${it[UserTable.userName]}, ${it[UserTable.name]}, ${it[AccountTable.id]}, ${it[AccountTable.type]}") }
            }
            this.context.respond("success")
        }
    }

}

// ignore for now
// account and user, keep transaction
fun Route.deleteAccount() {
    route("/deleteAccount") {
        delete {
            val userName: String = this.context.request.queryParameters["userName"]!!
            transaction {
                UserTable.deleteWhere { UserTable.userName eq userName }
            }
        }
    }
}