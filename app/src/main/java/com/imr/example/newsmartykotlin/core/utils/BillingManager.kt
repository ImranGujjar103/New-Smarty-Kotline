package com.imr.example.newsmartykotlin.core.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.*
import com.imr.example.newsmartykotlin.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.comparisons.then
import kotlin.coroutines.resume
import kotlin.getValue


class BillingManager(val context: Context, val dataStorePrefs: DataStorePrefs) : PurchasesUpdatedListener {

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    private val _weeklySubscriptionState = MutableStateFlow<ProductState>(ProductState.Loading)
    val weeklySubscriptionState: StateFlow<ProductState> = _weeklySubscriptionState.asStateFlow()

    private val _monthlySubscriptionState = MutableStateFlow<ProductState>(ProductState.Loading)
    val monthlySubscriptionState: StateFlow<ProductState> = _monthlySubscriptionState.asStateFlow()

    private val _yearlySubscriptionState = MutableStateFlow<ProductState>(ProductState.Loading)
    val yearlySubscriptionState: StateFlow<ProductState> = _yearlySubscriptionState.asStateFlow()

    private val _discountedSubscriptionState = MutableStateFlow<ProductState>(ProductState.Loading)

    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    private var weeklyProductDetails: ProductDetails? = null
    private var monthlyProductDetails: ProductDetails? = null
    private var yearlyProductDetails: ProductDetails? = null

    // Connection management
    private var connectionState = ConnectionState.DISCONNECTED
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 3
    private val reconnectDelayMs = 1000L
    private val reconnectHandler = Handler(Looper.getMainLooper())
    private var reconnectRunnable: Runnable? = null

    private enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    companion object {
        private const val TAG = "BillingManager"
        const val WEEKLY_SUBSCRIPTION_ID = "weekly_package"
        const val MONTHLY_SUBSCRIPTION_ID = "monthly_package"
        const val YEARLY_SUBSCRIPTION_ID = "yearly_purchases"
    }

    private var purchaseCallback: ((PurchaseState) -> Unit)? = null

    fun initializeBilling() {
        startConnection()
    }

    fun clearPurchaseCallback() {
        Log.d(TAG, "Clearing purchase callback")
        purchaseCallback = null
    }

