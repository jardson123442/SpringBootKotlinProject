package com.example.project.controller

import com.example.project.database.model.Note
import com.example.project.database.repository.NoteRepository
import com.example.project.database.resources.NoteRequestDto
import com.example.project.database.resources.NoteResponseDto
import org.bson.types.ObjectId
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping("/notes")
class NoteController (
    private val noteRepository: NoteRepository,
) {

    @PostMapping
    fun save(@RequestBody body: NoteRequestDto): ResponseEntity<NoteResponseDto> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val note = noteRepository.save(
            Note(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = body.title,
                content = body.content,
                color = body.color,
                createdAt = Instant.now(),
                ownerId = ObjectId(ownerId)
            )
        )

        return ResponseEntity.status(201).body(note.toResponse())
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponseDto> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        return noteRepository.findByOwnerId(ObjectId(ownerId)).map {
            it.toResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        val note = noteRepository.findById(ObjectId(id))
            .orElseThrow { IllegalArgumentException("Note with id $id not found") }
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        if (note.ownerId.toHexString() == ownerId) {
            noteRepository.deleteById(ObjectId(id))
        }
    }

    private fun Note.toResponse(): NoteResponseDto {
        return NoteResponseDto(
            id = id.toHexString(),
            title = title,
            content = content,
            color = color,
            createdAt = createdAt.toString()
        )
    }
}