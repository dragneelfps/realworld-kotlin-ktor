package com.nooblabs

import com.nooblabs.service.ArticleService
import com.nooblabs.service.AuthService
import com.nooblabs.service.CommentService
import com.nooblabs.service.DatabaseFactory
import com.nooblabs.service.IArticleService
import com.nooblabs.service.IAuthService
import com.nooblabs.service.ICommentService
import com.nooblabs.service.IDatabaseFactory
import com.nooblabs.service.IProfileService
import com.nooblabs.service.ProfileService
import org.koin.dsl.module

val serviceKoinModule = module {
    single<IArticleService> { ArticleService(get()) }
    single<IAuthService> { AuthService(get()) }
    single<ICommentService> { CommentService(get()) }
    single<IProfileService> { ProfileService(get()) }
}

val databaseKoinModule = module {
    single<IDatabaseFactory> { DatabaseFactory() }
}