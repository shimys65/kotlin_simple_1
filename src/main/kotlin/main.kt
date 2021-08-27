
import java.text.SimpleDateFormat


fun readLineTrim() = readLine()!!.trim()

fun main() {
    println("== SIMPLE SSG 시작 ==")

    memberRepository.makeTestMembers()
    articleRepository.makeTestArticle()

    // controller 클래스 생성후 각 객체 만들기
    val systemController = SystemController()
    val articleController = ArticleController()
    val memberController = MemberController()

    while (true) {
        val propmpt = if (loginedMember == null){
            "명령어> "
        } else {
            "${loginedMember!!.nickname}> "
        }
        print(propmpt)
        val command = readLineTrim()
        
        val rq = Rq(command)

        when (rq.actionPath) {
            "/system/exit" -> {
                systemController.exit(rq)
                break
            }

            "/member/logout" -> {
                memberController.logout(rq)
            }

            "/member/login" -> {
                memberController.login(rq)
            }

            // 새로운 멤버 추가
            "/member/join" -> {
                memberController.join(rq)
            }

            "/article/write" -> {
                articleController.write(rq)
            }

            "/article/list" -> {
                articleController.list(rq)
            }

            "/article/detail" -> {
                articleController.detail(rq)
            }

            "/article/modify" -> {
                articleController.modify(rq)
            }

            "/article/delete" -> {
                articleController.delete(rq)
            }
        } // end fo when
    } // end of while

    println("== SIMPLE SSG 끝 ==")
} //end of Main



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
        }// end of paramMap
        //println(paramMap)
    }//end of init


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

}// end of Rq()

// 세션 시작
var loginedMember: Member? = null //로그인 상태 식별
// 세션 끝


// 컨트롤러 시작
// 시스템 컨트롤러 시작
class SystemController {
    fun exit(rq: Rq) {
        println("시스템 종료")
    }
}
// 시스템 컨트롤러 끝

// 회원 컨트롤러 시작
class MemberController {
    fun logout(rq: Rq) {
        loginedMember = null // 이 변수는 세션으로 선언 해야만 에러 없슴
        println("로그아웃.")
    }

    fun login(rq: Rq) {
        print("로그인아이디 : ")
        val loginId = readLineTrim()

        val member = memberRepository.getMemberByLoginedId(loginId) // 현재 저장되어있는 아이디인지 확인
        if (member == null) {
            println("'$loginId'(은)는 존재하지 않은 아이디입니다")
            return // 함수이기때문에 이전의 continue를 return으로...
        }
        print("로그인비밀번호 : ")
        val loginPw = readLineTrim()
        if (member.loginPw != loginPw) {
            print("비밀번호가 일치하지 않습니다")
            return
        }
        loginedMember = member // 로그인 된 멤버의 객체를 넘겨 로그인 상태 유지 체크

        println("'${member.nickname}'님 환영합니다.")
    }

    fun join(rq: Rq) {
        print("로그인아이디 : ")
        val loginId = readLineTrim()

        // 파라메터로 loginId를 넘겨 멤버가 이미 존재하는지 확인
        val isJoinableLoginId = memberRepository.isJoinableLoginId(loginId)
        if (isJoinableLoginId == false) {
            println("'$loginId'(은)는 이미 사용중인 아이디입니다")
            return
        }

        print("로그인비밀번호 : ")
        val loginPw = readLineTrim()
        print("이름 : ")
        val name = readLineTrim()
        print("별명 : ")
        val nickname = readLineTrim()
        print("휴대전화번호 : ")
        val cellphoneNo = readLineTrim()
        print("이메일 : ")
        val email = readLineTrim()

        val id = memberRepository.join(loginId, loginPw, name, nickname, cellphoneNo, email)

        // 이전에 id가 9이면 새로운 가입자의 id는 '10'이 됨.
        println("${id}번 회원으로 가입되었습니다.")
    }
}
// 회원 컨트롤러 끝


// 게시물 컨트롤러 시작
class ArticleController { // object는 class 생성없이 객체를 바로 만든다
    fun write(rq: Rq) {
        if (loginedMember == null) {
            println("로그인해주세요.")
            return
        }

        print("제목 : ")
        val title = readLineTrim()
        print("내용 : ")
        val body = readLineTrim()

        val id = articleRepository.addArticle(loginedMember!!.id, title, body)

        println("${id}번 게시물이 추가되었습니다.")
    }

    fun list(rq: Rq) {
        val page = rq.getIntParam("page", 1) //입력 명령어가 page
        val searchKeyword = rq.getStringParam("searchKeyword", "") //입력 명령어가 searchKeyword

        val filteredArticles = articleRepository.getFilteredArticles(searchKeyword, page,10)

        println("번호 /      작성날짜      / 작성자 / 제목 / 내용")
        for (article in filteredArticles) {
            val writer = memberRepository.getMemberById(article.memberId)!!
            val writerName = writer.nickname

            println(" ${article.id} / ${article.regDate} / $writerName / ${article.title} / ${article.body}")
        }
    }

