
import java.text.SimpleDateFormat


fun readLineTrim() = readLine()!!.trim()

fun main() {
    println("== SIMPLE SSG 시작 ==")

    articleRepository.makeTestArticle()

    while (true) {
        print("명령어> ")
        val command = readLineTrim()
        
        val rq = Rq(command)

        when (rq.actionPath) {
            "/system/exit" -> {
                println("시스템 종료")
                break
            }
            "/article/detail" -> {
                val id = rq.getIntParam("id", 0)

                if (id == 0){
                    println("id를 입력해 주세요.")
                    continue
                }
                val article = articleRepository.getArticleByID(id)

                if (article == null) {
                    println("${id}번 게시물은 존재하지 않습니다.")
                    continue
                }
                println("번호 : ${article.id}")
                println("작성날짜 : ${article.regDate}")
                println("갱신날짜 : ${article.updateDate}")
                println("제목 : ${article.title}")
                println("내용 : ${article.body}")
            } // end of "/article/detail"

            "/article/modify" -> {
                val id = rq.getIntParam("id", 0)

                if (id == 0){
                    println("id를 입력해 주세요.")
                    continue
                }
                val article = articleRepository.getArticleByID(id)

                if (article == null) {
                    println("${id}번 게시물은 존재하지 않습니다.")
                    continue
                }
                print("${id}번 게시물 새 제목 : ")
                val title = readLineTrim()
                print("${id}번 게시물 새 제목 : ")
                val body = readLineTrim()

                articleRepository.articleModify(id, title, body)
                println("${id}번 게시물이 수정되었음")
            } // end of "/article/modify"

            "/article/list" -> {
                val page = rq.getIntParam("page", 1) //입력 명령어가 page
                val searchKeyword = rq.getStringParam("searchKeyword", "") //입력 명령어가 searchKeyword

                val filteredArticles = articleRepository.getFilteredArticles(searchKeyword, page,10)

                println("번호 / 작성날짜 / 갱신날짜 / 제목 / 내용")
                for (article in filteredArticles) {
                    println("${article.id} / ${article.regDate} / ${article.updateDate} / ${article.title} / ${article.body}")
                }
            } // end of "/article/list"

            "/article/delete" -> {
                val id = rq.getIntParam("id", 0)

                if (id == 0) {
                    println("id를 입력해 주세요.")
                    continue
                }
                val article = articleRepository.getArticleByID(id)

                if (article == null) {
                    println("${id}번 게시물은 존재하지 않습니다.")
                    continue
                }
                articleRepository.deleteArticle(article)
            } // end of "/article/delete"
        }
    }

    println("== SIMPLE SSG 끝 ==")
}

data class Article(
    val id: Int, val regDate: String, var updateDate: String, var title: String, var body: String,
)


object articleRepository {
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

    fun addArticle(title: String, body: String) {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()

        articles.add(Article(id, regDate, updateDate, title, body))
    }

    fun makeTestArticle() {
        for (id in 1..100) {
            addArticle("제목_$id", "내용_$id")
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

} // end of articleRepository

class Rq(command: String) {
    // 전체 URL : /artile/detail?id=1
    // actionPath : /artile/detail
    val actionPath: String

    // 전체 URL : /artile/detail?id=1&title=안녕
    // paramMap : {id:"1", title:"안녕"}
    val paramMap: Map<String, String>

    // actionPath와 paramMap의 초기화
    init {
        val commandBits = command.split("?", limit = 2)

        // url path 부분을 저장
        actionPath = commandBits[0].trim() // '?'의 왼쪽이 저장

        // /article/detail?id=2&title=we&body=us
        // *** 파라메터 부분('?'의 오른쪽)을 저장하기위해 입력한 내용이 있는지 검증 ***
        // lastIndex는 get() = this.size - 1이므로 ?가 있으면 두개로 나누어지므로 1을 반환, '?'가 없으면
        // 나누어지는 것이 없으므로 size가 1이됨 그래서 1-1='0'을 반환
        // commandBits[1].isNotEmpty()는 '?' 뒤에 아무것도 없으면 fales, 있으면 true 반환
        val queryStr = if (commandBits.lastIndex == 1 && commandBits[1].isNotEmpty()) {
            commandBits[1].trim() // '?' 오른쪽을 quertStr에 저장
        } else {
            ""
        } // queryStr 출력은 id=2&title=제목1

        // queryStr에 저장된 파라메터를 split으로 &로 나누고 다시 = 로 나누는 작업, 그리고 paramMap에 저장
        paramMap = if (queryStr.isEmpty()) {
            mapOf()  // 비어있으면 고정된 map에 저장
        } else {
        // 그렇지 않으면 가변 맵, paramMapTemp을 만들어서 paramName에는 키를 paramValue에는 밸류를 저장
            val paramMapTemp = mutableMapOf<String, String>()

            // queryStrBits 출력은 [id=2, title=제목1]
            val queryStrBits = queryStr.split("&")

            for (queryStrBit in queryStrBits) {
                // queryStrBitBits의 출력은 [id, 2] [title, 제목1]
                val queryStrBitBits = queryStrBit.split("=", limit = 2)
                val paramName = queryStrBitBits[0]

                // queryStrBitBits.lastIndex는 '='가 있어야하고 [1]이 비어있지 않으면 true
                val paramValue = if (queryStrBitBits.lastIndex == 1 && queryStrBitBits[1].isNotEmpty()) {
                    queryStrBitBits[1].trim()
                } else {
                    ""
                }
                if (paramValue.isNotEmpty()) {
                    paramMapTemp[paramName] = paramValue
                }
            } // end of for(), 저장된 최종값을 불변 맵에 저장
            paramMapTemp.toMap()
        } // end of paramMap
        //println(paramMap)
    } //end of init


    // 입력 : /article/detail?id=2&title=제목1
    // 입력을 잘못해서 /article/detail 뒤가 없는 경우 예외처리
    fun getStringParam(name: String, default: String): String {
        // 3번
        return paramMap[name] ?: default
        //return paramMap[name]!!

        /* 2번
        return if (paramMap[name] == null) {
            default
        } else {
            paramMap[name]!!
        }*/

        /* 1번
        return try {
            paramMap[name]!!
        }
        catch ( e: NullPointerException ) {
            default
        }*/
    }


    fun getIntParam(name: String, default: Int): Int {
        //        return paramMap[name]!!.toInt()

        return if (paramMap[name] != null) {
            try {
                paramMap[name]!!.toInt()
    // 만약 'id=2번'으로 입력 시 '번'은 toInt()가 안됨.
            } catch (e: NumberFormatException) {
                default
            }
        } else {
            default
        }
    }

}

object Util {
    fun getNowDateStr(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return dateFormat.format(System.currentTimeMillis())
    }
}









