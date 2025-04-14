package com.company.eve.ui.home.kotlintest;

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Camera(
    val province: String,
    val city: String,
    val addr: String,
    val lat: Double,
    val lng: Double,
    val statNm: String,
    val place: String,
    val space: String,
    val type: String,
    val typeS: String,
    val bnm: String,
    val busiNm: String,
    val output: String,
    val Ctype: String,
    val limit: String,
    val chgerId: Int
) : ClusterItem {
    override fun getPosition(): LatLng {
        return LatLng(lat, lng)
    }

    override fun getTitle(): String? {
        return statNm
    }

    override fun getSnippet(): String? {
        return "$lat, $lng"
    }
}