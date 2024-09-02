package com.example.ziolaminardemo.login

import sttp.tapir.Schema

final case class LoginPassword(login: String, password: String) derives zio.json.JsonCodec, Schema
