//
//  Copyright (c) 2024 Couchbase, Inc All rights reserved.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//  http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package com.couchbase.lite.color

import android.graphics.Color
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.couchbase.lite.color.databinding.ActivityMainBinding
import com.couchbase.lite.color.model.MainViewModel
import com.couchbase.lite.color.model.SearchResult
import com.couchbase.lite.color.model.ServiceStatus
import com.couchbase.lite.color.ui.ListViewAdapter
import com.couchbase.lite.color.util.RGB
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val model by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // List View:
        val listView = viewBinding.listView
        listView.adapter = ListViewAdapter()

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        listView.layoutManager = layoutManager

        val decoration = DividerItemDecoration(listView.context, layoutManager.orientation)
        listView.addItemDecoration(decoration)

        // Display Color View:
        val colorView = viewBinding.colorView

        // Search View:
        val searchView = viewBinding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                try {
                    val rgb = RGB.vector(query)
                    colorView.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
                    model.search(rgb)
                } catch (e: Exception) {
                    displayMessage("Error:" + e.message)
                }
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    override fun onStart() {
        super.onStart()
        model.serviceStatus.observe(this, this::onServiceStatusChanged)
        model.searchResult.observe(this, this::onSearchResultChanged)
        model.init()
    }

    private fun onServiceStatusChanged(status: ServiceStatus) {
        if (status.error != null) {
            displayMessage("Error:" + status.error)
        }
    }

    private fun onSearchResultChanged(result: SearchResult) {
        val adapter = viewBinding.listView.adapter as ListViewAdapter
        adapter.updateItems(result.colors ?: emptyList())
        if (result.error != null) {
            displayMessage("Search Error: " + result.error)
        }
    }

    private fun displayMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}