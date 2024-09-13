package network.ermis.client.parser

import network.ermis.core.models.AndFilterObject
import network.ermis.core.models.AutocompleteFilterObject
import network.ermis.core.models.BeloFilterChannelObject
import network.ermis.core.models.ContainsFilterObject
import network.ermis.core.models.DistinctFilterObject
import network.ermis.core.models.EqualsFilterObject
import network.ermis.core.models.ExistsFilterObject
import network.ermis.core.models.FilterObject
import network.ermis.core.models.GreaterThanFilterObject
import network.ermis.core.models.GreaterThanOrEqualsFilterObject
import network.ermis.core.models.InFilterObject
import network.ermis.core.models.LessThanFilterObject
import network.ermis.core.models.LessThanOrEqualsFilterObject
import network.ermis.core.models.NeutralFilterObject
import network.ermis.core.models.NorFilterObject
import network.ermis.core.models.NotEqualsFilterObject
import network.ermis.core.models.NotExistsFilterObject
import network.ermis.core.models.NotInFilterObject
import network.ermis.core.models.OrFilterObject

@Suppress("ComplexMethod")
internal fun FilterObject.toMap(): Map<String, Any?> = when (this) {
    is BeloFilterChannelObject -> {
        val mapFilter: HashMap<String, Any> = hashMapOf(KEY_MEMBERS to this.members, KEY_MEMBER_ROLE to this.roles)
        if (this.type != null) mapFilter.put(KEY_CHANNEL_TYPE, this.type!!)
        if (this.project_id != null) mapFilter.put(KEY_PROJECT_ID, this.project_id!!)
        mapFilter
    }
    is AndFilterObject -> mapOf(KEY_AND to this.filterObjects.map(FilterObject::toMap))
    is OrFilterObject -> mapOf(KEY_OR to this.filterObjects.map(FilterObject::toMap))
    is NorFilterObject -> mapOf(KEY_NOR to this.filterObjects.map(FilterObject::toMap))
    is ExistsFilterObject -> mapOf(this.fieldName to mapOf(KEY_EXIST to true))
    is NotExistsFilterObject -> mapOf(this.fieldName to mapOf(KEY_EXIST to false))
    is EqualsFilterObject -> mapOf(this.fieldName to this.value)
    is NotEqualsFilterObject -> mapOf(this.fieldName to mapOf(KEY_NOT_EQUALS to this.value))
    is ContainsFilterObject -> mapOf(this.fieldName to mapOf(KEY_CONTAINS to this.value))
    is GreaterThanFilterObject -> mapOf(this.fieldName to mapOf(KEY_GREATER_THAN to this.value))
    is GreaterThanOrEqualsFilterObject -> mapOf(this.fieldName to mapOf(KEY_GREATER_THAN_OR_EQUALS to this.value))
    is LessThanFilterObject -> mapOf(this.fieldName to mapOf(KEY_LESS_THAN to this.value))
    is LessThanOrEqualsFilterObject -> mapOf(this.fieldName to mapOf(KEY_LESS_THAN_OR_EQUALS to this.value))
    is InFilterObject -> mapOf(this.fieldName to mapOf(KEY_IN to this.values))
    is NotInFilterObject -> mapOf(this.fieldName to mapOf(KEY_NOT_IN to this.values))
    is AutocompleteFilterObject -> mapOf(this.fieldName to mapOf(KEY_AUTOCOMPLETE to this.value))
    is DistinctFilterObject -> mapOf(KEY_DISTINCT to true, KEY_MEMBERS to this.memberIds)
    is NeutralFilterObject -> emptyMap<String, String>()
}

private const val KEY_EXIST: String = "\$exists"
private const val KEY_CONTAINS: String = "\$contains"
private const val KEY_AND: String = "\$and"
private const val KEY_OR: String = "\$or"
private const val KEY_NOR: String = "\$nor"
private const val KEY_NOT_EQUALS: String = "\$ne"
private const val KEY_GREATER_THAN: String = "\$gt"
private const val KEY_GREATER_THAN_OR_EQUALS: String = "\$gte"
private const val KEY_LESS_THAN: String = "\$lt"
private const val KEY_LESS_THAN_OR_EQUALS: String = "\$lte"
private const val KEY_IN: String = "\$in"
private const val KEY_NOT_IN: String = "\$nin"
private const val KEY_AUTOCOMPLETE: String = "\$autocomplete"

private const val KEY_DISTINCT: String = "distinct"
private const val KEY_MEMBERS: String = "members"
private const val KEY_CHANNEL_TYPE: String = "type"
private const val KEY_PROJECT_ID: String = "project_id"
private const val KEY_MEMBER_ROLE: String = "roles"
