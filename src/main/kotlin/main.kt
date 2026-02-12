import com.sun.source.tree.TryTree

open class Note(
    open val idNote: Int,
    open val id: Int,
    open val date: Int,
    open val text: String,
    open val userId: Int) {

    open fun copy(idNote: Int = this.idNote,
                  id: Int = this.id,
                  date: Int = this.date,
                  text: String = this.text,
                  userId: Int = this.userId): Note {
            return Note(idNote, id, date, text, userId)
   }
    override fun toString(): String {
        return "Comment(idNote=${this.idNote}, id=${this.id}, text='${this.text}')"
    }
}


class Comment(override val idNote: Int,
              override val id: Int,
              override val date: Int,
              override val text: String,
              override val userId: Int) : Note(idNote, id, date, text, userId) {
    override fun copy(
        idNote: Int,
        id: Int,
        date: Int,
        text: String,
        userId: Int
    ): Note {
        return Comment(idNote, id, date, text, userId)
    }

    override fun toString(): String {
        return "Comment(idNote=${this.idNote}, id=${this.id}, text='${this.text}')"
    }
}


class DeletedComment(override val idNote: Int,
                     override val id: Int,
                     override val date: Int,
                     override val text: String,
                     override val userId: Int) : Note(idNote, id, date, text, userId) {
    override fun copy(
        idNote: Int,
        id: Int,
        date: Int,
        text: String,
        userId: Int
    ): Note {
        return DeletedComment(idNote, id, date, text, userId)
    }
    override fun toString(): String {
        return "Comment(idNote=${this.idNote}, id=${this.id}, text='${this.text}')"
    }
}

object NoteService {
    private var notes = emptyList<Note>()

    fun clear() {
        notes = emptyList<Note>()
    }

    fun newId(): Int {
        return notes.maxByOrNull { it.id }?.let { it.id + 1 } ?: 1
    }

    fun getSize(): Int {
        return notes.size
    }

    fun <T: Note> getComments(idNote: Int) : List<T> {
        val сoments: List<Comment> =  notes.filterIsInstance<Comment>().filter{it.idNote==idNote}
        return сoments as List<T>
    }

    fun <T: Note> getDeletedComments(idNote: Int) : List<T> {
        val сoments: List<DeletedComment> =  notes.filterIsInstance<DeletedComment>().filter{it.idNote==idNote}
        return сoments as List<T>
    }

    fun <T: Note> getAllComments(idNote: Int) : List<T> {
        val сoments: List<Any> =  notes.filter{it.idNote==idNote}
        return сoments as List<T>
    }


    fun <T: Note> get(userId: Int) : List<T> {
        return notes.filterIsInstance<Note>().filter{it.userId==userId} as List<T>

    }


    fun <T: Note> getById(id: Int) : Note {
        val myComents: List<Note> = notes.filterIsInstance<Note>().filter { it.id == id }
        if (myComents.size == 0) {
            throw IllegalArgumentException("Не найдена запись с id ${id}")
        } else {
            return myComents[0]
        }
    }


    fun add(note: Note) {
        val newNote = if (note.id == 0) note.copy(id = newId()) else note.copy()
        notes = notes.plus(newNote)
    }


    fun createComment(comment: Note) {
        if (comment !is Comment && comment !is DeletedComment) throw IllegalArgumentException("Неверный тип комментария")

        val newNote = if (comment.id == 0) comment.copy(id = newId()) else comment.copy()
        notes = notes.plus(newNote)
    }


    fun delete(idNote: Int) {
        // Полностью удаляет все комментарии к заметке с заданным id и саму заметку
        notes = notes.minus(getAllComments<Comment>(idNote))
        notes = notes.minus(getById<Note>(idNote))
    }

    fun deleteComment(id: Int) {
        // Помечает комментарий удаленным
        val currentRecord = getById<Comment>(id)
        if (currentRecord !is Comment) throw IllegalArgumentException("Комментария с таким id не найден")

        notes = notes.minus(currentRecord)
        createComment(DeletedComment(currentRecord.idNote,
            currentRecord.id,
            currentRecord.date,
            currentRecord.text,
            currentRecord.userId))
    }

    fun restoreComment(id: Int) {
        // Восстанавливает ранее удаленный комментарий
        val currentRecord = getById<Comment>(id)
        if (currentRecord !is DeletedComment) throw IllegalArgumentException("Удаленный комментария с таким id не найден")

        notes = notes.minus(currentRecord)
        createComment(Comment(currentRecord.idNote,
            currentRecord.id,
            currentRecord.date,
            currentRecord.text,
            currentRecord.userId))
    }

    fun edit(id: Int, text: String) {
        val currentRecord = getById<Note>(id)
        if (currentRecord is Comment || currentRecord is DeletedComment) throw IllegalArgumentException("Заметка с id-${id} не найдена")

        notes = notes.minus(currentRecord)
        add(Note(currentRecord.idNote,
            currentRecord.id,
            currentRecord.date,
            text,
            currentRecord.userId))
    }


    fun editComment(id: Int, text: String) {
        val currentRecord = getById<Comment>(id)
        if (currentRecord !is Comment && currentRecord !is DeletedComment) throw IllegalArgumentException("Неверный тип комментария")

        notes = notes.minus(currentRecord)
        if (currentRecord is Comment)
        createComment(Comment(currentRecord.idNote,
            currentRecord.id,
            currentRecord.date,
            text,
            currentRecord.userId))
        else createComment(DeletedComment(currentRecord.idNote,
            currentRecord.id,
            currentRecord.date,
            text,
            currentRecord.userId))
    }
}


fun main() {
    val records = NoteService

    records.add(Note(0, 0,33333333, "Первая запись", 1))
    val testCreate = Note(0, 0,33333333, "Вторая запись", 1)


    try {
        records.createComment(testCreate) }
    catch (e: IllegalArgumentException) {
        println("Ошибка типа параметра")
    }

    records.createComment(DeletedComment(1, 0, 33339456, "Второй комментарий", 1))

    //records.restoreComment(3)
    //records.delete(1)
    //records.editComment(2, "новый текст")


}


