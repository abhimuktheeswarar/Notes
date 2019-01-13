package msa.notes

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.github.florent37.inlineactivityresult.kotlin.startForResult
import kotlinx.android.synthetic.main.fragment_insert_update_note.*
import msa.domain.statemachine.NoteAction
import msa.domain.statemachine.NoteState
import msa.notes.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

class InsertUpdateNoteFragment : BaseFragment() {

    private val notesViewModel by sharedViewModel<NotesViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_insert_update_note

    private var noteImagePath: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_insert_update_note.setOnClickListener {

            val title = edit_title.text.toString()
            val body = edit_body.text.toString()
            val date = Date(System.currentTimeMillis())

            val currentState = notesViewModel.state.value as? NoteState

            Timber.d("imagePath = $noteImagePath")

            currentState?.noteId?.let { id ->

                notesViewModel.input.accept(NoteAction.UpdateNoteAction(id, title, body, date, noteImagePath))

            } ?: run {

                notesViewModel.input.accept(NoteAction.InsertNoteAction(title, body, date, noteImagePath))
            }

            edit_title.clearFocus()
            edit_body.clearFocus()

            activity?.onBackPressed()
        }

        button_pick_image.setOnClickListener {

            openImagePicker()
        }
    }

    override fun onStart() {
        super.onStart()
        notesViewModel.state.observe(this, Observer {
            setupViews(it as NoteState)
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val title = edit_title.text.toString()
        val body = edit_body.text.toString()
        val editNote = (notesViewModel.state.value as? NoteState)?.editingNote
        editNote?.let {
            notesViewModel.input.accept(
                NoteAction.SaveEditNoteProgressAction(
                    it.copy(
                        title = title,
                        body = body,
                        imagePath = noteImagePath
                    )
                )
            )
        }
    }

    private fun setupViews(state: NoteState) {

        state.editingNote?.let { note ->

            edit_title.setText(note.title)
            edit_body.setText(note.body)
            if (noteImagePath == null)
                noteImagePath = note.imagePath
            noteImagePath?.let { Glide.with(image_note).load(Uri.parse(noteImagePath)).into(image_note) }
        }
    }

    private fun openImagePicker() {

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.flags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION// (Intent.FLAG_GRANT_READ_URI_PERMISSION , Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.flags = Intent.FLAG_GRANT_WRITE_URI_PERMISSION

        startForResult(intent) { result ->
            //use the result, eg:
            val imageUri = result.data?.data
            noteImagePath = imageUri.toString()
            Glide.with(image_note).load(imageUri).into(image_note)

        }.onFailed {

            Timber.e("onFailed")

        }
    }
}