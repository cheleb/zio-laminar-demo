package com.example.ziolaminardemo.app.login

import dev.cheleb.scalamigen.NoPanel

import com.example.ziolaminardemo.login.LoginPassword

@NoPanel
final case class LoginPasswordUI(login: String, password: String):
  def http: LoginPassword = LoginPassword(login, password)
