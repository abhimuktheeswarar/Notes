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

            notesViewModel.input.accept(NoteAction.InsertNoteAction(title, body, date))

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

    private fun setupViews(state: NoteState) {

    }
}