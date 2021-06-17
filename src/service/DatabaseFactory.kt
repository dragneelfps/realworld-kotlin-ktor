package com.nooblabs.service

import com.nooblabs.models.ArticleComment
import com.nooblabs.models.ArticleTags
import com.nooblabs.models.Articles
import com.nooblabs.models.Comments
import com.nooblabs.models.FavoriteArticle
import com.nooblabs.models.Followings
import com.nooblabs.models.Tags
import com.nooblabs.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.transactions.transaction

interface IDatabaseFactory {
    fun init()

    suspend fun <T> dbQuery(block: () -> T): T

    suspend fun drop()
}

class DatabaseFactory : IDatabaseFactory {

    override fun init() {
        Database.connect(hikari())
        transaction {
            create(Users, Followings, Articles, Tags, ArticleTags, FavoriteArticle, Comments, ArticleComment)

            //NOTE: Insert initial rows if any here
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
//            jdbcUrl = "jdbc:h2:tcp://localhost/~/realworldtest"
            jdbcUrl = "jdbc:h2:mem:~realworldtest"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }
        return HikariDataSource(config)
    }

    override suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }

    override suspend fun drop() {
        dbQuery { drop(Users, Followings, Articles, Tags, ArticleTags, FavoriteArticle, Comments, ArticleComment) }
    }
}