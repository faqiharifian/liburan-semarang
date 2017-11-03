package com.arifian.training.liburansemarang.models.remote.responses.route

import com.google.gson.annotations.SerializedName

data class OverviewPolyline(

	@field:SerializedName("points")
	val points: String? = null
)