
import java.text.SimpleDateFormat

var loginedMember: Member? = null //로그인 상태 식별

val memberRepository = MemberRepository()
val articleRepository = ArticleRepository()
val boardRepository = BoardRepository()

fun main() {
    println("== SIMPLE SSG 시작 ==")

    //memberRepository.makeTestMembers()
    boardRepository.makeTestBoards()
    //articleRepository.makeTestArticle()

    // controller 클래스 생성후 각 객체 만들기
    val systemController = SystemController()
    val boardController = BoardController()
    val articleController = ArticleController()
    val memberController = MemberController()

    // 1번 회원으로 로그인 된 상태로 시작한다.
    loginedMember = memberRepository.getMemberById(1)

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

            "/board/make" -> {
                boardController.make(rq)
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



