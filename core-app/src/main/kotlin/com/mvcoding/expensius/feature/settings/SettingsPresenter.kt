/*
 * Copyright (C) 2016 Mantas Varnagiris.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package com.mvcoding.expensius.feature.settings

import com.mvcoding.expensius.RxSchedulers
import com.mvcoding.expensius.feature.currency.CurrenciesProvider
import com.mvcoding.expensius.feature.login.LoginPresenter.Destination
import com.mvcoding.expensius.feature.login.LoginPresenter.Destination.SUPPORT_DEVELOPER
import com.mvcoding.expensius.model.Currency
import com.mvcoding.expensius.model.SubscriptionType
import com.mvcoding.expensius.service.AppUserService
import com.mvcoding.expensius.service.AppUserWriteService
import com.mvcoding.mvp.Presenter
import rx.Observable

class SettingsPresenter(
        private val appUserService: AppUserService,
        private val appUserWriteService: AppUserWriteService,
        private val currenciesProvider: CurrenciesProvider,
        private val schedulers: RxSchedulers) : Presenter<SettingsPresenter.View>() {

    override fun onViewAttached(view: View) {
        super.onViewAttached(view)

        appUserService.appUser()
                .subscribeOn(schedulers.io)
                .map { it.settings }
                .observeOn(schedulers.main)
                .subscribeUntilDetached {
                    view.showMainCurrency(it.mainCurrency)
                    view.showSubscriptionType(it.subscriptionType)
                }

        view.mainCurrencyRequests()
                .switchMap { currenciesProvider.currencies() }
                .switchMap { view.chooseMainCurrency(it) }
                .observeOn(schedulers.io)
                .withLatestFrom(appUserService.appUser().map { it.settings }, { newCurrency, settings -> settings.copy(mainCurrency = newCurrency) })
                .switchMap { appUserWriteService.saveSettings(it) }
                .subscribeUntilDetached { }

        view.supportDeveloperRequests()
                .withLatestFrom(appUserService.appUser(), { unit, appUser -> appUser })
                .subscribeUntilDetached { if (it.isWithProperAccount()) view.displaySupportDeveloper() else view.displayLogin(SUPPORT_DEVELOPER) }

        view.aboutRequests().subscribeUntilDetached { view.displayAbout() }
    }

    interface View : Presenter.View {
        fun mainCurrencyRequests(): Observable<Unit>
        fun supportDeveloperRequests(): Observable<Unit>
        fun aboutRequests(): Observable<Unit>

        fun chooseMainCurrency(currencies: List<Currency>): Observable<Currency>

        fun showMainCurrency(mainCurrency: Currency)
        fun showSubscriptionType(subscriptionType: SubscriptionType)

        fun displayLogin(destination: Destination)
        fun displaySupportDeveloper()
        fun displayAbout()
    }
}