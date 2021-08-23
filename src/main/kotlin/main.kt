import kotlin.time.measureTimedValue

/*
fun readLineTrim() = readLine()!!.trim()

fun main() {
    println("== SIMPLE SSG 시작 ==")

    while ( true ) {
        print("명령어> ")
        val command = readLineTrim()
        val commandBits = command.split("?", limit = 2)
        val url = commandBits[0]
        val paramStr = commandBits[1]
        val paramMap = mutableMapOf<String, String>()

        // paramStr = id=2&title=안녕&body=ㅋㅋ
        val paramStrBits = paramStr.split("&")

        for (paramStrBit in paramStrBits) {
            val paramStrBitBits = paramStrBit.split("=", limit = 2)
            val key = paramStrBitBits[0]
            val value = paramStrBitBits[1]

            paramMap[key] = value
        }

        when (url) {
            "/system/exit" -> {
                println("프로그램이 종료됩니다.")
                break
            }
            "/article/detail" -> {
                val id = paramMap["id"]!!.toInt()

                println("${id}번 게시물을 선택하였습니다.")
            }
        }
    }

    println("== SIMPLE SSG 끝 ==")
}

 */

fun readLineTrim() = readLine()!!.trim()

fun main() {
    println("== SIMPLE SSG 시작 ==")

    while (true) {
        print("명령어> ")
        val command = readLineTrim()

        // 입력받은 command를 ?를 기준으로 둘로 나눔. /article/detail?id=2&title=we&body=us
        val commandBits = command.split("?", limit = 2)
        val url = commandBits[0] // ? 좌측 첫번째 command. /article/detail
        val paramStr = commandBits[1] // ? 우측 두번째 command. id=2&title=we
        // Tip: /article/detail와 id=12&title=we를 각각 변수에 저장했기에 paramStr을 split 가능하다
        // 아래의 paramStrBits을 '='로 split하기 위헤서는 id=2, title=we, body=us를 각각 저장해야 함

        // "자세히 보기"를 위해, 원하는 id만을 추출하기위한 map 사용하기
        val paramMap = mutableMapOf<String, String>()

        // paramStr에서 'id=2' 만을 추출하기 위해 '&'에서 split 함
        val paramStrBits = paramStr.split("&") //id=2, title=we, body=us로 나누어짐

        // '2'만을 추출하기 위해 '='를 기준으로 좌=key,우=value로 하는 map에 저장하는 for문 사용
        for (paramStrBit in paramStrBits) {
            val paramStrBitBits = paramStrBit.split("=", limit = 2)
            val key = paramStrBitBits[0]
            val value = paramStrBitBits[1]

            paramMap[key] = value  // 2, we, us 출력 paramMap["id"] -> 2가 출력
        }


        when (url) {
            "/system/exit" -> {
                println("시스템 종료")
                break
            }

            "/article/detail" -> {
                val i = paramMap["id"]!!.toInt()

                println("${i}번째 게시물 선택")
            }
        }

    }

    println("== SIMPLE SSG 끝 ==")
}









