package com.example.ziolaminardemo.app

import dev.cheleb.scalamigen.ui5.UI5WidgetFactory
import dev.cheleb.scalamigen.WidgetFactory

import dev.cheleb.ziolaminartapir.Session

import dev.cheleb.ziolaminartapir.SessionLive

import com.example.ziolaminardemo.domain.UserToken

given f: WidgetFactory = UI5WidgetFactory

given session: Session[UserToken] = SessionLive[UserToken]
