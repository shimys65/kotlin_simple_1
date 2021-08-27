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