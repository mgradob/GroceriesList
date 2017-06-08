package com.mgb.grolis.views

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.*
import com.mgb.grolis.R
import com.mgb.grolis.adapters.MainAdapter
import com.mgb.grolis.dialogs.AddItemDialog
import com.mgb.grolis.models.ItemModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import org.jetbrains.anko.startActivity
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    private val mPresenter: MainPresenter by lazy { MainPresenter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.setSupportActionBar(mMainToolbar)

        mPresenter.start()
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.stop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_item -> {
                showAddItemDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun updateUi(items: ArrayList<ItemModel>) {
        Collections.sort(items, compareBy { it.done })

        mItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        mItemsRecyclerView.adapter = MainAdapter(items, mPresenter::updateItem, mPresenter::goToItemDetail)
    }

    fun showAddItemDialog() {
        val addItemDialog = AddItemDialog()
        addItemDialog.setAddItemListener(object : AddItemDialog.AddItemListener {
            override fun onAddClicked(name: String, notes: String, quantity: Int) = mPresenter.addItem(name, notes, quantity)
        })
        addItemDialog.show(supportFragmentManager, "add_item")
    }
}

class MainPresenter(val mView: MainActivity, val context: Context = mView.applicationContext) : AnkoLogger {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val itemsRef: DatabaseReference = database.getReference("items")

    fun start() = syncDb()

    fun stop() {}

    private fun syncDb() {
        itemsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(err: DatabaseError) {
                error("Failed to read value:\n$err")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val items = arrayListOf<ItemModel>()

                for (child in snapshot.children) {
                    val item = child.getValue(ItemModel::class.java)
                    item.uid = child.key

                    info("Database info: $item")

                    items.add(item)
                }

                mView.updateUi(items)
            }
        })
    }

    fun addItem(name: String, notes: String, quantity: Int) {
        val key = itemsRef.push().key
        val item = ItemModel(key, name, notes, quantity)

        itemsRef.child(key).updateChildren(item.toMap())
    }

    fun updateItem(item: ItemModel, checked: Boolean) {
        val childUpdate = HashMap<String, Any>()
        childUpdate.put("/done", checked)

        itemsRef.child(item.uid).updateChildren(childUpdate)
    }

    fun goToItemDetail(item: ItemModel) = context.startActivity<ItemDetailActivity>("uid" to item.uid)
}