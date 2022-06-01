package com.ashehata.instabugtask.singelton

import com.ashehata.instabugtask.home.HomeRepository
import com.ashehata.instabugtask.home.HomeUseCase

object Singleton {

    fun getHomeUseCase(): HomeUseCase {
        return HomeUseCase(getHomeRepo())
    }

    private fun getHomeRepo() = HomeRepository()

}