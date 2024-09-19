package com.example.ziolaminardemo.domain

import zio.json.JsonCodec

import dev.cheleb.ziojwt.WithToken
import sttp.model.Uri

final case class UserToken(issuer: Uri, id: Long, email: String, token: String, expiration: Long) extends WithToken
    derives JsonCodec

object UserToken:
  given JsonCodec[Uri] = JsonCodec.string.transformOrFail(Uri.parse(_), _.toString)
