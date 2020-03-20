open module com.pylons.wallet.walletcore_test {
    requires com.pylons.wallet.core;
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core;
    requires tuweni.bytes;
    requires klaxon;
    requires org.bouncycastle.provider;
    requires org.apache.commons.codec;
    requires bip39;
    requires bip32;
    requires model;
    requires bip39.wordlist.en;
    requires tuweni.crypto;
    requires jsr305;
    requires tuweni.units;
    requires com.google.common;
    requires tuweni.io;
    requires org.junit.jupiter.api;
    requires org.apache.commons.lang3;
}