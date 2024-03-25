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

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object Zip {
    @Throws(IOException::class)
    fun unzip(input: InputStream, destDir: File) {
        if (!destDir.exists()) {
            destDir.mkdirs()
        }

        ZipInputStream(input).use { zin ->
            var entry: ZipEntry? = zin.nextEntry
            while (entry != null) {
                val destFile = File(destDir, entry.name)
                if (entry.isDirectory) {
                    destFile.mkdirs()
                } else {
                    destFile.outputStream().buffered(1024).use { zin.copyTo(it) }
                    zin.closeEntry()
                }
                entry = zin.nextEntry
            }
        }
    }
}