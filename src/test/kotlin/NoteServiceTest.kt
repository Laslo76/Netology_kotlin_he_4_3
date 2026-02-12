package netolgy.ru

import Comment
import DeletedComment
import Note
import NoteService
import org.junit.Assert.*
import org.junit.Before
import kotlin.test.Test

class NoteServiceTest {
    @Before
    fun clearBeforeTest() {
        NoteService.clear()
    }

    @Test
    fun newIdEmpty() {
        val notes = NoteService
        val result = notes.newId()

        assertEquals(1, result)
    }

    @Test
    fun newIdNotEmpty() {
        val notes = NoteService
        val testID = 3
        notes.add(Note(0, testID,33333333, "Первая запись", 1))
        val result = notes.newId()

        assertEquals(testID+1, result)
    }

    @Test
    fun add() {
        val records = NoteService
        records.add(Note(0, 0,33333333, "Первая запись", 1))
        val result = records.getSize()

        assertEquals(1, result)
    }

    @Test
    fun getById() {
        val notes = NoteService
        notes.add(Note(1, 1, 33333333, "Первая запись", 1))
        val testNote = Note(0, 0, 33333334, "Вторая запись", 1)
        notes.add(testNote)
        notes.add(Note(0, 0, 33333335, "Третья запись", 1))

        val result = notes.getById<Note>(2)
        assertEquals(testNote.text, result.text)
    }

    @Test
    fun delete() {
        val notes = NoteService
        notes.add(Note(0, 0, 33333333, "Первая запись", 1))
        notes.add(Note(0, 0, 33333334, "Вторая запись", 2))
        notes.createComment(Comment(1,0,33333335, "Комментарий к первой записи", 1))
        notes.createComment(Comment(2,0,33333335, "Комментарий ко второй записи", 3))
        notes.createComment(DeletedComment(2,0,33333335, "Новый комментарий к записи", 53))
        notes.delete(2)
        val result = notes.getSize()
        assertEquals(2, result)
    }

    @Test
    fun deleteComment() {
        val notes = NoteService
        notes.add(Note(0, 0, 33333333, "Первая запись", 1))
        notes.add(Note(0, 0, 33333334, "Вторая запись", 2))
        notes.createComment(Comment(1,0,33333335, "Комментарий к первой записи", 1))
        notes.createComment(Comment(2,0,33333335, "Комментарий ко второй записи", 3))
        notes.createComment(DeletedComment(2,0,33333335, "Новый комментарий к записи", 53))
        notes.deleteComment(3)
        val result = (notes.getById<Note>(3) is DeletedComment)
        assertEquals(true, result)
    }

    @Test
    fun restoreComment() {
        val notes = NoteService
        notes.add(Note(0, 0, 33333333, "Первая запись", 1))
        notes.add(Note(0, 0, 33333334, "Вторая запись", 2))
        notes.createComment(Comment(1,0,33333335, "Комментарий к первой записи", 1))
        notes.createComment(Comment(2,0,33333335, "Комментарий ко второй записи", 3))
        notes.createComment(DeletedComment(2,0,33333335, "Новый комментарий к записи", 53))
        notes.restoreComment(5)
        val result = (notes.getById<Note>(5) is Comment)
        assertEquals(true, result)
    }

    @Test
    fun get() {
        val notes = NoteService
        notes.add(Note(0, 0, 33333333, "Первая запись", 1))
        notes.add(Note(0, 0, 33333334, "Вторая запись", 2))
        notes.createComment(Comment(1,0,33333335, "Комментарий к первой записи", 1))
        notes.createComment(Comment(2,0,33333335, "Комментарий ко второй записи", 3))
        notes.createComment(DeletedComment(2,0,33333335, "Новый комментарий к записи", 53))
        val listNote = notes.get<Note>(3)
        val result = listNote.size
        assertEquals(1, listNote.size)
    }

    @Test
    fun getComments() {
        val notes = NoteService
        notes.add(Note(0, 0, 33333333, "Первая запись", 1))
        notes.add(Note(0, 0, 33333334, "Вторая запись", 2))
        notes.createComment(Comment(1,0,33333335, "Комментарий к первой записи", 1))
        notes.createComment(Comment(2,0,33333335, "Комментарий ко второй записи", 3))
        notes.createComment(DeletedComment(2,0,33333335, "Новый комментарий к записи", 53))
        val listNote = notes.getComments<Note>(2)
        val result = listNote.size
        assertEquals(1, listNote.size)
    }


    @Test
    fun edit() {
        val notes = NoteService
        notes.add(Note(0, 0, 33333333, "Первая запись", 1))
        notes.add(Note(0, 0, 33333334, "Вторая запись", 2))
        notes.createComment(Comment(1,0,33333335, "Комментарий к первой записи", 1))
        notes.createComment(Comment(2,0,33333335, "Комментарий ко второй записи", 3))
        notes.createComment(DeletedComment(2,0,33333335, "Новый комментарий к записи", 53))
        notes.edit(2, "Исправленная запись")
        val result = notes.getById<Note>(2)
        assertEquals("Исправленная запись", result.text)
    }

    @Test
    fun editComment() {
        val notes = NoteService
        notes.add(Note(0, 0, 33333333, "Первая запись", 1))
        notes.add(Note(0, 0, 33333334, "Вторая запись", 2))
        notes.createComment(Comment(1,0,33333335, "Комментарий к первой записи", 1))
        notes.createComment(Comment(2,0,33333335, "Комментарий ко второй записи", 3))
        notes.createComment(DeletedComment(2,0,33333335, "Новый комментарий к записи", 53))
        notes.editComment(3, "Исправленная запись")
        val result = notes.getById<Note>(3)
        assertEquals("Исправленная запись", result.text)
    }


    @Test(expected = IllegalArgumentException::class)
    fun createCommentThrow() {
        val notes = NoteService
        notes.add(Note(1, 1, 33333333, "Первая запись", 1))
        val firstComment = Note(1, 2, 124551241,"Комментарий к первой записи", 1)
        notes.createComment(firstComment)
    }

    @Test(expected = IllegalArgumentException::class)
    fun editThrow() {
        val notes = NoteService
        notes.add(Note(1, 1, 33333333, "Первая запись", 1))
        notes.createComment(Comment(1, 0, 124551241,"Комментарий к первой записи", 1))
        notes.edit(2, "Исправления")
    }

    @Test(expected = IllegalArgumentException::class)
    fun editCommentThrow() {
        val notes = NoteService
        notes.add(Note(1, 1, 33333333, "Первая запись", 1))
        notes.createComment(Comment(1, 0, 124551241,"Комментарий к первой записи", 1))
        notes.editComment(1, "Исправления")
    }

    @Test(expected = IllegalArgumentException::class)
    fun getByIdThrow() {
        val notes = NoteService
        notes.add(Note(1, 1, 33333333, "Первая запись", 1))
        notes.createComment(Comment(1, 0, 124551241,"Комментарий к первой записи", 1))
        notes.getById<Note>(5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun restoreCommentThrow() {
        val notes = NoteService
        notes.add(Note(1, 1, 33333333, "Первая запись", 1))
        notes.createComment(Comment(1, 0, 124551241,"Комментарий к первой записи", 1))
        notes.restoreComment(2)
    }

}




