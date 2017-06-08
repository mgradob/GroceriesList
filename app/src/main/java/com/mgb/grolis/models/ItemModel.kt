package com.mgb.grolis.models

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Created by mgradob on 6/7/17.
 */
@IgnoreExtraProperties
class ItemModel() {

    var uid: String = ""
    var name: String = ""
    var notes: String = ""
    var quantity: Int = 0
    var done: Boolean = false

    constructor(uid: String, name: String, notes: String, quantity: Int) : this() {
        this.uid = uid
        this.name = name
        this.notes = notes
        this.quantity = quantity
    }

    fun toMap(): Map<String, Any>{
        val result = HashMap<String, Any>()

        result.put("name", name)
        result.put("notes", notes)
        result.put("quantity", quantity)
        result.put("done", done)

        return result
    }

    override fun toString(): String {
        return "ItemModel(uid='$uid', name='$name', notes='$notes', quantity=$quantity, done=$done)"
    }
}