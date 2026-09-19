package com.algorithmlearning.shared.library.data

import com.algorithmlearning.shared.library.Dashboard
import com.algorithmlearning.shared.library.DashboardRemote
import io.ktor.client.call.body
import io.ktor.client.request.get

class KtorDashboardRemote(private val api: ApiClient) : DashboardRemote {

    override suspend fun get(): Dashboard = api.decode {
        api.authorized { token ->
            api.client.get("${api.root}/dashboard") { bearer(token) }
        }.body<DashboardDto>().toDomain()
    }
}
