class MemberRepository {
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