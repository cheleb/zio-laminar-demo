package com.example.ziolaminardemo.domain.errors

sealed abstract class ApplicationException(message: String) extends RuntimeException(message)

case class UnauthorizedException(message: String) extends ApplicationException(message)

case class TooYoungException(age: Int) extends ApplicationException(s"Person is too young: $age")

case class InvalidCredentialsException() extends ApplicationException("Invalid credentials")

case class UserAlreadyExistsException() extends ApplicationException("User already exists")
