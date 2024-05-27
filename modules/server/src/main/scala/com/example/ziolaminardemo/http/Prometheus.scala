package com.example.ziolaminardemo.http

import zio.*
import sttp.tapir.ztapir.ZServerEndpoint
import sttp.tapir.server.metrics.prometheus.PrometheusMetrics

object Prometheus {
  val metrics: PrometheusMetrics[Task]           = PrometheusMetrics.default[Task]()
  val metricsEndpoint: ZServerEndpoint[Any, Any] = metrics.metricsEndpoint

}
