package walletcore.internal

import kotlin.math.*
import walletcore.Core
import walletcore.types.*

/**
 * Returns a new Item matching the given prototype.
 * Never, ever, ever call this in production code. Items shouldn't be created locally; they should be
 * created in the blockchain. This exists to make the dummy implementation work.
 * Not particularly rigorous. If you give it a set of constraints that contradict themselves, it'll do something
 * stupid.
 */

private var itemsCreated = 0

internal fun newItemFromPrototype (itemPrototype: ItemPrototype) : Item {
    val id = "${itemsCreated++}_[ITEM]_${Core.txHandler.getNewUserId()}" // garbage, but we just need a unique id
    val strings = mutableMapOf<String, String>()
    itemPrototype.stringConstraints.orEmpty().forEach{
        var strBuilder = StringBuilder()
        it.value.forEach{
            if (it.mode == ConstraintMode.EXACT_MATCH) strBuilder.delete(0, strBuilder.length); strBuilder.append(it.value)
            if (it.mode == ConstraintMode.STRING_INCLUDES) strBuilder.append(it.value)
        }
        strings[it.key] = strBuilder.toString()
    }
    val longs = mutableMapOf<String, Long>()
    itemPrototype.longConstraints.orEmpty().forEach{
        var v : Long = 0
        it.value.forEach{
            if (it.mode == ConstraintMode.EXACT_MATCH) v = it.value
            if (it.mode == ConstraintMode.NUM_LESS_THAN && v > it.value) v = it.value - 1
            if (it.mode == ConstraintMode.NUM_MORE_THAN && v < it.value) v = it.value + 1
        }
        longs[it.key] = v
    }
    val doubles = mutableMapOf<String, Double>()
    itemPrototype.doubleConstraints.orEmpty().forEach{
        var v : Double = 0.0
        it.value.forEach{
            if (it.mode == ConstraintMode.EXACT_MATCH) v = it.value
            if (it.mode == ConstraintMode.NUM_LESS_THAN && v > it.value) v = it.value.nextDown()
            if (it.mode == ConstraintMode.NUM_MORE_THAN && v < it.value) v = it.value.nextUp()
        }
    }
    return Item(id = id, strings = strings, longs = longs, doubles = doubles)
}