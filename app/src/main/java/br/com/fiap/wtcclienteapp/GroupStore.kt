package br.com.fiap.wtcclienteapp

import java.util.UUID

data class Group(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val memberIds: List<String>,
    val memberNames: List<String>
)

object GroupStore {
    private val groups: MutableList<Group> = mutableListOf()

    fun createGroup(name: String, memberIds: List<String>, memberNames: List<String>): Group {
        val group = Group(name = name, memberIds = memberIds, memberNames = memberNames)
        groups.add(group)
        return group
    }

    fun listGroups(): List<Group> = groups.toList()

    fun groupsForMember(memberIdOrName: String): List<Group> = groups.filter {
        it.memberIds.contains(memberIdOrName) || it.memberNames.contains(memberIdOrName)
    }
}


