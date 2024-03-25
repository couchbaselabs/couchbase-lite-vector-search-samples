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

package com.couchbase.lite.color.util

import android.graphics.Color

/** Generate RGB vectors from hex color codes or 3 space-separated numbers. */
object RGB {
    fun vector(colorCode: String) : List<Int> {
        if (colorCode.length == 6) {
            val color = Color.parseColor("#$colorCode")
            val r = color shr 16 and 0xff
            val g = color shr 8 and 0xff
            val b = color and 0xff
            return listOf(r, g, b)
        } else {
            val comps = colorCode.split(" ")
            if (comps.count() != 3) {
                throw IllegalArgumentException("Invalid Color Code")
            }
            val result = mutableListOf<Int>()
            for (c in comps) {
                var value = -1;
                try { value = c.toInt() } catch (e: Exception) { }
                if (value < 0 || value > 255) {
                    throw IllegalArgumentException("Invalid Color Code")
                }
                result.add(value)
            }
            return result
        }
    }
}