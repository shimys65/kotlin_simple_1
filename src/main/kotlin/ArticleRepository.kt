class ArticleRepository {
    val articles = mutableListOf<Article>()
    var lastId = 0

    fun getArticleByID(id: Int) : Article? {
        for (article in articles) {
            if (article.id == id) {
                return article
            }
        }
        return  null
    }

    fun addArticle(memberId: Int, title: String, body: String): Int {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()

        articles.add(Article(id, regDate, updateDate, memberId, title, body))

        return id
    }

    fun makeTestArticle() {
        for (id in 1..20) {
            addArticle(id % 9 +1, "제목_$id", "내용_$id") //1 ~ 10(멤버 수)
        }
    }

    fun deleteArticle(article: Article) {
        articles.remove(article)
    }

    @JvmName("getArticles1")
    fun getArticles(): List<Article> { // mutableListOf가 아닌 List로 하면 권한이 축소, 추가가 안됨.
        return articles
    }

    fun articleModify(id: Int, title: String, body: String) {
        val article = getArticleByID(id)!!

        article.title = title
        article.body = body
        article.updateDate = Util.getNowDateStr()
    }

    fun getFilteredArticles(searchKeyword: String, page: Int, itemsCountInAPage: Int): List<Article> {
        // 1차 searchKeyword로 필터돤 것을 저장
        val filtered1Articles = getSearchKeywordFilteredArticles(articles, searchKeyword)
        // 2차 page로 필터돤 것을 저장
        val filtered2Articles = getPageFilteredArticles(filtered1Articles, page, itemsCountInAPage)

        return filtered2Articles
    }


    private fun getSearchKeywordFilteredArticles(articles: List<Article>, searchKeyword: String): List<Article> {
        val filteredArticles = mutableListOf<Article>()

        for (article in articles) {
            if (article.title.contains(searchKeyword)) {
                filteredArticles.add(article)
            }
        }
        return filteredArticles
    }


    private fun getPageFilteredArticles(filtered1Articles: List<Article>, page: Int, itemsCountInAPage: Int): List<Article> {
        val filteredArticles = mutableListOf<Article>()

        val offsetCount = (page - 1) * itemsCountInAPage

        val startIndex = articles.lastIndex - offsetCount
        var endIndex = startIndex - (itemsCountInAPage - 1)

        if (endIndex < 0) {
            endIndex = 0
        }

        for (i in startIndex downTo endIndex) {
            filteredArticles.add(articles[i])
        }

        return filteredArticles
    }

}