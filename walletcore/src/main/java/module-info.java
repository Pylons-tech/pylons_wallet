module com.pylons.wallet.core {
    exports com.pylons.wallet.core;
    exports com.pylons.wallet.core.constants;
    exports com.pylons.wallet.core.engine;
    exports com.pylons.wallet.core.engine.crypto;
    exports com.pylons.wallet.core.ops;
    exports com.pylons.wallet.core.types;
    exports com.pylons.wallet.core.types.jsonTemplate;
    exports com.pylons.wallet.core.types.tx;
    exports com.pylons.wallet.core.types.tx.msg;
    exports com.pylons.wallet.core.types.tx.recipe;

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
}