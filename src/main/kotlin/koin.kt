package com.nooblabs

import com.nooblabs.service.*
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