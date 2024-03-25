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

package com.couchbase.lite.color.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.couchbase.lite.color.R
import com.couchbase.lite.color.service.ColorObject

class ListViewAdapter : RecyclerView.Adapter<ListViewAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val colorView : View
        val idTextView: TextView
        val nameTextView: TextView
        val distanceTextView: TextView
        init {
            colorView = itemView.findViewById(R.id.colorView)
            idTextView = itemView.findViewById(R.id.idTextView)
            nameTextView = itemView.findViewById(R.id.nameTextView)
            distanceTextView = itemView.findViewById(R.id.distanceTextView)
        }
    }

    private var items: List<ColorObject> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context) .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val color = items[position]
        val rgb = color.rgb
        holder.colorView.setBackgroundColor(Color.rgb(rgb[0], rgb[1], rgb[2]))
        holder.idTextView.text = color.id
        holder.nameTextView.text = color.name
        holder.distanceTextView.text = String.format("%.0f", color.distance)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(items: List<ColorObject> ) {
        this.items = items
        notifyDataSetChanged()
    }
}