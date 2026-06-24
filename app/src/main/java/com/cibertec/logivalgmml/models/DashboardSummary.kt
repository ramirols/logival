package com.cibertec.logivalgmml.models

data class DashboardSummary(
    val solicitudesPendientes: Int = 0,
    val vehiculosDentro: Int = 0,
    val ingresosHoy: Int = 0,
    val salidasHoy: Int = 0,
    val incidenciasAbiertas: Int = 0,
    val permanenciaPromedio: Long = 0L
)