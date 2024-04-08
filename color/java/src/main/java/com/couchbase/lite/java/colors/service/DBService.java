//
// Copyright (c) 2024 Couchbase, Inc All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package com.couchbase.lite.java.colors.service;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.couchbase.lite.Array;
import com.couchbase.lite.Collection;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseConfiguration;
import com.couchbase.lite.MutableArray;
import com.couchbase.lite.Parameters;
import com.couchbase.lite.Query;
import com.couchbase.lite.Result;
import com.couchbase.lite.ResultSet;
import com.couchbase.lite.VectorEncoding;
import com.couchbase.lite.VectorIndexConfiguration;
import com.couchbase.lite.java.colors.util.FileUtils;


public final class DBService {
    private final Object lock = new Object();
    private Database db;

    public void init() throws CouchbaseLiteException, IOException {
        synchronized (lock) {
            Database db = this.db;
            if (db != null) { return; }

            db = loadDataset();
            this.db = db;
        }

        Collection coll = db.getCollection("colors");
        if (coll == null) { throw new IllegalStateException("Collection 'colors' does not exist"); }

        VectorIndexConfiguration config = new VectorIndexConfiguration("colorvect_l2", 3L, 2L);
        config.setEncoding(VectorEncoding.none());
        coll.createIndex("colors_index", config);
    }

    @NonNull
    public List<NamedColor> search(@NonNull NamedColor color) throws CouchbaseLiteException {
        synchronized (lock) {
            if (this.db == null) { throw new IllegalStateException("Database is not ready"); }
        }

        Query q = db.createQuery("SELECT id, color, colorvect_l2, VECTOR_DISTANCE(colors_index)"
            + " FROM colors"
            + " WHERE vector_match(colors_index, $vector , 8)");

        Parameters params = new Parameters();
        params.setArray("vector", new MutableArray(color.getRGBVector()));
        q.setParameters(params);

        List<NamedColor> colors = new ArrayList<>();
        try (ResultSet rs = q.execute()) {
            for (Result result: rs.allResults()) {
                final String id = result.getString(0);
                if (id == null) { continue; }

                final String name = result.getString(2);
                if (name == null) { continue; }

                final Array rgb = result.getArray(2);
                if ((rgb == null) || (rgb.count() != 3)) { continue; }

                final double distance = result.getDouble(3);

                final String r = rgb.getString(0);
                if (r == null) { continue; }
                final String g = rgb.getString(1);
                if (g == null) { continue; }
                final String b = rgb.getString(2);
                if (b == null) { continue; }

                colors.add(new NamedColor(id, name, r, g, b, distance));
            }
        }

        return colors;
    }

    private Database loadDataset() throws IOException, CouchbaseLiteException {
        File unzipDir = new File(new File(""), ".zip-tmp");
        FileUtils.eraseFileOrDir(unzipDir);
        unzipDir.mkdirs();

        InputStream is = FileUtils.getAsset("db.cblite2.zip");
        FileUtils.unzip(is, unzipDir);

        Database.copy(new File(unzipDir, "db.cblite2"), "db", new DatabaseConfiguration());

        return new Database("db");
    }
}
