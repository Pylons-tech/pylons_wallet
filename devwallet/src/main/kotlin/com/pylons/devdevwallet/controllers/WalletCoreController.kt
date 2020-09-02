package com.pylons.devdevwallet.controllers

import tornadofx.*
import java.util.*
import kotlin.concurrent.thread


class CoreInteractEvent(val action : Function<Unit>) : FXEvent()

@ExperimentalUnsignedTypes
class WalletCoreController : Controller() {
    private object FunctionQueue : Queue<() -> Unit> {
        @Volatile
        var isDirty = false

        private val backingList = LinkedList<() -> Unit>()
        override fun contains(element: (() -> Unit)?): Boolean =
                backingList.contains(element)

        override fun addAll(elements: Collection<() -> Unit>): Boolean =
                backingList.addAll(elements)

        override fun clear() =
                backingList.clear()

        override fun element(): () -> Unit =
                backingList.element()

        override fun isEmpty(): Boolean =
                backingList.isEmpty()

        override fun remove(): () -> Unit =
                backingList.remove()

        override val size: Int = backingList.size

        override fun containsAll(elements: Collection<() -> Unit>): Boolean =
                backingList.containsAll(elements)

        override fun iterator(): MutableIterator<() -> Unit> =
                backingList.iterator()

        override fun remove(element: (() -> Unit)?): Boolean =
                backingList.remove(element)

        override fun removeAll(elements: Collection<() -> Unit>): Boolean =
                backingList.removeAll(elements)

        override fun add(element: (() -> Unit)?): Boolean {
            backingList.addLast(element)
            return true // no capacity limit on function queue
        }

        override fun offer(e: (() -> Unit)?): Boolean = add(e)

        override fun retainAll(elements: Collection<() -> Unit>): Boolean =
                backingList.retainAll(elements)

        override fun peek(): () -> Unit =
                backingList.peekLast()

        override fun poll(): () -> Unit =
                backingList.pollLast()

    }

    private val coreThread: Thread = thread {
        println("ct setup")
        if (FunctionQueue.isDirty) {
            FunctionQueue.poll().invoke()
            if (FunctionQueue.size == 0) FunctionQueue.isDirty = false
        }
        else Thread.sleep(100)
    }

    init {
        println("ccc")
        coreThread
        subscribe<CoreInteractEvent> { evt ->
            FunctionQueue.add { evt.action }
            FunctionQueue.isDirty = true
        }
    }
}