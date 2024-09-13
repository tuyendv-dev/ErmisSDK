package network.ermis.state.utils

import network.ermis.client.utils.extensions.snakeToLowerCamelCase
import network.ermis.core.models.AndFilterObject
import network.ermis.core.models.AutocompleteFilterObject
import network.ermis.core.models.BeloFilterChannelObject
import network.ermis.core.models.Channel
import network.ermis.core.models.ContainsFilterObject
import network.ermis.core.models.CustomObject
import network.ermis.core.models.DistinctFilterObject
import network.ermis.core.models.EqualsFilterObject
import network.ermis.core.models.ExistsFilterObject
import network.ermis.core.models.FilterObject
import network.ermis.core.models.GreaterThanFilterObject
import network.ermis.core.models.GreaterThanOrEqualsFilterObject
import network.ermis.core.models.InFilterObject
import network.ermis.core.models.LessThanFilterObject
import network.ermis.core.models.LessThanOrEqualsFilterObject
import network.ermis.core.models.Member
import network.ermis.core.models.NeutralFilterObject
import network.ermis.core.models.NorFilterObject
import network.ermis.core.models.NotEqualsFilterObject
import network.ermis.core.models.NotExistsFilterObject
import network.ermis.core.models.NotInFilterObject
import network.ermis.core.models.OrFilterObject
import java.lang.ClassCastException
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

private const val MEMBERS_FIELD_NAME = "members"
private const val MEMBERSHIP_ROLE_FIELD_NAME = "membership"

internal fun <T : CustomObject> Collection<T>.filter(filterObject: FilterObject): List<T> =
    filter { filterObject.filter(it) }

@Suppress("UNCHECKED_CAST")
internal fun <T : CustomObject> FilterObject.filter(t: T): Boolean = try {
    when (this) {
        is AndFilterObject -> filterObjects.all { it.filter(t) }
        is OrFilterObject -> filterObjects.any { it.filter(t) }
        is NorFilterObject -> filterObjects.none { it.filter(t) }
        is ContainsFilterObject -> when (fieldName) {
            MEMBERS_FIELD_NAME -> t.getMembersId().contains(value)
            else -> t.getMemberPropertyOrExtra(fieldName, List::class)?.contains(value) ?: false
        }
        is AutocompleteFilterObject -> t.getMemberPropertyOrExtra(fieldName, String::class)?.contains(value) ?: false
        is ExistsFilterObject -> t.getMemberPropertyOrExtra(fieldName, Any::class) != null
        is NotExistsFilterObject -> t.getMemberPropertyOrExtra(fieldName, Any::class) == null
        is EqualsFilterObject -> value == t.getMemberPropertyOrExtra(fieldName, value::class)
        is NotEqualsFilterObject -> value != t.getMemberPropertyOrExtra(fieldName, value::class)
        is GreaterThanFilterObject ->
            compare(t.getMemberPropertyOrExtra(fieldName, value::class) as? Comparable<Any>, value as? Comparable<Any>) { it > 0 }
        is GreaterThanOrEqualsFilterObject ->
            compare(t.getMemberPropertyOrExtra(fieldName, value::class) as? Comparable<Any>, value as? Comparable<Any>) { it >= 0 }
        is LessThanFilterObject ->
            compare(t.getMemberPropertyOrExtra(fieldName, value::class) as? Comparable<Any>, value as? Comparable<Any>) { it < 0 }
        is LessThanOrEqualsFilterObject ->
            compare(t.getMemberPropertyOrExtra(fieldName, value::class) as? Comparable<Any>, value as? Comparable<Any>) { it <= 0 }
        is InFilterObject -> when (fieldName) {
            MEMBERS_FIELD_NAME -> values.any(t.getMembersId()::contains)
            MEMBERSHIP_ROLE_FIELD_NAME -> values.any(listOf(t.getMemberShipChannelRole())::contains)
            else -> {
                val fieldValue = t.getMemberPropertyOrExtra(fieldName, Any::class)
                if (fieldValue is List<*>) {
                    values.any(fieldValue::contains)
                } else {
                    values.contains(fieldValue)
                }
            }
        }
        is NotInFilterObject -> when (fieldName) {
            MEMBERS_FIELD_NAME -> values.none(t.getMembersId()::contains)
            else -> {
                val fieldValue = t.getMemberPropertyOrExtra(fieldName, Any::class)
                if (fieldValue is List<*>) {
                    values.none(fieldValue::contains)
                } else {
                    !values.contains(fieldValue)
                }
            }
        }
        is DistinctFilterObject -> (t as? Channel)?.let { channel ->
            channel.type == "messaging" &&
                channel.members.size == memberIds.size &&
                channel.members.map { it.user.id }.containsAll(memberIds)
        } ?: false
        NeutralFilterObject -> true
        // TODO tuyendv add filter channels new belo
        is BeloFilterChannelObject -> (t as? Channel)?.let { channel ->
            (this.type.isNullOrEmpty() || this.type == channel.type) &&
                channel.members.map { it.user.id }.containsAll(this.members) &&
                (channel.membership?.channelRole in this.roles) &&
                (this.project_id.isNullOrEmpty() || channel.id.contains(this.project_id!!))
        } ?: false
    }
} catch (e: ClassCastException) {
    false
}

@Suppress("UNCHECKED_CAST")
private fun <T : Any> CustomObject.getMemberPropertyOrExtra(name: String, clazz: KClass<out T>): T? =
    name.snakeToLowerCamelCase().let { fieldName ->
        this::class.memberProperties.firstOrNull { it.name == fieldName }?.getter?.call(this)?.cast(clazz)
            ?: this.getExtra(name, clazz)
    }

private fun <T : Any> Any.cast(clazz: KClass<out T>): T = clazz.javaObjectType.cast(this)!!

private fun <T : Comparable<T>> compare(a: T?, b: T?, compareFun: (Int) -> Boolean): Boolean =
    a?.let { notNullA ->
        b?.let { notNullB ->
            compareFun(notNullA.compareTo(notNullB))
        }
    } ?: false

private fun CustomObject.getMembersId(): List<String> =
    getMemberPropertyOrExtra(MEMBERS_FIELD_NAME, List::class)?.mapNotNull { (it as? Member)?.getUserId() } ?: emptyList()

private fun CustomObject.getMemberShipChannelRole(): String =
    getMemberPropertyOrExtra(MEMBERSHIP_ROLE_FIELD_NAME, Member::class)?.channelRole ?: ""

@Suppress("UNCHECKED_CAST")
private fun <T : Any> CustomObject.getExtra(name: String, clazz: KClass<out T>): T? =
    extraData[name]?.let {
        when (clazz) {
            Double::class -> (it as? Number)?.toDouble()
            Float::class -> (it as? Number)?.toFloat()
            Long::class -> (it as? Number)?.toLong()
            Int::class -> (it as? Number)?.toInt()
            Char::class -> (it as? Number)?.toChar()
            Short::class -> (it as? Number)?.toShort()
            Byte::class -> (it as? Number)?.toByte()
            else -> it
        }
    } as? T
