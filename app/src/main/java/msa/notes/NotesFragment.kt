package msa.notes

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.Observer
import com.airbnb.epoxy.*
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_notes.*
import msa.domain.core.Action
import msa.domain.statemachine.NoteAction
import msa.domain.statemachine.NoteState
import msa.notes.base.BaseEpoxyHolder
import msa.notes.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

class NotesFragment : BaseFragment() {

    private val notesViewModel by sharedViewModel<NotesViewModel>()

    private val notesController by lazy { NotesController { notesViewModel.input.accept(it) } }

    override fun getLayoutId(): Int = R.layout.fragment_notes

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        epoxyRecyclerView.setController(notesController)
        epoxyRecyclerView.setItemSpacingDp(8)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        notesViewModel.state.observe(this, Observer {
            setupViews(it as NoteState)
        })

        notesViewModel.input.accept(NoteAction.GetNotesAction())
    }

    private fun setupViews(state: NoteState) {

        notesController.setNoteState(state)

    }
}

class NotesController(private val itemActionListener: (action: Action) -> Unit) : TypedEpoxyController<NoteState>() {

    @AutoModel
    lateinit var loadingItemModel: LoadingItemModel_

    fun setNoteState(state: NoteState) {
        setData(state)
    }

    override fun buildModels(state: NoteState) {

        loadingItemModel.addIf(state.loading, this)

        state.notes?.forEach { note ->

            noteItem {
                id(note.id)
                noteId(note.id!!)
                title(note.title)
                body(note.body)
                date(note.date)
                itemActionListener(itemActionListener)
            }
        }
    }
}

@EpoxyModelClass(layout = R.layout.item_note)
abstract class NoteItemModel : EpoxyModelWithHolder<NoteItemModel.NoteItemViewHolder>() {

    @EpoxyAttribute
    open var noteId: Int = -1
    @EpoxyAttribute
    lateinit var title: String
    @EpoxyAttribute
    lateinit var body: String
    @EpoxyAttribute
    lateinit var date: Date
    @EpoxyAttribute(hash = false)
    lateinit var itemActionListener: (action: Action) -> Unit

    override fun bind(holder: NoteItemViewHolder) {
        super.bind(holder)
        holder.titleTextView.text = title
        holder.bodyTextView.text = body
        holder.dateTextView.text = date.time.toString()

        holder.deleteButton.setOnClickListener {
            itemActionListener(
                NoteAction.DeleteNoteAction(
                    noteId,
                    title,
                    body,
                    date
                )
            )
        }

        holder.rootItemView.setOnClickListener { itemActionListener(NoteAction.ViewNoteDetailAction(noteId)) }
    }


    class NoteItemViewHolder : BaseEpoxyHolder() {

        val rootItemView by bind<MaterialCardView>(R.id.materialCardView_note)
        val titleTextView by bind<TextView>(R.id.text_title)
        val bodyTextView by bind<TextView>(R.id.text_body)
        val dateTextView by bind<TextView>(R.id.text_date)
        val deleteButton by bind<ImageButton>(R.id.button_delete)

    }
}

@EpoxyModelClass(layout = R.layout.item_loading)
abstract class LoadingItemModel : EpoxyModelWithHolder<LoadingItemModel.LoadingItemViewEpoxyHolder>() {


    override fun getSpanSize(totalSpanCount: Int, position: Int, itemCount: Int): Int {
        return totalSpanCount
    }

    class LoadingItemViewEpoxyHolder : BaseEpoxyHolder() {

        val progressBar by bind<ProgressBar>(R.id.progressBar_notes)

    }
}
