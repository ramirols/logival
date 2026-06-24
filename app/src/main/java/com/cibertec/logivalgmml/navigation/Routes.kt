package com.cibertec.logivalgmml.navigation

object Routes {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"

    const val VEHICLE_LIST = "vehicle_list"
    const val VEHICLE_FORM = "vehicle_form"

    const val REQUEST_LIST = "request_list"
    const val REQUEST_FORM = "request_form"
    const val REQUEST_DETAIL = "request_detail"
    const val REQUEST_DETAIL_ROUTE = "request_detail/{requestId}"

    const val QR_ACCESS = "qr_access"
    const val QR_ACCESS_ROUTE = "qr_access/{requestId}"
    const val QR_SCANNER = "qr_scanner"

    const val INCIDENT_LIST = "incident_list"
    const val INCIDENT_FORM = "incident_form"

    const val DASHBOARD = "dashboard"
    const val PROFILE = "profile"

    fun requestDetail(requestId: String) = "$REQUEST_DETAIL/$requestId"
    fun qrAccess(requestId: String) = "$QR_ACCESS/$requestId"
}