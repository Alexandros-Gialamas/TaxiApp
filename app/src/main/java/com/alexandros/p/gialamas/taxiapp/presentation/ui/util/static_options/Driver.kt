package com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options

enum class Driver(val driverId: Int?, val driverName: String, val minKm: Double) {
    DRIVER_ALL(null, "All", 0.000),
    DRIVER_1(1, "Homer Simpson", 1000.000),
    DRIVER_2(2, "Dominic Toretto", 5000.000),
    DRIVER_3(3, "James Bond", 10000.000),
    DRIVER_4(4, "Fake Driver Test", 20000.000)
}