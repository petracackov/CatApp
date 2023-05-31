package com.petracackov.catapp.catRanking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.petracackov.catapp.data.CatModel
import kotlinx.coroutines.flow.*

class RankingsViewModel: ViewModel() {

    val cats: Flow<PagingData<CatModel>> = Pager(
        pagingSourceFactory = { CatsPagingSource() },
        config = PagingConfig(pageSize = 10)
    ).flow.cachedIn(viewModelScope)

}