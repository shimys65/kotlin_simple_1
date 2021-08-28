
import java.text.SimpleDateFormat


fun readLineTrim() = readLine()!!.trim()

fun main() {
    println("== SIMPLE SSG 시작 ==")

    memberRepository.makeTestMembers()
    articleRepository.makeTestArticle()

    // controller 클래스 생성후 각 객체 만들기
    val systemController = SystemController()
    val boardController = BoardController()
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

            "/board/list" -> {
                boardController.list(rq)
            }

            "/board/add" -> {
                boardController.add(rq)
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



// 세션 시작
var loginedMember: Member? = null //로그인 상태 식별
// 세션 끝
val memberRepository = MemberRepository()
val articleRepository = ArticleRepository()
val boardRepository = BoardRepository()