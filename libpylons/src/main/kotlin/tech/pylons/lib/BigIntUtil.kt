package tech.pylons.lib

import tech.pylons.lib.types.tx.recipe.*
import java.math.BigDecimal

/**
 * converter from LONG|DOUBLE to BigInt support
 */
object BigIntUtil {

    fun long2bigInt(value: String): String {
        return value.toBigDecimal().multiply(BigDecimal.valueOf(1000000000000000000)).toBigInteger().toString()
    }

    fun toDoubleParam(doubleParam: DoubleParam): DoubleParam {
        var weightRanges = mutableListOf<DoubleWeightRange>()
        doubleParam.weightRanges.forEach { doubleWeightRange->
            weightRanges.add(
                DoubleWeightRange(
                    weight = doubleWeightRange.weight,
                    upper = long2bigInt(doubleWeightRange.upper),
                    lower = long2bigInt(doubleWeightRange.lower)
                )
            )
        }

        return DoubleParam(
            key= doubleParam.key,
            weightRanges = weightRanges.toList(),
            program = doubleParam.program
        )
    }

    fun toDoubleInputParam(doubleInputParam: DoubleInputParam): DoubleInputParam {
        return DoubleInputParam(
            key = doubleInputParam.key,
            minValue = long2bigInt(doubleInputParam.minValue),
            maxValue = long2bigInt(doubleInputParam.maxValue)
        )
    }

    fun toLongParam(longParam: LongParam): LongParam {
        return LongParam(
            key= longParam.key,
            weightRanges = longParam.weightRanges,
            program = longParam.program
        )

    }

    fun toStringParam(stringParam: StringParam): StringParam {
        return StringParam(
            key = stringParam.key,
            value = stringParam.value,
            program = stringParam.program
        )
    }

    fun toStringKeyValueParam(stringKeyValueParam: StringKeyValue): StringKeyValue {
        return StringKeyValue(
            Key = stringKeyValueParam.Key,
            Value = stringKeyValueParam.Value
        )
    }


    fun toItemModifyOutput(output: ItemModifyOutput): ItemModifyOutput {
        var doubles = mutableListOf<DoubleParam>()
        var longs = mutableListOf<LongParam>()
        var strings = mutableListOf<StringParam>()
        var mutableStrings = mutableListOf<StringKeyValue>()

        output.doubles.forEach { value->
            doubles.add(toDoubleParam(value))
        }

        output.longs.forEach { value->
            longs.add(toLongParam(value))
        }

        output.strings.forEach { value->
            strings.add(toStringParam(value))
        }

        output.mutableStrings.forEach { value->
            mutableStrings.add(toStringKeyValueParam(value))
        }

        return ItemModifyOutput(
            ID = output.ID,
            itemInputRef = output.itemInputRef,
            doubles = doubles.toList(),
            longs = longs.toList(),
            strings = strings.toList(),
            mutableStrings = mutableStrings.toList(),
            transferFee = output.transferFee,
            tradePercentage = output.tradePercentage,
            quantity = output.quantity,
            amountMinted = output.amountMinted,
            tradeable = output.tradeable
        )
    }

    fun toItemOutput(itemOutput: ItemOutput): ItemOutput {
        var doubles = mutableListOf<DoubleParam>()
        var longs = mutableListOf<LongParam>()
        var strings = mutableListOf<StringParam>()
        var mutableStrings = mutableListOf<StringKeyValue>()

        itemOutput.doubles.forEach { value->
            doubles.add(toDoubleParam(value))
        }

        itemOutput.longs.forEach { value->
            longs.add(toLongParam(value))
        }

        itemOutput.strings.forEach { value->
            strings.add(toStringParam(value))
        }

        itemOutput.mutableStrings.forEach { value->
            mutableStrings.add(toStringKeyValueParam(value))
        }

        return ItemOutput(
            id = itemOutput.id,
            doubles = doubles.toList(),
            longs = longs.toList(),
            strings = strings.toList(),
            mutableStrings = mutableStrings.toList(),
            transferFee = itemOutput.transferFee,
            tradePercentage = itemOutput.tradePercentage,
            quantity = itemOutput.quantity,
            amountMinted = itemOutput.amountMinted,
            tradeable = itemOutput.tradeable
        )
    }

    fun toItemInput(itemInput: ItemInput): ItemInput {
        var doubles = mutableListOf<DoubleInputParam>()
        var longs = mutableListOf<LongInputParam>()
        var conditionDoubles = mutableListOf<DoubleInputParam>()

        itemInput.conditions.doubles.forEach {
            conditionDoubles.add(toDoubleInputParam(it))
        }

        itemInput.longs.forEach {
            longs.add(it)
        }

        itemInput.doubles.forEach {
            doubles.add(toDoubleInputParam(it))
        }

        return ItemInput(
            id = itemInput.id,
            conditions = ConditionList(
                doubles = conditionDoubles,
                longs = itemInput.conditions.longs,
                strings = itemInput.conditions.strings
            ),
            doubles = doubles,
            longs = longs,
            strings = itemInput.strings
        )
    }
}