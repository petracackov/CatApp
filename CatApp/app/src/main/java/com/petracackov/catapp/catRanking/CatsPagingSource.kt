package com.petracackov.catapp.catRanking

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.petracackov.catapp.data.CatApi
import com.petracackov.catapp.data.CatModel

class CatsPagingSource: PagingSource<Int, CatModel>() {

    override fun getRefreshKey(state: PagingState<Int, CatModel>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CatModel> {
       return try {
            val page = params.key ?: 0
            val size = params.loadSize
            val data = CatApi.retrofitService.getCatsList(page = page, limit = size)
            val cats = data.body().orEmpty()
            LoadResult.Page(
                data = cats,
                prevKey = null,
                nextKey = if (cats.isEmpty()) null else (page + 1)
            )
        } catch (e: java.lang.Exception) {
            LoadResult.Error(e)
        }
    }
}