    private fun startConnection() {
        Log.d(TAG, "startConnection: Current state = $connectionState")
        // Prevent multiple simultaneous connection attempts
        if (connectionState == ConnectionState.CONNECTING ||
            connectionState == ConnectionState.CONNECTED) {
            Log.d(TAG, "Connection already in progress or established")
            return
        }

        connectionState = ConnectionState.CONNECTING
        Log.d(TAG, "Starting billing connection...")

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                Log.d(TAG, "onBillingSetupFinished: ResponseCode = ${billingResult.responseCode}, Message = ${billingResult.debugMessage}")
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing client connected")
                    connectionState = ConnectionState.CONNECTED
                    reconnectAttempts = 0 // Reset counter on success

                    queryProducts()
                    checkPurchases()
                } else {
                    Log.e(TAG, "Billing setup failed: ${billingResult.debugMessage}")
                    connectionState = ConnectionState.DISCONNECTED

                    _weeklySubscriptionState.value = ProductState.Error(billingResult.debugMessage)
                    _monthlySubscriptionState.value = ProductState.Error(billingResult.debugMessage)
                    _yearlySubscriptionState.value = ProductState.Error(billingResult.debugMessage)

                    // Retry connection if within limits
                    scheduleReconnect()
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected")
                connectionState = ConnectionState.DISCONNECTED
                scheduleReconnect()
            }
        })
    }

    private fun scheduleReconnect() {
        // Cancel any pending reconnection attempts
        reconnectRunnable?.let { reconnectHandler.removeCallbacks(it) }

        if (reconnectAttempts >= maxReconnectAttempts) {
            Log.e(TAG, "Max reconnection attempts ($maxReconnectAttempts) reached")
            _weeklySubscriptionState.value = ProductState.Error("Billing service unavailable")
            _monthlySubscriptionState.value = ProductState.Error("Billing service unavailable")
            _yearlySubscriptionState.value = ProductState.Error("Billing service unavailable")
            return
        }

        reconnectAttempts++
        val delay = reconnectDelayMs * reconnectAttempts // Exponential backoff

        Log.d(TAG, "Scheduling reconnection attempt $reconnectAttempts in ${delay}ms")

        reconnectRunnable = Runnable {
            if (connectionState == ConnectionState.DISCONNECTED) {
                startConnection()
            }
        }

        reconnectRunnable?.let { r ->
            reconnectHandler.postDelayed(r, delay)
        }
    }

    fun resetConnection() {
        Log.d(TAG, "Resetting connection state")
        reconnectAttempts = 0
        if (!billingClient.isReady && connectionState == ConnectionState.DISCONNECTED) {
            startConnection()
        }
    }

    private fun queryProducts() {
        Log.d(TAG, "queryProducts called")
        val subscriptionParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(WEEKLY_SUBSCRIPTION_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(MONTHLY_SUBSCRIPTION_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build(),

                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(YEARLY_SUBSCRIPTION_ID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(subscriptionParams) { billingResult, productDetailsResult ->
            Log.d(TAG, "queryProductDetailsAsync Response: ${billingResult.responseCode}, ${billingResult.debugMessage}")
            Log.d(TAG, "Products found: ${productDetailsResult.productDetailsList.size}")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                productDetailsResult.productDetailsList.forEach { productDetails ->
                    Log.d(TAG, "Processing product: ${productDetails.productId}")
                    Log.d(TAG, "ProductDetails: $productDetails")
                    Log.d(TAG, "Offers count: ${productDetails.subscriptionOfferDetails?.size ?: 0}")

                    val price = getRegularPrice(productDetails)
                    val trialInfo = getTrialText(productDetails)
                    val trialInfoAfter = getTrialInfoAfter(productDetails)
                    val hasFreeTrial = trialInfo.isNotEmpty()
                    val priceAmountMicros = getPriceAmountMicros(productDetails)

                    Log.d(TAG, "Extracted values for ${productDetails.productId}: price=$price, hasTrial=$hasFreeTrial")

                    when (productDetails.productId) {

                        WEEKLY_SUBSCRIPTION_ID -> {
                            weeklyProductDetails = productDetails
                            _weeklySubscriptionState.value = ProductState.Available(
                                productDetails = productDetails,
                                price = price,
                                priceAmountMicros = priceAmountMicros
                            )

                            saveWeeklyTrialInfo(
                                trialInfo = trialInfo,
                                trialInfoAfter = trialInfoAfter,
                                hasFreeTrial = hasFreeTrial,
                                weeklyPrice = price
                            )
                        }

                        MONTHLY_SUBSCRIPTION_ID -> {
                            monthlyProductDetails = productDetails

                            _monthlySubscriptionState.value = ProductState.Available(
                                productDetails = productDetails,
                                price = price,
                                priceAmountMicros = priceAmountMicros
                            )

                            saveMonthlyTrialInfo(
                                trialInfo = trialInfo,
                                trialInfoAfter = trialInfoAfter,
                                hasFreeTrial = hasFreeTrial,
                                monthlyPrice = price
                            )

                            Log.d(TAG, "Monthly product saved: price=$price trial=$trialInfo")
                        }

                        YEARLY_SUBSCRIPTION_ID -> {
                            yearlyProductDetails = productDetails

                            _yearlySubscriptionState.value = ProductState.Available(
                                productDetails = productDetails,
                                price = price,
                                priceAmountMicros = priceAmountMicros
                            )

                            saveYearlyTrialInfo(
                                trialInfo = trialInfo,
                                trialInfoAfter = trialInfoAfter,
                                hasFreeTrial = hasFreeTrial,
                                yearlyPrice = price
                            )

                            Log.d(TAG, "Yearly product saved: price=$price trial=$trialInfo")
                        }
                    }
                }
            } else {
                Log.e(TAG, "Billing query failed: ${billingResult.debugMessage}")
                _weeklySubscriptionState.value = ProductState.Error(billingResult.debugMessage)
                _monthlySubscriptionState.value = ProductState.Error(billingResult.debugMessage)
                _yearlySubscriptionState.value = ProductState.Error(billingResult.debugMessage)
            }
        }
    }

    private fun saveWeeklyTrialInfo(
        trialInfo: String,
        trialInfoAfter: String,
        hasFreeTrial: Boolean,
        weeklyPrice: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            dataStorePrefs.setIsWeeklyTrial(hasFreeTrial)
            dataStorePrefs.setWeeklyPrice(weeklyPrice)

            if (hasFreeTrial) {
                dataStorePrefs.setWeeklyTrialInfo(trialInfo)
                dataStorePrefs.setWeeklyTrialInfoAfter(trialInfoAfter)
            } else {
                dataStorePrefs.setWeeklyTrialInfo("")
                dataStorePrefs.setWeeklyTrialInfoAfter("")
            }
        }
    }

    private fun saveMonthlyTrialInfo(
        trialInfo: String,
        trialInfoAfter: String,
        hasFreeTrial: Boolean,
        monthlyPrice: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {

            dataStorePrefs.setIsMonthlyTrial(hasFreeTrial)
            dataStorePrefs.setMonthlyPrice(monthlyPrice)

            if (hasFreeTrial) {
                dataStorePrefs.setMonthlyTrialInfo(trialInfo)
                dataStorePrefs.setMonthlyTrialInfoAfter(trialInfoAfter)
            } else {
                dataStorePrefs.setMonthlyTrialInfo("")
                dataStorePrefs.setMonthlyTrialInfoAfter("")
            }

            Log.d(
                TAG,
                "Monthly trial saved: hasTrial=$hasFreeTrial, price=$monthlyPrice, trialInfo=$trialInfo"
            )
        }
    }

    private fun saveYearlyTrialInfo(
        trialInfo: String,
        trialInfoAfter: String,
        hasFreeTrial: Boolean,
        yearlyPrice: String
    ) {
        CoroutineScope(Dispatchers.Main).launch {

            dataStorePrefs.setIsYearlyTrial(hasFreeTrial)
            dataStorePrefs.setYearlyPrice(yearlyPrice)

            if (hasFreeTrial) {
                dataStorePrefs.setYearlyTrialInfo(trialInfo)
                dataStorePrefs.setYearlyTrialInfoAfter(trialInfoAfter)
            } else {
                dataStorePrefs.setYearlyTrialInfo("")
                dataStorePrefs.setYearlyTrialInfoAfter("")
            }

            Log.d(
                TAG,
                "Yearly trial saved: hasTrial=$hasFreeTrial, price=$yearlyPrice, trialInfo=$trialInfo"
            )
        }
    }
    private fun getTrialInfoAfter(productDetails: ProductDetails): String {
        val freeTrialPhase = productDetails.subscriptionOfferDetails
            ?.firstOrNull()
            ?.pricingPhases
            ?.pricingPhaseList
            ?.firstOrNull { it.priceAmountMicros == 0L }

        val trialDays = freeTrialPhase?.billingPeriod?.let {
            parseBillingPeriodToDays(it)
        } ?: 0

        val price = getRegularPrice(productDetails)
        val period = productDetails.subscriptionOfferDetails
            ?.firstOrNull()
            ?.pricingPhases
            ?.pricingPhaseList
            ?.lastOrNull { it.priceAmountMicros > 0L }
            ?.billingPeriod
            ?.let { parseBillingPeriodToString(it) }
            ?: ""

        return if (trialDays > 0) {
            "Then $price/$period"
        } else {
            ""
        }
    }
    private fun getRegularPrice(productDetails: ProductDetails): String {
        return productDetails.subscriptionOfferDetails
            ?.firstOrNull()
            ?.pricingPhases
            ?.pricingPhaseList
            ?.lastOrNull { it.priceAmountMicros > 0L }
            ?.formattedPrice
            ?: ""
    }

    private fun getTrialText(productDetails: ProductDetails): String {
        val freeTrialPhase = productDetails.subscriptionOfferDetails
            ?.firstOrNull()
            ?.pricingPhases
            ?.pricingPhaseList
            ?.firstOrNull { it.priceAmountMicros == 0L }

        val trialDays = freeTrialPhase?.billingPeriod?.let {
            parseBillingPeriodToDays(it)
        } ?: 0

        return if (trialDays > 0) {
            "Enjoy $trialDays day free trial"
        } else {
            ""
        }
    }

    fun purchaseSubscription(
        activity: Activity,
        productId: String
    ) {
        when (productId) {
            WEEKLY_SUBSCRIPTION_ID -> purchaseWeeklySubscription(activity)
            MONTHLY_SUBSCRIPTION_ID -> purchaseMonthlySubscription(activity)
            YEARLY_SUBSCRIPTION_ID -> purchaseYearlySubscription(activity)
            else -> {
                _purchaseState.value = PurchaseState.Error("Invalid subscription product")
            }
        }
    }

    fun purchaseWeeklySubscription(activity: Activity) {
        ensureConnectionAndLaunch(activity) {
            weeklyProductDetails?.let { productDetails ->
                val offerToken = productDetails.subscriptionOfferDetails
                    ?.firstOrNull()
                    ?.offerToken

                launchSubscriptionBillingFlow(
                    activity = activity,
                    productDetails = productDetails,
                    offerToken = offerToken
                )
            } ?: run {
                _purchaseState.value = PurchaseState.Error("Weekly subscription not available")
            }
        }
    }

    fun purchaseYearlySubscription(activity: Activity) {
        ensureConnectionAndLaunch(activity) {
            yearlyProductDetails?.let { productDetails ->
                val offerToken = productDetails.subscriptionOfferDetails
                    ?.firstOrNull()
                    ?.offerToken

                launchSubscriptionBillingFlow(
                    activity = activity,
                    productDetails = productDetails,
                    offerToken = offerToken
                )
            } ?: run {
                _purchaseState.value = PurchaseState.Error("Yearly subscription not available")
            }
        }
    }

    fun purchaseMonthlySubscription(activity: Activity) {
        ensureConnectionAndLaunch(activity) {
            monthlyProductDetails?.let { productDetails ->
                val offerToken = productDetails.subscriptionOfferDetails
                    ?.firstOrNull()
                    ?.offerToken

                launchSubscriptionBillingFlow(
                    activity = activity,
                    productDetails = productDetails,
                    offerToken = offerToken
                )
            } ?: run {
                _purchaseState.value = PurchaseState.Error("Monthly subscription not available")
            }
        }
    }

    // Helper function to check for paid trial
    private fun checkPaidTrial(productDetails: ProductDetails): PaidTrialInfo {
        val offerDetails = productDetails.subscriptionOfferDetails?.firstOrNull()
        val pricingPhases = offerDetails?.pricingPhases?.pricingPhaseList

        Log.d(TAG, "checkPaidTrial: Pricing phases count = ${pricingPhases?.size}")

        if (pricingPhases != null && pricingPhases.size >= 2) {
            // First phase is trial, second phase is regular price
            val trialPhase = pricingPhases[0]
            val regularPhase = pricingPhases[1]

            Log.d(TAG, "checkPaidTrial: Trial phase price micros = ${trialPhase.priceAmountMicros}")
            Log.d(TAG, "checkPaidTrial: Regular phase price micros = ${regularPhase.priceAmountMicros}")

            // Check if first phase price is less than regular price (paid trial)
            val trialPriceMicros = trialPhase.priceAmountMicros
            val regularPriceMicros = regularPhase.priceAmountMicros

            if (trialPriceMicros < regularPriceMicros) {
                // It's a paid trial (or free trial if price is 0)
                val isPaidTrial = trialPriceMicros > 0

                Log.d(TAG, "checkPaidTrial: Is paid trial = $isPaidTrial")

                // Parse trial days
                val trialDays = parseBillingPeriodToDays(trialPhase.billingPeriod)
                val billingPeriod = parseBillingPeriodToString(regularPhase.billingPeriod)

                return PaidTrialInfo(
                    hasPaidTrial = isPaidTrial,
                    trialPrice = trialPhase.formattedPrice,
                    afterPrice = regularPhase.formattedPrice,
                    trialDuration = trialPhase.billingPeriod,
                    trialDays = trialDays,
                    billingPeriod = billingPeriod,
                    trialPriceMicros = trialPriceMicros,
                    afterPriceMicros = regularPriceMicros
                )
            }
        }

        // No paid trial - return empty info
        val regularPrice = pricingPhases?.firstOrNull()?.formattedPrice ?: ""
        val regularPriceMicros = pricingPhases?.firstOrNull()?.priceAmountMicros ?: 0L
        val billingPeriod = parseBillingPeriodToString(
            pricingPhases?.firstOrNull()?.billingPeriod ?: "P1M"
        )

        Log.d(TAG, "checkPaidTrial: No paid trial detected")

        return PaidTrialInfo(
            hasPaidTrial = false,
            trialPrice = "",
            afterPrice = regularPrice,
            trialDuration = "",
            trialDays = 0,
            billingPeriod = billingPeriod,
            trialPriceMicros = 0L,
            afterPriceMicros = regularPriceMicros
        )
    }

    private fun saveMonthlyPaidTrialInfo(paidTrialInfo: PaidTrialInfo) {
        CoroutineScope(Dispatchers.Main).launch {
            // Save trial info strings
            val trialInfo = "Enjoy a ${paidTrialInfo.trialDays} days trial for"
            val trialPrice = paidTrialInfo.trialPrice
            val afterTrialInfo = "Then ${paidTrialInfo.afterPrice}/${paidTrialInfo.billingPeriod}"
            val monthlyPrice = "${paidTrialInfo.afterPrice}/${paidTrialInfo.billingPeriod}"

            dataStorePrefs.setIsMonthlyTrial(true)
            dataStorePrefs.setMonthlyTrialPrice(trialPrice)
            dataStorePrefs.setMonthlyTrialInfo(trialInfo)
            dataStorePrefs.setMonthlyTrialInfoAfter(afterTrialInfo)
            dataStorePrefs.setMonthlyPrice(monthlyPrice)

            Log.d(TAG, "Saved monthly paid trial info: $trialInfo | $afterTrialInfo")
        }
    }

    private fun saveMonthlyRegularPrice(price: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val monthlyPrice = "$price/month"
            dataStorePrefs.setMonthlyPrice(monthlyPrice)
            Log.d(TAG, "Saved monthly regular price: $monthlyPrice")
        }
    }



    private fun calculateOriginalPrice(discountedPrice: String): String {
        return try {
            // Extract numeric value (handles decimals and commas)
            val numericValue = discountedPrice
                .filter { it.isDigit() || it == '.' || it == ',' }
                .replace(",", "")
                .toDoubleOrNull()

            if (numericValue != null) {
                val originalPrice = numericValue * 2

                // Find currency symbol position (before or after number)
                val firstDigitIndex = discountedPrice.indexOfFirst { it.isDigit() }
                val currencyPrefix = if (firstDigitIndex > 0) {
                    discountedPrice.substring(0, firstDigitIndex).trim()
                } else ""

                val lastDigitIndex = discountedPrice.indexOfLast { it.isDigit() || it == '.' }
                val currencySuffix = if (lastDigitIndex < discountedPrice.length - 1) {
                    discountedPrice.substring(lastDigitIndex + 1).trim()
                } else ""

                // Format price
                val formattedPrice = if (originalPrice % 1.0 == 0.0) {
                    originalPrice.toInt().toString()
                } else {
                    "%.2f".format(originalPrice)
                }

                // Reconstruct
                when {
                    currencyPrefix.isNotEmpty() -> "$currencyPrefix$formattedPrice"
                    currencySuffix.isNotEmpty() -> "$formattedPrice $currencySuffix"
                    else -> formattedPrice
                }
            } else {
                Log.e(TAG, "Failed to parse price: $discountedPrice")
                discountedPrice
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating original price: ${e.message}")
            discountedPrice
        }
    }

    fun daysOfTrail(productDetails: ProductDetails?): String {
        productDetails?.subscriptionOfferDetails?.let {
            return getFreeTrialDays(productDetails)
        }
        return "No free trial"
    }

    fun getFreeTrialDays(productDetails: ProductDetails): String {
        return if (checkHasFreetrial(productDetails)) {
            val trialDays = getTrialDays(productDetails)

            if (trialDays > 0) {
                "${trialDays}-Day trial"
            } else {
                "No free trial"
            }
        } else {
            "No free trial"
        }
    }

    fun checkHasFreetrial(productDetails: ProductDetails): Boolean {
        return (productDetails.subscriptionOfferDetails?.first()?.pricingPhases?.pricingPhaseList?.size
            ?: 0) >= 1
    }

    fun getTrialDays(productDetails: ProductDetails): Int {
        productDetails.subscriptionOfferDetails?.forEach { offer ->
            offer.pricingPhases.pricingPhaseList.forEach { phase ->
                if (phase.priceAmountMicros == 0L) {
                    return parseTrialPeriod(phase.billingPeriod)
                }
            }
        }
        return 0
    }

    fun parseTrialPeriod(billingPeriod: String): Int {
        val regex = Regex("P(?:(\\d+)Y)?(?:(\\d+)M)?(?:(\\d+)D)?")
        val match = regex.matchEntire(billingPeriod)

        if (match != null) {
            val (years, months, days) = match.destructured
            return (years.toIntOrNull() ?: 0) * 365 +
                    (months.toIntOrNull() ?: 0) * 30 +
                    (days.toIntOrNull() ?: 0)
        }

        return 0
    }

    private fun getPriceAmountMicros(productDetails: ProductDetails): Long {
        // Get the regular price (non-trial) pricing phase
        val pricingPhases = productDetails.subscriptionOfferDetails?.firstOrNull()
            ?.pricingPhases?.pricingPhaseList

        // Find the last paid phase (regular price after trial if trial exists)
        return pricingPhases?.lastOrNull { it.priceAmountMicros > 0L }?.priceAmountMicros ?: 0L
    }


    private fun saveTrialInfo(
        trialInfo: String,
        trialInfoAfter: String,
        hasFreeTrial: Boolean,
        monthlyPrice: String
    ) {

        CoroutineScope(Dispatchers.Main).launch {

            if (hasFreeTrial) {

                dataStorePrefs.setIsMonthlyTrial(true)

                dataStorePrefs.setMonthlyTrialInfo(trialInfo)

                dataStorePrefs.setMonthlyTrialInfoAfter(trialInfoAfter)

                dataStorePrefs.setMonthlyPrice(monthlyPrice)

            } else {

                dataStorePrefs.setIsMonthlyTrial(false)

                dataStorePrefs.setMonthlyPrice(monthlyPrice)
            }
        }
    }

    private fun parseBillingPeriodToDays(period: String): Int {
        Log.d(TAG, "parseBillingPeriodToDays: period === $period")
        return when {
            period.contains("D") -> {
                period.replace("P", "").replace("D", "").toIntOrNull() ?: 0
            }
            period.contains("W") -> {
                (period.replace("P", "").replace("W", "").toIntOrNull() ?: 0) * 7
            }
            period.contains("M") -> {
                (period.replace("P", "").replace("M", "").toIntOrNull() ?: 0) * 30
            }
            else -> 0
        }
    }

    private fun parseBillingPeriodToString(period: String): String {
        Log.d(TAG, "parseBillingPeriodToString: period === $period")
        return when {
            period.contains("P1D") -> "day"
            period.contains("D") -> "days"
            period.contains("P1W") -> "week"
            period.contains("W") -> "weeks"
            period.contains("P1M") -> "month"
            period.contains("M") -> "Months"
            period.contains("P1Y") -> "year"
            period.contains("Y") -> "years"
            else -> "Week"
        }
    }

    private fun checkPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { billingResult, purchases ->

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

                val activeSubscription = purchases.firstOrNull { purchase ->
                    purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                            (
                                    purchase.products.contains(MONTHLY_SUBSCRIPTION_ID) ||
                                            purchase.products.contains(YEARLY_SUBSCRIPTION_ID)
                                    )
                }

                if (activeSubscription != null) {
                    _purchaseState.value = PurchaseState.Purchased(activeSubscription)

                    CoroutineScope(Dispatchers.Main).launch {
                        dataStorePrefs.setIsPurchased(true)
                    }
                }
            }
        }
    }


    private fun ensureConnectionAndLaunch(activity: Activity, purchaseAction: () -> Unit) {
        if (!billingClient.isReady) {
            Log.e(TAG, "Billing client not ready, reconnecting...")

            // Show loading state
            _purchaseState.value = PurchaseState.Idle

            // Reset reconnection attempts and try to connect
            reconnectAttempts = 0
            connectionState = ConnectionState.DISCONNECTED
            startConnection()

            // Delay the billing flow launch
            reconnectHandler.postDelayed({
                if (billingClient.isReady) {
                    purchaseAction()
                } else {
                    Log.e(TAG, "Failed to reconnect billing client")
                    _purchaseState.value = PurchaseState.Error("Unable to connect to billing service. Please try again.")
                }
            }, 2000L)

            return
        }

        // Connection is ready, proceed with purchase
        purchaseAction()
    }

    private fun launchSubscriptionBillingFlow(
        activity: Activity,
        productDetails: ProductDetails,
        offerToken: String?
    ) {
        if (offerToken == null) {
            _purchaseState.value = PurchaseState.Error("No offer available")
            return
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        if (billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            _purchaseState.value = PurchaseState.Error(billingResult.debugMessage)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        Log.d(TAG, "onPurchasesUpdated - Response: ${billingResult.responseCode}, Purchases: ${purchases?.size ?: 0}")

        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    Log.d(TAG, "Processing purchase: ${purchase.products}")
                    handlePurchase(purchase)
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Log.d(TAG, "User cancelled the purchase")
                val cancelledState = PurchaseState.Cancelled
                _purchaseState.value = cancelledState

                Handler(Looper.getMainLooper()).post {
                    purchaseCallback?.invoke(cancelledState)
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Log.d(TAG, "Item already owned")
                val errorState = PurchaseState.Error("Item already owned")
                _purchaseState.value = errorState

                Handler(Looper.getMainLooper()).post {
                    purchaseCallback?.invoke(errorState)
                }

                checkPurchases()
            }
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> {
                Log.e(TAG, "Service disconnected during purchase")
                val errorState = PurchaseState.Error("Connection lost. Please try again.")
                _purchaseState.value = errorState

                Handler(Looper.getMainLooper()).post {
                    purchaseCallback?.invoke(errorState)
                }

                // Trigger reconnection
                scheduleReconnect()
            }
            else -> {
                Log.e(TAG, "Purchase failed: ${billingResult.responseCode} - ${billingResult.debugMessage}")
                val errorState = PurchaseState.Error(billingResult.debugMessage)
                _purchaseState.value = errorState

                Handler(Looper.getMainLooper()).post {
                    purchaseCallback?.invoke(errorState)
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        Log.d(TAG, "handlePurchase called - State: ${purchase.purchaseState}, Acknowledged: ${purchase.isAcknowledged}")
        Log.d(TAG, "Products: ${purchase.products}")

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                Log.d(TAG, "Acknowledging purchase...")
                acknowledgePurchase(purchase)
            } else {
                Log.d(TAG, "Purchase already acknowledged")
                val purchasedState = PurchaseState.Purchased(purchase)
                _purchaseState.value = purchasedState

                Handler(Looper.getMainLooper()).post {
                    purchaseCallback?.invoke(purchasedState)
                }
            }
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            Log.d(TAG, "Purchase is pending")
            val pendingState = PurchaseState.Pending
            _purchaseState.value = pendingState

            Handler(Looper.getMainLooper()).post {
                purchaseCallback?.invoke(pendingState)
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.acknowledgePurchase(params) { billingResult ->
            Log.d(TAG, "Acknowledge result: ${billingResult.responseCode}, ${billingResult.debugMessage}")

            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                Log.d(TAG, "Purchase acknowledged successfully")
                val purchasedState = PurchaseState.Purchased(purchase)
                _purchaseState.value = purchasedState
                CoroutineScope(Dispatchers.Main).launch {
                    dataStorePrefs.setIsPurchased(true)
                }

                Handler(Looper.getMainLooper()).post {
                    Log.d(TAG, "Invoking purchase callback for successful acknowledgement")
                    purchaseCallback?.invoke(purchasedState)
                }
            } else {
                Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                val errorState = PurchaseState.Error(billingResult.debugMessage)
                _purchaseState.value = errorState

                Handler(Looper.getMainLooper()).post {
                    purchaseCallback?.invoke(errorState)
                }
            }
        }
    }

    fun cleanup() {
        Log.d(TAG, "Cleaning up billing manager")
        reconnectRunnable?.let { reconnectHandler.removeCallbacks(it) }
        reconnectAttempts = 0
        connectionState = ConnectionState.DISCONNECTED

        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

    fun endConnection() {
        cleanup()
    }

    // Data classes
    data class TrialInfo(
        val hasFreeTrial: Boolean,
        val trialDays: Int,
        val priceAfterTrial: String,
        val billingPeriod: String
    )

    data class PaidTrialInfo(
        val hasPaidTrial: Boolean,
        val trialPrice: String,          // e.g., "$0.99" or "$1.99"
        val afterPrice: String,          // e.g., "$9.99"
        val trialDuration: String,       // e.g., "P1W" (1 week), "P3D" (3 days)
        val trialDays: Int,              // Trial duration in days
        val billingPeriod: String,       // e.g., "month", "week"
        val trialPriceMicros: Long,      // Price in micros (for comparisons)
        val afterPriceMicros: Long       // Regular price in micros
    )

    sealed class ProductState {
        object Loading : ProductState()

        data class Available(
            val productDetails: ProductDetails,
            val price: String,
            val priceAmountMicros: Long
        ) : ProductState()

        data class AvailableWithTrial(
            val productDetails: ProductDetails,
            val trialInfo: TrialInfo,
            val priceAmountMicros: Long
        ) : ProductState()

        data class AvailableWithPaidTrial(
            val productDetails: ProductDetails,
            val paidTrialInfo: PaidTrialInfo,
            val priceAmountMicros: Long
        ) : ProductState()

        data class Error(val message: String) : ProductState()
    }

    sealed class PurchaseState {
        object Idle : PurchaseState()
        object Pending : PurchaseState()
        data class Purchased(val purchase: Purchase) : PurchaseState()
        object Cancelled : PurchaseState()
        data class Error(val message: String) : PurchaseState()
    }
}