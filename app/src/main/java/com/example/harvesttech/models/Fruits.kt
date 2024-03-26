package com.example.harvesttech.models

data class Fruits(
    var jenis: String = "",
    var buah: String = "",
    var imgUri: String? = "", // Field untuk menyimpan URL gambar
) {
    var key: String? = null
}
