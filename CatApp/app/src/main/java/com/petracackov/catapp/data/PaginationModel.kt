package com.petracackov.catapp.data

data class PaginationData(
    val limit: Int,
    val page: Int,
    val count: Int
) {
    companion object {
        operator fun invoke(header: Iterable<Pair<String, String>>): PaginationData {
            val count = header.first { (key, _) -> key == "pagination-count" }.second.toInt()
            val limit = header.first { (key, _) -> key == "pagination-limit" }.second.toInt()
            val page = header.first { (key, _) -> key == "pagination-page" }.second.toInt()
            return PaginationData(count, limit, page)
        }
    }
}