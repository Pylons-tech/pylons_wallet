package com.pylons.devwallet.controllers

import com.pylons.wallet.core.Core
import tornadofx.*

class ActionViewController: Controller() {
    private val resultViewController: ResultViewController by inject()

    fun getAccount() {
        val profile = Core.engine.getOwnBalances()
        resultViewController.results.value =  profile.toString()
    }

    fun getTransaction(id: String) {
        val tx = Core.engine.getTransaction(id)
        resultViewController.results.value = tx.toString()
    }

    fun getItems() {
        val profile = Core.engine.getOwnBalances()
        resultViewController.results.value =  profile?.items.toString()
    }

    fun listTrades() {
        val trades = Core.engine.listTrades()
        resultViewController.results.value = trades.toString()
    }

    fun listCookbooks() {
        val cookbooks = Core.engine.listCookbooks()
        resultViewController.results.value = cookbooks.toString()
    }

    fun listRecipes() {
        val recipes = Core.engine.listRecipes()
        resultViewController.results.value = recipes.toString()
    }
}