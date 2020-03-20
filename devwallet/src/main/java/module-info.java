module com.pylons.devwallet {
    exports com.pylons.devwallet;
    exports com.pylons.devwallet.controllers;
    exports com.pylons.devwallet.views;

    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires kotlin.stdlib;
    requires tornadofx;
    requires com.pylons.wallet.core;
}