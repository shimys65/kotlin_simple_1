class BoardRepository {
    val boards = mutableListOf<Board>()
    var lastId = 0

    fun getFilteredBoards(): List<Board> {
        return boards
    }

    fun getBoardByName(name: String): Board? {
        for (board in boards) {
            if (board.name == name) {
                return board
            }
        }
        return  null
    }

    fun getBoardByCode(code: String): Board? {
        for (board in boards) {
            if (board.code == code) {
                return board
            }
        }
        return  null

    }

    fun makeBoard(name: String, code: String): Int {
        val id = ++lastId
        val regDate = Util.getNowDateStr()
        val updateDate = Util.getNowDateStr()

        boards.add(Board(id, regDate, updateDate, name, code))

        return id

    }

}
