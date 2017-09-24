package innovate.jain.com.shakynote.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import innovate.jain.com.shakynote.R;
import innovate.jain.com.shakynote.adapter.NoteListAdapter;
import innovate.jain.com.shakynote.model.Note;
import innovate.jain.com.shakynote.service.SensorListenerService;
import innovate.jain.com.shakynote.task.LoadNotes;
import innovate.jain.com.shakynote.utils.AppUtils;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    private ProgressBar loadNotesProgress;
    private List<Note> notes;
    private NoteListAdapter noteListAdapter;
    private long count;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar customToolbar = (Toolbar) findViewById(R.id.toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.card_list);
        RecyclerView.LayoutManager manager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(manager);

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.top_container);
        refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_to_refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLoaderManager().restartLoader(1, null, noteLoader);
            }
        });

        loadNotesProgress = (ProgressBar) findViewById(R.id.load_note_progress);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(1000);
        defaultItemAnimator.setRemoveDuration(500);
        recyclerView.setItemAnimator(defaultItemAnimator);

        getLoaderManager().restartLoader(1, null, noteLoader);
        notes = new ArrayList<>();

        noteListAdapter = new NoteListAdapter(notes, this, coordinatorLayout);
        recyclerView.setAdapter(noteListAdapter);

        customToolbar.setTitle("Shaky Notes");
        setSupportActionBar(customToolbar);

        if (AppUtils.isShakeActivated(this)) {
            Intent intent = new Intent(MainActivity.this, SensorListenerService.class);
            startService(intent);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(intent);

            }
        });

    }

    private final LoaderManager.LoaderCallbacks<List<Note>> noteLoader = new LoaderManager.LoaderCallbacks<List<Note>>() {
        @Override
        public Loader<List<Note>> onCreateLoader(int id, Bundle args) {
            return new LoadNotes(MainActivity.this);
        }

        @Override
        public void onLoadFinished(Loader<List<Note>> loader, List<Note> data) {

            loadNotesProgress.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            noteListAdapter.setNotes(data);

            if (notes != null && data != null && data.size() > notes.size()) {
                int i = data.size() - notes.size();
                for (int k = 0; k < i; k++) {
                    noteListAdapter.notifyItemInserted(k);
                }
            } else {
                noteListAdapter.notifyDataSetChanged();
            }
            notes = data;
            refresh.setRefreshing(false);
        }

        @Override
        public void onLoaderReset(Loader<List<Note>> loader) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(1, null, noteLoader);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem search = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        if (searchView != null)
            searchView.setOnQueryTextListener(this);


        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses
                fab.setVisibility(View.VISIBLE);
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                fab.setVisibility(View.GONE);
                return true;  // Return true to expand action view
            }
        };

        MenuItemCompat.setOnActionExpandListener(search, expandListener);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (count + 2000 < System.currentTimeMillis()) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }

        count = System.currentTimeMillis();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            backupManager.dataChanged();
            Intent intent = new Intent(MainActivity.this, SettingsActivtiy.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        List<Note> filteredNotes = filter(notes, newText);
        noteListAdapter.setNotes(filteredNotes);
        noteListAdapter.notifyDataSetChanged();
        return true;
    }

    public List<Note> filter(List<Note> notes, String query) {
        String lowerCaseQuery = query.toLowerCase();
        List<Note> filteredNotes = new ArrayList<>();
        for (Note note : notes) {
            if (note.getTitle().toLowerCase().contains(lowerCaseQuery) || (note.getDescription().toLowerCase().contains(lowerCaseQuery) && note.getType() != 1)) {
                filteredNotes.add(note);
            }
        }

        return filteredNotes;
    }

}
