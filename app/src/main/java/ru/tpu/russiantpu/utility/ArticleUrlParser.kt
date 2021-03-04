package ru.tpu.russiantpu.utility

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.tpu.russiantpu.main.enums.ContentType
import ru.tpu.russiantpu.main.items.Item
import ru.tpu.russiantpu.main.items.LinkItem

object ArticleUrlParser {

    // Асинхронный парсинг ссылки
    fun navigateDeepLink(url: String,
                         mainActivityItems: MainActivityItems,
                         onLinkParsed: (item: Item?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val item = getItemFromDeepLink(url, mainActivityItems)
            withContext(Dispatchers.Main) {
                onLinkParsed(item)
            }
        }
    }

    // Получение соответствующего предмета Item в зависимости от ссылки
    // (Item необходим для перехода к определенному фрагменту через класс FragmentReplacer)
    private fun getItemFromDeepLink(url: String, mainActivityItems: MainActivityItems?): Item? {
        // Разделяем url на части
        val urlArgs = url.split("://", "/")

        // Проверяем префикс
        if (urlArgs[0] != "pretpu") {
            return null
        }

        // Возвращаем item с типом фрагмента, который надо открыть
        val item = LinkItem().apply { id = urlArgs[2] }
        return when (urlArgs[1]) {
            "article" -> {
                item.apply {
                    idArticle = id
                    type = ContentType.ARTICLE
                }
            }
            "articleList" -> {
                item.apply { type = ContentType.FEED_LIST }
            }
            "linksList" -> {
                mainActivityItems?.let { findItemById(item.id, it.getItems()) }
            }
            else -> null
        }
    }

    private fun findItemById(id: String, items: List<LinkItem>): LinkItem? {
        return items.asSequence().flatMap { deepFlatten(it).asSequence() }.find { it.id == id }
    }

    // Рекурсивное получение единого списка из children переданного linkItem
    private fun deepFlatten(item: LinkItem, result: ArrayList<LinkItem> = ArrayList()): List<LinkItem> {
        val children = item.children
        if (children != null) {
            result.add(item)
            for (element in children) {
                deepFlatten(element, result)
            }
        } else {
            result.add(item)
        }
        return result
    }
}