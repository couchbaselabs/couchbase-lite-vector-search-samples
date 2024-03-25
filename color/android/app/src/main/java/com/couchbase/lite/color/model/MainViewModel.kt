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

package com.couchbase.lite.color.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.lite.color.service.ColorObject
import com.couchbase.lite.color.service.AppService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val service: AppService) : ViewModel() {
    val serviceStatus = MutableLiveData(ServiceStatus(false, null))

    val searchResult = MutableLiveData(SearchResult(emptyList(), null))

    fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            var error: String? = null
            try {
                service.initialize()
            } catch (e: Exception) {
                error = getErrorMessage(e)
            }
            serviceStatus.postValue(ServiceStatus(error == null, error))
        }
    }

    fun search(color: List<Int>) {
        viewModelScope.launch(Dispatchers.IO) {
            var colors: List<ColorObject>? = null
            var error: String? = null
            try {
                colors = service.search(color)
            } catch (e: Exception) {
                error = getErrorMessage(e)
            }
            searchResult.postValue(SearchResult(colors, error))
        }
    }

    private fun getErrorMessage(e: Exception): String {
        return if (e.message != null) e.message!! else "Unknown Error"
    }
}

data class ServiceStatus(val ready: Boolean, val error: String?)

data class SearchResult(val colors: List<ColorObject>?, val error: String?)