data class Member(
    val id: Int,
    val regDate: String,
    var updateDate: String,
    val loginId: String,
    val loginPw: String,
    val name: String,
    val nickname: String,
    val cellphoneNo: String,
    val email: String
    ) {
    fun toJson(): String {
        var jsonStr = ""

        jsonStr += "{"
        jsonStr += "\r\n"

        jsonStr += "\t" + """ "id":$id """.trim() + ","

        jsonStr += "\r\n"

        jsonStr += "\t" + """ "regDate":"$regDate" """.trim() + ","

        jsonStr += "\r\n"

        jsonStr += "\t" + """ "updateDate":"$updateDate" """.trim() + ","

        jsonStr += "\r\n"

        jsonStr += "\t" + """ "loginId":"$loginId" """.trim() + ","

        jsonStr += "\r\n"

        jsonStr += "\t" + """ "loginPw":"$loginPw" """.trim() + ","

        jsonStr += "\r\n"

        jsonStr += "\t" + """ "name":"$name" """.trim() + ","

        jsonStr += "\r\n"

        jsonStr += "\t" + """ "nickname":"$nickname" """.trim() + ","

        jsonStr += "\r\n"

        jsonStr += "\t" + """ "cellphoneNo":"$cellphoneNo" """.trim() + ","

        jsonStr += "\r\n"

        jsonStr += "\t" + """ "email":"$email" """.trim()

        jsonStr += "\r\n"

        jsonStr += "}"

        return jsonStr
    }
}
