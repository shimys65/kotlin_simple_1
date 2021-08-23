fun readLineTrim() = readLine()!!.trim()

fun main() {
    println("== SIMPLE SSG 시작 ==")

    while (true) {
        print("명령어> ")
        val command = readLineTrim()
        
        val rq = Rq(command)
        println(rq.getStringParam("title") == "제목1") // true
        println(rq.getIntParam("id") == 1) // true
    }

    println("== SIMPLE SSG 끝 ==")
}

class Rq(command: String) {
    val actionPath: String
    val paramMap: Map<String, String>

    init {
        val commandBits = command.split("?", limit = 2)

        // url path 부분을 저장
        actionPath = commandBits[0].trim() // '?'의 왼쪽이 저장

        // /article/detail?id=2&title=we&body=us
        // *** 파라메터 부분('?'의 오른쪽)을 저장하기위해 입력한 내용이 있는지 검증 ***
        // commandBits.lastIndex는 ?가 있으면 1을 반환, 없으면 '0'을 반환
        // commandBits[1].isNotEmpty()는 '?' 뒤에 아무것도 없으면 fales, 있으면 true 반환
        val queryStr = if (commandBits.lastIndex == 1 && commandBits[1].isNotEmpty()) {
            commandBits[1].trim()
        } else {
            ""
        }

        paramMap = if (queryStr.isEmpty()) {
            mapOf()
        } else {
            val paramMapTemp = mutableMapOf<String, String>()

            val queryStrBits = queryStr.split("&")

            for (queryStrBit in queryStrBits) {
                val queryStrBitBits = queryStrBit.split("=", limit = 2)
                val paramName = queryStrBitBits[0]

                // queryStrBitBits.lastIndex는 '='가 있으면 1을 반환
                val paramValue = if (queryStrBitBits.lastIndex == 1 && queryStrBitBits[1].isNotEmpty()) {
                    queryStrBitBits[1].trim()
                } else {
                    ""
                }

                if (paramValue.isNotEmpty()) {
                    paramMapTemp[paramName] = paramValue
                }
            }

            paramMapTemp.toMap()
        }
    }

    fun getStringParam(name: String): String {
        return paramMap[name]!!
    }

    fun getIntParam(name: String): Int {
        return paramMap[name]!!.toInt()
    }

}









