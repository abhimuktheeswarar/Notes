package msa.notes

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.fragment_notes.*
import msa.domain.core.Action
import msa.domain.statemachine.NoteAction
import msa.domain.statemachine.NoteState
import msa.domain.statemachine.OrderBy
import msa.domain.statemachine.SortBy
import msa.notes.base.BaseEpoxyHolder
import msa.notes.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*

/**
 * Created by Abhi Muktheeswarar.
 */

class NotesFragment : BaseFragment() {

    private val notesViewModel by sharedViewModel<NotesViewModel>()

    private val notesController by lazy {
        NotesController {
            notesViewModel.input.accept(it)
            if (it is NoteAction.ViewNoteDetailAction) {
                findNavController().navigate(R.id.noteDetailFragment)
            }
        }
    }

    private lateinit var sortOptionDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun getLayoutId(): Int = R.layout.fragment_notes

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        epoxyRecyclerView.setController(notesController)
        epoxyRecyclerView.setItemSpacingDp(8)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val sortMenuItem = menu.add(Menu.NONE, 2, Menu.NONE, "Sort")
        sortMenuItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS)
        sortMenuItem.setIcon(R.drawable.ic_sort)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            2 -> {
                notesViewModel.input.accept(NoteAction.ShowNotesSortOptionsAction)
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        notesViewModel.state.observe(this, Observer {
            setupViews(it as NoteState)
        })

        notesViewModel.input.accept(NoteAction.GetNotesAction)
    }


    private fun setupViews(state: NoteState) {

        Timber.d("setupViews state = ${state.showSortOption}")

        notesController.setNoteState(state)

        if (state.showSortOption) showSortOption(state)
        else {

            if (::sortOptionDialog.isInitialized) {
                sortOptionDialog.dismiss()
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showSortOption(state: NoteState) {

        if (!::sortOptionDialog.isInitialized) {

            sortOptionDialog = BottomSheetDialog(context!!)
        }

        sortOptionDialog.setOnDismissListener { notesViewModel.input.accept(NoteAction.HideNotesSortOptionsAction) }
        sortOptionDialog.setOnCancelListener { notesViewModel.input.accept(NoteAction.HideNotesSortOptionsAction) }
        val contentView = layoutInflater.inflate(R.layout.dialog_notes_sort, null)

        val sortRadioGroup = contentView.findViewById<RadioGroup>(R.id.radioGroup_sort)


        when (state.orderBy) {

            OrderBy.NA -> sortRadioGroup.check(R.id.radioButton_sort_default)
            OrderBy.DATE -> {

                when (state.sortBy) {

                    SortBy.ASC -> sortRadioGroup.check(R.id.radioButton_sort_date_old_new)
                    SortBy.DESC -> sortRadioGroup.check(R.id.radioButton_sort_date_new_old)
                    SortBy.NA -> {
                    }
                }
            }
            OrderBy.TITLE -> {

                when (state.sortBy) {

                    SortBy.ASC -> sortRadioGroup.check(R.id.radioButton_sort_title_a_z)
                    SortBy.DESC -> sortRadioGroup.check(R.id.radioButton_sort_title_z_a)
                    SortBy.NA -> {
                    }
                }
            }
        }

        sortRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {

                R.id.radioButton_sort_default -> notesViewModel.input.accept(
                    NoteAction.SortOrderNotesAction(
                        SortBy.ASC,
                        OrderBy.NA
                    )
                )

                R.id.radioButton_sort_date_old_new -> notesViewModel.input.accept(
                    NoteAction.SortOrderNotesAction(
                        SortBy.ASC,
                        OrderBy.DATE

                    )
                )

                R.id.radioButton_sort_date_new_old -> notesViewModel.input.accept(
                    NoteAction.SortOrderNotesAction(
                        SortBy.DESC,
                        OrderBy.DATE
                    )
                )

                R.id.radioButton_sort_title_a_z -> notesViewModel.input.accept(
                    NoteAction.SortOrderNotesAction(
                        SortBy.ASC,
                        OrderBy.TITLE
                    )
                )

                R.id.radioButton_sort_title_z_a -> notesViewModel.input.accept(
                    NoteAction.SortOrderNotesAction(
                        SortBy.DESC,
                        OrderBy.TITLE
                    )
                )
            }

            sortOptionDialog.dismiss()

        }

        sortOptionDialog.setContentView(contentView)
        sortOptionDialog.show()
    }
}

class NotesController(private val itemActionListener: (action: Action) -> Unit) : TypedEpoxyController<NoteState>() {

    @AutoModel
    lateinit var loadingItemModel: LoadingItemModel_

    fun setNoteState(state: NoteState) {
        setData(state)
    }

    override fun buildModels(state: NoteState) {

        if (state.loading) {
            loadingItemModel.addTo(this)
            return
        }

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
        holder.dateTextView.text = DateUtils.getRelativeTimeSpanString(
            date.time,
            System.currentTimeMillis(),
            DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE
        )

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