    fun detail(rq: Rq) {
        if (loginedMember == null) {
            println("로그인해주세요.")
            return
        }

        val id = rq.getIntParam("id", 0)

        if (id == 0){
            println("id를 입력해 주세요.") // 삭제할 id 입력
            return
        }
        val article = articleRepository.getArticleByID(id)

        if (article == null) {
            println("${id}번 게시물은 존재하지 않습니다.")
            return
        }

        println("번호 : ${article.id}")
        println("작성날짜 : ${article.regDate}")
        println("갱신날짜 : ${article.updateDate}")
        println("제목 : ${article.title}")
        println("내용 : ${article.body}")
    }

    fun modify(rq: Rq) {
        if (loginedMember == null) {
            println("로그인해주세요.")
            return
        }

        val id = rq.getIntParam("id", 0)

        if (id == 0){
            println("id를 입력해 주세요.")
            return
        }
        val article = articleRepository.getArticleByID(id)

        if (article == null) {
            println("${id}번 게시물은 존재하지 않습니다.")
            return
        }

        // 현재 게시물의 사용자와 로그인 사용자가 일치하는지 확인
        if (article.id != loginedMember!!.id) {
            println("권한이 없습니다")
            return
        }

        print("${id}번 게시물 새 제목 : ")
        val title = readLineTrim()
        print("${id}번 게시물 새 제목 : ")
        val body = readLineTrim()

        articleRepository.articleModify(id, title, body)
        println("${id}번 게시물이 수정되었음")
    }

    fun delete(rq: Rq) {
        if (loginedMember == null) {
            println("로그인해주세요.")
            return
        }

        val id = rq.getIntParam("id", 0)

        if (id == 0) {
            println("id를 입력해 주세요.")
            return
        }
        val article = articleRepository.getArticleByID(id)

        if (article == null) {
            println("${id}번 게시물은 존재하지 않습니다.")
            return
        }

        // 현재 게시물의 사용자와 로그인 사용자가 일치하는지 확인
        if (article.id != loginedMember!!.id) {
            println("권한이 없습니다")
            return
        }

        articleRepository.deleteArticle(article)

        println("${id}번 게시물을 삭제하였습니다.")
    }
}
// 게시물 컨트롤러 끝

// 컨트롤러 끝


// 회원 DTO(Data Transfer Object)
data class Member(
    val id: Int, val regDate: String, var updateDate: String, val loginId: String,
    val loginPw: String, val name: String, val nickname: String, val cellphoneNo: String, val email: String)

// 회원 리포지터리
object memberRepository {
    private val members = mutableListOf<Member>()
    private var lastId = 0

    fun join(loginId: String, loginPw: String, name: String, nickName: String, cellphoneNo: String, email: String): Int {
        val id = ++lastId // 가입된 멤버 순서 id, loginId(user1)과 표기는 다르지만 일치 됨.
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()
        members.add(Member(id, regDate, updateDate, loginId, loginPw, name, nickName, cellphoneNo, email))
        //println(members)

        return id
    }

    fun makeTestMembers() { // user1부터 user9까지 사용자 입력, user10은 존재하지 않음, user join하여 입력가능
        for (id in 1..9) {
            join("user${id}", "user${id}", "홍길동${id}", "사용자${id}",
                "0101234123${id}", "user${id}@test.com")
        }
    }

    // 파라메터로 입력 받은 loginId가, 즉 멤버가 존재하는지 확인
    fun isJoinableLoginId(loginId: String): Boolean {
        val member = getMemberByLoginedId(loginId)

        return member == null
    }

    // 현재 저장되어있는 사용자와 로그인하려는 사용자 일치 비교
    fun getMemberByLoginedId(loginId: String): Member? {
        for (member in members) {
            if (member.loginId == loginId) {
                return member
            }
        }
        return  null
    }

    fun getMemberById(memberId: Int): Member? {
        for (member in members) {
            if (member.id == memberId) {
                return member
            }
        }
        return null
    }
}
// 회원 관련 끝


// 게시물 관련 시작
// 게시물 DTO
data class Article(
    val id: Int, val regDate: String, var updateDate: String,  val memberId: Int, var title: String, var body: String,
)

// 게시물 리포지터리
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

}// end of articleRepository
// 게시물 관련 끝



// 유틸관련
object Util {
    fun getNowDateStr(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

        return dateFormat.format(System.currentTimeMillis())
    }
}
// 유틸관련 끝








