package com.example.ziolaminardemo.app.demos.scalariform

import dev.cheleb.scalamigen.ui5.UI5WidgetFactory
import dev.cheleb.scalamigen.WidgetFactory
import dev.cheleb.scalamigen.Defaultable

import com.example.ziolaminardemo.domain.*

given f: WidgetFactory = UI5WidgetFactory

given Defaultable[Cat] with
  def default = Cat("")

given Defaultable[Dog] with
  def default = Dog("", 1)
