package banking.web.user

import banking.db.user.User
import banking.db.user.UserTable
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

fun Route.createUser() {
    route("/createUser") {
        post {
            // get the customer name from postman
            val userName = this.context.request.queryParameters["userName"]!!
            val name = this.context.request.queryParameters["name"]!!



            transaction {
                User.new {
                    this.userName = userName
                    this.name = name
                }
            }
            this.context.respond("success")
        }
    }
}

// ignore for now
// can update user name or name
fun Route.updateName() {
    route("/updateName") {
        post {
            val oldName: String = this.context.request.queryParameters["oldName"]!!
            val currentUserName: String = this.context.request.queryParameters["current_userName"]!!
            val newName: String = this.context.request.queryParameters["newName"]!!

//            val oldUserName: String = this.context.request.queryParameters["old_userName"]!!
//            val currentName: String = this.context.request.queryParameters["current_name"]!!
//            val newUserName : String = this.context.request.queryParameters["new_userName"]!!

            transaction {
                UserTable.update({ (UserTable.name eq oldName) and (UserTable.userName eq currentUserName) }) {
                    it[name] = newName
                }

//                AccountTable.update({(AccountTable.userName eq oldUserName) and (AccountTable.name eq currentName)}){
//                    it[userName] = newUserName
//                }
            }
            this.context.respond("success")
        }

    }
}