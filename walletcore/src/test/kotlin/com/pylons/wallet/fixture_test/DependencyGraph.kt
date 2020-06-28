package com.pylons.wallet.fixture_test

import com.pylons.wallet.fixture_test.evtesting.TestContext
import com.pylons.wallet.fixture_test.types.FixtureStep
import kotlinx.coroutines.sync.Mutex

object DependencyGraph {
    val vMap: MutableMap<String, Int> = mutableMapOf()
    val vMapMutex: Mutex = Mutex()
    var nv: Int = 0
    val adj: MutableList<MutableList<Int>> = mutableListOf()

    fun addVertex(vsId : String) : Boolean {
        return when {
            (vMap.containsKey(vsId)) -> false
            else -> {
                vMap[vsId] = nv
                adj.add(mutableListOf())
                nv++
                true
            }
        }
    }

    fun addEdge(vsId : String, wsId : String) : Boolean {
        return when {
            (!vMap.containsKey(vsId) || !vMap.containsKey(wsId)) -> false
            else -> {
                val v = vMap[vsId]!!
                val w = vMap[wsId]!!
                adj[v].add(w)
                true
            }
        }
    }

    private fun isCyclicUtil (v : Int, visited : MutableList<Boolean>, recStack : MutableList<Boolean>) : Boolean {
        if (!visited[v]) {
            visited[v] = true
            recStack[v] = true
            adj.forEachIndexed { w, _ ->
                if (!visited[w] && isCyclicUtil(w, visited, recStack))
                    return true
                else if (recStack[w]) return false
            }
        }
        recStack[v] = false
        return false
    }

    fun isCyclic () : Boolean {
        val visited = mutableListOf<Boolean>()
        val recStack = mutableListOf<Boolean>()
        for (i in 0 until nv) {
            visited.add(false)
            recStack.add(false)
        }
        for (i in 0 until nv) {
            if (isCyclicUtil(i, visited, recStack)) return true
        }
        return false
    }

    suspend fun checkSteps (steps : List<FixtureStep>, t : TestContext) {
        vMapMutex.lock()
        steps.forEach {
            if (it.id.isEmpty()) t.fatal("please add ID field for all steps")
            if (!addVertex(it.id)) {
                t.withFields(mapOf("stepID" to it.id)).fatal("same stepID is available")
            }
        }

        steps.forEach { step ->
            step.runAfter.preconditions.forEach { pd ->
                if (!addEdge(pd, step.id)) {
                    t.withFields(mapOf("precondition" to pd))
                            .fatal("ID is not available which is refering to.")
                }
            }
        }

        vMapMutex.unlock()
        if (isCyclic()) t.fatal("cyclic dependency is available")
    }
}