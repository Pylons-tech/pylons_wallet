/*
 * Copyright by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pylons.lib.types;

import java.io.ByteArrayOutputStream;

/**
 * Java functions for use w/ Bech32Cosmos.
 * Exists mainly just b/c doing bitwise operations on 8-bit values in Kotlin is a pain.
 */
public class Bech32CosmosExtensions {
    public static final int WITNESS_PROGRAM_LENGTH_PKH = 20;
    public static final int WITNESS_PROGRAM_LENGTH_SH = 32;
    public static final int WITNESS_PROGRAM_MIN_LENGTH = 2;
    public static final int WITNESS_PROGRAM_MAX_LENGTH = 40;

    /**
     * Helper for re-arranging bits into groups.
     * Appropriated from bitcoinj codebase; see
     * https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/SegwitAddress.java
     */
    public static byte[] convertBits(final byte[] in, final int inStart, final int inLen, final int fromBits,
                                      final int toBits, final boolean pad) throws Exception {
        int acc = 0;
        int bits = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(64);
        final int maxv = (1 << toBits) - 1;
        final int max_acc = (1 << (fromBits + toBits - 1)) - 1;
        for (int i = 0; i < inLen; i++) {
            int value = in[i + inStart] & 0xff;
            if ((value >>> fromBits) != 0) {
                throw new Exception(
                        String.format("Input value '%X' exceeds '%d' bit size", value, fromBits));
            }
            acc = ((acc << fromBits) | value) & max_acc;
            bits += fromBits;
            while (bits >= toBits) {
                bits -= toBits;
                out.write((acc >>> bits) & maxv);
            }
        }
        if (pad) {
            if (bits > 0)
                out.write((acc << (toBits - bits)) & maxv);
        } else if (bits >= fromBits || ((acc << (toBits - bits)) & maxv) != 0) {
            throw new Exception("Could not convert bits, invalid padding");
        }
        return out.toByteArray();
    }
}