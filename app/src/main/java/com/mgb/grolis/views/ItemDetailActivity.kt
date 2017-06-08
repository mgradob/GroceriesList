package com.mgb.grolis.views

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.firebase.database.*
import com.mgb.grolis.R
import com.mgb.grolis.models.ItemModel
import kotlinx.android.synthetic.main.activity_item_detail.*
import kotlinx.android.synthetic.main.view_item_detail_content.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info

class ItemDetailActivity : AppCompatActivity() {

    private lateinit var mPresenter: ItemDetailPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        val itemUid = intent.extras["uid"] as String

        mPresenter = ItemDetailPresenter(this, uid = itemUid)
        mPresenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.stop()
    }

    fun updateUi(item: ItemModel) {
        with(item) {
            mItemNotesTextView.text = notes
            mItemQuantityTextView.text = quantity.toString()

            mItemDetailCollapsingToolbar.title = name
            mItemDetailCollapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.primary_light))

            if (done) mItemDoneFab.visibility = View.GONE
            else mItemDoneFab.setOnClickListener { mPresenter.updateItem(true) }
        }
    }
}

class ItemDetailPresenter(val mView: ItemDetailActivity, val context: Context = mView.applicationContext, val uid: String) : AnkoLogger {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val itemRef: DatabaseReference = database.getReference("items").child(uid)

    fun start() = syncDb()

    fun stop() {}

    private fun syncDb() {
        itemRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
                error("Failed to read value:\n$err")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val item = snapshot.getValue(ItemModel::class.java)

                info("Database info: $item")

                mView.updateUi(item)
            }
        })
    }

    fun updateItem(checked: Boolean) {
        val childUpdate = HashMap<String, Any>()
        childUpdate.put("/done", checked)

        itemRef.updateChildren(childUpdate)
    }
}