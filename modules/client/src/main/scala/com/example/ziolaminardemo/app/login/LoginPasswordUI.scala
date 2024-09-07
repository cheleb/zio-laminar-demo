package com.example.ziolaminardemo.app.login

import dev.cheleb.scalamigen.NoPanel

import com.example.ziolaminardemo.login.LoginPassword
import com.example.ziolaminardemo.domain.Password

@NoPanel
final case class LoginPasswordUI(login: String, password: Password):
  def http: LoginPassword = LoginPassword(login, password)
