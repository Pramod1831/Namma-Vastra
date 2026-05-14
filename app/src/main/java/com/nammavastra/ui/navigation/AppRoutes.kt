package com.nammavastra.ui.navigation

enum class BottomDestination(val route: String, val label: String) {
    Trend("trend", "Trend"),
    Gallery("gallery", "Gallery"),
    Calculator("calculator", "Calc"),
    Story("story", "Story")
}

object AppRoutes {
    const val Auth = "auth"
    const val SareeDetail = "saree/{sareeId}"
    const val Upload = "upload"
    const val StorySubmission = "story-submission"
    const val Admin = "admin"
    const val Cart = "cart"
    const val Account = "account"

    fun sareeDetail(sareeId: String) = "saree/$sareeId"
}
