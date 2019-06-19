package com.nooblabs.util

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

class MissingParameter(val params: Set<String>) : RuntimeException()

class UserExists : RuntimeException()

class UserDoesNotExists : RuntimeException()

class ArticleDoesNotExist(val slug: String) : RuntimeException()
