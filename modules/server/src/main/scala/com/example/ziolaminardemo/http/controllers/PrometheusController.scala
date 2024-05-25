package com.example.ziolaminardemo.http.controllers

import zio.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics

class PrometheusController extends BaseController {
  val routes: List[ZServerEndpoint[Any, Any]] = List(PrometheusController.metricsEndpoint)
}

object PrometheusController {
  val prometheusMetrics: PrometheusMetrics[Task] = PrometheusMetrics.default[Task]()
  val metricsEndpoint: ZServerEndpoint[Any, Any] = prometheusMetrics.metricsEndpoint

  val makeZIO = ZIO.succeed(PrometheusController())

}
