package banking.app


import banking.*
import banking.db.account.AccountTable
import banking.db.transaction.PayeeTransactionTable
import banking.db.transaction.ReceiveTransactionTable
import banking.db.user.UserTable
import banking.web.account.createAccount
import banking.web.account.deleteAccount
import banking.web.account.getAccountsForUser
import banking.web.transaction.depositWithdrawByName
import banking.web.transaction.paymentId
import banking.web.transaction.paymentName
import banking.web.user.createUser
import banking.web.user.updateName
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import org.slf4j.event.Level

// entry point
fun main(args:Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

// call individual functions
fun Application.module(){
    install(CallLogging) { level = Level.INFO }
    install(ContentNegotiation) {
        json()
    }

    // connect to sql
    Database.connect("jdbc:postgresql://localhost:5432/postgres?user=postgres&password=yoyo")
    // , driver = "org.postgresql.Driver"

    // create both tables
    transaction {
        SchemaUtils.create(UserTable)
        SchemaUtils.create(AccountTable)
        SchemaUtils.create(PayeeTransactionTable)
        SchemaUtils.create(ReceiveTransactionTable)
    }


    routing {
        createUser()
        createAccount()
        depositWithdrawByName()
        deleteAccount()
        paymentId()
        paymentName()
        updateName()
        getAccountsForUser()
    }
}

// val log = LoggerFactory.getLogger(Application::class.java)




