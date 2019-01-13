package msa.notes

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_note_detail.*
import msa.domain.statemachine.NoteAction
import msa.domain.statemachine.NoteState
import msa.notes.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.text.DateFormat


/**
 * Created by Abhi Muktheeswarar.
 */

class NoteDetailFragment : BaseFragment() {

    private val notesViewModel by sharedViewModel<NotesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun getLayoutId(): Int = R.layout.fragment_note_detail

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button_edit_note.setOnClickListener {

            notesViewModel.input.accept(NoteAction.OpenEditNoteAction)
            findNavController().navigate(R.id.insertUpdateNoteFragment)
        }

        image_note.setOnTouchListener { v, event ->

            Timber.d("action = ${event.action}")

            return@setOnTouchListener when (event.action) {

                MotionEvent.ACTION_DOWN -> {

                    val matrix = ColorMatrix()
                    matrix.setSaturation(0f)
                    val filter = ColorMatrixColorFilter(matrix)
                    image_note.colorFilter = filter
                    true
                }

                MotionEvent.ACTION_UP -> {

                    image_note.colorFilter = null
                    true

                }

                else -> false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val deleteMenuItem = menu.add(Menu.NONE, 2, Menu.NONE, "Delete")
        deleteMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        deleteMenuItem.setIcon(R.drawable.ic_delete_white)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            2 -> {
                val note = (notesViewModel.state.value as? NoteState)?.note!!
                notesViewModel.input.accept(
                    NoteAction.DeleteNoteAction(
                        id = note.id!!,
                        title = note.title,
                        body = note.body,
                        date = note.date,
                        imagePath = note.imagePath
                    )
                )
                activity?.onBackPressed()
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()
        notesViewModel.state.observe(this, Observer {
            setupViews(it as NoteState)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        notesViewModel.input.accept(NoteAction.CloseNoteDetailAction)
    }

    private fun setupViews(state: NoteState) {

        state.note?.let { note ->

            text_title.text = note.title
            text_body.text = note.body

            text_date.text = DateFormat.getDateTimeInstance().format(note.date)
            Timber.d(note.toString())

            note.imagePath?.let { Glide.with(image_note).load(Uri.parse(it)).into(image_note) }

        }
    }
}