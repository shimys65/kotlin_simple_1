
class ArticleRepository {

    fun getArticles(): List<Article> {
        val articles = mutableListOf<Article>()
        val lastId = getLastId()
        for (id in 1..lastId) {
            val article = articleFromFile("data/article/$id.json")

            if (article != null) {
                articles.add(article)
            }
        }
        return articles
    }

    fun articleFromFile(jsonFilePath: String): Article? {
        val jsonStr = readStrFromFile(jsonFilePath)
        if (jsonStr == "") {
            return null
        }
        val map = mapFromJson(jsonStr)

        val id = map["id"].toString().toInt()
        val regDate = map["regDate"].toString()
        var updateDate = map["updateDate"].toString()
        val boardId = map["boardId"].toString().toInt()
        val memberId = map["memberId"].toString().toInt()
        var title = map["title"].toString()
        var body = map["body"].toString()

        return Article(id, regDate, updateDate, boardId, memberId, title, body)
    }

    fun getLastId(): Int {
        val lastId = readIntFromFile("data/article/lastId.txt", 0)
        return lastId                        //txt에서 가져 올거 없으면 0, default 선언
    }

    private fun setLastId(newLastId: Int) {
        writeIntFile("data/article/lastId.txt", newLastId)
    }

    fun deleteArticle(article: Article) {
        deleteFile("data/article/${article.id}.json")
    }

    fun getArticleByID(id: Int) : Article? {
        // 파일에서 객체 얻기
        val article = articleFromFile("data/article/$id.json")
        return article
    }

    fun writeArticle(boardId: Int, memberId: Int, title: String, body: String): Int {
        // 파일 생성
        val id = getLastId() + 1
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()

        val article = Article(id, regDate, updateDate, boardId, memberId, title, body)
        val jsonStr = article.toJson()
        writeStrFile("data/article/${article.id}.json", jsonStr)

        setLastId(id)
        return id
    }

    fun makeTestArticle() {
        for (id in 1..20) {
            writeArticle(id % 2 + 1,id % 9 +1, "제목_$id", "내용_$id") //1 ~ 10(멤버 수)
        }
    }


    fun articleModify(id: Int, title: String, body: String) {
        val article = getArticleByID(id)!!

        article.title = title
        article.body = body
        article.updateDate = Util.getNowDateStr()

        val jsonStr = article.toJson()
        writeStrFile("data/article/${article.id}.json", jsonStr)
    }

    fun getFilteredArticles(boardCode: String, searchKeyword: String, page: Int, itemsCountInAPage: Int): List<Article> {
        // 1차 searchKeyword로 필터돤 것을 저장
        val filtered1Articles = getSearchKeywordFilteredArticles(getArticles(), boardCode, searchKeyword)
        // 2차 page로 필터돤 것을 저장
        val filtered2Articles = getPageFilteredArticles(filtered1Articles, page, itemsCountInAPage)

        return filtered2Articles
    }


    private fun getSearchKeywordFilteredArticles(articles: List<Article>, boardCode: String,
        searchKeyword: String): List<Article> {

        if (boardCode.isEmpty() && searchKeyword.isEmpty()) {
            return articles
        }
        val filteredArticles = mutableListOf<Article>()

        val boardId = if (boardCode.isEmpty()) {
            0
        } else {
            boardRepository.getBoardByCode(boardCode)!!.id
        }

        for (article in articles) {

            if (boardId != 0) {
                if (article.boardId != boardId) {
                    continue
                }
            }

            if (searchKeyword.isNotEmpty()) {
                if (!article.title.contains(searchKeyword)) {
                    continue
                }
            }
            filteredArticles.add(article)
        }
        return filteredArticles
    }

    private fun getPageFilteredArticles(articles: List<Article>, page: Int, itemsCountInAPage: Int): List<Article> {
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