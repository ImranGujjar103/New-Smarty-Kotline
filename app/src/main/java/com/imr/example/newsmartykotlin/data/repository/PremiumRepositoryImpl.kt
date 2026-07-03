package com.imr.example.newsmartykotlin.data.repository

import android.util.Log
import com.imr.example.newsmartykotlin.core.utils.BillingManager
import com.imr.example.newsmartykotlin.core.utils.DataStorePrefs
import com.imr.example.newsmartykotlin.domain.model.PremiumLayoutType
import com.imr.example.newsmartykotlin.domain.model.PremiumPlan
import com.imr.example.newsmartykotlin.domain.repository.PremiumRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class PremiumRepositoryImpl(
    private val prefs: DataStorePrefs,
    private val billingManager: BillingManager
) : PremiumRepository {

    override fun observePlans(): Flow<List<PremiumPlan>> {
        Log.d("PremiumRepository", "observePlans called")
        val flows: List<Flow<Any>> = listOf(
            prefs.getWeeklyPrice(),
            prefs.getIsWeeklyTrial(),
            prefs.getWeeklyTrialInfo(),
            prefs.getWeeklyTrialInfoAfter(),
            prefs.getMonthlyPrice(),
            prefs.getIsMonthlyTrial(),
            prefs.getMonthlyTrialInfo(),
            prefs.getMonthlyTrialInfoAfter(),
            prefs.getYearlyPrice(),
            prefs.getIsYearlyTrial(),
            prefs.getYearlyTrialInfo(),
            prefs.getYearlyTrialInfoAfter()
        )

        return combine(flows) { array ->
            val weeklyPrice = array[0] as String
            val isWeeklyTrial = array[1] as Boolean
            val weeklyTrialInfo = array[2] as String
            val weeklyTrialInfoAfter = array[3] as String
            val monthlyPrice = array[4] as String
            val isMonthlyTrial = array[5] as Boolean
            val monthlyTrialInfo = array[6] as String
            val monthlyTrialInfoAfter = array[7] as String
            val yearlyPrice = array[8] as String
            val isYearlyTrial = array[9] as Boolean
            val yearlyTrialInfo = array[10] as String
            val yearlyTrialInfoAfter = array[11] as String

            Log.d("PremiumRepository", "observePlans: weeklyPrice=$weeklyPrice, monthlyPrice=$monthlyPrice, yearlyPrice=$yearlyPrice")

            listOf(
                PremiumPlan(
                    productId = BillingManager.WEEKLY_SUBSCRIPTION_ID,
                    title = "Weekly",
                    price = if (weeklyPrice.isEmpty()) "Loading..." else weeklyPrice,
                    billingPeriod = "/Week",
                    isSelected = false,
                    hasTrial = isWeeklyTrial,
                    trialInfo = weeklyTrialInfo,
                    trialInfoAfter = weeklyTrialInfoAfter
                ),
                PremiumPlan(
                    productId = BillingManager.MONTHLY_SUBSCRIPTION_ID,
                    title = "Monthly",
                    price = if (monthlyPrice.isEmpty()) "Loading..." else monthlyPrice,
                    billingPeriod = "/Month",
                    isSelected = true,
                    hasTrial = isMonthlyTrial,
                    trialInfo = monthlyTrialInfo,
                    trialInfoAfter = monthlyTrialInfoAfter
                ),
                PremiumPlan(
                    productId = BillingManager.YEARLY_SUBSCRIPTION_ID,
                    title = "Yearly",
                    price = if (yearlyPrice.isEmpty()) "Loading..." else yearlyPrice,
                    billingPeriod = "/year",
                    isSelected = false,
                    hasTrial = isYearlyTrial,
                    trialInfo = yearlyTrialInfo,
                    trialInfoAfter = yearlyTrialInfoAfter
                )
            )
        }
    }
    override fun observePremiumLayout(): Flow<PremiumLayoutType> {

        return prefs.getPremiumLayoutType().map { value ->

            when (value) {
                "single_card" -> PremiumLayoutType.SINGLE_CARD
                else -> PremiumLayoutType.SINGLE_CARD
               // else -> PremiumLayoutType.TWO_CARD
            }
        }
    }


    override fun observePurchaseState(): Flow<Boolean> {
        return prefs.getIsPurchased()
    }

    override suspend fun fetchBillingProducts() {
        Log.d("PremiumRepository", "fetchBillingProducts called")
        billingManager.initializeBilling()
    }

    override suspend fun purchase(productId: String) {
        Log.d("PremiumRepository", "purchase called for: $productId")
        // Note: The actual billing flow launch must happen on an Activity context.
        // Usually we handle this via an Event/Flow that the UI observes,
        // or by passing the Activity to the BillingManager.
    }
}