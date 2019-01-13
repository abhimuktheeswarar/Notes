package msa.notes

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_insert_update_note.*
import msa.domain.statemachine.NoteAction
import msa.domain.statemachine.NoteState
import msa.notes.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

class InsertUpdateNoteFragment : BaseFragment() {

    private val notesViewModel by sharedViewModel<NotesViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_insert_update_note

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button_insert_update_note.setOnClickListener {

            val title = edit_title.text.toString()
            val body = edit_body.text.toString()
            val date = Date(System.currentTimeMillis())

            val currentState = notesViewModel.state.value as? NoteState

            currentState?.noteId?.let { id ->


                notesViewModel.input.accept(NoteAction.UpdateNoteAction(id, title, body, date))

            } ?: run {

                notesViewModel.input.accept(NoteAction.InsertNoteAction(title, body, date))
            }


            edit_title.clearFocus()
            edit_body.clearFocus()

            activity?.onBackPressed()
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
                        body = body
                    )
                )
            )
        }
    }

    private fun setupViews(state: NoteState) {

        state.editingNote?.let { note ->

            edit_title.setText(note.title)
            edit_body.setText(note.body)
        }

    }
}