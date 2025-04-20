package com.atfotiad.pokemonexplorerapp.paging

data class PagingIndex(
    val limit: Int,
    val offset: Int,
    val total: Int
) {
    fun pagingIndexing(
        offset: Int,
        totalFromServer: Int,
        totalFromPaging: Int
    ): Pair<PagingIndex?, PagingIndex?> {
        var nextKeyOffset: Int? = offset + loadSize
        if (offset != 0 && offset >= totalFromPaging) {
            nextKeyOffset = null
        }
        var prevKeyOffset: Int? = offset
        if (offset == 0) {
            prevKeyOffset = null
        }
        val prevKey = prevKeyOffset?.let {
            PagingIndex(limit = loadSize, it, totalFromServer)
        }
        val nextKey = nextKeyOffset?.let {
            PagingIndex(limit = loadSize, it, totalFromServer)
        }
        return prevKey to nextKey
    }
}
