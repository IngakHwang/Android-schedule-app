/*
  Copyright 2019 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.sdk.sample.common.internal

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.kakao.sdk.sample.common.R
import io.reactivex.Single
import kotlinx.android.synthetic.main.activity_friends.*

class FriendsActivity : AppCompatActivity() {
    private lateinit var adapter: FriendsAdapter
    private lateinit var result: ResultReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        val items =
            intent.extras!!.getParcelableArrayList<PickerItem>("items")
                ?: throw IllegalStateException()
        result = intent.extras!!["result"] as ResultReceiver
        adapter = FriendsAdapter(items)
        friendsView.layoutManager = LinearLayoutManager(this)
        friendsView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        friendsView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_friends, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.menu_send -> {
                val selectedFriends = adapter.items.filter { it.checked }.map { it.id }
                val bundle = Bundle()
                bundle.putStringArrayList("items", ArrayList(selectedFriends))
                result.send(Activity.RESULT_OK, bundle)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        result.send(Activity.RESULT_CANCELED, Bundle())
    }

    companion object {
        fun startForResult(
            context: Context,
            items: List<PickerItem>
        ): Single<List<String>> {
            return Single.create {
                startForResult(context, items) { selectedItems ->
                    it.onSuccess(selectedItems)
                }
            }
        }

        fun startForResult(
            context: Context,
            items: List<PickerItem>,
            callback: (selectedItems: List<String>) -> Unit
        ) {
            val resultReceiver = object: ResultReceiver(Handler(Looper.getMainLooper())) {
                override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                    callback(resultData?.getStringArrayList("items").orEmpty())
                }
            }
            context.startActivity(
                Intent(context, FriendsActivity::class.java)
                    .putParcelableArrayListExtra("items", ArrayList<PickerItem>(items))
                    .putExtra("result", resultReceiver)
            )
        }
    }
}
