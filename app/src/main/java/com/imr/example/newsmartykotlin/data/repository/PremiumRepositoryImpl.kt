package com.imr.example.newsmartykotlin.data.repository

import com.imr.example.newsmartykotlin.core.utils.BillingManager
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.domain.model.PremiumLayoutType
import com.imr.example.newsmartykotlin.domain.model.PremiumPlan
import com.imr.example.newsmartykotlin.domain.repository.PremiumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PremiumRepositoryImpl(
    private val prefs: DataStorePrefs
) : PremiumRepository {

    override fun observePlans(): Flow<List<PremiumPlan>> {

        return combine(
            prefs.getMonthlyPrice(),
            prefs.getYearlyPrice()
        ) { monthlyPrice, yearlyPrice ->

            listOf(

                PremiumPlan(
                    productId = BillingManager.MONTHLY_SUBSCRIPTION_ID,
                    title = "Month",
                    price = monthlyPrice.ifEmpty { "Loading..." },
                    billingPeriod = "/month",
                    isSelected = true
                ),

                PremiumPlan(
                    productId = BillingManager.YEARLY_SUBSCRIPTION_ID,
                    title = "Year",
                    price = yearlyPrice.ifEmpty { "Loading..." },
                    billingPeriod = "/year"
                )
            )
        }
    }
    override fun observePremiumLayout(): Flow<PremiumLayoutType> {

        return prefs.getPremiumLayoutType().map { value ->

            when (value) {
                "single_card" -> PremiumLayoutType.SINGLE_CARD
                else -> PremiumLayoutType.TWO_CARD
            }
        }
    }


    override fun observePurchaseState(): Flow<Boolean> {
        return prefs.getIsPurchased()
    }

    override suspend fun fetchBillingProducts() {
        /*
         Here call BillingClient.
         After getting ProductDetails, save values:

         prefs.setMonthlyPrice(productDetailsPrice)
         prefs.setWeeklyPrice(productDetailsPrice)
         prefs.setIsMonthlyTrial(true/false)
        */
    }

    override suspend fun purchase(productId: String) {
        /*
         Launch billing flow from Activity.
         On purchase success:
         prefs.setIsPurchased(true)
        */
    }
}