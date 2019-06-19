package com.nooblabs.util

class AuthenticationException : RuntimeException()
class AuthorizationException : RuntimeException()

class MissingParameter(val param: String) : RuntimeException()
