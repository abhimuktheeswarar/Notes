package msa.notes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import kotlinx.android.synthetic.main.activity_notes.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotesActivity : AppCompatActivity() {

    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

    private val notesViewModel by viewModel<NotesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)

        navController.addOnNavigatedListener { _, destination ->

            when (destination.id) {

                R.id.notesFragment -> fab_add_note.show()

                else -> fab_add_note.hide()
            }
        }

        fab_add_note.setOnClickListener { navController.navigate(R.id.insertUpdateNoteFragment) }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}
