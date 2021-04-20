package banking.web.transaction

import banking.db.account.AccountTable
import banking.db.transaction.PayeeTransaction
import banking.db.transaction.ReceiveTransaction
import banking.db.user.UserTable
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.text.SimpleDateFormat
import java.util.*

// by username/email
fun Route.depositWithdrawByName() {
    route("/depositWithdrawByName") {
        post {
            val userName: String = this.context.request.queryParameters["userName"]!!
            val movement: String = this.context.request.queryParameters["movement"]!!
            val type: String = this.context.request.queryParameters["type"]!!


            transaction {

                //val current = AccountTable.name eq userName2
                val tempID = UserTable.select { UserTable.userName eq userName }.single()[UserTable.id].value

                AccountTable.update({ (AccountTable.userId eq tempID) and (AccountTable.type eq type) }) {
                    with(SqlExpressionBuilder) {
                        it.update(AccountTable.balance, AccountTable.balance + movement.toDouble())
                    }
                }
            }
            this.context.respond("success")
        }
    }
}

fun Route.paymentId() {
    route("/paymentId") {
        post {
            val payee: UUID = UUID.fromString(this.context.request.queryParameters["payee"]!!) // user id ->? not unique
            val receive: UUID = UUID.fromString(this.context.request.queryParameters["receive"]!!)
            val payment = this.context.request.queryParameters["payment"]!!
            val payeeType = this.context.request.queryParameters["payeeType"]!!
            val receiveType = this.context.request.queryParameters["receiveType"]!!

            transaction {
                AccountTable.update({(AccountTable.userId eq payee) and (AccountTable.type eq payeeType)}) {
                    with(SqlExpressionBuilder) {
                        it.update(AccountTable.balance, AccountTable.balance - payment.toDouble())
                    }
                }
                AccountTable.update({(AccountTable.userId eq receive) and (AccountTable.type eq receiveType)}) {
                    with(SqlExpressionBuilder) {
                        it.update(AccountTable.balance, AccountTable.balance + payment.toDouble())
                    }
                }

                val time: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                val key : UUID = UUID.randomUUID()

                PayeeTransaction.new{
                    this.payeeId = AccountTable.select {(AccountTable.userId eq payee) and (AccountTable.type eq payeeType)}.single()[AccountTable.id].value
                    this.payment = -1*payment.toDouble() // ------  negative value here
                    this.time = time
                    this.key = key
                }

                ReceiveTransaction.new{
                    this.receiveId = AccountTable.select {(AccountTable.userId eq receive) and (AccountTable.type eq receiveType)}.single()[AccountTable.id].value
                    this.payment = payment.toDouble() // ------  positive value here
                    this.time = time
                    this.key = key
                }
            }
            this.context.respond("success")
        }
    }
}

fun Route.paymentName() {
    route("/paymentName") {
        post {
            val payee: String = this.context.request.queryParameters["payee"]!!
            val receive: String = this.context.request.queryParameters["receive"]!!
            val payment = this.context.request.queryParameters["payment"]!!
            val payeeType = this.context.request.queryParameters["payeeType"]!!
            val receiveType = this.context.request.queryParameters["receiveType"]!!


            transaction {
                val payeeUserId = UserTable.select { UserTable.userName eq payee }.single()[UserTable.id].value
                val receiveUserId = UserTable.select { UserTable.userName eq receive }.single()[UserTable.id].value

                AccountTable.update({ (AccountTable.userId eq payeeUserId) and (AccountTable.type eq payeeType) }) {
                    with(SqlExpressionBuilder) {
                        it.update(AccountTable.balance, AccountTable.balance - payment.toDouble())
                    }
                }
                AccountTable.update({ (AccountTable.userId eq receiveUserId) and (AccountTable.type eq receiveType) }) {
                    with(SqlExpressionBuilder) {
                        it.update(AccountTable.balance, AccountTable.balance + payment.toDouble())
                    }
                }

                val time: String = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
                val key : UUID = UUID.randomUUID()

                PayeeTransaction.new {
                    this.payeeId = UserTable.select { (AccountTable.userId eq payeeUserId) and (AccountTable.type eq payeeType)}.single()[AccountTable.id].value
                    this.payment = payment.toDouble()
                    this.time = time
                    this.key = key
                }

                ReceiveTransaction.new {
                    this.receiveId = UserTable.select { (AccountTable.userId eq receiveUserId) and (AccountTable.type eq receiveType)}.single()[AccountTable.id].value
                    this.payment = payment.toDouble()
                    this.time = time
                    this.key = key
                }
            }
            this.context.respond("success")
        }
    }
}