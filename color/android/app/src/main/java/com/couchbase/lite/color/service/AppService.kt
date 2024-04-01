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

package com.couchbase.lite.color.service

import android.content.Context
import com.couchbase.lite.Collection
import com.couchbase.lite.CouchbaseLite
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.couchbase.lite.LogLevel
import com.couchbase.lite.MutableArray
import com.couchbase.lite.Parameters
import com.couchbase.lite.Query
import com.couchbase.lite.VectorEncoding
import com.couchbase.lite.VectorIndexConfiguration
import com.couchbase.lite.color.util.Zip
import java.io.File

private const val DATASET_ZIP_FILE = "db.cblite2.zip"
private const val DATASET_FILE = "db.cblite2"
private const val DATASET_UNZIP_DIR = "unzip/"
private const val DATABASE_NAME = "db"
private const val COLLECTION_NAME = "colors"
private const val INDEX_NAME = "colors_index"

/** Color data model */
data class ColorObject(val id: String, val name: String, val rgb: List<Int>, val distance: Double)

/** App Service for searching similar colors from Couchbase Lite Database */
class AppService (private val context: Context) {
    private val lock = Any()

    private var db: Database? = null
    private lateinit var collection: Collection
    private var query: Query? = null

    companion object {
        fun init(context: Context) {
            CouchbaseLite.init(context)
        }
    }

    /**
     * Initialize the App's database by loading the dataset,
     * create a vector index and pre-create a query object.
     */
    fun initialize() = synchronized(lock) {
        if (db != null) {  return }

        // Load dataset:
        db = loadDataset()
        collection = db!!.getCollection(COLLECTION_NAME)!!

        // Create vector index:
        val config = VectorIndexConfiguration("colorvect_l2", 3, 2)
        config.encoding = VectorEncoding.none()
        collection.createIndex(INDEX_NAME, config)
    }

    /** Search similar colors. */
    fun search(color: List<Int>) : List<ColorObject> = synchronized(lock) {
        // Check if the database is initialized:
        checkInitialized()

        // Create the query object:
        if (query == null) {
            val sql = "SELECT id, color, colorvect_l2, VECTOR_DISTANCE($INDEX_NAME) " +
                    "FROM $COLLECTION_NAME " +
                    "WHERE vector_match($INDEX_NAME, \$vector , 8)"
            query = db!!.createQuery(sql)
        }

        // Set $vector parameter on the query:
        val params = Parameters()
        params.setArray("vector", MutableArray(color))
        query!!.parameters = params

        // Execute search and return result:
        val colors = mutableListOf<ColorObject>()
        query!!.execute().use { rs ->
            for (result in rs) {
                val id = result.getString(0)!!
                val name = result.getString(1)!!
                val rgb = result.getArray(2)!!.toList() as List<Int>
                val distance = result.getDouble(3)
                colors.add(ColorObject(id, name, rgb, distance))
            }
        }
        return colors
    }

    /** Load dataset. */
    private fun loadDataset() : Database {
        if (!Database.exists(DATABASE_NAME, null)) {
            val unzipDir = File(context.filesDir, DATASET_UNZIP_DIR)
            unzipDir.deleteRecursively()
            unzipDir.mkdirs()

            Zip.unzip(context.assets.open(DATASET_ZIP_FILE), unzipDir)
            Database.copy(File(unzipDir, DATASET_FILE), DATABASE_NAME, DatabaseConfiguration())
        }
        return Database(DATABASE_NAME)
    }

    /** Check if the database initialization is done */
    private fun checkInitialized() {
        if (db == null) {
            throw IllegalStateException("Database is not ready")
        }
    }
}