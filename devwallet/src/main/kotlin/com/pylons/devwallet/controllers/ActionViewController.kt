package com.pylons.devwallet.controllers

import com.pylons.wallet.core.Core
import tornadofx.*

class ActionViewController : Controller() {
    private val resultViewController: ResultViewController by inject()

    fun getAccount() {
        val profile = Core.engine.getOwnBalances()
        resultViewController.results.value = profile.toString()
    }

    fun getTransaction(id: String) {
        val tx = Core.engine.getTransaction(id)
        resultViewController.results.value = tx.toString()
    }

    fun getItems() {
        val profile = Core.engine.getOwnBalances()
        resultViewController.items.value = profile?.items?.observable()
    }

    fun listTrades() {
        val trades = Core.engine.listTrades()
        resultViewController.trades.value = trades.observable()
    }

    fun listCookbooks() {
        val cookbooks = Core.engine.listCookbooks()
        resultViewController.cookbooks.value = cookbooks.observable()
    }

    fun listRecipes() {
        val recipes = Core.engine.listRecipes()
        resultViewController.recipes.value = recipes.observable()
    }
}