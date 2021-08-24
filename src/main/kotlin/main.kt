
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
            "/system/init" -> {
                println("시스템 종료")
                break
            }
            "/article/detail" -> {
                val id = rq.getIntParam("id", 0)

                if (id == 0){
                    println("id를 입력해 주세요.")
                    continue
                }
                val article = articleRepository.articles[id-1]
                println(article)
            }
        }
    }

    println("== SIMPLE SSG 끝 ==")
}

data class Article(
    val id: Int, val regDate: String, val updateDate: String, val title: String, val body: String,
)

object articleRepository {
    val articles = mutableListOf<Article>()
    var lastId = 0

    fun addArticle(title: String, body: String) {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()

        articles.add(Article(id, regDate, updateDate, title, body ))
    }

    fun makeTestArticle() {
        for (id in 0..100) {
            addArticle("제목_$id", "내용_$id" )
        }
    }
}

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









