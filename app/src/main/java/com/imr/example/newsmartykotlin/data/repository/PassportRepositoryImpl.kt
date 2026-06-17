package com.imr.example.newsmartykotlin.data.repository

import com.imr.example.newsmartykotlin.R
import com.imr.example.newsmartykotlin.domain.model.PassportCountry
import com.imr.example.newsmartykotlin.domain.repository.PassportRepository

class PassportRepositoryImpl : PassportRepository {

    private val countries = listOf(
        passportCountry("united_states", R.string.united_states, "51 x 51 mm", "2.0 x 2.0 inch", "600 x 600 px", "White", R.drawable.ic_flag_us),
        passportCountry("canada", R.string.canada, "50 x 70 mm", "2.0 x 2.8 inch", "600 x 840 px", "White", R.drawable.ic_flag_canada),
        passportCountry("mexico", R.string.mexico, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_mexico),
        passportCountry("guatemala", R.string.guatemala, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_guatemala),
        passportCountry("cuba", R.string.cuba, "45 x 45 mm", "1.8 x 1.8 inch", "531 x 531 px", "White", R.drawable.ic_flag_cuba),
        passportCountry("jamaica", R.string.jamaica, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_jamaica),
        passportCountry("haiti", R.string.haiti, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_haiti),
        passportCountry("dominican_republic", R.string.dominican_republic, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_dominican_republic),
        passportCountry("panama", R.string.panama, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_panama),
        passportCountry("costa_rica", R.string.costa_rica, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_costa_rica),

        passportCountry("brazil", R.string.brazil, "50 x 70 mm", "2.0 x 2.8 inch", "600 x 840 px", "White", R.drawable.ic_flag_brazil),
        passportCountry("argentina", R.string.argentina, "40 x 40 mm", "1.6 x 1.6 inch", "472 x 472 px", "White", R.drawable.ic_flag_argentina),
        passportCountry("chile", R.string.chile, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_chile),
        passportCountry("colombia", R.string.colombia, "40 x 50 mm", "1.6 x 2.0 inch", "472 x 591 px", "White", R.drawable.ic_flag_colombia),
        passportCountry("peru", R.string.peru, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_peru),
        passportCountry("venezuela", R.string.venezuela, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_venezuela),
        passportCountry("ecuador", R.string.ecuador, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_ecuador),
        passportCountry("bolivia", R.string.bolivia, "40 x 40 mm", "1.6 x 1.6 inch", "472 x 472 px", "White", R.drawable.ic_flag_bolivia),
        passportCountry("paraguay", R.string.paraguay, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_paraguay),
        passportCountry("uruguay", R.string.uruguay, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_uruguay),

        passportCountry("united_kingdom", R.string.united_kingdom, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_uk),
        passportCountry("france", R.string.france, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_france),
        passportCountry("germany", R.string.germany, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_germany),
        passportCountry("italy", R.string.italy, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_italy),
        passportCountry("spain", R.string.spain, "30 x 40 mm", "1.2 x 1.6 inch", "354 x 472 px", "White", R.drawable.ic_flag_spain),
        passportCountry("netherlands", R.string.netherlands, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_netherlands),
        passportCountry("belgium", R.string.belgium, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_belgium),
        passportCountry("sweden", R.string.sweden, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_sweden),
        passportCountry("norway", R.string.norway, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_norway),
        passportCountry("denmark", R.string.denmark, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_denmark),
        passportCountry("finland", R.string.finland, "36 x 47 mm", "1.4 x 1.9 inch", "425 x 555 px", "White", R.drawable.ic_flag_finland),
        passportCountry("poland", R.string.poland, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_poland),
        passportCountry("czech_republic", R.string.czech_republic, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_czech_republic),
        passportCountry("slovakia", R.string.slovakia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_slovakia),
        passportCountry("hungary", R.string.hungary, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_hungary),
        passportCountry("austria", R.string.austria, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_austria),
        passportCountry("switzerland", R.string.switzerland, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_switzerland),
        passportCountry("greece", R.string.greece, "40 x 60 mm", "1.6 x 2.4 inch", "472 x 709 px", "White", R.drawable.ic_flag_greece),
        passportCountry("portugal", R.string.portugal, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_portugal),
        passportCountry("ireland", R.string.ireland, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_ireland),
        passportCountry("romania", R.string.romania, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_romania),
        passportCountry("bulgaria", R.string.bulgaria, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_bulgaria),
        passportCountry("croatia", R.string.croatia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_croatia),
        passportCountry("serbia", R.string.serbia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_serbia),
        passportCountry("ukraine", R.string.ukraine, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_ukraine),
        passportCountry("russia", R.string.russia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_russia),
        passportCountry("turkey", R.string.turkey, "50 x 60 mm", "2.0 x 2.4 inch", "600 x 720 px", "White", R.drawable.ic_flag_turkey),
        passportCountry("iceland", R.string.iceland, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_iceland),
        passportCountry("lithuania", R.string.lithuania, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_lithuania),
        passportCountry("latvia", R.string.latvia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_latvia),

        passportCountry("india", R.string.india, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_india),
        passportCountry("pakistan", R.string.pakistan, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_pakistan),
        passportCountry("bangladesh", R.string.bangladesh, "45 x 55 mm", "1.8 x 2.2 inch", "531 x 650 px", "White", R.drawable.ic_flag_bangladesh),
        passportCountry("china", R.string.china, "33 x 48 mm", "1.3 x 1.9 inch", "390 x 567 px", "White", R.drawable.ic_flag_china),
        passportCountry("japan", R.string.japan, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_japan),
        passportCountry("south_korea", R.string.south_korea, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_south_korea),
        passportCountry("malaysia", R.string.malaysia, "35 x 50 mm", "1.4 x 2.0 inch", "413 x 591 px", "White", R.drawable.ic_flag_malaysia),
        passportCountry("singapore", R.string.singapore, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_singapore),
        passportCountry("indonesia", R.string.indonesia, "40 x 60 mm", "1.6 x 2.4 inch", "472 x 709 px", "White", R.drawable.ic_flag_indonesia),
        passportCountry("thailand", R.string.thailand, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_thailand),
        passportCountry("vietnam", R.string.vietnam, "40 x 60 mm", "1.6 x 2.4 inch", "472 x 709 px", "White", R.drawable.ic_flag_vietnam),
        passportCountry("philippines", R.string.philippines, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_philippines),
        passportCountry("sri_lanka", R.string.sri_lanka, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_sri_lanka),
        passportCountry("nepal", R.string.nepal, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_nepal),
        passportCountry("afghanistan", R.string.afghanistan, "40 x 45 mm", "1.6 x 1.8 inch", "472 x 531 px", "White", R.drawable.ic_flag_afghanistan),
        passportCountry("iran", R.string.iran, "40 x 60 mm", "1.6 x 2.4 inch", "472 x 709 px", "White", R.drawable.ic_flag_iran),
        passportCountry("iraq", R.string.iraq, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_iraq),
        passportCountry("saudi_arabia", R.string.saudi_arabia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_saudi_arabia),
        passportCountry("uae", R.string.uae, "43 x 55 mm", "1.7 x 2.2 inch", "508 x 650 px", "White", R.drawable.ic_flag_uae),
        passportCountry("qatar", R.string.qatar, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_qatar),
        passportCountry("kuwait", R.string.kuwait, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_kuwait),
        passportCountry("oman", R.string.oman, "40 x 60 mm", "1.6 x 2.4 inch", "472 x 709 px", "White", R.drawable.ic_flag_oman),
        passportCountry("jordan", R.string.jordan, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_jordan),
        passportCountry("israel", R.string.israel, "50 x 50 mm", "2.0 x 2.0 inch", "591 x 591 px", "White", R.drawable.ic_flag_israel),
        passportCountry("kazakhstan", R.string.kazakhstan, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_kazakhstan),

        passportCountry("south_africa", R.string.south_africa, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_south_africa),
        passportCountry("nigeria", R.string.nigeria, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_nigeria),
        passportCountry("egypt", R.string.egypt, "40 x 60 mm", "1.6 x 2.4 inch", "472 x 709 px", "White", R.drawable.ic_flag_egypt),
        passportCountry("kenya", R.string.kenya, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_kenya),
        passportCountry("ethiopia", R.string.ethiopia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_ethiopia),
        passportCountry("ghana", R.string.ghana, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_ghana),
        passportCountry("tanzania", R.string.tanzania, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_tanzania),
        passportCountry("uganda", R.string.uganda, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_uganda),
        passportCountry("morocco", R.string.morocco, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_morocco),
        passportCountry("algeria", R.string.algeria, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_algeria),
        passportCountry("tunisia", R.string.tunisia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_tunisia),
        passportCountry("zimbabwe", R.string.zimbabwe, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_zimbabwe),
        passportCountry("cameroon", R.string.cameroon, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_cameroon),
        passportCountry("senegal", R.string.senegal, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_senegal),
        passportCountry("angola", R.string.angola, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_angola),

        passportCountry("australia", R.string.australia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_australia),
        passportCountry("new_zealand", R.string.new_zealand, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_new_zealand),
        passportCountry("fiji", R.string.fiji, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_fiji),
        passportCountry("papua_new_guinea", R.string.papua_new_guinea, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_papua_new_guinea),
        passportCountry("samoa", R.string.samoa, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_samoa),

        passportCountry("luxembourg", R.string.luxembourg, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_luxembourg),
        passportCountry("malta", R.string.malta, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_malta),
        passportCountry("mongolia", R.string.mongolia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_mongolia),
        passportCountry("belarus", R.string.belarus, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_belarus),
        passportCountry("moldova", R.string.moldova, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_moldova),
        passportCountry("georgia", R.string.georgia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_georgia),
        passportCountry("azerbaijan", R.string.azerbaijan, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_azerbaijan),
        passportCountry("armenia", R.string.armenia, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_armenia),
        passportCountry("kyrgyzstan", R.string.kyrgyzstan, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_kyrgyzstan),
        passportCountry("uzbekistan", R.string.uzbekistan, "35 x 45 mm", "1.4 x 1.8 inch", "413 x 531 px", "White", R.drawable.ic_flag_uzbekistan)
    )
    override fun getCountries(): List<PassportCountry> = countries

    override fun getCountryById(id: String): PassportCountry? {
        return countries.firstOrNull { it.id == id }
    }
    private fun passportCountry(
        id: String,
        nameRes: Int,
        passportSizeMm: String,
        passportSizeInch: String,
        passportPixel: String,
        background: String,
        flagRes: Int
    ): PassportCountry {
        return PassportCountry(
            id = id,
            nameRes = nameRes,

            passportSizeMm = passportSizeMm,
            passportSizeInch = passportSizeInch,
            passportPixel = passportPixel,

            visaSizeMm = passportSizeMm,
            visaSizeInch = passportSizeInch,
            visaPixel = passportPixel,

            standardSizeMm = "35 x 45 mm",
            standardSizeInch = "1.4 x 1.8 inch",
            standardPixel = "413 x 531 px",

            background = background,
            flagRes = flagRes
        )
    }
}