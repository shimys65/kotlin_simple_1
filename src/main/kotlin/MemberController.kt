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