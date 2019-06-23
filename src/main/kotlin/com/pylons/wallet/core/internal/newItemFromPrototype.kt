package com.pylons.wallet.core.internal

import kotlin.math.*
import com.pylons.wallet.core.Core
import com.pylons.wallet.core.types.*
import kotlin.random.Random

/**
 * Returns a new Item matching the given prototype.
 * Never, ever, ever call this in production code. Items shouldn't be created locally; they should be
 * created in the blockchain. This exists to make the dummy implementation work.
 * Not particularly rigorous. If you give it a set of constraints that contradict themselves, it'll do something
 * stupid.
 */

private var itemsCreated = 0

internal fun newItemFromPrototype (itemPrototype: ItemPrototype) : Item {
    val id = "${itemsCreated++}_[ITEM]_${Random.nextLong()}" // garbage, but we just need a unique address
    val strings = mutableMapOf<String, String>()
    itemPrototype.stringConstraints.orEmpty().forEach{
        var strBuilder = StringBuilder()
        var set = mutableSetOf<String>()
        it.value.forEach{
            if (it.mode == ConstraintMode.EXACT_MATCH) strBuilder.delete(0, strBuilder.length); strBuilder.append(it.value)
            if (it.mode == ConstraintMode.STRING_INCLUDES) strBuilder.append(it.value)
            if (it.mode == ConstraintMode.ONE_OF_THESE) set.add(it.value)
        }
        strings[it.key] = when (set.size){
            0 -> strBuilder.toString()
            else -> set.random()
        }
    }
    val longs = mutableMapOf<String, Long>()
    itemPrototype.longConstraints.orEmpty().forEach{
        var v : Long = 0
        var set = mutableSetOf<Long>()
        it.value.forEach{
            if (it.mode == ConstraintMode.EXACT_MATCH) v = it.value
            if (it.mode == ConstraintMode.NUM_LESS_THAN && v > it.value) v = it.value - 1
            if (it.mode == ConstraintMode.NUM_MORE_THAN && v < it.value) v = it.value + 1
            if (it.mode == ConstraintMode.ONE_OF_THESE) set.add(it.value)
        }
        longs[it.key] = when (set.size) {
            0 -> v
            else -> set.random()
        }
    }
    val doubles = mutableMapOf<String, Double>()
    itemPrototype.doubleConstraints.orEmpty().forEach{
        var v : Double = 0.0
        var set = mutableSetOf<Double>()
        it.value.forEach{
            if (it.mode == ConstraintMode.EXACT_MATCH) v = it.value
            if (it.mode == ConstraintMode.NUM_LESS_THAN && v > it.value) v = it.value.nextDown()
            if (it.mode == ConstraintMode.NUM_MORE_THAN && v < it.value) v = it.value.nextUp()
            if (it.mode == ConstraintMode.ONE_OF_THESE) set.add(it.value)
        }
        doubles[it.key] = when (set.size) {
            0 -> v
            else -> set.random()
        }
    }
    return Item(id = id, strings = strings, longs = longs, doubles = doubles)
}